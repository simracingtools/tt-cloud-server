package de.bausdorf.simcacing.tt.web.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import de.bausdorf.simcacing.tt.planning.model.ScheduleDriverOptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleView {
	private LocalDateTime validFrom;
	private ScheduleDriverOptionType status;

	public LocalDate getValidFromDate() {
		return validFrom.toLocalDate();
	}

	public void setValidFromDate(LocalDate date) {
		if (validFrom == null) {
			validFrom = LocalDateTime.of(date, LocalTime.MIN);
		} else {
			validFrom = LocalDateTime.of(date, validFrom.toLocalTime());
		}
	}

	public LocalTime getValidFromTime() {
		return validFrom.toLocalTime();
	}

	public void setValidFromTime(LocalTime time) {
		if (validFrom == null) {
			validFrom = LocalDateTime.of(LocalDate.MIN, time);
		} else {
			validFrom = LocalDateTime.of(validFrom.toLocalDate(), time);
		}
	}
}