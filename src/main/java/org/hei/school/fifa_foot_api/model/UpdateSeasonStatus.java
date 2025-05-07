package org.hei.school.fifa_foot_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateSeasonStatus {
    private SeasonStatus newStatus;

    public SeasonStatus getNewStatus() {
        return newStatus;
    }

    @JsonProperty("newStatus")
    public void setNewStatus(String status) {
        if (status != null) {
            this.newStatus = SeasonStatus.valueOf(status.toUpperCase());
        }
    }

    public UpdateSeasonStatus(SeasonStatus newStatus) {
        this.newStatus = newStatus;
    }
}
