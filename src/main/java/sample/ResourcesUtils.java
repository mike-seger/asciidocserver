package sample;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ResourcesUtils {
	public synchronized static boolean copyResourceLocation(final URL sourceUrl, final Path target)
			throws URISyntaxException, IOException {
		final Path resourcePath = Paths.get(sourceUrl.toURI());
		final boolean[] changed = new boolean[] { false };

		Files.walkFileTree(resourcePath, new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
				Path currentTarget = target.resolve(resourcePath.relativize(dir).toString());
				Files.createDirectories(currentTarget);
				return FileVisitResult.CONTINUE;
			}
	
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				Path destPath = target.resolve(resourcePath.relativize(file).toString());
				File destFile = destPath.toFile();
				if(!destFile.exists() || file.toFile().lastModified()!=destFile.lastModified()) {
					Files.copy(file, destPath, StandardCopyOption.REPLACE_EXISTING);
					changed[0] = true;
				}
				return FileVisitResult.CONTINUE;
			}
		});

		return changed[0];
	}

	public static Set<Resource> getResources(String pattern) throws IOException {
		ClassLoader cl = ResourcesUtils.class.getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
		return new HashSet<>(Arrays.asList(resolver.getResources("classpath*:/"+pattern))) ;
	}
}
