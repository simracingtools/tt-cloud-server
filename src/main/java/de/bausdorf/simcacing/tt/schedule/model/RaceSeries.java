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

	public static final String NAME = "name";
	public static final String SEASON = "season";
	public static final String CAR_CLASSES = "carClasses";
	public static final String START_DATE = "startDate";
	public static final String START_TIMES = "startTimes";
	public static final String EVENT_INTERVAL = "eventInterval";
	public static final String RACE_SESSION_OFFSET = "raceSessionOffset";

	@DocumentId
	private String name;

	private String season;
	private List<String> cars;
	private Date startDate;
	private List<TimeOffset> startTimes;
	private TimeOffset eventInterval;
	private TimeOffset raceSessionOffset;
}
