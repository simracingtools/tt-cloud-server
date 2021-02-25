package de.bausdorf.simcacing.tt.util;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 - 2021 bausdorf engineering
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
