package org.hei.school.fifa_foot_api.service;


import org.hei.school.fifa_foot_api.dao.Match.MatchDAOImplementation;
import org.hei.school.fifa_foot_api.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MatchService {
    private MatchDAOImplementation implementation;
    private ClubService clubService;

    private boolean filterByStatus(Match match, String matchStatus) {
        if (matchStatus == null) return true;
        return match.getActualStatus().name().equalsIgnoreCase(matchStatus);
    }

    private boolean filterByClubName(Match match, String clubPlayingName) {
        if (clubPlayingName == null || clubPlayingName.isBlank()) return true;

        String search = clubPlayingName.toLowerCase();
        String homeName = match.getClubPlayingHome().getClub().getName().toLowerCase();
        String awayName = match.getClubPlayingAway().getClub().getName().toLowerCase();

        return homeName.contains(search) || awayName.contains(search);
    }

    private boolean filterByDateAfter(Match match, LocalDate matchAfter) {
        if (matchAfter == null) return true;
        return match.getMatchDateTime().toLocalDate().isAfter(matchAfter);
    }

    private boolean filterByDateBeforeOrEquals(Match match, LocalDate matchBeforeOrEquals) {
        if (matchBeforeOrEquals == null) return true;
        return !match.getMatchDateTime().toLocalDate().isAfter(matchBeforeOrEquals);
    }



    @Autowired
    public MatchService(MatchDAOImplementation implementation, ClubService clubService) {
        this.implementation = implementation;
        this.clubService = clubService;
    }

    public List<Match> generateMatchesForSeason(int seasonYear) {
        UUID seasonId = implementation.getStartedSeasonId(seasonYear);

        if (seasonId == null) {
            throw new IllegalArgumentException("Season not found or not started");
        }

        if(implementation.seasonAlreadyHasMatches(seasonYear)) {
            throw new IllegalArgumentException("Matches already exist for season " + seasonYear);
        }
        List<Club> clubs = clubService.getAllClubs();
        List<Match> matches = new ArrayList<>();

        for (int i = 0; i < clubs.size(); i++) {
            for (int j = 0; j < clubs.size(); j++) {
                if (i != j) {
                    Club home = clubs.get(i);
                    Club away = clubs.get(j);

                    Match match = new Match();
                    match.setId(UUID.randomUUID());
                    match.setStadium(home.getStadiumName());
                    match.setMatchDateTime(LocalDateTime.now().plusDays(i + j));
                    match.setActualStatus(MatchStatus.NOT_STARTED);

                    MatchClub homeClub = new MatchClub();
                    homeClub.setClub(new ClubMinimumInfo(home.getId(), home.getName(), home.getAcronym()));
                    homeClub.setScore(new ClubScore());
                    match.setClubPlayingHome(homeClub);

                    MatchClub awayClub = new MatchClub();
                    awayClub.setClub(new ClubMinimumInfo(away.getId(), away.getName(), away.getAcronym()));
                    awayClub.setScore(new ClubScore());
                    match.setClubPlayingAway(awayClub);

                    matches.add(match);
                }
            }
        }

        implementation.saveAllMatches(matches, seasonYear);

        return matches;
    }

    public List<Match> getMatchesForSeason(int seasonYear, String matchStatus, String clubPlayingName, LocalDate matchAfter, LocalDate matchBeforeOrEquals) {
        List<Match> matches = implementation.findBySeasonYear(seasonYear);

        return matches.stream()
                .filter(match -> filterByStatus(match, matchStatus))
                .filter(match -> filterByClubName(match, clubPlayingName))
                .filter(match -> filterByDateAfter(match, matchAfter))
                .filter(match -> filterByDateBeforeOrEquals(match, matchBeforeOrEquals))
                .toList();
    }
}

