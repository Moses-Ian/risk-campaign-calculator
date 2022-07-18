package net.phoenixramen.risk;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Help extends Common
{
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
	}
	
	//Options Menu
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();    
		inflater.inflate(R.menu.return_menu, menu);    
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle item selection    
		int itemId = item.getItemId();
		switch (itemId)
		{
			case R.id.revert:
				finish();            
				return true;        
			default:
				return super.onOptionsItemSelected(item);    
		}
	}
}