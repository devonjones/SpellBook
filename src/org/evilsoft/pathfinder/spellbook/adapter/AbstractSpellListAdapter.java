package org.evilsoft.pathfinder.spellbook.adapter;

import org.evilsoft.pathfinder.reference.api.contracts.SpellContract;
import org.evilsoft.pathfinder.spellbook.R;
import org.evilsoft.pathfinder.spellbook.SpellListItem;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class AbstractSpellListAdapter extends DisplayListAdapter {
	public AbstractSpellListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public abstract View inflate(int index, View convertView, ViewGroup parent);

	public abstract View localGetView(int index, View convertView,
			ViewGroup parent, View local, SpellListItem currentItem);

	public abstract boolean isClassSpellList();

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		View view = convertView;
		moveCursor(index);
		if (view == null) {
			view = inflate(index, convertView, parent);
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
		return localGetView(index, convertView, parent, view, currentItem);
	}

	@Override
	public Object buildItem(Cursor c) {
		if (isClassSpellList()) {
			return GenerateClassSpellListItem(c);
		} else {
			return GenerateSpellListItem(c);
		}
	}

	public static SpellListItem GenerateSpellListItem(Cursor c) {
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

	public static SpellListItem GenerateClassSpellListItem(Cursor c) {
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
