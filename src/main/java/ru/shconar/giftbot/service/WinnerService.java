package ru.shconar.giftbot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.shconar.giftbot.domain.entity.Participant;
import ru.shconar.giftbot.domain.entity.Raffle;
import ru.shconar.giftbot.domain.entity.Winner;
import ru.shconar.giftbot.domain.entity.WinnerRepository;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WinnerService {

    private final WinnerRepository winnerRepository;

    @Transactional
    public boolean addWinners(Raffle raffle, List<Participant> participants) {
        try {
            for (Participant participant : participants) {
                Winner winner = new Winner(raffle, participant);
                winnerRepository.save(winner);
            }
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    public boolean deleteAllWinners(Raffle raffle) {
        try {
            winnerRepository.deleteByRaffle(raffle);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    public List<Winner> getWinners(Raffle raffle) {
        return winnerRepository.findByRaffle(raffle);
    }
}
