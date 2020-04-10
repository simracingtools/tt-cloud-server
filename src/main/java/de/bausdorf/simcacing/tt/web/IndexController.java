package de.bausdorf.simcacing.tt.web;

import de.bausdorf.simcacing.tt.live.impl.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.thymeleaf.spring5.view.ThymeleafView;

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

    @GetMapping({"/main"})
    public String mainContent(Model model) {
        return "main";
    }

    @ModelAttribute("sessions")
    public Set<SessionHolder.SessionKey> availableSessionKeys() {
        return sessionHolder.getAvailableSessions();
    }

}
