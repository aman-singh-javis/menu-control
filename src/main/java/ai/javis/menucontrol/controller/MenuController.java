package ai.javis.menucontrol.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import ai.javis.menucontrol.model.Menu;
import ai.javis.menucontrol.service.MenuService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api")
public class MenuController {

    @Autowired
    MenuService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/menu")
    public ResponseEntity<List<Menu>> getMenus() {
        return new ResponseEntity<>(service.getMenus(), HttpStatus.OK);
    }

    @GetMapping("/menu/{menuId}")
    public ResponseEntity<Menu> getMenuById(@PathVariable long menuId) {
        try {
            Menu menu = service.getMenuById(menuId);
            return new ResponseEntity<>(menu, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    // @PreAuthorize("hasRole('ADMIN')")
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
