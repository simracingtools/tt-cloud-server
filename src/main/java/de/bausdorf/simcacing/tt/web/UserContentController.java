package de.bausdorf.simcacing.tt.web;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.web.model.UserProfileView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.bausdorf.simcacing.tt.web.security.TtUser;
import de.bausdorf.simcacing.tt.web.security.TtUserType;

@Controller
public class UserContentController extends BaseController {


    @GetMapping("/acknewuser")
    public String showUserAck() {
        return "acknewuser";
    }

    @PostMapping("/acknewuser")
    public String showProfileAfterAck() {
        return "profile";
    }

    @GetMapping("/profile")
    public String showUserProfile() {
        return "profile";
    }

    @PostMapping("/profile")
    public String saveUserProfile(@ModelAttribute UserProfileView user, Model model) {
        saveUser(user, model);
        return "profile";
    }

    @PostMapping("/saveuser")
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
