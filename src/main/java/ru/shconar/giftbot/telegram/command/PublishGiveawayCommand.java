package ru.shconar.giftbot.telegram.command;

import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.shconar.giftbot.service.RaffleService;
import ru.shconar.giftbot.telegram.info.SendContent;

@Slf4j @Component
@RequiredArgsConstructor
public class PublishGiveawayCommand implements Command {

    public static final String BUTTON_CALLBACK = "join_giveaway";

    private final RaffleService raffleService;

    @Override
    public String command() {
        return "/publish_giveaway";
    }

    @Override
    public String description() {
        return "Опубликовать розыгрыш";
    }

    @Override
    public SendContent handle(Update update) {

        Long adminId = update.message().from().id();
        String messageText = "Введите корректный ID канала";

        String input = update.message().photo() != null
            ? update.message().caption()
            : update.message().text();
        int startChannelId = input.indexOf(" ");
        int startTextIndex = input.indexOf("\n");

        if (startChannelId != -1 && startTextIndex != -1) {
            String channelId = input.substring(startChannelId + 1, startTextIndex);

            if (raffleService.addRaffle(Long.parseLong(channelId), adminId)) {
                InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                    new InlineKeyboardButton("участвовать").callbackData(BUTTON_CALLBACK)
                );

                messageText = input.substring(startTextIndex + 1);
                if (update.message().photo() != null) {
                    PhotoSize[] photos = update.message().photo();
                    PhotoSize bestPhoto = photos[photos.length - 1];

                    return new SendContent(
                        null,
                        new SendPhoto(channelId, bestPhoto.fileId()).caption(messageText).replyMarkup(keyboard)
                    );
                }
                return new SendContent(
                    new SendMessage(channelId, messageText).replyMarkup(keyboard),
                    null
                );
            } else {
                messageText = "Произошла ошибка создания конкурса";
            }
        }
        return new SendContent(
            new SendMessage(adminId, messageText),
            null
        );
    }
}
