package sample;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.util.List;

@Configuration
@ConfigurationProperties("asciidocserver")
public class AsciidocServerConfig {
	private URL sourceUrl;
	private String destinationId;
	private List<String> installedFonts;

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

	public List<String> getInstalledFonts() {
		return installedFonts;
	}

	public void setInstalledFonts(List<String> installedFonts) {
		this.installedFonts = installedFonts;
	}
}
