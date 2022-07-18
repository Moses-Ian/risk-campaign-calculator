package net.phoenixramen.risk;

import android.app.Activity;
import android.content.res.Resources;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
// import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import java.util.ArrayList;
import java.util.Arrays;		//debug only

public class option2 extends Common
{	
	//logcat
	private static final String TAG = "option2";
	private Context context;
	LayoutInflater inflater;
	private LinearLayout ll;
	private Button button1;
	private Button button2;
	private Button button3;
	private TerritoryViewArray tArray;
	int BLUE;
	int DARK_GREY;
	boolean useRisiko = false;
	private OnClickListener territoryOnClickListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			// see if that view is inside of view array
			int row = tArray.indexOf(v);
			int attackingArmies = tArray.get(row).getAttackingArmies();
			int defendingArmies = tArray.get(row).getDefendingArmies();
			if(attackingArmies >= 0 && defendingArmies >= 0)
			{
				//create the bundle of data to send
				Intent intent = new Intent(getApplicationContext(), net.phoenixramen.risk.option0.class);
				intent.putExtra("attackingArmies", attackingArmies);
				intent.putExtra("defendingArmies", defendingArmies);
				intent.putExtra("row", row);
				//start the other activity for result
				startActivityForResult(intent, REQUEST_CODE_FOR_OPTION2_AND_OPTION0);
			}
		}
	};
	
//Life-Cycle Methods
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
try
{
		setContentView(R.layout.option2);
		context = getApplicationContext();

		//get the risiko preference
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		useRisiko = preferences.getBoolean("pref_risiko", false);
		
		//the keyboard
		final InputMethodManager kbd = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
		
		//handle the calculate button
		button1 = (Button) findViewById(R.id.calculate2);
		button1.setOnClickListener(new View.OnClickListener() 
		{             
			public void onClick(View v) 
			{
				kbd.hideSoftInputFromWindow(button1.getWindowToken(), 0);		//lose the keyboard
				pathAnalysis();
			}
		});
		button2 = (Button) findViewById(R.id.add2);
		button2.setOnClickListener(new View.OnClickListener() 
		{             
			public void onClick(View v) 
			{
				// Log.i(LOGTAG,"addRowClicked");
				addRow();
			}
		});
		button3 = (Button) findViewById(R.id.clear2);
		button3.setOnClickListener(new View.OnClickListener() 
		{             
			public void onClick(View v) 
			{
				// Log.i(LOGTAG,"clearClicked");
				initialize();
			}
		});
		
		//define the colors for the textviews
		Resources res = getResources();
		BLUE = res.getColor(R.color.blue);
		DARK_GREY = res.getColor(R.color.dark_grey);
		
		initialize();
		
		if (savedInstanceState != null)
		{
			int[] attackerArray = savedInstanceState.getIntArray("attackerArray");
			int[] defenderArray = savedInstanceState.getIntArray("defenderArray");
			double[] getOddsOfWinningArray = savedInstanceState.getDoubleArray("getOddsOfWinningArray");
			double[] expectedRemainingArray = savedInstanceState.getDoubleArray("expectedRemainingArray");
			if(attackerArray.length == defenderArray.length && attackerArray.length != 0)
			{
				for(int i=0; i<attackerArray.length; i++)
				{
					if(i>0)
						addRow();
					Territory t = tArray.get(i);
					t.setAttackingArmies(attackerArray[i]);
					t.setDefendingArmies(defenderArray[i]);
					t.setOddsOfWinning(getOddsOfWinningArray[i]);
					t.setExpectedRemaining(expectedRemainingArray[i]);
				}
			}
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);		//lose the keyboard
		}		
		// Log.i(LOGTAG,"end onCreate");
}
catch (Exception e)
{
	// Log.i(LOGTAG, e.toString());
}

	}
	
	protected void onSaveInstanceState(Bundle outState) 
	{
		outState.putIntArray("attackerArray",tArray.getAttackerArray());
		outState.putIntArray("defenderArray",tArray.getDefenderArray());
		outState.putDoubleArray("getOddsOfWinningArray",tArray.getOddsOfWinningArray());
		outState.putDoubleArray("expectedRemainingArray",tArray.getExpectedRemainingArray());
		super.onSaveInstanceState(outState);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)	//immediately before onResume()
	{
		// Log.i(LOGTAG, "onActivityResult");
		if(requestCode == REQUEST_CODE_FOR_OPTION2_AND_OPTION0 && resultCode == Activity.RESULT_OK)
		{
			int cp = 0;
			Bundle b = data.getExtras();
			int row = b.getInt("row");
			int attackingArmies = b.getInt("attackingArmies");
			int defendingArmies = b.getInt("defendingArmies");
			Territory t = tArray.get(row);
			t.setAttackingArmies(attackingArmies);
			t.setDefendingArmies(defendingArmies);
			
			if(defendingArmies == 0)											//if the attacker won, let's carry this shit through
				if(tArray.size() != row+1)										//if this isn't the last row, let's carry through
					if(tArray.get(row+1).getAttackingArmies() == -1)				//if the next value isn't already filled out, carry through
						tArray.get(row+1).setAttackingArmies(attackingArmies-1);	//you have to leave one behind

			pathAnalysis();
		}
	}
 
	protected void onResume()
	{
		super.onResume();
		// Log.i(LOGTAG, "onResume");

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean temp = preferences.getBoolean("pref_risiko", false);
		//if the preference changed, we need to update everything, recalculating all of the math
		if(temp != useRisiko)
		{
			useRisiko = temp;
			pathAnalysis();
		}
		
		tArray.updateAll();		//if I put this in onCreate(), the views aren't visible yet, so there are problems
	}

 	protected void onStart()
	{
		super.onStart();
		// Log.i(LOGTAG,"onStart");
	}
	
	protected void onPause()
	{
		super.onPause();
		// Log.i(LOGTAG,"onPause");
	}

	protected void onStop()
	{
		super.onStop();
		// Log.i(LOGTAG,"onStop");
	}

	protected void onDestroy()
	{
		super.onDestroy();
		// Log.i(LOGTAG,"onDestroy");
	}
 
//Methods
	private void initialize()
	{
		ll = (LinearLayout)findViewById(R.id.linear2);
		ll.removeAllViews();
		tArray = new TerritoryViewArray();
		inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View territoryView = inflater.inflate(R.layout.territory_item, ll, true);
		territoryView.setOnClickListener(territoryOnClickListener);		//register the onClickListener
		tArray.add(territoryView);	
	}
	
	private void addRow()
	{
		inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View territoryView = inflater.inflate(R.layout.territory_item, ll, false);
		territoryView.setOnClickListener(territoryOnClickListener);		//register the onClickListener
		tArray.add(territoryView);
		ll.addView(territoryView);
		
	}
	
 	private void pathAnalysis()
	{
		// Log.i(TAG, "pathAnalysis");
		
		ArrayList<Territory> territory_data = tArray.getTerritoryArray();
		
			//make sure that the first number of attacking armies is not -1
		if ( territory_data.get(0).getAttackingArmies() == -1 )
			return;
		
		double[] victory = new double[0];	//an array of probabilities of ending at each victory condition
		double[][] transitionOdds;			//a matrix indicating the probability of moving from a starting condition to various conditions
		
		int attackers = 0;						//only needed if we estimate
		int defenders = 0;
		boolean estimate = false;
		
		//loop for every territory
		for(int k=0; k<territory_data.size(); k++)
		{
		//if there's no defenders, that's the end of the chain
			Territory t = territory_data.get(k);
			if (t.getDefendingArmies() <= 0)
			{
				t.setOddsOfWinning(-1);
				t.setExpectedRemaining(-1);
				continue;
			}
			
			//define the victory array
			if (t.getAttackingArmies() != -1)		//user-defined number of attacking armies
			{
				if(t.getAttackingArmies() >= 1000 || t.getDefendingArmies() >= 1000)
				{
					estimate = true;
					attackers = t.getAttackingArmies();
					defenders = t.getDefendingArmies();
				}//check for whether we need to estimate
				else
				{
					estimate = false;
					//create the transition matrix
					if(useRisiko)
						transitionOdds = createTransitionMatrixRisiko(t.getAttackingArmies(), t.getDefendingArmies());
					else
						transitionOdds = createTransitionMatrix(t.getAttackingArmies(), t.getDefendingArmies());
				
					//create the victory array
					victory = new double[ transitionOdds.length ];
					for(int i=0; i<victory.length; i++)
						victory[i] = transitionOdds[i][0];			//it's an array of the victory conditions found in transition matrix
				}
			}
			else												//running probabilities from the previous territory
			{
				if(estimate || t.getDefendingArmies() >= 1000)	//check for whether we need to estimate
				{
					attackers = t.getAttackingArmies();
					defenders = t.getDefendingArmies();
					int r = k;
					do
					{
						attackers =  territory_data.get(r-1).getAttackingArmies();
						defenders += territory_data.get(r-1).getDefendingArmies();
						r--;
					}while(attackers == -1);	//find a territory before this one that has a user-entered value
					estimate = true;
				}
				else
				{
					estimate = false;
					//shift victory down by one cell to account for loss of one army at each territory
					for(int i=0; i<victory.length-2; i++)
						victory[i] = victory[i+1];
					victory[ victory.length-1 ] = 0;
				
					//create a matrix to become victory later
					double[] runningSum = new double[ victory.length ];

					//loop through for each possibility of number of attacking armies
					for(int i=0; i<victory.length; i++)
					{
						if( victory[i] > 0 )
						{
							if(useRisiko)
								transitionOdds = createTransitionMatrixRisiko(i, t.getDefendingArmies());
							else
								transitionOdds = createTransitionMatrix(i, t.getDefendingArmies());
							//take what would go in victory and put it into runningSum, with weight from victory
							//this is the analogous to victory[i] = transitionOdds[i][0], accounting for the probability that the starting condition occured
							for(int j=0; j<runningSum.length && j<transitionOdds.length; j++)
								runningSum[j] += victory[i] * transitionOdds[j][0];
						}
					}
					//reset victory to the new runningSum
					victory = runningSum;
				}
			}

			//calculate odds and expected remaining
			double odds = 0;
			double expRem = 0;
			if(estimate)
			{
				odds = Common.estimateProbability(attackers, defenders, useRisiko);
				expRem = -2;
				if(t.getExpectedRemaining() != -2)	// changing view colors is expensive, so make sure we actually need it
					tArray.setRowColor(k, BLUE);
			}
			else
			{
				for(int i=1; i<victory.length; i++)
				{
					odds += victory[i];
					expRem += victory[i] * i;
				}
				if(t.getExpectedRemaining() == -2)	// changing view colors is expensive, so make sure we actually need it
					tArray.setRowColor(k, DARK_GREY);
			}
			//plug them in
			t.setOddsOfWinning(odds);
			t.setExpectedRemaining(expRem);
			// Log.i(LOGTAG, "odds="+odds);
			// Log.i(LOGTAG, "rem="+expRem);
		}
		
		tArray.updateAll();	// consider updating periodically for long or high-valued lists
	}
 	
 	private void printTerritories()
	{
		for(int i=0; i<tArray.size(); i++)
		{
			Territory t = tArray.get(i);
			StringBuilder sb = new StringBuilder();
			sb.append(t.getAttackingArmies());
			sb.append(", ");
			sb.append(t.getDefendingArmies());
			sb.append(", ");
			sb.append(t.getOddsOfWinning());
			sb.append(", ");
			sb.append(t.getExpectedRemaining());
			sb.append(", ");
			sb.append(i);
			// Log.i("option2", sb.toString());
		}
	}
}



























