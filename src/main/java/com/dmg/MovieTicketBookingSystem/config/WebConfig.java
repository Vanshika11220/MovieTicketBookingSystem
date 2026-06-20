package com.dmg.MovieTicketBookingSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dmg.MovieTicketBookingSystem.security.RoleInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	private final RoleInterceptor roleInterceptor;

	public WebConfig(RoleInterceptor roleInterceptor) {
		this.roleInterceptor = roleInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(roleInterceptor);
	}
}
