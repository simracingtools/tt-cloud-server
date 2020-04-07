package de.bausdorf.simcacing.tt.live.clientapi;

public class MessageConstants {

    public static class MessageType {
        public static final String LAPDATA_NAME = "lapdata";
        public static final String SESSION_INFO_NAME = "sessionInfo";
        public static final String RUN_DATA_NAME = "runData";
        public static final String EVENTDATA_NAME = "event";
        public static final String SYNCDATA_NAME = "syncData";

        private MessageType() {}
    }

    public static class Message {
        public static final String VERSION = "version";
        public static final String SESSION_ID = "sessionId";
        public static final String TEAM_ID = "teamId";
        public static final String CLIENT_ID = "clientId";
        public static final String PAYLOAD = "payload";

        private Message() {}
    }

    public static class LapData {
        public static final String LAP = "lap";
        public static final String STINT_LAP = "stintLap";
        public static final String STINT_COUNT = "stintCount";
        public static final String DRIVER = "driver";
        public static final String LAP_TIME = "laptime"; //.laptime / 86400
        public static final String FUEL_LEVEL = "fuelLevel";
        public static final String TRACK_TEMP = "trackTemp";
        public static final String SESSION_TIME = "sessionTime";

        private LapData() {}
    }
    
    public static class RunData {
        public static final String FUEL_LEVEL = "fuelLevel";
        public static final String FLAGS = "flags";
        public static final String SESSION_TIME = "sessionTime";
        public static final String EST_LAP_TIME = "estLaptime";

        private RunData() {}
    }
    
    public static class EventData {
        public static final String SESSION_TIME = "sessionTime";
        public static final String TRACK_LOCATION = "trackLocation";
        public static final String FLAGS = "flags";
        public static final String TOW_TIME = "towingTime";
        public static final String REPAIR_TIME = "repairTime";
        public static final String OPT_REPAIR_TIME = "optRepairTime";

        private EventData() {}
    }
    
    public static class SessionData {
        public static final String TRACK_NAME = "track";
        public static final String SESSION_ID = "sessionId";
        public static final String SESSION_LAPS = "sessionLaps";
        public static final String SESSION_DURATION = "sessionTime";
        public static final String SESSION_TYPE = "sessionType";
        public static final String TEAM_NAME = "teamName";
        public static final String CAR_NAME = "car";
        public static final String MAX_FUEL = "maxFuel";

        private SessionData() {}
    }
    
    public static class SyncData {
        public static final String SESSION_TIME = "sessionTime";
        public static final String CLIENT_ID = "irid";
        public static final String IN_CAR = "isInCar";

        private SyncData() {}
    }

    private MessageConstants() {}
}
