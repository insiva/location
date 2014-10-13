package com.matteo.matteolocationhistory.model;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.baidu.location.BDLocation;
import com.matteo.matteolocationhistory.CurrentLocation;
import com.matteo.matteolocationhistory.DbConn;
import com.matteo.matteolocationhistory.Matteo;
import com.matteo.matteolocationhistory.R;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

public class MtLocation implements Parcelable {
	public int Guid;
	public double Latitude, Longitude, Altitude, Accuracy;
	public Date CreateTime;
	public float Speed, Bearing;
	public String Province, City, District, Street, StreetNum, Address;

	public MtLocation(){
		
	}

	public MtLocation(Attributes attrs){
		this.Guid=Integer.parseInt(attrs.getValue("loc_guid"));
		this.Latitude=Double.parseDouble(attrs.getValue("latitude"));
		this.Accuracy=Double.parseDouble(attrs.getValue("accuracy"));
		this.Longitude=Double.parseDouble(attrs.getValue("longitude"));
		this.Altitude=Double.parseDouble(attrs.getValue("altitude"));
		this.Speed=Float.parseFloat(attrs.getValue("speed"));
		this.Bearing=Float.parseFloat(attrs.getValue("bearing"));
		this.CreateTime=Fun.ParseDate(attrs.getValue("loc_createtime"));
		this.Address=attrs.getValue("address");
		this.Province=attrs.getValue("province");
		this.City=attrs.getValue("city");
		this.District=attrs.getValue("district");
		this.Street=attrs.getValue("street");
		this.StreetNum=attrs.getValue("streetnum");
	}

	public MtLocation(int g){
		this.Guid=g;
		String sql="select * from location_history where guid="+this.Guid;
		Cursor cur=Matteo.MtDb.Query(sql);
		cur.moveToFirst();
		this.InitByDb(cur);
	}

	public MtLocation(Cursor cur){
		this.InitByDb(cur);
	}
	
	public void InitByDb(Cursor cur) {
		int gdi = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnGuid));
		int cti = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnCreateTime));
		int lati = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnLatitude));
		int loni = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnLongitude));
		int acri = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnAccuracy));
		int alti = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnAltitude));
		int spi = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnSpeed));
		int bri = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnBearing));
		int adi = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnAddress));
		int pri = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnProvince));
		int cyi = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnCity));
		int dii = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnDistrict));
		int sti = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnStreet));
		int sni = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnStreetNum));
		this.Guid = cur.getInt(gdi);
		this.Latitude = cur.getDouble(lati);
		this.Longitude = cur.getDouble(loni);
		this.Accuracy = cur.getDouble(acri);
		this.Altitude = cur.getDouble(alti);
		this.CreateTime = Fun.ParseDate(cur.getString(cti));
		this.Speed = cur.getFloat(spi);
		this.Bearing = cur.getFloat(bri);
		this.Address=cur.getString(adi);
		this.Province=cur.getString(pri);
		this.City=cur.getString(cyi);
		this.District=cur.getString(dii);
		this.Street=cur.getString(sti);
		this.StreetNum=cur.getString(sni);
	}

	private MtLocation(BDLocation loc) {
		this.Latitude = loc.getLatitude();
		this.Longitude = loc.getLongitude();
		this.Altitude = loc.getAltitude();
		this.Speed = loc.getSpeed();
		this.Accuracy = loc.getRadius();
		this.Bearing = loc.getDerect();// loc.get
		this.Province = loc.getProvince();
		this.City = loc.getCity();
		this.District = loc.getDistrict();
		this.Street = loc.getStreet();
		this.StreetNum = loc.getStreetNumber();
		this.Address = loc.getAddrStr();
	}

	public static MtLocation Insert(BDLocation loc) {
		MtLocation mloc = new MtLocation(loc);
		String sql = "insert into location_history(longitude,latitude,altitude,accuracy,speed,bearing,"
				+ "address,province,city,district,street,streetnum) values("
				+ mloc.Longitude
				+ ","
				+ mloc.Latitude
				+ ","
				+ mloc.Altitude
				+ ","
				+ mloc.Accuracy
				+ ","
				+ mloc.Speed
				+ ","
				+ mloc.Bearing
				+ ",'"
				+ mloc.Address
				+ "','"
				+ mloc.Province
				+ "','"
				+ mloc.City
				+ "','"
				+ mloc.District
				+ "','"
				+ mloc.Street
				+ "','" + mloc.StreetNum + "')";
		Matteo.MtDb.Execute(sql);
		mloc.CreateTime=new Date();
		int g=DbConn.GetMaxGuid("location_history");
		mloc.Guid=g;
		CurrentLocation.Set(mloc);
		try{
		mloc.Upload();
		}catch(Exception e){
			e.printStackTrace();
		}
		
        Intent intent = new Intent();  
        intent.setAction(Matteo.ActionNewLocation);
        Matteo.ThisApplication.sendBroadcast(intent);
		return mloc;
	}
	
	public void Upload() throws XmlPullParserException, IllegalArgumentException, IllegalStateException, IOException{
		StringWriter sw = new StringWriter();
		XmlPullParserFactory xf = XmlPullParserFactory.newInstance();
		XmlSerializer xs = xf.newSerializer();
		// 设置输出流对象
		xs.setOutput(sw);
		xs.startDocument("utf-8", true);
		xs.startTag(null, "history");
		this.AddElement(xs);
		xs.endTag(null, "history");
		xs.endDocument();

		Message msg = new Message();
		Bundle bdl = new Bundle();
		msg.what = Conf.UPLOADWHATLOCATION;
		bdl.putString(Conf.XML, sw.toString());
		msg.setData(bdl);
		UploadThread.sendMessage(msg);
	}

	public void AddElement(XmlSerializer xs) throws IllegalArgumentException,
			IllegalStateException, IOException {
		xs.startTag(null, "location");
		xs.attribute(null, "guid", Integer.toString(this.Guid));
		xs.attribute(null, "latitude", Double.toString(this.Latitude));
		xs.attribute(null, "longitude", Double.toString(this.Longitude));
		xs.attribute(null, "altitude", Double.toString(this.Altitude));
		xs.attribute(null, "speed", Float.toString(this.Speed));
		xs.attribute(null, "bearing", Float.toString(this.Bearing));
		xs.attribute(null, "createtime",Fun.ParseDate(this.CreateTime, Conf.DateF));
		xs.attribute(null, "accuracy", Double.toString(this.Accuracy));
		xs.attribute(null, "address", this.Address);
		xs.attribute(null, "province", this.Province);
		xs.attribute(null, "city", this.City);
		xs.attribute(null, "district", this.District);
		xs.attribute(null, "street", this.Street);
		xs.attribute(null, "streetnum", this.StreetNum);
		xs.endTag(null, "location");
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(this.Guid);
		dest.writeFloat(this.Bearing);
		dest.writeFloat(this.Speed);
		dest.writeDouble(this.Accuracy);
		dest.writeDouble(this.Altitude);
		dest.writeDouble(this.Latitude);
		dest.writeDouble(this.Longitude);
		dest.writeString(this.Address);
		dest.writeString(this.City);
		dest.writeString(this.District);
		dest.writeString(this.Province);
		dest.writeString(this.Street);
		dest.writeString(this.StreetNum);
		dest.writeString(Fun.ParseDate(this.CreateTime));
	}
	
	public static final Parcelable.Creator<MtLocation> CREATOR = new Creator<MtLocation>() {
		 
        @Override
        public MtLocation[] newArray(int size) {
            return null;
        }
 
        @Override
        public MtLocation createFromParcel(Parcel source) {
        	MtLocation result = MtLocation.CreateFromParcel(source); 
            return result;
        }
    };
    
    public static MtLocation CreateFromParcel(Parcel src){
    	MtLocation loc=new MtLocation();
    	loc.Guid=src.readInt();
    	loc.Bearing=src.readFloat();
    	loc.Speed=src.readFloat();
    	loc.Accuracy=src.readDouble();
    	loc.Altitude=src.readDouble();
    	loc.Latitude=src.readDouble();
    	loc.Longitude=src.readDouble();
    	loc.Address=src.readString();
    	loc.City=src.readString();
    	loc.District=src.readString();
    	loc.Province=src.readString();
    	loc.Street=src.readString();
    	loc.StreetNum=src.readString();
    	loc.CreateTime=Fun.ParseDate(src.readString());
    	return loc;
    }
}
