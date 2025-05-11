package org.hei.school.fifa_foot_api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Match {
    private UUID id;
    private MatchClub clubPlayingHome;
    private MatchClub clubPlayingAway;
    private String stadium;
    private LocalDateTime matchDateTime;
    private MatchStatus actualStatus;

    public Match(UUID id, MatchClub clubPlayingHome, MatchClub clubPlayingAway, String stadium, LocalDateTime matchDateTime, MatchStatus actualStatus) {
        this.id = id;
        this.clubPlayingHome = clubPlayingHome;
        this.clubPlayingAway = clubPlayingAway;
        this.stadium = stadium;
        this.matchDateTime = matchDateTime;
        this.actualStatus = actualStatus;
    }
}
