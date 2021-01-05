package de.bausdorf.simcacing.tt.schedule.model;

import java.util.List;

import org.springframework.cloud.gcp.data.firestore.Document;

import com.google.cloud.firestore.annotation.DocumentId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Document(collectionName = "RaceSeries")
public class RaceSeries {

	@DocumentId
	private String seriesId;

	private String name;
	private String season;
	private List<String> cars;
	private Date startDate;
	private List<TimeOffset> startTimes;
	private TimeOffset eventInterval;
	private TimeOffset raceSessionOffset;
}
