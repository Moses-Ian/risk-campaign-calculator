package com.mosesian.riskcampaigncalculator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class CampaignOdds : AppCompatActivity() {

	//colors
	var BLUE = 0
	var DARK_GREY = 0
	
	var useRisiko = true
	
	// Territory View Array
	lateinit var tvArray: TerritoryViewArray
	lateinit var campaignList: LinearLayout
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.campaign_odds)
		
		//preferences
		
		
		// get the elements
		val calcButton: Button = findViewById(R.id.calculate2)
		val addRowButton: Button = findViewById(R.id.add2)
		val clearButton: Button = findViewById(R.id.clear2)
		
		// attach listeners
		calcButton.setOnClickListener {_calcButton -> 
			hideKeyboard(_calcButton)
			setupPathAnalysis()
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
		// create the campaignlist
		campaignList = findViewById(R.id.campaign_list)
		campaignList.removeAllViews()

		// create the territory view array
		tvArray = TerritoryViewArray()
		
		// add a row
		addRow()
	}
	
	fun addRow() {
		Log.v(TAG, "add row")
		// create the territory view item
		val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		val territoryView = inflater.inflate(R.layout.territory_item, campaignList, false)
		
		// set the click listener
		territoryView.setOnClickListener { view: View -> startFightOdds(view) }
		
		// add it to the views
		campaignList.addView(territoryView)
		tvArray.add(territoryView)
		
		
	}
	
	fun setupPathAnalysis() {
		// update the data with the data from the views
		val result = tvArray.updateData()
		
		// extract the data
		val territory_data = tvArray.territoryArray
		
		// do the math
		pathAnalysis(territory_data, useRisiko)	// updates territory_data
		
		// update the views
		tvArray.updateAll()
		
	}
	
	fun startFightOdds(view: View) {
		Log.v(TAG, "start fight odds")
		// do something
		// this is the function defined on option2.java -> line 41
	}

	fun hideKeyboard(view: View) {
		val keyboard = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
		keyboard.hideSoftInputFromWindow(view.windowToken, 0)		
	}
	
}