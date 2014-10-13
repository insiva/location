package com.matteo.matteolocationhistory.model;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PictureItem extends LinearLayout {

	private Picture pic;
	private Context ctx;
	private ImageView ivPic;
	private LinearLayout llText;
	private TextView tvAddress,tvTime;
	public PictureItem(Context context,Picture p) {
		super(context);
		// TODO Auto-generated constructor stub
		this.pic=p;
		this.ctx=context;
		this.CreateView();
	}
	
	public void CreateView(){
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.ivPic=new ImageView(this.ctx);
		this.ivPic.setImageURI(this.pic.GetUri(Conf.PICTURETYPES));
		this.addView(this.ivPic);
		this.llText=new LinearLayout(this.ctx);
		this.tvAddress=new TextView(this.ctx);
		this.tvAddress.setText(this.pic.Loc.Address);
		this.llText.addView(this.tvAddress);
		this.tvTime=new TextView(this.ctx);
		this.tvTime.setText(Fun.ParseDate(this.pic.CreateTime, Conf.DateF));
		this.llText.addView(this.tvTime);
		this.addView(this.llText);
		this.addView(this.llText,MTLayoutParams.GetPicItemTextLL());
	}

}
