package ai.javis.menucontrol.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ai.javis.menucontrol.model.Team;

@Repository
public interface TeamRepo extends JpaRepository<Team, Integer> {
    Optional<Team> findByTeamNameIgnoreCase(String teamName);

    Boolean existsByTeamName(String teamName);
}
