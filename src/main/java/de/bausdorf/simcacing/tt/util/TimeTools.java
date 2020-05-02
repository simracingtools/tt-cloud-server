package de.bausdorf.simcacing.tt.util;

import de.bausdorf.simcacing.tt.live.model.client.LapData;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class TimeTools {

    private static final List<String> TIME_PATTERNS = new ArrayList<>();

    static {
        TIME_PATTERNS.add("HH:mm:ss");
        TIME_PATTERNS.add("HH:mm:s");
        TIME_PATTERNS.add("HH:m:ss");
        TIME_PATTERNS.add("HH:m:s");
        TIME_PATTERNS.add("H:mm:ss");
        TIME_PATTERNS.add("H:mm:s");
        TIME_PATTERNS.add("H:m:ss");
        TIME_PATTERNS.add("H:m:s");
        TIME_PATTERNS.add("HH:mm:ss.S");
        TIME_PATTERNS.add("HH:mm:ss.SS");
        TIME_PATTERNS.add("HH:mm:ss.SSS");
        TIME_PATTERNS.add("HH:m:ss.S");
        TIME_PATTERNS.add("HH:m:ss.SS");
        TIME_PATTERNS.add("HH:m:ss.SSS");
        TIME_PATTERNS.add("H:mm:ss.S");
        TIME_PATTERNS.add("H:mm:ss.SS");
        TIME_PATTERNS.add("H:mm:ss.SSS");
        TIME_PATTERNS.add("H:m:ss.S");
        TIME_PATTERNS.add("H:m:ss.SS");
        TIME_PATTERNS.add("H:m:ss.SSS");
        TIME_PATTERNS.add("HH:mm:s.S");
        TIME_PATTERNS.add("HH:mm:s.SS");
        TIME_PATTERNS.add("HH:mm:s.SSS");
        TIME_PATTERNS.add("HH:m:s.S");
        TIME_PATTERNS.add("HH:m:s.SS");
        TIME_PATTERNS.add("HH:m:s.SSS");
        TIME_PATTERNS.add("H:mm:s.S");
        TIME_PATTERNS.add("H:mm:s.SS");
        TIME_PATTERNS.add("H:mm:s.SSS");
        TIME_PATTERNS.add("H:m:s.S");
        TIME_PATTERNS.add("H:m:s.SS");
        TIME_PATTERNS.add("H:m:s.SSS");
    }

    private TimeTools() {
        super();
    }

    public static Duration getAverageLapDuration(Stream<LapData> stream) {
        return Duration.ofMillis(
                (long)stream.mapToDouble(s -> s.getLapTime().toMillis()).average().orElse(0.0D)
        );
    }

    public static Duration durationFromPattern(String timestring, String pattern) {
        LocalTime time = LocalTime.parse(timestring, DateTimeFormatter.ofPattern(pattern));
        return Duration.between(LocalTime.MIN, time);
    }

    public static String longDurationString(Duration duration) {
        long h = duration.toHours();
        long m = duration.toMinutes() - (h * 60);
        double S = ((double)duration.toMillis() / 1000) - (m * 60) - (h * 3600);
        if (S < 0) {
            S = 0.0;
        }
        return String.format("%d:%02d:%06.3f", h, m, S);
    }

    public static String longDurationDeltaString(Duration d1, Duration d2) {
        Duration delta = d1.minus(d2);
        String prefix = delta.isNegative() ? "-" : "";
        return prefix + longDurationString(delta.isNegative() ? d2.minus(d1) : delta);
    }

    public static String shortDurationString(Duration duration) {
        long h = duration.toHours();
        long m = duration.toMinutes() - (h * 60);
        long S = duration.getSeconds() - (m * 60) - (h * 3600);
        return String.format("%d:%02d:%02d", h, m, S);
    }

    public static LocalTime timeFromString(String time) {
        if( time == null ) {
            return LocalTime.MIN;
        }
        String timestring = normalizeTimeString(time);
        for (String pattern : TIME_PATTERNS) {
            try {
                return LocalTime.parse(timestring,
                        DateTimeFormatter.ofPattern(pattern));
            } catch (Exception e) {
                log.debug("Pattern {} not successful", pattern);
            }
        }
        log.warn("Timestring {} could not be parsed", time);
        return LocalTime.MIN;
    }

    public static Duration durationFromString(String timestring) {
        if (timestring == null) {
            return Duration.ZERO;
        }
        Duration duration = Duration.ZERO;
        String[] timeParts = timestring.split(":");
        if (timeParts.length == 3) {
            duration = duration.plusHours(Long.parseLong(timeParts[0]));
            duration = duration.plusMinutes(Long.parseLong(timeParts[1]));
            duration = duration.plusMillis((long)Double.parseDouble(timeParts[2]) * 1000);
        } else if (timeParts.length == 2) {
            if (timeParts[1].indexOf('.') > -1) {
                duration = duration.plusMinutes(Long.parseLong(timeParts[0]));
                duration = duration.plusMillis((long)Double.parseDouble(timeParts[1]) * 1000);
            } else {
                duration = duration.plusHours(Long.parseLong(timeParts[0]));
                duration = duration.plusMinutes(Long.parseLong(timeParts[1]));
            }
        }
        return duration;
    }

    public static String normalizeTimeString(String timestring) {
        String result = timestring;
        int colonPos = timestring.indexOf(':');
        if( colonPos == -1 ) {
            result = "00:00:" + timestring;
        } else if( timestring.indexOf(':', colonPos + 1) == -1 ) {
            result = "00:" + timestring;
        }

        if (timestring.contains(",")) {
            result = result.replace(",", ".");
        }
        return result;
    }
}
