package de.bausdorf.simcacing.tt.web;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import de.bausdorf.simcacing.tt.stock.TeamRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import de.bausdorf.simcacing.tt.web.model.NewDriver;
import de.bausdorf.simcacing.tt.web.model.TeamView;
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

    public static final String SELECTED_TEAM = "selectedTeam";
    public static final String TEAMS = "teams";
    public static final String TEAMS_VIEW = "teams";
    public static final String NEW_DRIVER = "newDriver";
    public static final String TEAM_ID = "Team ID ";

    private final TeamRepository teamRepository;

    public TeamController(@Autowired TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @GetMapping("/teams")
    public String teamsOverview(@RequestParam("teamId") Optional<String> teamId, Model model) {
        if( teamId.isPresent() ) {
            Optional<IRacingTeam> repoTeam = teamRepository.findById(teamId.get());
            if (repoTeam.isPresent() /*&& repoTeam.get().getOwnerId().equals(currentUser().getIRacingId())*/) {
                model.addAttribute(SELECTED_TEAM, new TeamView(repoTeam.get(), driverRepository, currentUser().getIRacingId()));
                model.addAttribute(NEW_DRIVER, NewDriver.builder()
                        .id("0")
                        .name("N.N")
                        .teamAdmin(false)
                        .teamId(repoTeam.get().getId())
                        .build());
                return TEAMS_VIEW;
            }
        }

        addEmptySelectedTeam(model);
        return TEAMS_VIEW;
    }

    @PostMapping("/teams")
    public String saveTeam(@ModelAttribute(SELECTED_TEAM) TeamView resultTeam, Model model) {
        Optional<IRacingTeam> existingTeam = teamRepository.findById(resultTeam.getId());
        if( existingTeam.isPresent() ) {
            if (!resultTeam.isCurrentUserTeamAdmin()) {
                addError("Team with ID " + resultTeam.getId() + " already exists and you are not a team admin.", model);
                model.addAttribute(NEW_DRIVER, NewDriver.builder()
                        .id("0")
                        .name("N.N")
                        .teamAdmin(false)
                        .teamId(resultTeam.getId())
                        .build());
                return TEAMS_VIEW;
            } else {
                teamRepository.save(resultTeam.getTeam());
                model.addAttribute(SELECTED_TEAM, resultTeam);
            }
        } else {
            resultTeam.setOwnerId(currentUser().getIRacingId());
            teamRepository.save(resultTeam.getTeam());
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
            addWarning(TEAM_ID + teamId + " does not exist.", model);
        } else {
            if (!existingTeam.get().getOwnerId().equals(currentUser().getIRacingId())) {
                addError("Only the team owner is allowed to delete his team.", model);
            } else {
                teamRepository.delete(teamId);
                model.addAttribute(TEAMS, getMyTeams());
            }
        }
        addEmptySelectedTeam(model);
        return TEAMS_VIEW;
    }

    @GetMapping("/removeTeamMember")
    public String removeTeamMember(@RequestParam String teamId, @RequestParam String teamMemberId, Model model) {
        Optional<IRacingTeam> existingTeam = teamRepository.findById(teamId);
        if( !existingTeam.isPresent() ) {
            addWarning(TEAM_ID + teamId + " does not exist.", model);
            addEmptySelectedTeam(model);
            return TEAMS_VIEW;
        } else {
            if (!existingTeam.get().getOwnerId().equals(currentUser().getIRacingId())) {
                addError("Only the team owner is allowed to delete his team member.", model);
                addEmptySelectedTeam(model);
                return TEAMS_VIEW;
            } else {
                existingTeam.get().getAuthorizedDriverIds().remove(teamMemberId);
                teamRepository.save(existingTeam.get());
                model.addAttribute(TEAMS, getMyTeams());
            }
        }
        return "redirect:" + TEAMS_VIEW + "?teamId=" + existingTeam.get().getId();
    }

    @PostMapping("/newDriver")
    public String addNewDriver(@ModelAttribute("newDriver") NewDriver newDriver, Model model) {
        Optional<IRacingDriver> existingDriver = driverRepository.findById(newDriver.getId());
        if( !existingDriver.isPresent() ) {
            driverRepository.save(IRacingDriver.builder()
                    .id(newDriver.getId())
                    .name(newDriver.getName())
                    .build());
        }
        Optional<IRacingTeam> team = teamRepository.findById(newDriver.getTeamId());
        if( team.isPresent() ) {
            if( newDriver.isTeamAdmin() ) {
                team.get().getTeamAdminIds().add(newDriver.getId());
            }
            if( team.get().getAuthorizedDriverIds() == null ) {
                team.get().setAuthorizedDriverIds(new ArrayList<>());
            }
            team.get().getAuthorizedDriverIds().add(newDriver.getId());
            teamRepository.save(team.get());
            model.addAttribute(SELECTED_TEAM, new TeamView(team.get(), driverRepository, currentUser().getIRacingId()));
        } else {
            addError(TEAM_ID + newDriver.getTeamId() + " not found.", model);
        }
        return TEAMS_VIEW;
    }

    @ModelAttribute("teams")
    public List<IRacingTeam> getMyTeams() {
        List<IRacingTeam> teams = teamRepository.findByOwnerId(currentUser().getIRacingId());
        teams.addAll(teamRepository.findByAuthorizedDrivers(currentUser().getIRacingId()).stream()
                .filter(s -> !teams.contains(s)).collect(Collectors.toList())
        );
        return teams;
    }

    private void addEmptySelectedTeam(Model model) {
        model.addAttribute(SELECTED_TEAM, TeamView.builder()
                .ownerId(currentUser().getIRacingId())
                .currentUserTeamAdmin(true)
                .authorizedDrivers(new ArrayList<>())
                .build()
        );
    }
}
