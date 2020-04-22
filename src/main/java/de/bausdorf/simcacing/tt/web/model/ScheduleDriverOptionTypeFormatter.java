package de.bausdorf.simcacing.tt.web.model;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import de.bausdorf.simcacing.tt.planning.model.ScheduleDriverOptionType;

public class ScheduleDriverOptionTypeFormatter implements Formatter<ScheduleDriverOptionType> {

	@Override
	public ScheduleDriverOptionType parse(String s, Locale locale) throws ParseException {
		return ScheduleDriverOptionType.fromCssClass(s);
	}

	@Override
	public String print(ScheduleDriverOptionType scheduleDriverOptionType, Locale locale) {
		return scheduleDriverOptionType.cssClassName();
	}
}
