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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.stock.TeamRepository;
import de.bausdorf.simcacing.tt.stock.model.IRacingTeam;
import de.bausdorf.simcacing.tt.util.TeamtacticsServerProperties;
import de.bausdorf.simcacing.tt.web.model.UserProfileView;
import de.bausdorf.simcacing.tt.web.security.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.thymeleaf.util.StringUtils;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class BaseController {

    public static final String MESSAGES = "messages";

    @Autowired
    TtIdentityRepository userService;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    TeamtacticsServerProperties config;

    ObjectMapper mapper = new ObjectMapper();

    @ModelAttribute("user")
    public UserProfileView currentUserProfile() {
        return new UserProfileView(currentUser(), driverRepository);
    }

    @ModelAttribute("serverVersion")
    public String serverVersion() {
        return "TeamTactics server version: " + config.getVersion();
    }

    public List<IRacingTeam> getMyTeams(TeamRepository teamRepository) {
        List<IRacingTeam> teams = teamRepository.findByIdIsNotNull().stream()
                .filter(team -> team.getTeamAdminIds().contains(currentUser().getIracingId()))
                .collect(Collectors.toList());
        teams.addAll(teamRepository.findByAuthorizedDriverIdsContaining(currentUser().getIracingId()).stream()
                .filter(s -> !teams.contains(s)).collect(Collectors.toList())
        );
        return teams;
    }

    protected TtIdentity currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<TtIdentity> details = auth != null ? userService.findById(auth.getName()) : Optional.empty();
        return details.isPresent() ? details.get()
                : TtIdentity.builder()
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

    protected RedirectBuilder redirectBuilder(String viewName) {
        return new RedirectBuilder(viewName);
    }

    protected String messagesEncoded(@NonNull Model model) {
        try {
            Messages messages = ((Messages)model.getAttribute(MESSAGES));
            if(messages == null || messages.isEmpty()) {
                return null;
            }
            String messagesJson = mapper.writeValueAsString(messages.toArray());
            return Base64.getEncoder().encodeToString(messagesJson.getBytes());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    protected void decodeMessagesToModel(String messagesEncoded, Model model) {
        try {
            String messagesDecoded = new String(Base64.getDecoder().decode(messagesEncoded));
            Messages messages = mapper.readValue(messagesDecoded, Messages.class);
            model.addAttribute(MESSAGES, messages);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    public class RedirectBuilder {
        private String redirectUri = "";
        private int parameterCount = 0;

        public RedirectBuilder(String viewName) {
            redirectUri += (viewName.startsWith("/") ? "redirect:" : "redirect:/") + viewName;
        }

        public RedirectBuilder withParameter(String name, String value) {
            if(!StringUtils.isEmpty(value)) {
                redirectUri += (parameterCount == 0 ? "?" : "&") + name + "=" + value;
                parameterCount++;
            }
            return this;
        }

        public RedirectBuilder withParameter(String name, long value) {
            return withParameter(name, value == 0L ? null : Long.toString(value));
        }

        public String build(@Nullable Model model) {
            if(model == null) {
                return redirectUri;
            }
            String encodedMessages = messagesEncoded(model);
            if(encodedMessages != null) {
                redirectUri += (parameterCount == 0 ? "?" : "&") + MESSAGES + "=" + encodedMessages;
            }
            return redirectUri;
        }
    }
}
