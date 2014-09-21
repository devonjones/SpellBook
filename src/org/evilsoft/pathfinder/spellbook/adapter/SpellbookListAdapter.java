package org.evilsoft.pathfinder.spellbook.adapter;

import java.util.List;

import org.evilsoft.pathfinder.spellbook.R;
import org.evilsoft.pathfinder.spellbook.SpellListItem;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpellbookListAdapter extends ArrayAdapter<SpellListItem> {

	private final SpellListItem[] items;

	public SpellbookListAdapter(final Context context,
			final int textViewResourceId, final SpellListItem[] items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			final LayoutInflater vi = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.class_spell_list_item, null);
		}
		final SpellListItem currentItem = items[position];
		if (currentItem != null) {
			final TextView nameView = (TextView) view
					.findViewById(R.id.spell_list_name);
			if (nameView != null) {
				nameView.setText(currentItem.getName());
			}
			final TextView descriptorView = (TextView) view
					.findViewById(R.id.spell_list_descriptor);
			if (descriptorView != null) {
				descriptorView.setText(currentItem.buildSchoolLine());
			}
			final TextView descriptionView = (TextView) view
					.findViewById(R.id.spell_list_description);
			if (descriptionView != null) {
				descriptionView.setText(Html.fromHtml("<B>Description:</B> "
						+ currentItem.getDescription()));
			}
		}
		return view;
	}

	public static SpellListItem[] toSpellListItemArray(List<SpellListItem> input) {
		if (input.size() == 0) {
			return null;
		}
		SpellListItem[] retarr = new SpellListItem[input.size()];
		for (int i = 0; i < input.size(); i++) {
			retarr[i] = input.get(i);
		}
		return retarr;
	}
}
