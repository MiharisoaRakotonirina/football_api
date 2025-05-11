package org.hei.school.fifa_foot_api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Player {
    private UUID id;
    private String name;
    private int number;
    private PlayerPosition position;
    private String nationality;
    private int age;
    private Club club;

    public Player(UUID id, String name, int number, PlayerPosition position, String nationality, int age, Club club) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.number = number;
        this.position = position;
        this.nationality = nationality;
        this.age = age;
        this.club = club;
    }

    @Override
    public String toString() {
        return "Player { " +
                " id = " + id +
                ", name = '" + name + '\'' +
                ", number = " + number +
                ", position = " + position +
                ", nationality = '" + nationality + '\'' +
                ", age = " + age +
                ", club = " + club +
                '}';
    }
}
