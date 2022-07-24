package com.mosesian.riskcampaigncalculator

import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView

class TerritoryViewArray() {
	
	val territoryArray = ArrayList<Territory>()
	val viewArray = ArrayList<View>()
	
	fun add(territoryView: View): View {
		
		// define the territory object
		var t = Territory()
		t.attackingArmies = -1
		t.defendingArmies = -1
		
		// add the territory to the arrays
		territoryArray.add(t)
		viewArray.add(territoryView)
		
		// update the row
		updateRow(viewArray.size - 1)

		return territoryView
	}
	
	fun updateAll() {
		for(i in 0..(viewArray.size-1))
			updateRow(i)
	}
	
	fun updateRow(row: Int) {
		// get the rows from the arrays
		val t = territoryArray.get(row)
		Log.v(TAG, t.expectedRemaining.toString())
		val territoryView = viewArray.get(row)
		
		// get the elements
		val atk2: EditText = territoryView.findViewById(R.id.atk2)
		val def2: EditText = territoryView.findViewById(R.id.def2)
		val odd2: TextView = territoryView.findViewById(R.id.odd2)
		val rem2: TextView = territoryView.findViewById(R.id.rem2)
		
		// update the elements
		atk2.setText( if (t.attackingArmies == -1) "" else t.attackingArmies.toString())
		def2.setText( if (t.defendingArmies == -1) "" else t.defendingArmies.toString())
		odd2.setText( if (t.oddsOfWinning == -1.0) "---" else t.getOddsString())
		rem2.setText( 
			if (t.expectedRemaining == -1.0) "---" 
			else if (t.expectedRemaining == -2.0) "?"
			else t.getRemainingString()
		)
			
		// show/hide the attacker armies input
		if (row == 0 || territoryArray.get(row-1).attackingArmies != -1)
			atk2.setVisibility(View.VISIBLE)
		else
			atk2.setVisibility(View.INVISIBLE)
		
	}
	
	fun updateData() {
		viewArray.forEachIndexed { index: Int, territoryView: View ->
			//get the attackers
			val atkEditText: EditText = territoryView.findViewById(R.id.atk2)
			var attackers: Int
			try {
				attackers = atkEditText.getText().toString().toInt()
			} catch (nfe: NumberFormatException) {
				attackers = -1
			}
				
			//get the defenders
			val defEditText : EditText = territoryView.findViewById(R.id.def2)
			var defenders: Int
			try {
				defenders = defEditText.getText().toString().toInt()
			} catch (nfe: NumberFormatException) {
				defenders = -1
			}
			
			// update the territory data based on the edittexts
			val territory = territoryArray[index]
			territory.attackingArmies = attackers
			territory.defendingArmies = defenders
			
		}
	}
	
	override fun toString(): String {
		var str: String = ""
		territoryArray.forEach {territory -> 
			str += territory.toString()
			str += "\n"
		}
		return str
	}
	
}