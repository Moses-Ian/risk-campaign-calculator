package com.mosesian.riskcampaigncalculator

class Territory {
	var attackingArmies = -1
	var defendingArmies = -1
	var oddsOfWinning = -1.0
	var expectedRemaining = -1.0
	var estimate = false

	fun getOddsString(): String { return String.format("%.2f%%", oddsOfWinning * 100) }
	
	fun getRemainingString(): String { return String.format("%.1f", expectedRemaining) }
	
	override fun toString(): String {
	return "$attackingArmies $defendingArmies ${getOddsString()} ${getRemainingString()}"
	}
}