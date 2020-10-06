package sample;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Slf4j
public class FontInstaller {
	public static void installFont(final URL sourceUrl) {
		try {
			try(InputStream is = sourceUrl.openStream()) {
				Font customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(12f);
				log.info("Font: {} ({})", customFont.getFontName(), customFont.getSize());
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				ge.registerFont(customFont);
			}
		} catch (IOException | FontFormatException e) {
			log.error("Unable to install font: {}", sourceUrl, e);
		}
	}
}
