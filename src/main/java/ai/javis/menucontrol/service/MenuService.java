package ai.javis.menucontrol.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.javis.menucontrol.model.Menu;
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

    public void addMenu(Menu menu) {
        repo.save(menu);
    }

    public void updateMenu(Menu menu) {
        repo.save(menu);
    }

    public void deleteMenu(Long menuId) {
        repo.deleteById(menuId);
    }
}
