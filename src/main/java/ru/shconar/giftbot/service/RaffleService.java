package ru.shconar.giftbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.shconar.giftbot.domain.RaffleRepository;
import ru.shconar.giftbot.domain.entity.Raffle;
import java.util.Optional;

@Slf4j @Service
@RequiredArgsConstructor
public class RaffleService {

    private final RaffleRepository raffleRepository;

    public boolean addRaffle(Long channelId, Long adminId) {
        try {
            Raffle raffle = new Raffle(channelId, adminId);
            raffleRepository.save(raffle);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    public Optional<Raffle> findRaffleByAdminId(Long adminId) {
        return raffleRepository.findByAdminId(adminId);
    }

    public Optional<Raffle> findRaffleByChannelId(Long channelId) {
        return raffleRepository.findByChannelId(channelId);
    }

    public boolean deleteRaffle(Raffle raffle) {
        try {
            raffleRepository.delete(raffle);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }
}
