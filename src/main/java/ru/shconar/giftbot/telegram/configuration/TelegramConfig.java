package ru.shconar.giftbot.telegram.configuration;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramConfig {

    @Bean
    public TelegramBot bot(TelegramProperties telegramProperties) {
        return new TelegramBot(telegramProperties.botToken());
    }
}
