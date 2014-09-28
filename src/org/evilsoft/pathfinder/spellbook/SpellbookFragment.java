package org.evilsoft.pathfinder.spellbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evilsoft.pathfinder.reference.api.contracts.CasterContract;
import org.evilsoft.pathfinder.reference.api.contracts.SectionContract;
import org.evilsoft.pathfinder.reference.api.contracts.SpellContract;
import org.evilsoft.pathfinder.spellbook.adapter.AbstractSpellListAdapter;
import org.evilsoft.pathfinder.spellbook.adapter.SpellbookListAdapter;
import org.evilsoft.pathfinder.spellbook.api.BaseApiHelper;
import org.evilsoft.pathfinder.spellbook.data.SpellBookContract.SpellbookEntry;
import org.evilsoft.pathfinder.spellbook.data.SpellBookSpellContract.SpellbookSpellEntry;
import org.evilsoft.pathfinder.spellbook.sectionlist.SectionListAdapter;
import org.evilsoft.pathfinder.spellbook.sectionlist.SectionListView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.InputType;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SpellbookFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "SpellbookFragment";
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
	private View searchTools1;
	private View searchTools2;
	private View v;
	private Uri spellbookUri;
	private Long spellbookId;
	private String spellbookName;
	private String spellbookClass;
	private Long spellbookClassId;
	private Map<String, SpellListItem> spells;
	private List<SpellListItem> spellbookSpells;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cr = this.getActivity().getContentResolver();
		v = inflater.inflate(R.layout.spellbook_fragment, container, false);
		mSpellbookClass = (TextView) v.findViewById(R.id.spellbook_class);
		levelSpinner = (Spinner) v.findViewById(R.id.level_spinner);
		levelSpinner
				.setOnItemSelectedListener(new LevelOnItemSelectedListener());
		levelSpinner.setVisibility(View.INVISIBLE);
		levelText = (TextView) v.findViewById(R.id.level_text);
		levelText.setVisibility(View.INVISIBLE);
		nameInput = (EditText) v.findViewById(R.id.name_input);
		addSearch = (TextView) v.findViewById(R.id.add_search);
		searchTools1 = (View) v.findViewById(R.id.search_layout1);
		searchTools2 = (View) v.findViewById(R.id.search_layout2);
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
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		return v;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_spellbook, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_rename_spellbook:
			return editSpellbookName();
		case R.id.menu_delete_spellbook:
			deleteSpellbook();
			return true;
		case R.id.menu_add:
			SpellbookActivity act = (SpellbookActivity) this.getActivity();
			act.mPager.setCurrentItem(1);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void deleteSpellbook() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE: // Yes button clicked
					ContentValues spellbookValues = new ContentValues();
					spellbookValues.put(SpellbookEntry.COLUMN_NAME,
							"New Spellbook");
					spellbookValues.put(SpellbookEntry.COLUMN_SPELL_CLASS,
							"Wizard");
					getActivity().getContentResolver().delete(
							SpellbookEntry.buildSpellbookUri(spellbookId),
							null, null);
					Intent showContent = new Intent(SpellbookFragment.this
							.getActivity().getApplicationContext(),
							MainActivity.class);
					startActivity(showContent);
					break;
				case DialogInterface.BUTTON_NEGATIVE: // No button clicked
					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this.getActivity());
		builder.setMessage("Are you sure? Spellbook Delete cannot be undone")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
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

	private boolean editSpellbookName() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Spellbook Name");

		// Set up the input
		final EditText input = new EditText(getActivity());
		input.setText(spellbookName);
		input.setSelectAllOnFocus(true);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ContentValues spellbookValues = new ContentValues();
				spellbookValues.put(SpellbookEntry.COLUMN_NAME, input.getText()
						.toString());
				String[] args = { String.valueOf(spellbookId) };
				getActivity().getContentResolver().update(
						SpellbookEntry.CONTENT_URI, spellbookValues,
						SpellbookEntry._ID + " = ?", args);
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
		input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dialog.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		dialog.show();
		return true;
	}

	protected class SpellbookNameLongClickListener implements
			View.OnLongClickListener {
		@Override
		public boolean onLongClick(View v) {
			return editSpellbookName();
		}
	}

	private static final int SPELLBOOK_LOADER = 0;
	private static final String[] SPELLBOOK_COLUMNS = {
			SpellbookEntry.TABLE_NAME + "." + SpellbookEntry._ID,
			SpellbookEntry.COLUMN_NAME, SpellbookEntry.COLUMN_SPELL_CLASS };

	private static final int SPELLBOOK_SPELLS_LOADER = 1;
	private static final String[] SPELLBOOK_SPELLS_COLUMNS = {
			SpellbookSpellEntry._ID, SpellbookSpellEntry.COLUMN_URL,
			SpellbookSpellEntry.COLUMN_NAME, SpellbookSpellEntry.COLUMN_LEVEL };

	private static final int CLASSLIST_LOADER = 2;
	private static final int CLASSID_LOADER = 3;

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String sortOrder;
		switch (id) {
		case SPELLBOOK_LOADER:
			sortOrder = SpellbookEntry.COLUMN_NAME + " ASC";
			return new CursorLoader(getActivity(), spellbookUri,
					SPELLBOOK_COLUMNS, null, null, sortOrder);
		case SPELLBOOK_SPELLS_LOADER:
			Uri spellbookSpellUri = SpellbookSpellEntry
					.buildSpellbookSpellsUri(spellbookId);
			sortOrder = SpellbookSpellEntry.COLUMN_LEVEL + " ASC, "
					+ SpellbookSpellEntry.COLUMN_NAME + " ASC";
			return new CursorLoader(getActivity(), spellbookSpellUri,
					SPELLBOOK_SPELLS_COLUMNS, null, null, sortOrder);
		case CLASSLIST_LOADER:
			return new CursorLoader(getActivity(),
					CasterContract.CASTER_LIST_URI, null, null, null, null);
		case CLASSID_LOADER:
			String[] selectionArgs = new String[2];
			selectionArgs[0] = "class";
			selectionArgs[1] = spellbookClass;
			return new CursorLoader(getActivity(),
					SectionContract.SECTION_LIST_URI, null,
					"type = ? AND name = ?", selectionArgs, null);

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
				getLoaderManager().restartLoader(SPELLBOOK_SPELLS_LOADER, null,
						this);
				getLoaderManager().restartLoader(CLASSLIST_LOADER, null, this);
				getLoaderManager().restartLoader(CLASSID_LOADER, null, this);
			}
			break;
		case SPELLBOOK_SPELLS_LOADER:
			spellbookSpells = new ArrayList<SpellListItem>();
			hasNext = cursor.moveToFirst();
			while (hasNext) {
				SpellListItem spell = new SpellListItem();
				spell.setContentUrl(cursor.getString(1));
				spell.setName(cursor.getString(2));
				spell.setLevel(cursor.getInt(3));
				spellbookSpells.add(spell);
				hasNext = cursor.moveToNext();
			}
			populateSpellList();
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
		default:
			throw new UnsupportedOperationException("Unknown id: "
					+ loader.getId());
		}
		if (classListHandler != null && spellbookClass != null) {
			classListHandler.populateLevelSpinner(levelSpinner, spellbookClass,
					true);
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
			Cursor cursor = spellListClient.query(uri, null,
					BaseApiHelper.joinSelectionCriteria(selection),
					BaseApiHelper.toStringArray(selectionArgs), null);
			spells = new HashMap<String, SpellListItem>();
			boolean hasNext = cursor.moveToFirst();
			while (hasNext) {
				SpellListItem spell = AbstractSpellListAdapter
						.GenerateClassSpellListItem(cursor);
				spells.put(spell.getContentUrl(), spell);
				hasNext = cursor.moveToNext();
			}
			populateSpellList();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void populateSpellList() {
		if (spells != null && spellbookSpells != null) {
			List<SpellListItem> newSpells = new ArrayList<SpellListItem>();
			for (int i = 0; i < spellbookSpells.size(); i++) {
				SpellListItem spell = spellbookSpells.get(i);
				if (spells.containsKey(spell.getContentUrl())) {
					SpellListItem bookSpell = spells.get(spell.getContentUrl());
					spell.setId(bookSpell.getId());
					spell.setSource(bookSpell.getSource());
					spell.setType(bookSpell.getType());
					spell.setSubType(bookSpell.getSubType());
					spell.setDescription(bookSpell.getDescription());
					spell.setSchool(bookSpell.getSchool());
					spell.setSubschool(bookSpell.getSubschool());
					spell.setDescriptor(bookSpell.getDescriptor());
					spell.setComponents(bookSpell.getComponents());
					spell.setMagicType(bookSpell.getMagicType());
					newSpells.add(spell);
				}
			}
			SpellListItem[] spellItems = SpellbookListAdapter
					.toSpellListItemArray(newSpells);
			if (spellItems == null) {
				spellItems = new SpellListItem[0];
			}
			if (spellbookSpells.size() == 0) {
				addSearch.setVisibility(View.VISIBLE);
				searchTools1.setVisibility(View.GONE);
				searchTools2.setVisibility(View.GONE);
			} else {
				addSearch.setVisibility(View.GONE);
				searchTools1.setVisibility(View.VISIBLE);
				searchTools2.setVisibility(View.VISIBLE);
			}
			searchAdapter = new SpellbookListAdapter(getActivity()
					.getApplicationContext(), R.layout.class_spell_list_item,
					spellItems);
			sectionAdapter = new SectionListAdapter(getActivity()
					.getLayoutInflater(), searchAdapter);
			listView.setAdapter(sectionAdapter);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}
