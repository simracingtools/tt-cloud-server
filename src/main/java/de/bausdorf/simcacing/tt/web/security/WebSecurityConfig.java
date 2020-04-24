package de.bausdorf.simcacing.tt.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    GoogleUserService userService;

    public WebSecurityConfig(@Autowired GoogleUserService userService) {
        this.userService = userService;
    }

//    @Bean
//    @ConditionalOnMissingBean
//    public OAuth2AuthorizedClientService authorizedClientService(
//            ClientRegistrationRepository clientRegistrationRepository) {
//        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
//    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/clientmessage", "/_ah/**", "/live/**", "/liveclient");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
    			.antMatchers("!/_ah", "!/clientmessage", "!/live/**", "!/liveclient")
                .permitAll()
                .anyRequest().authenticated()
				.and()
				.oauth2Login()
                .userInfoEndpoint()
                .oidcUserService(userService);
    }
}