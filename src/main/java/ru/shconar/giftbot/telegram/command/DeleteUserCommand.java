package ru.shconar.giftbot.telegram.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.shconar.giftbot.domain.entity.Participant;
import ru.shconar.giftbot.domain.entity.Raffle;
import ru.shconar.giftbot.service.ParticipantService;
import ru.shconar.giftbot.service.RaffleService;
import ru.shconar.giftbot.telegram.info.SendContent;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeleteUserCommand implements Command {

    private final RaffleService raffleService;
    private final ParticipantService participantService;

    @Override
    public String command() {
        return "/delete_user";
    }

    @Override
    public String description() {
        return "Удалить участника";
    }

    @Override
    public SendContent handle(Update update) {

        Long adminId = update.message().from().id();

        String messageText = "Введите корректное имя пользователя (без @)";
        String input = update.message().text();
        int index = input.indexOf(" ");
        if (index != -1) {
            String username = input.substring(index + 1);
            Optional<Raffle> raffle = raffleService.findRaffleByAdminId(adminId);
            if (raffle.isPresent()) {
                Optional<Participant> participant = participantService.findParticipant(username, raffle.get());
                messageText = participant.map(value -> participantService.deleteParticipant(value)
                        ? "Пользователь @%s удален из числа участников".formatted(username)
                        : "Возникла ошибка при удалении пользователя")
                    .orElse("Пользователя @%s нету в списке участников".formatted(username));
            } else {
                messageText = "У вас нет конкурса";
            }
        }
        return new SendContent(
            new SendMessage(update.message().chat().id(), messageText),
            null
        );
    }
}
