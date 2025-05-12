package org.hei.school.fifa_foot_api.Endpoint;


import org.hei.school.fifa_foot_api.model.Match;
import org.hei.school.fifa_foot_api.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class MatchController {
    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/matchMaker/{seasonYear}")
    public ResponseEntity<?> generateMatches(@PathVariable(required = true) int seasonYear){
        try {
            List<Match> matches = matchService.generateMatchesForSeason(seasonYear);
            return ResponseEntity.ok(matches);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @GetMapping("/matches/{seasonYear}")
    public ResponseEntity<List<Match>> getMatchesBySeason( @PathVariable int seasonYear, @RequestParam(required = false) String matchStatus, @RequestParam(required = false) String clubPlayingName, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate matchAfter, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate matchBeforeOrEquals) {
        List<Match> filteredMatches = matchService.getMatchesForSeason(seasonYear, matchStatus, clubPlayingName, matchAfter, matchBeforeOrEquals);

        if (filteredMatches.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filteredMatches);
    }
}
