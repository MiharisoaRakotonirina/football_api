package org.hei.school.fifa_foot_api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class ClubScore {
    private Integer score;
    private List<Scorer> scorers;

    public ClubScore(Integer score, List<Scorer> scorers) {
        this.score = score;
        this.scorers = scorers;
    }

}
