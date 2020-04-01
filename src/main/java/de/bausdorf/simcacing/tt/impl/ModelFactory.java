package de.bausdorf.simcacing.tt.impl;

import de.bausdorf.simcacing.tt.clientapi.MessageConstants;
import de.bausdorf.simcacing.tt.model.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;

public class ModelFactory {

    private ModelFactory() {
        super();
    }

    public static LapData fromLapMessage(Map<String, Object> messagePayload) {
        return LapData.builder()
                .driver((String)messagePayload.get(MessageConstants.Lap.DRIVER))
                .fuelLevel(((Double)messagePayload.get(MessageConstants.Lap.FUEL_LEVEL)))
                .lapTime(getFromIracingDuration(messagePayload.get(MessageConstants.Lap.LAP_TIME)))
                .no((Integer)messagePayload.get(MessageConstants.Lap.LAP))
                .trackTemp((Double)messagePayload.get(MessageConstants.Lap.TRACK_TEMP))
                .sessionTime(getFromIracingSessionTime(messagePayload.get(MessageConstants.Lap.SESSION_TIME)))
                .build();
    }

    public static RunData fromRunDataMessage(Map<String, Object> messagePayload) {
        return RunData.builder()
                .estLapTime(getFromIracingDuration(messagePayload.get(MessageConstants.RunData.EST_LAP_TIME)))
                .fuelLevel((Double)messagePayload.get(MessageConstants.RunData.FUEL_LEVEL))
                .sessionTime(getFromIracingSessionTime(messagePayload.get(MessageConstants.RunData.SESSION_TIME)))
                .flags(FlagType.fromIrBitmask((Long)messagePayload.get(MessageConstants.RunData.FLAGS)))
                .build();
    }

    public static EventData getFromEventMessage(Map<String, Object> messagePayload) {
        return EventData.builder()
                .flags(FlagType.fromIrBitmask((Long)messagePayload.get(MessageConstants.EventData.FLAGS)))
                .trackLocationType(TrackLocationType.forIrCode((Integer)messagePayload.get(MessageConstants.EventData.TRACK_LOCATION)))
                .sessionTime(getFromIracingSessionTime(messagePayload.get(MessageConstants.EventData.SESSION_TIME)))
                .optRepairTime(getFromIracingDuration(messagePayload.get(MessageConstants.EventData.OPT_REPAIR_TIME)))
                .repairTime(getFromIracingDuration(messagePayload.get(MessageConstants.EventData.REPAIR_TIME)))
                .towingTime(getFromIracingDuration(messagePayload.get(MessageConstants.EventData.TOW_TIME)))
                .build();
    }

    public static SessionData getFromSessionMessage(Map<String, Object> messagePayload) {
        return SessionData.builder()
                .carName((String)messagePayload.get(MessageConstants.SessionData.CAR_NAME))
                .maxCarFuel((Double)messagePayload.get(MessageConstants.SessionData.MAX_FUEL))
                .sessionId(getFromClientString(messagePayload.get(MessageConstants.SessionData.SESSION_ID)))
                .sessionLaps((String)messagePayload.get(MessageConstants.SessionData.SESSION_LAPS))
                .sessionTime((String)messagePayload.get(MessageConstants.SessionData.SESSION_DURATION))
                .sessionType((String)messagePayload.get(MessageConstants.SessionData.SESSION_TYPE))
                .teamName((String)messagePayload.get(MessageConstants.SessionData.TEAM_NAME))
                .trackName((String)messagePayload.get(MessageConstants.SessionData.TRACK_NAME))
                .build();
    }

    public static SyncData getFromSyncMessage(Map<String, Object> payload) {
        return SyncData.builder()
                .clientId((String)payload.get(MessageConstants.SyncData.CLIENT_ID))
                .sessionTime(getFromIracingSessionTime(payload.get(MessageConstants.SyncData.SESSION_TIME)))
                .isInCar((Boolean)payload.get(MessageConstants.SyncData.IN_CAR))
                .build();
    }

    public static SessionId parseClientSessionId(String sessionId) {
        try {
            String[] parts = ( sessionId).split("[@#]");
            return SessionId.builder()
                    .teamName(parts[0])
                    .sessionId(parts[1])
                    .subSessionId(parts[2])
                    .sessionNum(Integer.parseInt(parts[3]))
                    .build();
        } catch( Exception e ) {
            throw new IllegalArgumentException("Session id " + sessionId + " is not valid");
        }
    }

    private static LocalTime getFromIracingSessionTime(Object iRacingSessionTime) {
        // iRacing session time is seconds as double
        return LocalTime.ofNanoOfDay((long)((Double)iRacingSessionTime * 1000));
    }

    private static Duration getFromIracingDuration(Object iRacingDuration) {
        // iRacing lap, repair and tow times are seconds as float
        return Duration.ofNanos((long)((Double)iRacingDuration * 1000));
    }

    private static SessionId getFromClientString(Object clientSessionId) {
        return parseClientSessionId((String)clientSessionId);
    }

}
