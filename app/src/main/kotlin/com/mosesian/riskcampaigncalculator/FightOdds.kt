package com.mosesian.riskcampaigncalculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log


class FightOdds : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.fight_odds)
		
		Log.v(TAG, "FightOdds")
	}
}