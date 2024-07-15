package comatching.comatching3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Comatching3Application {

	public static void main(String[] args) {
		SpringApplication.run(Comatching3Application.class, args);
	}

}
