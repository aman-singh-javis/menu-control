package ai.javis.menucontrol.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.javis.menucontrol.dto.TeamDTO;
import ai.javis.menucontrol.exception.TeamAlreadyExists;
import ai.javis.menucontrol.model.Company;
import ai.javis.menucontrol.model.Team;
import ai.javis.menucontrol.repository.TeamRepo;

@Service
public class TeamService {

    @Autowired
    private TeamRepo teamRepo;

    @Autowired
    private ModelMapper modelMapper;

    public List<TeamDTO> getAllTeams() {
        return teamRepo.findAll().stream().map(team -> convertModelToDto(team)).collect(Collectors.toList());
    }

    public Team getTeamById(int teamId) {
        return teamRepo.findById(teamId).orElseThrow();
    }

    public Team createTeam(String teamName, Company company) throws TeamAlreadyExists {
        if (teamRepo.existsByTeamName(teamName)) {
            throw new TeamAlreadyExists("team with name: " + teamName + " already exists");
        }

        Team team = new Team(teamName, company);
        return teamRepo.save(team);
    }

    public Team getOrCreateTeam(String teamName, Company company) {
        Optional<Team> existingTeam = teamRepo.findByTeamName(teamName.toUpperCase());
        return existingTeam.orElseGet(() -> {
            Team team = new Team(teamName, company);
            teamRepo.save(team);
            return team;
        });
    }

    public Team updateTeam(Team team) {
        return teamRepo.save(team);
    }

    public TeamDTO convertModelToDto(Team team) {
        return modelMapper.map(team, TeamDTO.class);
    }

    public Team convertDtoToModel(TeamDTO teamDTO) {
        return modelMapper.map(teamDTO, Team.class);
    }

    // public void deleteTeam(Integer teamId) {
    // repo.deleteById(teamId);
    // }
}
