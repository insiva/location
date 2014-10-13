package com.matteo.matteolocationhistory;

import android.graphics.Paint;
import android.graphics.Paint.Align;

public class TextToDraw {
	public String Text;
	public float X,Y;
	public Paint TextPaint;
	public static float TextHeight;
	public Align TextAlign;
	public TextToDraw(String t,float x,float y,Paint p,Align a){
		this.Text=t;
		this.X=x;this.Y=y;
		this.TextPaint=p;
		this.TextAlign=a;
	}
	
	public float GetWidth(){
		return this.TextPaint.measureText(this.Text);
	}
	
	public float GetAlignRightX(){
		return this.X-this.GetWidth();
	}
	
	public float GetAlignRightY(){
		return this.Y+TextHeight/3;
	}
	
	public float GetX(){
		if(this.TextAlign.equals(Align.RIGHT)){
			return this.X-HistoryPathView.ScaleLength;
		}else if(this.TextAlign.equals(Align.CENTER)){
			return this.X;
		}
		return this.X;
	}
	
	public float GetY(){
		if(this.TextAlign.equals(Align.RIGHT)){
			return this.Y+TextHeight/3;
		}else if(this.TextAlign.equals(Align.CENTER)){
			return this.Y+TextHeight;
		}
		return this.Y;
	}
}
