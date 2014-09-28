package org.evilsoft.pathfinder.spellbook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SpellbookActivity extends SherlockFragmentActivity {
	SpellbookPagerAdapter mAdapter;
	protected ViewPager mPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spellbook_phone);
		mAdapter = new SpellbookPagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
	}

	public static class SpellbookPagerAdapter extends FragmentPagerAdapter {
		public SpellbookPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return new SpellbookFragment();
			} else if (position == 1) {
				return new SpellbookEditFragment();
			}
			return new SpellbookFragment();
		}
	}
}
