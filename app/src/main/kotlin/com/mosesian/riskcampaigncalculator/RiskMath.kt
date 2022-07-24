package com.mosesian.riskcampaigncalculator

import android.util.Log

val TOLERANCE = 0.99

//commonly accepted values for single-throw probabilities for true dice		

// 1v1
val p11a =  0.58333333;		//probability attacker loses an army
val p11d =  0.41666667;		//probability defender loses an army

//1v2
val p12a =  0.74537037;
val p12d =  0.25462963;

//2v1
val p21a =  0.42129630;
val p21d =  0.57870370;

//2v2
val p22a2 = 0.44830247;		//probability attacker loses 2 armies
val p22ad = 0.32407407;		//probability attacker and defender each lose 1
val p22d2 = 0.22762346;		//probability defender loses 2 armies

//3v1
val p31a =  0.34027778;
val p31d =  0.65972222;

//3v2
val p32a2 = 0.29256687;
val p32ad = 0.33577675;
val p32d2 = 0.37165638;

//risiko values -> defender allowed 3 dice
//1v3
val p13a =   0.826388889;
val p13d =   0.173611111;

//2v3
val p23a2 =  0.619341564;
val p23ad =  0.254758230;
val p23d2 =  0.125900206;

//3v3
val p33a3 =  0.383037551;	//prob attacker loses 3 armies
val p33a2d = 0.264660494; //prob attacker loses 2 and defender loses 1
val p33ad2 = 0.214699074;	//prob attacker loses 1 and defender loses 2
val p33d3 =  0.137602881;	//prob defender loses 3


fun populateOddsMatrix(odds: Array<Array<Double>>, useRisiko: Boolean = false) {
	Log.v(TAG, "populate odds matrix")
	if (!useRisiko)
		odds.forEachIndexed { atkIndex, atkArray -> 
			atkArray.forEachIndexed { defIndex, _ ->
				findOdds(odds, atkIndex, defIndex)
			}
		}
	else
		odds.forEachIndexed { atkIndex, atkArray -> 
			atkArray.forEachIndexed { defIndex, _ ->
				findOddsRisiko(odds, atkIndex, defIndex)
			}
		}
		
}

//odds, atkIndex, defIndex
fun findOdds(odds: Array<Array<Double>>, a: Int, d: Int) {
	//can't do recursion - terribly unoptimal
	//fills the cell with corresponding data
	//outside, fill from left to right, top to bottom
	//NOTE: garbage in, garbage out
	//p32 happens most often, so it goes first
	odds[a][d] =
		// 3+ v 2+
		if (a >= 3 && d >= 2) p32a2 * odds[a-2][d] + p32ad * odds[a-1][d-1] + p32d2 * odds[a][d-2]
		// either side has zero
		else if (a == 0) 0.0
		else if (d == 0) 1.0
		// 1v1
		else if (a == 1 && d == 1) p11d
		// 1v2+
		else if (a == 1) p12d * odds[1][d-1]
		// 2v1
		else if (a == 2 && d == 1) p21a * odds[1][1] + p21d
		// 2v2+
		else if (a == 2) p22ad * odds[1][d-1] + p22d2 * odds[2][d-2]
		//3+v1
		else if (d == 1) p31a * odds[a-1][1] + p31d
		//shouldn't happen
		else -1.0
}

//odds, atkIndex, defIndex
fun findOddsRisiko(odds: Array<Array<Double>>, a: Int, d: Int) {
	if (d <= 2) 
		return findOdds(odds, a, d)
	
	// 3+v3+
	odds[a][d] = 
		if (a >= 3) p33a3 * odds[a-3][d] + p33a2d * odds[a-2][d-1] + p33ad2 * odds[a-1][d-2] + p33d3 * odds[a][d-3]
	// 2v3+
	else if (a == 2) p23a2 * odds[a-2][d] + p23ad * odds[a-1][d-1] + p23d2 * odds[a][d-2]
	// 1v3+
	else if (a == 1) p13a * odds[a-1][d] + p13d * odds[a][d-1]
	// 0v3+
	else if (a == 0) 0.0
	// shouldn't happen
	else -1.0
}

fun populateRemainingAttackers(remainingAttackers: Array<Array<Double>>, useRisiko: Boolean = false) {
	// first, find the expected amount the attacker will lose
	// remainingAttackers will temporarily hold the value `attackersLost`
	if (!useRisiko)
		remainingAttackers.forEachIndexed { atkIndex, atkArray ->
			atkArray.forEachIndexed { defIndex, _ -> 
				findAtkLoss(remainingAttackers, atkIndex, defIndex)
			}
		}
	else
		remainingAttackers.forEachIndexed { atkIndex, atkArray ->
			atkArray.forEachIndexed { defIndex, _ -> 
				findAtkLossRisiko(remainingAttackers, atkIndex, defIndex)
			}
		}
	
	// then, find the expected amount the attacker will keep
	remainingAttackers.forEachIndexed { atkIndex, atkArray ->
		atkArray.forEachIndexed { defIndex, attackersLost -> 
			remainingAttackers[atkIndex][defIndex] = atkIndex - attackersLost
		}
	}
}

// atkLost: an array filled with zeros or attackers lost
// a, d: atkIndex, defIndex
fun findAtkLoss(atkLost: Array<Array<Double>>, a: Int, d: Int) {
	atkLost[a][d] =
		// 3+v2+ -> If you lose two, then you lose however many you'll lose in the next round plus 2
		if (a >= 3 && d >= 2) p32a2 * (2+atkLost[a-2][d]) + p32ad * (1+atkLost[a-1][d]) + p32d2 * atkLost[a][d-2]
		// match is over -> you won't lose any more
		else if (a == 0 || d == 0) 0.0
		// 1v1 -> average loss is the same as the probability of losing 1 army
		else if (a == 1 && d == 1) p11a
		else if (a == 2 && d == 1) p21a * (1+p11a)
		else if (a == 1) p12a + p12d * atkLost[a][d-1]
		else if (a == 2) p22a2 * 2 + p22ad * (1+atkLost[a-1][d-1]) + p22d2 * atkLost[a][d-2]
		else if (d == 1) p31a * (1+atkLost[a-1][d])
		else -1.0
}

fun findAtkLossRisiko(atkLost: Array<Array<Double>>, a: Int, d: Int) {
	if (d <= 2)
		return findAtkLoss(atkLost, a, d)
	
	atkLost[a][d] =
		if (a >= 3) p33a3 * (3+atkLost[a-3][d]) + p33a2d * (2+atkLost[a-2][d-1]) + p33ad2 * (1+atkLost[a-1][d-2]) + p33d3 * atkLost[a][d-3]
		else if (a == 2) p23a2 * (2+atkLost[a-2][d]) + p23ad * (1+atkLost[a-1][d-1]) + p23d2 * (atkLost[a][d-2])
		else if (a == 1) p13a * (1+atkLost[a-1][d]) + p13d * atkLost[a][d-1]
		else -1.0
}

fun estimateProbability(attackers: Int, defenders: Int, useRisiko: Boolean = false) : Double {
	//I need to re-analyze my math here. I'm sure there's something simpler than what my old code was doing
	return 0.5
}

fun estimateRemaining(attackers: Int, defenders: Int, useRisiko: Boolean = false) : Double {
	//this is new
	return -2.0
}

fun pathAnalysis(territories: ArrayList<Territory>) {
	Log.v(TAG, "path analysis")
	// make sure there are actually armies to calculate
	if (territories[0].attackingArmies == -1 || territories[0].defendingArmies == -1)
		return
	
	// initialize the victory array
	var victory = emptyArray<Double>()
	
	// do math
	territories.forEachIndexed {index, territory -> 
		// if garbage in, set default values
		if (territory.defendingArmies <= 0) {
			territory.oddsOfWinning = -1.0
			territory.expectedRemaining = -1.0
			return
		}			
		
		//if this is an estimate, or the previous was an estimate
		if (territory.attackingArmies >= 1000 || territory.defendingArmies >= 1000 || (index != 0 && territories[index-1].estimate)) {
			estimateAnalysis(index, territory, territories)
		} else {
			victory = territoryAnalysis(index, territory, territories, victory)
		}
	}
}

fun estimateAnalysis(index: Int, territory: Territory, territories: ArrayList<Territory>) {
	// the idea here is that each row's attackers will be the previous row's remaining armies
	// the defenders won't change
	// the odds will be the estimated odds
	// the expected remaining will also be estimated
	
	var attackers = territory.attackingArmies
	var defenders = territory.defendingArmies
	
	// if attackers == -1
	if (attackers == -1)
		attackers = territories[index-1].expectedRemaining.toInt()
	
	//estimate odds
	val odds = estimateProbability(attackers, defenders)
	val expRemaining = estimateRemaining(attackers, defenders)
	
	territory.attackingArmies = attackers
	territory.defendingArmies = defenders	//redundant but that's ok
	territory.oddsOfWinning = odds
	territory.expectedRemaining = expRemaining
	territory.estimate = true
}

fun territoryAnalysis(index: Int, territory: Territory, territories: ArrayList<Territory>, victory: Array<Double>): Array<Double> {
	
	var _victory = victory
	
	// if the user set the number of armies
	if (territory.attackingArmies != -1) {
		// calculate victory from a single number of attackers
		_victory = createVictoryArray(territory.attackingArmies, territory.defendingArmies)
	} else {
		// if this is a continuation
		// shift victory down
		for(i in 0..(victory.size-2))
			_victory[i] = victory[i+1]
		_victory[ victory.size-1 ] = 0.0
		
		// create a new victory array
		val newVictory = Array(_victory.size-1, {0.0})
		
		// for each possible number of attacking armies in victory...
		_victory.forEachIndexed { index, probability -> 
			if (probability <= 0) return newVictory
			
			// create a transitionOdds matrix
			val transitionOdds = createVictoryArray(index, territory.defendingArmies)
			
			// update the newVictory array
			for (i in 0..(transitionOdds.size-1))
				newVictory[i] += probability * transitionOdds[i]
		}
		
		// update victory
		_victory = newVictory
	}
	
	// calculate odds and expected remaining
	var odds = 0.0
	var expRemaining = 0.0
	for (i in 0..(_victory.size-1)) {
		odds += _victory[i]
		expRemaining += _victory[i] * i
	}
	
	// update the territory
	territory.oddsOfWinning = odds
	territory.expectedRemaining = expRemaining
	
	// return the victory array
	return _victory
}

fun createVictoryArray(attackers: Int, defenders: Int): Array<Double> {
	
	// transitionOdds has a 3 cell buffer to simplify calculations
	val transitionOdds = Array(attackers+3, {
		Array(defenders+3, {0.0})
	})
	transitionOdds[attackers][defenders] = 1.0
	for(i in attackers..0)
		for(j in defenders..0)
			transitionOdds[i][j] = findTransitionOdds(transitionOdds, i, j)

	// create the victory array...
	val victory = Array(attackers+1, {0.0})
	for(i in 0..(victory.size-1))
		victory[i] = transitionOdds[i][0]
	
	// ...and return it
	return victory
}

fun findTransitionOdds(transitionOdds: Array<Array<Double>>, atk: Int, def: Int): Double {
	return 0.33
}































