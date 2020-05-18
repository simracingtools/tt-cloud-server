package de.bausdorf.simcacing.tt.util;

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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeToolsTest {

	@Test
	public void timeFromStringTest() {
		LocalTime time = TimeTools.timeFromString("33.33");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();

		time = TimeTools.timeFromString("33.333");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();

		time = TimeTools.timeFromString("1:15.209");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();

		time = TimeTools.timeFromString("1:15.20");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();

		time = TimeTools.timeFromString("1:15.2");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();

		time = TimeTools.timeFromString("10:15.209");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();

		time = TimeTools.timeFromString("10:5.209");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();

		time = TimeTools.timeFromString("10:5.20");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();

		time = TimeTools.timeFromString("10:5.2");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();

		time = TimeTools.timeFromString("1:0:5.209");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();

		time = TimeTools.timeFromString("10:0:5.20");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();

		time = TimeTools.timeFromString("0:5.2");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();

		time = TimeTools.timeFromString("0:5,2");
		assertThat(time.isAfter(LocalTime.MIN)).isTrue();
	}

	@Test
	public void testDeltaDurationString() {
		Duration d1 = Duration.ofSeconds(721);
		Duration d2 = Duration.ofSeconds(859);

		String delta = TimeTools.longDurationDeltaString(d1, d2);
		assertThat(delta.startsWith("-")).isTrue();
		delta = TimeTools.longDurationDeltaString(d2, d1);
		assertThat(delta.startsWith("-")).isFalse();
	}

	@Test
	public void testShortDurationString() {
		Duration d1 = Duration.ofSeconds(721);

		String delta = TimeTools.shortDurationString(d1);
		assertThat(delta.startsWith("-")).isFalse();
		d1 = d1.multipliedBy(-1);
		delta = TimeTools.shortDurationString(d1);
		assertThat(delta.startsWith("-")).isTrue();
	}

	@Test
	public void zoneIdFormats() {
		ZoneId zoneId = ZoneId.of("GMT+0");

		log.info("{}", zoneId.toString());
		log.info("{}", zoneId.getId());
		log.info("{}", zoneId.getDisplayName(TextStyle.SHORT, Locale.US));
		log.info("{}", zoneId.getDisplayName(TextStyle.NARROW, Locale.US));
		log.info("{}", zoneId.getDisplayName(TextStyle.FULL, Locale.US));
		log.info("{}", TimeTools.toShortZoneId(zoneId));
	}

	@Test
	public void zonedTimeConversions() {
		ZonedDateTime now = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("GMT+2"));

		log.info("{}", now.toString());

		log.info(now.withZoneSameInstant(ZoneId.of("GMT")).toString());
	}
}
