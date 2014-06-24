package org.evilsoft.pathfinder.spellbook;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.parse.Parse;

@ReportsCrashes(formKey = "0AhyaOyI0JhN9dFVDOVUwNXc2WW5ZYll4RXVsTWxGeWc", socketTimeout = 5000)
public class SpellBookApplication extends Application {
	@Override
	public void onCreate() {
		ACRA.init(this);
		Parse.initialize(this, "J8Mm9rHeYfzc1ubTVPLf6zVe3ptZYdFiHD2B6HYM",
				"6Z9jdUUZjiHVGaWnqas9EiKM8lss29nXeFZxDiov");
		super.onCreate();
	}
}
