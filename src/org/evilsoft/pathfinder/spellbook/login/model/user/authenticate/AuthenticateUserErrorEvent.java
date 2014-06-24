package org.evilsoft.pathfinder.spellbook.login.model.user.authenticate;

import org.evilsoft.pathfinder.spellbook.login.event.ErrorEvent;

import com.parse.ParseException;

public class AuthenticateUserErrorEvent extends ErrorEvent {

	public AuthenticateUserErrorEvent(ParseException exception) {
		super(exception);
	}

}
