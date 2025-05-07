package org.hei.school.fifa_foot_api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayingTime {
    private Double value;
    private DurationUnit durationUnit;

    public PlayingTime(Double value, DurationUnit durationUnit) {
        this.value = value;
        this.durationUnit = durationUnit;
    }
}
