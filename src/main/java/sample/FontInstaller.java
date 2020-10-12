package sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;

@Slf4j
public class FontInstaller {
	public static Set<FontInfo> installFonts(Collection<String> locations) {
		Set<FontInfo> fonts=new HashSet<>();
		locations.forEach(l -> fonts.addAll(installFonts(l)));
		return fonts;
	}

	public static Set<FontInfo> installFonts(String location) {
		try {
			location = location
				.replaceAll("^/*", "")
				.replaceAll("/$","")
				.replaceAll("$", "/*.ttf");
			return ResourcesUtils.getResources(location)
					.stream().map(FontInstaller::getResourceUrl)
					.filter(Objects::nonNull)
					.map(FontInstaller::installFont)
					.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		} catch (IOException e) {
			log.error("Unable to install fonts from: {}", location, e);
			return emptySet();
		}
	}

	private static URL getResourceUrl(Resource resource) {
		try {
			return resource.getURL();
		} catch (IOException e) {
			log.error("Failed to get URL from: {}", resource, e);
		}
		return null;
	}

	public static FontInfo installFont(final URL sourceUrl) {
		try {
			try(InputStream is = sourceUrl.openStream()) {
				Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);//.deriveFont(12f);
				log.info("Font: {} ({})", customFont.getFontName(), customFont.getSize());
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				ge.registerFont(customFont);
				return new FontInfo(sourceUrl, customFont.getFontName());
			}
		} catch (Exception e) {
			log.error("Unable to install font: {}", sourceUrl, e);
			return null;
		}
	}

	public static void createFontCss(File cssFile, Set<FontInfo> fontInfos, String prefixRegex, String replacement) throws IOException {
		String css = fontInfos.stream().map(fi -> fontInfo2Css(fi, prefixRegex, replacement)).collect(Collectors.joining());
		Files.write(cssFile.toPath(), css.getBytes());
	}

	private static String fontInfo2Css(FontInfo fontInfo, String prefixRegex, String replacement) {
		return String.format(
			"@font-face { font-family: '%s'; src: url('%s'); }\n",
			fontInfo.name,
			fontInfo.location.toExternalForm().replaceAll(prefixRegex, replacement));
	}

	private static String getRelativeUri(URI baseDir, URI path) {
		return baseDir.relativize(path).getPath();
	}

	@Data
	@AllArgsConstructor
	public static class FontInfo {
		private URL location;
		private String name;
	}
}
