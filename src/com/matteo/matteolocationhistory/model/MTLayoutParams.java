package com.matteo.matteolocationhistory.model;

import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class MTLayoutParams {
	private static LinearLayout.LayoutParams PicItemTextLL;
	private static LayoutParams CateItem;
	
	public static LinearLayout.LayoutParams GetPicItemTextLL(){
		if(MTLayoutParams.PicItemTextLL==null){
			MTLayoutParams.PicItemTextLL=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			//MTLayoutParams.PicItemTextLL.setOrientation(LinearLayout.VERTICAL);
		}
		return MTLayoutParams.PicItemTextLL;
	}
	
	public static LayoutParams GetCateItem(){
		if(MTLayoutParams.CateItem==null){
			MTLayoutParams.CateItem=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		}
		//MTLayoutParams.CateItem.
		return MTLayoutParams.CateItem;
	}
}
