package sample;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import com.github.skjolber.jackson.jsh.AnsiSyntaxHighlight;
import com.github.skjolber.jackson.jsh.DefaultSyntaxHighlighter;
import com.github.skjolber.jackson.jsh.SyntaxHighlighter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.skjolber.jackson.jsh.SyntaxHighlightingJsonGenerator;

@Configuration
public class PropertiesDumper {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesDumper.class);
    private final static int debugValueLength = 40;

    private final ConfigurableEnvironment environment;
    private Set<String> propertyKeys;
    private final ObjectMapper om = new ObjectMapper();

    @Autowired
    public PropertiesDumper(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void dumpEnvironment() {
        try {
            if (logger.isDebugEnabled()) {
                propertyKeys = new HashSet<>();
                Map<String, Object> environmentMap = environmentMap(propertyKeys);
                logger.trace("PROPERTY DUMP environment:\n{}\n", toJson(environmentMap, logger.isDebugEnabled()));
            }
        } catch (RuntimeException e) {
            logger.error("Dump failed.", e);
        }
    }

    @EventListener({ ContextRefreshedEvent.class })
    public void dumpResolvedProperties() {
        try {
            if (logger.isDebugEnabled()) {
                if (propertyKeys != null) {
                    SortedMap<String, String> resolvedProperties = new TreeMap<>();
                    for (String key : propertyKeys) {
                        String value;
                        try {
                            value = environment.getProperty(key);
                        } catch (Exception e) {
                            try {
                                value = toJson(environment.getProperty(key, Object.class), true);
                            } catch (Exception e2) {
                                value = "[Unconvertable value]";
                            }
                        }
                        if (key.toLowerCase().endsWith("path") && value.length() > debugValueLength && !logger.isTraceEnabled()) {
                            value = value.substring(0, debugValueLength) + "...";
                        }
                        resolvedProperties.put(key, value);
                    }
                    logger.debug("PROPERTY DUMP resolved properties:\n{}", toJson(resolvedProperties, true));
                }
            }
        } catch (RuntimeException e) {
            logger.error("Dump failed", e);
        }
    }

    private Map<String, Object> environmentMap(Set<String> collectedPropertyKeys) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("profiles", environment.getActiveProfiles());
        for (Map.Entry<String, PropertySource<?>> entry : getPropertySources().entrySet()) {
            PropertySource<?> source = entry.getValue();
            String sourceName = entry.getKey();
            if (source instanceof EnumerablePropertySource) {
                EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) source;
                Map<String, Object> properties = new TreeMap<String, Object>();
                for (String name : enumerable.getPropertyNames()) {
                    try {
                        properties.put(name, sanitize(name, enumerable.getProperty(name)));
                    } catch (RuntimeException e) {
                        logger.error("problem with property {}", name);
                    }
                }
                result.put(sourceName, properties);
                propertyKeys.addAll(properties.keySet());
            }
        }
        return result;
    }

    private Map<String, PropertySource<?>> getPropertySources() {
        Map<String, PropertySource<?>> map = new LinkedHashMap<String, PropertySource<?>>();
        MutablePropertySources sources = null;
        if (environment != null) {
            sources = environment.getPropertySources();
        } else {
            sources = new StandardEnvironment().getPropertySources();
        }
        for (PropertySource<?> source : sources) {
            extract("", map, source);
        }
        return map;
    }

    private void extract(String root, Map<String, PropertySource<?>> map, PropertySource<?> source) {
        if (source instanceof CompositePropertySource) {
            for (PropertySource<?> nest : ((CompositePropertySource) source).getPropertySources()) {
                extract(source.getName() + ":", map, nest);
            }
        } else {
            map.put(root + source.getName(), source);
        }
    }

    private Object sanitize(String name, Object object) {
        return object; // this.sanitizer.sanitize(name, object);
    }

    private String toJson(Object o, boolean pretty) {
        try {
            if(!pretty) {
                return om.writeValueAsString(o);
            }
            JsonFactory jsonFactory = new JsonFactory();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JsonGenerator delegate = jsonFactory.createGenerator(baos, JsonEncoding.UTF8);
            SyntaxHighlighter highlighter = DefaultSyntaxHighlighter
                .newBuilder()
                .withField(AnsiSyntaxHighlightIntense.BLUE)
                .withString(AnsiSyntaxHighlightIntense.YELLOW)
                .withNumber(AnsiSyntaxHighlight.MAGENTA)
                .withCurlyBrackets(AnsiSyntaxHighlight.CYAN)
                .withComma(AnsiSyntaxHighlight.WHITE)
                .withColon(AnsiSyntaxHighlight.WHITE)
                .build();
            try (JsonGenerator jsonGenerator = new SyntaxHighlightingJsonGenerator(delegate, highlighter)) {
                jsonGenerator.setCodec(om);
                jsonGenerator.writeObject(o);
                baos.write(AnsiSyntaxHighlight.RESET.getBytes());
                String result = baos.toString();
                return result;
            } catch(Exception e) {
                return om.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            }
        } catch (Exception e) {
            logger.error("Failed to serialize object", e);
            return "";
        }
    }

    private final class AnsiSyntaxHighlightIntense {
        public static final String BLACK	= "90";
        public static final String RED	    = "91";
        public static final String GREEN	= "92";
        public static final String YELLOW	= "93";
        public static final String BLUE	    = "94";
        public static final String PURPLE	= "95";
        public static final String CYAN	    = "96";
        public static final String WHITE	= "97";
    }
}