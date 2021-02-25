package de.bausdorf.simcacing.tt.web.model.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bausdorf.simcacing.tt.schedule.model.RaceEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaceSeasonView {
	private String name;
	private List<RaceEventView> events = new ArrayList<>();

	public static List<RaceSeasonView> fromEventList(List<RaceEvent> raceEvents) {
		Map<String, List<RaceEventView>> seasonViews = new HashMap<>();

		raceEvents.forEach(s -> {
			List<RaceEventView> seasonView = seasonViews.computeIfAbsent(s.getSeason(), k -> new ArrayList<>());
			seasonView.add(RaceEventView.fromRaceEvent(s));
		});

		List<RaceSeasonView> raceSeasonViews = new ArrayList<>();
		seasonViews.forEach((k, v) -> raceSeasonViews.add(RaceSeasonView.builder()
					.name(k)
					.events(v)
					.build())
		);
		return raceSeasonViews;
	}
}
