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

import java.util.List;

import org.springframework.cloud.gcp.data.firestore.Document;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Document(collectionName = "RaceEvents")
public class RaceEvent {
	@DocumentId
	private String eventId;
	private String name;
	private String series;
	private String season;
	private String trackId;
	private List<String> carIds;
	private Date simDate;
	private Time simTime;
	private Timestamp sessionTimestamp;
	private Date sessionDate;
	private Time sessionTime;
	private Time raceDuration;
	private TimeOffset raceSessionOffset;
}
