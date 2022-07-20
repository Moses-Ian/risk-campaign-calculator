package com.mosesian.riskcampaigncalculator

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class FightOdds : AppCompatActivity() {
	
	var useRisiko = false
	var currentAttackers: Int = 0
	var currentDefenders: Int = 0
	lateinit var odds: Array<Array<Double>>
	lateinit var remainingAttackers: Array<Array<Double>>
	
	//colors
	var BLUE = 0
	var DARK_GREY = 0
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.fight_odds)
		
		//get the preferences
		//TODO: get preferences and set useRisiko
		useRisiko = false
		
		//get the page elements
		val calcButton : Button = findViewById(R.id.calculate0)
		// button1 = (Button) findViewById(R.id.a_lose_2);
		// button2 = (Button) findViewById(R.id.both_lose);
		// button3 = (Button) findViewById(R.id.d_lose_2);
		// button4 = (Button) findViewById(R.id.risiko_extra);
		// tv1 = (TextView) findViewById(R.id.out_current0);	//atk v def textview
		// tv2 = (TextView) findViewById(R.id.out_chances0);
		// tv3 = (TextView) findViewById(R.id.out_remaining0);
		
		//set the listeners
		calcButton.setOnClickListener {calcButton -> initialCalculateClick(calcButton)}
		
		//set colors
		BLUE = ContextCompat.getColor(this, R.color.blue)
		DARK_GREY = ContextCompat.getColor(this, R.color.dark_grey)
		
		//created by option2
		
		
		//savedInstanceState stuff
		
		
		
		Log.v(TAG, "FightOdds")
	}
	
	fun initialCalculateClick(calcButton: View) {
		hideKeyboard(calcButton)
		
		//get the attackers
		val atkEditText = findViewById(R.id.atk_entry_0) as EditText
		var attackers: Int
		try {
			attackers = atkEditText.getText().toString().toInt()
			if (attackers <= 0)
				throw NumberFormatException()
		} catch (nfe: NumberFormatException) {
			Toast.makeText(applicationContext, getString(R.string.atk_entry_invalid), Toast.LENGTH_SHORT).show()
			return
		}
			
		//get the defenders
		val defEditText : EditText = findViewById(R.id.def_entry_0)
		var defenders: Int
		try {
			defenders = defEditText.getText().toString().toInt()
			if (defenders <= 0)
				throw NumberFormatException()
		} catch (nfe: NumberFormatException) {
			Toast.makeText(applicationContext, getString(R.string.def_entry_invalid), Toast.LENGTH_SHORT).show()
			return
		}
		
		currentAttackers = attackers
		currentDefenders = defenders
		createOddsMatrix()
		
		
		Log.v(TAG, "calculate")
	}
	
	fun hideKeyboard(view: View) {
		val keyboard = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
		keyboard.hideSoftInputFromWindow(view.windowToken, 0)		
	}
	
	fun createOddsMatrix() {
		//initialize the matrixes
		val atkSize = if (currentAttackers >= 1000) 1001 else currentAttackers+1
		val defSize = if (currentDefenders >= 1000) 1001 else currentDefenders+1
		odds = Array(atkSize, {
				Array(defSize, {0.0})
		})
		remainingAttackers = Array(atkSize, {
				Array(defSize, {0.0})
		})
		
		//populate them
		useRisiko = true
		populateOddsMatrix(odds, useRisiko)
		populateRemainingAttackers(remainingAttackers, useRisiko)
		
		
		
		
		
		
		
		updateLayout()
	}
	
	fun updateLayout() {
		//get view elements
		val currentFightText: TextView = findViewById(R.id.out_current0)
		val oddsText: TextView = findViewById(R.id.out_chances0)
		val remainingAttackersText: TextView = findViewById(R.id.out_remaining0)

		//prepare the data
		var chances: Double
		var remaining: Double
		if (isEstimate()) {
			// chances = estimateProbability(currentAttackers, currentDefenders, useRisiko)
			chances = 1.0
			remaining = 0.0
		} else {
			chances = odds[currentAttackers][currentDefenders] * 100
			remaining = remainingAttackers[currentAttackers][currentDefenders]
		}

		// populate fields
		currentFightText.setText("$currentAttackers v $currentDefenders")
		oddsText.setText(String.format("%.2f%%", chances))
		remainingAttackersText.setText(if (isEstimate()) "?" else String.format("%.1f", remaining))
		
		// set text color
		if (isEstimate()) {
			oddsText.setTextColor(BLUE)
			remainingAttackersText.setTextColor(BLUE)
		} else {
			oddsText.setTextColor(DARK_GREY)
			remainingAttackersText.setTextColor(DARK_GREY)
		}		
		
		//show the view
		(findViewById(R.id.reveal_chances0) as View).setVisibility(0)
	}
	
	fun isEstimate(): Boolean {
		return currentAttackers >= 1000 || currentDefenders >= 1000
	}
}














































