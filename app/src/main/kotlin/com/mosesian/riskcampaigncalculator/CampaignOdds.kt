package com.mosesian.riskcampaigncalculator

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CampaignOdds : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.campaign_odds)
		
		//preferences
		
		
		// get the elements
		val calcButton: Button = findViewById(R.id.calculate2)
		val addRowButton: Button = findViewById(R.id.add2)
		val clearButton: Button = findViewById(R.id.clear2)
		
		// attach listeners
		calcButton.setOnClickListener {calcButton -> 
			hideKeyboard(calcButton)
			pathAnalysis()
		}
		
		addRowButton.setOnClickListener { addRow() }
		clearButton.setOnClickListener { initialize() }
	}
	
	fun initialize() {
		Log.v(TAG, "initialize")
	}
	
	fun addRow() {
		Log.v(TAG, "add row")
	}
	
	fun pathAnalysis() {
		Log.v(TAG, "path analysis")
	}

	fun hideKeyboard(view: View) {
		val keyboard = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
		keyboard.hideSoftInputFromWindow(view.windowToken, 0)		
	}
	
}