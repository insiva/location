package com.mlh.model;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import android.database.Cursor;

import com.mlh.Config;
import com.mlh.communication.IUpload;
import com.mlh.communication.IXml;
import com.mlh.database.DaoFactory;
import com.mlh.database.DataBaseConnection;

public class AlbumDao implements IXml<Album>,IUpload<Album>{

	@Override
	public Album getByAttributes(Attributes attr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Album getByXmlString(String xmlStr) {
		// TODO Auto-generated method stub
		Album album=new Album();
		this.readXml(album, xmlStr);
		return album;
	}
	
	public void readXml(Album album,String xmlStr){
		Config.Log(xmlStr);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		AlbumXmlHandler handler=null;
		SAXParser parser = null;
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handler = new AlbumXmlHandler(album);
		StringReader read = new StringReader(xmlStr);
		// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(read);
		try {
			parser.parse(source, handler);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		read.close();
	}
	
	public Album getUnUploadedPicture(){
		String sql = "select p.*,l.latitude,l.longitude from picture as p inner join location_history as l on p.loc_guid=l.guid where p.uploaded=0";
		Cursor cur = DataBaseConnection.query(sql);
		Picture pic;
		Album album = new Album();
		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
			pic = DaoFactory.getPictureDaoInstance().getByCursor(cur);
			album.add(pic);
		}
		return album;
	}

	@Override
	public String toXml(Album t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addToXmlDocument(XmlSerializer xs, Album t) throws Exception {
		// TODO Auto-generated method stub
		
	}
	public class AlbumXmlHandler extends DefaultHandler {
		private Album album;
		
		public AlbumXmlHandler(Album a){
			this.album=a;
		}
		
		public Album getAlbum(){
			return this.album;
		}
		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (localName.equals("list")) {
			} else if (localName.equals("picture")) {
				this.album.add(DaoFactory.getPictureDaoInstance().getByAttributes(atts));
			}
		}

		@Override
		public void endElement(String namespaceURI, String localName,
				String qName) throws SAXException {
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {

		}
	}
	@Override
	public void upload(Album album) {
		for(int i=0;i<album.size();i++){
			Picture pic=album.get(i);
			pic.upload();
		}
	}

}
