package ai.javis.menucontrol.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.javis.menucontrol.dto.MenuDTO;
import ai.javis.menucontrol.exception.MenuAlreadyExists;
import ai.javis.menucontrol.exception.MenuNotFound;
import ai.javis.menucontrol.model.Company;
import ai.javis.menucontrol.model.Menu;
import ai.javis.menucontrol.repository.MenuRepo;

@Service
public class MenuService {

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private ModelMapper modelMapper;

    public List<MenuDTO> getAllMenus() {
        return menuRepo.findAll().stream().map(menu -> convertModelToDto(menu)).collect(Collectors.toList());
    }

    public Menu getMenuById(long menuId) {
        return menuRepo.findById(menuId).orElseThrow();
    }

    public Menu getMenuByName(String menuName) throws MenuNotFound {
        try {
            return menuRepo.findByMenuName(menuName).orElseThrow();
        } catch (NoSuchElementException excep) {
            throw new MenuNotFound("menu with name: " + menuName + " not found");
        }
    }

    public Menu addMenu(String menuName, Company comp) throws MenuAlreadyExists {
        if (menuRepo.existsByMenuName(menuName)) {
            throw new MenuAlreadyExists("menu " + menuName + " already exists");
        }

        Menu menu = new Menu(menuName, comp);
        return menuRepo.save(menu);
    }

    public Menu getOrCreateMenu(String menuName, Company company) {
        Optional<Menu> existingMenu = menuRepo.findByMenuName(menuName.toUpperCase());
        return existingMenu.orElseGet(() -> {
            Menu newMenu = new Menu(menuName.toUpperCase(), company);
            menuRepo.save(newMenu);
            return newMenu;
        });
    }

    public MenuDTO convertModelToDto(Menu menu) {
        return modelMapper.map(menu, MenuDTO.class);
    }

    public Menu convertDtoToModel(MenuDTO menuDTO) {
        return modelMapper.map(menuDTO, Menu.class);
    }
}
