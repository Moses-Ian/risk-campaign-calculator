package com.mosesian.riskcampaigncalculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
// import androidx.preference.PreferenceManager
import android.util.Log
import android.widget.ListView

var TAG = "RISK"

class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main)
		
		//initialize the preferences
		// PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
		
		//set the listview content
		val mainOptions = getResources().getStringArray(R.array.main_options_array)
		Log.v(TAG, mainOptions[0])
		Log.v(TAG, mainOptions[1])
		Log.v(TAG, mainOptions[2])
		
		val lv : ListView = findViewById(R.id.list);
		//todo: implement MainAdapter (a class I wrote)
		lv.setAdapter(MainAdapter(this, R.layout.list_item, mainOptions))
		lv.setTextFilterEnabled(true)
		
		//set the listview click handler
		//todo: that
		
		//log if successful
		Log.v(TAG, "Hello")
	}
}