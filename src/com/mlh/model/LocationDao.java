package com.mlh.model;

import java.util.Date;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlSerializer;

import android.database.Cursor;

import com.baidu.location.BDLocation;
import com.mlh.Config;
import com.mlh.R;
import com.mlh.communication.IXml;
import com.mlh.database.DataBaseConnection;
import com.mlh.database.IDao;

public class LocationDao implements IDao<Location>,IXml<Location> {

	@Override
	public void insert(Location t){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Location t){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(long id){
		// TODO Auto-generated method stub
		
	}

	@Override
	public Location getById(long id){
		// TODO Auto-generated method stub
		String sql="select * from location_history where guid="+id;
		Cursor cur=DataBaseConnection.query(sql);
		cur.moveToFirst();
		Location loc=this.getByCursor(cur);
		return loc;
	}

	public Location insert(BDLocation bloc){
		String insertSql = "insert into location_history(guid,longitude,latitude,altitude,accuracy,speed,bearing,"
				+ "address,province,city,district,street,streetnum) values("
				+ new Date().getTime()
				+","
				+ bloc.getLongitude()
				+ ","
				+ bloc.getLatitude()
				+ ","
				+ bloc.getAltitude()
				+ ","
				+ bloc.getRadius()
				+ ","
				+ bloc.getSpeed()
				+ ","
				+ bloc.getDerect()
				+ ",'"
				+ bloc.getAddrStr()
				+ "','"
				+ bloc.getProvince()
				+ "','"
				+ bloc.getCity()
				+ "','"
				+ bloc.getDistrict()
				+ "','"
				+ bloc.getStreet()
				+ "','" + bloc.getStreetNumber() + "')";
		String querySql="select max(guid) as c from location_history";
		long id=DataBaseConnection.insertAndGetID(insertSql, querySql);
		return this.getById(id);
	}

	@Override
	public Location getByAttributes(Attributes attrs) {
		// TODO Auto-generated method stub
		Location loc=new Location();
		loc.setGuid(Long.parseLong(attrs.getValue("loc_guid")));
		loc.setLatitude(Double.parseDouble(attrs.getValue("latitude")));
		loc.setAccuracy(Double.parseDouble(attrs.getValue("accuracy")));
		loc.setLongitude(Double.parseDouble(attrs.getValue("longitude")));
		loc.setAltitude(Double.parseDouble(attrs.getValue("altitude")));
		loc.setSpeed(Float.parseFloat(attrs.getValue("speed")));
		loc.setBearing(Float.parseFloat(attrs.getValue("bearing")));
		loc.setCreateTime(attrs.getValue("loc_createtime"));
		loc.setAddress(attrs.getValue("address"));
		loc.setProvince(attrs.getValue("province"));
		loc.setCity(attrs.getValue("city"));
		loc.setDistrict(attrs.getValue("district"));
		loc.setStreet(attrs.getValue("street"));
		loc.setStreetNum(attrs.getValue("streetnum"));
		return loc;
	}

	
	public Location getByPictureAttributes(Attributes attrs) {
		// TODO Auto-generated method stub
		Location loc=new Location();
		loc.setGuid(Long.parseLong(attrs.getValue("loc_guid")));
		loc.setLatitude(Double.parseDouble(attrs.getValue("latitude")));
		loc.setAccuracy(Double.parseDouble(attrs.getValue("accuracy")));
		loc.setLongitude(Double.parseDouble(attrs.getValue("longitude")));
		loc.setAltitude(Double.parseDouble(attrs.getValue("altitude")));
		loc.setSpeed(Float.parseFloat(attrs.getValue("speed")));
		loc.setBearing(Float.parseFloat(attrs.getValue("bearing")));
		loc.setCreateTime(attrs.getValue("loc_createtime"));
		loc.setAddress(attrs.getValue("address"));
		loc.setProvince(attrs.getValue("province"));
		loc.setCity(attrs.getValue("city"));
		loc.setDistrict(attrs.getValue("district"));
		loc.setStreet(attrs.getValue("street"));
		loc.setStreetNum(attrs.getValue("streetnum"));
		return loc;
	}

	@Override
	public String toXml(Location loc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addToXmlDocument(XmlSerializer xs,Location loc) throws Exception {
		// TODO Auto-generated method stub
		xs.startTag(null, "location");
		xs.attribute(null, "guid", Long.toString(loc.getGuid()));
		xs.attribute(null, "latitude", Double.toString(loc.getLatitude()));
		xs.attribute(null, "longitude", Double.toString(loc.getLongitude()));
		xs.attribute(null, "altitude", Double.toString(loc.getAltitude()));
		xs.attribute(null, "speed", Float.toString(loc.getSpeed()));
		xs.attribute(null, "bearing", Float.toString(loc.getBearing()));
		xs.attribute(null, "createtime",loc.getCreateTimeStr());
		xs.attribute(null, "accuracy", Double.toString(loc.getAccuracy()));
		xs.attribute(null, "address", loc.getAddress());
		xs.attribute(null, "province", loc.getProvince());
		xs.attribute(null, "city",loc.getCity());
		xs.attribute(null, "district", loc.getDistrict());
		xs.attribute(null, "street", loc.getStreet());
		xs.attribute(null, "streetnum", loc.getStreetNum());
		xs.endTag(null, "location");
	}

	@Override
	public void updateUploadedFlag(long id) {
		// TODO Auto-generated method stub
		String sql="update location_history set uploaded=1 where guid="+id;
		DataBaseConnection.execute(sql);
	}

	@Override
	public Location getByXmlString(String xmlStr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Location getByCursor(Cursor cur) {
		// TODO Auto-generated method stub
		Location loc=new Location();
		int gdi = cur.getColumnIndex(Config.getString(R.string.ColumnGuid));
		int cti = cur.getColumnIndex(Config.getString(R.string.ColumnCreateTime));
		int lati = cur.getColumnIndex(Config.getString(R.string.ColumnLatitude));
		int loni = cur.getColumnIndex(Config.getString(R.string.ColumnLongitude));
		int acri = cur.getColumnIndex(Config.getString(R.string.ColumnAccuracy));
		int alti = cur.getColumnIndex(Config.getString(R.string.ColumnAltitude));
		int spi = cur.getColumnIndex(Config.getString(R.string.ColumnSpeed));
		int bri = cur.getColumnIndex(Config.getString(R.string.ColumnBearing));
		int adi = cur.getColumnIndex(Config.getString(R.string.ColumnAddress));
		int pri = cur.getColumnIndex(Config.getString(R.string.ColumnProvince));
		int cyi = cur.getColumnIndex(Config.getString(R.string.ColumnCity));
		int dii = cur.getColumnIndex(Config.getString(R.string.ColumnDistrict));
		int sti = cur.getColumnIndex(Config.getString(R.string.ColumnStreet));
		int sni = cur.getColumnIndex(Config.getString(R.string.ColumnStreetNum));
		loc.setGuid(cur.getLong(gdi));
		loc.setLatitude(cur.getDouble(lati));
		loc.setLongitude(cur.getDouble(loni));
		loc.setAccuracy(cur.getDouble(acri));
		loc.setAltitude(cur.getDouble(alti));
		loc.setCreateTime(cur.getString(cti));
		loc.setSpeed(cur.getFloat(spi));
		loc.setBearing(cur.getFloat(bri));
		loc.setAddress(cur.getString(adi));
		loc.setProvince(cur.getString(pri));
		loc.setCity(cur.getString(cyi));
		loc.setDistrict(cur.getString(dii));
		loc.setStreet(cur.getString(sti));
		loc.setStreetNum(cur.getString(sni));
		return loc;
	}
}
