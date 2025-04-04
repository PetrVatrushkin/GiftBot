package ru.shconar.giftbot.domain.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WinnerRepository extends JpaRepository<Winner, Long> {
    List<Winner> findByRaffle(Raffle raffle);
    void deleteByRaffle(Raffle raffle);
}
