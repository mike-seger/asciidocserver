package sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;

@Slf4j
public class FontInstaller {
	public static void installFonts(final List<String> fonts) {
		fonts.forEach(font -> {
			URL fontUrl = FontInstaller.class.getResource(font);
			if(fontUrl == null) {
				log.error("Cannot find font: {}", font);
			}
			installFont(fontUrl);
		});
	}

	public static void installFonts(final String location) {
		try {
			ResourcesUtils.getResources(location+"/*.ttf")
				.stream().map(FontInstaller::getResourceUrl).filter(Objects::nonNull)
				.forEach(FontInstaller::installFont);
		} catch (IOException e) {
			log.error("Unable to install fonts from: {}", location, e);
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

	public static void installFont(final URL sourceUrl) {
		try {
			try(InputStream is = sourceUrl.openStream()) {
				Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);//.deriveFont(12f);
				log.info("Font: {} ({})", customFont.getFontName(), customFont.getSize());
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				ge.registerFont(customFont);
			}
		} catch (Exception e) {
			log.error("Unable to install font: {}", sourceUrl, e);
		}
	}
}
