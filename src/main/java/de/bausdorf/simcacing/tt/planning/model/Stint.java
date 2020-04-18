package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class Stint {

	public static final String DRIVER_NAME = "driverName";
	public static final String TOD_START_TIME = "todStartTime";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";
	public static final String REFUEL_AMOUNT = "refuelAmount";
	public static final String LAPS = "laps";
	public static final String PITSTOP_SERVICE = "pitstopService";

	private String driverName;
	private LocalDateTime todStartTime;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private double refuelAmount;
	private int laps;
	private Optional<PitStop> pitStop;


	public Duration getStintDuration(boolean includePitStopTimes) {
		if( startTime != null && endTime != null ) {
			if( includePitStopTimes && pitStop.isPresent() ) {
				return Duration.between(startTime, endTime).minus(pitStop.get().getOverallDuration());
			}
			return Duration.between(startTime, endTime);
		}
		return Duration.ZERO;
	}

	public String getStintDurationString(boolean includePitStopTimes) {
		return TimeTools.shortDurationString(getStintDuration(includePitStopTimes));
	}

	public String getStartTimeString() {
		return getStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	public String getTodStartTimeString() {
		return getTodStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	public String getEndTimeString() {
		return getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();

		map.put(DRIVER_NAME, driverName);
		map.put(TOD_START_TIME, todStartTime.toString());
		map.put(START_TIME, startTime.toString());
		map.put(END_TIME, endTime.toString());
		map.put(REFUEL_AMOUNT, refuelAmount);
		map.put(LAPS, laps);
		map.put(PITSTOP_SERVICE, pitStop.isPresent()
				? pitStop.get().getService().stream().map(s -> s.name()).collect(Collectors.toList())
				: null
		);

		return map;
	}
}
