package com.mlh.model;


import java.util.Date;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlSerializer;

import android.database.Cursor;

import com.mlh.Config;
import com.mlh.R;
import com.mlh.communication.HttpConnnection;
import com.mlh.communication.IUpload;
import com.mlh.communication.IXml;
import com.mlh.database.DaoFactory;
import com.mlh.database.DataBaseConnection;
import com.mlh.database.IDao;

/**
 * @author Matteo
 *����Ƭ�йص�һЩ����
 */
public class PictureDao implements IDao<Picture>,IXml<Picture>,IUpload<Picture> {

	/**
	 * ��XML�л�ȡPictureʵ��
	 *  */
	@Override
	public Picture getByAttributes(Attributes attrs) {
		// TODO Auto-generated method stub
		Picture pic=new Picture();
		pic.setGuid(Long.parseLong(attrs.getValue("guid")));
		pic.setPictureName(attrs.getValue("filename"));
		pic.setCreateTime(attrs.getValue("createtime"));
		pic.setLocation(DaoFactory.getLocationDaoInstance().getByAttributes(attrs));
		return pic;
	}

	@Override
	public String toXml(Picture t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addToXmlDocument(XmlSerializer xs, Picture t) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(Picture pic) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * �����ݿ��в�������Ƭ��Ϣ
	 * @param picName ��Ƭ��
	 * @param locGuid �������Location��ID
	 * @return
	 */
	public Picture insert(String picName,long locGuid){
		String insertSql = "insert into picture(guid,filename,loc_guid)" + " values("
				+ (new Date()).getTime() + ",'" + picName + "',"
				+locGuid + ")";
		String querySql="select max(guid) as c from picture";
		long id=DataBaseConnection.insertAndGetID(insertSql, querySql);
		return this.getById(id);
	}

	@Override
	public void update(Picture t) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * ɾ����Ƭ
	 *  */
	@Override
	public void delete(long id) {
		// TODO Auto-generated method stub
		String sql="delete from picture wehre guid="+id;
		DataBaseConnection.execute(sql);
	}

	/**
	 * �����ݿ��л��Pictureʵ��
	 * */
	@Override
	public Picture getById(long id) {
		// TODO Auto-generated method stub
		Picture pic=new Picture();
		String sql="select * from picture where guid="+id;
		Cursor cur=DataBaseConnection.query(sql);
		cur.moveToFirst();
		pic=DaoFactory.getPictureDaoInstance().getByCursor(cur);
		return pic;
	}

	/**
	 * ������Ƭ���ϴ���Ϣ
	 */
	@Override
	public void updateUploadedFlag(long id) {
		// TODO Auto-generated method stub
		String sql="update picture set uploaded=1 where guid="+id;
		DataBaseConnection.execute(sql);
	}

	@Override
	public Picture getByXmlString(String xmlStr) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * �����ݿ��Cursor��ȡPictureʵ��
*/
	@Override
	public Picture getByCursor(Cursor cur) {
		// TODO Auto-generated method stub
		Picture pic=new Picture();
		//cur.moveToFirst();
		int g = cur.getColumnIndex(Config.getString(R.string.ColumnGuid));
		pic.setGuid(cur.getLong(g));
		g = cur.getColumnIndex(Config.getString(R.string.ColumnPicName));
		pic.setPictureName (cur.getString(g));
		g = cur.getColumnIndex(Config.getString(R.string.ColumnCreateTime));
		pic.setCreateTime(cur.getString(g));
		g = cur.getColumnIndex(Config.getString(R.string.ColumnLocGuid));
		pic.setLocation(DaoFactory.getLocationDaoInstance().getById(cur.getLong(g)));
		return pic;
	}

	/**
	 * �ϴ���Ƭ
*/
	@Override
	public void upload(Picture pic) {
		// TODO Auto-generated method stub
		if(!pic.rawPicureExists()){
			this.updateUploadedFlag(pic.getGuid());
			return;
		}
		String url = Picture.RECEIVE_PICTURE_PAGE
				+ "?filename="
				+ HttpConnnection.encode(pic.getPictureName())
				+ "&guid="
				+ pic.getGuid()
				+ "&lguid="
				+ pic.getLocationGuid()
				+ "&createtime="
				+ HttpConnnection.encode(pic.getCreateTimeStr());
		String result=HttpConnnection.uploadFile(url, pic.getPicturePath()).substring(0, 1);
		if(result.equals(Config.SUCCESS)){
			this.updateUploadedFlag(pic.getGuid());
		}
	}
}
