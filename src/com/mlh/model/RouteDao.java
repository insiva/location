package com.mlh.model;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.database.Cursor;

import com.mlh.Config;
import com.mlh.communication.HttpConnnection;
import com.mlh.communication.IUpload;
import com.mlh.communication.IXml;
import com.mlh.database.DaoFactory;
import com.mlh.database.DataBaseConnection;

public class RouteDao implements IXml<Route>,IUpload<Route> {

	@Override
	public Route getByAttributes(Attributes attr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Route getByXmlString(String xmlStr) {
		// TODO Auto-generated method stub
		SAXParserFactory factory = SAXParserFactory.newInstance();
		RouteXmlHandler handler=null;
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
		handler = new RouteXmlHandler();
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
		return handler.getRoute();
	}

	
	public Route getUnUploadedLocation(){
		String sql = "select * from location_history where uploaded=0";
		Cursor cur = DataBaseConnection.query(sql);
		Location loc;
		Route route = new Route();
		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
			loc = DaoFactory.getLocationDaoInstance().getByCursor(cur);
			route.add(loc);
		}
		return route;
	}
	
	@Override
	public String toXml(Route t) {
		// TODO Auto-generated method stub
		StringWriter sw = new StringWriter();
		try{
		XmlPullParserFactory xf = XmlPullParserFactory.newInstance();
		XmlSerializer xs = xf.newSerializer();
		// 设置输出流对象
		xs.setOutput(sw);
		xs.startDocument("utf-8", true);
		xs.startTag(null, "history");
		for (int i=0;i<t.size();i++) {
			Location loc=t.get(i);
			//this.addToXmlDocument(xs, t);
			DaoFactory.getLocationDaoInstance().addToXmlDocument(xs, loc);
		}
		xs.endTag(null, "history");
		xs.endDocument();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return sw.toString();
	}

	@Override
	public void addToXmlDocument(XmlSerializer xs, Route t) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public class RouteXmlHandler extends DefaultHandler {
		private Route route;
		
		public RouteXmlHandler(){
			this.route=new Route();
		}
		
		public Route getRoute(){
			return this.route;
		}
		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (localName.equals("list")) {
			} else if (localName.equals("location")) {
				this.route.add(DaoFactory.getLocationDaoInstance().getByAttributes(atts));
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
	public void upload(Route t) {
		// TODO Auto-generated method stub
		String strUrl=Route.RECEIVE_ROUTE_PAGE;
		String strXml=this.toXml(t);
		String result=HttpConnnection.uploadXmlStr(strUrl, strXml);
		String[] lines=result.split("\n");
		if(lines==null)return;
		if(lines[0].equals(Config.SUCCESS)&&lines.length>1){
			String sql = "update location_history set uploaded=1 where guid in ("
					+ lines[1] + ")";
			DataBaseConnection.execute(sql);
		}
	}
}
