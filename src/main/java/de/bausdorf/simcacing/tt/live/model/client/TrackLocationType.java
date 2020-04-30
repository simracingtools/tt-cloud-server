package de.bausdorf.simcacing.tt.live.model.client;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
public enum TrackLocationType {
    OFF_WORLD(-1, "loc-black"),
    OFFTRACK(0, "loc-yellow"),
    PIT_STALL(1, "loc-blue"),
    APPROACHING_PITS(2, "loc-orange"),
    ONTRACK(3, "loc-green");

    TrackLocationType(int code, String cssClass) {
        this.cssClass = cssClass;
        this.irCode = code;
    }

    private int irCode;
    private String cssClass;

    public static TrackLocationType forIrCode(int code) {
        switch(code) {
            case -1: return OFF_WORLD;
            case 0: return OFFTRACK;
            case 1: return PIT_STALL;
            case 2: return APPROACHING_PITS;
            case 3: return ONTRACK;
            default: throw new IllegalArgumentException("iRacing track location " + code + " not valid");
        }
    }
}
