package de.bausdorf.simcacing.tt.schedule.model;

import java.time.Duration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TimeOffset {
	private String offset;

	public TimeOffset(Duration duration) {
		super();
		offset = duration.toString();
	}
}
