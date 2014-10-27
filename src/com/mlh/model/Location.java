package com.mlh.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmlpull.v1.XmlSerializer;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

import com.baidu.mapapi.model.LatLng;
import com.mlh.Config;
import com.mlh.database.DaoFactory;

public class Location implements Parcelable {
	public static final String ACTION_NEW_LOCATION="com.mlh.location.actionnewlocation";
	private static Location CurrentLocation;
	private long Guid;
	private double Latitude, Longitude, Altitude, Accuracy;
	private Date CreateTime;
	private float Speed, Bearing;
	private String Province, City, District, Street, StreetNum, Address;

	public Location() {
	}

	public Location(Location loc) {
		this.Guid = loc.Guid;
		this.Latitude = loc.Latitude;
		this.Longitude = loc.Longitude;
		this.Altitude = loc.Altitude;
		this.Accuracy = loc.Accuracy;
		this.CreateTime = loc.CreateTime;
		this.Speed = loc.Speed;
		this.Bearing = loc.Bearing;
		this.Province = loc.Province;
		this.City = loc.City;
		this.District = loc.District;
		this.Street = loc.Street;
		this.StreetNum = loc.StreetNum;
		this.Address = loc.Address;
	}

	public void setGuid(long guid) {
		this.Guid = guid;
	}

	public void setLatitude(double lat) {
		this.Latitude = lat;
	}

	public void setLongitude(double lng) {
		this.Longitude = lng;
	}

	public void setAltitude(double alti) {
		this.Altitude = alti;
	}

	public void setAccuracy(double accu) {
		this.Accuracy = accu;
	}

	public void setCreateTime(Date ct) {
		this.CreateTime = ct;
	}

	@SuppressLint("SimpleDateFormat")
	public void setCreateTime(String s) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(
					Config.DEFAULT_DATEFORMAT);
			this.CreateTime = sdf.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSpeed(float speed) {
		this.Speed = speed;
	}

	public void setBearing(float bearing) {
		this.Bearing = bearing;
	}

	public void setProvince(String prov) {
		this.Province = prov;
	}

	public void setCity(String city) {
		this.City = city;
	}

	public void setDistrict(String dist) {
		this.District = dist;
	}

	public void setStreet(String street) {
		this.Street = street;
	}

	public void setStreetNum(String sn) {
		this.StreetNum = sn;
	}

	public void setAddress(String addr) {
		this.Address = addr;
	}

	public long getGuid() {
		return this.Guid;
	}

	public double getLatitude() {
		return this.Latitude;
	}

	public double getLongitude() {
		return this.Longitude;
	}

	public double getAltitude() {
		return this.Altitude;
	}

	public double getAccuracy() {
		return this.Accuracy;
	}

	public LatLng getLatLng() {
		return new LatLng(this.getLatitude(), this.getLongitude());
	}

	public Date getCreateTime() {
		return this.CreateTime;
	}

	public String getCreateTimeStr() {
		return DateFormat.format(Config.DEFAULT_DATEFORMAT, this.CreateTime)
				.toString();
	}

	public float getSpeed() {
		return this.Speed;
	}

	public float getBearing() {
		return this.Bearing;
	}

	public String getProvince() {
		return this.Province;
	}

	public String getCity() {
		return this.City;
	}

	public String getDistrict() {
		return this.District;
	}

	public String getStreet() {
		return this.Street;
	}

	public String getStreetNum() {
		return this.StreetNum;
	}

	public String getAddress() {
		return this.Address;
	}

	public void addToXmlDocument(XmlSerializer xs) {
		try {
			DaoFactory.getLocationDaoInstance().addToXmlDocument(xs, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Location getCurrentLocation() {
		return Location.CurrentLocation;
	}

	public static void setCurrentLocation(Location loc) {
		Location.CurrentLocation = loc;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeLong(this.Guid);
		dest.writeDouble(this.Latitude);
		dest.writeDouble(this.Longitude);
		dest.writeDouble(this.Altitude);
		dest.writeDouble(this.Accuracy);
		dest.writeString(this.getCreateTimeStr());
		dest.writeFloat(this.Speed);
		dest.writeFloat(this.Bearing);
		dest.writeString(this.Province);
		dest.writeString(this.City);
		dest.writeString(this.District);
		dest.writeString(this.Street);
		dest.writeString(this.StreetNum);
		dest.writeString(this.Address);
	}

	public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
		public Location createFromParcel(Parcel in) {
			Location loc=new Location();
			loc.setGuid(in.readLong());
			loc.setLatitude(in.readDouble());
			loc.setLongitude(in.readDouble());
			loc.setAltitude(in.readDouble());
			loc.setAccuracy(in.readDouble());
			loc.setCreateTime(in.readString());
			loc.setSpeed(in.readFloat());
			loc.setBearing(in.readFloat());
			loc.setProvince(in.readString());
			loc.setCity(in.readString());
			loc.setDistrict(in.readString());
			loc.setStreet(in.readString());
			loc.setStreetNum(in.readString());
			loc.setAddress(in.readString());
			return loc;
		}

		public Location[] newArray(int size) {
			return new Location[size];
		}
	};
}
