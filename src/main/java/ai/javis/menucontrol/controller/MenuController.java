package ai.javis.menucontrol.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ai.javis.menucontrol.dto.ApiResponse;
import ai.javis.menucontrol.dto.MenuDTO;
import ai.javis.menucontrol.exception.UserNotFound;
import ai.javis.menucontrol.model.User;
import ai.javis.menucontrol.service.MenuService;
import ai.javis.menucontrol.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    MenuService menuService;

    @Autowired
    UserService userService;

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

    @GetMapping("/all")
    public ResponseEntity<?> getAllMenus() {
        List<MenuDTO> menus = menuService.getAllMenus();
        ApiResponse<List<MenuDTO>> resp = new ApiResponse<>("all org menus", menus);

        return ResponseEntity.ok(resp);
    }

    // @GetMapping("/menu/{menuId}")
    // public ResponseEntity<Menu> getMenuById(@PathVariable long menuId) {
    // try {
    // Menu menu = service.getMenuById(menuId);
    // return new ResponseEntity<>(menu, HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    // }
    // }

    @PostMapping("/menu")
    public String postMethodName(@RequestBody String entity) {

        return entity;
    }

    // @PostMapping("/menu")
    // public ResponseEntity<?> addMenu(@RequestBody Menu menu) {
    // try {
    // service.addMenu(menu);
    // return new ResponseEntity<>(HttpStatus.CREATED);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // @PreAuthorize("hasRole('ADMIN')")
    // @PutMapping("/menu")
    // public ResponseEntity<?> updateMenu(@RequestBody Menu menu) {
    // try {
    // service.updateMenu(menu);
    // return new ResponseEntity<>(HttpStatus.ACCEPTED);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // @PreAuthorize("hasRole('ADMIN')")
    // @DeleteMapping("menu/{menuId}")
    // public ResponseEntity<?> deleteMenu(@PathVariable long menuId) {
    // try {
    // service.deleteMenu(menuId);
    // return new ResponseEntity<>(HttpStatus.ACCEPTED);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }
}
