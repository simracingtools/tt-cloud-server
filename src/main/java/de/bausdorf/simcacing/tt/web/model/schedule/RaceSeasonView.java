package de.bausdorf.simcacing.tt.web.model.schedule;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 - 2021 bausdorf engineering
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
public class RaceSeasonView {
	private String name;
	private List<RaceEventView> events = new ArrayList<>();

	public static List<RaceSeasonView> fromEventList(List<RaceEvent> raceEvents) {
		Map<String, List<RaceEventView>> seasonViews = new HashMap<>();

		raceEvents.forEach(s -> {
			List<RaceEventView> seasonView = seasonViews.computeIfAbsent(s.getSeason(), k -> new ArrayList<>());
			seasonView.add(RaceEventView.fromRaceEvent(s));
		});

		List<RaceSeasonView> raceSeasonViews = new ArrayList<>();
		seasonViews.forEach((k, v) -> raceSeasonViews.add(RaceSeasonView.builder()
					.name(k)
					.events(v)
					.build())
		);
		return raceSeasonViews;
	}
}
