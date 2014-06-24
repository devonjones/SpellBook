package org.evilsoft.pathfinder.spellbook.login.model.user.authenticate;

import org.evilsoft.pathfinder.spellbook.login.event.ErrorEvent;

import com.parse.ParseException;

/**
 * Event for forgot password errors.
 * 
 * @author Trey Robinson
 * 
 */
public class UserForgotPasswordErrorEvent extends ErrorEvent {

	public UserForgotPasswordErrorEvent(ParseException exception) {
		super(exception);
	}

}
