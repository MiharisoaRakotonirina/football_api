package org.hei.school.fifa_foot_api.dao.Season;

import org.hei.school.fifa_foot_api.model.Season;
import org.hei.school.fifa_foot_api.model.SeasonStatus;

import java.util.List;

public interface SeasonDAO {
    List<Season> getAll();
    List<Season> saveAll(List<Season> seasonsToCreate);
    Season updateStatus(int year, SeasonStatus newStatus);
    Season findByYear(int year);
}
