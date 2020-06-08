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

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.web.model.UserProfileView;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.bausdorf.simcacing.tt.web.security.TtUser;
import de.bausdorf.simcacing.tt.web.security.TtUserType;

@Controller
public class UserContentController extends BaseController {


    public static final String PROFILE_VIEW = "profile";

    @GetMapping("/acknewuser")
    @Secured({ "ROLE_TT_NEW"})
    public String showUserAck() {
        return "acknewuser";
    }

    @PostMapping("/acknewuser")
    @Secured({ "ROLE_TT_NEW"})
    public String showProfileAfterAck() {
        return PROFILE_VIEW;
    }

    @GetMapping("/profile")
    @Secured({ "ROLE_TT_REGISTERED", "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
    public String showUserProfile(Principal principal) {

        return PROFILE_VIEW;
    }

    @PostMapping("/profile")
    @Secured({ "ROLE_TT_REGISTERED", "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
    public String saveUserProfile(@ModelAttribute UserProfileView user, Model model) {
        saveUser(user, model);
        return PROFILE_VIEW;
    }

    @PostMapping("/saveuser")
    @Secured({ "ROLE_TT_REGISTERED", "ROLE_TT_MEMBER", "ROLE_TT_TEAMADMIN", "ROLE_TT_SYSADMIN" })
    public String saveUser(@ModelAttribute UserProfileView profileView, Model model) {
        if( profileView.getUserType().equalsIgnoreCase(TtUserType.TT_NEW.toText()) ) {
            profileView.setUserType(TtUserType.TT_REGISTERED.toText());
        }

        if( profileView.getClientMessageAccessToken() == null ) {
            profileView.setClientMessageAccessToken(UUID.randomUUID().toString());
        }

        String newIRacingId = profileView.getIRacingId();
        if( newIRacingId.isEmpty() ) {
            addError("iRacing ID must not be empty !", model);
        } else if( !newIRacingId.equals(currentUser().getIRacingId()) ) {
            // Changed iRacing id in profile form - generate a new token
            List<TtUser> existingUser = userService.findByIracingId(newIRacingId);
            if (existingUser.isEmpty()) {
                profileView.setClientMessageAccessToken(UUID.randomUUID().toString());
                userService.save(profileView.getUser(currentUser()));
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
        return "acknewuser";
    }
}
