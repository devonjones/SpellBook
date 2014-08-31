package org.evilsoft.pathfinder.spellbook;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class StartupFragment extends SherlockFragment {
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
		getSpellbooks();

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

	/*
	 * private void createSpellBook1() { ParseObject spellBook = new
	 * ParseObject("SpellBook"); spellBook.put("name", "SpellBook 1");
	 * spellBook.put("characterClass", "Wizard"); spellBook.setACL(new
	 * ParseACL(UserManager.getInstance().getUser()));
	 * spellBook.saveEventually(); }
	 */

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

	private void getSpellbooks() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("SpellBook");
		// query.whereEqualTo("owner", UserManager.getInstance().getUser());
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> spellBooks, ParseException e) {
				if (e == null) {
					spellBookList.clear();
					for (int i = 0; i < spellBooks.size(); i++) {
						ParseObject spellBook = spellBooks.get(i);
						spellBookList.add(new ParseListItem(spellBook
								.getString("name"), "Spell Books", spellBook));
					}
					spellBookList.add(new ActionListItem("New...",
							"Spell Books"));
					arrayAdapter.clear();
					arrayAdapter.addAll(createViewList());
					arrayAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(getActivity(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private class ParseListItem extends SectionListItem implements
			ClickableItem {
		ParseObject po;

		public ParseListItem(Object item, String section, ParseObject po) {
			this.item = item;
			this.section = section;
			this.po = po;
		}

		@Override
		public void onClick() {
			Toast.makeText(getActivity(), (String) item, Toast.LENGTH_SHORT)
					.show();
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

	List<SectionListItem> characterList;
	List<SectionListItem> spellBookList;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		characterList = new ArrayList<SectionListItem>();
		characterList.add(new ActionListItem("New...", "Characters"));
		spellBookList = new ArrayList<SectionListItem>();
		spellBookList.add(new ActionListItem("New...", "Spell Books"));
	}

	private List<SectionListItem> createViewList() {
		List<SectionListItem> tmpList = new ArrayList<SectionListItem>();
		tmpList.addAll(characterList);
		tmpList.addAll(spellBookList);
		return tmpList;
	}
}
