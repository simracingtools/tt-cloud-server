package de.bausdorf.simcacing.tt.web.model;

import java.util.Locale;

import org.springframework.format.Formatter;

import de.bausdorf.simcacing.tt.web.security.SubscriptionType;

public class SubscriptionTypeFormatter implements Formatter<SubscriptionType> {

	@Override
	public SubscriptionType parse(String s, Locale locale) {
		return SubscriptionType.valueOf(s);
	}

	@Override
	public String print(SubscriptionType subscriptionType, Locale locale) {
		return subscriptionType.name();
	}
}
