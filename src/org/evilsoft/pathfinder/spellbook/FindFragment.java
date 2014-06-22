package org.evilsoft.pathfinder.spellbook;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.api.contracts.CasterContract;
import org.evilsoft.pathfinder.reference.api.contracts.SectionContract;
import org.evilsoft.pathfinder.reference.api.contracts.SpellContract;
import org.evilsoft.pathfinder.spellbook.api.BaseApiHelper;

import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class FindFragment extends SherlockFragment {
	private static final String TAG = "FindFragment";
	private BaseAdapter searchAdapter;
	private SectionListAdapter sectionAdapter;
	private SectionListView listView;
	private ContentResolver cr;
	private List<CasterClass> classList;
	private Spinner classSpinner;
	private Spinner levelSpinner;
	private TextView levelText;
	private EditText nameInput;
	private TextView addSearch;
	private View v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cr = this.getActivity().getContentResolver();
		v = inflater.inflate(R.layout.find_fragment, container, false);
		classSpinner = (Spinner) v.findViewById(R.id.class_spinner);
		classSpinner
				.setOnItemSelectedListener(new ClassOnItemSelectedListener());
		levelSpinner = (Spinner) v.findViewById(R.id.level_spinner);
		levelSpinner
				.setOnItemSelectedListener(new LevelOnItemSelectedListener());
		levelSpinner.setVisibility(View.INVISIBLE);
		levelText = (TextView) v.findViewById(R.id.level_text);
		levelText.setVisibility(View.INVISIBLE);
		nameInput = (EditText) v.findViewById(R.id.name_input);
		addSearch = (TextView) v.findViewById(R.id.add_search);
		nameInput
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							requestSpells();
							hideSoftKeyboard(v);
							return true;
						}
						return false;
					}
				});
		listView = (SectionListView) v.findViewById(getResources()
				.getIdentifier("startup_list", "id",
						this.getClass().getPackage().getName()));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SpellListItem currentItem = (SpellListItem) listView
						.getAdapter().getItem(position);
				Intent intent = new Intent("android.intent.action.MAIN");
				intent.setComponent(ComponentName
						.unflattenFromString("org.evilsoft.pathfinder.reference/org.evilsoft.pathfinder.reference.DetailsActivity"));
				intent.setData(Uri.parse(currentItem.getContentUrl()));
				intent.addCategory("android.intent.category.LAUNCHER");
				startActivity(intent);
			}
		});
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getClassList();
		populateClassSpinner();
		requestSpells();
		return v;
	}

	protected void hideSoftKeyboard(TextView input) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

	}

	public void populateClassSpinner() {
		List<String> list = new ArrayList<String>();
		list.add("-None-");
		for (int i = 0; i < classList.size(); i++) {
			list.add(classList.get(i).getName());
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		classSpinner.setAdapter(dataAdapter);
	}

	public void populateLevelSpinner(Integer classNum) {
		List<String> list = new ArrayList<String>();
		list.add("-");
		list.addAll(1, classList.get(classNum).getLevels());
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		levelSpinner.setAdapter(dataAdapter);
	}

	public Integer getClassIndexId(String className) throws RemoteException {
		ContentProviderClient classListClient = cr
				.acquireContentProviderClient(SectionContract.AUTHORITY);
		String[] selectionArgs = new String[2];
		selectionArgs[0] = "class";
		selectionArgs[1] = className;
		Cursor curs = classListClient.query(SectionContract.SECTION_LIST_URI,
				null, "type = ? AND name = ?", selectionArgs, null);
		boolean hasNext = curs.moveToFirst();
		if (hasNext) {
			return SectionContract.SectionContractUtils.getId(curs);
		}
		return null;
	}

	public void requestSpells() {
		Integer classPos = classSpinner.getSelectedItemPosition();
		Integer level = levelSpinner.getSelectedItemPosition() - 1;
		String nameFilter = nameInput.getText().toString();
		if (classPos == 0 && nameFilter.equals("")) {
			addSearch.setVisibility(View.VISIBLE);
		} else {
			addSearch.setVisibility(View.GONE);
			try {
				ContentProviderClient spellListClient = cr
						.acquireContentProviderClient(SpellContract.AUTHORITY);
				if (classPos > 0) {
					Integer classId = getClassIndexId(classList.get(
							classPos - 1).getName());
					List<String> selectionArgs = new ArrayList<String>();
					List<String> selection = new ArrayList<String>();

					if (level > -1) {
						selectionArgs.add(level.toString());
						selection.add("level = ?");
					}
					if (!nameInput.getText().toString().equals("")) {
						selection.add("name LIKE '%"
								+ nameInput.getText().toString() + "%'");
					}
					Uri uri = SpellContract.getClassSpellList(classId
							.toString());
					Cursor curs = spellListClient.query(uri, null,
							BaseApiHelper.joinSelectionCriteria(selection),
							BaseApiHelper.toStringArray(selectionArgs), null);
					searchAdapter = new ClassSpellListAdapter(getActivity()
							.getApplicationContext(), curs);
				} else {
					List<String> selectionArgs = new ArrayList<String>();
					List<String> selection = new ArrayList<String>();

					if (!nameInput.getText().toString().equals("")) {
						selection.add("name LIKE '%"
								+ nameInput.getText().toString() + "%'");
					}
					Uri uri = SpellContract.SPELL_LIST_URI;
					Cursor curs = spellListClient.query(uri, null,
							BaseApiHelper.joinSelectionCriteria(selection),
							BaseApiHelper.toStringArray(selectionArgs), null);
					searchAdapter = new SpellListAdapter(getActivity()
							.getApplicationContext(), curs);

				}
				sectionAdapter = new SectionListAdapter(getActivity()
						.getLayoutInflater(), searchAdapter);
				listView.setAdapter(sectionAdapter);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected class ClassOnItemSelectedListener implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (pos == 0) {
				levelSpinner.setVisibility(View.INVISIBLE);
				levelText.setVisibility(View.INVISIBLE);
				v.requestLayout();
			} else {
				populateLevelSpinner(pos - 1);
				levelSpinner.setVisibility(View.VISIBLE);
				levelText.setVisibility(View.VISIBLE);
				v.requestLayout();
			}
			requestSpells();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	}

	protected class LevelOnItemSelectedListener implements
			OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			requestSpells();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
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

	private void getClassList() {
		ContentProviderClient casterClient = cr
				.acquireContentProviderClient(CasterContract.AUTHORITY);
		classList = new ArrayList<CasterClass>();
		try {
			Cursor curs = casterClient.query(CasterContract.CASTER_LIST_URI,
					null, null, null, null);
			boolean hasNext = curs.moveToFirst();
			CasterClass casterClass = null;
			while (hasNext) {
				String className = CasterContract.CasterContractUtils
						.getClass(curs);
				String classLevel = CasterContract.CasterContractUtils
						.getLevel(curs).toString();
				if (casterClass == null) {
					casterClass = new CasterClass(className);
					classList.add(casterClass);
				}
				if (!className.equals(casterClass.getName())) {
					casterClass = new CasterClass(className);
					classList.add(casterClass);
				}
				casterClass.getLevels().add(classLevel);
				hasNext = curs.moveToNext();
			}
		} catch (RemoteException e) {
			Log.e("CasterList", "Failed on load", e);
		}
	}

	private class StandardArrayAdapter extends ArrayAdapter<SectionListItem> {

		private final SectionListItem[] items;

		public StandardArrayAdapter(final Context context,
				final int textViewResourceId, final SectionListItem[] items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(final int position, final View convertView,
				final ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				final LayoutInflater vi = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.find_list_view, null);
			}
			final SectionListItem currentItem = items[position];
			if (currentItem != null) {
				final TextView textView = (TextView) view
						.findViewById(R.id.find_name);
				if (textView != null) {
					textView.setText(currentItem.item.toString());
				}
			}
			return view;
		}
	}

	SectionListItem[] exampleArray = { new TestListItem("Test 1 - A", "A"),
			new TestListItem("Test 2 - A", "A"),
			new TestListItem("Test 3 - A", "A"),
			new TestListItem("Test 4 - A", "A"),
			new TestListItem("Test 5 - A", "A"),
			new TestListItem("Test 6 - B", "B"),
			new TestListItem("Test 7 - B", "B"),
			new TestListItem("Test 8 - B", "B"),
			new TestListItem("Test 9 - Long", "Long section"),
			new TestListItem("Test 10 - Long", "Long section"),
			new TestListItem("Test 11 - Long", "Long section"),
			new TestListItem("Test 12 - Long", "Long section"),
			new TestListItem("Test 13 - Long", "Long section"),
			new TestListItem("Test 14 - A again", "A"),
			new TestListItem("Test 15 - A again", "A"),
			new TestListItem("Test 16 - A again", "A"),
			new TestListItem("Test 17 - B again", "B"),
			new TestListItem("Test 18 - B again", "B"),
			new TestListItem("Test 19 - B again", "B"),
			new TestListItem("Test 20 - B again", "B"),
			new TestListItem("Test 21 - B again", "B"),
			new TestListItem("Test 22 - B again", "B"),
			new TestListItem("Test 23 - C", "C"),
			new TestListItem("Test 24 - C", "C"),
			new TestListItem("Test 25 - C", "C"),
			new TestListItem("Test 26 - C", "C"), };

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private class TestListItem extends SectionListItem {
		public TestListItem(Object item, String section) {
			this.item = item;
			this.section = section;
		}
	}
}
