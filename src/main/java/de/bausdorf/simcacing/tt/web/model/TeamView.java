package de.bausdorf.simcacing.tt.web.model;

import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamView {

    private String name;
    private String id;
    private String ownerId;
    private List<TeamDriver> authorizedDrivers;
    private boolean currentUserTeamAdmin;

    public TeamView(IRacingTeam team, DriverRepository driverRepository, String currentUserId) {
        this.id = team.getId();
        this.name = team.getName();
        this.ownerId = team.getOwnerId();
        this.authorizedDrivers = new ArrayList<>();
        this.currentUserTeamAdmin = isTeamAdmin(team, currentUserId);
        if( team.getAuthorizedDriverIds() != null ) {
            for (String authorizedId : team.getAuthorizedDriverIds()) {
                Optional<IRacingDriver> driver = driverRepository.findById(authorizedId);
                if (!driver.isPresent()) {
                    authorizedDrivers.add(TeamDriver.builder()
                            .id(authorizedId)
                            .name("N.N")
                            .validated(false)
                            .teamAdmin(isTeamAdmin(team, authorizedId))
                            .owner(team.getOwnerId().equals(authorizedId))
                            .build());
                } else {
                    authorizedDrivers.add(TeamDriver.builder()
                            .id(authorizedId)
                            .name(driver.get().getName())
                            .validated(driver.get().isValidated())
                            .teamAdmin(isTeamAdmin(team, authorizedId))
                            .owner(team.getOwnerId().equals(authorizedId))
                            .build());
                }
            }
        }
    }

    public IRacingTeam getTeam() {
        IRacingTeam team = IRacingTeam.builder()
                .id(id.trim())
                .name(name.trim())
                .ownerId(ownerId.trim())
                .authorizedDriverIds(new ArrayList<>())
                .teamAdminIds(new ArrayList<>())
                .build();

        if( authorizedDrivers != null ) {
            for (TeamDriver teamDriver : authorizedDrivers) {
                if (teamDriver.isTeamAdmin()) {
                    team.getTeamAdminIds().add(teamDriver.getId().trim());
                }
                team.getAuthorizedDriverIds().add(teamDriver.getId().trim());
            }
        }
        return team;
    }

    private boolean isTeamAdmin(IRacingTeam team, String authorizedId) {
        return (team.getTeamAdminIds() != null && team.getTeamAdminIds().contains(authorizedId))
                || team.getOwnerId().equals(authorizedId);
    }
}
