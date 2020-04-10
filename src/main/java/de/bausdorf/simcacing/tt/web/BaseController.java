package de.bausdorf.simcacing.tt.web;

import de.bausdorf.simcacing.tt.web.security.TtClientRegistrationRepository;
import de.bausdorf.simcacing.tt.web.security.TtUser;
import de.bausdorf.simcacing.tt.web.security.TtUserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.Optional;

public class BaseController {

    @Autowired
    TtClientRegistrationRepository userService;

    Messages messages;

    @ModelAttribute("user")
    TtUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<TtUser> details = userService.findById(auth.getName());
        return details.isPresent() ? details.get() : TtUser.builder()
                .name("Unknown")
                .userType(TtUserType.TT_NEW)
                .build();
    }

    @ModelAttribute("messages")
    Messages messages() {
        if( messages == null ) {
            messages = new Messages();
        }
        return messages;
    }

    protected void addMessage(Message msg) {
        messages().add(msg);
    }

    protected void addError(String error) {
        addMessage(Message.builder()
            .type(Message.ERROR)
            .text(error)
            .build());
    }

    protected void addWarning(String error) {
        addMessage(Message.builder()
                .type(Message.WARN)
                .text(error)
                .build());
    }

    protected void addInfo(String error) {
        addMessage(Message.builder()
                .type(Message.INFO)
                .text(error)
                .build());
    }

    protected void clearMessages() {
        messages().clear();
    }
}
