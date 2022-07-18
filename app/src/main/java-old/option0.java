package net.phoenixramen.risk;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
// import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Map;

public class option0 extends Common
{	
	//logcat
	// final String TAG = "option0";
	//important values
	double[][] oddsMatrix;
	double[][] atkRemMatrix;
	int[] currentFight = new int[2];
	boolean estimate = false;
	int option2Row;	// need this so that it's easier to handle movement b/t option2 and option0
	boolean createdByOption2 = false;
	//the views
	Button button1;
	Button button2;
	Button button3;
	Button button4;
	TextView tv1;
	TextView tv2;
	TextView tv3;
	int BLUE;
	int DARK_GREY;
	boolean useRisiko = false;
	public enum BattleState
	{
		MATCH_OVER,
		ONE_LOSS, 	//buttons 1 and 2 are visible
		TWO_LOSS, 	//buttons 1,2,3 are visible
		THREE_LOSS	//buttons 1,2,3,4 are visible
	}
	BattleState state = BattleState.ONE_LOSS;
		
//Life-Cycle Methods
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option0);
		
		//get the risiko preference
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		useRisiko = preferences.getBoolean("pref_risiko", false);
		
		// Log.i(LOGTAG, "onCreate");
		final InputMethodManager kbd = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE); //the keyboard
		final EditText editText1 = (EditText) findViewById(R.id.atk_entry_0);
		final EditText editText2 = (EditText) findViewById(R.id.def_entry_0);
		final Button button  = (Button) findViewById(R.id.calculate0);         
		button1 = (Button) findViewById(R.id.a_lose_2);
		button2 = (Button) findViewById(R.id.both_lose);
		button3 = (Button) findViewById(R.id.d_lose_2);
		button4 = (Button) findViewById(R.id.risiko_extra);
		tv1 = (TextView) findViewById(R.id.out_current0);	//atk v def textview
		tv2 = (TextView) findViewById(R.id.out_chances0);
		tv3 = (TextView) findViewById(R.id.out_remaining0);
	
		button.setOnClickListener(new View.OnClickListener() 
		{             
			public void onClick(View v) 
			{                 
				// Perform action on click
				kbd.hideSoftInputFromWindow(editText1.getWindowToken(), 0);		//lose the keyboard

				//get and check the number of attacking armies
				int x1=0;
				String input1;
				boolean input1Int = false;
				input1 = editText1.getText().toString();
				try 
				{ 
					x1 = Integer.parseInt(input1);  
					if (x1 == 0)
						throw new NumberFormatException();	//easiest way to check for zero
					input1Int = true;
				} 
				catch(NumberFormatException nFE) 
				{ 
					Toast.makeText(getApplicationContext(),  getString(R.string.atk_entry_invalid), Toast.LENGTH_SHORT).show();
				}
				
				//get and check the number of defending armies
				int x2=0;
				String input2;
				boolean input2Int = false;
				input2 = editText2.getText().toString();
				try 
				{ 
					x2 = Integer.parseInt(input2);  
					if (x2 == 0)
						throw new NumberFormatException();
					input2Int = true;
				} 
				catch(NumberFormatException nFE) 
				{ 
					Toast.makeText(getApplicationContext(), getString(R.string.def_entry_invalid), Toast.LENGTH_SHORT).show();
				}
				
				//create the odds matrix, init in class option0
				if(input1Int && input2Int)
				{
					currentFight[0]=x1;
					currentFight[1]=x2;
					createOddsMatrix();
				}
			}
		});
		
		editText2.setOnEditorActionListener(new OnEditorActionListener() 
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
			{
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_DONE) 
				{
					button.performClick();                     
					return true;                 
				}
				return false;
			}
		});
		
		button1.setOnClickListener(new View.OnClickListener() 
		{             
			public void onClick(View v) 
			{                 
				kbd.hideSoftInputFromWindow(editText1.getWindowToken(), 0);		//lose the keyboard
				updateOdds(1);	//1 means button1 called it
			}
		});
		
		button2.setOnClickListener(new View.OnClickListener() 
		{             
			public void onClick(View v) 
			{                 
				kbd.hideSoftInputFromWindow(editText1.getWindowToken(), 0);		//lose the keyboard
				updateOdds(2);	//1 means button1 called it
			}
		});
		
		button3.setOnClickListener(new View.OnClickListener() 
		{             
			public void onClick(View v) 
			{                 
				kbd.hideSoftInputFromWindow(editText1.getWindowToken(), 0);		//lose the keyboard
				updateOdds(3);	//1 means button1 called it
			}
		});

		button4.setOnClickListener(new View.OnClickListener() 
		{             
			public void onClick(View v) 
			{                 
				kbd.hideSoftInputFromWindow(editText1.getWindowToken(), 0);		//lose the keyboard
				updateOdds(4);	//1 means button1 called it
			}
		});
		
		

		
		//define the colors for the textviews
		Resources res = getResources();
		BLUE = res.getColor(R.color.blue);
		DARK_GREY = res.getColor(R.color.dark_grey);

		//if this was created by option2
		Bundle intentExtras = getIntent().getExtras();
		createdByOption2 = (null != intentExtras);
		
		if (savedInstanceState != null)
		{
			currentFight = savedInstanceState.getIntArray("currentFight");
			if ( ! (currentFight[0] == 0 && currentFight[1] == 0 ) )
				createOddsMatrix();
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);		//lose the keyboard
		} else if(createdByOption2)
		{
			option2Row = intentExtras.getInt("row");
			editText1.setText(Integer.toString(intentExtras.getInt("attackingArmies")));
			editText2.setText(Integer.toString(intentExtras.getInt("defendingArmies")));
			button.performClick();
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);		//lose the keyboard
		}
	}
	
	protected void onSaveInstanceState(Bundle outState) 
	{
		outState.putIntArray("currentFight",currentFight);
		super.onSaveInstanceState(outState);
	}

	protected void onResume()
	{
		super.onResume();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean temp = preferences.getBoolean("pref_risiko", false);
		//if the preference changed, we need to update everything, recalculating all of the math
		if(temp != useRisiko)
		{
			useRisiko = temp;
			createOddsMatrix();
		}
	}

	//Methods
	public void createOddsMatrix()
	{
		double array[][];
		double array2[][];
		int atkSize = (currentFight[0] >= 1000 ? 1001 : currentFight[0]+1);
		int defSize = (currentFight[1] >= 1000 ? 1001 : currentFight[1]+1);
		isEstimate();
		array = new double[atkSize][defSize];
		array2 = new double[atkSize][defSize];
		
		if(useRisiko)
		{
			populateOddsMatrixRisiko(array);
			populatedExpectedRemainingRisiko(array2);
		}
		else
		{
			populateOddsMatrix(array);
			populatedExpectedRemaining(array2);
		}
		oddsMatrix = array;
		atkRemMatrix = array2;
		updateLayout();
	}
	
	public void updateOdds(int call)
	{
		switch(state)
		{
			case ONE_LOSS:
				if(call == 1)
					currentFight[0]--;	//attacker lost 1
				else
					currentFight[1]--;	//defender lost 1
				break;
			case TWO_LOSS:
				if(call == 1)
					currentFight[0] -= 2;	//attacker lost 2
				else if(call == 2)
				{
					currentFight[0]--;		//both lose 1
					currentFight[1]--;
				}
				else
					currentFight[1] -= 2;	//defender lost 2
				break;
			case THREE_LOSS:
				if(call == 1)
					currentFight[0] -= 3;	//attacker lost 3
				else if(call == 2)
				{
					currentFight[0] -=2;	//attacker lost 2 and defender lost 1
					currentFight[1]--;
				}
				else if(call == 3)
				{
					currentFight[0]--;		//attacker lost 1 and defender lost 2
					currentFight[1] -= 2;
				}
				else
					currentFight[1] -= 3;	//defender lost 3
				break;
			default:
				break;
		}
		
		if (estimate) isEstimate();
		
		if(createdByOption2)
		{
			Intent returnData = new Intent();
			returnData.putExtra("row", option2Row);
			returnData.putExtra("attackingArmies", currentFight[0]);
			returnData.putExtra("defendingArmies", currentFight[1]);
			setResult(Activity.RESULT_OK, returnData);	// should check that everything is ok
		}
		
		//update xml
		updateLayout();
	}
	
	public void updateLayout()
	{
		//update the text with the new match values
		tv1.setText(currentFight[0] + "v" + currentFight[1]);
		
		double chances;
		String remaining;
		
		//get the new odds from the old table
		if(estimate)
		{
			chances = estimateProbability(currentFight[0], currentFight[1], useRisiko) * 100;
			remaining = "?";
		}
		else
		{
			chances = oddsMatrix[currentFight[0]][currentFight[1]] * 100;			//percent textview
			remaining = String.format("%.1f", atkRemMatrix[ currentFight[0] ][ currentFight[1] ] );
		}

		//update the texts with the new values
		tv2.setText(String.format("%.2f%%", chances));
		tv3.setText(remaining);
		
		//set the buttons based on whether it's an estimate
		tv2.setTextColor(estimate ? BLUE : DARK_GREY);
		tv3.setTextColor(estimate ? BLUE : DARK_GREY);
		
		//set the button state based on the match values
		if(useRisiko && currentFight[0] >= 3 && currentFight[1] >= 3)
			state = BattleState.THREE_LOSS;
		else if(currentFight[0] >= 2 && currentFight[1] >= 2)
			state = BattleState.TWO_LOSS;
		else if(currentFight[0] >= 1 && currentFight[1] >= 1)
			state = BattleState.ONE_LOSS;
		else
			state = BattleState.MATCH_OVER;

		//set the buttons based on the state
		switch(state)
		{
			case MATCH_OVER:
				button1.setVisibility(View.GONE);	
				button2.setVisibility(View.GONE);	
				button3.setVisibility(View.GONE);	
				button4.setVisibility(View.GONE);
				break;
			case ONE_LOSS:
				button1.setText(R.string.a);
				button1.setVisibility(View.VISIBLE);	
				button2.setText(R.string.d);
				button2.setVisibility(View.VISIBLE);
				button3.setVisibility(View.GONE);	
				button4.setVisibility(View.GONE);
				break;
			case TWO_LOSS:
				button1.setText(R.string.a2);
				button1.setVisibility(View.VISIBLE);
				button2.setText(R.string.ad);
				button2.setVisibility(View.VISIBLE);	
				button3.setText(R.string.d2);
				button3.setVisibility(View.VISIBLE);	
				button4.setVisibility(View.GONE);
				break;
			case THREE_LOSS:
				button1.setText(R.string.a3);
				button1.setVisibility(View.VISIBLE);
				button2.setText(R.string.a2d);
				button2.setVisibility(View.VISIBLE);	
				button3.setText(R.string.ad2);
				button3.setVisibility(View.VISIBLE);	
				button4.setText(R.string.d3);
				button4.setVisibility(View.VISIBLE);
				break;
			default:
				break;				
		}
		findViewById(R.id.reveal_chances0).setVisibility(0);	//make the second table visible
	}
	
	public boolean isEstimate()
	{
		estimate = (currentFight[0] >= 1000 || currentFight[1] >= 1000);
		return estimate;
	}
	
}
