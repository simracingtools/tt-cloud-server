package de.bausdorf.simcacing.tt.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanParametersView {
    private String id;
    private String trackId;
    private String carId;
    private String teamId;
    private String planName;
    private String raceDuration;
    private String startTime;
}
