package de.bausdorf.simcacing.tt.schedule.model;

import org.springframework.cloud.gcp.data.firestore.FirestoreReactiveRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RaceEventRepository extends FirestoreReactiveRepository<RaceEvent> {

	Flux<RaceEvent> findRaceEventBySeries(String series);

	Mono<RaceEvent> findRaceEventBySeriesAndSessionDateAndSessionTime(String series, Date date, Time time);
}
