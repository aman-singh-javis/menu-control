package ai.javis.menucontrol.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.javis.menucontrol.dto.ApiResponse;
import ai.javis.menucontrol.dto.CreateTeamRequest;
import ai.javis.menucontrol.exception.MenuAlreadyExists;
import ai.javis.menucontrol.exception.TeamAlreadyExists;
import ai.javis.menucontrol.exception.UserNotFound;
import ai.javis.menucontrol.model.Menu;
import ai.javis.menucontrol.model.Team;
import ai.javis.menucontrol.model.User;
import ai.javis.menucontrol.service.MenuService;
import ai.javis.menucontrol.service.TeamService;
import ai.javis.menucontrol.service.UserService;
import jakarta.validation.Valid;

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
    private TeamService service;

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @GetMapping("/team")
    public ResponseEntity<List<Team>> getTeams() {
        return new ResponseEntity<List<Team>>(service.getTeams(), HttpStatus.OK);
    }

    private String getUsernameFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
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
    public ResponseEntity<?> createTeam(@Valid @RequestBody CreateTeamRequest body)
            throws TeamAlreadyExists, UserNotFound {
        String username = getUsernameFromSecurityContext();
        User user = userService.getUserByUsername(username);
        List<User> users = new ArrayList<>();
        users.add(user);
        Team team = service.createTeam(body.getTeamName(), users);

        for (String menu : body.getMenus()) {
            // try {
            // menuService.addMenu(menu, team);
            // } catch (MenuAlreadyExists exc) {
            // Menu menu1 = menuService.getMenuByName(menu);
            // if (menu1 != null) {
            // List<Menu> menus = team.getMenus();
            // menus.add(menu1);
            // team.setMenus(menus);

            // service.updateTeam(team);
            // }
            // }
        }

        ApiResponse<?> rsp = new ApiResponse<>("team created successfully", null);
        return new ResponseEntity<>(rsp, HttpStatus.CREATED);
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
