package org.hei.school.fifa_foot_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Club {
    private UUID id;
    private String name;
    private String acronym;
    @JsonProperty("stadium")
    private String stadiumName;
    private Integer yearCreation;
    @JsonIgnore
    private List<Player> players;
    private Coach coach;

    public Club(UUID id, String name, String acronym, String stadiumName, Integer yearCreation) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.acronym = acronym;
        this.stadiumName = stadiumName;
        this.yearCreation = yearCreation;
    }

    @Override
    public String toString() {
        return "Club { " +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", acronym = '" + acronym + '\'' +
                ", stadiumName = '" + stadiumName + '\'' +
                ", yearCreation = " + yearCreation +
                '}';
    }
}
