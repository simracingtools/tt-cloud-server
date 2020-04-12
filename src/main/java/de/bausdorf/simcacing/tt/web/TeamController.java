package de.bausdorf.simcacing.tt.web;

import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.TeamRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import de.bausdorf.simcacing.tt.web.model.NewDriver;
import de.bausdorf.simcacing.tt.web.model.TeamDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class TeamController extends BaseController {

    public static final String TEAM_DRIVERS = "teamDrivers";
    public static final String SELECTED_TEAM = "selectedTeam";
    public static final String TEAMS = "teams";
    public static final String TEAMS_VIEW = "teams";
    public static final String NEW_DRIVER = "newDriver";

    private TeamRepository teamRepository;
    private DriverRepository driverRepository;

    public TeamController(@Autowired TeamRepository teamRepository, @Autowired DriverRepository driverRepository) {
        this.teamRepository = teamRepository;
        this.driverRepository = driverRepository;
    }

    @GetMapping("/teams")
    public String teamsOverview(@RequestParam("teamId") Optional<String> teamId, Model model) {
        if( teamId.isPresent() ) {
            Optional<IRacingTeam> repoTeam = teamRepository.findById(teamId.get());
            if (repoTeam.isPresent() /*&& repoTeam.get().getOwnerId().equals(currentUser().getIRacingId())*/) {
                model.addAttribute(TEAM_DRIVERS, getTeamDrivers(repoTeam.get()));
                model.addAttribute(SELECTED_TEAM, repoTeam.get());
                model.addAttribute(NEW_DRIVER, NewDriver.builder()
                        .id("0")
                        .name("N.N")
                        .teamAdmin(false)
                        .teamId(repoTeam.get().getId())
                        .build());
                return TEAMS_VIEW;
            }
        }
        model.addAttribute(SELECTED_TEAM, IRacingTeam.builder()
                .ownerId(currentUser().getIRacingId())
                .authorizedDriverIds(new ArrayList<>())
                .build()
        );
        model.addAttribute(TEAM_DRIVERS, new ArrayList<>());
        return TEAMS_VIEW;
    }

    @PostMapping("/teams")
    public String saveTeam(@ModelAttribute(SELECTED_TEAM) IRacingTeam resultTeam, Model model) {
        Optional<IRacingTeam> existingTeam = teamRepository.findById(resultTeam.getId());
        if( existingTeam.isPresent() ) {
            if (!existingTeam.get().getOwnerId().equals(currentUser().getIRacingId())) {
                addError("Team with ID " + resultTeam.getId() + " already exists and you are not a team admin.", model);
                model.addAttribute(NEW_DRIVER, NewDriver.builder()
                        .id("0")
                        .name("N.N")
                        .teamAdmin(false)
                        .teamId(resultTeam.getId())
                        .build());
                return TEAMS_VIEW;
            } else {
                existingTeam.get().setName(resultTeam.getName());
                existingTeam.get().setId(resultTeam.getId());
                existingTeam.get().setAuthorizedDriverIds(resultTeam.getAuthorizedDriverIds());
                teamRepository.save(existingTeam.get());
                model.addAttribute(SELECTED_TEAM, existingTeam.get());
            }
        } else {
            resultTeam.setOwnerId(currentUser().getIRacingId());
            teamRepository.save(resultTeam);
            model.addAttribute(SELECTED_TEAM, resultTeam);
        }
        model.addAttribute(TEAMS, getMyTeams());
        model.addAttribute(NEW_DRIVER, NewDriver.builder()
                .id("0")
                .name("N.N")
                .teamAdmin(false)
                .teamId(resultTeam.getId())
                .build());
        return TEAMS_VIEW;
    }

    @GetMapping("/deleteTeam")
    public String deleteTeam(@RequestParam String teamId, Model model) {
        Optional<IRacingTeam> existingTeam = teamRepository.findById(teamId);
        if( !existingTeam.isPresent() ) {
            addWarning("Team ID " + teamId + " does not exist.", model);
        } else {
            if (!existingTeam.get().getOwnerId().equals(currentUser().getIRacingId())) {
                addError("You are not the owner of this team.", model);
            } else {
                teamRepository.delete(teamId);
            }
        }

        return TEAMS_VIEW;
    }

    @PostMapping("/newDriver")
    public String addNewDriver(@ModelAttribute("newDriver") NewDriver newDriver, Model model) {
        IRacingDriver newIracingDriver = IRacingDriver.builder()
                .id(newDriver.getId())
                .name(newDriver.getName())
                .build();
        Optional<IRacingDriver> existingDriver = driverRepository.findById(newDriver.getId());
        if( !existingDriver.isPresent() ) {
            driverRepository.save(newIracingDriver);
        } else {
            newIracingDriver = existingDriver.get();
        }
        Optional<IRacingTeam> team = teamRepository.findById(newDriver.getTeamId());
        if( team.isPresent() ) {
            if( team.get().getAuthorizedDriverIds() == null ) {
                team.get().setAuthorizedDriverIds(new ArrayList<>());
            }
            team.get().getAuthorizedDriverIds().add(newDriver.getId());
            teamRepository.save(team.get());
            List<TeamDriver> teamDrivers = getTeamDrivers(team.get());
            teamDrivers.add(new TeamDriver(newIracingDriver));
            model.addAttribute(SELECTED_TEAM, team.get());
            model.addAttribute(TEAM_DRIVERS, teamDrivers);
        } else {
            addError("Team ID " + newDriver.getTeamId() + " not found.", model);
        }
        return TEAMS_VIEW;
    }

    public List<TeamDriver> getTeamDrivers(IRacingTeam selectedTeam) {
        List<TeamDriver> authorizedDrivers = new ArrayList<>();
        if( selectedTeam.getOwnerId() == null || selectedTeam.getAuthorizedDriverIds() == null) {
            return authorizedDrivers;
        }
        List<String> authorizedDriverIds = selectedTeam.getAuthorizedDriverIds();
        for( String driverId : authorizedDriverIds ) {
            Optional<IRacingDriver> driver = driverRepository.findById(driverId);
            if(driver.isPresent()) {
                TeamDriver teamDriver = new TeamDriver(driver.get());
                if( teamDriver.getId().equals(currentUser().getIRacingId())) {
                    teamDriver.setTeamAdmin(true);
                }
                authorizedDrivers.add(teamDriver);
            }
        }
        return authorizedDrivers;
    }

    @ModelAttribute("teams")
    public List<IRacingTeam> getMyTeams() {
        List<IRacingTeam> teams = teamRepository.findByOwnerId(currentUser().getIRacingId());
        teams.addAll(teamRepository.findByAuthorizedDrivers(currentUser().getIRacingId()).stream()
                .filter(s -> !teams.contains(s)).collect(Collectors.toList())
        );
        return teams;
    }
}
