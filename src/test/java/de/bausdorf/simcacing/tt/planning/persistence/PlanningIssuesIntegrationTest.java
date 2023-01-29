package de.bausdorf.simcacing.tt.planning.persistence;

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

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import de.bausdorf.simcacing.tt.planning.PlanParameterRepository;
import de.bausdorf.simcacing.tt.web.security.TtIdentityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import de.bausdorf.simcacing.tt.planning.PlanningTools;
import de.bausdorf.simcacing.tt.planning.persistence.PlanningIssuesIntegrationTest.TestConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Import(TestConfig.class)
class PlanningIssuesIntegrationTest {

	static class TestConfig {
		@Bean
		static ClientRegistrationRepository clientRegistrationRepository() {
			return new InMemoryClientRegistrationRepository(List.of(
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
	@Autowired
	TtIdentityRepository clientRegistrationRepository;

	@Autowired
	PlanParameterRepository planRepository;

	@Test
	void test_wrong_index_in_planned_stints() {
		PlanParameters planParameters = planRepository.findById("faf22813-9ee3-4190-a0fc-02c6e26c77e6").orElseThrow(IllegalArgumentException::new);
		log.info("Loaded plan timezone    : {}", ZoneId.ofOffset("", planParameters.getSessionStartDateTime().getOffset()));
		log.info("System default time zone: {}", ZoneId.of("UTC"));
		PlanParameters serverZonedRacePlan = new PlanParameters(planParameters);
		log.info("Converted start time    : {}", serverZonedRacePlan.getSessionStartDateTime().toString());

		OffsetDateTime from = serverZonedRacePlan.getSessionStartDateTime();
		for( int i = 1; i < 24; i++) {
			from = from.plus(Duration.ofMinutes(59));
			int index = PlanningTools.stintIndexAt(from, serverZonedRacePlan.getStints());
			log.info("Stint index at {}: {}", from.toString(), index);
		}
	}
}
