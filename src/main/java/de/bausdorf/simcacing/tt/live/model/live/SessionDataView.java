package de.bausdorf.simcacing.tt.live.model.live;

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

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import de.bausdorf.simcacing.tt.live.clientapi.ClientData;
import de.bausdorf.simcacing.tt.live.impl.SessionController;
import de.bausdorf.simcacing.tt.live.model.client.ChartData;
import de.bausdorf.simcacing.tt.live.model.client.TrackLocationType;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDataView implements ClientData {
	private String sessionId;
	private String teamName;
	private String carName;
	private String sessionDuration;
	private String maxCarFuel;
	private String trackName;
	private String sessionType;
	private LapDataView lastLapData;
	private String trackLocation;
	private String trackLocationCssClass;
	private List<PitstopDataView> pitStops;
	private String timeZone;
	private ChartData chartData;
	private String slowestLap;
	private String fastestLap;

	public static SessionDataView getSessionDataView(SessionController controller) {
		TrackLocationType locationType = controller.getCurrentTrackLocation() == null
				? TrackLocationType.OFF_WORLD : controller.getCurrentTrackLocation();
		return SessionDataView.builder()
				.carName(controller.getSessionData().getCarName())
				.trackName(controller.getSessionData().getTrackName())
				.sessionDuration(controller.getSessionData().getSessionDuration().orElse(LocalTime.MIN)
						.format(DateTimeFormatter.ofPattern("HH:mm")))
				.sessionType(controller.getSessionData().getSessionType())
				.teamName(controller.getSessionData().getTeamName())
				.sessionId(controller.getSessionData().getSessionId().getSubscriptionId())
				.maxCarFuel(String.format(Locale.US,"%.1f", controller.getMaxCarFuel()))
				.lastLapData(LapDataView.getLapDataView(controller.getLastRecordedLap().orElse(null), controller))
				.trackLocation(locationType.name())
				.trackLocationCssClass(locationType.getCssClass())
				.pitStops(PitstopDataView.getPitstopDataView(controller))
				.timeZone("GMT" + ZonedDateTime.now().getOffset().getId())
				.chartData(controller.getChartData())
				.slowestLap(TimeTools.longDurationString(controller.getSlowestLap()))
				.fastestLap(TimeTools.longDurationString(controller.getFastestLap()))
				.build();
	}
}
