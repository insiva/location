package com.matteo.matteolocationhistory.model;

import java.io.Serializable;

import org.xml.sax.Attributes;


public class PicCategory implements Serializable{
	 private static final long serialVersionUID = 1L;
	public static final String CATEGORY="CATEGORY";
	public static final String CATEGORYVALUE="CATEGORYVALUE";
	public static final String CATEGORYTYPE="CATEGORYTYPE";
	public String CateStr,Province,City,District;
	public int Count;
	public int Type;
	public PicCategory(String cs,int c){
		this.CateStr=cs;
		this.Count=c;
	}
	public PicCategory(Attributes atts){
		this.CateStr=atts.getValue("value");
		this.Count=Integer.parseInt(atts.getValue("count"));
		this.Type=Integer.parseInt(atts.getValue("type"));
		if(this.Type==Conf.SiteType){
			this.Province=atts.getValue("province");
			this.City=atts.getValue("city");
			this.District=atts.getValue("district");
		}
	}
	
	public String GetDescription(){
		if(this.Type==Conf.DayType)
		{
			return CateStr+"£¨"+Count+"’≈’’∆¨";
		}
		if(this.Province.equals(this.City))
			return this.City+this.District+"£¨"+Count+"’≈’’∆¨";
		return this.Province+this.City+this.District+"£¨"+Count+"’≈’’∆¨";
	}
}