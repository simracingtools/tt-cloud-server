package de.bausdorf.simcacing.tt.util;

import java.util.Arrays;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import de.bausdorf.simcacing.tt.util.AbstractIntegrationTest.TestConfig;


@SpringBootTest
@Import(TestConfig.class)
public abstract class AbstractIntegrationTest {

	static class TestConfig {
		@Bean
		static ClientRegistrationRepository clientRegistrationRepository() {
			return new InMemoryClientRegistrationRepository(Arrays.asList(
					ClientRegistration
							.withRegistrationId("google")
							.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
							.clientId("fake-google-id")
							.redirectUriTemplate("/redirectUriTemplate")
							.authorizationUri("/authUri")
							.tokenUri("/tokenUri")
							.clientSecret("clientSecret")
							.build()));
		}
	}
}
