package ru.shconar.giftbot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.shconar.giftbot.telegram.info.SendContent;

import java.util.List;

@Slf4j
@Component
public class MyTelegramBot implements Bot {

    private final UserMessageProcessor messageProcessor;
    private final TelegramBot bot;

    public MyTelegramBot(UserMessageProcessor messageProcessor, TelegramBot bot) {
        this.messageProcessor = messageProcessor;
        this.bot = bot;
        this.bot.execute(new SetMyCommands(messageProcessor.commandsForMenu()));
    }

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        SendResponse sendResponse = null;
        if (request instanceof SendMessage sm) {
            sendResponse = bot.execute(sm);
        } else if (request instanceof SendPhoto sp) {
            sendResponse = bot.execute(sp);
        }
        logResponse(sendResponse);
    }

    private void logResponse(SendResponse sendResponse) {
        if (sendResponse == null) return;
        log.info(sendResponse.toString());
        if (!sendResponse.isOk()) {
            log.warn(sendResponse.description());
        }
    }

    @Override
    public int process(List<Update> updates) {
        int processedUpdates = 0;
        for (Update update : updates) {
            if (update.message() != null || update.callbackQuery() != null) {
                SendContent sendContent = messageProcessor.process(update);
                execute(sendContent.sendMessage());
                execute(sendContent.sendPhoto());
                processedUpdates++;
            }
        }
        return processedUpdates;
    }

    @PostConstruct
    @Override
    public void start() {
        bot.setUpdatesListener(updates -> {
            process(updates);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            if (e.response() != null) {
                log.warn(e.response().description(), e);
            }
        });
    }

    @Override
    public void close() {}
}
