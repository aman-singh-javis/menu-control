package ai.javis.menucontrol.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ai.javis.menucontrol.model.Menu;

@Repository
public interface MenuRepo extends JpaRepository<Menu, Long> {
    Optional<Menu> findByMenuName(String menuName);

    Boolean existsByMenuName(String menuName);
}
