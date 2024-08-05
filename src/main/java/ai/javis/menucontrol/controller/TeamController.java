package ai.javis.menucontrol.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.javis.menucontrol.dto.ApiResponse;
import ai.javis.menucontrol.dto.CreateTeamRequest;
import ai.javis.menucontrol.dto.TeamDTO;
import ai.javis.menucontrol.exception.TeamAlreadyExists;
import ai.javis.menucontrol.exception.UserNotFound;
import ai.javis.menucontrol.model.Team;
import ai.javis.menucontrol.model.User;
import ai.javis.menucontrol.service.TeamService;
import ai.javis.menucontrol.service.UserService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getTeam() throws UserNotFound {
        User curUser = userService.getCurrentUser();
        List<TeamDTO> teams = curUser.getTeams().stream().map(team -> teamService.convertModelToDto(team))
                .collect(Collectors.toList());

        ApiResponse<?> resp = new ApiResponse<>("teams associated with the user", teams);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTeams() {
        List<TeamDTO> teams = teamService.getAllTeams();
        ApiResponse<?> resp = new ApiResponse<>("all teams in the org", teams);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<Team> getTeamById(@PathVariable int teamId) {
        try {
            Team team = teamService.getTeamById(teamId);
            return new ResponseEntity<>(team, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/team")
    public ResponseEntity<?> createTeam(@Valid @RequestBody CreateTeamRequest body)
            throws TeamAlreadyExists, UserNotFound {
        User curUser = userService.getCurrentUser();
        Team team = teamService.getOrCreateTeam(body.getTeamName(), curUser.getCompany());

        ApiResponse<?> rsp = new ApiResponse<>("team created successfully", teamService.convertModelToDto(team));
        return ResponseEntity.ok(rsp);
    }

    // @PostMapping("/team")
    // public ResponseEntity<?> addTeam(@RequestBody Team team) {
    // try {
    // service.addTeam(team);
    // return new ResponseEntity<>(HttpStatus.CREATED);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // @PutMapping("/team")
    // public ResponseEntity<?> updateMenu(@RequestBody Team team) {
    // try {
    // service.updateTeam(team);
    // return new ResponseEntity<>(HttpStatus.ACCEPTED);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // @DeleteMapping("team/{teamId}")
    // public ResponseEntity<?> deleteMenu(@PathVariable int teamId) {
    // try {
    // service.deleteTeam(teamId);
    // return new ResponseEntity<>(HttpStatus.ACCEPTED);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

}
