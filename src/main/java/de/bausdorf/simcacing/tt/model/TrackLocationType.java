package de.bausdorf.simcacing.tt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TrackLocationType {
    OFF_WORLD(-1),
    OFFTRACK(0),
    PIT_STALL(1),
    APPROACHING_PITS(2),
    ONTRACK(3);

    private int irCode;

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
