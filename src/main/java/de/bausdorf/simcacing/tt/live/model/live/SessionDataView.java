package de.bausdorf.simcacing.tt.live.model.live;

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
