package org.hei.school.fifa_foot_api.dao.Player;

import org.hei.school.fifa_foot_api.model.Player;
import org.hei.school.fifa_foot_api.model.PlayerStatistics;

import java.util.List;
import java.util.UUID;

public interface PlayerDAO {
    List<Player> getAll();
    List<Player> saveOrUpdatePlayers(List<Player> players);
    PlayerStatistics getStatisticsByPlayerIdAndSeason(UUID playerId, int seasonYear);
}
