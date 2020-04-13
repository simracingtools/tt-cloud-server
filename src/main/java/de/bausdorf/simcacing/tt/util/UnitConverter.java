package de.bausdorf.simcacing.tt.util;

public class UnitConverter {

    public static final double KG_TO_L = 1.3564471935108;
    public static final double GAL_TO_L = 3.785411784;
    public static final double IGAL_TO_L = 4.5460902819948;

    private UnitConverter() {
        super();
    }

    public static double toLiters(double value, String unit) {
        switch(unit) {
            case "g": return value * GAL_TO_L;
            case "i": return value * IGAL_TO_L;
            case "k": return value * KG_TO_L;
            case "l": return value;
            default: throw new IllegalArgumentException("Unknown unit type: " + unit);
        }
    }
}
