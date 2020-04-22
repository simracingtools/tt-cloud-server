package de.bausdorf.simcacing.tt.web.model;

import java.text.ParseException;
import java.time.Duration;
import java.util.Locale;

import org.springframework.format.Formatter;

import de.bausdorf.simcacing.tt.util.TimeTools;

public class DurationFormatter implements Formatter<Duration> {

	@Override
	public Duration parse(String s, Locale locale) throws ParseException {
		return TimeTools.durationFromString(s);
	}

	@Override
	public String print(Duration duration, Locale locale) {
		return TimeTools.shortDurationString(duration);
	}
}
