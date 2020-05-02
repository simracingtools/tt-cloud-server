package de.bausdorf.simcacing.tt.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

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
}
