package ai.javis.menucontrol.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.javis.menucontrol.exception.MenuAlreadyExists;
import ai.javis.menucontrol.model.Menu;
import ai.javis.menucontrol.model.Team;
import ai.javis.menucontrol.repository.MenuRepo;

@Service
public class MenuService {

    @Autowired
    MenuRepo repo;

    public List<Menu> getMenus() {
        return repo.findAll();
    }

    public Menu getMenuById(long menuId) {
        return repo.findById(menuId).orElseThrow();
    }

    public Menu getMenuByName(String menuName) {
        return repo.findByMenuName(menuName);
    }

    public Menu addMenu(String menuName, Team team) {
        // if (repo.existsByMenuName(menuName)) {
        // throw new MenuAlreadyExists("menu " + menuName + " already exists");
        // }

        Menu menu = new Menu(menuName, team);
        return repo.save(menu);
    }
}
