package de.bausdorf.simcacing.tt.util;

import static org.assertj.core.api.Assertions.assertThat;

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
}
