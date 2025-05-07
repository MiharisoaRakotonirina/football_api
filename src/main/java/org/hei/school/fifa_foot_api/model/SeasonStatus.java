package org.hei.school.fifa_foot_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SeasonStatus {
    NOT_STARTED,STARTED, FINISHED;

    @JsonCreator
    public static SeasonStatus parseEnumIgnoreCase(String key) {
        return SeasonStatus.valueOf(key.toUpperCase());
    }
}
