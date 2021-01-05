package de.bausdorf.simcacing.tt.schedule;

import org.springframework.cloud.gcp.data.firestore.FirestoreReactiveRepository;

import de.bausdorf.simcacing.tt.schedule.model.RaceSeries;
import reactor.core.publisher.Flux;

public interface RaceSeriesRepository extends FirestoreReactiveRepository<RaceSeries> {

	Flux<RaceSeries> findRaceSeriesBySeason(String season);

}
