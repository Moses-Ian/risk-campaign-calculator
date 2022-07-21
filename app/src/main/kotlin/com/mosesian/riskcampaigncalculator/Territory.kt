package com.mosesian.riskcampaigncalculator

class Territory() {
	var attackingArmies = -1
	var defendingArmies = -1
	var oddsOfWinning = 0.0
	var expectedRemaining = 0.0

	fun getPercentWinning() { oddsOfWinning * 100 }
}