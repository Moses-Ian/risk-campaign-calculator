package com.mosesian.riskcampaigncalculator

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.view.View


class FightOdds : AppCompatActivity() {
	
	var currentAttackers: Int = 0
	var currentDefenders: Int = 0
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.fight_odds)
		
		//get the preferences
		//TODO: get preferences and set useRisiko
		val useRisiko = false
		
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
		Log.v(TAG, "create odds matrix")
		Log.v(TAG, currentAttackers.toString())
		Log.v(TAG, currentDefenders.toString())
		
		updateLayout()
	}
	
	fun updateLayout() {
		// populate fields
		val currentFightText: TextView = findViewById(R.id.out_current0)
		currentFightText.setText("$currentAttackers v $currentDefenders")
		
		
		//show the view
		(findViewById(R.id.reveal_chances0) as View).setVisibility(0)
	}
}














































