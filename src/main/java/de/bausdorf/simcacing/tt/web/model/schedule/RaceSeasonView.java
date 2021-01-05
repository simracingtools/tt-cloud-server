package de.bausdorf.simcacing.tt.web.model.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.schedule.model.RaceSeries;
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
	private List<RaceSeriesView> series = new ArrayList<>();

	public static List<RaceSeasonView> fromSeriesList(List<RaceSeries> series) {
		Map<String, RaceSeasonView> seasonViews = new HashMap<>();

		series.forEach(s -> {
			RaceSeasonView seasonView = seasonViews.get(s.getSeason());
			if(seasonView == null) {
				seasonView = new RaceSeasonView();
				seasonView.setName(s.getSeason());
				seasonViews.put(seasonView.getName(), seasonView);
			}
			seasonView.getSeries().add(RaceSeriesView.fromRaceSeries(s));
		});
		return seasonViews.values().stream()
				.sorted()
				.collect(Collectors.toList());
	}
}
