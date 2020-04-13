package de.bausdorf.simcacing.tt.web;

import de.bausdorf.simcacing.tt.planning.RacePlanRepository;
import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.stock.CarRepository;
import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.TeamRepository;
import de.bausdorf.simcacing.tt.stock.TrackRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingCar;
import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import de.bausdorf.simcacing.tt.stock.model.IRacingTrack;
import de.bausdorf.simcacing.tt.util.TimeTools;
import de.bausdorf.simcacing.tt.web.model.PlanParametersView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class RacePlanController extends BaseController {

    TrackRepository trackRepository;
    CarRepository carRepository;
    TeamRepository teamRepository;
    DriverRepository driverRepository;
    RacePlanRepository planRepository;

    public RacePlanController(@Autowired TrackRepository trackRepository,
                              @Autowired CarRepository carRepository,
                              @Autowired TeamRepository teamRepository,
                              @Autowired DriverRepository driverRepository,
                              @Autowired RacePlanRepository racePlanRepository) {
        this.trackRepository = trackRepository;
        this.carRepository = carRepository;
        this.teamRepository = teamRepository;
        this.driverRepository = driverRepository;
        this.planRepository = racePlanRepository;
    }

    @GetMapping("/newraceplan")
    public String viewNewRacePlan(Model model) {
        model.addAttribute("raceplan", new PlanParametersView());
        model.addAttribute("plans", planRepository.findByTeamIds(getMyTeams().stream()
                .map(s -> s.getId())
                .collect(Collectors.toList()))
        );
        return "newraceplan";
    }

    @PostMapping("/newraceplan")
    public String createNewRacePlan(@ModelAttribute PlanParametersView planView, Model model) {
        if( planView.getId() != null ) {
            model.addAttribute("selectedPlan", planRepository.findById(planView.getId()));
            return "planning";
        }
        String planId = UUID.randomUUID().toString();
        planRepository.save(RacePlanParameters.builder()
                .id(planId)
                .carId(planView.getCarId())
                .trackId(planView.getTrackId())
                .teamId(planView.getTeamId())
                .driverCount(3)
                .sessionStartTime(LocalTime.parse(planView.getStartTime(), DateTimeFormatter.ofPattern("HH:mm")))
                .raceDuration(TimeTools.durationFromPattern(planView.getRaceDuration(), "HH:mm"))
                .name(planView.getPlanName())
                .build());

        planView.setId(planId);
        model.addAttribute("selectedPlan", planView);

        return "planning";
    }

    @GetMapping("/planning")
    public String doPlanning(@ModelAttribute("selectedPlan") PlanParametersView planView, Model model) {
        if(planView == null) {
            return "newraceplan";
        }
        return "planning";
    }

    @ModelAttribute("teams")
    public List<IRacingTeam> getMyTeams() {
        List<IRacingTeam> teams = teamRepository.findByOwnerId(currentUser().getIRacingId());
        teams.addAll(teamRepository.findByAuthorizedDrivers(currentUser().getIRacingId()).stream()
                .filter(s -> !teams.contains(s)).collect(Collectors.toList())
        );
        return teams;
    }

    @ModelAttribute("tracks")
    List<IRacingTrack> getAllTracks() {
        return trackRepository.loadAll(true);
    }

    @ModelAttribute("cars")
    List<IRacingCar> getAllCars() {
        return carRepository.loadAll(true);
    }
}
