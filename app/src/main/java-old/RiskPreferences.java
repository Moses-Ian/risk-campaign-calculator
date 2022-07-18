package net.phoenixramen.risk;

//this class has all of the onClick code and all of the garbage inside it
//so this really is enough code

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class RiskPreferences extends PreferenceActivity
{
	private static final String TAG = "pref";
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}