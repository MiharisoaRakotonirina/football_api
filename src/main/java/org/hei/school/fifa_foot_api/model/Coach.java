package org.hei.school.fifa_foot_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Coach {
    private UUID id;
    private String name;
    private String nationality;
    @JsonIgnore
    private Club club;

    public Coach(UUID id, String name, String nationality, Club club) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.nationality = nationality;
        this.club = club;
    }

    public String toString() {
        return "Coach { " +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", nationality = '" + nationality + '\'' +
                '}';
    }
}
