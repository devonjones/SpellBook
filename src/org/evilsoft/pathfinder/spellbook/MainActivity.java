package org.evilsoft.pathfinder.spellbook;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.parse.Parse;

public class MainActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_phone);
		Parse.initialize(this, "J8Mm9rHeYfzc1ubTVPLf6zVe3ptZYdFiHD2B6HYM",
				"6Z9jdUUZjiHVGaWnqas9EiKM8lss29nXeFZxDiov");
	}
}
