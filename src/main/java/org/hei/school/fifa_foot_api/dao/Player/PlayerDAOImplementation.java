package org.hei.school.fifa_foot_api.dao.Player;

import org.hei.school.fifa_foot_api.dao.DataSource;
import org.hei.school.fifa_foot_api.model.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class PlayerDAOImplementation implements PlayerDAO{
    private DataSource dataSource;

    public PlayerDAOImplementation() {
        this.dataSource = new DataSource();
    }

    @Override
    public List<Player> getAll() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.id AS player_id, p.name AS player_name, p.number, p.position, p.nationality AS player_nationality, p.age, c.id AS club_id, c.name AS club_name, c.acronym, c.stadium_name, c.yearcreation, co.name AS coach_name, co.nationality AS coach_nationality FROM players p JOIN clubs c ON p.club_id = c.id LEFT JOIN coaches co ON c.id = co.club_id";

        try(Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Club club = new Club(
                        UUID.fromString(rs.getString("club_id")),
                        rs.getString("club_name"),
                        rs.getString("acronym"),
                        rs.getString("stadium_name"),
                        rs.getInt("yearcreation")
                );

                Coach coach = new Coach(
                        null,
                        rs.getString("coach_name"),
                        rs.getString("coach_nationality"),
                        club
                );
                club.setCoach(coach);

                Player player = new Player(
                        UUID.fromString(rs.getString("player_id")),
                        rs.getString("player_name"),
                        rs.getInt("number"),
                        PlayerPosition.valueOf(rs.getString("position")),
                        rs.getString("player_nationality"),
                        rs.getInt("age"),
                        club
                );

                players.add(player);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(players);
        return players;
    }

    @Override
    public List<Player> saveOrUpdatePlayers(List<Player> players) {
        String sql = """
        INSERT INTO players (id, name, number, position, nationality, age, club_id)
        VALUES (?, ?, ?, ?::player_position, ?, ?, ?)
        ON CONFLICT (id) DO UPDATE SET
            name = EXCLUDED.name,
            number = EXCLUDED.number,
            position = EXCLUDED.position,
            nationality = EXCLUDED.nationality,
            age = EXCLUDED.age,
            club_id = EXCLUDED.club_id
        """;

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            for (Player player : players) {
                if (player.getId() == null) {
                    player.setId(UUID.randomUUID());
                }

                ps.setObject(1, player.getId());
                ps.setString(2, player.getName());
                ps.setInt(3, player.getNumber());
                ps.setString(4, player.getPosition().name());
                ps.setString(5, player.getNationality());
                ps.setInt(6, player.getAge());

                if (player.getClub() != null) {
                    ps.setObject(7, player.getClub().getId());
                } else {
                    ps.setNull(7, Types.OTHER);
                }

                ps.addBatch();
            }

            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List<UUID> playerIds = players.stream()
                .map(Player::getId)
                .toList();
        return getAll().stream().filter(player -> playerIds.contains(player.getId())).toList();
    }

    @Override
    public PlayerStatistics getStatisticsByPlayerIdAndSeason(UUID playerId, int seasonYear) {
        String sql = """
            SELECT scored_goals, playing_time_value, duration_unit
            FROM player_statistics
            WHERE player_id = ? AND season_year = ?
        """;

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setObject(1, playerId);
            ps.setInt(2, seasonYear);

            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                PlayerStatistics stats = new PlayerStatistics();
                stats.setScoredGoals(rs.getInt("scored_goals"));

                PlayingTime playingTime = new PlayingTime();
                playingTime.setValue(rs.getDouble("playing_time_value"));
                playingTime.setDurationUnit(DurationUnit.valueOf(rs.getString("duration_unit")));

                stats.setPlayingTime(playingTime);

                return stats;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Player findById(UUID playerId) {
        String sql = """
        SELECT id, name, number, position, nationality, age, club_id
        FROM players
        WHERE id = ?
    """;

        try (Connection conn = dataSource.getConnection()){
             PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setObject(1, playerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Player player = new Player();
                player.setId(UUID.fromString(rs.getString("id")));
                player.setName(rs.getString("name"));
                player.setNumber(rs.getInt("number"));
                player.setPosition(PlayerPosition.valueOf(rs.getString("position")));
                player.setNationality(rs.getString("nationality"));
                player.setAge(rs.getInt("age"));

                String clubIdStr = rs.getString("club_id");
                if (clubIdStr != null) {
                    Club club = new Club();
                    club.setId(UUID.fromString(clubIdStr));
                    player.setClub(club);
                }

                return player;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void assignPlayerToClub(UUID playerId, UUID clubId) {
        String sql = "UPDATE players SET club_id = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection()){
             PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, clubId);
            stmt.setObject(2, playerId);
            stmt.executeUpdate();
            System.out.println("✅ Update player successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
