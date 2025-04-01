package ru.shconar.giftbot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.shconar.giftbot.domain.entity.Participant;
import ru.shconar.giftbot.domain.entity.Raffle;
import ru.shconar.giftbot.service.ParticipantService;
import ru.shconar.giftbot.service.RaffleService;
import ru.shconar.giftbot.telegram.command.Command;
import ru.shconar.giftbot.telegram.info.SendContent;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static ru.shconar.giftbot.telegram.command.PublishGiveawayCommand.BUTTON_CALLBACK;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUserMessageProcessor implements UserMessageProcessor {

    private final List<Command> commands;
    private final TelegramBot bot;
    private final ParticipantService participantService;
    private final RaffleService raffleService;

    @Override
    public List<Command> commands() {
        return commands;
    }

    @Override
    public BotCommand[] commandsForMenu() {
        int size = commands.size();
        BotCommand[] menuCommands = new BotCommand[size];
        for (int i = 0; i < size; i++) {
            menuCommands[i] = commands.get(i).toApiCommand();
        }
        return menuCommands;
    }

    @Override
    public SendContent process(Update update) {
        if (update.callbackQuery() != null) {
            return handleCallback(update.callbackQuery());
        }
        for (Command command : commands) {
            if (command.supports(update)) {
                return command.handle(update);
            }
        }
        return new SendContent(
            new SendMessage(update.message().chat().id(), "Неизвестная команда"),
            null
        );
    }

    private SendContent handleCallback(CallbackQuery callbackQuery) {
        if (BUTTON_CALLBACK.equals(callbackQuery.data())) {
            long userId = callbackQuery.from().id();
            String username = callbackQuery.from().username();
            long chatId = callbackQuery.maybeInaccessibleMessage().chat().id();

            Optional<Raffle> optionalRaffle = raffleService.findRaffleByChannelId(chatId);
            if (optionalRaffle.isPresent()) {
                Raffle raffle = optionalRaffle.get();
                List<Participant> participants = participantService.findParticipants(raffle);

                if (isUserParticipant(participants, userId)) {
                    log.info("Пользователь {} уже участвует", username);
                    bot.execute(new AnswerCallbackQuery(callbackQuery.id())
                        .text("так ты уже ;)")
                        .showAlert(true));
                } else {
                    if (isUserSubscribed(chatId, userId)) {

                        participantService.addParticipant(raffle, userId, username);
                        log.info("Пользователь {} зарегистрирован", username);

                        bot.execute(new AnswerCallbackQuery(callbackQuery.id())
                            .text("ты лично участвуешь в конкурсе XD")
                            .showAlert(true));

                        return new SendContent(
                            new SendMessage(
                                raffle.getAdminId(),
                                String.format("Пользователь %s участвует", username)
                            ),
                            null
                        );
                    } else {
                        bot.execute(new AnswerCallbackQuery(callbackQuery.id())
                            .text("так ты не подписан :/")
                            .showAlert(true));
                    }
                }
            }
        }
        return new SendContent();
    }

    private boolean isUserParticipant(List<Participant> participants, long userId) {
        return !participants.stream()
            .map(Participant::getUserId)
            .filter(Predicate.isEqual(userId))
            .toList()
            .isEmpty();
    }

    private boolean isUserSubscribed(long chatId, long userId) {
        GetChatMember getChatMember = new GetChatMember(chatId, userId);
        ChatMember chatMember = bot.execute(getChatMember).chatMember();

        ChatMember.Status status = chatMember.status();
        return switch (status) {
            case member, administrator, creator -> true;
            case null, default -> false;
        };
    }
}
