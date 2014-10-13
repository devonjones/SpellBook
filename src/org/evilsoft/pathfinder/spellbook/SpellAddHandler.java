package org.evilsoft.pathfinder.spellbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.evilsoft.pathfinder.spellbook.data.SpellBookContract.SpellbookEntry;
import org.evilsoft.pathfinder.spellbook.data.SpellBookSpellContract.SpellbookSpellEntry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class SpellAddHandler {
	private Context context;
	private ClassListHandler classListHandler;
	private SpellListItem currentItem;

	public SpellAddHandler(Context context, ClassListHandler classListHandler,
			SpellListItem currentItem) {
		this.context = context;
		this.classListHandler = classListHandler;
		this.currentItem = currentItem;
	}

	protected class SpellBookPojo {
		private Integer id;
		private String name;
		private String className;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String toString() {
			return name + " (" + className + ")";
		}
	}

	private static final String[] SPELLBOOK_COLUMNS = {
			SpellbookEntry.TABLE_NAME + "." + SpellbookEntry._ID,
			SpellbookEntry.COLUMN_NAME, SpellbookEntry.COLUMN_SPELL_CLASS };

	public void populateSpellBookSpinner(Spinner spellBookSpinner) {
		String sortOrder = SpellbookEntry.COLUMN_NAME + " ASC";
		Cursor cursor = context.getContentResolver().query(
				SpellbookEntry.CONTENT_URI, SPELLBOOK_COLUMNS, null, null,
				sortOrder);
		List<SpellBookPojo> list = new ArrayList<SpellBookPojo>();
		try {
			boolean hasNext = cursor.moveToFirst();
			while (hasNext) {
				SpellBookPojo sbp = new SpellBookPojo();
				sbp.setId(cursor.getInt(0));
				sbp.setName(cursor.getString(1));
				sbp.setClassName(cursor.getString(2));
				list.add(sbp);
				hasNext = cursor.moveToNext();
			}
		} finally {
			cursor.close();
		}
		ArrayAdapter<SpellBookPojo> dataAdapter = new ArrayAdapter<SpellBookPojo>(
				context, android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spellBookSpinner.setAdapter(dataAdapter);
	}

	public void populateLevelSpinner(Spinner levelSpinner,
			SpellListItem currentItem) {
		// Set up the input
		Integer level;
		if (currentItem.getClasses() == null) {
			level = currentItem.getLevel();
		} else {
			level = findLevelMode(currentItem.getClasses());
		}
		classListHandler.populateLevelSpinner(levelSpinner, "Wizard", false,
				level.toString());
	}

	public Integer findLevelMode(String classString) {
		String[] classes = classString.split(";");
		Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < classes.length; i++) {
			String[] fields = classes[i].split(":");
			Integer level = Integer.parseInt(fields[1].trim());
			if (countMap.containsKey(level)) {
				countMap.put(level, countMap.get(level) + 1);
			} else {
				countMap.put(level, 1);
			}
		}
		TreeSet<Integer> keySet = new TreeSet<Integer>(countMap.keySet());
		Integer high = keySet.first();
		int highValue = countMap.get(high);
		for (Integer i : keySet) {
			int currValue = countMap.get(i);
			if (currValue > highValue) {
				highValue = currValue;
				high = i;
			}
		}
		return high;
	}

	public boolean createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Add To Spellbook");

		final Spinner spellBookSpinner = new Spinner(context);
		populateSpellBookSpinner(spellBookSpinner);
		final Spinner levelSpinner = new Spinner(context);
		populateLevelSpinner(levelSpinner, currentItem);
		final LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(spellBookSpinner);
		layout.addView(levelSpinner);
		builder.setView(layout);
		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SpellBookPojo sbp = (SpellBookPojo) spellBookSpinner
						.getSelectedItem();
				ContentValues spellValues = new ContentValues();
				spellValues.put(SpellbookSpellEntry.COLUMN_LEVEL,
						(String) levelSpinner.getSelectedItem());
				spellValues.put(SpellbookSpellEntry.COLUMN_SPELLBOOK_ID,
						sbp.getId());
				spellValues.put(SpellbookSpellEntry.COLUMN_URL,
						currentItem.getContentUrl());
				spellValues.put(SpellbookSpellEntry.COLUMN_NAME,
						currentItem.getName());
				context.getContentResolver()
						.insert(SpellbookSpellEntry.buildSpellbookSpellsUri(sbp
								.getId()), spellValues);
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		final Dialog dialog = builder.create();
		spellBookSpinner
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							dialog.getWindow()
									.setSoftInputMode(
											WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						}
					}
				});
		dialog.show();
		return true;
	}
}
