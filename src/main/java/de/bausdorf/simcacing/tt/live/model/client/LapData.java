package de.bausdorf.simcacing.tt.live.model.client;

import de.bausdorf.simcacing.tt.live.clientapi.ClientData;
import de.bausdorf.simcacing.tt.util.MapTools;
import lombok.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class LapData implements ClientData {

	public static final String LAP_NO = "no";
	public static final String DRIVER = "driver";
	public static final String DRIVER_ID = "driverId";
	public static final String FUEL_LEVEL = "fuelLevel";
	public static final String LAP_TIME = "lapTime";
	public static final String TRACK_TEMP = "trackTemp";
	public static final String SESSION_TIME = "sessionTime";
	public static final String PIT_LAP = "pitLap";
	public static final String UNCLEAN = "unclean";
	public static final String LAST_LAP_FUEL_USAGE = "lastLapFuelUsage";
	public static final String STINT = "stint";
	public static final String STINT_LAP = "stintLap";
	private final int no;
	private final String driver;
	private final String driverId;
	private final double fuelLevel;
	private final Duration lapTime;
	private final double trackTemp;
	private final LocalTime sessionTime;
	@Setter
	private boolean pitStop;
	@Setter
	private boolean unclean;
	@Setter
	private double lastLapFuelUsage;
	@Setter
	private int stint;
	@Setter
	private int stintLap;

	public LapData(Map<String, Object> data) {
		this.no = MapTools.intFromMap(LAP_NO, data);
		this.driver = MapTools.stringFromMap(DRIVER, data);
		this.driverId = MapTools.stringFromMap(DRIVER_ID, data);
		this.fuelLevel = MapTools.doubleFromMap(FUEL_LEVEL, data);
		this.lapTime = MapTools.durationFromMap(LAP_TIME, data);
		this.trackTemp = MapTools.doubleFromMap(TRACK_TEMP, data);
		this.sessionTime = MapTools.timeFromMap(SESSION_TIME, data);
		this.pitStop = MapTools.booleanFromMap(PIT_LAP, data, false);
		this.unclean = MapTools.booleanFromMap(UNCLEAN, data, false);
		this.lastLapFuelUsage = MapTools.doubleFromMap(LAST_LAP_FUEL_USAGE, data);
		this.stint = MapTools.intFromMap(STINT, data);
		this.stintLap = MapTools.intFromMap(STINT_LAP, data);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> data = new HashMap<>();

		data.put(LAP_NO, no);
		data.put(DRIVER, driver);
		data.put(DRIVER_ID, driverId);
		data.put(FUEL_LEVEL, fuelLevel);
		data.put(LAP_TIME, lapTime.toString());
		data.put(TRACK_TEMP, trackTemp);
		data.put(SESSION_TIME, sessionTime.toString());
		data.put(PIT_LAP, pitStop);
		data.put(UNCLEAN, unclean);
		data.put(LAST_LAP_FUEL_USAGE, lastLapFuelUsage);
		data.put(STINT, stint);
		data.put(STINT_LAP, stintLap);
		return data;
	}
}
