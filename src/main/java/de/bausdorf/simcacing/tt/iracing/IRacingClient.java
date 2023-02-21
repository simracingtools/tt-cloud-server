package de.bausdorf.simcacing.tt.iracing;

/*-
 * #%L
 * racecontrol-server
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

import de.bausdorf.simcacing.tt.schedule.model.RaceEvent;
import de.bausdorf.simracing.irdataapi.client.AuthorizationException;
import de.bausdorf.simracing.irdataapi.client.DataApiException;
import de.bausdorf.simracing.irdataapi.client.IrDataClient;
import de.bausdorf.simracing.irdataapi.client.impl.IrDataClientImpl;
import de.bausdorf.simracing.irdataapi.config.ConfigProperties;
import de.bausdorf.simracing.irdataapi.model.*;
import de.bausdorf.simracing.irdataapi.tools.StockDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class IRacingClient {

	public static final String I_RACING_HTTP_ERROR = "iRacing http error {}: {}";
	private final ConfigProperties serverProperties;
	private final IrDataClient dataClient;
	private final StockDataCache dataCache;

	public IRacingClient(@Autowired ConfigProperties serverProperties) {
		this.serverProperties = serverProperties;
		this.dataClient = new IrDataClientImpl();
		this.dataClient.setHashPassword(serverProperties.getHashPassword());
		this.dataCache = new StockDataCache(serverProperties.getCacheDirectory());
	}

	public StockDataCache getDataCache() {
		if(!dataCache.isInitialized()) {
			authenticate();
			dataCache.fetchFromService(dataClient);
		}
		return dataCache;
	}

	public void renewStockDataCache() {
		authenticate();
		dataCache.fetchFromService(dataClient);
		log.info("Stock data cache renewed");
	}

	public TrackInfoDto getTrackFromCache(long trackId) {
		return Arrays.stream(dataCache.getTracks()).filter(track -> track.getTrackId() == trackId).findFirst().orElse(null);
	}

	public Optional<TeamInfoDto> getTeamInfo(long teamId) {
		try {
			authenticate();
			return Optional.of(dataClient.getTeamMembers(teamId));
		} catch(HttpClientErrorException clientError) {
			log.warn(I_RACING_HTTP_ERROR,clientError.getRawStatusCode(), clientError.getResponseBodyAsString());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public List<SeasonDto> getTeamSeasons() {
		try {
			authenticate();
			return Arrays.stream(dataClient.getSeasonInfo(true))
					.filter(SeasonDto::getDriverChanges)
					.collect(Collectors.toList());
		} catch(HttpClientErrorException clientError) {
			log.warn(I_RACING_HTTP_ERROR,clientError.getRawStatusCode(), clientError.getResponseBodyAsString());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return List.of();
	}

	public List<RaceEvent> getRaceEventsForSeason(SeasonDto seasonDto) {
		List<RaceEvent> raceEvents = new ArrayList<>();

		List<CarClassDto> carClasses = Arrays.stream(dataCache.getCarClasses())
				.filter(carClass -> Arrays.asList(seasonDto.getCarClassIds()).contains(carClass.getCarClassId()))
				.collect(Collectors.toList());

		List<String> carIds = new ArrayList<>();
		carClasses.forEach(cc -> Arrays.stream(cc.getCarsInClass()).forEach(cic -> carIds.add(cic.getCarId().toString())));

		Arrays.stream(seasonDto.getSchedules()).forEach(schedule -> Arrays.stream(schedule.getRaceTimeDescriptors())
				.forEach(desc -> Arrays.stream(desc.getSessionTimes()).forEach(time -> {
					String[] seriesParts = seasonDto.getSeasonName().split("-");
					String seriesName = seriesParts.length > 0 ? seriesParts[0].trim() : "";

					RaceEvent event = RaceEvent.builder()
							.season(seasonDto.getSeasonShortName())
							.carIds(carIds)
							.raceDuration(Duration.ofMinutes(schedule.getRaceTimeLimit()))
							.series(seriesName.contains(seasonDto.getSeasonShortName()) ? "Special Events" : seriesName)
							.simDateTime(schedule.getWeather().getSimulatedStartTime())
							.raceSessionOffset(Long.toString(desc.getSessionMinutes() - schedule.getRaceTimeLimit()))
							.sessionDateTime(time.toOffsetDateTime())
							.name(seasonDto.getSeasonName())
							.trackId(schedule.getTrack().getTrackId().toString())
							.build();

					raceEvents.add(event);
				})));

		return raceEvents;
	}

	public Optional<MemberInfoDto> getFullMemberInfo(long iracingId) {
		try {
			authenticate();
			return Arrays.stream(dataClient.getMembersInfo(List.of(iracingId)).getMembers()).findFirst();
		} catch(HttpClientErrorException clientError) {
			log.warn(I_RACING_HTTP_ERROR,clientError.getRawStatusCode(), clientError.getResponseBodyAsString());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public LeagueInfoDto getLeagueInfo(long leagueId) {
		try {
			authenticate();
			return dataClient.getLeagueInfo(leagueId);
		} catch(HttpClientErrorException clientError) {
			log.warn(I_RACING_HTTP_ERROR,clientError.getRawStatusCode(), clientError.getResponseBodyAsString());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Optional<SubsessionResultDto> getSubsessionResult(long subsessionId) {
		try {
			authenticate();
			return Optional.of(dataClient.getSubsessionResult(subsessionId));
		} catch(HttpClientErrorException clientError) {
			log.warn(I_RACING_HTTP_ERROR,clientError.getRawStatusCode(), clientError.getResponseBodyAsString());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public List<LapChartEntryDto> getLapChartData(long subsessionId, long simsessionNumber) {
		try {
			authenticate();
			LapChartDto lapChartDto = dataClient.getLapChartData(subsessionId, simsessionNumber);
			return dataClient.getLapEntries(lapChartDto.getChunkInfo());
		} catch(HttpClientErrorException clientError) {
			log.warn(I_RACING_HTTP_ERROR,clientError.getRawStatusCode(), clientError.getResponseBodyAsString());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return List.of();
	}

	public Optional<CustLeagueSessionsDto> getLeagueFutureSessions() {
		try {
			authenticate();
			return Optional.of(dataClient.getLeagueSessions(true));
		} catch(HttpClientErrorException clientError) {
			log.warn(I_RACING_HTTP_ERROR,clientError.getRawStatusCode(), clientError.getResponseBodyAsString());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<LeagueSeasonsDto> getLeagueSeasons(long seasonId) {
		try {
			authenticate();
			return Optional.of(dataClient.getLeagueSeasons(seasonId, false));
		} catch(HttpClientErrorException clientError) {
			log.warn(I_RACING_HTTP_ERROR,clientError.getRawStatusCode(), clientError.getResponseBodyAsString());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<LeagueSeasonSessionsDto> getLeaguePastSessions(long leagueId, long seasonId) {
		try {
			authenticate();
			return Optional.of(dataClient.getLeagueSeasonSessions(leagueId, seasonId, true));
		} catch(HttpClientErrorException clientError) {
			log.warn(I_RACING_HTTP_ERROR,clientError.getRawStatusCode(), clientError.getResponseBodyAsString());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	private void authenticate() {
		try {
			dataClient.getMemberSummary(229120L);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				log.warn("IrDataClient not authorized - try to authenticate");
				LoginRequestDto loginRequestDto = LoginRequestDto.builder()
						.email(serverProperties.getUser())
						.password(serverProperties.getPassword())
						.build();
				try {
					dataClient.authenticate(loginRequestDto);
					log.info("IrDataClient authentication success");
				} catch (DataApiException | AuthorizationException exx) {
					log.error(exx.getMessage());
				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
				}
			}
		}
	}
}
