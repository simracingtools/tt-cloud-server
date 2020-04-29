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
import org.springframework.web.bind.annotation.RequestParam;

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
    public String index() {
        return "index";
    }

    @GetMapping("/racing")
    public String getLive(@RequestParam("subscriptionId") Optional<String> subscriptionId,
            @RequestParam Optional<String> planId, Model model) {
        if (!subscriptionId.isPresent()) {
            addError("No racing session selected", model);
            return "index";
        }
        SessionIdentifierView selectedView = getSelectedSession(subscriptionId.get());
        if (selectedView != null) {
            if (prepareModel(selectedView, planId.orElse(null), model)) {
                return "racing";
            }
        } else {
            addWarning("No session selected", model);
        }
        return "index";
    }

    @PostMapping("/racing")
    public String postLive(@ModelAttribute("SessionView") SessionView sessionView, Model model) {
        if (sessionView != null) {
            return "redirect:racing?subscriptionId=" + sessionView.getSelectedSession()
                    + "&planId=" + sessionView.getSelectedPlanId();
//            SessionIdentifierView selectedView = getSelectedSession(sessionView.getSelectedSession());
//            if (selectedView != null) {
//                if (prepareModel(selectedView, sessionView.getSelectedPlanId(), model)) {
//                    return "redirect:racing?subscriptionId=" + sessionView.getSelectedSession()
//                            + "&planId=" + sessionView.getSelectedPlanId();
//                }
//            } else {
//                addWarning("No session selected", model);
//            }
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

    private boolean prepareModel(SessionIdentifierView selectedView, String selectedPlanId, Model model) {
        SessionKey sessionKey = SessionKey.builder()
                .sessionId(SessionIdentifier.parse(selectedView.getSessionKey()))
                .teamId(selectedView.getTeamId())
                .build();

        SessionController controller = sessionHolder.getSessionController(sessionKey);
        if (controller != null) {
            if (selectedPlanId != null) {
                Optional<RacePlanParameters> planParameters = planRepository.findById(selectedPlanId);
                planParameters.ifPresent(racePlanParameters -> controller.setRacePlan(RacePlan.createRacePlanTemplate(racePlanParameters)));
            }
            model.addAttribute("sessionData", selectedView);
            return true;
        }
        return false;
    }

    private SessionIdentifierView getSelectedSession(String subscriptionId) {
        if (subscriptionId == null) {
            return null;
        }
        for (SessionKey sessionKey : sessionHolder.getAvailableSessions()) {
            if (sessionKey.getSessionId().getSubscriptionId().equalsIgnoreCase(subscriptionId)) {
                return new SessionIdentifierView(sessionKey.getTeamId(), sessionKey.getSessionId());
            }
        }
        return null;
    }
}
