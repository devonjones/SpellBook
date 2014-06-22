package org.evilsoft.pathfinder.spellbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

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
				.getApplicationContext(), R.id.startup_text_view, exampleArray);
		sectionAdapter = new SectionListAdapter(getActivity()
				.getLayoutInflater(), arrayAdapter);
		listView = (SectionListView) v.findViewById(getResources()
				.getIdentifier("startup_list", "id",
						this.getClass().getPackage().getName()));
		listView.setAdapter(sectionAdapter);

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
				view = vi.inflate(R.layout.startup_list_view, null);
			}
			final SectionListItem currentItem = items[position];
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

	private class TestListItem extends SectionListItem {
		public TestListItem(Object item, String section) {
			this.item = item;
			this.section = section;
		}
	}

	SectionListItem[] exampleArray = {
			new TestListItem("Petrovich", "Characters"),
			new TestListItem("Yagi", "Characters"),
			new TestListItem("New...", "Characters"),
			new TestListItem("Mook 1", "Spell Books"),
			new TestListItem("Yagi", "Spell Books"),
			new TestListItem("New...", "Spell Books"), };

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
