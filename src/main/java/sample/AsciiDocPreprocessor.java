package sample;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AsciiDocPreprocessor {
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
		String diagramPattern = "^([A-Za-z0-9]+)::([A-Za-z0-9\\-_.]*/[A-Za-z0-9\\-_/.]+)\\[(.*)] *$";
		if(line.matches(diagramPattern)) {
			File diagramFile = new File(adocFile.getParent(),
				line.replaceAll(diagramPattern, "$2"));
			String replacedLine = line.replaceAll(diagramPattern, "[$1, $3]");
			try {
				String diagram = Files.readString(diagramFile.toPath()).trim();
				line = replacedLine+"\n----\n"+diagram+"\n----\n\n";
			} catch(IOException e) {
				log.error("Error on line: {}. File {}", line, diagramFile.getAbsolutePath(), e);
			}
		}
		return line;
	}
}
