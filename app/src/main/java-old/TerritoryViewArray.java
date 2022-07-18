package net.phoenixramen.risk;

//---------------------------------------------------------------------------------------------------
// The ArrayList<Territory> is hidden from the user.
// The user adds and removes views, and TerritoryViewArray adds and removes territories as necessary.
// These views are shallow copies of the views in the sister linearlayout.
// It's the user's responsibility to add and remove views from both at the same time.
//---------------------------------------------------------------------------------------------------

import android.text.Editable;
import android.text.TextWatcher;
// import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TerritoryViewArray
{	
	private ArrayList<Territory> territoryArray;
	private ArrayList<View> viewArray;
	// Create an anonymous implementation of OnClickListener
	
	public TerritoryViewArray()
	{
		territoryArray = new ArrayList<Territory>();
		viewArray = new ArrayList<View>();
	}
	
	public View add(View territoryView)
	{
	
		//define the territory object
		Territory t = new Territory();
		t.setAttackingArmies(-1);
		t.setDefendingArmies(-1);
		// if (size() == 0)	t.setAttackingArmies(10);
		// t.setDefendingArmies(size()+1);
		
		//modify the view accordingly
		EditText atk2 = (EditText)territoryView.findViewById(R.id.atk2);
		EditText def2 = (EditText)territoryView.findViewById(R.id.def2);
		TextView odd2 = (TextView)territoryView.findViewById(R.id.odd2);
		TextView rem2 = (TextView)territoryView.findViewById(R.id.rem2);

		atk2.setText(	t.getAttackingArmies() == -1 	? "" 	: Integer.toString(t.getAttackingArmies()));
		def2.setText(	t.getDefendingArmies() == -1 	? "" 	: Integer.toString(t.getDefendingArmies()));
		odd2.setText(	t.getOddsOfWinning() == -1 		? "---" : String.format("%.2f%%", t.getPercentWinning()));
		rem2.setText(	t.getExpectedRemaining() == -1 	? "---" : String.format("%.1f", t.getExpectedRemaining()));
		
		setAtkVisible(atk2, size());

		atk2.addTextChangedListener( new MyTextWatcher(size(), true, t) );
		def2.addTextChangedListener( new MyTextWatcher(size(), false, t) );
		
		//add the territory and view to their respective lists
		territoryArray.add(t);
		viewArray.add(territoryView);
		
		return territoryView;
	}
	
	private void setAtkVisible(EditText atk2, int row)
	{
		if(row == 0 || territoryArray.get(row-1).getAttackingArmies() != -1)
			atk2.setVisibility(View.VISIBLE);
		else
			atk2.setVisibility(View.INVISIBLE);
	}
	
	public void updateAll()
	{
		for(int i=0; i<size(); i++)
			updateRow(i);
	}
	
	public void updateRow(int row)
	{
		Territory t = territoryArray.get(row);
		View territoryView = viewArray.get(row);
		
		EditText atk2 = (EditText)territoryView.findViewById(R.id.atk2);
		EditText def2 = (EditText)territoryView.findViewById(R.id.def2);
		TextView odd2 = (TextView)territoryView.findViewById(R.id.odd2);
		TextView rem2 = (TextView)territoryView.findViewById(R.id.rem2);

		atk2.setText(	t.getAttackingArmies() == -1 	? "" 	: Integer.toString(t.getAttackingArmies()));
		def2.setText(	t.getDefendingArmies() == -1 	? "" 	: Integer.toString(t.getDefendingArmies()));
		odd2.setText(	t.getOddsOfWinning() == -1 		? "---" : String.format("%.2f%%", t.getPercentWinning()));

		String rem2Text;
		if(t.getExpectedRemaining() == -1)
			rem2Text = "---";
		else if(t.getExpectedRemaining() == -2)
			rem2Text = "?";
		else
			rem2Text = String.format("%.1f", t.getExpectedRemaining());
		
		rem2.setText(rem2Text);
		
		setAtkVisible(atk2, row);
	}
	
	public void setRowColor(int row, int color)
	{
		//color should be a color id or something of the form Color.parseColor("#rrggbb")
		View territoryView = viewArray.get(row);
		TextView odd2 = (TextView)territoryView.findViewById(R.id.odd2);
		TextView rem2 = (TextView)territoryView.findViewById(R.id.rem2);
		
		odd2.setTextColor(color);
		rem2.setTextColor(color);
	}
	
	public int size()
	{
		return territoryArray.size();
	}
	
	public int viewSize()
	{
		return viewArray.size();
	}
	
	public Territory get(int row)
	{
		return territoryArray.get(row);
	}
	
	public ArrayList<Territory> getTerritoryArray()
	{
		return territoryArray;
	}
	
	public int[] getAttackerArray()
	{
		int[] attackerArray = new int[size()];
		for(int i=0; i<attackerArray.length; i++)
			attackerArray[i] = get(i).getAttackingArmies();
		return attackerArray;
	}
	
	public int[] getDefenderArray()
	{
		int[] defenderArray = new int[size()];
		for(int i=0; i<defenderArray.length; i++)
			defenderArray[i] = get(i).getDefendingArmies();
		return defenderArray;
	}

	public double[] getOddsOfWinningArray()
	{
		double[] getOddsOfWinningArray = new double[size()];
		for(int i=0; i<getOddsOfWinningArray.length; i++)
			getOddsOfWinningArray[i] = get(i).getOddsOfWinning();
		return getOddsOfWinningArray;
	}

	public double[] getExpectedRemainingArray()
	{
		double[] expectedRemainingArray = new double[size()];
		for(int i=0; i<expectedRemainingArray.length; i++)
			expectedRemainingArray[i] = get(i).getExpectedRemaining();
		return expectedRemainingArray;
	}

	public int indexOf(View v)
	{
		return viewArray.indexOf(v);
	}
	
	public LinearLayout getView(int row)
	{
		return (LinearLayout) viewArray.get(row);
	}
	
	class MyTextWatcher implements TextWatcher 
	{
		private int position;
		private boolean isAttacking;
		private Territory t;

		public MyTextWatcher(int position, boolean isAttacking, Territory territory) 
		{
			this.position = position;
			this.isAttacking = isAttacking;
			this.t = territory;
		}

		public void afterTextChanged(Editable s) 
		{
			String str = s.toString();
			try 
			{ 
				int x = Integer.parseInt(str);  
				StringBuilder sb = new StringBuilder("row: ");
				sb.append(Integer.toString(position));
				sb.append(", start: ");
				if (isAttacking)
				{
					sb.append("atk");
					sb.append(Integer.toString(t.getAttackingArmies()));
					t.setAttackingArmies(x);
					sb.append(", end: ");
					sb.append(Integer.toString(t.getAttackingArmies()));
				}
				else
				{
					sb.append("def");
					sb.append(Integer.toString(t.getDefendingArmies()));
					t.setDefendingArmies(x);
					sb.append(", end: ");
					sb.append(Integer.toString(t.getDefendingArmies()));
				}
				// Log.i("option2", sb.toString());
				// notifyDataSetChanged();
			} 
			catch(NumberFormatException nFE) 
			{ 
				// Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
				if (str == "")
					if (isAttacking)
						t.setAttackingArmies(-1);
					else
						t.setDefendingArmies(-1);
			}
		}
		
		//not used
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
		{
			// Log.i("option2", "before text changed: " + charSequence);
		}
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){}

	}

}













































