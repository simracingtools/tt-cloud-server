package de.bausdorf.simcacing.tt.web;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.web.model.UserProfileView;
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

    @Autowired
    DriverRepository driverRepository;

    @ModelAttribute("user")
    public UserProfileView currentUserProfile() {
        return new UserProfileView(currentUser(), driverRepository);
    }

    protected TtUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<TtUser> details = userService.findById(auth.getName());
        return details.isPresent() ? details.get()
                : TtUser.builder()
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

    protected void addWarning(String warning, Model model) {
        addMessage(Message.builder()
                .type(Message.WARN)
                .text(warning)
                .build(), model);
    }

    protected void addInfo(String info, Model model) {
        addMessage(Message.builder()
                .type(Message.INFO)
                .text(info)
                .build(), model);
    }
}
