package de.bausdorf.simcacing.tt.web;

import de.bausdorf.simcacing.tt.impl.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class IndexController {

    SessionHolder sessionHolder;

    public IndexController(@Autowired SessionHolder holder) {
        this.sessionHolder = holder;
    }

    @GetMapping({"/", "/index", "index.html"})
    public String index(Model model, Principal user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
}
