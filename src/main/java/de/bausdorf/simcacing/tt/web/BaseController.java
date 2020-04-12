package de.bausdorf.simcacing.tt.web;

import de.bausdorf.simcacing.tt.web.security.TtClientRegistrationRepository;
import de.bausdorf.simcacing.tt.web.security.TtUser;
import de.bausdorf.simcacing.tt.web.security.TtUserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

public class BaseController {

    public static final String MESSAGES = "messages";

    @Autowired
    TtClientRegistrationRepository userService;

    @ModelAttribute("user")
    TtUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<TtUser> details = userService.findById(auth.getName());
        return details.isPresent() ? details.get() : TtUser.builder()
                .name("Unknown")
                .userType(TtUserType.TT_NEW)
                .build();
    }

    @ModelAttribute(MESSAGES)
    Messages messages() {
        return new Messages();
    }

    protected void addMessage(Message msg, Model model) {
        Messages messages = ((Messages)model.getAttribute(MESSAGES));
        if( messages == null ) {
            messages = messages();
            model.addAttribute(MESSAGES, messages);
        }
        messages.add(msg);
    }

    protected void addError(String error, Model model) {
        addMessage(Message.builder()
            .type(Message.ERROR)
            .text(error)
            .build(), model);
    }

    protected void addWarning(String error, Model model) {
        addMessage(Message.builder()
                .type(Message.WARN)
                .text(error)
                .build(), model);
    }

    protected void addInfo(String error, Model model) {
        addMessage(Message.builder()
                .type(Message.INFO)
                .text(error)
                .build(), model);
    }
}
