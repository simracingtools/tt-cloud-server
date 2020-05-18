package de.bausdorf.simcacing.tt.stock.model;

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

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import de.bausdorf.simcacing.tt.live.model.client.Pitstop;
import de.bausdorf.simcacing.tt.util.MapTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class StatsEntry {
	public static final String TOD_START = "todStart";
	public static final String TOD_END = "todEnd";
	public static final String STINT_LAPS = "stintLaps";
	public static final String AVG_LAP_TIME = "avgLapTime";
	public static final String AVG_FUEL_PER_LAP = "avgFuelPerLap";
	public static final String AVG_TRACK_TEMP = "avgTrackTemp";
	public static final String PITSTOP = "pitstop";

	private LocalTime todStart;
	private LocalTime todEnd;
	private int stintLaps;
	private Duration avgLapTime;
	private double avgFuelPerLap;
	private double avgTrackTemp;
	private Pitstop pitstop;

	public StatsEntry(Map<String, Object> data) {
		this.todStart = MapTools.timeFromMap(TOD_START, data);
		this.todEnd = MapTools.timeFromMap(TOD_END, data);
		this.stintLaps = MapTools.intFromMap(STINT_LAPS, data);
		this.avgLapTime = MapTools.durationFromMap(AVG_LAP_TIME, data);
		this.avgFuelPerLap = MapTools.doubleFromMap(AVG_FUEL_PER_LAP, data);
		this.avgTrackTemp = MapTools.doubleFromMap(AVG_TRACK_TEMP, data);
		Map<String, Object> pitstopData = MapTools.mapFromMap(PITSTOP, data);
		this.pitstop = pitstopData.isEmpty() ? null : new Pitstop(pitstopData);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> data = new HashMap<>();
		data.put(TOD_START, todStart != null ? todStart.toString() : null);
		data.put(TOD_END, todEnd != null ? todEnd.toString() : null);
		data.put(STINT_LAPS, stintLaps);
		data.put(AVG_LAP_TIME, avgLapTime.toString());
		data.put(AVG_FUEL_PER_LAP, avgFuelPerLap);
		data.put(AVG_TRACK_TEMP, avgTrackTemp);
		data.put(PITSTOP, pitstop != null ? pitstop.toMap() : null);
		return data;
	}

	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof StatsEntry)) {
			return false;
		}
		final StatsEntry other = (StatsEntry) o;
		if (!other.canEqual(this)) {
			return false;
		}
		final Object this$todStart = this.getTodStart();
		final Object other$todStart = other.getTodStart();
		if (this$todStart == null ? other$todStart != null : !this$todStart.equals(other$todStart)) {
			return false;
		}
		final Object this$todEnd = this.getTodEnd();
		final Object other$todEnd = other.getTodEnd();
		if (this$todEnd == null ? other$todEnd != null : !this$todEnd.equals(other$todEnd)) {
			return false;
		}
		if (this.getStintLaps() != other.getStintLaps()) {
			return false;
		}
		final Object this$avgLapTime = this.getAvgLapTime();
		final Object other$avgLapTime = other.getAvgLapTime();
		if (this$avgLapTime == null ? other$avgLapTime != null : !this$avgLapTime.equals(other$avgLapTime)) {
			return false;
		}
		if (Double.compare(this.getAvgFuelPerLap(), other.getAvgFuelPerLap()) != 0) {
			return false;
		}
		return Double.compare(this.getAvgTrackTemp(), other.getAvgTrackTemp()) == 0;
	}

	protected boolean canEqual(final Object other) {
		return other instanceof StatsEntry;
	}

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $todStart = this.getTodStart();
		result = result * PRIME + ($todStart == null ? 43 : $todStart.hashCode());
		final Object $todEnd = this.getTodEnd();
		result = result * PRIME + ($todEnd == null ? 43 : $todEnd.hashCode());
		result = result * PRIME + this.getStintLaps();
		final Object $avgLapTime = this.getAvgLapTime();
		result = result * PRIME + ($avgLapTime == null ? 43 : $avgLapTime.hashCode());
		final long $avgFuelPerLap = Double.doubleToLongBits(this.getAvgFuelPerLap());
		result = result * PRIME + (int) ($avgFuelPerLap >>> 32 ^ $avgFuelPerLap);
		final long $avgTrackTemp = Double.doubleToLongBits(this.getAvgTrackTemp());
		result = result * PRIME + (int) ($avgTrackTemp >>> 32 ^ $avgTrackTemp);
		return result;
	}
}
