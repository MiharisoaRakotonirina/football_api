package org.hei.school.fifa_foot_api.Endpoint;

import org.hei.school.fifa_foot_api.model.Club;
import org.hei.school.fifa_foot_api.service.ClubService;
import org.hei.school.fifa_foot_api.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
