package org.evilsoft.pathfinder.spellbook;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.api.contracts.CasterContract;
import org.evilsoft.pathfinder.reference.api.contracts.SectionContract;
import org.evilsoft.pathfinder.reference.api.contracts.SpellContract;
import org.evilsoft.pathfinder.spellbook.adapter.ClassSpellListAdapter;
import org.evilsoft.pathfinder.spellbook.adapter.SpellListAdapter;
import org.evilsoft.pathfinder.spellbook.api.BaseApiHelper;
import org.evilsoft.pathfinder.spellbook.sectionlist.SectionListAdapter;
import org.evilsoft.pathfinder.spellbook.sectionlist.SectionListView;

import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class FindFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "FindFragment";
	private BaseAdapter searchAdapter;
	private SectionListAdapter sectionAdapter;
	private SectionListView listView;
	private ContentResolver cr;
	private ClassListHandler classListHandler;
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
		nameInput.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				requestSpells();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View vire,
					int position, long id) {
				final SpellListItem currentItem = (SpellListItem) listView
						.getAdapter().getItem(position);
				SpellAddHandler sah = new SpellAddHandler(getActivity(),
						classListHandler, currentItem);
				return sah.createDialog();
			}
		});
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		requestSpells();
		return v;
	}

	protected void hideSoftKeyboard(TextView input) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
	}

	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(CLASSLIST_LOADER, null, this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(CLASSLIST_LOADER, null, this);
	}

	private static final int CLASSLIST_LOADER = 2;

	public Integer getClassIndexId(String className) throws RemoteException {
		ContentProviderClient classListClient = cr
				.acquireContentProviderClient(SectionContract.AUTHORITY);
		String[] selectionArgs = new String[2];
		selectionArgs[0] = "class";
		selectionArgs[1] = className;
		Cursor curs = classListClient.query(SectionContract.SECTION_LIST_URI,
				null, "type = ? AND name = ?", selectionArgs, null);
		try {
			boolean hasNext = curs.moveToFirst();
			if (hasNext) {
				return SectionContract.SectionContractUtils.getId(curs);
			}
			return null;
		} finally {
			curs.close();
		}
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

					Integer classId = getClassIndexId(classListHandler
							.getClassName(classPos - 1));
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
				classListHandler.populateLevelSpinner(levelSpinner, pos - 1,
						true, null);
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

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case CLASSLIST_LOADER:
			return new CursorLoader(getActivity(),
					CasterContract.CASTER_LIST_URI, null, null, null, null);
		default:
			throw new UnsupportedOperationException("Unknown id: " + id);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
		case CLASSLIST_LOADER:
			classListHandler = new ClassListHandler(this.getActivity(), cursor);
			break;
		default:
			throw new UnsupportedOperationException("Unknown id: "
					+ loader.getId());
		}
		if (classListHandler != null) {
			classListHandler.populateClassSpinner(classSpinner, null, true);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}
}
