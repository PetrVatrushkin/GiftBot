package ru.shconar.giftbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.shconar.giftbot.domain.ParticipantRepository;
import ru.shconar.giftbot.domain.entity.Participant;
import ru.shconar.giftbot.domain.entity.Raffle;
import java.util.List;
import java.util.Optional;

@Slf4j @Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public boolean addParticipant(Raffle raffle, Long userId, String username) {
        try {
            Participant participant = new Participant(raffle, userId, username);
            participantRepository.save(participant);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    public boolean addParticipant(Raffle raffle, String username) {
        return addParticipant(raffle, null, username);
    }

    public List<Participant> findParticipants(Raffle raffle) {
        return participantRepository.findByRaffle(raffle);
    }

    public Optional<Participant> findParticipant(String username, Raffle raffle) {
        return participantRepository.findByUsernameAndRaffle(username, raffle);
    }

    public boolean deleteParticipant(Participant participant) {
        try {
            participantRepository.delete(participant);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }
}
