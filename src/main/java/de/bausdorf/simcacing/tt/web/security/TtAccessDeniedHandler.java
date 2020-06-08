package de.bausdorf.simcacing.tt.web.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TtAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
			throws IOException {

		Authentication auth
				= SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			log.warn("User: {}({}) attempted to access the protected URL: {}",
					auth.getName(), auth.getAuthorities(), request.getRequestURI());
		}

		response.sendRedirect(request.getContextPath() + "/index?error=Access%20denied%20to%20" + request.getRequestURI());
	}
}
