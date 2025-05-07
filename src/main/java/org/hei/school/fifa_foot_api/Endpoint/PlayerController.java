package org.hei.school.fifa_foot_api.Endpoint;

import org.hei.school.fifa_foot_api.model.Player;
import org.hei.school.fifa_foot_api.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/players")
@RestController
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<Player> getAllPlayers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer ageMin,
            @RequestParam(required = false) Integer ageMax,
            @RequestParam(required = false) String clubName
    ) {
        return playerService.getFilteredPlayers(name, ageMin, ageMax, clubName);
    }

    @PutMapping
    public List<Player> createOrUpdatePlayers(@RequestBody(required = true) List<Player> players) {
        return playerService.saveOrUpdatePlayers(players);
    }
}
