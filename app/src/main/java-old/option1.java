package net.phoenixramen.risk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.lang.*;
import java.util.*;

// import android.util.Log;


public class option1 extends Common
{	
	//logcat
	// private static final String LOGTAG = "option1";
	GraphView graph; 
	private Context context;
	double[][] array;						//the main array
	int defenderArmies=0;
	int count;
	boolean useRisiko = false;
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.option1);
		context = getApplicationContext();
		
		//get the risiko preference
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		useRisiko = preferences.getBoolean("pref_risiko", false);
		
		final InputMethodManager kbd = (InputMethodManager)getSystemService(context.INPUT_METHOD_SERVICE); //the keyboard
		
		//define the views
		graph = (GraphView) findViewById(R.id.graph1);
		final EditText editText = (EditText) findViewById(R.id.def_entry_1);
		final Button button = (Button) findViewById(R.id.calculate1);         
		// final Button odds1 = (Button) findViewById(R.id.odds1);         
		// final Button remaining1 = (Button) findViewById(R.id.remaining1);         

		//set their actions
		button.setOnClickListener(new View.OnClickListener() 
		{             
			public void onClick(View v) 
			{                 
				kbd.hideSoftInputFromWindow(editText.getWindowToken(), 0);		//lose the keyboard
				
				//get and check the number of defending armies
				int x=0;
				String input;
				boolean inputIsInt = false;
				input = editText.getText().toString();
				try 
				{ 
					x = Integer.parseInt(input);  
					inputIsInt = true;
				} 
				catch(NumberFormatException nFE) 
				{ 
					Toast.makeText(context, getString(R.string.def_entry_invalid), Toast.LENGTH_SHORT).show();
				}
				
				if (inputIsInt)
				{
					defenderArmies = x;
					decideWhetherToCalculateOrEstimate();
				}
			}
		});
		
		editText.setOnEditorActionListener(new OnEditorActionListener() 
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
			{
				if (actionId == EditorInfo.IME_ACTION_DONE) 
				{
					button.performClick();                     
					return true;                 
				}
				return false;
			}
		});
		
		if (savedInstanceState != null)
		{
			defenderArmies = savedInstanceState.getInt("defenderArmies");
			if (defenderArmies != 0)
				decideWhetherToCalculateOrEstimate();
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);		//lose the keyboard
		}
	}
	
	protected void onSaveInstanceState(Bundle outState) 
	{
        super.onSaveInstanceState(outState);
		outState.putInt("defenderArmies",defenderArmies);
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
			decideWhetherToCalculateOrEstimate();
		}
	}

	//--	METHODS		------------------------------------------------------------------------------
	public void decideWhetherToCalculateOrEstimate()
	{
		if(defenderArmies >= 1000)
			estimateTheThing();
		else if(Common.calculateArmiesFromProbability(defenderArmies, 0.9, true, useRisiko) >= 1000)
			estimateTheThing();
		else
			calculateTheThing();
	}
	
	public void calculateTheThing()
	{
		int attackerArmies = defenderArmies;	//can guess anything, so arbitrarily this
		
		//start the array=========================================================================================================
		array = new double[1][1];
		count = 0;
		
		int i=0;
		int j=0;
		int loopRun=0;
		
		do	
		{ 
			extendArray();
			//fill with values
			if(useRisiko)
			{
				for(i=count; i<array.length; i++)
					for(j=0; j<array[0].length; j++)
						array[i][j] = Common.findValRisiko(array,i,j);					
			}
			else
			{
				for(i=count; i<array.length; i++)
					for(j=0; j<array[0].length; j++)
						array[i][j] = Common.findVal(array,i,j);					
			}
			//mark where the values are filled to
			count=array.length;
			
		}while(array[i-1][j-1] < TOLERANCE);		//if this is true, run the loop again
	
		//if at some point in the future, i add a second chart to chart remaining armies
/*		double[][] array2 = new double[array.length][array[0].length];
		if(useRisiko)
		{
			for(i=0; i<array.length; i++)
				for(j=0; j<array[0].length; j++)
					array2[i][j] = Common.findAtkLossRisiko(array2,i,j);
		}
		else
		{
			for(i=0; i<array.length; i++)
				for(j=0; j<array[0].length; j++)
					array2[i][j] = Common.findAtkLoss(array2,i,j);
		}
*/
	//	Common.printMatrix(LOGTAG, array2);
		
		double[] data = new double[array.length];
		for(i=0; i<array.length; i++)
			data[i] = array[i][array[0].length - 1] * 100;
	//		data[i] = i-array2[i][defenderArmies];
		
		graph.setData(data);
		graph.createChart();
		graph.setVisibility(View.VISIBLE);
	}
	
	public void estimateTheThing()
	{
		int cp=0;
		double[][] data = new double[2][81];
		double p=.1;
		for(int i=0; i<data[0].length; i++,p+=.01)
		{
			double attackers = Common.calculateArmiesFromProbability(defenderArmies, p, true, useRisiko);
			int atk = (int) Math.round(attackers);
			data[0][i] = atk;
			data[1][i] = p * 100;	//p increments with i
		}
		graph.setData(data);
		graph.createChart();
		graph.setVisibility(View.VISIBLE);
	}
	
	private void extendArray()
	{
		//extends rows only
		//make  a bigger array
		double[][] temp = new double[ array.length + defenderArmies ][ defenderArmies + 1 ];
		//fill with old values
		for (int i=0; i<array.length; i++)
			for (int j=0; j<array[0].length; j++) 
				temp[i][j] = array[i][j];
		//redefine the pointer
		array = temp;
	}
	
}