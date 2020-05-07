package de.bausdorf.simcacing.tt.live.model.client;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ChartData {
	private List<Integer> lapNos;
	private List<String> temps;
	private List<String> laps;

	public ChartData() {
		this.lapNos = new ArrayList<>();
		this.laps = new ArrayList<>();
		this.temps = new ArrayList<>();
	}

	public void addLap(LapData lap) {
		lapNos.add(lap.getNo());
		temps.add(String.format(Locale.US,"%.1f", lap.getTrackTemp()));
		laps.add(lap.getLapTime().getSeconds() > Duration.ZERO.getSeconds()
				? TimeTools.longDurationString(lap.getLapTime()) : "");
	}
}
