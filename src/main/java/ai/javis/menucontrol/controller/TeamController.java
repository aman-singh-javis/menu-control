package ai.javis.menucontrol.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.javis.menucontrol.dto.ApiResponse;
import ai.javis.menucontrol.dto.TeamDTO;
import ai.javis.menucontrol.exception.ForbiddenRequest;
import ai.javis.menucontrol.exception.MenuNotFound;
import ai.javis.menucontrol.exception.TeamAlreadyExists;
import ai.javis.menucontrol.exception.TeamNotFound;
import ai.javis.menucontrol.exception.UserNotFound;
import ai.javis.menucontrol.model.Menu;
import ai.javis.menucontrol.model.Team;
import ai.javis.menucontrol.model.User;
import ai.javis.menucontrol.service.MenuService;
import ai.javis.menucontrol.service.TeamService;
import ai.javis.menucontrol.service.UserService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @GetMapping
    public ResponseEntity<?> getTeam() throws UserNotFound {
        User curUser = userService.getCurrentUser();
        List<TeamDTO> teams = curUser.getTeams().stream().map(team -> teamService.convertModelToDto(team))
                .collect(Collectors.toList());

        ApiResponse<?> resp = new ApiResponse<>("teams associated with the user", teams);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTeams() throws UserNotFound, MenuNotFound, ForbiddenRequest {
        if (!userService.isMenuAssociatedWithCurrentUser("MANAGE_TEAM")) {
            throw new ForbiddenRequest("you don't have permission to view all team");
        }

        List<TeamDTO> teams = teamService.getAllTeams();
        ApiResponse<?> resp = new ApiResponse<>("all teams in the org", teams);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/team")
    public ResponseEntity<?> createTeam(@Valid @RequestBody TeamDTO body)
            throws TeamAlreadyExists, UserNotFound, MenuNotFound, ForbiddenRequest {
        if (!userService.isMenuAssociatedWithCurrentUser("MANAGE_TEAM")) {
            throw new ForbiddenRequest("you don't have permission to create team");
        }

        User curUser = userService.getCurrentUser();
        Team team = teamService.getOrCreateTeam(body.getTeamName(), curUser.getCompany());

        List<Menu> menus = body.getMenus().stream()
                .map(menu -> menuService.getOrCreateMenu(menu.getMenuName(), curUser.getCompany()))
                .collect(Collectors.toList());

        for (Menu menu : menus) {
            team.addMenu(menu);
            team = teamService.updateTeam(team);
        }

        ApiResponse<?> rsp = new ApiResponse<>("team created successfully", teamService.convertModelToDto(team));
        return ResponseEntity.ok(rsp);
    }

    @PostMapping("/add-menu")
    public ResponseEntity<?> addMenuToTeam(@RequestBody TeamDTO body)
            throws UserNotFound, MenuNotFound, ForbiddenRequest, TeamNotFound {
        if (!userService.isMenuAssociatedWithCurrentUser("MANAGE_TEAM")) {
            throw new ForbiddenRequest("you don't have permission to add menu");
        }

        User curUser = userService.getCurrentUser();
        Team team = teamService.findTeamByName(body.getTeamName());

        List<Menu> menus = body.getMenus().stream()
                .map(menu -> menuService.getOrCreateMenu(menu.getMenuName(), curUser.getCompany()))
                .collect(Collectors.toList());

        for (Menu menu : menus) {
            team.addMenu(menu);
            team = teamService.updateTeam(team);
        }

        ApiResponse<?> rsp = new ApiResponse<>("menu added successfully", teamService.convertModelToDto(team));
        return ResponseEntity.ok(rsp);
    }

    @PostMapping("/remove-menu")
    public ResponseEntity<?> removeMenuFromTeam(@RequestBody TeamDTO body)
            throws UserNotFound, MenuNotFound, ForbiddenRequest, TeamNotFound {
        if (!userService.isMenuAssociatedWithCurrentUser("MANAGE_TEAM")) {
            throw new ForbiddenRequest("you don't have permission to remove menu");
        }

        Team team = teamService.findTeamByName(body.getTeamName());

        List<Menu> menus = body.getMenus()
                .stream()
                .map(menu -> {
                    try {
                        return menuService.getMenuByName(menu.getMenuName());
                    } catch (MenuNotFound e) {
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList());

        for (Menu menu : menus) {
            if (menu == null) {
                continue;
            }

            team.removeMenu(menu);
            team = teamService.updateTeam(team);
        }

        ApiResponse<?> rsp = new ApiResponse<>("menu removed successfully", teamService.convertModelToDto(team));
        return ResponseEntity.ok(rsp);
    }

}
