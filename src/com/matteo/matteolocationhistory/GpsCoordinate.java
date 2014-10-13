package com.matteo.matteolocationhistory;


public class GpsCoordinate {
	public double Latitude, Longitude;

	public GpsCoordinate(double lat, double lon) {
		this.Latitude = lat;
		this.Longitude = lon;
	}

	public double Distance(GpsCoordinate gc) {
		return MatteoMap.GpsDistance(this, gc);
	}
	
	public GpsCoordinate clone() {  
        return new GpsCoordinate(this.Latitude,this.Longitude);
    } 
}