/*
Contains methods common between classes of this package
such as:
	probability calculations
	options menu methods
TODO AFTER MAKING USER SETTINGS:
	make tolerance a user setting
*/
package net.phoenixramen.risk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
// import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.lang.String;

public class Common extends Activity
{	
	//logcat
	//final String LOGTAG = "Common";
	static final double TOLERANCE = .99;
	static final int REQUEST_CODE_FOR_OPTION2_AND_OPTION0 = 1;
	//web address for explanation of the maths
	static final String PROOF_URL = "http://www.phoenixramen.net/proof.html";
	//commonly accepted values for single-throw probabilities for true dice		
	static final double p11a =  0.58333333;		//1v1, probability attacker loses an army
	static final double p11d =  0.41666667;		//1v1, probability defender loses an army
	static final double p12a =  0.74537037;
	static final double p12d =  0.25462963;
	static final double p21a =  0.42129630;
	static final double p21d =  0.57870370;
	static final double p22a2 = 0.44830247;		//2v2, probability attacker loses 2 armies
	static final double p22ad = 0.32407407;		//2v2, probability attacker and defender each lose 1
	static final double p22d2 = 0.22762346;		//2v2, probability defender loses 2 armies
	static final double p31a =  0.34027778;
	static final double p31d =  0.65972222;
	static final double p32a2 = 0.29256687;
	static final double p32ad = 0.33577675;
	static final double p32d2 = 0.37165638;
	//risiko values
	static final double p13a =   0.826388889;
	static final double p13d =   0.173611111;

	static final double p23a2 =  0.619341564;
	static final double p23ad =  0.254758230;
	static final double p23d2 =  0.125900206;

	static final double p33a3 =  0.383037551;
	static final double p33a2d = 0.264660494;
	static final double p33ad2 = 0.214699074;
	static final double p33d3 =  0.137602881;
	
	
	
	//authored values for estimation
	//split up into 3 sections, like a spline: 0-.1 < .1-.9 < .9-1
	static final double[] bfSlopeLow =  {-83.06260475519534 ,18.99816014955087 ,-1.8198251510419632,1.3151302206558517};	//slope     = bfSlope[0]*prob^3 + bfSlope[1]*prob^2 + bfSlope[2]*prob + bfSlope[3];
	static final double[] bfItrcpLow =  {-23094.27520398637 ,5488.589900772121 ,-542.5620714027687 ,42.304465277596336};	//intercept = bfItrcp[0]*prob^3 + bfItrcp[1]*prob^2 + bfItrcp[2]*prob + bfItrcp[3];
	static final double[] bfSlope =     {-0.2264350340320248,0.3472165481600891,-0.3058969722835889,1.2664608388781802};	//slope     = bfSlope[0]*prob^3 + bfSlope[1]*prob^2 + bfSlope[2]*prob + bfSlope[3];
	static final double[] bfItrcp =     {-76.64280381937392 ,121.11525394826177,-101.48121235669154,29.034010616463572};	//intercept = bfItrcp[0]*prob^3 + bfItrcp[1]*prob^2 + bfItrcp[2]*prob + bfItrcp[3];
	static final double[] bfSlopeHigh = {-62.14814841396642 ,172.16530441139187,-159.28923252014593,50.3199707662367  };	//slope     = bfSlope[0]*prob^3 + bfSlope[1]*prob^2 + bfSlope[2]*prob + bfSlope[3];
	static final double[] bfItrcpHigh = {-16369.620039642587,45010.817394221216,-41353.80802369885 ,12672.70572883051 };	//intercept = bfItrcp[0]*prob^3 + bfItrcp[1]*prob^2 + bfItrcp[2]*prob + bfItrcp[3];
	
	static final double[] bfSlopeHighRisiko = {-37.178484939859615 ,102.78527307765984 ,-94.90178148898845  ,29.806357747180467};	//r2 = NaN
	static final double[] bfItrcpHighRisiko = {-12171.423446578057 ,33554.25070877108  , -30911.04093970501 ,9499.223268138068 };	//r2 = NaN
	static final double[] bfSlopeRisiko =     {-0.14365940589886383,0.22153617020796684,-0.18681400110658433,0.6407578014311719};   //r2 = 0.9998498837291081
	static final double[] bfItrcpRisiko =     {-64.06666211870991  ,99.60990850700779  ,-81.02353249543751  ,24.18153677448606 };   //r2 = 0.9998036116202338
	static final double[] bfSlopeLowRisiko =  {-60.36012814396143  ,12.922847679608957 ,-1.1371551626168828 ,0.6680047849765494};   //r2 = 0.9920531885809855
	static final double[] bfItrcpLowRisiko =  {-13920.07619297441  ,3618.029076872195  ,-392.3918185400332  ,34.07748886208883 };   //r2 = 0.9916970259490339

	public static void populateOddsMatrix(double[][] array)
	{
		for(int i=0; i<array.length; i++)
			for(int j=0; j<array[0].length; j++)
				array[i][j] = findVal(array, i, j);
	}
	
	public static void populateOddsMatrixRisiko(double[][] array)
	{
		for(int i=0; i<array.length; i++)
			for(int j=0; j<array[0].length; j++)
				array[i][j] = findValRisiko(array, i, j);
	}
	
	public static void populatedExpectedRemaining(double[][] array)
	{
		for(int i=0; i<array.length; i++)
			for(int j=0; j<array[0].length; j++)
				array[i][j] = findAtkLoss(array,i,j);
		for(int i=0; i<array.length; i++)
			for(int j=0; j<array[0].length; j++)
				array[i][j] = i-array[i][j];
	}

	public static void populatedExpectedRemainingRisiko(double[][] array)
	{
		for(int i=0; i<array.length; i++)
			for(int j=0; j<array[0].length; j++)
				array[i][j] = findAtkLossRisiko(array,i,j);
		for(int i=0; i<array.length; i++)
			for(int j=0; j<array[0].length; j++)
				array[i][j] = i-array[i][j];
	}

	public static double[][] createTransitionMatrix(int atk, int def)
	{
		double[][] trans = new double[atk+3][def+3];	//it needs a 2 cell buffer to simplify checks and calculations
		trans[atk][def] = 1;							//100% chance of transitioning from state A to A
		for(int i=atk; i>=0; i--)
			for(int j= (i!=atk?def:def-1); j>=0; j--)
				trans[i][j] = findTransitionOdds(trans, i, j);	//fill it up
		return trans;
	}
	
	public static double[][] createTransitionMatrixRisiko(int atk, int def)
	{
		double[][] trans = new double[atk+4][def+4];	//it needs a 3 cell buffer to simplify checks and calculations
		trans[atk][def] = 1;							//100% chance of transitioning from state A to A
		for(int i=atk; i>=0; i--)
			for(int j= (i!=atk?def:def-1); j>=0; j--)
				trans[i][j] = findTransitionOddsRisiko(trans, i, j);	//fill it up
		for(int i=0; i<trans.length; i++)
			printArray("", trans[i]);
		return trans;
	}
	
	public static double findVal(double[][] array, int i, int j)	
	{
		//can't do recursion - terribly unoptimal
		//fills the cell with corresponding data
		//outside, fill from left to right, top to bottom
		//p32 happens most often, so it goes first
		if (i >= 3 && j >= 2)	return p32a2 * array[i-2][j] + p32ad * array[i-1][j-1] + p32d2 * array[i][j-2];
		else if (i == 0)			return 0;
		else if (j == 0)			return 1;
		else if (i == 1 && j == 1)	return p11d;
		else if (i == 1)			return p12d  * array[1][j-1];
		else if (i == 2 && j == 1)	return p21a  * array[1][1]   + p21d;
		else if (i == 2)			return p22ad * array[1][j-1] + p22d2 * array[2][j-2];
		else if (j == 1)			return p31a  * array[i-1][1] + p31d;
		else						return -1;
	}
	
/*	public static double findValRisiko(double[][] array, int i, int j)	
	{
		//this is the full version
		//can't do recursion - terribly unoptimal
		//fills the cell with corresponding data
		//outside, fill from left to right, top to bottom
		//p33 happens most often, so it goes first
		if (i >= 3 && j >= 3)	return p33a3 * array[i-3][j] + p33a2d * array[i-2][j-1] + p33ad2 * array[i-1][j-2] + p33d3 * array[i][j-3];
		if (i >= 3 && j == 2)	return p32a2 * array[i-2][j] + p32ad * array[i-1][j-1] + p32d2 * array[i][j-2];
		if (i >= 3 && j == 1)	return p31a * array[i-1][j] + p31d * array[i][j-1];
		if (i == 2 && j >= 3)	return p23a2 * array[i-2][j] + p23ad * array[i-1][j-1] + p23d2 * array[i][j-2];
		if (i == 2 && j == 2)	return p22a2 * array[i-2][j] + p22ad * array[1][j-1] + p22d2 * array[2][j-2];
		if (i == 2 && j == 1)	return p21a  * array[i-1][j] + p21d * array[i][j-1];
		if (i == 1 && j >= 3)	return p13a * array[i-1][j] + p13d * array[i][j-1];
		if (i == 1 && j == 2)	return p12a * array[i-1][j] + p12d * array[i][j-1];
		if (i == 1 && j == 1)	return p11a * array[i-1][j] + p11d * array[i][j-1];
		if (i == 0)				return 0;
		if (j == 0)				return 1;
		return -1;
	}
*/
	public static double findValRisiko(double[][] array, int i, int j)	
	{
		//full version is above
		//can't do recursion - terribly unoptimal
		//fills the cell with corresponding data
		//outside, fill from left to right, top to bottom
		//p33 happens most often, so it goes first
		if (i >= 3 && j >= 3)	return p33a3 * array[i-3][j] + p33a2d * array[i-2][j-1] + p33ad2 * array[i-1][j-2] + p33d3 * array[i][j-3];
		if (i >= 3 && j == 2)	return p32a2 * array[i-2][2] + p32ad * array[i-1][1] + p32d2;
		if (i >= 3 && j == 1)	return p31a * array[i-1][1] + p31d;
		if (i == 2 && j >= 3)	return p23ad * array[1][j-1] + p23d2 * array[2][j-2];		
		if (i == 2 && j == 2)	return p22ad * p11d + p22d2;
		if (i == 2 && j == 1)	return p21a  * p11d + p21d;
		if (i == 1 && j >= 3)	return p13d * array[1][j-1];
		if (i == 1 && j == 2)	return p12d * p11d;
		if (i == 1 && j == 1)	return p11d;
		if (i == 0)				return 0;
		if (j == 0)				return 1;
		return -1;
	}

	public static double findAtkLoss(double[][] array, int i, int j)
	{
		//outside, fill from left to right, top to bottom
		if (i >= 3 && j >= 2)	return p32a2 * (2+array[i-2][j]) + p32ad * (1+array[i-1][j-1]) + p32d2 * array[i][j-2];
		if (i == 0 && j == 0)	return -1;
		if (i == 0 || j == 0)	return 0;
		if (i == 1 && j == 1)	return p11a;
		if (i == 2 && j == 1)	return p21a  * (1+p11a);
		if (i == 1)				return p12a       + p12d  * array[1][j-1];
		if (i == 2)				return p22a2 *  2 + p22ad * (1+array[1][j-1]) + p22d2 * array[2][j-2];
		if (j == 1)				return p31a  * (1+array[i-1][1]);
		/*else*/				return -1;
	}

	public static double findAtkLossRisiko(double[][] array, int i, int j)
	{
		//outside, fill from left to right, top to bottom
		if (i >= 3 && j >= 3)	return p33a3 * (3+array[i-3][j]) + p33a2d * (2+array[i-2][j-1]) + p33ad2 * (1+array[i-1][j-2]) + p33d3 * array[i][j-3];
		if (i >= 3 && j == 2)	return p32a2 * (2+array[i-2][j]) + p32ad * (1+array[i-1][j-1]) + p32d2 * array[i][j-2];
		if (i >= 3 && j == 1)	return p31a * (1+array[i-1][j]) + p31d * array[i][j-1];
		if (i == 2 && j >= 3)	return p23a2 * (2+array[i-2][j]) + p23ad * (1+array[i-1][j-1]) + p23d2 * array[i][j-2];
		if (i == 2 && j == 2)	return p22a2 * (2+array[i-2][j]) + p22ad * (1+array[i-1][j-1]) + p22d2 * array[i][j-2];
		if (i == 2 && j == 1)	return p21a  * (1+array[i-1][j]) + p21d * array[i][j-1];
		if (i == 1 && j >= 3)	return p13a * (1+array[i-1][j]) + p13d * array[i][j-1];
		if (i == 1 && j == 2)	return p12a * (1+array[i-1][j]) + p12d * array[i][j-1];
		if (i == 1 && j == 1)	return p11a * (1+array[i-1][j]) + p11d * array[i][j-1];
		if (i == 0)				return 0;
		if (j == 0)				return 1;
		return -1;
	}

	public static double findTransitionOdds(double[][] array, int i, int j)
	{
		//fills the cell with corresponding data
		//outside, fill from right to left, bottom to top
		if (i >= 3 && j >= 2)	return p32a2 * array[i+2][j] + p32ad * array[i+1][j+1] + p32d2 * array[i][j+2];
		if (i == 0 && j == 0)	return -1;
		if (i == 0 && j == 1)	return p11a  * array[1][1];
		if (i == 1 && j == 0)	return p11d  * array[1][1];
		if (i == 1 && j == 1)	return p21a  * array[2][1] + p22ad * array[2][2] + p12d  * array[1][2];
		if (i == 2 && j == 0)	return p22d2 * array[2][2] + p21d  * array[2][1];
		if (i == 2 && j == 1)	return p31a  * array[3][1] + p32ad * array[3][2] + p22d2 * array[2][3];
		if (i == 2)				return p22d2 * array[2][j+2] + p32ad * array[3][j+1] + p32a2 * array[4][j];
		if (i == 0)				return p22a2 * array[2][j] + p12a  * array[1][j];
		if (i == 1)				return p32a2 * array[3][j] + p22ad * array[2][j+1] + p12d * array[1][j+1];
		if (j == 0)				return p32d2 * array[i][2] + p31d  * array[i][1];
		if (j == 1)				return p31a  * array[i+1][1] + p32ad * array[i+1][2]   + p32d2 * array[i][3];
		/*else*/				return -1;	
	}
	
	public static double findTransitionOddsRisiko(double[][] array, int i, int j)
	{
		//fills the cell with corresponding data
		//outside, fill from right to left, bottom to top
		if (i >= 3 && j >= 3)	return p33a3 * array[i+3][j]    + p33a2d * array[i+2][j+1] + p33ad2 * array[i+1][j+2] + p33d3 * array[i][j+3];
		if (i >= 3 && j == 2)	return p33a2d * array[i+2][j+1] + p33ad2 * array[i+1][j+2] + p33d3 * array[i][j+3]    + p32a2 * array[i+2][j];
		if (i >= 3 && j == 1)	return p33ad2 * array[i+1][j+2] + p33d3 * array[i][j+3]    + p32ad * array[i+1][j+1]  + p31a * array[i+1][j];
		if (i >= 3 && j == 0)	return p33d3 * array[i][j+3]    + p32d2 * array[i][j+2]    + p31d * array[i][j+1];
		if (i == 2 && j >= 3)	return p33a3 * array[i+3][j]    + p33a2d * array[i+2][j+1] + p33ad2 * array[i+1][j+2] + p23d2 * array[i][j+2];
		if (i == 2 && j == 2)	return p33a2d * array[i+2][j+1] + p33ad2 * array[i+1][j+2] + p32a2 * array[i+2][j]    + p23d2 * array[i][j+2];
		if (i == 2 && j == 1)	return p33ad2 * array[i+1][j+2] + p32ad * array[i+1][j+1]  + p23d2 * array[i][j+2]    + p31a * array[i+1][j];
		if (i == 2 && j == 0)	return p22d2 * array[i][j+2]    + p21d * array[i][j+1];
		if (i == 1 && j >= 3)	return p33a3 * array[i+3][j]    + p33a2d * array[i+2][j+1] + p23ad * array[i+1][j+1]  + p13d * array[i][j+1];
		if (i == 1 && j == 2)	return p33a2d * array[i+2][j+1]   + p32a2 * array[i+2][j]    + p23ad * array[i+1][j+1]  + p13d * array[i][j+1];
		if (i == 1 && j == 1)	return p22ad * array[i+1][j+1]  + p21a * array[i+1][j]     + p12d * array[i][j+1];
		if (i == 1 && j == 0)	return p11d * array[i][j+1];
		if (i == 0 && j >= 3)	return p33a3 * array[i+3][j]    + p23a2 * array[i+2][j]    + p13a * array[i+1][j];
		if (i == 0 && j == 2)	return p22a2 * array[i+2][j]    + p12a * array[i+1][j];
		if (i == 0 && j == 1)	return p11a * array[i+1][j];
		/*if(i==0 && j==0)*/	return -1;
	}
	
//Options Menu
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();    
		inflater.inflate(R.menu.options_menu, menu);    
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle item selection    
		int itemId = item.getItemId();
		switch (itemId)
		{
			case R.id.help:
				showHelp(getApplicationContext());            
				return true;
			case R.id.proof:
				showProof(getApplicationContext());
				return true;
			case R.id.preferences:
				showPreferences(getApplicationContext());
				return true;
			default:
				return super.onOptionsItemSelected(item);    
		}
	}
	
	public void showProof(Context context)
	{
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PROOF_URL));
		startActivity(browserIntent);
	}

	public void showHelp(Context context)
	{
		Intent intent = new Intent(context, Help.class);
		startActivity(intent);
	}

	public void showPreferences(Context context)
	{
		Intent intent = new Intent(context, RiskPreferences.class);
		startActivity(intent);
	}

//assistance methods
	private static void printMatrix(String s, double[][] arr)
	{
		for(int i=0; i<arr.length; i++)
		{
			StringBuilder sb = new StringBuilder();
			for(int j=0; j<arr[i].length; j++)
				sb.append(String.format("%.2f\t",arr[i][j]));
			// Log.i(s, sb.toString());
		}
	}
	
	private static void printArray(String s, double[] arr)
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<arr.length; i++)
			sb.append(String.format("%.4f\t",arr[i]));
		// Log.i("option2", sb.toString());
	}
	
//estimation methods
	public static double estimateProbability(int attackers, int defenders, boolean useRisiko)
	{
		double def;
		int section = 1;
		// guess 0.9
		// if it's bigger than this, use the high values
		def = calculateArmiesFromProbability(attackers, 0.9, false, useRisiko);
		if (def > defenders)
		{
			def = calculateArmiesFromProbability(attackers, 0.99, false, 2, useRisiko);	// if it's bigger than this, just return 100% 
			if (def > defenders)
				return 1;
			section = 2;
		}
		// guess 0.1
		// if it's less than this, use the low values
		def = calculateArmiesFromProbability(attackers, 0.1, false, useRisiko);
		if (def < defenders)
		{
			def = calculateArmiesFromProbability(attackers, 0.01, false, 0, useRisiko);	//if it's less than this, just return 0%
			if (def < defenders)
				return 0;
			section = 0;
		}
		// begin binary searching for the right answer
		double max = 1;
		double min = 0;
		double guess;
	
		do
		{	// yeah, this is suboptimal, fuck you
			guess = (max+min)/2;
				def = calculateArmiesFromProbability(attackers, guess, false, section, useRisiko);
			if (Math.round(def) == defenders && round(guess, 4) != guess)	// it rounds to the key and has sufficient precision
				return guess;
			if (def < defenders)
				max = guess;
			if (def > defenders)
				min = guess;
		}while(max-min > 0.00001);
		
		return guess;
	}
	
	public static double calculateArmiesFromProbability(int armies, double prob, boolean findAttackers, boolean useRisiko)
	{
		if(useRisiko)
			return calculateArmiesFromProbabilityRisiko(armies, prob, findAttackers, 1);
		else
			return calculateArmiesFromProbability(armies, prob, findAttackers, 1);
	}
	
	public static double calculateArmiesFromProbability(int armies, double prob, boolean findAttackers, int section, boolean useRisiko)
	{
		if(useRisiko)
			return calculateArmiesFromProbabilityRisiko(armies, prob, findAttackers, section);
		else
			return calculateArmiesFromProbability(armies, prob, findAttackers, section);
	}

	public static double calculateArmiesFromProbability(int armies, double prob, boolean findAttackers, int section)
	{
		double slope = 0;
		double intercept = 0;

		switch(section)
		{
			case 0:	//the stuff less than 0.1
				slope     = bfSlopeLow[0]*Math.pow(prob,3) + bfSlopeLow[1]*Math.pow(prob,2) + bfSlopeLow[2]*prob + bfSlopeLow[3];
				intercept = bfItrcpLow[0]*Math.pow(prob,3) + bfItrcpLow[1]*Math.pow(prob,2) + bfItrcpLow[2]*prob + bfItrcpLow[3];
				break;
			case 1:	//the stuff between 0.1 and 0.9
				slope     = bfSlope[0]*Math.pow(prob,3) + bfSlope[1]*Math.pow(prob,2) + bfSlope[2]*prob + bfSlope[3];
				intercept = bfItrcp[0]*Math.pow(prob,3) + bfItrcp[1]*Math.pow(prob,2) + bfItrcp[2]*prob + bfItrcp[3];
				break;
			case 2:	//the stuff greater than 0.9
				slope     = bfSlopeHigh[0]*Math.pow(prob,3) + bfSlopeHigh[1]*Math.pow(prob,2) + bfSlopeHigh[2]*prob + bfSlopeHigh[3];
				intercept = bfItrcpHigh[0]*Math.pow(prob,3) + bfItrcpHigh[1]*Math.pow(prob,2) + bfItrcpHigh[2]*prob + bfItrcpHigh[3];
				break;
		}
		if(findAttackers)
			return (armies - intercept) / slope;	//armies is defenders -> find attackers
		else
			return slope * armies + intercept;	//armies is attackers -> find defenders
	}
	
	public static double calculateArmiesFromProbabilityRisiko(int armies, double prob, boolean findAttackers, int section)
	{
		double slope = 0;
		double intercept = 0;

		switch(section)
		{
			case 0:	//the stuff less than 0.1
				slope     = bfSlopeLowRisiko[0]*Math.pow(prob,3) + bfSlopeLowRisiko[1]*Math.pow(prob,2) + bfSlopeLowRisiko[2]*prob + bfSlopeLowRisiko[3];
				intercept = bfItrcpLowRisiko[0]*Math.pow(prob,3) + bfItrcpLowRisiko[1]*Math.pow(prob,2) + bfItrcpLowRisiko[2]*prob + bfItrcpLowRisiko[3];
				break;
			case 1:	//the stuff between 0.1 and 0.9
				slope     = bfSlopeRisiko[0]*Math.pow(prob,3) + bfSlopeRisiko[1]*Math.pow(prob,2) + bfSlopeRisiko[2]*prob + bfSlopeRisiko[3];
				intercept = bfItrcpRisiko[0]*Math.pow(prob,3) + bfItrcpRisiko[1]*Math.pow(prob,2) + bfItrcpRisiko[2]*prob + bfItrcpRisiko[3];
				break;
			case 2:	//the stuff greater than 0.9
				slope     = bfSlopeHighRisiko[0]*Math.pow(prob,3) + bfSlopeHighRisiko[1]*Math.pow(prob,2) + bfSlopeHighRisiko[2]*prob + bfSlopeHighRisiko[3];
				intercept = bfItrcpHighRisiko[0]*Math.pow(prob,3) + bfItrcpHighRisiko[1]*Math.pow(prob,2) + bfItrcpHighRisiko[2]*prob + bfItrcpHighRisiko[3];
				break;
		}
		if(findAttackers)
			return (armies - intercept) / slope;	//armies is defenders -> find attackers
		else
			return slope * armies + intercept;	//armies is attackers -> find defenders
	}
	
	private static double round(double a, int b)
	{	//dangerous! only use in THIS class
		a = a*Math.pow(10, b);
		a = (double) Math.round(a);
		a = a/Math.pow(10, b);
		return a;
	}

}