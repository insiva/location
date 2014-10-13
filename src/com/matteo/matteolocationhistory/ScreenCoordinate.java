package com.matteo.matteolocationhistory;

public class ScreenCoordinate{
	public int X, Y;

	public ScreenCoordinate(int _x, int _y) {
		this.X = _x;
		this.Y = _y;
	}
	
	public MapCoordinate toMapCoordinate(){
		return MatteoMap.RelativeScreenCoordinateToMapCoordinate(this);
	}
	
	public ScreenCoordinate clone() {  
        return new ScreenCoordinate(this.X,this.Y);
    } 
	
	public void Print()
	{
		//Matteo.MtLog(this.getClass().getName()+"X="+Integer.toString(X)+",Y="+Integer.toString(Y));
	}
	
	public void Print(String s)
	{
		//Matteo.MtLog(s+":X="+Integer.toString(X)+",Y="+Integer.toString(Y));
	}
	
	public int GetMapR(){
		return MatteoMap.GetMapImageRByScreenCoordinate(this);
	}
	
	public int GetMapD()
	{
		return MatteoMap.GetMapImageDByScreenCoordinate(this);
	}
}