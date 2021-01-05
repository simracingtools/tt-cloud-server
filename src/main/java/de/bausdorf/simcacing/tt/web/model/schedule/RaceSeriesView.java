package de.bausdorf.simcacing.tt.web.model.schedule;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.bausdorf.simcacing.tt.schedule.ScheduleTools;
import de.bausdorf.simcacing.tt.schedule.model.RaceSeries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaceSeriesView {
	private String seriesId;
	private String name;
	private String season;
	private List<String> cars = new ArrayList<>();
	private LocalDate startDate;
	private String startTimes;
	private long eventIntervalDays;
	private Duration raceSessionOffset;

	public static RaceSeriesView fromRaceSeries(RaceSeries series) {
		return RaceSeriesView.builder()
				.seriesId(series.getSeriesId())
				.cars(series.getCars())
				.name(series.getName())
				.season(series.getSeason())
				.raceSessionOffset(ScheduleTools.durationFromTimeOffset(series.getRaceSessionOffset()))
				.eventIntervalDays(ScheduleTools.daysFromTimeOffset(series.getEventInterval()))
				.startDate(ScheduleTools.localDateFromDate(series.getStartDate()))
				.startTimes(ScheduleTools.startTimesString(series.getStartTimes()))
				.build();

	}
}
