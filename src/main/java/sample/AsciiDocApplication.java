package sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AsciiDocApplication {
	public static void main(String[] args) {
//		FontInstaller.installFont(
//			AsciiDocApplication.class.getResource("documentation/assets/fonts/fg-virgil.ttf"));
		SpringApplication.run(AsciiDocApplication.class, args);
	}
}
