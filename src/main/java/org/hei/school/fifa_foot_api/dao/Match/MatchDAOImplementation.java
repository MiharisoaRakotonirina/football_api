package org.hei.school.fifa_foot_api.dao.Match;

import org.hei.school.fifa_foot_api.dao.DataSource;
import org.hei.school.fifa_foot_api.model.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MatchDAOImplementation implements MatchDAO{
    private DataSource dataSource;

    public MatchDAOImplementation() {
        this.dataSource = new DataSource();
    }


    @Override
    public UUID getStartedSeasonId(int seasonYear) {
        String sql = "SELECT id FROM season WHERE year = ? AND season_status = 'STARTED'";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, seasonYear);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return UUID.fromString(rs.getString("id"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch started season", e);
        }
    }

    @Override
    public boolean seasonAlreadyHasMatches(int seasonYear) {
        String sql = "SELECT COUNT(*) FROM match WHERE season_year = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, seasonYear);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existing matches", e);
        }
    }

    @Override
    public void saveAllMatches(List<Match> matches, int seasonYear) {
        String sql = """
            INSERT INTO match (id, season_year, home_club_id, away_club_id, stadium, match_datetime, match_status)
            VALUES (?, ?, ?, ?, ?, ?, ?::match_status)
        """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (Match match : matches) {
                ps.setObject(1, match.getId());
                ps.setInt(2, seasonYear);
                ps.setObject(3, match.getClubPlayingHome().getClub().getId());
                ps.setObject(4, match.getClubPlayingAway().getClub().getId());
                ps.setString(5, match.getStadium());
                ps.setTimestamp(6, Timestamp.valueOf(match.getMatchDateTime()));
                ps.setString(7, match.getActualStatus().name());
                ps.addBatch();
            }

            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save matches", e);
        }
    }

    @Override
    public List<Match> findBySeasonYear(int seasonYear) {
        List<Match> matches = new ArrayList<>();
        String sql = """
        SELECT m.id AS match_id,
               m.season_year,
               m.stadium,
               m.match_datetime,
               m.match_status,
               hc.id AS home_id,
               hc.name AS home_name,
               hc.acronym AS home_acronym,
               ac.id AS away_id,
               ac.name AS away_name,
               ac.acronym AS away_acronym
        FROM match m
        JOIN clubs hc ON m.home_club_id = hc.id
        JOIN clubs ac ON m.away_club_id = ac.id
        WHERE m.season_year = ?
    """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, seasonYear);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UUID matchId = UUID.fromString(rs.getString("match_id"));
                String stadium = rs.getString("stadium");
                LocalDateTime matchDateTime = rs.getTimestamp("match_datetime").toLocalDateTime();
                MatchStatus status = MatchStatus.valueOf(rs.getString("match_status"));

                // Club Domicile
                UUID homeId = UUID.fromString(rs.getString("home_id"));
                String homeName = rs.getString("home_name");
                String homeAcronym = rs.getString("home_acronym");
                ClubMinimumInfo homeInfo = new ClubMinimumInfo(homeId, homeName, homeAcronym);
                MatchClub home = new MatchClub(homeInfo, new ClubScore(0, new ArrayList<>())); // score par défaut

                // Club Extérieur
                UUID awayId = UUID.fromString(rs.getString("away_id"));
                String awayName = rs.getString("away_name");
                String awayAcronym = rs.getString("away_acronym");
                ClubMinimumInfo awayInfo = new ClubMinimumInfo(awayId, awayName, awayAcronym);
                MatchClub away = new MatchClub(awayInfo, new ClubScore(0, new ArrayList<>()));

                Match match = new Match();
                match.setId(matchId);
                match.setClubPlayingHome(home);
                match.setClubPlayingAway(away);
                match.setStadium(stadium);
                match.setMatchDateTime(matchDateTime);
                match.setActualStatus(status);

                matches.add(match);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des matchs", e);
        }

        return matches;
    }
}

