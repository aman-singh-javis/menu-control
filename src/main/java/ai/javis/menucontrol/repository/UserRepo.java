package ai.javis.menucontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ai.javis.menucontrol.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByEmailIgnoreCase(String emailId);

    User findByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);
}
