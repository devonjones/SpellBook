package org.evilsoft.pathfinder.spellbook.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.evilsoft.pathfinder.reference.api.contracts.SpellContract;
import org.evilsoft.pathfinder.spellbook.R;
import org.evilsoft.pathfinder.spellbook.SpellListItem;
import org.evilsoft.pathfinder.spellbook.api.BaseApiHelper;
import org.evilsoft.pathfinder.spellbook.data.SpellBookSpellContract.SpellbookSpellEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

public class EditSpellListAdapter extends AbstractSpellListAdapter {
	private Long spellbookId;
	private Set<String> selectedSpells;

	public EditSpellListAdapter(Context context, Cursor c, Long spellbookId,
			Set<String> selectedSpells) {
		super(context, c);
		this.spellbookId = spellbookId;
		this.selectedSpells = selectedSpells;
	}

	public View inflate(int index, View convertView, ViewGroup parent) {
		final LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return vi.inflate(R.layout.edit_spell_list_item, null);
	}

	public View localGetView(int index, View convertView, ViewGroup parent,
			View local, final SpellListItem currentItem) {
		final CheckBox cb = (CheckBox) local.findViewById(R.id.spell_selected);
		if (selectedSpells.contains(currentItem.getContentUrl())) {
			cb.setChecked(true);
		} else {
			cb.setChecked(false);
		}
		cb.refreshDrawableState();
		cb.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (cb.isChecked()) {
					ContentValues spellValues = new ContentValues();
					spellValues.put(SpellbookSpellEntry.COLUMN_LEVEL,
							currentItem.getLevel());
					spellValues.put(SpellbookSpellEntry.COLUMN_SPELLBOOK_ID,
							spellbookId);
					spellValues.put(SpellbookSpellEntry.COLUMN_URL,
							currentItem.getContentUrl());
					spellValues.put(SpellbookSpellEntry.COLUMN_NAME,
							currentItem.getName());
					context.getContentResolver().insert(
							SpellbookSpellEntry
									.buildSpellbookSpellsUri(spellbookId),
							spellValues);
				} else {
					List<String> spellValues = new ArrayList<String>();
					spellValues.add(spellbookId.toString());
					spellValues.add(currentItem.getContentUrl());
					context.getContentResolver().delete(
							SpellbookSpellEntry
									.buildSpellbookSpellsUri(spellbookId),
							"spellbook_id = ? AND url = ?",
							BaseApiHelper.toStringArray(spellValues));
				}
			}
		});
		return local;
	}

	public boolean isClassSpellList() {
		return true;
	}
}
