package ru.shconar.giftbot.domain.entity;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WinnerRepository extends JpaRepository<Winner, Long> {
    List<Winner> findByRaffle(Raffle raffle);

    @Transactional
    void deleteByRaffle(Raffle raffle);
}
