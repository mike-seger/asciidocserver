package sample;

import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.*;
import org.apache.tika.Tika;
import org.asciidoctor.*;
import org.asciidoctor.jruby.AsciiDocDirectoryWalker;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping
@Slf4j
public class DocumentationController {
    private final File htmlDir;
    final private AsciiDocPreprocessor asciiDocPreprocessor;
    final private Tika tika = new Tika();

    public DocumentationController(final AsciidocServerConfig asciidocServerConfig,
            AsciiDocPreprocessor asciiDocPreprocessor) throws IOException, URISyntaxException {
        this.asciiDocPreprocessor=asciiDocPreprocessor;
        htmlDir = new TempFileUtils().createTempDirWithId(asciidocServerConfig.getDestinationId());
        boolean changed = ResourcesUtils.copyResourceLocation(asciidocServerConfig.getSourceUrl(), htmlDir.toPath());
        log.info("Finished Resource Sync ({}).", asciidocServerConfig.getSourceUrl());
        Set<FontInstaller.FontInfo> fontInfos=FontInstaller
            .installFonts(asciidocServerConfig.getFontLocations());
        FontInstaller.createFontCss(
            new File(htmlDir.getAbsolutePath()+"/assets/css/fonts2.css"),
            fontInfos,".*/documentation/assets/fonts/", "../fonts/");
        log.info("Finished installFonts");
        if (changed) {
            processAsciiDoctor(htmlDir, null);
        }

        CompletableFuture.runAsync(() -> watchSource(asciidocServerConfig.getSourceUrl()));
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

    private synchronized void processAsciiDoctor(File htmlDir, File singleDoc) throws IOException {
        long time = System.currentTimeMillis();
        log.info("Started asciidoctor");
        try (Asciidoctor asciidoctor = Asciidoctor.Factory.create()) {
            asciidoctor.requireLibrary("asciidoctor-diagram");
            String imageDirName = "images";
            File imageDir=createDir(new File(htmlDir, imageDirName));

            Attributes attributes = AttributesBuilder.attributes()
                .attribute("imagesoutdir", imageDir.getAbsolutePath())
                .attribute("docinfo", "shared")
                .icons("font")
                .copyCss(true)
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

    private void convertAsciiDoc(Asciidoctor asciidoctor, File asciiDoc, OptionsBuilder optionsBuilder) throws IOException {
        if(!asciiDoc.isFile()) {
            log.warn("Not converting non-file: {}", asciiDoc);
            return;
        }
        File htmlFile=new File(asciiDoc.getParent(), asciiDoc.getName()+".html");
        if(asciiDoc.exists() && asciiDoc.lastModified()<htmlFile.lastModified()) {
            return;
        }
        File filteredAsciiDoc=asciiDocPreprocessor.filterDiagrams(
            asciiDoc, htmlFile.getParentFile());
        try (Reader r=new FileReader(filteredAsciiDoc)) {
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
            try {
                headers.setContentType(MediaType.valueOf(tika.detect(file)));
            } catch (IOException | InvalidMimeTypeException | InvalidMediaTypeException e) {
                log.debug("JDK MIME detection failed for {}. Reason: {}", file, e.getMessage());
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }
        }
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
