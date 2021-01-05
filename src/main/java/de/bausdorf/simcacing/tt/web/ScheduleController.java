package de.bausdorf.simcacing.tt.web;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.bausdorf.simcacing.tt.schedule.RaceSeriesRepository;
import de.bausdorf.simcacing.tt.schedule.RaceEventRepository;
import de.bausdorf.simcacing.tt.schedule.model.RaceSeries;
import de.bausdorf.simcacing.tt.stock.CarRepository;
import de.bausdorf.simcacing.tt.stock.TrackRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingCar;
import de.bausdorf.simcacing.tt.stock.model.IRacingTrack;
import de.bausdorf.simcacing.tt.web.model.schedule.RaceSeasonView;
import de.bausdorf.simcacing.tt.web.model.schedule.RaceSeriesView;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
public class ScheduleController extends BaseController {

	public static final String EVENTSCHEDULE_VIEW = "eventschedule";
	final TrackRepository trackRepository;
	final CarRepository carRepository;
	final RaceSeriesRepository seriesRepository;
	final RaceEventRepository eventRepository;

	public ScheduleController(@Autowired TrackRepository trackRepository,
			@Autowired CarRepository carRepository,
			@Autowired RaceSeriesRepository seriesRepository,
			@Autowired RaceEventRepository eventRepository) {
		this.carRepository = carRepository;
		this.eventRepository = eventRepository;
		this.trackRepository = trackRepository;
		this.seriesRepository = seriesRepository;
	}

	@GetMapping("/schedule")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String viewNewRacePlan(Model model) {

		Flux<RaceSeries> series = seriesRepository.findAll();
		List<RaceSeasonView> seriesViews = RaceSeasonView.fromSeriesList(
				series.collectList().blockOptional().orElse(Collections.emptyList()));

		model.addAttribute("raceSeries", seriesViews);
		model.addAttribute("newSeries", new RaceSeriesView());

		return EVENTSCHEDULE_VIEW;
	}

	@PostMapping("/addSeries")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String addSeries(@ModelAttribute RaceSeriesView newSeries, Model model) {
		return "redirect:" + EVENTSCHEDULE_VIEW;
	}

	@PostMapping("/saveSeries")
	@Secured({ "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
	public String saveSeries(@ModelAttribute RaceSeriesView selectedSeries, Model model) {
		return "redirect:" + EVENTSCHEDULE_VIEW;
	}

	@ModelAttribute("allCars")
	List<IRacingCar> getAllCars() {
		return carRepository.loadAll(true);
	}

	@ModelAttribute("allTracks")
	List<IRacingTrack> getAllTracks() {
		return trackRepository.loadAll(true);
	}
}
