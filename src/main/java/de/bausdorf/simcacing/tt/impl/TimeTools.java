package de.bausdorf.simcacing.tt.impl;

import de.bausdorf.simcacing.tt.model.LapData;

import java.time.Duration;
import java.time.LocalDateTime;
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

    public static Duration durationFromPattern(String timestring, String pattern) {
        LocalDateTime time = LocalDateTime.parse(timestring, DateTimeFormatter.ofPattern(pattern));
        Duration duration = Duration.ofHours(time.getHour());
        duration.plusMinutes(time.getMinute());
        duration.plusSeconds(time.getSecond());
        duration.plusNanos(time.getNano());
        return duration;
    }
}
