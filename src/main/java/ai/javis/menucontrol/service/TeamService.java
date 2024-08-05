package ai.javis.menucontrol.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.javis.menucontrol.exception.TeamAlreadyExists;
import ai.javis.menucontrol.model.Team;
import ai.javis.menucontrol.model.User;
import ai.javis.menucontrol.repository.TeamRepo;

@Service
public class TeamService {

    @Autowired
    TeamRepo teamRepo;

    public List<Team> getTeams() {
        return teamRepo.findAll();
    }

    public Team getTeamById(int teamId) {
        return teamRepo.findById(teamId).orElseThrow();
    }

    public Team createTeam(String teamName, List<User> users) throws TeamAlreadyExists {
        if (teamRepo.existsByTeamName(teamName)) {
            throw new TeamAlreadyExists("team with name: " + teamName + " already exists");
        }

        Team team = new Team(teamName, users);
        return teamRepo.save(team);
    }

    public void updateTeam(Team team) {
        teamRepo.save(team);
    }

    // public void deleteTeam(Integer teamId) {
    // repo.deleteById(teamId);
    // }
}
