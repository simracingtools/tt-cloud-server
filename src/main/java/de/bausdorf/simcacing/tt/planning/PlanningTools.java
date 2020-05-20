package de.bausdorf.simcacing.tt.planning;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import de.bausdorf.simcacing.tt.planning.model.PitStop;
import de.bausdorf.simcacing.tt.planning.model.Stint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlanningTools {

	private PlanningTools() {
		super();
	}

	public static String driverNameAt(ZonedDateTime clock, List<Stint> stints) {
		log.debug("Get driver for clock: {}", clock.toString());
		Stint stint = stintAt(clock, stints);
		return stint != null ? stint.getDriverName() : "unassigned";
	}

	public static Optional<PitStop> pitstopAt(ZonedDateTime clock, List<Stint> stints) {
		log.debug("Get pitstop for clock: {}", clock.toString());
		Stint stint = stintAt(clock, stints);
		return stint != null ? stint.getPitStop() : Optional.of(PitStop.defaultPitStop());
	}

	public static Stint stintAt(ZonedDateTime clock, List<Stint> stints) {
		for (Stint stint : stints) {
			log.debug("Stint from {} until {}", stint.getStartTime().toString(), stint.getEndTime().toString());
			if (stint.getStartTime().isEqual(clock)
					|| (clock.isAfter(stint.getStartTime()) && clock.isBefore(stint.getEndTime()))) {
				return stint;
			}
		}
		return null;
	}
}
