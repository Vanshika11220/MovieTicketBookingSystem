package com.dmg.MovieTicketBookingSystem.security;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.dmg.MovieTicketBookingSystem.common.ApiException;
import com.dmg.MovieTicketBookingSystem.domain.enums.Role;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RoleInterceptor implements HandlerInterceptor {
	private final UserContext userContext;

	public RoleInterceptor(UserContext userContext) {
		this.userContext = userContext;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (!(handler instanceof HandlerMethod handlerMethod)) {
			return true;
		}

		RoleRequired required = handlerMethod.getMethodAnnotation(RoleRequired.class);
		if (required == null) {
			required = handlerMethod.getBeanType().getAnnotation(RoleRequired.class);
		}
		if (required == null) {
			return true;
		}

		String userIdHeader = request.getHeader("X-User-Id");
		String roleHeader = request.getHeader("X-User-Role");
		if (userIdHeader == null || roleHeader == null) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "X-User-Id and X-User-Role headers are required");
		}

		Role role = Role.valueOf(roleHeader.trim().toUpperCase());
		if (Arrays.stream(required.value()).noneMatch(role::equals)) {
			throw new ApiException(HttpStatus.FORBIDDEN, "Role " + role + " cannot access this endpoint");
		}

		userContext.setCurrentUser(new CurrentUser(Long.valueOf(userIdHeader), role));
		return true;
	}
}
