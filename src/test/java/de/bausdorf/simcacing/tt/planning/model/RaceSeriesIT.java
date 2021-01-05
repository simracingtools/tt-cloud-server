package de.bausdorf.simcacing.tt.planning.model;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bausdorf.simcacing.tt.schedule.RaceSeriesRepository;
import de.bausdorf.simcacing.tt.schedule.ScheduleTools;
import de.bausdorf.simcacing.tt.schedule.model.Date;
import de.bausdorf.simcacing.tt.schedule.model.RaceEvent;
import de.bausdorf.simcacing.tt.schedule.RaceEventRepository;
import de.bausdorf.simcacing.tt.schedule.model.RaceSeries;
import de.bausdorf.simcacing.tt.schedule.model.TimeOffset;
import de.bausdorf.simcacing.tt.util.AbstractIntegrationTest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class RaceSeriesIT extends AbstractIntegrationTest {

	@Autowired
	RaceSeriesRepository seriesRepository;

	@Autowired
	RaceEventRepository eventRepository;

	@Test
	void seriesCrudTest() {
		RaceSeries series = createRaceSeries();

		seriesRepository.save(series).block();

		RaceSeries foundSeries = seriesRepository.findRaceSeriesBySeason("2021/1").blockFirst();

		seriesRepository.delete(foundSeries).block();
	}

	@Test
	void createSeriesEventTest() {
		RaceSeries series = createRaceSeries();

		List<RaceEvent> eventList = ScheduleTools.generateSeriesEvents(series, 6);

		eventRepository.saveAll(eventList).blockLast();

		eventList.forEach(s -> log.info(s.toString()));
	}

	private RaceSeries createRaceSeries() {
		return RaceSeries.builder()
				.seriesId("2021-1#iEEC")
				.name("iEEC")
				.season("2021-1")
				.cars(Arrays.asList("1", "2", "3"))
				.raceSessionOffset(new TimeOffset(Duration.ofMinutes(13)))
				.eventInterval(new TimeOffset(Duration.ofDays(14)))
				.startDate(new Date(ZonedDateTime.now().toLocalDate()))
				.startTimes(Arrays.asList(
						new TimeOffset(Duration.ofHours(8)),
						new TimeOffset(Duration.ofHours(17)),
						new TimeOffset(Duration.ofHours(14).plusDays(1)))
				)
				.build();
	}
}
