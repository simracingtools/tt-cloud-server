package de.bausdorf.simcacing.tt.schedule.model;

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
