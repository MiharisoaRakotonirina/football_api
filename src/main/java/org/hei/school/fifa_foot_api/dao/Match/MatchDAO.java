package org.hei.school.fifa_foot_api.dao.Match;

import org.hei.school.fifa_foot_api.model.Match;

import java.util.List;
import java.util.UUID;

public interface MatchDAO {
    UUID getStartedSeasonId(int seasonYear);
    boolean seasonAlreadyHasMatches(int seasonYear);
    void saveAllMatches(List<Match> matches, int seasonYear);
    List<Match> findBySeasonYear(int seasonYear);
}
