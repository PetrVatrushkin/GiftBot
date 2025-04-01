package ru.shconar.giftbot.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "raffles")
@Getter
@Setter
@NoArgsConstructor
public class Raffle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel_id", nullable = false, unique = true)
    private Long channelId;

    @Column(name = "admin_id", nullable = false, unique = true)
    private Long adminId;

    public Raffle(Long channelId, Long adminId) {
        this.channelId = channelId;
        this.adminId = adminId;
    }
}
