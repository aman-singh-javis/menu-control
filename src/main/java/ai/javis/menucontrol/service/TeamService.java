package ai.javis.menucontrol.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.javis.menucontrol.model.Team;
import ai.javis.menucontrol.repository.TeamRepo;

@Service
public class TeamService {

    @Autowired
    TeamRepo repo;

    public List<Team> getTeams() {
        return repo.findAll();
    }

    public Team getTeamById(int teamId) {
        return repo.findById(teamId).orElseThrow();
    }

    public void addTeam(Team team) {
        repo.save(team);
    }

    public void updateTeam(Team team) {
        repo.save(team);
    }

    public void deleteTeam(Integer teamId) {
        repo.deleteById(teamId);
    }
}
