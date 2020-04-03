package de.bausdorf.simcacing.tt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableAdminServer
@EnableConfigurationProperties
public class Application {

//	@Configuration
	public static class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().anyRequest().permitAll()
					.and().csrf().disable();
//			http.authorizeRequests()
//					.anyRequest().authenticated()
//					.and()
//					.oauth2Login();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
