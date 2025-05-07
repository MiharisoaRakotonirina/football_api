package org.hei.school.fifa_foot_api.service;

import org.hei.school.fifa_foot_api.dao.Player.PlayerDAOImplementation;
import org.hei.school.fifa_foot_api.model.Player;
import org.hei.school.fifa_foot_api.model.PlayerStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    private final PlayerDAOImplementation implementation;

    @Autowired
    public PlayerService(PlayerDAOImplementation implementation) {
        this.implementation = implementation;
    }

    public List<Player> getFilteredPlayers(String name, Integer ageMin, Integer ageMax, String clubName) {
        return implementation.getAll().stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> ageMin == null || p.getAge() >= ageMin)
                .filter(p -> ageMax == null || p.getAge() <= ageMax)
                .filter(p -> clubName == null ||
                        (p.getClub() != null && p.getClub().getName().toLowerCase().contains(clubName.toLowerCase())))
                .collect(Collectors.toList());
    }

    public List<Player> saveOrUpdatePlayers(List<Player> players) {
        return implementation.saveOrUpdatePlayers(players);
    }

    public PlayerStatistics getStatisticsOfPlayer(UUID playerId, int seasonYear) {
        return implementation.getStatisticsByPlayerIdAndSeason(playerId, seasonYear);
    }
}
