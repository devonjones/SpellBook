package org.evilsoft.pathfinder.spellbook.api;

import java.util.List;

public class BaseApiHelper {
	public static String[] toStringArray(List<String> input) {
		if (input.size() == 0) {
			return null;
		}
		String[] retarr = new String[input.size()];
		for (int i = 0; i < input.size(); i++) {
			retarr[i] = input.get(i);
		}
		return retarr;
	}

	public static String joinSelectionCriteria(List<String> input) {
		if (input.size() == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < input.size(); i++) {
			if (i > 0) {
				sb.append(" AND ");
			}
			sb.append(input.get(i));
		}
		return sb.toString();
	}
}
