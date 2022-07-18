package net.phoenixramen.risk;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainAdapter extends ArrayAdapter<String>
{
	// final String LOGTAG = "option2";
	Context context; 
	int layoutResourceId;    
	String[] data = null;
	//final int TYPE = TextView.BufferType.EDITABLE;

	public MainAdapter(Context context, int layoutResourceId, String[] data) 
	{
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	//@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View row = inflater.inflate(layoutResourceId, parent, false);
		TextView text = (TextView)row.findViewById(R.id.menu_text);
		text.setText(data[position]);
		int resId = 0;	//zero is default -> remove the background
		switch (position)
		{
			case 0:
				resId = R.color.top_menu_img0;
				break;
			case 1:
				resId = R.color.top_menu_img1;
				break;
			case 2:
				resId = R.color.top_menu_img2;
				break;
			default:
				resId = 0;
		}
		text.setBackgroundResource(resId);

		return row;
	}
}

