package de.bausdorf.simcacing.tt.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.bausdorf.simcacing.tt.web.model.DoubleFormatter;
import de.bausdorf.simcacing.tt.web.model.DurationFormatter;
import de.bausdorf.simcacing.tt.web.model.IntegerFormatter;
import de.bausdorf.simcacing.tt.web.model.LocalDateFormatter;
import de.bausdorf.simcacing.tt.web.model.LocalTimeFormatter;
import de.bausdorf.simcacing.tt.web.model.ScheduleDriverOptionTypeFormatter;

@Configuration
public class SpringWebConfig implements WebMvcConfigurer {

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatter(new DurationFormatter());
		registry.addFormatter(new LocalTimeFormatter());
		registry.addFormatter(new DoubleFormatter());
		registry.addFormatter(new IntegerFormatter());
		registry.addFormatter(new LocalDateFormatter());
		registry.addFormatter(new ScheduleDriverOptionTypeFormatter());
	}
}
