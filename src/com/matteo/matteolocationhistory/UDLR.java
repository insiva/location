package com.matteo.matteolocationhistory;


public class UDLR {
	public int Width,Height;
	public ScreenCoordinate CenterScreenCoordinate;
	public boolean Screen;
	public MatteoMap MtMap;

	public UDLR(ScreenCoordinate sc,int wid,int hei,MatteoMap mm,boolean IsScreen) {
		this.CenterScreenCoordinate=sc.clone();
		this.Width=wid;
		this.Height=hei;
		//this.ScrollDirection=Direction.CC;
		this.Screen=IsScreen;
		if(!this.Screen)return;
		this.MtMap=mm;
	}

	public UDLR(ScreenCoordinate sc,int wid,int hei,MatteoMap mm) {
		this.CenterScreenCoordinate=sc.clone();
		this.Width=wid;
		this.Height=hei;
		//this.ScrollDirection=Direction.CC;
		this.Screen=false;
		this.MtMap=mm;
	}
	
	public int GetU()
	{
		return this.CenterScreenCoordinate.Y-this.GetHeight()/2;
	}
	
	public int GetD()
	{
		return this.CenterScreenCoordinate.Y+this.GetHeight()/2;
	}
	
	public int GetL()
	{
		return this.CenterScreenCoordinate.X-this.GetWidth()/2;
	}
	
	public int GetR()
	{
		return this.CenterScreenCoordinate.X+this.GetWidth()/2;
	}

	public void Scroll(int DistanceX, int DistanceY) {
		if(!this.Screen)return;
		this.CenterScreenCoordinate.X=this.CenterScreenCoordinate.X+DistanceX;
		this.CenterScreenCoordinate.Y=this.CenterScreenCoordinate.Y+DistanceY;
	}

	public boolean Contains(ScreenCoordinate mc) {
		if (this.GetU() <= mc.Y && this.GetD() >= mc.Y && this.GetL() <= mc.X
				&& this.GetR() >= mc.X)
			return true;
		return false;
	}
	
	public boolean Contains(UDLR udlr){
		int u1=this.GetU();
		int d1=this.GetD();
		int l1=this.GetL();
		int r1=this.GetR();
		int u2=udlr.GetU();
		int d2=udlr.GetD();
		int l2=udlr.GetL();
		int r2=udlr.GetR();
		if(u2>d1||d2<u1||l2>r1||r2<l1) return false;
		return true;
	}
	
	public int GetWidth()
	{
		if(this.Screen)return this.Width;
		return MatteoMap.GetMapWidth();
	}
	
	public int GetHeight(){
		if(this.Screen)return this.Height;
		return MatteoMap.GetMapHeight();
	}
}