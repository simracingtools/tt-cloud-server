package de.bausdorf.simcacing.tt.live.model.client;

import java.util.ArrayList;
import java.util.List;

public enum FlagType {
    CHECKERED(0x00000001L),
    WHITE(0x00000002L),
    GREEN(0x00000004L),
    YELLOW(0x00000008L),
    RED(0x00000010L),
    BLUE(0x00000020L),
    DEBRIS(0x00000010L),
    CROSSED(0x00000080L),
    YELLOW_WAVED(0x00000100L),
    ONE_TO_GREEN(0x00000200L),
    GREEN_HELD(0x00000400L),
    TEN_TO_GO(0x00000800L),
    FIVE_TO_GO(0x00001000L),
    RANDOM_WAVED(0x00002000L),
    CAUTION(0x00004000L),
    CAUTION_WAVED(0x00008000L),
    BLACK(0x00010000L),
    DQ(0x00020000L),
    SERVICEABLE(0x00040000L),
    SERVICIBLE(0x00040000L), // error - to be fixed in TT-Client
    FURLED(0x00080000L),
    REPAIR(0x0010000L),
    START_HIDDEN(0x10000000L),
    START_READY(0x20000000L),
    START_SET(0x40000000L);

    long bitFlag;

    FlagType(long flag) {
        this.bitFlag = flag;
    }

    public static List<FlagType> fromIrBitmask(long sessionFlags) {
        List<FlagType> flags = new ArrayList<>();

        for (FlagType flagType : FlagType.values()) {
            if ((flagType.bitFlag & sessionFlags) > 0) {
                flags.add(flagType);
            }
        }

        return flags;
    }
}
