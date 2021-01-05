package de.bausdorf.simcacing.tt.schedule;

import org.springframework.cloud.gcp.data.firestore.FirestoreReactiveRepository;

import de.bausdorf.simcacing.tt.schedule.model.Date;
import de.bausdorf.simcacing.tt.schedule.model.RaceEvent;
import de.bausdorf.simcacing.tt.schedule.model.Time;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RaceEventRepository extends FirestoreReactiveRepository<RaceEvent> {

	Flux<RaceEvent> findRaceEventBySeries(String series);

	Mono<RaceEvent> findRaceEventBySeriesAndSessionDateAndSessionTime(String series, Date date, Time time);
}
