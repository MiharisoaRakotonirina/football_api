package org.hei.school.fifa_foot_api.service;

import org.hei.school.fifa_foot_api.dao.Season.SeasonDAOImplementation;
import org.hei.school.fifa_foot_api.model.Season;
import org.hei.school.fifa_foot_api.model.SeasonStatus;
import org.hei.school.fifa_foot_api.model.SeasonToCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SeasonService {
    private final SeasonDAOImplementation implementation;

    @Autowired
    public SeasonService(SeasonDAOImplementation implementation) {
        this.implementation = implementation;
    }

    public List<Season> getAllSeasons() {
        return implementation.getAll();
    }

    public List<Season> createSeasons(List<SeasonToCreate> toCreateList) {
        List<Season> seasons = toCreateList.stream().map(seasonToCreate -> {
            Season season = new Season();
            season.setYear(seasonToCreate.getYear());
            season.setAlias(seasonToCreate.getAlias());
            season.setId(UUID.randomUUID());
            season.setSeasonStatus(SeasonStatus.NOT_STARTED);
            return  season;
        }).toList();

        return implementation.saveAll(seasons);
    }

    public Season updateSeasonStatus(int year, SeasonStatus newStatus) {
        Season currentSeason = implementation.findByYear(year);
        SeasonStatus currentStatus = currentSeason.getSeasonStatus();
        boolean valid = (currentStatus == SeasonStatus.NOT_STARTED && newStatus == SeasonStatus.STARTED) || (currentStatus == SeasonStatus.STARTED && newStatus == SeasonStatus.FINISHED);

        if (!valid) {
            throw new IllegalArgumentException("Invalid transition from " + currentStatus + " to " + newStatus);
        }

        return implementation.updateStatus(year, newStatus);
    }
}
