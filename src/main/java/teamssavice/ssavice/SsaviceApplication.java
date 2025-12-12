package teamssavice.ssavice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SsaviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsaviceApplication.class, args);
	}

}
