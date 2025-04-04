package ru.shconar.giftbot.telegram.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.shconar.giftbot.domain.entity.Participant;
import ru.shconar.giftbot.domain.entity.Raffle;
import ru.shconar.giftbot.domain.entity.Winner;
import ru.shconar.giftbot.service.RaffleService;
import ru.shconar.giftbot.service.WinnerService;
import ru.shconar.giftbot.telegram.info.SendContent;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishResultsCommand implements Command {

    private final RaffleService raffleService;
    private final WinnerService winnerService;

    @Override
    public String command() {
        return "/publish_results";
    }

    @Override
    public String description() {
        return "Опубликовать результаты розыгрыша в канале (конкурс и все участники будут удалены)";
    }

    @Override
    public SendContent handle(Update update) {

        Long adminId = update.message().from().id();
        String messageText = "У вас нет конкурса";

        Optional<Raffle> raffle = raffleService.findRaffleByAdminId(adminId);
        if (raffle.isPresent()) {

            Long channelId = raffle.get().getChannelId();
            List<Winner> winners = winnerService.getWinners(raffle.get());

            messageText = "Список победителей\n" +
                winners.stream()
                    .map(Winner::getParticipant)
                    .map(Participant::getUsername)
                    .map(user -> "@" + user)
                    .collect(Collectors.joining("\n"));

            boolean result = raffleService.deleteRaffle(raffle.get());
            if (result) log.info("Конкурс пользователя {} и все участники удалены из БД", adminId);

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
