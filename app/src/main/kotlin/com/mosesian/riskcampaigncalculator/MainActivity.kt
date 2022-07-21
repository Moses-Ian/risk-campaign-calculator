package com.mosesian.riskcampaigncalculator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
// import androidx.preference.PreferenceManager
import android.util.Log
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
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
		val lv : ListView = findViewById(R.id.list);
		lv.setAdapter(MainAdapter(this, R.layout.list_item, mainOptions))
		lv.setTextFilterEnabled(true)
		
		//set the listview click handler
		lv.setOnItemClickListener {parent, view, position, id ->
			val intent = when(id) {
				0L -> Intent(this, FightOdds::class.java)
				1L -> Intent(this, ArmiesNeeded::class.java)
				else -> Intent(this, CampaignOdds::class.java)
			}
			
			startActivity(intent)
		}
		
		//log if successful
		Log.v(TAG, "Hello")
	}
}