package org.hei.school.fifa_foot_api.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MatchClub {
    private ClubMinimumInfo club;
    private ClubScore score;

    public MatchClub(ClubMinimumInfo club, ClubScore score) {
        this.club = club;
        this.score = score;
    }
}
