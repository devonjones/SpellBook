package org.evilsoft.pathfinder.spellbook.login;

import java.util.Locale;

import org.evilsoft.pathfinder.spellbook.MainActivity;
import org.evilsoft.pathfinder.spellbook.R;
import org.evilsoft.pathfinder.spellbook.login.activity.BaseActivity;
import org.evilsoft.pathfinder.spellbook.login.forgotpassword.ForgotPasswordDialogFragment;
import org.evilsoft.pathfinder.spellbook.login.model.user.UserManager;
import org.evilsoft.pathfinder.spellbook.login.model.user.authenticate.AuthenticateUserErrorEvent;
import org.evilsoft.pathfinder.spellbook.login.model.user.authenticate.AuthenticateUserStartEvent;
import org.evilsoft.pathfinder.spellbook.login.model.user.authenticate.AuthenticateUserSuccessEvent;
import org.evilsoft.pathfinder.spellbook.login.model.user.authenticate.UserForgotPasswordErrorEvent;
import org.evilsoft.pathfinder.spellbook.login.model.user.authenticate.UserForgotPasswordStartEvent;
import org.evilsoft.pathfinder.spellbook.login.model.user.authenticate.UserForgotPasswordSuccessEvent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.Parse;
import com.squareup.otto.Subscribe;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well. Based loosley on the default Login template.
 * 
 * @author Trey Robinson
 */
public class LoginActivity extends BaseActivity {
	public static final String PREFS_NAME = "loginPrefs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Parse.initialize(this, "J8Mm9rHeYfzc1ubTVPLf6zVe3ptZYdFiHD2B6HYM",
				"6Z9jdUUZjiHVGaWnqas9EiKM8lss29nXeFZxDiov");

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		if (settings.contains("username")) {
			attemptLogin();
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction
				.replace(R.id.main_view, LoginFragment.newInstance());
		fragmentTransaction.commit();
	}

	/**
	 * Attempts to sign in.
	 */
	public void attemptLogin() {

		// Store values at the time of the login attempt.
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String username = settings.getString("username", "n/a");
		String password = settings.getString("password", "n/a");
		// perform the user login attempt.
		UserManager.getInstance().authenticate(
				username.toLowerCase(Locale.getDefault()), password);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_forgot_password:
			forgotPassword();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Open the forgotPassword dialog
	 */
	private void forgotPassword() {
		FragmentManager fm = getSupportFragmentManager();
		ForgotPasswordDialogFragment forgotPasswordDialog = new ForgotPasswordDialogFragment();
		forgotPasswordDialog.show(fm, null);
	}

	@Subscribe
	public void onSignInStart(AuthenticateUserStartEvent event) {
		showProgress(true, getString(R.string.login_progress_signing_in));
	}

	@Subscribe
	public void onSignInSuccess(AuthenticateUserSuccessEvent event) {
		showProgress(false, getString(R.string.login_progress_signing_in));
		Intent loginSuccess = new Intent(this, MainActivity.class);
		startActivity(loginSuccess);
		finish();
	}

	@Subscribe
	public void onSignInError(AuthenticateUserErrorEvent event) {
		showProgress(false, getString(R.string.login_progress_signing_in));
	}

	@Subscribe
	public void onForgotPasswordStart(UserForgotPasswordStartEvent event) {
		showProgress(true, getString(R.string.login_progress_signing_in));
	}

	@Subscribe
	public void onForgotPasswordSuccess(UserForgotPasswordSuccessEvent event) {
		showProgress(false, getString(R.string.login_progress_signing_in));
		Toast toast = Toast.makeText(this,
				"A password reset email has been sent.", Toast.LENGTH_LONG);
		toast.show();
	}

	@Subscribe
	public void onForgotPasswordError(UserForgotPasswordErrorEvent event) {
		showProgress(false, getString(R.string.login_progress_signing_in));
		Toast toast = Toast.makeText(this,
				"An error has occured. Please try again.", Toast.LENGTH_LONG);
		toast.show();
	}
}
