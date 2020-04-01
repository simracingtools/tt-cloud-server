package de.bausdorf.simcacing.tt.model;

import java.util.ArrayList;
import java.util.List;

public enum FlagType {
    CHECKERED,
    WHITE,
    GREEN,
    YELLOW,
    RED,
    BLUE,
    DEBRIS,
    CROSSED,
    YELLOW_WAVED,
    ONE_TO_GREEN,
    GREEN_HELD,
    START_SET,
    TEN_TO_GO,
    FIVE_TO_GO,
    RANDOM_WAVED,
    CAUTION,
    CAUTION_WAVED,
    BLACK,
    DQ,
    SERVICEABLE,
    FURLED,
    REPAIR,
    START_HIDDEN,
    START_READY;

    public static List<FlagType> fromIrBitmask(long sessionFlags) {
        List<FlagType> flags = new ArrayList<>();

        if ((sessionFlags & 0x00000001L) > 0) {
            flags.add(CHECKERED);
        } else if ((sessionFlags & 0x00000002L) > 0) {
            flags.add(WHITE);
        } else if ((sessionFlags & 0x00000004L) > 0) {
            flags.add(GREEN);
        } else if ((sessionFlags & 0x00000008L) > 0) {
            flags.add(YELLOW);
        } else if ((sessionFlags & 0x00000010L) > 0) {
            flags.add(RED);
        } else if ((sessionFlags & 0x00000020L) > 0) {
            flags.add(BLUE);
        } else if ((sessionFlags & 0x00000040L) > 0) {
            flags.add(DEBRIS);
        } else if ((sessionFlags & 0x00000080L) > 0) {
            flags.add(CROSSED);
        } else if ((sessionFlags & 0x00000100L) > 0) {
            flags.add(YELLOW_WAVED);
        } else if ((sessionFlags & 0x00000200L) > 0) {
            flags.add(ONE_TO_GREEN);
        } else if ((sessionFlags & 0x00000400L) > 0) {
            flags.add(GREEN_HELD);
        } else if ((sessionFlags & 0x00000800L) > 0) {
            flags.add(TEN_TO_GO);
        } else if ((sessionFlags & 0x00001000L) > 0) {
            flags.add(FIVE_TO_GO);
        } else if ((sessionFlags & 0x00002000L) > 0) {
            flags.add(RANDOM_WAVED);
        } else if ((sessionFlags & 0x00004000L) > 0) {
            flags.add(CAUTION);
        } else if ((sessionFlags & 0x00008000L) > 0) {
            flags.add(CAUTION_WAVED);
        } else if ((sessionFlags & 0x00010000L) > 0) {
            flags.add(BLACK);
        } else if ((sessionFlags & 0x00020000L) > 0) {
            flags.add(DQ);
        } else if ((sessionFlags & 0x00040000L) > 0) {
            flags.add(SERVICEABLE);
        } else if ((sessionFlags & 0x00080000L) > 0) {
            flags.add(FURLED);
        } else if ((sessionFlags & 0x0010000L) > 0) {
            flags.add(REPAIR);
        } else if ((sessionFlags & 0x10000000L) > 0) {
            flags.add(START_HIDDEN);
        } else if ((sessionFlags & 0x20000000L) > 0) {
            flags.add(START_READY);
        } else if ((sessionFlags & 0x40000000L) > 0) {
            flags.add(START_SET);
        }
        return flags;
    }
}
