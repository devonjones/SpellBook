package org.evilsoft.pathfinder.spellbook;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.spellbook.data.SpellBookContract.SpellbookEntry;
import org.evilsoft.pathfinder.spellbook.sectionlist.SectionListAdapter;
import org.evilsoft.pathfinder.spellbook.sectionlist.SectionListItem;
import org.evilsoft.pathfinder.spellbook.sectionlist.SectionListView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class StartupFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "StartupFragment";
	private StandardArrayAdapter arrayAdapter;
	private SectionListAdapter sectionAdapter;
	private SectionListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.startup_fragment, container, false);
		arrayAdapter = new StandardArrayAdapter(getActivity()
				.getApplicationContext(), R.id.startup_text_view,
				createViewList());
		sectionAdapter = new SectionListAdapter(getActivity()
				.getLayoutInflater(), arrayAdapter);
		listView = (SectionListView) v.findViewById(getResources()
				.getIdentifier("startup_list", "id",
						this.getClass().getPackage().getName()));
		listView.setAdapter(sectionAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ClickableItem i = (ClickableItem) listView.getAdapter()
						.getItem(position);
				i.onClick();
			}
		});

		TextView find = (TextView) v.findViewById(getResources().getIdentifier(
				"find_spells", "id", this.getClass().getPackage().getName()));
		find.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent showContent = new Intent(StartupFragment.this
						.getActivity().getApplicationContext(),
						FindActivity.class);
				startActivity(showContent);
			}
		});
		return v;
	}

	private class StandardArrayAdapter extends ArrayAdapter<SectionListItem> {

		private final List<SectionListItem> items;

		public StandardArrayAdapter(final Context context,
				final int textViewResourceId, final List<SectionListItem> items) {
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
				view = vi.inflate(R.layout.startup_list_view, null);
			}
			final SectionListItem currentItem = items.get(position);
			if (currentItem != null) {
				final TextView textView = (TextView) view
						.findViewById(R.id.startup_text_view);
				if (textView != null) {
					textView.setText(currentItem.item.toString());
				}
			}
			return view;
		}
	}

	private class ActionListItem extends SectionListItem implements
			ClickableItem {
		public ActionListItem(Object item, String section) {
			this.item = item;
			this.section = section;
		}

		@Override
		public void onClick() {
			Toast.makeText(getActivity(), (String) item, Toast.LENGTH_SHORT)
					.show();
		}
	}

	private class NewSpellbookListItem extends SectionListItem implements
			ClickableItem {
		public NewSpellbookListItem(Object item, String section) {
			this.item = item;
			this.section = section;
		}

		@Override
		public void onClick() {
			ContentValues spellbookValues = new ContentValues();
			spellbookValues.put(SpellbookEntry.COLUMN_NAME, "New Spellbook");
			spellbookValues.put(SpellbookEntry.COLUMN_SPELL_CLASS, "Wizard");
			Uri spellbookInsertUri = getActivity().getContentResolver().insert(
					SpellbookEntry.CONTENT_URI, spellbookValues);
			Intent showContent = new Intent(StartupFragment.this.getActivity()
					.getApplicationContext(), SpellbookActivity.class);
			showContent.setData(spellbookInsertUri);
			startActivity(showContent);
		}
	}

	private class ClickableSpellbookListItem extends SpellbookListItem
			implements ClickableItem {
		public ClickableSpellbookListItem(long spellbookId, String name,
				String spellClass) {
			super(spellbookId, name, spellClass);
		}

		@Override
		public void onClick() {
			Uri spellbookInsertUri = SpellbookEntry
					.buildSpellbookUri(spellbookId);
			Intent showContent = new Intent(StartupFragment.this.getActivity()
					.getApplicationContext(), SpellbookActivity.class);
			showContent.setData(spellbookInsertUri);
			startActivity(showContent);
		}
	}

	List<SectionListItem> characterList;
	List<SectionListItem> spellBookList;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		characterList = new ArrayList<SectionListItem>();
		characterList.add(new ActionListItem("New...", "Characters"));
		spellBookList = new ArrayList<SectionListItem>();
		spellBookList.add(new NewSpellbookListItem("New...", "Spell Books"));
	}

	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(SPELLBOOK_LOADER, null, this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(SPELLBOOK_LOADER, null, this);
	}

	private List<SectionListItem> createViewList() {
		List<SectionListItem> tmpList = new ArrayList<SectionListItem>();
		tmpList.addAll(characterList);
		tmpList.addAll(spellBookList);
		return tmpList;
	}

	private static final String[] SPELLBOOK_COLUMNS = {
			SpellbookEntry.TABLE_NAME + "." + SpellbookEntry._ID,
			SpellbookEntry.COLUMN_NAME, SpellbookEntry.COLUMN_SPELL_CLASS };

	private static final int SPELLBOOK_LOADER = 0;

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Sort order: Ascending, by date.
		if (id == SPELLBOOK_LOADER) {
			String sortOrder = SpellbookEntry.COLUMN_NAME + " ASC";
			return new CursorLoader(getActivity(), SpellbookEntry.CONTENT_URI,
					SPELLBOOK_COLUMNS, null, null, sortOrder);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
		case SPELLBOOK_LOADER:
			boolean has_next = cursor.moveToFirst();
			spellBookList.clear();
			while (has_next) {
				long spellbookId = cursor.getLong(0);
				String name = cursor.getString(1);
				String spellClass = cursor.getString(2);
				spellBookList.add(new ClickableSpellbookListItem(spellbookId,
						name, spellClass));
				has_next = cursor.moveToNext();
			}
			spellBookList
					.add(new NewSpellbookListItem("New...", "Spell Books"));
			arrayAdapter.clear();
			arrayAdapter.addAll(createViewList());
			arrayAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}
