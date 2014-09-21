package org.evilsoft.pathfinder.spellbook.adapter;

import org.evilsoft.pathfinder.spellbook.R;
import org.evilsoft.pathfinder.spellbook.SpellListItem;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ClassSpellListAdapter extends AbstractSpellListAdapter {
	public ClassSpellListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View inflate(int index, View convertView, ViewGroup parent) {
		final LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return vi.inflate(R.layout.class_spell_list_item, null);
	}

	@Override
	public View localGetView(int index, View convertView, ViewGroup parent,
			View local, SpellListItem currentItem) {
		return local;
	}

	public boolean isClassSpellList() {
		return true;
	}
}
