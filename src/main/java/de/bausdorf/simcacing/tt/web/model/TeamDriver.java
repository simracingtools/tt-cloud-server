package de.bausdorf.simcacing.tt.web.model;

import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDriver {
    private String name;
    private String id;
    private boolean validated;
    private boolean teamAdmin;
    private boolean owner;

    public TeamDriver(IRacingDriver iRacingDriver) {
        this.name = iRacingDriver.getName();
        this.id = iRacingDriver.getId();
        this.validated = iRacingDriver.isValidated();
        this.teamAdmin = false;
        this.owner = false;
    }
}
