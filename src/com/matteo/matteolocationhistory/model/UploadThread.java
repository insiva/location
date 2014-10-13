package com.matteo.matteolocationhistory.model;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.matteo.matteolocationhistory.Matteo;
import com.matteo.matteolocationhistory.R;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;

public class UploadThread extends Thread {
	private static UploadThread ut;

	private Handler hdl;

	private UploadThread() {
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void run() {
		Looper.prepare();
		this.hdl = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Upload(msg);
			}
		};
		Looper.loop();
	}

	private void Upload(Message msg) {
		Bundle bdl = msg.getData();
		try {
			switch (msg.what) {
			case Conf.UPLOADWHATPICTURE:
				this.UploadPicture(bdl);
				break;
			case Conf.UPLOADWHATLOCATION:
				this.UploadLocation(bdl);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void UploadPicture(Bundle bdl) {
		ArrayList<Picture> ary=bdl.getParcelableArrayList(Conf.PICTURE);
		Picture pic;
		for(int i=0;i<ary.size();i++)
		{
			pic=ary.get(i);
			pic.BeginUpload();
		}
	}

	private void UploadLocation(Bundle bdl) throws IOException {
		String xml = bdl.getString(Conf.XML);

		URL url = new URL(Conf.RecvLocURL);

		byte[] xmlbyte = xml.toString().getBytes("UTF-8");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setDoOutput(true);// 允许输出
		conn.setDoInput(true);
		conn.setUseCaches(false);// 不使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Content-Length",
				String.valueOf(xmlbyte.length));
		conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
		conn.setRequestProperty("X-ClientType", "2");// 发送自定义的头信息

		conn.getOutputStream().write(xmlbyte);
		conn.getOutputStream().flush();
		conn.getOutputStream().close();

		if (conn.getResponseCode() != 200)
			throw new RuntimeException("请求url失败");

		InputStream is = conn.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String result = br.readLine();
		if (result.equals(Conf.SUCCESS)) {
			result=br.readLine();
			String sql = "update location_history set uploaded=1 where guid in ("+result+")";
			Matteo.MtDb.Execute(sql);
		} else {
			while (result != null) {
				System.out.println(result);
				result = br.readLine();
			}
		}
	}

	public static UploadThread getInstance() {
		if (UploadThread.ut == null) {
			UploadThread.ut = new UploadThread();
			UploadThread.ut.start();
		}
		return UploadThread.ut;
	}

	public static void sendMessage(Message msg) {
		UploadThread u = UploadThread.getInstance();
		while (u.hdl == null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		u.hdl.sendMessage(msg);
	}

	public static void sendEmptyMessage(int what) {
		UploadThread.getInstance().hdl.sendEmptyMessage(what);
	}

	public static void UploadData() {
		try {
			UploadThread.UploadDataLoc();
			UploadThread.UploadDataPic();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public static void UploadDataPic() {
		String sql="select p.*,l.latitude,l.longitude from picture as p inner join location_history as l on p.loc_guid=l.guid where p.uploaded=0";
		Cursor cur=Matteo.MtDb.Query(sql);
		Picture pic;
		ArrayList<Picture> ary=new ArrayList<Picture>();
		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
			pic=new Picture(cur);
			ary.add(pic);
		}
		
		Message msg = new Message();
		Bundle bdl = new Bundle();
		msg.what = Conf.UPLOADWHATPICTURE;
		bdl.putParcelableArrayList(Conf.PICTURE, ary);
		msg.setData(bdl);
		UploadThread.sendMessage(msg);
	}

	public static void UploadDataLoc() throws XmlPullParserException,
			IllegalArgumentException, IllegalStateException, IOException {
		String sql = "select * from location_history where uploaded=0";
		Cursor cur = Matteo.MtDb.Query(sql);
		MtLocation mloc=null;
		StringWriter sw = new StringWriter();
		XmlPullParserFactory xf = XmlPullParserFactory.newInstance();
		XmlSerializer xs = xf.newSerializer();
		// 设置输出流对象
		xs.setOutput(sw);
		xs.startDocument("utf-8", true);
		xs.startTag(null, "history");
		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
			mloc = new MtLocation(cur);
			mloc.AddElement(xs);
		}
		if(mloc==null){
			return;
		}
		xs.endTag(null, "history");
		xs.endDocument();

		Message msg = new Message();
		Bundle bdl = new Bundle();
		msg.what = Conf.UPLOADWHATLOCATION;
		bdl.putString(Conf.XML, sw.toString());
		msg.setData(bdl);
		UploadThread.sendMessage(msg);
	}
}
