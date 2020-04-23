package de.bausdorf.simcacing.tt.web.model;

import java.time.Duration;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Duration raceDuration;
    private LocalDateTime startTime;
    private LocalDateTime todStartTime;
}
