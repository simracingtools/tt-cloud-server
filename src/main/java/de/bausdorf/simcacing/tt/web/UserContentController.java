package de.bausdorf.simcacing.tt.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.view.ThymeleafView;

@Controller
public class UserContentController extends BaseController {

    @GetMapping("/newuser")
    public String showNewUserContent() {
        return "newuser";
    }

    @Bean(name="newuser")
    @Scope("prototype")
    public ThymeleafView content() {
        ThymeleafView view = new ThymeleafView("newuser");
        view.setMarkupSelector("content");
        return view;
    }
}
