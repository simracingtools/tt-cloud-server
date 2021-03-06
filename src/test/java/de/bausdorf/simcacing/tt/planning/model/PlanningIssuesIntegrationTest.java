package de.bausdorf.simcacing.tt.planning.model;

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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import de.bausdorf.simcacing.tt.planning.PlanningTools;
import de.bausdorf.simcacing.tt.planning.RacePlanRepository;
import de.bausdorf.simcacing.tt.planning.model.PlanningIssuesIntegrationTest.TestConfig;
import de.bausdorf.simcacing.tt.web.security.TtClientRegistrationRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Import(TestConfig.class)
class PlanningIssuesIntegrationTest {

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
	@Autowired
	TtClientRegistrationRepository clientRegistrationRepository;

	@Autowired
	RacePlanRepository planRepository;

	@Test
	void test_wrong_index_in_planned_stints() {
		RacePlanParameters planParameters = planRepository.findById("faf22813-9ee3-4190-a0fc-02c6e26c77e6").orElseThrow(IllegalArgumentException::new);
		log.info("Loaded plan timezone    : {}", planParameters.getSessionStartTime().getZone().getId());
		log.info("System default time zone: {}", ZoneId.of("UTC"));
		RacePlanParameters serverZonedRacePlan = new RacePlanParameters(planParameters, ZoneId.of("UTC"));
		log.info("Converted start time    : {}", serverZonedRacePlan.getSessionStartTime().toString());

		ZonedDateTime from = serverZonedRacePlan.getSessionStartTime();
		for( int i = 1; i < 24; i++) {
			from = from.plus(Duration.ofMinutes(59));
			int index = PlanningTools.stintIndexAt(from, serverZonedRacePlan.getStints());
			log.info("Stint index at {}: {}", from.toString(), index);
		}
	}
}
