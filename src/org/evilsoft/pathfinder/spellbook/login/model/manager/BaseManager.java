package org.evilsoft.pathfinder.spellbook.login.model.manager;

import com.squareup.otto.BusProvider;

/**
 * Base manager handles bus related functions.
 * 
 * @author Trey Robinson
 * 
 */
public class BaseManager {

	protected void postEvent(Object event) {
		BusProvider.getInstance().post(event);
	}
}
