package org.hei.school.fifa_foot_api.dao.Club;

import org.hei.school.fifa_foot_api.dao.DataSource;
import org.hei.school.fifa_foot_api.model.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ClubDAOImplementation implements ClubDAO{
    private DataSource dataSource;

    public ClubDAOImplementation() {
        this.dataSource = new DataSource();
    }

    @Override
    public List<Club> getAllClubs() {
        List<Club> clubs = new ArrayList<>();
        String sql = """
                        SELECT c.id, c.name, c.acronym, c.stadium_name, c.yearcreation,
                        co.id as coach_id, co.name as coach_name, co.nationality as coach_nationality
                        FROM clubs c
                        LEFT JOIN coaches co ON c.id = co.club_id
                """;

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Club club = new Club();
                club.setId(UUID.fromString(result.getString("id")));
                club.setName(result.getString("name"));
                club.setAcronym(result.getString("acronym"));
                club.setStadiumName(result.getString("stadium_name"));
                club.setYearCreation(result.getInt("yearcreation"));

                String coachId = result.getString("coach_id");
                if (coachId != null) {
                    Coach coach = new Coach();
                    coach.setId(UUID.fromString(coachId));
                    coach.setName(result.getString("coach_name"));
                    coach.setNationality(result.getString("coach_nationality"));
                    club.setCoach(coach);
                }

                clubs.add(club);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return clubs;
    }

    @Override
    public List<Club> saveOrUpdateClubs(List<Club> clubs) {
        List<Club> clubList = new ArrayList<>();
        String sql = """
                        INSERT INTO clubs (id, name, acronym, stadium_name, yearcreation)
                        VALUES (?, ?, ?, ?, ?)
                        ON CONFLICT (id) DO UPDATE SET
                        name = EXCLUDED.name,
                        acronym = EXCLUDED.acronym,
                        stadium_name = EXCLUDED.stadium_name,
                        yearcreation = EXCLUDED.yearcreation
                """;

        try (Connection connection = dataSource.getConnection()){
            PreparedStatement ps = connection.prepareStatement(sql);

            for (Club club : clubs) {
                if (club.getId() == null) {
                    club.setId(UUID.randomUUID());
                }

                ps.setObject(1, club.getId());
                ps.setString(2, club.getName());
                ps.setString(3, club.getAcronym());
                ps.setString(4, club.getStadiumName());
                if (club.getYearCreation() != null) {
                    ps.setInt(5, club.getYearCreation());
                } else {
                    ps.setNull(5, Types.INTEGER);
                }

                ps.addBatch();
            }
            ps.executeBatch();
            return clubs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SimplePlayer> findPlayersOfClub(UUID clubId) {
        List<SimplePlayer> players = new ArrayList<>();
        String sql = "SELECT id, name, number, position, nationality, age FROM players WHERE club_id = ?";

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setObject(1, clubId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                SimplePlayer player = new SimplePlayer();
                player.setId(UUID.fromString(rs.getString("id")));
                player.setName(rs.getString("name"));
                player.setNumber(rs.getInt("number"));
                player.setPosition(PlayerPosition.valueOf(rs.getString("position")));
                player.setNationality(rs.getString("nationality"));
                player.setAge(rs.getInt("age"));
                players.add(player);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return players;
    }

    @Override
    public Club findById(UUID id) {
        String sql = """
                SELECT c.id, c.name, c.acronym, c.stadium_name, c.yearcreation,
                co.id as coach_id, co.name as coach_name, co.nationality as coach_nationality
                FROM clubs c
                LEFT JOIN coaches co ON c.id = co.club_id
                WHERE c.id = ?
    """;

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Club club = new Club();
                club.setId(UUID.fromString(rs.getString("id")));
                club.setName(rs.getString("name"));
                club.setAcronym(rs.getString("acronym"));
                club.setStadiumName(rs.getString("stadium_name"));
                club.setYearCreation(rs.getInt("yearcreation"));

                String coachId = rs.getString("coach_id");
                if (coachId != null) {
                    Coach coach = new Coach();
                    coach.setId(UUID.fromString(coachId));
                    coach.setName(rs.getString("coach_name"));
                    coach.setNationality(rs.getString("coach_nationality"));
                    club.setCoach(coach);
                }

                return club;
            } else {
                return null;
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Player> replacePlayersOfClub(UUID clubId, List<Player> players) {
        String detachSql = "UPDATE players SET club_id = NULL WHERE club_id = ?";
        String attachSql = "UPDATE players SET club_id = ? WHERE id = ?";
        String checkSql = "SELECT id FROM players WHERE id = ? AND club_id IS NOT NULL AND club_id <> ?";

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false); // ⚠️ Début de la transaction

            // Détacher tous les joueurs du club
            try (PreparedStatement detachStmt = connection.prepareStatement(detachSql)) {
                detachStmt.setObject(1, clubId);
                detachStmt.executeUpdate();
            }

            // Attacher les nouveaux joueurs
            for (Player player : players) {
                // Vérifie que le joueur n’est pas déjà dans un autre club
                try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                    checkStmt.setObject(1, player.getId());
                    checkStmt.setObject(2, clubId);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        connection.rollback();
                        throw new IllegalArgumentException("Player " + player.getId() + " is already in another club.");
                    }
                }

                // Attache le joueur au club
                try (PreparedStatement attachStmt = connection.prepareStatement(attachSql)) {
                    attachStmt.setObject(1, clubId);
                    attachStmt.setObject(2, player.getId());
                    attachStmt.executeUpdate();
                }
            }

            connection.commit(); // ✅ Tout est bon, on valide
            return players;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to replace players", e);
        }
    }

    @Override
    public List<SimplePlayer> addOrAssignPlayersToClub(UUID clubId, List<SimplePlayer> simplePlayers) {
        List<SimplePlayer> addedPlayers = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            for (SimplePlayer sp : simplePlayers) {
                UUID playerId = sp.getId();

                if (playerId != null) {
                    String checkSql = "SELECT club_id FROM players WHERE id = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setObject(1, playerId);
                        ResultSet rs = checkStmt.executeQuery();

                        if (rs.next()) {
                            UUID existingClubId = (UUID) rs.getObject("club_id");

                            if (existingClubId != null && !existingClubId.equals(clubId)) {
                                conn.rollback();
                                throw new IllegalStateException("Player " + playerId + " is already attached to another club.");
                            }

                            if (existingClubId == null) {
                                // Assign to a new club
                                String updateSql = "UPDATE players SET club_id = ? WHERE id = ?";
                                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                    updateStmt.setObject(1, clubId);
                                    updateStmt.setObject(2, playerId);
                                    updateStmt.executeUpdate();
                                }
                            }

                            addedPlayers.add(sp);
                            continue;
                        } else {
                            // Provided Player with ID doesn't exist -> we create
                            String insertSql = """
                            INSERT INTO players (id, name, number, position, nationality, age, club_id)
                            VALUES (?, ?, ?, ?::player_position, ?, ?, ?)
                        """;
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                insertStmt.setObject(1, playerId);
                                insertStmt.setString(2, sp.getName());
                                insertStmt.setInt(3, sp.getNumber());
                                insertStmt.setString(4, sp.getPosition().name());
                                insertStmt.setString(5, sp.getNationality());
                                insertStmt.setInt(6, sp.getAge());
                                insertStmt.setObject(7, clubId);
                                insertStmt.executeUpdate();
                            }

                            addedPlayers.add(sp);
                            continue;
                        }
                    }

                } else {
                    // No ID provided -> search by name + number
                    UUID existingPlayerId = null;
                    UUID existingClubId = null;

                    String findSql = "SELECT id, club_id FROM players WHERE name = ? AND number = ?";
                    try (PreparedStatement findStmt = conn.prepareStatement(findSql)) {
                        findStmt.setString(1, sp.getName());
                        findStmt.setInt(2, sp.getNumber());
                        ResultSet rs = findStmt.executeQuery();
                        if (rs.next()) {
                            existingPlayerId = (UUID) rs.getObject("id");
                            existingClubId = (UUID) rs.getObject("club_id");

                            if (existingClubId != null && !existingClubId.equals(clubId)) {
                                conn.rollback();
                                throw new IllegalStateException("Player " + existingPlayerId + " is already attached to another club.");
                            }
                        }
                    }

                    if (existingPlayerId == null) {
                        existingPlayerId = UUID.randomUUID();
                        String insertSql = """
                        INSERT INTO players (id, name, number, position, nationality, age, club_id)
                        VALUES (?, ?, ?, ?::player_position, ?, ?, ?)
                    """;
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setObject(1, existingPlayerId);
                            insertStmt.setString(2, sp.getName());
                            insertStmt.setInt(3, sp.getNumber());
                            insertStmt.setString(4, sp.getPosition().name());
                            insertStmt.setString(5, sp.getNationality());
                            insertStmt.setInt(6, sp.getAge());
                            insertStmt.setObject(7, clubId);
                            insertStmt.executeUpdate();
                        }
                    } else {
                        // Player exists but no club -> assign to the club
                        String updateSql = "UPDATE players SET club_id = ? WHERE id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setObject(1, clubId);
                            updateStmt.setObject(2, existingPlayerId);
                            updateStmt.executeUpdate();
                        }
                    }

                    addedPlayers.add(new SimplePlayer(existingPlayerId, sp.getName(), sp.getNumber(), sp.getPosition(), sp.getNationality(), sp.getAge()));
                }
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL Error: " + e.getMessage());
        }

        return findPlayersOfClub(clubId);
    }

}
