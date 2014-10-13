package com.matteo.matteolocationhistory;

import java.text.ParseException;
import java.util.Date;

import com.baidu.mapapi.model.LatLng;

public class MatteoLocation {
	public double Accuracy,Altitude;
	public LatLng Coordiante;
	public int Guid;
	public Date CreateTime;
	public double Distance;
	public float LeftViewX,ViewX;
	public MatteoLocation(int gd,double lat,double lon,double acr,double alt,String ct){
		this.Guid=gd;
		this.Coordiante=new LatLng(lat,lon);
		this.Accuracy=acr;
		this.Altitude=alt;
		this.LeftViewX=0;
		this.ViewX=0;
		try {
			this.CreateTime=MatteoHistory.TimeFormatter.parse(ct);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String GetDistanceText()
	{
		int d=(int)Math.ceil(this.Distance);
		return MatteoHistory.GetDistanceText(d);
	}
	
	public String GetDescription(){
		return MatteoHistory.TimeFormatter.format(this.CreateTime)+"\n"+this.GetDistanceText();
	}
}
