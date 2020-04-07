package de.bausdorf.simcacing.tt.stock;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import de.bausdorf.simcacing.tt.stock.model.IRacingCar;
import de.bausdorf.simcacing.tt.stock.model.IRacingTrack;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class StockRepositoryTest {

	@Autowired
	CarRepository carRepository;

	@Autowired
	TrackRepository trackRepository;

	@Test
	@Disabled
	public void loadCars() {
		List<IRacingCar> cars = loadObjectList(IRacingCar.class, "carIds.csv");
		for( IRacingCar o : cars) {
			carRepository.save(o);
		}
	}

	@Test
	@Disabled
	public void loadTracks() {
		List<IRacingTrack> cars = loadObjectList(IRacingTrack.class, "allTracksDetails.csv");
		for( IRacingTrack o : cars) {
			if( o.getName().indexOf('/') > 0 ) {
				o.setName(o.getName().replaceAll("/", " "));
			}
			log.info("Try to save: {}", o);
			trackRepository.save(o);
		}
	}

	public <T> List<T> loadObjectList(Class<T> type, String fileName) {
		try {
			CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
			CsvMapper mapper = new CsvMapper();
			File file = new ClassPathResource(fileName).getFile();
			MappingIterator<T> readValues =
					mapper.reader(type).with(bootstrapSchema).readValues(file);
			return readValues.readAll();
		} catch (Exception e) {
			log.error("Error occurred while loading object list from file " + fileName, e);
			return Collections.emptyList();
		}
	}
}