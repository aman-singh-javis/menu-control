package ai.javis.menucontrol.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ai.javis.menucontrol.dto.ApiResponse;
import ai.javis.menucontrol.dto.MenuDTO;
import ai.javis.menucontrol.exception.ForbiddenRequest;
import ai.javis.menucontrol.exception.MenuNotFound;
import ai.javis.menucontrol.exception.UserNotFound;
import ai.javis.menucontrol.model.Menu;
import ai.javis.menucontrol.model.Team;
import ai.javis.menucontrol.model.User;
import ai.javis.menucontrol.service.MenuService;
import ai.javis.menucontrol.service.TeamService;
import ai.javis.menucontrol.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllMenus() throws UserNotFound, MenuNotFound, ForbiddenRequest {
        if (!userService.isMenuAssociatedWithCurrentUser("MANAGE_MENU")) {
            throw new ForbiddenRequest("you don't have permission to view all menu");
        }

        List<MenuDTO> menus = menuService.getAllMenus();
        ApiResponse<List<MenuDTO>> resp = new ApiResponse<>("all org menus", menus);

        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<?> getMenus() throws UserNotFound {
        User curUser = userService.getCurrentUser();

        Set<MenuDTO> menus = curUser.getTeams().stream()
                .flatMap(team -> team.getMenus().stream())
                .map(menu -> menuService.convertModelToDto(menu))
                .collect(Collectors.toSet());

        ApiResponse<Set<MenuDTO>> resp = new ApiResponse<>("menus related to user", menus);
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<?> createMenu(@Valid @RequestBody MenuDTO body)
            throws UserNotFound, MenuNotFound, ForbiddenRequest {
        if (!userService.isMenuAssociatedWithCurrentUser("MANAGE_MENU")) {
            throw new ForbiddenRequest("you don't have permission to create menu");
        }

        User curUser = userService.getCurrentUser();

        Menu menu = menuService.getOrCreateMenu(body.getMenuName(), curUser.getCompany());
        ApiResponse<?> response = new ApiResponse<>("menu created successfully", menuService.convertModelToDto(menu));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMenu(@NotBlank(message = "name is required") @RequestParam String name)
            throws MenuNotFound, ForbiddenRequest, UserNotFound {
        if (!userService.isMenuAssociatedWithCurrentUser("MANAGE_MENU")) {
            throw new ForbiddenRequest("you don't have permission to create menu");
        }

        Menu menu = menuService.getMenuByName(name);

        for (Team team : new ArrayList<>(menu.getTeams())) {
            team.removeMenu(menu);
            teamService.updateTeam(team);
        }

        ApiResponse<?> rspn = new ApiResponse<>("menu " + name + " deleted successfully", null);
        return ResponseEntity.ok(rspn);
    }
}
