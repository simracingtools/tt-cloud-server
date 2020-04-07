package de.bausdorf.simcacing.tt.web;

import de.bausdorf.simcacing.tt.live.impl.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

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

    @ModelAttribute("sessions")
    public Set<SessionHolder.SessionKey> availableSessionKeys() {
//        ArrayList<String> keys = new ArrayList<>();
//        for (SessionHolder.SessionKey key : sessionHolder.getAvailableSessions() ) {
//            keys.add(key.toString());
//        }
//        if( keys.isEmpty() ) {
//            keys.add("No session available");
//        }
        return sessionHolder.getAvailableSessions();
    }

    @GetMapping("/newuser")
    public String showHeaderPart() {
        return "newuser";
    }

    @GetMapping("/showFooterPart")
    public String showFooterPart() {
        return "footer-part";
    }
}
