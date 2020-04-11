package de.bausdorf.simcacing.tt.planning.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@Builder
public class RacePlanParameters {
    private String teamId;
    private String trackId;
    private Duration raceDuration;
    private ZonedDateTime sessionStartTime;
    private double carMaxFuel;
    private int driverCount;
}
