package ru.shconar.giftbot.telegram;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import ru.shconar.giftbot.telegram.command.Command;
import ru.shconar.giftbot.telegram.info.SendContent;

import java.util.List;

public interface UserMessageProcessor {
    List<? extends Command> commands();

    BotCommand[] commandsForMenu();

    SendContent process(Update update);
}
