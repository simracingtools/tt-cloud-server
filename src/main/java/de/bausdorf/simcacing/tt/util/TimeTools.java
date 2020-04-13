package de.bausdorf.simcacing.tt.util;

import de.bausdorf.simcacing.tt.live.model.LapData;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class TimeTools {

    private TimeTools() {
        super();
    }

    public static Duration getAverageLapDuration(Stream<LapData> stream) {
        return Duration.ofMillis(
                (long)stream.mapToDouble(s -> s.getLapTime().toMillis()).average().getAsDouble()
        );
    }

    public static ZonedDateTime zonedDateTimeFromPattern(String timestring, String pattern) {
        LocalDateTime localTime = LocalDateTime.parse(timestring, DateTimeFormatter.ofPattern(pattern));
        return ZonedDateTime.of(localTime, ZoneId.of("CET"));
    }

    public static Duration durationFromPattern(String timestring, String pattern) {
        LocalTime time = LocalTime.parse(timestring, DateTimeFormatter.ofPattern(pattern));
        Duration duration = Duration.ofHours(time.getHour());
        duration.plusMinutes(time.getMinute());
        duration.plusSeconds(time.getSecond());
        duration.plusNanos(time.getNano());
        return duration;
    }
}
