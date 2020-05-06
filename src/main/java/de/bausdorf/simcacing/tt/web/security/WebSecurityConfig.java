package de.bausdorf.simcacing.tt.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private GoogleUserService userService;
//	private ClientRegistrationRepository registrationRepository;

	public WebSecurityConfig(@Autowired GoogleUserService userService,
			@Autowired	ClientRegistrationRepository registrationRepository) {
		super(false);
		this.userService = userService;
//		this.registrationRepository = registrationRepository;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/clientmessage", "/_ah/**", "/live/**", "/app/**", "/liveclient");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("!/_ah", "!/clientmessage", "!/live/**", "!/app/**", "!/liveclient")
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
				.oidcUserService(userService);

	}

}