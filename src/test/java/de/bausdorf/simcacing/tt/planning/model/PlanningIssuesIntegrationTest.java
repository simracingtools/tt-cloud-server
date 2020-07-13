package de.bausdorf.simcacing.tt.planning.model;

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
