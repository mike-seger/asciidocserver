package sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;


@Slf4j
public class ResourcesUtilsTest {
    @Test
    public void testGetResources() {
        try {
            Set<String> fonts =
                ResourcesUtils.getResources("documentation/assets/fonts/*.ttf")
                    .stream().map(Resource::getFilename).collect(Collectors.toSet());
            assertTrue(fonts.size()>0);
            log.info("fonts: {}", fonts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInstallFonts() {
        FontInstaller.installFonts("documentation/assets/fonts");
    }
}
