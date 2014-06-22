package org.evilsoft.pathfinder.spellbook;

import org.evilsoft.pathfinder.reference.api.contracts.SpellContract;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ClassSpellListAdapter extends DisplayListAdapter {
	public ClassSpellListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		View view = convertView;
		moveCursor(index);
		if (view == null) {
			final LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.class_spell_list_item, null);
		}
		final SpellListItem currentItem = (SpellListItem) buildItem(c);
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

	@Override
	public Object buildItem(Cursor c) {
		SpellListItem sla = new SpellListItem();
		sla.setId(SpellContract.SpellListContractUtils.getId(c));
		sla.setSource(SpellContract.SpellListContractUtils.getSource(c));
		sla.setType(SpellContract.SpellListContractUtils.getType(c));
		sla.setSubType(SpellContract.SpellListContractUtils.getSubType(c));
		sla.setName(SpellContract.SpellListContractUtils.getName(c));
		sla.setContentUrl(SpellContract.SpellListContractUtils.getContentUrl(c));
		sla.setDescription(SpellContract.SpellListContractUtils
				.getDescription(c));
		sla.setSchool(SpellContract.SpellListContractUtils.getSchool(c));
		sla.setSubschool(SpellContract.SpellListContractUtils.getSubschool(c));
		sla.setDescriptor(SpellContract.SpellListContractUtils.getDescriptor(c));
		sla.setComponents(SpellContract.SpellListContractUtils.getComponents(c));
		sla.setClassName(SpellContract.SpellListContractUtils.getClass(c));
		sla.setLevel(SpellContract.SpellListContractUtils.getLevel(c));
		sla.setMagicType(SpellContract.SpellListContractUtils.getMagicType(c));
		return sla;
	}
}
