package ru.shconar.giftbot.telegram.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.shconar.giftbot.domain.entity.Participant;
import ru.shconar.giftbot.domain.entity.Raffle;
import ru.shconar.giftbot.service.ParticipantService;
import ru.shconar.giftbot.service.RaffleService;
import ru.shconar.giftbot.service.WinnerService;
import ru.shconar.giftbot.telegram.configuration.TelegramProperties;
import ru.shconar.giftbot.telegram.info.SendContent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RollGiveawayCommand implements Command {

    private final TelegramProperties telegramProperties;
    private final RaffleService raffleService;
    private final ParticipantService participantService;
    private final WinnerService winnerService;

    @Override
    public String command() {
        return "/roll_giveaway";
    }

    @Override
    public String description() {
        return "Определить победителя (результат придет в чат с ботом)";
    }

    @Override
    public SendContent handle(Update update) {

        Long adminId = update.message().from().id();
        String messageText = "У вас нет конкурса";

        Optional<Raffle> raffle = raffleService.findRaffleByAdminId(adminId);
        if (raffle.isPresent()) {
            List<Participant> participants = participantService.findParticipants(raffle.get());
            log.info("Участники: {}", listOfParticipantToString(participants));

            Collections.shuffle(participants);
            log.info("Участники после перемешивания: {}", listOfParticipantToString(participants));

            List<Participant> sortedParticipants = participants.stream()
                .sorted(Comparator.comparingInt(Participant::getPriority))
                .toList()
                .reversed();

            List<String> users = new ArrayList<>(
                sortedParticipants.stream()
                    .map(Participant::getUsername)
                    .toList()
            );
            log.info("Участники после сортировки: {}", listOfUsernamesToString(users));

            int count = Math.min(telegramProperties.winnersLimit(), sortedParticipants.size());
            List<Participant> randomParticipants = sortedParticipants.subList(0, count);

            winnerService.addWinners(raffle.get(), randomParticipants);

            messageText = "Список победителей\n" +
                randomParticipants.stream()
                    .map(Participant::getUsername)
                    .map(user -> "@" + user)
                    .collect(Collectors.joining("\n"));

            return new SendContent(
                new SendMessage(adminId, messageText),
                null
            );
        }
        return new SendContent(
            new SendMessage(adminId, messageText),
            null
        );
    }

    private String listOfParticipantToString(List<Participant> participants) {
        return "[" + participants.stream()
            .map(participant -> participant.getUsername() + " - " + participant.getPriority())
            .collect(Collectors.joining(","))
            + "]";
    }

    private String listOfUsernamesToString(List<String> participants) {
        return "[" + String.join(",", participants) + "]";
    }
}
