package org.hei.school.fifa_foot_api.dao.Season;

import org.hei.school.fifa_foot_api.dao.DataSource;
import org.hei.school.fifa_foot_api.model.Season;
import org.hei.school.fifa_foot_api.model.SeasonStatus;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class SeasonDAOImplementation implements SeasonDAO{
    private DataSource dataSource;

    public SeasonDAOImplementation() {
        this.dataSource = new DataSource();
    }
    @Override
    public List<Season> getAll() {
        List<Season> seasons = new ArrayList<>();
        String sql = "SELECT id, year, alias, season_status FROM season ORDER BY year DESC";

        try (Connection connection = dataSource.getConnection())  {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Season season = new Season();
                season.setId(UUID.fromString(resultSet.getString("id")));
                season.setYear(resultSet.getInt("year"));
                season.setAlias(resultSet.getString("alias"));
                season.setSeasonStatus(
                        SeasonStatus.valueOf(resultSet.getString("season_status"))
                );
                seasons.add(season);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(seasons);
        return seasons;
    }

    @Override
    public List<Season> saveAll(List<Season> seasonsToCreate) {
        String sql = "INSERT INTO season (id, year, alias, season_status) VALUES (?, ?, ?, ?::season_status)";

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (Season season : seasonsToCreate) {
                UUID generatedId = UUID.randomUUID();
                season.setId(generatedId);
                season.setSeasonStatus(SeasonStatus.NOT_STARTED);

                preparedStatement.setObject(1, season.getId());
                preparedStatement.setInt(2, season.getYear());
                preparedStatement.setString(3, season.getAlias());
                preparedStatement.setString(4, season.getSeasonStatus().name());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return getAll();
    }

    @Override
    public Season updateStatus(int year, SeasonStatus newStatus) {
        String sql = "UPDATE season SET season_status = ?::season_status WHERE year = ?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newStatus.name());
            statement.setInt(2, year);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new IllegalArgumentException("No season found to update for year: " + year);
            }
            return findByYear(year);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Season findByYear(int year) {
        String sql = "SELECT id, year, alias, season_status FROM season WHERE year = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, year);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                Season season = new Season();
                season.setId(UUID.fromString(rs.getString("id")));
                season.setYear(rs.getInt("year"));
                season.setAlias(rs.getString("alias"));
                season.setSeasonStatus(SeasonStatus.valueOf(rs.getString("season_status")));
                System.out.println(season);
                return season;
            } else {
                throw new IllegalArgumentException("No season found for year: " + year);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
