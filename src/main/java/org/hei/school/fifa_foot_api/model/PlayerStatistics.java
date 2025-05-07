package org.hei.school.fifa_foot_api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerStatistics {
    private Integer scoredGoals;
    private PlayingTime playingTime;

    public PlayerStatistics(Integer scoredGoals, PlayingTime playingTime) {
        this.scoredGoals = scoredGoals;
        this.playingTime = playingTime;
    }
}
