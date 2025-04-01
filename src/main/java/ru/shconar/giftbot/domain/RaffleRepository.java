package ru.shconar.giftbot.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shconar.giftbot.domain.entity.Raffle;
import java.util.Optional;

@Repository
public interface RaffleRepository extends JpaRepository<Raffle, Long> {
    Optional<Raffle> findByAdminId(Long adminId);
    Optional<Raffle> findByChannelId(Long channelId);
}
