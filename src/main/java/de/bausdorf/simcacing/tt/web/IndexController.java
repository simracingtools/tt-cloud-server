package de.bausdorf.simcacing.tt.web;

import de.bausdorf.simcacing.tt.live.clientapi.SessionKey;
import de.bausdorf.simcacing.tt.live.impl.SessionHolder;
import de.bausdorf.simcacing.tt.live.model.SessionIdentifier;
import de.bausdorf.simcacing.tt.web.model.SessionIdentifierView;
import de.bausdorf.simcacing.tt.web.model.SessionView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class IndexController extends BaseController {

    SessionHolder sessionHolder;

    public IndexController(@Autowired SessionHolder holder) {
        this.sessionHolder = holder;
    }

    @GetMapping({"/", "/index", "index.html"})
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/live")
    public String goLive(@ModelAttribute("SessionView") SessionView sessionView, Model model) {
        if (sessionView != null) {
            SessionIdentifierView selectedView = sessionView.getSelectedSession().orElse(null);
            if (selectedView != null) {
                SessionKey sessionKey = SessionKey.builder()
                        .sessionId(SessionIdentifier.parse(selectedView.getSessionId()))
                        .teamId(selectedView.getTeamId())
                        .build();
                sessionHolder.getSessionController(sessionKey);
                //TODO: Go to live session
            }
        }
        return "index";
    }

//    @GetMapping({"/main"})
//    public String mainContent(Model model) {
//        return "main";
//    }

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

}
