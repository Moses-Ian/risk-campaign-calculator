package com.mosesian.riskcampaigncalculator

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ArmiesNeeded : AppCompatActivity() {
	
	var useRisiko = false
	var defenderArmies = 0
	lateinit var odds: Array<Array<Double>>
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.armies_needed)

		//get preferences
		
		
		//get the elements
		//fuck me there's a graph
		val calcButton: Button = findViewById(R.id.calculate1)
		
		//set the click handler
		calcButton.setOnClickListener {calcButton -> calculate(calcButton)}
		
		
		//apparently the edit button is able to perform click??
		
		
		//savedInstanceState stuff

	}
	
	fun calculate(calcButton: View) {
		//hide the keyboard
		hideKeyboard(calcButton)
		
		//get number of defending armies
		val defEntry: EditText = findViewById(R.id.def_entry_1)
		val defenders: Int
		try {
			defenders = defEntry.getText().toString().toInt()
			if (defenders <= 0)
				throw NumberFormatException()
		} catch (nfe: NumberFormatException) {
			Toast.makeText(applicationContext, getString(R.string.atk_entry_invalid), Toast.LENGTH_SHORT).show()
			return
		}
		
		defenderArmies = defenders
		if (shouldEstimate())
			doEstimation()
		else
			doCalculation()

	}
	
	fun shouldEstimate(): Boolean {
		return defenderArmies >= 1000
	}
	
	fun doEstimation() {
		Log.v(TAG, "estimate")
	}

	fun doCalculation() {
		Log.v(TAG, "calculate")
		
		//initialize to a 0v0 fight
		odds = Array(1, {
				Array(1, {0.0})
		})
		var i = 0
		var j = 0
		var prev = 0
		
		do {
			extendOddsArray()
			
			for(i in prev..(odds.size-1)) {
				for(j in 0..(odds[0].size-1)) {
					findOdds(odds, i, j)
				}
			}
			
			i = odds.size-1
			j = odds[0].size-1
			Log.v(TAG, odds[i][j].toString())
		} while(odds[i][j] < TOLERANCE)
		
		//show results
		
	}
	
	fun extendOddsArray() {
		val temp = Array(odds.size + defenderArmies, {
			Array(defenderArmies+1, {0.0})
		})
		for(atkIndex in 0..(odds.size-1)) {
			for(defIndex in 0..(odds[0].size-1)) {
				temp[atkIndex][defIndex] = odds[atkIndex][defIndex]
			}
		}
		odds = temp
	}

	fun hideKeyboard(view: View) {
		val keyboard = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
		keyboard.hideSoftInputFromWindow(view.windowToken, 0)		
	}
	
}













































