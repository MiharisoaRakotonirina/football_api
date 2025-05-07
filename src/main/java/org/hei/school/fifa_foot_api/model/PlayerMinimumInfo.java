package org.hei.school.fifa_foot_api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PlayerMinimumInfo {
    private UUID id;
    private String name;
    private Integer number;

    public PlayerMinimumInfo(UUID id, String name, Integer number) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.number = number;
    }
}
