package com.mosesian.riskcampaigncalculator

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MainAdapter : ArrayAdapter<String> {
	
	val layoutResourceId: Int
	val data: Array<String>
	
	constructor(context : Context, _layoutResourceId : Int, _data : Array<String>) : super(context, _layoutResourceId, _data){
		layoutResourceId = _layoutResourceId
		data = _data		
	}
	
	override fun getView(position : Int, convertView : View?, parent : ViewGroup) : View {
		
		//create the layout
		val inflater = (context as Activity).getLayoutInflater()
		val row = inflater.inflate(layoutResourceId, parent, false)
		val text : TextView = row.findViewById(R.id.menu_text)
		text.setText(data[position])
		
		//set the background
		// TODO: I think the image structure changed
		// -> I need to redo the images
		// val resId = when(position) {
			// 0 -> R.color.top_menu_img0
			// 1 -> R.color.top_menu_img1
			// else -> R.color.top_menu_img2
		// }
		// text.setBackgroundResource(resId)
		
		return row
	}
}