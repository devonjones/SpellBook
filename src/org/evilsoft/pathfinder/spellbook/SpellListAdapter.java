package org.evilsoft.pathfinder.spellbook;

import org.evilsoft.pathfinder.reference.api.contracts.SpellContract;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SpellListAdapter extends DisplayListAdapter {
	public SpellListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		View view = convertView;
		moveCursor(index);
		if (view == null) {
			final LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.spell_list_item, null);
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
			final TextView classesView = (TextView) view
					.findViewById(R.id.spell_list_classes);
			if (classesView != null) {
				classesView.setText(currentItem.getClasses());
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
		sla.setId(SpellContract.SpellContractUtils.getId(c));
		sla.setSource(SpellContract.SpellContractUtils.getSource(c));
		sla.setType(SpellContract.SpellContractUtils.getType(c));
		sla.setSubType(SpellContract.SpellContractUtils.getSubType(c));
		sla.setName(SpellContract.SpellContractUtils.getName(c));
		sla.setContentUrl(SpellContract.SpellContractUtils.getContentUrl(c));
		sla.setDescription(SpellContract.SpellContractUtils.getDescription(c));
		sla.setSchool(SpellContract.SpellContractUtils.getSchool(c));
		sla.setSubschool(SpellContract.SpellContractUtils.getSubschool(c));
		sla.setDescriptor(SpellContract.SpellContractUtils.getDescriptor(c));
		sla.setComponents(SpellContract.SpellContractUtils.getComponents(c));
		sla.setClasses(SpellContract.SpellContractUtils.getClasses(c));
		return sla;
	}
}
