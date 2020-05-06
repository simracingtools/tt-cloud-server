package de.bausdorf.simcacing.tt.web.security;

import java.util.Date;

import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Component;

@Component
public class TokenRepository extends InMemoryTokenRepositoryImpl {

	@Override
	public synchronized void createNewToken(PersistentRememberMeToken token) {
		super.createNewToken(token);
	}

	@Override
	public synchronized void updateToken(String series, String tokenValue, Date lastUsed) {
		super.updateToken(series, tokenValue, lastUsed);
	}

	@Override
	public synchronized PersistentRememberMeToken getTokenForSeries(String seriesId) {
		return super.getTokenForSeries(seriesId);
	}

	@Override
	public synchronized void removeUserTokens(String username) {
		super.removeUserTokens(username);
	}
}
