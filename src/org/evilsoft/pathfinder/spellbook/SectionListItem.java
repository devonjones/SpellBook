package org.evilsoft.pathfinder.spellbook;

/**
 * Item definition including the section.
 */
public class SectionListItem {
	public Object item;
	public String section;

	public Object getItem() {
		return item;
	}

	public void setItem(Object item) {
		this.item = item;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	@Override
	public String toString() {
		return item.toString();
	}

}
