package com.dmg.MovieTicketBookingSystem.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dmg.MovieTicketBookingSystem.domain.enums.Role;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleRequired {
	Role[] value();
}
