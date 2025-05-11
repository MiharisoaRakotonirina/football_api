package org.hei.school.fifa_foot_api.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Scorer {
    private PlayerMinimumInfo player;
    private Integer minuteOfGoal;
    private Boolean ownGoal;


    public Scorer(PlayerMinimumInfo player, Integer minuteOfGoal, Boolean ownGoal) {
        this.player = player;
        this.minuteOfGoal = minuteOfGoal;
        this.ownGoal = ownGoal;
    }
}
