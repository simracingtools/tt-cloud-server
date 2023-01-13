package de.bausdorf.simcacing.tt.schedule.model;

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

import java.time.*;
import java.util.List;

import de.bausdorf.simcacing.tt.util.OffsetDateTimeConverter;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
public class RaceEvent {
	@Id
	@GeneratedValue
	private Long eventId;

	private String name;
	private String series;
	private String season;
	private String trackId;

	@ElementCollection
	private List<String> carIds;
	private LocalDateTime simDateTime;
	@Convert(converter = OffsetDateTimeConverter.class)
	private OffsetDateTime sessionDateTime;
	private Duration raceDuration;
	private String raceSessionOffset;
}
