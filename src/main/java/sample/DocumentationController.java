package sample;

import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.*;
import org.asciidoctor.*;
import org.asciidoctor.jruby.AsciiDocDirectoryWalker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping
@Slf4j
public class DocumentationController {
    private final File htmlDir;

    public DocumentationController(
            @Value("${asciidocserver.source-url}") URL sourceUrl,
            @Value("${asciidocserver.destination-id}") String destinationId
            ) throws IOException, URISyntaxException {
        long time = System.currentTimeMillis();

        //URL fontUrl=new URL(sourceUrl.toString()+"/assets/fg-virgil.ttf");
        //URL fontUrl=getClass().getResource("/documentation/assets/fg-virgil.ttf");
        //URL fontUrl=getClass().getResource("/documentation/assets/Another Birdhouse Light.ttf");
        URL fontUrl=getClass().getResource("/documentation/assets/Dylan.ttf");

        FontInstaller.installFont(fontUrl);
        htmlDir = new TempFileUtils().createTempDirWithId(destinationId);
        boolean changed = ResourcesUtils.copyResourceLocation(sourceUrl, htmlDir.toPath());
        log.info("Finished Resource Sync ({}). Took {} ms", sourceUrl, System.currentTimeMillis() - time);
        if (changed) {
            processAsciiDoctor(htmlDir, null);
        }

        CompletableFuture.runAsync(() -> watchSource(sourceUrl));
    }

    private void watchSource(URL sourceUrl) {
        Path path = null;
        try {
            if (sourceUrl != null && "file".equals(sourceUrl.getProtocol())) {
                WatchService watcher = FileSystems.getDefault().newWatchService();
                path = Paths.get(sourceUrl.toURI());
                path.register(
                    watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

                WatchKey key;
                while ((key = watcher.take()) != null) {
                    try {
                        ResourcesUtils.copyResourceLocation(sourceUrl, htmlDir.toPath());
                        for (WatchEvent<?> event : key.pollEvents()) {
                            log.info("Event kind: {}. File affected: {} ({}).",
                                event.kind(), event.context(), event.context().getClass());
                            if (event.context() instanceof Path) {
                                String name = (((Path) event.context()).toFile()).getName();
                                processAsciiDoctor(htmlDir, new File(htmlDir, name));
                            } else {
                                processAsciiDoctor(htmlDir, null);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error occurred processing file", e);
                    }
                    key.reset();
                }
            }
        } catch (Exception e) {
            log.error("Error watching path: {}", path, e);
        }
    }

    private static synchronized void processAsciiDoctor(File htmlDir, File singleDoc) throws IOException {
        long time = System.currentTimeMillis();
        log.info("Started asciidoctor");
        try (Asciidoctor asciidoctor = Asciidoctor.Factory.create()) {
            asciidoctor.requireLibrary("asciidoctor-diagram");
            String imageDirName = "images";
            File imageDir=createDir(new File(htmlDir, imageDirName));

            Attributes attributes = AttributesBuilder.attributes()
                    .attribute("imagesoutdir", imageDir.getAbsolutePath())
                    .icons("font")
                    .tableOfContents2(Placement.LEFT)
                    .sectionNumbers(true)
                    .setAnchors(true)
                    .noFooter(true)
                    .imagesDir(imageDirName)
                    .get();

            OptionsBuilder optionsBuilder = OptionsBuilder.options()
                    .safe(SafeMode.UNSAFE)
                    .backend("html5")
                    .headerFooter(true)
                    .baseDir(htmlDir)
                    .mkDirs(true)
                    .attributes(attributes);

            if(singleDoc!=null) {
                convertAsciiDoc(asciidoctor, singleDoc, optionsBuilder);
            } else {
                for (File f : new AsciiDocDirectoryWalker(htmlDir.getAbsolutePath()))
                    convertAsciiDoc(asciidoctor, f, optionsBuilder);
            }
        }
        log.info("Finished asciidoctor. Took {} ms", System.currentTimeMillis()-time);
    }

    private static void convertAsciiDoc(Asciidoctor asciidoctor, File asciiDoc, OptionsBuilder optionsBuilder) throws IOException {
        if(!asciiDoc.isFile()) {
            log.warn("Not converting non-file: {}", asciiDoc);
            return;
        }
        File htmlFile=new File(asciiDoc.getParent(), asciiDoc.getName()+".html");
        try (Reader r=new FileReader(asciiDoc)) {
            optionsBuilder.baseDir(asciiDoc.getParentFile());
            try (Writer w=new FileWriter(htmlFile)) {
                asciidoctor.convert(r, w, optionsBuilder);
            }
        }
    }

    private static File createDir(File dir) {
        if(!dir.exists() && !dir.mkdir()) {
            throw new RuntimeException("Failed to create dir: "+dir.getAbsolutePath());
        }
        return dir;
    }

    @ResponseBody
    @GetMapping(path = "/{*path}")
    public ResponseEntity<Resource> documentation(@PathVariable("path") String path) throws MagicParseException, MagicException, MagicMatchNotFoundException {
        Resource resource;
        if(path.isEmpty() || "/".equals(path)) {
            path = "README.adoc";
        }
        final HttpHeaders headers = new HttpHeaders();
        if(path.endsWith(".adoc")) {
            resource = new FileSystemResource(new File(htmlDir, path + ".html"));
            headers.setContentType(MediaType.TEXT_HTML);
        } else {
            File file = new File(htmlDir, path);
            resource = new FileSystemResource(file);
            MagicMatch match = Magic.getMagicMatch(file, true);
            headers.setContentType(MediaType.valueOf(match.getMimeType()));
        }

        return new ResponseEntity<>(resource, headers, HttpStatus.CREATED);
    }
}
