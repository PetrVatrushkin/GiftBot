package ru.shconar.giftbot.telegram.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import ru.shconar.giftbot.telegram.info.SendContent;

public interface Command {

    String command();

    String description();

    SendContent handle(Update update);

    default boolean supports(Update update) {
        if (update.message() != null) {
            String text = update.message().text();
            String caption = update.message().caption();
            return text != null && (text.equals(command()) || text.startsWith(command()))
                || caption != null && (caption.equals(command()) || caption.startsWith(command()));
        } else {
            return false;
        }
    }

    default BotCommand toApiCommand() {
        return new BotCommand(command(), description());
    }
}
