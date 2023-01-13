package de.bausdorf.simcacing.tt.web.security;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	public static final String ROLE_PREFIX = "ROLE_";

	private final GoogleUserService userService;

	public WebSecurityConfig(@Autowired GoogleUserService userService) {
		super(false);
		this.userService = userService;
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/clientmessage", "/_ah/**", "/live/**", "/plan/**", "/app/**", "/liveclient", "/planclient");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("!/_ah", "!/clientmessage", "!/live/**", "!/plan/**", "!/app/**", "!/liveclient", "!/planclient")
					.permitAll()
					.anyRequest().authenticated()
				.and()
				.rememberMe()
					.key("irtactics")
					.tokenValiditySeconds(90000)
				.and()
				.oauth2Login()
					.authorizationEndpoint()
//					.authorizationRequestResolver(
//						new CustomAuthorizationRequestResolver(
//								registrationRepository))
				.and()
				.userInfoEndpoint()
					.userAuthoritiesMapper(this.userAuthoritiesMapper())
				.oidcUserService(userService)
				.and()
				.and()
				.exceptionHandling().accessDeniedHandler(accessDeniedHandler());


	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new TtAccessDeniedHandler();
	}

	private GrantedAuthoritiesMapper userAuthoritiesMapper() {
		return authorities -> {
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

			authorities.forEach(authority -> {
				if (authority instanceof OidcUserAuthority) {
					OidcUserAuthority oidcUserAuthority = (OidcUserAuthority)authority;

					SimpleGrantedAuthority userRole = userService.determineUserRole(oidcUserAuthority.getIdToken().getSubject());
					if (userRole != null){
						mappedAuthorities.add(userRole);
					}
				} else if (authority instanceof OAuth2UserAuthority) {
					OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority)authority;

					SimpleGrantedAuthority userRole = userService.determineUserRole(oauth2UserAuthority.getAttributes().get("sub").toString());
					if (userRole != null){
						mappedAuthorities.add(userRole);
					}
				}
			});

			return mappedAuthorities;
		};
	}

	public static void updateCurrentUserRole(TtUserType newRole) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
		updatedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + newRole.name()));
		Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
		SecurityContextHolder.getContext().setAuthentication(newAuth);
	}
}
