package de.bausdorf.simcacing.tt.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.bausdorf.simcacing.tt.stock.DriverRepository;
import de.bausdorf.simcacing.tt.web.model.DoubleFormatter;
import de.bausdorf.simcacing.tt.web.model.DurationFormatter;
import de.bausdorf.simcacing.tt.web.model.IntegerFormatter;
import de.bausdorf.simcacing.tt.web.model.LocalDateFormatter;
import de.bausdorf.simcacing.tt.web.model.LocalTimeFormatter;

@Configuration
public class SpringWebConfig implements WebMvcConfigurer, ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Autowired
	DriverRepository driverRepository;

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatter(new DurationFormatter());
		registry.addFormatter(new LocalTimeFormatter());
		registry.addFormatter(new DoubleFormatter());
		registry.addFormatter(new IntegerFormatter());
		registry.addFormatter(new LocalDateFormatter());
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
