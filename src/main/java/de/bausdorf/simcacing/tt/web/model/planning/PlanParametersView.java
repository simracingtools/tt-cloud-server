package de.bausdorf.simcacing.tt.web.model.planning;

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

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.planning.PlanningTools;
import de.bausdorf.simcacing.tt.planning.ScheduleDriverOptionType;
import de.bausdorf.simcacing.tt.planning.persistence.PlanParameters;
import de.bausdorf.simcacing.tt.planning.persistence.Roster;
import de.bausdorf.simcacing.tt.planning.persistence.Stint;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanParametersView {
    private String id;
    private Long trackId;
    private Long carId;
    private Long teamId;
    private String planName;
    private Duration raceDuration;
    private LocalDateTime startTime;
    private LocalDate sessionStartDate;
    private LocalTime sessionStartTime;
    private LocalDateTime todStartTime;
    private LocalTime todTime;
    private LocalDate todDate;
    private Duration greenFlagOffsetTime;
    private Duration avgLapTime;
    private Duration avgPitLaneTime;
    private Double avgFuelPerLap;
    private Double maxCarFuel;
    private String timezone;
    private List<Stint> stints;
    private List<Long> allDriverIds;
    private List<IRacingDriver> allDrivers;
    private Roster roster;

    public ScheduleDriverOptionType getDriverStatusAt(String driverId, OffsetDateTime time) {
        if (roster != null ) {
            return PlanningTools.getDriverStatusAt(roster, driverId, time);
        }
        return ScheduleDriverOptionType.UNSCHEDULED;
    }

    public void updateEntity(PlanParameters entity) {
        ZoneId zoneId = ZoneId.of(timezone);
        LocalDateTime tod = updateTime(todStartTime, todDate, todTime);
        entity.setId(id != null ? id : entity.getId());
        entity.setTrackId(trackId != 0 ? trackId : entity.getTrackId());
        entity.setCarId(carId !=0 ? carId : entity.getCarId());
        entity.setTeamId(teamId != 0 ? teamId : entity.getTeamId());
        entity.setName(planName != null ? planName : entity.getName());

        LocalDateTime sessionTime = updateTime(startTime, sessionStartDate, sessionStartTime);
        OffsetDateTime newSessionStartDateTime = OffsetDateTime.of(sessionTime, zoneId.getRules().getOffset(sessionTime));
        if (!entity.getSessionStartDateTime().equals(newSessionStartDateTime)) {
            // change first driver availability entry
            entity.getRoster().getDriverAvailability().stream()
                    .filter(entry -> entry.getFromTime().equals(entity.getSessionStartDateTime()))
                    .forEach(entry -> entry.setFromTime(newSessionStartDateTime));
        }
        entity.setSessionStartDateTime(newSessionStartDateTime);

        entity.setTodStartTime(tod);
        entity.setGreenFlagOffsetTime(greenFlagOffsetTime != null ? greenFlagOffsetTime : entity.getGreenFlagOffsetTime());
        entity.setAvgLapTime(avgLapTime != null ? avgLapTime : entity.getAvgLapTime());
        entity.setAvgPitLaneTime(avgPitLaneTime != null ? avgPitLaneTime : entity.getAvgPitLaneTime());
        entity.setAvgFuelPerLap(avgFuelPerLap != null ? avgFuelPerLap : entity.getAvgFuelPerLap());
        entity.setMaxCarFuel(maxCarFuel != 0.0 ? maxCarFuel : entity.getMaxCarFuel());
    }

    public static PlanParametersView fromEntity(@NonNull PlanParameters parameters, @Nullable ZoneId timezone) {
        return PlanParametersView.builder()
                .id(parameters.getId())
                .planName(parameters.getName())
                .trackId(parameters.getTrackId())
                .carId(parameters.getCarId())
                .teamId(parameters.getTeamId())
                .raceDuration(parameters.getRaceDuration())
                .startTime(parameters.getSessionStartDateTime().toLocalDateTime())
                .sessionStartTime(parameters.getSessionStartDateTime().toLocalTime())
                .sessionStartDate(parameters.getSessionStartDateTime().toLocalDate())
                .todStartTime(parameters.getTodStartTime())
                .todTime(parameters.getTodStartTime().toLocalTime())
                .todDate(parameters.getTodStartTime().toLocalDate())
                .greenFlagOffsetTime(parameters.getGreenFlagOffsetTime())
                .avgLapTime(parameters.getAvgLapTime())
                .avgPitLaneTime(parameters.getAvgPitLaneTime())
                .avgFuelPerLap(parameters.getAvgFuelPerLap())
                .maxCarFuel(parameters.getMaxCarFuel())
                .timezone(timezone == null ? ZoneId.systemDefault().toString() : timezone.toString())
                .allDriverIds(parameters.getRoster().getDrivers().stream()
                        .map(IRacingDriver::getId)
                        .map(Long::parseLong)
                        .collect(Collectors.toList()))
                .allDrivers(parameters.getRoster().getDrivers())
                .roster(parameters.getRoster())
                .build();
    }

    private LocalDateTime updateTime(LocalDateTime localDateTime, LocalDate localDate, LocalTime localTime) {
        if (localDateTime == null) {
            localDateTime = LocalDateTime.of(localDate, localTime);
        }
        return localDateTime;
    }
}
