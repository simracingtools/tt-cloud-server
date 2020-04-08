package de.bausdorf.simcacing.tt.web;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.bausdorf.simcacing.tt.web.security.TtUser;
import de.bausdorf.simcacing.tt.web.security.TtUserType;

@Controller
public class UserContentController extends BaseController {

    @GetMapping("/newuser")
    public String showNewUserForm() {
        return "user";
    }

    @GetMapping("/profile")
    public String showUserProfile() {
        return "profile";
    }

    @PostMapping("/saveuser")
    public String saveUser(@ModelAttribute TtUser user, BindingResult errors, Model model) {
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
        userService.save(user);
        modelUser.setClientMessageAccessToken(user.getClientMessageAccessToken());
        modelUser.setIRacingId(user.getIRacingId());
        return "profile";
    }
}
