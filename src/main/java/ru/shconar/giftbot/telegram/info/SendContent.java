package ru.shconar.giftbot.telegram.info;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;

public record SendContent(
    SendMessage sendMessage,
    SendPhoto sendPhoto
) {
    public SendContent() {
        this(null, null);
    }
}
