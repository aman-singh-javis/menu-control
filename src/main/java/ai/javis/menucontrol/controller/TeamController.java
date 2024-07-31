package ai.javis.menucontrol.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.javis.menucontrol.model.Team;
import ai.javis.menucontrol.service.TeamService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
public class TeamController {

    @Autowired
    TeamService service;

    @GetMapping("/team")
    public ResponseEntity<List<Team>> getTeams() {
        return new ResponseEntity<List<Team>>(service.getTeams(), HttpStatus.OK);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<Team> getTeamById(@PathVariable int teamId) {
        try {
            Team team = service.getTeamById(teamId);
            return new ResponseEntity<>(team, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/team")
    public ResponseEntity<?> addTeam(@RequestBody Team team) {
        try {
            service.addTeam(team);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/team")
    public ResponseEntity<?> updateMenu(@RequestBody Team team) {
        try {
            service.updateTeam(team);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("team/{teamId}")
    public ResponseEntity<?> deleteMenu(@PathVariable int teamId) {
        try {
            service.deleteTeam(teamId);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
