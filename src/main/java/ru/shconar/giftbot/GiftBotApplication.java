package ru.shconar.giftbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "ru.shconar.giftbot")
public class GiftBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiftBotApplication.class, args);
	}

}
