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

import java.util.*;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.web.security.TtIdentity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.bausdorf.simcacing.tt.web.model.SearchView;
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
			List<TtIdentity> userList = findBySearchView(searchView);
			model.addAttribute(SEARCH_VIEW, searchView);
			model.addAttribute(USER_LIST, userList);
		} else {
			model.addAttribute(SEARCH_VIEW, new SearchView());
			model.addAttribute(USER_LIST, new ArrayList<TtIdentity>());
		}
		return ADMIN_VIEW;
	}

	@GetMapping("/savesiteuser")
	@Secured("ROLE_TT_SYSADMIN")
	public String saveUser(@RequestParam String userId,  @RequestParam String role,
			@RequestParam boolean enabled, @RequestParam boolean locked, @RequestParam boolean expired, Model model) {
		SearchView searchView = new SearchView();
		if (userId != null) {
			Optional<TtIdentity> existingUser = userService.findById(userId);
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

	@GetMapping("/deletesiteuser")
	public String deleteUser(@RequestParam String userId, Model model) {
		SearchView searchView = new SearchView();
		if (userId != null) {
			Optional<TtIdentity> existingUser = userService.findById(userId);
			if (existingUser.isPresent()) {
				userService.deleteById(userId);
				addInfo("User " + existingUser.get().getName() + " deleted", model);
				searchView.setUserRole(existingUser.get().getUserType().name());
			}
		}
		return searchUsers(searchView, model);
	}

	@ModelAttribute("userTypes")
	public TtUserType[] userTypes() {
		return TtUserType.values();
	}

    List<TtIdentity> findBySearchView(SearchView searchView)
    {
		List<TtIdentity> users = userService.findAllByEnabledAndExpiredAndLocked(!searchView.isDisabled(), searchView.isExpired(), searchView.isLocked());
		return users.stream()
				.filter(user -> searchView.getUserRole().equalsIgnoreCase("*")
						|| user.getUserType() == TtUserType.valueOf(searchView.getUserRole()))
				.filter(user -> searchView.getUserName().isEmpty()
						|| user.getName().toLowerCase().contains(searchView.getUserName().toLowerCase()))
				.filter(user -> searchView.getEmail().isEmpty()
						|| user.getEmail().toLowerCase().contains(searchView.getEmail().toLowerCase()))
				.collect(Collectors.toList());
    }
}
