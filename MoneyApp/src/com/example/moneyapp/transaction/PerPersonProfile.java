package com.example.moneyapp.transaction;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.helpers.CustomHttpClient;
import com.example.helpers.DateGen;
import com.example.helpers.HttpReaders;
import com.example.helpers.MyToast;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;

/*
 * Class for per person mode, profile for each person who you owe to
 */
public class PerPersonProfile extends FragmentActivity implements PayDialog.NoticeDialogListener {

	/* Debug */
	private static final String TAG = "PerPersonProfile";
	private String errorMessage = "no error";

	private static final int NUM_PAGES = 2;
	private static final int PROFILE = 0;
	private static final int LOG = 1;
	
	/* Gives you animated effect. ViewPager uses PageAdapters */
	private ViewPager mPager;
	/* Used by view pager. provide it with "pages" (fragments) */
	private PagerAdapter mPagerAdapter;
	public ProfilePageFragment ppfragment;
	public LogPageFragment lpfragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_per_person_log_profile);

		// Enabling Up button
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	
		// Instantiate a ViewPager and a Pager Adapter
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new PerPersonLogProfileAdapter(getFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				/*
				 * When changing pages, reset the action bar actions since they
				 * are dependent on which page is currently active. An
				 * alternative approach is to have each fragment expose actions
				 * itself (rather than the activity exposing actions), but for
				 * simplicity, the activity provides the actions in this sample.
				 */
				invalidateOptionsMenu();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.profilelogslide, menu);

		menu.findItem(R.id.profileMenu).setEnabled(
				mPager.getCurrentItem() == LOG);
		menu.findItem(R.id.logMenu).setEnabled(
				mPager.getCurrentItem() == PROFILE);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Navigate "up" the demo structure to the launchpad activity.
			// See http://developer.android.com/design/patterns/navigation.html
			// for more.
			NavUtils.navigateUpTo(this, getIntent().setClass(this, MainMenu.class));
			return true;

		case R.id.profileMenu:
			// Go to the previous step in the wizard. If there is no previous
			// step,
			// setCurrentItem will do nothing.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			return true;

		case R.id.logMenu:
			// Advance to the next step in the wizard. If there is no next step,
			// setCurrentItem
			// will do nothing.
			mPager.setCurrentItem(mPager.getCurrentItem() + 1);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			// If the user is currently looking at the first step, allow the
			// system to handle the Back button. This calls finish() on this
			// activity and pops the
			// back stack.
			super.onBackPressed();
			//Start new activity of PerPerson
/*			Intent i = getIntent();
			i.setClass(getApplicationContext(), PerPerson.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); */
		} else { // Otherwise,select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}
	

	/* Adapter which gives you either log page or profile page */
	private class PerPersonLogProfileAdapter extends FragmentStatePagerAdapter {

		public PerPersonLogProfileAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			switch (pos) {
			case PROFILE: 
				ppfragment = ProfilePageFragment.create();
				return ppfragment;
			case LOG:
				lpfragment = LogPageFragment.create();
				return lpfragment;
			}
			return null;
		}

		@Override
		public int getCount() {

			return NUM_PAGES;
		}

	}
	
	
	public void handlePayment(View view) {
		Log.v(TAG, "Making a payment");
        PayDialog payD = PayDialog.newInstance(MainMenu.PER_PERSON_VIEW);
        payD.show(getFragmentManager(), "Payment");
	
	}

	@Override
	public void onDialogPartialClick(DialogFragment dialog) {
		//Partial Payment
		ppfragment.makePartialPayment();
		
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		//Do nothing
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		//Do nothing
	}

	@Override
	public void onDialogFullyClick(DialogFragment dialog) {
		//Make a payment
		//new MakePayment().execute();
		ppfragment.makeFullPayment();
	}

	public void nudge_button(View view) {
		ppfragment.nudge_button();
		
	}
	
}
