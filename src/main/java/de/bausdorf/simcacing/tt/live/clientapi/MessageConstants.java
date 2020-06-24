package de.bausdorf.simcacing.tt.live.clientapi;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
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

public class MessageConstants {

    public static class MessageType {
        public static final String LAPDATA_NAME = "lapdata";
        public static final String SESSION_INFO_NAME = "sessionInfo";
        public static final String RUN_DATA_NAME = "runData";
        public static final String EVENTDATA_NAME = "event";
        public static final String SYNCDATA_NAME = "syncData";
        public static final String PING_NAME = "ping";

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
        public static final String DRIVER = "driver";
        public static final String DRIVER_ID = "driverId";
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
        public static final String SESSION_TOD = "sessionToD";
        public static final String EST_LAP_TIME = "estLaptime";
        public static final String LAP_NO = "lapNo";
        public static final String TIME_IN_LAP = "timeInLap";
        public static final String TIME_REMAINING = "sessionTimeRemain";
        public static final String LAPS_REMAINING = "sessionLapsRemain";
        public static final String SESSION_STATE = "sessionState";

		private RunData() {}
    }
    
    public static class EventData {
        public static final String SESSION_TIME = "sessionTime";
        public static final String SESSION_TOD = "sessionToD";
        public static final String TRACK_LOCATION = "trackLocation";
        public static final String FLAGS = "flags";
        public static final String TOW_TIME = "towingTime";
        public static final String REPAIR_TIME = "repairTime";
        public static final String OPT_REPAIR_TIME = "optRepairTime";
        public static final String SERVICE_FLAGS = "serviceFlags";

        private EventData() {}
    }
    
    public static class SessionData {
        public static final String TRACK_NAME = "track";
        public static final String TRACK_ID = "trackId";
        public static final String SESSION_ID = "sessionId";
        public static final String SESSION_LAPS = "sessionLaps";
        public static final String SESSION_DURATION = "sessionTime";
        public static final String SESSION_TYPE = "sessionType";
        public static final String TEAM_NAME = "teamName";
        public static final String CAR_NAME = "car";
        public static final String CAR_ID = "carId";
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
