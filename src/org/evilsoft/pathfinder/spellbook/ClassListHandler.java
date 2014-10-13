package org.evilsoft.pathfinder.spellbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evilsoft.pathfinder.reference.api.contracts.CasterContract;

import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ClassListHandler {
	private Context context;
	private List<CasterClass> classList;
	private Map<String, CasterClass> classMap;

	public ClassListHandler(Context context, Cursor cursor) {
		this.context = context;
		load(cursor);
	}

	private void load(Cursor cursor) {
		boolean hasNext = cursor.moveToFirst();
		CasterClass casterClass = null;
		classList = new ArrayList<CasterClass>();
		classMap = new HashMap<String, CasterClass>();
		while (hasNext) {
			String className = CasterContract.CasterContractUtils
					.getClass(cursor);
			String classLevel = CasterContract.CasterContractUtils.getLevel(
					cursor).toString();
			if (casterClass == null) {
				casterClass = new CasterClass(className);
				classList.add(casterClass);
				classMap.put(className, casterClass);
			}
			if (!className.equals(casterClass.getName())) {
				casterClass = new CasterClass(className);
				classList.add(casterClass);
				classMap.put(className, casterClass);
			}
			casterClass.getLevels().add(classLevel);
			hasNext = cursor.moveToNext();
		}
	}

	public String getClassName(Integer classNum) {
		return classList.get(classNum).getName();
	}

	public void populateLevelSpinner(Spinner levelSpinner, Integer classNum,
			boolean noLevel, String defaultLevel) {
		populateLevelSpinner(levelSpinner, getClassName(classNum), noLevel,
				defaultLevel);
	}

	public void populateLevelSpinner(Spinner levelSpinner, String className,
			boolean noLevel, String defaultLevel) {
		CasterClass defaultClass = classMap.get(className);
		List<String> list = new ArrayList<String>();
		if (noLevel) {
			list.add("-");
		}
		list.addAll(defaultClass.getLevels());
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		levelSpinner.setAdapter(dataAdapter);
		if (defaultLevel != null) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) == defaultLevel) {
					levelSpinner.setSelection(i);
					break;
				}
			}
		}
	}

	public void populateClassSpinner(Spinner classSpinner, String className,
			boolean noClass) {
		List<String> list = new ArrayList<String>();
		if (noClass) {
			list.add("-None-");
		}
		for (int i = 0; i < classList.size(); i++) {
			list.add(classList.get(i).getName());
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		classSpinner.setAdapter(dataAdapter);
		if (className != null) {
			for (int i = 0; i < dataAdapter.getCount(); i++) {
				String item = dataAdapter.getItem(i);
				if (item.equals(className)) {
					classSpinner.setSelection(i);
					break;
				}
			}
		}
	}

	protected class CasterClass {
		private String name;
		private List<String> levels;

		public CasterClass(String name) {
			this.name = name;
			levels = new ArrayList<String>();
		}

		public void setName(String name) {
			this.name = name;
		}

		public void addLevel(String level) {
			levels.add(level);
		}

		public String getName() {
			return this.name;
		}

		public List<String> getLevels() {
			return this.levels;
		}
	}
}
