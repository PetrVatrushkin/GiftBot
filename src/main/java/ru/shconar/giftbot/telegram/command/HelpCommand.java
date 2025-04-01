package ru.shconar.giftbot.telegram.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.shconar.giftbot.telegram.info.SendContent;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HelpCommand implements Command {

    private final List<Command> commands;

    public HelpCommand(List<Command> commands) {
        this.commands = commands;
        this.commands.addFirst(this);
    }

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String description() {
        return "Вывести окно с командами";
    }

    @Override
    public SendContent handle(Update update) {
        String messageText = "Список доступных команд:\n"
            + commands.stream()
                .map(com -> com.command() + " - " + com.description())
                .collect(Collectors.joining("\n"));
        return new SendContent(
            new SendMessage(update.message().chat().id(), messageText),
            null
        );
    }
}
