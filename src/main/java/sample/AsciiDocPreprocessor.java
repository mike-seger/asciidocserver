package sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AsciiDocPreprocessor {
	private final String diagramTemplate;

	public AsciiDocPreprocessor(AsciidocServerConfig config ) throws IOException, URISyntaxException {
		diagramTemplate = Files.readString(new File(config.getDiagramTemplateUrl().toURI()).toPath()).trim();
	}
	/**
	 * The purpose of this method is to optimize for speed as external
	 * diagrams do not seem to be cached, so they are converted to inline
	 * diagrams on the fly.
	 *
	 * @param adocFile - The AsciiDoc file
	 * @param destinationDir - The destination directory where the result file is to be written to
	 * @return preprocessed AsciiDoc file in tmpDir
	 * @throws IOException - An IOException can be thrown for various reasons.
	 */
	public File filterDiagrams(File adocFile, File destinationDir) throws IOException {
		List<String> lines = Files.readAllLines(adocFile.toPath());
		File outputFile=new File(destinationDir, adocFile.getName()+".filterDiagrams");
		if(outputFile.exists() && outputFile.lastModified()>adocFile.lastModified()) {
			return outputFile;
		}
		String content = lines.stream().map(l -> convertDiagramLine(adocFile, l)).collect(Collectors.joining("\n"));
		Files.write(outputFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
		return outputFile;
	}

	private String convertDiagramLine(File adocFile, String line) {
		String diagramPattern = "^([A-Za-z0-9]+)::([A-Za-z0-9\\-_.+'()]*/[A-Za-z0-9\\-_/.+'()]+)\\[(.*)] *$";
		if(line.matches(diagramPattern)) {
			String path=line.replaceAll(diagramPattern, "$2");
			File diagramFile = new File(adocFile.getParent(), path);
			String type = line.replaceAll(diagramPattern, "$1");
			String attrs = line.replaceAll(diagramPattern, "$3");
			try {
				String diagram = Files.readString(diagramFile.toPath()).trim();
				line = diagramTemplate;
				if("gnuplot".matches(type)) {
					line = line.replaceAll(", *(height|width) *= *\"[0-9]+%\"", "");
				}
				line = line.replace("${type}", type);
				line = line.replace("${diagram}", diagram);
				line = line.replace("${attrs}", attrs);
				line = line.replace("${path}", path);
			} catch(IOException e) {
				log.error("Error on line: {}. File {}", line, diagramFile.getAbsolutePath(), e);
			}
		}
		return line;
	}
}
