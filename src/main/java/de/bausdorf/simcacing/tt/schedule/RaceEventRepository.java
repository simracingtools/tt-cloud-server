package de.bausdorf.simcacing.tt.schedule;

import org.springframework.cloud.gcp.data.firestore.FirestoreReactiveRepository;

import com.google.cloud.Timestamp;

import de.bausdorf.simcacing.tt.schedule.model.RaceEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RaceEventRepository extends FirestoreReactiveRepository<RaceEvent> {

	Flux<RaceEvent> findAllBySessionTimestampGreaterThanEqual(Timestamp date);
	Flux<RaceEvent> findAllBySeriesAndSessionTimestampGreaterThanEqual(String series, Timestamp date);

	Mono<RaceEvent> findRaceEventBySeriesAndSeasonAndName(String series, String season, String name);

}
