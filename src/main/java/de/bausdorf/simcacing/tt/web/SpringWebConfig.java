package de.bausdorf.simcacing.tt.web;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import de.bausdorf.simcacing.tt.web.model.DoubleFormatter;
import de.bausdorf.simcacing.tt.web.model.DurationFormatter;
import de.bausdorf.simcacing.tt.web.model.IntegerFormatter;
import de.bausdorf.simcacing.tt.web.model.LocalTimeFormatter;

@Configuration
public class SpringWebConfig implements WebMvcConfigurer, ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void addFormatters(FormatterRegistry registry) {

		registry.addFormatter(new DurationFormatter());
		registry.addFormatter(new LocalTimeFormatter());
		registry.addFormatter(new DoubleFormatter());
		registry.addFormatter(new IntegerFormatter());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
