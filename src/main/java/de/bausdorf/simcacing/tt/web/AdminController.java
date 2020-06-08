package de.bausdorf.simcacing.tt.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.bausdorf.simcacing.tt.web.model.SearchView;
import de.bausdorf.simcacing.tt.web.security.TtUser;
import de.bausdorf.simcacing.tt.web.security.TtUserType;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AdminController extends BaseController {

	public static final String USER_LIST = "userList";
	public static final String SEARCH_VIEW = "searchView";
	public static final String ADMIN_VIEW = "admin";

	@GetMapping("/admin")
	@Secured("ROLE_TT_SYSADMIN")
	public String adminView(Model model) {

		model.addAttribute(SEARCH_VIEW, new SearchView());
		model.addAttribute(USER_LIST, new ArrayList<>());

		return ADMIN_VIEW;
	}

	@PostMapping("/usersearch")
	@Secured("ROLE_TT_SYSADMIN")
	public String searchUsers(@ModelAttribute SearchView searchView, Model model) {
		if (searchView != null) {
			List<TtUser> userList = userService.findBySearchView(searchView);
			model.addAttribute(SEARCH_VIEW, searchView);
			model.addAttribute(USER_LIST, userList);
		} else {
			model.addAttribute(SEARCH_VIEW, new SearchView());
			model.addAttribute(USER_LIST, new ArrayList<TtUser>());
		}
		return ADMIN_VIEW;
	}

	@GetMapping("/savesiteuser")
	@Secured("ROLE_TT_SYSADMIN")
	public String saveUser(@RequestParam String userId,  @RequestParam String role,
			@RequestParam boolean enabled, @RequestParam boolean locked, @RequestParam boolean expired, Model model) {
		SearchView searchView = new SearchView();
		if (userId != null) {
			Optional<TtUser> existingUser = userService.findById(userId);
			if (existingUser.isPresent()) {
				existingUser.get().setUserType(TtUserType.valueOf(role));
				existingUser.get().setEnabled(enabled);
				existingUser.get().setLocked(locked);
				existingUser.get().setExpired(expired);
				userService.save(existingUser.get());
				searchView.setUserRole(role);
				searchView.setUserName(existingUser.get().getName());
			}
		}
		return searchUsers(searchView, model);
	}

	@ModelAttribute("userTypes")
	public TtUserType[] userTypes() {
		return TtUserType.values();
	}
}
