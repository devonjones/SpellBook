package org.evilsoft.pathfinder.spellbook.login.model.user.authenticate;

import com.parse.ParseUser;

public class AuthenticateUserSuccessEvent {

	public ParseUser user;

	public AuthenticateUserSuccessEvent(ParseUser user) {
		super();

		this.user = user;
	}

}
