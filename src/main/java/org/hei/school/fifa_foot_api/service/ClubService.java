package org.hei.school.fifa_foot_api.service;

import org.hei.school.fifa_foot_api.dao.Club.ClubDAOImplementation;
import org.hei.school.fifa_foot_api.dao.Player.PlayerDAOImplementation;
import org.hei.school.fifa_foot_api.model.Club;
import org.hei.school.fifa_foot_api.model.Player;
import org.hei.school.fifa_foot_api.model.SimplePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClubService {
    private final ClubDAOImplementation implementation;

    @Autowired
    public ClubService(ClubDAOImplementation implementation) {
        this.implementation = implementation;
    }

    public List<Club> getAllClubs() {
        return implementation.getAllClubs();
    }

    public List<Club> saveOrUpdateClubs(List<Club> clubs) {
        return implementation.saveOrUpdateClubs(clubs);
    }
    public List<SimplePlayer> findPlayersOfClub(UUID clubId) {
        return implementation.findPlayersOfClub(clubId);
    }

    public Club findById(UUID id) {
        return implementation.findById(id);
    }

    public List<Player> replacePlayersOfClub(UUID clubId, List<Player> players) {
        return implementation.replacePlayersOfClub(clubId, players);
    }
}
