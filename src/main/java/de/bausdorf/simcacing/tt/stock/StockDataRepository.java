package de.bausdorf.simcacing.tt.stock;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 - 2023 bausdorf engineering
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

import de.bausdorf.simcacing.tt.iracing.IRacingClient;
import de.bausdorf.simcacing.tt.stock.model.IRacingCar;
import de.bausdorf.simcacing.tt.stock.model.IRacingCarClass;
import de.bausdorf.simcacing.tt.stock.model.IRacingTrack;
import de.bausdorf.simracing.irdataapi.tools.StockDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StockDataRepository {
    protected final StockDataCache stockDataCache;

    public StockDataRepository(@Autowired IRacingClient dataClient) {
        this.stockDataCache = dataClient.getDataCache();
    }

    public Optional<IRacingCar> findCarById(String carId) {
        return Arrays.stream(stockDataCache.getCars())
                .filter(carInfo -> carInfo.getCarId().equals(Long.parseLong(carId)))
                .findFirst()
                .map(carInfo -> IRacingCar.builder()
                        .id(carInfo.getCarId().toString())
                        .name(carInfo.getCarName())
                        .maxFuel(0.0)
                        .unit("l")
                        .build());
    }

    public List<IRacingCar> loadAllCars() {
        return Arrays.stream(stockDataCache.getCars())
                .map(car -> IRacingCar.builder()
                        .name(car.getCarName())
                        .id(car.getCarId().toString())
                        .maxFuel(0)
                        .unit("l")
                        .build())
                .sorted(Comparator.comparing(IRacingCar::getName))
                .collect(Collectors.toList());
    }

    public Optional<IRacingTrack> findTrackById(String trackId) {
        return Arrays.stream(stockDataCache.getTracks())
                .filter(trackInfo -> trackInfo.getTrackId().equals(Long.parseLong(trackId)))
                .findFirst()
                .map(trackInfo -> IRacingTrack.builder()
                        .id(trackInfo.getTrackId().toString())
                        .name(trackInfo.getTrackName() + (trackInfo.getConfigName() != null ? " - " + trackInfo.getConfigName(): ""))
                        .build());
    }

    public List<IRacingTrack> loadAllTracks() {
        return Arrays.stream(stockDataCache.getTracks())
                .map(track -> IRacingTrack.builder()
                        .id(track.getTrackId().toString())
                        .name(track.getTrackName() + (track.getConfigName() != null ? " - " + track.getConfigName() : ""))
                        .trackType(track.getTrackTypes()[0].getTrackType())
                        .nightLighting(track.getNightLighting().toString())
                        .nominalLapTime(track.getNominalLapTime().toString())
                        .cost("")
                        .surface("")
                        .build())
                .sorted(Comparator.comparing(IRacingTrack::getName))
                .collect(Collectors.toList());
    }

    public Optional<IRacingCarClass> findCarClassByName(String id) {
        return Arrays.stream(stockDataCache.getCarClasses()).filter(cc -> cc.getCarClassId() == Long.parseLong(id))
                .findFirst()
                .map(dto -> IRacingCarClass.builder()
                        .name(dto.getName())
                        .id(id)
                        .carIds(Arrays.stream(dto.getCarsInClass()).map(car -> car.getCarId().toString()).collect(Collectors.toList()))
                        .build()
                );
    }
}
