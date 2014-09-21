package org.evilsoft.pathfinder.spellbook;

import org.evilsoft.pathfinder.spellbook.sectionlist.SectionListItem;

public class SpellbookListItem extends SectionListItem {
	protected long spellbookId;
	protected String name;
	protected String spellClass;

	public SpellbookListItem(long spellbookId, String name, String spellClass) {
		this.item = name;
		if (spellClass != null) {
			this.item = name + " (" + spellClass + ")";
		}
		this.section = "Spell Books";
		this.spellbookId = spellbookId;
		this.name = name;
		this.spellClass = spellClass;
	}

	public long getSpellbookId() {
		return spellbookId;
	}

	public void setSpellbookId(long spellbookId) {
		this.spellbookId = spellbookId;
	}

	public Object getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getSpellClass() {
		return spellClass;
	}

	public void setSpellClass(String spellClass) {
		this.spellClass = spellClass;
	}
}
