package ru.shconar.giftbot.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "participants",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"raffle_id", "username"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "raffle_id", nullable = false)
    private Raffle raffle;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "priority")
    private Integer priority;

    public Participant(Raffle raffle, Long userId, String username) {
        this.raffle = raffle;
        this.userId = userId;
        this.username = username;
        this.priority = 0;
    }

    public Participant(Raffle raffle, String username) {
        this.raffle = raffle;
        this.username = username;
        this.priority = 0;
    }
}
