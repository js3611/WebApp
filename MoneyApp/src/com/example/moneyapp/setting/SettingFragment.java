package com.example.moneyapp.setting;


import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	public static final String PREF_VIEW_MODE = "pref_view_mode";
	private String TAG = "Setting Fragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.setting_layout);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		  if (key.equals(PREF_VIEW_MODE)) {
	            // Set summary to be the user-description for the selected value
			  Log.v(TAG, key+" changed to " +sharedPreferences.getBoolean(key, false));
	    		    		
	        }

		
	}
	
}
