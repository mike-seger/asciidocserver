package sample;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.util.List;

@Configuration
@ConfigurationProperties("asciidocserver")
public class AsciidocServerConfig {
	private URL sourceUrl;
	private URL diagramTemplateUrl;
	private String destinationId;
	private List<String> fontLocations;

	public URL getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(URL sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	public List<String> getFontLocations() {
		return fontLocations;
	}

	public void setFontLocations(List<String> fontLocations) {
		this.fontLocations = fontLocations;
	}

	public URL getDiagramTemplateUrl() {
		return diagramTemplateUrl;
	}

	public void setDiagramTemplateUrl(URL diagramTemplateUrl) {
		this.diagramTemplateUrl = diagramTemplateUrl;
	}
}
