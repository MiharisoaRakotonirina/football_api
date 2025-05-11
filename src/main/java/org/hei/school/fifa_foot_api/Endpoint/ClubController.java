package org.hei.school.fifa_foot_api.Endpoint;

import org.hei.school.fifa_foot_api.model.Club;
import org.hei.school.fifa_foot_api.model.Player;
import org.hei.school.fifa_foot_api.model.SimplePlayer;
import org.hei.school.fifa_foot_api.service.ClubService;
import org.hei.school.fifa_foot_api.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/clubs")
public class ClubController {
    private final ClubService clubService;

    @Autowired
    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @GetMapping
    public List<Club> getAllClubs() {
        return clubService.getAllClubs();
    }

    @PutMapping
    public List<Club> createOrUpdateClubs(@RequestBody(required = true) List<Club> clubs) {
        return clubService.saveOrUpdateClubs(clubs);
    }

    @GetMapping("/{id}/players")
    public ResponseEntity<?> getPlayersOfClub(@PathVariable(required = true) UUID id) {
        if (clubService.findById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found");
        }
        List<SimplePlayer> players = clubService.findPlayersOfClub(id);
        return ResponseEntity.ok(players);
    }

    @PutMapping("/{id}/players")
    public ResponseEntity<?> replacePlayers(
            @PathVariable("id") UUID clubId,
            @RequestBody List<Player> players) {
        try {
            List<Player> updated = clubService.replacePlayersOfClub(clubId, players);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error.");
        }
    }

    @PostMapping("/{id}/players")
    public ResponseEntity<?> assignPlayers(
            @PathVariable("id") UUID clubId,
            @RequestBody List<SimplePlayer> players
    ) {
        try {
            List<SimplePlayer> updatedPlayers = clubService.assignPlayersToClub(clubId, players);
            return ResponseEntity.ok(updatedPlayers);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Club not found."));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error."));
        }
    }
}
