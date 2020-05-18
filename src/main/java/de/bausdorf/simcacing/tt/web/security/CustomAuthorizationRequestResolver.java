package de.bausdorf.simcacing.tt.web.security;

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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
	private final OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver;

	public CustomAuthorizationRequestResolver(
			ClientRegistrationRepository clientRegistrationRepository) {

		this.defaultAuthorizationRequestResolver =
				new DefaultOAuth2AuthorizationRequestResolver(
						clientRegistrationRepository, "/oauth2/authorization");
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
		OAuth2AuthorizationRequest authorizationRequest =
				this.defaultAuthorizationRequestResolver.resolve(request);

		return authorizationRequest != null ?
		customAuthorizationRequest(authorizationRequest) :
		null;
	}

	@Override
	public OAuth2AuthorizationRequest resolve(
			HttpServletRequest request, String clientRegistrationId) {

		OAuth2AuthorizationRequest authorizationRequest =
				this.defaultAuthorizationRequestResolver.resolve(
						request, clientRegistrationId);

		return authorizationRequest != null ?
		customAuthorizationRequest(authorizationRequest) :
		null;
	}

	private OAuth2AuthorizationRequest customAuthorizationRequest(
			OAuth2AuthorizationRequest authorizationRequest) {

		Map<String, Object> additionalParameters =new LinkedHashMap<>(authorizationRequest.getAdditionalParameters());
		additionalParameters.put("access_type", "offline");

		return OAuth2AuthorizationRequest.from(authorizationRequest)
				.additionalParameters(additionalParameters)
				.build();
	}
}
