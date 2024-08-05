package ai.javis.menucontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ai.javis.menucontrol.model.Team;

@Repository
public interface TeamRepo extends JpaRepository<Team, Integer> {
    Boolean existsByTeamName(String teamName);
}
