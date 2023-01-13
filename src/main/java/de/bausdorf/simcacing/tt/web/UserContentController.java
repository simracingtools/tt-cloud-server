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

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.web.model.UserProfileView;

import de.bausdorf.simcacing.tt.web.security.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserContentController extends BaseController {


    public static final String PROFILE_VIEW = "profile";
    public static final String ACKNEWUSER_VIEW = "acknewuser";

    @GetMapping("/acknewuser")
    @Secured({ "ROLE_TT_NEW"})
    public String showUserAck() {
        return ACKNEWUSER_VIEW;
    }

    @PostMapping("/acknewuser")
    @Secured({ "ROLE_TT_NEW"})
    public String showProfileAfterAck() {
        return PROFILE_VIEW;
    }

    @GetMapping("/profile")
    @Secured({ "ROLE_TT_REGISTERED", "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
    public String showUserProfile() {

        return PROFILE_VIEW;
    }

    @PostMapping("/profile")
    @Secured({ "ROLE_TT_REGISTERED", "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
    public String saveUserProfile(@ModelAttribute UserProfileView user, Model model) {
        saveUser(user, model);
        return PROFILE_VIEW;
    }

    @PostMapping("/saveuser")
    @Secured({ "ROLE_TT_NEW", "ROLE_TT_REGISTERED", "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
    public String saveUser(@ModelAttribute UserProfileView profileView, Model model) {
        if( profileView.getUserType().equalsIgnoreCase(TtUserType.TT_NEW.toText()) ) {
            profileView.setUserType(TtUserType.TT_REGISTERED.toText());
            WebSecurityConfig.updateCurrentUserRole(TtUserType.TT_REGISTERED);
        }

        if( profileView.getClientMessageAccessToken() == null ) {
            profileView.setClientMessageAccessToken(UUID.randomUUID().toString());
        }

        String newIRacingId = profileView.getIRacingId();
        if( newIRacingId.isEmpty() ) {
            addError("iRacing ID must not be empty !", model);
        } else if( !newIRacingId.equals(currentUser().getIracingId()) ) {
            // Changed iRacing id in profile form - generate a new token
            Optional<TtIdentity> existingUser = userService.findByIracingId(newIRacingId);
            if (existingUser.isEmpty()) {
                profileView.setClientMessageAccessToken(UUID.randomUUID().toString());
                TtIdentity userToSave = profileView.getUser(currentUser());
                userToSave.setCreated(OffsetDateTime.now());
                userService.save(userToSave);
                addInfo("iRacing ID changed in profile. A new token was generated - change your TeamTactics client config", model);
            } else {
                addError("iRacingId " + newIRacingId + " is already registered", model);
            }
        } else {
            // No change of iRacing id
            userService.save(profileView.getUser(currentUser()));
            addInfo("Profile successfully saved." , model);
            Optional<IRacingDriver> existingDriver = driverRepository.findById(newIRacingId);
            if( existingDriver.isPresent() && profileView.getDriverNick() != null && !profileView.getDriverNick().isEmpty()) {
                existingDriver.get().setName(profileView.getDriverNick());
                driverRepository.save(existingDriver.get());
                addInfo("Driver nick changed." , model);
            }
        }
        model.addAttribute("user", profileView);
        return ACKNEWUSER_VIEW;
    }

    @ModelAttribute("subscriptions")
    public List<SubscriptionType> allSubscriptions() {
        return Arrays.asList(SubscriptionType.values());
    }
}
