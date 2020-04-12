package de.bausdorf.simcacing.tt.web;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
    public String showIndexAfterAck() {
        return "index";
    }

    @GetMapping("/profile")
    public String showUserProfile() {
        return "profile";
    }

    @PostMapping("/profile")
    public String saveUserProfile(@ModelAttribute TtUser user, BindingResult errors, Model model) {
        saveUser(user, errors, model);
        return "profile";
    }

    @PostMapping("/saveuser")
    public String saveUser(@ModelAttribute TtUser user, BindingResult errors, Model model) {
        messages().clear();
        TtUser modelUser = (TtUser)model.getAttribute("user");
        user.setExpired(modelUser.isExpired());
        user.setLocked(modelUser.isLocked());
        user.setImageUrl(modelUser.getImageUrl());
        if( modelUser.getUserType() == TtUserType.TT_NEW ) {
            user.setUserType(TtUserType.TT_REGISTERED);
        } else {
            user.setUserType(modelUser.getUserType());
        }
        if( modelUser.getClientMessageAccessToken() == null ) {
            user.setClientMessageAccessToken(UUID.randomUUID().toString());
        } else {
            user.setClientMessageAccessToken(modelUser.getClientMessageAccessToken());
        }
        String newIRacingId = user.getIRacingId();
        if( newIRacingId.isEmpty() ) {
            addError("iRacing ID must not be empty !", model);
        } else if( !newIRacingId.equals(modelUser.getIRacingId()) ) {
            List<TtUser> existingUser = userService.findByIracingId(newIRacingId);
            if (existingUser.isEmpty()) {
                user.setClientMessageAccessToken(UUID.randomUUID().toString());
                userService.save(user);
                modelUser.setIRacingId(user.getIRacingId());
                addInfo("iRacing ID changed in profile. A new token was generated - change your TeamTactics client config", model);
            } else {
                addError("iRacingId " + newIRacingId + " is already registered", model);
            }
        }
        modelUser.setClientMessageAccessToken(user.getClientMessageAccessToken());
        return "acknewuser";
    }
}
