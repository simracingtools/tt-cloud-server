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
public class RaceSeriesView {
	private String name;
	private List<RaceSeasonView> seasons = new ArrayList<>();

	public static List<RaceSeriesView> fromEventList(List<RaceEvent> raceEvents) {
		Map<String, List<RaceEvent>> seasonViews = new HashMap<>();

		raceEvents.forEach(s -> {
			List<RaceEvent> eventList = seasonViews.computeIfAbsent(s.getSeries(), k -> new ArrayList<>());
			eventList.add(s);
		});

		List<RaceSeriesView> seriesViews = new ArrayList<>();
		seasonViews.forEach((key, value) -> seriesViews.add(RaceSeriesView.builder()
				.name(key)
				.seasons(RaceSeasonView.fromEventList(value))
				.build()));
		return seriesViews;
	}
}
