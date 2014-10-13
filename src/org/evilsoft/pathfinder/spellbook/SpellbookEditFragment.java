package org.evilsoft.pathfinder.spellbook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evilsoft.pathfinder.reference.api.contracts.CasterContract;
import org.evilsoft.pathfinder.reference.api.contracts.SectionContract;
import org.evilsoft.pathfinder.reference.api.contracts.SpellContract;
import org.evilsoft.pathfinder.spellbook.adapter.EditSpellListAdapter;
import org.evilsoft.pathfinder.spellbook.api.BaseApiHelper;
import org.evilsoft.pathfinder.spellbook.data.SpellBookContract.SpellbookEntry;
import org.evilsoft.pathfinder.spellbook.data.SpellBookSpellContract.SpellbookSpellEntry;
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

public class SpellbookEditFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "SpellbookEditFragment";
	private BaseAdapter searchAdapter;
	private TextView mSpellbookClass;
	private SectionListAdapter sectionAdapter;
	private SectionListView listView;
	private ContentResolver cr;
	private ClassListHandler classListHandler;
	private Spinner levelSpinner;
	private TextView levelText;
	private EditText nameInput;
	private TextView addSearch;
	private View v;
	private Uri spellbookUri;
	private Long spellbookId;
	private String spellbookName;
	private String spellbookClass;
	private Long spellbookClassId;
	private Set<String> selectedSpells;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cr = this.getActivity().getContentResolver();
		v = inflater
				.inflate(R.layout.spellbook_edit_fragment, container, false);
		mSpellbookClass = (TextView) v.findViewById(R.id.spellbook_class);
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
				if (spellbookClassId != null) {
					requestSpells();
				}
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
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final SpellListItem currentItem = (SpellListItem) listView
						.getAdapter().getItem(position);
				SpellAddHandler sah = new SpellAddHandler(getActivity(),
						classListHandler, currentItem);
				sah.createDialog();
				return true;
			}
		});
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		selectedSpells = new HashSet<String>();
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		Intent launchingIntent = getActivity().getIntent();
		spellbookUri = launchingIntent.getData();
		getLoaderManager().restartLoader(SPELLBOOK_LOADER, null, this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Intent launchingIntent = getActivity().getIntent();
		spellbookUri = launchingIntent.getData();
		getLoaderManager().initLoader(SPELLBOOK_LOADER, null, this);
	}

	protected void hideSoftKeyboard(TextView input) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

	}

	protected class LevelOnItemSelectedListener implements
			OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (spellbookClassId != null) {
				requestSpells();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	}

	private static final int SPELLBOOK_LOADER = 0;
	private static final String[] SPELLBOOK_COLUMNS = {
			SpellbookEntry.TABLE_NAME + "." + SpellbookEntry._ID,
			SpellbookEntry.COLUMN_NAME, SpellbookEntry.COLUMN_SPELL_CLASS };
	private static final int CLASSLIST_LOADER = 1;
	private static final int CLASSID_LOADER = 2;
	private static final int SPELL_LOADER = 3;
	private static final String[] SPELL_COLUMNS = { SpellbookSpellEntry.TABLE_NAME
			+ "." + SpellbookSpellEntry.COLUMN_URL };

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] selectionArgs;
		switch (id) {
		case SPELLBOOK_LOADER:
			String sortOrder = SpellbookEntry.COLUMN_NAME + " ASC";
			return new CursorLoader(getActivity(), spellbookUri,
					SPELLBOOK_COLUMNS, null, null, sortOrder);
		case CLASSLIST_LOADER:
			return new CursorLoader(getActivity(),
					CasterContract.CASTER_LIST_URI, null, null, null, null);
		case CLASSID_LOADER:
			selectionArgs = new String[2];
			selectionArgs[0] = "class";
			selectionArgs[1] = spellbookClass;
			return new CursorLoader(getActivity(),
					SectionContract.SECTION_LIST_URI, null,
					"type = ? AND name = ?", selectionArgs, null);
		case SPELL_LOADER:
			return new CursorLoader(getActivity(),
					SpellbookSpellEntry.buildSpellbookSpellsUri(spellbookId),
					SPELL_COLUMNS, null, null, null);

		default:
			throw new UnsupportedOperationException("Unknown id: " + id);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		boolean hasNext = false;
		switch (loader.getId()) {
		case SPELLBOOK_LOADER:
			hasNext = cursor.moveToFirst();
			if (hasNext) {
				this.spellbookId = cursor.getLong(0);
				spellbookName = cursor.getString(1);
				getActivity().getActionBar().setTitle(spellbookName);
				spellbookClass = cursor.getString(2);
				this.mSpellbookClass.setText(spellbookClass);
				levelSpinner.setVisibility(View.VISIBLE);
				levelText.setVisibility(View.VISIBLE);
				v.requestLayout();
				getLoaderManager().restartLoader(CLASSLIST_LOADER, null, this);
				getLoaderManager().restartLoader(CLASSID_LOADER, null, this);
				getLoaderManager().restartLoader(SPELL_LOADER, null, this);
			}
			break;
		case CLASSLIST_LOADER:
			classListHandler = new ClassListHandler(this.getActivity(), cursor);
			break;
		case CLASSID_LOADER:
			hasNext = cursor.moveToFirst();
			if (hasNext) {
				spellbookClassId = SectionContract.SectionContractUtils.getId(
						cursor).longValue();
			}
			break;
		case SPELL_LOADER:
			selectedSpells.clear();
			hasNext = cursor.moveToFirst();
			while (hasNext) {
				selectedSpells.add(cursor.getString(0));
				hasNext = cursor.moveToNext();
			}
			return;
		default:
			throw new UnsupportedOperationException("Unknown id: "
					+ loader.getId());
		}
		if (classListHandler != null && spellbookClass != null) {
			classListHandler.populateLevelSpinner(levelSpinner, spellbookClass,
					true, null);
			if (spellbookClassId != null) {
				requestSpells();
			}
		}
	}

	public void requestSpells() {
		Integer level = levelSpinner.getSelectedItemPosition() - 1;
		try {
			ContentProviderClient spellListClient = cr
					.acquireContentProviderClient(SpellContract.AUTHORITY);
			List<String> selectionArgs = new ArrayList<String>();
			List<String> selection = new ArrayList<String>();

			if (level > -1) {
				selectionArgs.add(level.toString());
				selection.add("level = ?");
			}
			if (!nameInput.getText().toString().equals("")) {
				selection.add("name LIKE '%" + nameInput.getText().toString()
						+ "%'");
			}
			Uri uri = SpellContract.getClassSpellList(spellbookClassId
					.toString());
			Cursor curs = spellListClient.query(uri, null,
					BaseApiHelper.joinSelectionCriteria(selection),
					BaseApiHelper.toStringArray(selectionArgs), null);
			searchAdapter = new EditSpellListAdapter(getActivity()
					.getApplicationContext(), curs, spellbookId, selectedSpells);
			sectionAdapter = new SectionListAdapter(getActivity()
					.getLayoutInflater(), searchAdapter);
			listView.setAdapter(sectionAdapter);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}
