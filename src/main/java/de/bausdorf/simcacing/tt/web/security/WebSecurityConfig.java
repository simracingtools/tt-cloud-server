package de.bausdorf.simcacing.tt.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
@ConditionalOnBean(ClientRegistrationRepository.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    GoogleUserService userService;

    public WebSecurityConfig(@Autowired GoogleUserService userService) {
        this.userService = userService;
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public PrincipalExtractor githubPrincipalExtractor() {
        return new GooglePrincipalExtractor(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
    			.antMatchers("/clientmessage").permitAll()
                .anyRequest().authenticated()
				.and()
//                .userDetailsService(userService)
				.oauth2Login()
                .userInfoEndpoint()
                .oidcUserService(userService);
    }
}