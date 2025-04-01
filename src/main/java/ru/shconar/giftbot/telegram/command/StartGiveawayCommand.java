package ru.shconar.giftbot.telegram.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.shconar.giftbot.domain.entity.Participant;
import ru.shconar.giftbot.domain.entity.Raffle;
import ru.shconar.giftbot.service.ParticipantService;
import ru.shconar.giftbot.service.RaffleService;
import ru.shconar.giftbot.telegram.configuration.TelegramProperties;
import ru.shconar.giftbot.telegram.info.SendContent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StartGiveawayCommand implements Command {

    private final TelegramProperties telegramProperties;
    private final RaffleService raffleService;
    private final ParticipantService participantService;

    @Override
    public String command() {
        return "/start_giveaway";
    }

    @Override
    public String description() {
        return "Начать розыгрыш";
    }

    @Override
    public SendContent handle(Update update) {

        Long adminId = update.message().from().id();
        String messageText = "У вас нет конкурса";

        Optional<Raffle> raffle = raffleService.findRaffleByAdminId(adminId);
        if (raffle.isPresent()) {
            List<Participant> participants = participantService.findParticipants(raffle.get());
            Collections.shuffle(participants);

            List<String> users = new ArrayList<>(
                participants.stream()
                    .sorted(Comparator.comparingInt(Participant::getPriority))
                    .map(Participant::getUsername)
                    .toList()
            );

            int count = Math.min(telegramProperties.winnersLimit(), users.size());
            List<String> randomUsers = users.subList(0, count);

            messageText = "Список победителей\n" +
                randomUsers.stream()
                    .map(user -> "@" + user)
                    .collect(Collectors.joining("\n"));

            participants.forEach(participantService::deleteParticipant);
            Long channelId = raffle.get().getChannelId();
            raffleService.deleteRaffle(raffle.get());

            return new SendContent(
                new SendMessage(channelId, messageText),
                null
            );
        }
        return new SendContent(
            new SendMessage(adminId, messageText),
            null
        );
    }
}
