package de.bausdorf.simcacing.tt.web;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.live.clientapi.SessionKey;
import de.bausdorf.simcacing.tt.live.impl.SessionController;
import de.bausdorf.simcacing.tt.live.impl.SessionHolder;
import de.bausdorf.simcacing.tt.live.model.client.SessionIdentifier;
import de.bausdorf.simcacing.tt.planning.RacePlanRepository;
import de.bausdorf.simcacing.tt.planning.model.RacePlan;
import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.stock.TeamRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import de.bausdorf.simcacing.tt.web.model.PlanDescriptionView;
import de.bausdorf.simcacing.tt.web.model.SessionIdentifierView;
import de.bausdorf.simcacing.tt.web.model.SessionView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IndexController extends BaseController {

    SessionHolder sessionHolder;

    TeamRepository teamRepository;
    RacePlanRepository planRepository;

    public IndexController(@Autowired SessionHolder holder,
            @Autowired TeamRepository teamRepository,
            @Autowired RacePlanRepository planRepository) {
        this.planRepository = planRepository;
        this.teamRepository = teamRepository;
        this.sessionHolder = holder;
    }

    @GetMapping({"/", "/index", "index.html"})
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/racing")
    public String goLive(@ModelAttribute("SessionView") SessionView sessionView, Model model) {
        if (sessionView != null) {
            SessionIdentifierView selectedView = availableSessionKeys()
                    .getSessions().get(sessionView.getSelectedSessionIndex());
            if (selectedView != null) {
                SessionKey sessionKey = SessionKey.builder()
                        .sessionId(SessionIdentifier.parse(selectedView.getSessionId()))
                        .teamId(selectedView.getTeamId())
                        .build();

                SessionController controller = sessionHolder.getSessionController(sessionKey);
                if (controller != null) {
                    model.addAttribute("sessionData", selectedView);
                    if (sessionView.getSelectedPlanId() != null) {
                        Optional<RacePlanParameters> planParameters = planRepository.findById(sessionView.getSelectedPlanId());
                        if (planParameters.isPresent()) {
                            controller.setRacePlan(RacePlan.createRacePlanTemplate(planParameters.get()));
                        }
                    }
                    return "racing";
                }
            } else {
                addWarning("No session selected", model);
            }
        }
        return "index";
    }

    @ModelAttribute("sessionView")
    public SessionView availableSessionKeys() {
        SessionView sessionView = new SessionView();
        for (SessionKey sessionKey : sessionHolder.getAvailableSessions()) {
            sessionView.getSessions().add(
                    new SessionIdentifierView(sessionKey.getTeamId(), sessionKey.getSessionId())
            );
        }
        return sessionView;
    }

    @ModelAttribute("plans")
    public List<PlanDescriptionView> racePlans() {
        List<IRacingTeam> teams = teamRepository.findByOwnerId(currentUser().getIRacingId());
        teams.addAll(teamRepository.findByAuthorizedDrivers(currentUser().getIRacingId()).stream()
                .filter(s -> !teams.contains(s)).collect(Collectors.toList())
        );
        return planRepository.findByTeamIds(teams.stream()
                .map(IRacingTeam::getId)
                .collect(Collectors.toList())).stream()
                .map(s -> PlanDescriptionView.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .team(teamRepository.findById(s.getTeamId()).orElse(IRacingTeam.builder()
                                .name("Team not found")
                                .build()).getName()
                        )
                        .build()
                )
                .collect(Collectors.toList());
    }
}
