package de.bausdorf.simcacing.tt.live.impl;

import de.bausdorf.simcacing.tt.live.clientapi.MessageConstants;
import de.bausdorf.simcacing.tt.live.model.client.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelFactory {

    private ModelFactory() {
        super();
    }

    public static LapData fromLapMessage(Map<String, Object> messagePayload) {
        return LapData.builder()
                .driver((String)messagePayload.get(MessageConstants.LapData.DRIVER))
                .fuelLevel(((Double)messagePayload.get(MessageConstants.LapData.FUEL_LEVEL)))
                .lapTime(getFromIracingDuration(messagePayload.get(MessageConstants.LapData.LAP_TIME)))
                .no((Integer)messagePayload.get(MessageConstants.LapData.LAP))
                .trackTemp((Double)messagePayload.get(MessageConstants.LapData.TRACK_TEMP))
                .sessionTime(getFromIracingSessionTime(messagePayload.get(MessageConstants.LapData.SESSION_TIME)))
                .driverId(stringOfNumberOrString(messagePayload.get(MessageConstants.LapData.DRIVER_ID)))
                .build();
    }

    public static RunData fromRunDataMessage(Map<String, Object> messagePayload) {
        return RunData.builder()
                .estLapTime(getFromIracingDuration(messagePayload.get(MessageConstants.RunData.EST_LAP_TIME)))
                .fuelLevel((Double)messagePayload.get(MessageConstants.RunData.FUEL_LEVEL))
                .sessionTime(getFromIracingSessionTime(messagePayload.get(MessageConstants.RunData.SESSION_TIME)))
                .sessionToD(getFromIracingSessionTime(messagePayload.get(MessageConstants.RunData.SESSION_TOD)))
                .flags(((List<String>)messagePayload.get(MessageConstants.RunData.FLAGS)).stream()
                        .map(FlagType::valueOf)
                        .collect(Collectors.toList()))
                .lapNo((Integer)messagePayload.get(MessageConstants.RunData.LAP_NO))
                .timeInLap(getFromIracingDuration(messagePayload.get(MessageConstants.RunData.TIME_IN_LAP)))
                .build();
    }

    public static EventData getFromEventMessage(Map<String, Object> messagePayload) {
        return EventData.builder()
                .flags(FlagType.fromIrBitmask((Integer)messagePayload.get(MessageConstants.EventData.FLAGS)))
                .trackLocationType(TrackLocationType.forIrCode((Integer)messagePayload.get(MessageConstants.EventData.TRACK_LOCATION)))
                .sessionTime(getFromIracingSessionTime(messagePayload.get(MessageConstants.EventData.SESSION_TIME)))
                .sessionToD(getFromIracingSessionTime(messagePayload.get(MessageConstants.RunData.SESSION_TOD)))
                .optRepairTime(getFromIracingDuration(messagePayload.get(MessageConstants.EventData.OPT_REPAIR_TIME)))
                .repairTime(getFromIracingDuration(messagePayload.get(MessageConstants.EventData.REPAIR_TIME)))
                .towingTime(getFromIracingDuration(messagePayload.get(MessageConstants.EventData.TOW_TIME)))
                .serviceFlags(ServiceFlagType.ofBitmask((Integer)messagePayload.get(MessageConstants.EventData.SERVICE_FLAGS)))
                .build();
    }

    public static SessionData getFromSessionMessage(Map<String, Object> messagePayload) {
        return SessionData.builder()
                .carName((String)messagePayload.get(MessageConstants.SessionData.CAR_NAME))
                .carId(stringOfNumberOrString(messagePayload.get(MessageConstants.SessionData.CAR_ID)))
                .maxCarFuel((Double)messagePayload.get(MessageConstants.SessionData.MAX_FUEL))
                .sessionId(getFromClientString(messagePayload.get(MessageConstants.SessionData.SESSION_ID)))
                .sessionLaps(stringOfNumberOrString(messagePayload.get(MessageConstants.SessionData.SESSION_LAPS)))
                .sessionTime(stringOfNumberOrString(messagePayload.get(MessageConstants.SessionData.SESSION_DURATION)))
                .sessionType((String)messagePayload.get(MessageConstants.SessionData.SESSION_TYPE))
                .teamName((String)messagePayload.get(MessageConstants.SessionData.TEAM_NAME))
                .trackName((String)messagePayload.get(MessageConstants.SessionData.TRACK_NAME))
                .trackId(stringOfNumberOrString(messagePayload.get(MessageConstants.SessionData.TRACK_ID)))
                .build();
    }

    public static SyncData getFromSyncMessage(Map<String, Object> payload) {
        return SyncData.builder()
                .clientId((String)payload.get(MessageConstants.SyncData.CLIENT_ID))
                .sessionTime(getFromIracingSessionTime(payload.get(MessageConstants.SyncData.SESSION_TIME)))
                .isInCar((Boolean)payload.get(MessageConstants.SyncData.IN_CAR))
                .build();
    }

    public static SessionIdentifier parseClientSessionId(String sessionId) {
        try {
            String[] parts = sessionId.split("\\s*[@#]+");
            return SessionIdentifier.builder()
                    .teamName(parts[0])
                    .sessionId(parts[1])
                    .subSessionId(parts[2])
                    .sessionNum(Integer.parseInt(parts[3]))
                    .build();
        } catch( Exception e ) {
            throw new IllegalArgumentException("Session id " + sessionId + " is not valid");
        }
    }

    public static String stringOfNumberOrString(Object o) {
        if (o instanceof Double) {
            return o.toString();
        }
        if (o instanceof Integer) {
            return ((Integer)o).toString();
        }
        if (o instanceof Long) {
            return ((Long)o).toString();
        }
        return (String)o;
    }

    private static LocalTime getFromIracingSessionTime(Object iRacingSessionTime) {
        if (iRacingSessionTime == null) {
            return LocalTime.MIN;
        }
        // iRacing session time is seconds as double
        return LocalTime.ofNanoOfDay((long)((Double)iRacingSessionTime * 1000000000));
    }

    private static Duration getFromIracingDuration(Object iRacingDuration) {
        if (iRacingDuration == null) {
            return Duration.ZERO;
        }
        // iRacing lap, repair and tow times are seconds as float
        return Duration.ofNanos((long)((Double)iRacingDuration * 1000000000));
    }

    private static SessionIdentifier getFromClientString(Object clientSessionId) {
        return parseClientSessionId((String)clientSessionId);
    }
}
