package project.Codelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import project.Codelivery.service.MatchService;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class CoDeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoDeliveryApplication.class, args);

	}
}
