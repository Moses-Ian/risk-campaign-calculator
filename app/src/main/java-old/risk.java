package net.phoenixramen.risk;

import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

public class risk extends Common
{	
	//logcat
	//String LOGTAG = "risk";

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//initalize the preferences
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		String[] mainOptions = getResources().getStringArray(R.array.main_options_array);
		
		ListView lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(new MainAdapter(this, R.layout.list_item, mainOptions));
		lv.setTextFilterEnabled(true);
	
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent intent=null;
				switch((int)id)
				{
					case 0:
						intent = new Intent(getApplicationContext(), option0.class);
						break;
					case 1:
						intent = new Intent(getApplicationContext(), option1.class);
						break;
					case 2:
						intent = new Intent(getApplicationContext(), option2.class);
						break;
					default:
						break;
				}
				startActivity(intent);
			}
		});
		
	}
}
