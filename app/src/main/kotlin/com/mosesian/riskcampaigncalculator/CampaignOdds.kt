package com.mosesian.riskcampaigncalculator

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class CampaignOdds : AppCompatActivity() {

	//colors
	var BLUE = 0
	var DARK_GREY = 0
	
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

		//set colors
		BLUE = ContextCompat.getColor(this, R.color.blue)
		DARK_GREY = ContextCompat.getColor(this, R.color.dark_grey)
		
		//initialize
		initialize()
		
		//savedInstanceState
		
	}
	
	fun initialize() {
		Log.v(TAG, "initialize")
		val campaignList: LinearLayout = findViewById(R.id.campaign_list)
		campaignList.removeAllViews()
		
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