package org.engine.utils;

import org.engine.production.entity.Users;
import org.springframework.security.core.context.SecurityContextHolder;

public class GenericUtils {
	
	private GenericUtils() {
	    throw new IllegalStateException("GenericUtils class");
	  }
	/**
	 * WARNING: for JWT token it's not working. We need to get the user from the HEADER.
	 * This method is to get the current loggedin user in the system using his token
	 * */
	public static Users getLoggedInUser() {
		return (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

}
