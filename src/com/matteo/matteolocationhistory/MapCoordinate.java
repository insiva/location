package com.matteo.matteolocationhistory;


public class MapCoordinate {
	public int X, Y;

	public MapCoordinate(int _x, int _y) {
		this.X = _x;
		this.Y = _y;
	}
	
	public MapCoordinate clone() {  
        return new MapCoordinate(this.X,this.Y);
    } 

	public int Compare(MapCoordinate mc) {
		return MatteoMap.CompareMC(this, mc);
	}
	
	public ScreenCoordinate toScreenCoordinate(){
		return MatteoMap.MapCoordinateToMapImageRelativeCenterScreenCoordinate(this);
	}
	
}