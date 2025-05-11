package org.hei.school.fifa_foot_api.dao.Club;

import org.hei.school.fifa_foot_api.model.Club;
import org.hei.school.fifa_foot_api.model.Player;
import org.hei.school.fifa_foot_api.model.SimplePlayer;

import java.util.List;
import java.util.UUID;

public interface ClubDAO {
    List<Club> getAllClubs();
    List<Club> saveOrUpdateClubs(List<Club> clubs);
    List<SimplePlayer> findPlayersOfClub(UUID clubId);
    Club findById(UUID id);
    List<Player> replacePlayersOfClub(UUID clubId, List<Player> players);
    List<SimplePlayer> addOrAssignPlayersToClub(UUID clubId, List<SimplePlayer> players);
}
