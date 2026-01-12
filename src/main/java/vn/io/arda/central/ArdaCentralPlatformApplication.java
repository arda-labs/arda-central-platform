package vn.io.arda.central;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"vn.io.arda.central", "vn.io.arda.shared"})
public class ArdaCentralPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArdaCentralPlatformApplication.class, args);
	}

}
