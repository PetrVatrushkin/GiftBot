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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListUsersCommand implements Command {

    private final RaffleService raffleService;
    private final ParticipantService participantService;

    @Override
    public String command() {
        return "/list_users";
    }

    @Override
    public String description() {
        return "Список участников";
    }

    @Override
    public SendContent handle(Update update) {

        Long adminId = update.message().from().id();
        String messageText = "У вас нет конкурса";

        Optional<Raffle> raffle = raffleService.findRaffleByAdminId(adminId);
        if (raffle.isPresent()) {
            List<Participant> participants = participantService.findParticipants(raffle.get());
            messageText = "Список участников:\n"
                + participants.stream()
                .map(user -> "@" + user.getUsername() + " - priority: " + user.getPriority())
                .collect(Collectors.joining("\n"));
        }
        return new SendContent(
            new SendMessage(update.message().chat().id(), messageText),
            null
        );
    }
}
