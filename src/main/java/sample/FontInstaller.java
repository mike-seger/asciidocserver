package sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;

@Slf4j
public class FontInstaller {
	public static Set<URL> installFonts(Collection<String> locations) {
		Set<URL> fonts=new HashSet<>();
		locations.forEach(l -> fonts.addAll(installFonts(l)));
		return fonts;
	}

	public static Set<URL> installFonts(String location) {
		try {
			location = location
				.replaceAll("^/*", "")
				.replaceAll("/$","")
				.replaceAll("$", "/*.ttf");
			return ResourcesUtils.getResources(location)
				.stream().map(FontInstaller::getResourceUrl)
				.filter(Objects::nonNull)
				.map(FontInstaller::installFont)
				.collect(Collectors.toSet());
		} catch (IOException e) {
			log.error("Unable to install fonts from: {}", location, e);
			return emptySet();
		}
	}

	public static URL getResourceUrl(Resource resource) {
		try {
			return resource.getURL();
		} catch (IOException e) {
			log.error("Failed to get URL from: {}", resource, e);
		}
		return null;
	}

	public static URL installFont(final URL sourceUrl) {
		try {
			try(InputStream is = sourceUrl.openStream()) {
				Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);//.deriveFont(12f);
				log.info("Font: {} ({})", customFont.getFontName(), customFont.getSize());
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				ge.registerFont(customFont);
				return sourceUrl;
			}
		} catch (Exception e) {
			log.error("Unable to install font: {}", sourceUrl, e);
			return null;
		}
	}
}
