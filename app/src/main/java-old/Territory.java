package net.phoenixramen.risk;

public class Territory
{
	private int attackingArmies;
	private int defendingArmies;
	private double oddsOfWinning;
	private double expectedRemaining;

//constructors
	public Territory()
	{
		initialize();
	}
	
	public Territory(int def)
	{
		this();
		setDefendingArmies(def);
	}
	
	public Territory(int atk, int def)
	{
		this(def);
		setAttackingArmies(atk);
	}
	
//initialize the variables
	public void initialize()
	{
		attackingArmies = -1;
		defendingArmies = -1;
		oddsOfWinning = -1;
		expectedRemaining = -1;
	}

//if possible, set the other variables
	public boolean findOdds()
	{
		return false;
	}

//setters
	public boolean setAttackingArmies(int i)
	{
		if(i < 0)
			return false;
		attackingArmies = i;
		return true;
	}
	
	public boolean setDefendingArmies(int i)
	{
		if(i < 0)
			return false;
		defendingArmies = i;
		return true;
	}
	
	public boolean setOddsOfWinning(double d)
	{
		// if(d < 0 || d > 1)
			// return false;
		oddsOfWinning = d;
		return true;
	}
	
	public boolean setExpectedRemaining(double d)
	{
		// if(d < 0)
			// return false;
		expectedRemaining = d;
		return true;
	}
	
//getters
	public int getAttackingArmies()
	{
		return attackingArmies;
	}
	
	public int getDefendingArmies()
	{
		return defendingArmies;
	}
	
	public double getOddsOfWinning()
	{
		return oddsOfWinning;
	}
	
	public double getPercentWinning()
	{
		return oddsOfWinning*100;
	}
	
	public double getExpectedRemaining()
	{
		return expectedRemaining;
	}
}