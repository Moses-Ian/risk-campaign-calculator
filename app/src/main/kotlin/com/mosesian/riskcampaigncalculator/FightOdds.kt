package com.mosesian.riskcampaigncalculator

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
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
	
	//match state
	enum class BattleState {
		MATCH_OVER, ONE_LOSS, TWO_LOSS, THREE_LOSS
	}
	var state: BattleState = BattleState.MATCH_OVER
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.fight_odds)
		
		//get the preferences
		//TODO: get preferences and set useRisiko
		useRisiko = false
		
		//get the page elements
		val calcButton : Button = findViewById(R.id.calculate0)
		val aLose2Btn :      Button = findViewById(R.id.a_lose_2)
		val bothLoseBtn :    Button = findViewById(R.id.both_lose)
		val dLose2Btn :      Button = findViewById(R.id.d_lose_2)
		val risikoExtraBtn : Button = findViewById(R.id.risiko_extra)
		val defEditText : EditText = findViewById(R.id.def_entry_0)
		
		//set the listeners
		calcButton.setOnClickListener {calcButton -> initialCalculateClick(calcButton)}
		
		aLose2Btn.setOnClickListener { updateOdds(1) }
		bothLoseBtn.setOnClickListener { updateOdds(2) }
		dLose2Btn.setOnClickListener { updateOdds(3) }
		risikoExtraBtn.setOnClickListener { updateOdds(4) }
		defEditText.setOnEditorActionListener { view, actionId, event -> 
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				calcButton.performClick()
				true
			} else 
				false
		}
		
		//set colors
		BLUE = ContextCompat.getColor(this, R.color.blue)
		DARK_GREY = ContextCompat.getColor(this, R.color.dark_grey)
		
		//created by option2
		
		
		//savedInstanceState stuff
		// if (savedInstanceState != null) {
			// val list = savedInstanceState.getIntArray("currentFight")
			// if (list != null) {
				// currentAttackers = list[0]
				// currentDefenders = list[1]
				// createOddsMatrix()
			// }
		// }
		
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
		populateOddsMatrix(odds, useRisiko)
		populateRemainingAttackers(remainingAttackers, useRisiko)
		
		//update the page
		updateLayout()
	}
	
	fun updateOdds(caller: Int) {
		// update the army counts
		when(state) {
			BattleState.ONE_LOSS -> {
				if (caller == 1)
					currentAttackers--
				else
					currentDefenders--
			}
			BattleState.TWO_LOSS -> {
				if (caller == 1)
					currentAttackers -= 2
				else if (caller == 2) {
					currentAttackers--
					currentDefenders--
				} else
					currentDefenders -= 2
			}
			BattleState.THREE_LOSS -> {
				if (caller == 1)
					currentAttackers--
				else if (caller == 2) {
					currentAttackers -= 2
					currentDefenders--
				} else if (caller == 3) {
					currentAttackers--
					currentDefenders -= 2
				} else
					currentDefenders -= 3
			}
			BattleState.MATCH_OVER -> {}
		}
		
		//if created by option2, do something
		//something
		
		//update the page
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
			chances = estimateProbability(currentAttackers, currentDefenders, useRisiko) * 100
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
		
		// set the state
		state = 
			if (useRisiko && currentAttackers >= 3 && currentDefenders >= 3) BattleState.THREE_LOSS
			else if (currentAttackers >= 2 && currentDefenders >= 2) BattleState.TWO_LOSS
			else if (currentAttackers >= 1 && currentDefenders >= 1) BattleState.ONE_LOSS
			else BattleState.MATCH_OVER
		
		// get the elements
		val aLose2Btn :      Button = findViewById(R.id.a_lose_2)
		val bothLoseBtn :    Button = findViewById(R.id.both_lose)
		val dLose2Btn :      Button = findViewById(R.id.d_lose_2)
		val risikoExtraBtn : Button = findViewById(R.id.risiko_extra)
		
		// show/hide buttons depending on state
		when(state) {
			BattleState.MATCH_OVER -> {
				aLose2Btn.setVisibility(View.GONE)
				bothLoseBtn.setVisibility(View.GONE)
				dLose2Btn.setVisibility(View.GONE)
				risikoExtraBtn.setVisibility(View.GONE)
			}
			BattleState.ONE_LOSS -> {
				aLose2Btn.setText(R.string.a)
				bothLoseBtn.setText(R.string.d)
				
				aLose2Btn.setVisibility(View.VISIBLE)
				bothLoseBtn.setVisibility(View.VISIBLE)
				dLose2Btn.setVisibility(View.GONE)
				risikoExtraBtn.setVisibility(View.GONE)
			}
			BattleState.TWO_LOSS -> {
				aLose2Btn.setText(R.string.a2)
				bothLoseBtn.setText(R.string.ad)
				dLose2Btn.setText(R.string.d2)
				
				aLose2Btn.setVisibility(View.VISIBLE)
				bothLoseBtn.setVisibility(View.VISIBLE)
				dLose2Btn.setVisibility(View.VISIBLE)
				risikoExtraBtn.setVisibility(View.GONE)
			}
			BattleState.THREE_LOSS -> {
				aLose2Btn.setText(R.string.a3)
				bothLoseBtn.setText(R.string.a2d)
				dLose2Btn.setText(R.string.ad2)
				risikoExtraBtn.setText(R.string.d3)
				
				aLose2Btn.setVisibility(View.VISIBLE)
				bothLoseBtn.setVisibility(View.VISIBLE)
				dLose2Btn.setVisibility(View.VISIBLE)
				risikoExtraBtn.setVisibility(View.VISIBLE)
			}
		}
		
		
		
		//show the view
		(findViewById(R.id.reveal_chances0) as View).setVisibility(0)
	}
	
	fun isEstimate(): Boolean {
		return currentAttackers >= 1000 || currentDefenders >= 1000
	}

/* 	override fun onSaveInstanceState(outState: Bundle) {
		Log.v(TAG, "save instance")
		
		//save values to an array
		val list = ArrayList<Int>()
		list.add(currentAttackers)
		list.add(currentDefenders)
		
		//save that array to a bundle
		outState.putIntegerArrayList(
			"currentFight",
			list
		)
		
		//do your thing!
		super.onSaveInstanceState(outState)
	}
	
	override fun onResume() {
		super.onResume()
		Log.v(TAG, "resume")
		
		//hide the keyboard
		hideKeyboard(getWindow() as View)
		
		//preferences
		
		
		//if the preferences changed, we need to update everything
		
		
	}
 */}














































