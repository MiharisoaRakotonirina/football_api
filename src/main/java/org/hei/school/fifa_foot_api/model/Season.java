package org.hei.school.fifa_foot_api.model;

import lombok.Getter;
import lombok.Setter;
import org.hei.school.fifa_foot_api.dao.Season.SeasonDAOImplementation;

import java.util.UUID;


@Getter
@Setter
public class Season {
    private Integer year;
    private String alias;
    private UUID id;
    private SeasonStatus seasonStatus;

    public Season() {
        this.id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "Season { " +
                " year = " + year +
                ", alias = '" + alias + '\'' +
                ", id = " + id +
                ", seasonStatus = " + seasonStatus +
                '}';
    }

    public static void main(String[] args) {
        SeasonDAOImplementation i = new SeasonDAOImplementation();

        i.findByYear(2016);
    }
}
