package com.mosesian.riskcampaigncalculator

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
		
		// get the elements
		val atk2: EditText = territoryView.findViewById(R.id.atk2)
		val def2: EditText = territoryView.findViewById(R.id.def2)
		val odd2: TextView = territoryView.findViewById(R.id.odd2)
		val rem2: TextView = territoryView.findViewById(R.id.rem2)
		
		// update the elements
		atk2.setText( if (t.attackingArmies == -1) "" else t.attackingArmies.toString())
		def2.setText( if (t.defendingArmies == -1) "" else t.defendingArmies.toString())
		odd2.setText( if (t.oddsOfWinning == -1.0) "---" else t.getOddsString())
		rem2.setText( if (t.expectedRemaining == -1.0) "---" else t.getRemainingString())
			
		// show the row
		// i think i don't need to do this?
		
		return territoryView
	}
	
}