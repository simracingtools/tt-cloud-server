package de.bausdorf.simcacing.tt.web.model.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CalendarEvent {
	private String id;
	private String title;
	private String start;
	private String end;
	private String description;
}
