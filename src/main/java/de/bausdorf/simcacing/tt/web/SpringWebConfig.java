package de.bausdorf.simcacing.tt.web;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.bausdorf.simcacing.tt.web.model.DoubleFormatter;
import de.bausdorf.simcacing.tt.web.model.DurationFormatter;
import de.bausdorf.simcacing.tt.web.model.IntegerFormatter;
import de.bausdorf.simcacing.tt.web.model.LocalDateFormatter;
import de.bausdorf.simcacing.tt.web.model.LocalDateTimeFormatter;
import de.bausdorf.simcacing.tt.web.model.LocalTimeFormatter;
import de.bausdorf.simcacing.tt.web.model.ScheduleDriverOptionTypeFormatter;
import de.bausdorf.simcacing.tt.web.model.SubscriptionTypeFormatter;
import de.bausdorf.simcacing.tt.web.model.TtUserTypeFormatter;
import de.bausdorf.simcacing.tt.web.model.ZonedDateTimeFormatter;

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
		registry.addFormatter(new LocalDateTimeFormatter());
		registry.addFormatter(new ZonedDateTimeFormatter());
		registry.addFormatter(new TtUserTypeFormatter());
		registry.addFormatter(new SubscriptionTypeFormatter());
	}
}
