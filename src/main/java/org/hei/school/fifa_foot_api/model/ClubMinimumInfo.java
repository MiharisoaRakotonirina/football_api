package org.hei.school.fifa_foot_api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class ClubMinimumInfo {
    private UUID id;
    private String name;
    private String acronym;

    public ClubMinimumInfo(UUID id, String name, String acronym) {
        this.id = id;
        this.name = name;
        this.acronym = acronym;
    }
}
