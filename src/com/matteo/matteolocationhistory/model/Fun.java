package com.matteo.matteolocationhistory.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;

import com.matteo.matteolocationhistory.Matteo;
import com.matteo.matteolocationhistory.MatteoHistory;

public class Fun {
	public static Date ParseDate(String s) {
		Date d = null;
		try {
			d = MatteoHistory.TimeFormatter.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}

	public static String ParseDate(Date d, String f) {
		return DateFormat.format(f, d).toString();
	}

	public static String ParseDate(Date d) {
		return DateFormat.format(Conf.DateF, d).toString();
	}

	public static String UPP(String u, String k, String v) {
		StringBuilder sb = new StringBuilder(u);
		if (u.indexOf('?') > 0) {
			sb.append('&').append(k).append('=').append(v);
		} else {
			sb.append('?').append(k).append('=').append(v);
		}
		return sb.toString();
	}

	public static String Encode(String s) {
		try {
			return URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return "";
		}
	}

	public static String getString(int resId) {
		return Matteo.ThisApplication.getString(resId);
	}

	public static String LochToXML(Cursor cur) {
		return "";
	}

	public static void Log(String l) {
		if (Conf.NOTLOG)
			return;
		System.out.println("------------------");
		System.out.println(l);
		System.out.println("------------------");
	}

	public static void Log(int... is) {
		if (Conf.NOTLOG)
			return;
		String s = "";
		for (int i : is) {
			s = s + Integer.toString(i) + ",";
		}
		Log.e("Matteo", "------------------");
		Log.e("Matteo", s);
	}

	public static void Log(String ss, int... is) {
		if (Conf.NOTLOG)
			return;
		String s = ss + ":";
		for (int i : is) {
			s = s + Integer.toString(i) + ",";
		}
		Log.e("Matteo", "------------------");
		Log.e("Matteo", s);
	}

	public static void Log(int l) {
		Fun.Log(Integer.toString(l));
	}

	public static void Log(double l) {
		Fun.Log(Double.toString(l));
	}

	public static void Log(float l) {
		Fun.Log(Float.toString(l));
	}

	public static void GetXMLByThread(final String u, final Handler hdl) {
		Thread tr = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Fun.GetXML(u, hdl);
			}
		});
		tr.start();
	}
	
	public static String GetXML(String u){
		HttpGet httpRequest = new HttpGet(u);
		String xmlStr = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				Log.e("XXX", "can't reach:"
						+ httpResponse.getStatusLine().getStatusCode());
				return null;
			}
			InputStream is = httpResponse.getEntity().getContent();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String s = br.readLine();
			while (s != null) {
				sb.append(s);
				s = br.readLine();
			}
			xmlStr = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return xmlStr;
	}

	public static void GetXML(String u, Handler hdl) {
		String xmlStr=Fun.GetXML(u);
		Message msg = new Message();
		msg.what = Conf.GETXMLWHAT;
		Bundle bdl = new Bundle();
		bdl.putString(Conf.XML, xmlStr);
		msg.setData(bdl);
		Fun.Log(xmlStr);
		hdl.sendMessage(msg);
	}

	public static void GetImageByThread(final String fn, final int pt,
			final Handler hdl) {
		Thread tr = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Fun.GetImage(fn, pt);
				hdl.sendEmptyMessage(Conf.PICTURETLOADED);
			}
		});
		tr.start();
	}

	public static Bitmap GetImage(String u) {
		URL picUrl = null;
		Bitmap bm = null;
		try {
			picUrl = new URL(u);
			bm = BitmapFactory.decodeStream(picUrl.openStream());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bm;
	}

	public static Bitmap GetImage(String fn, int pt) {
		URL picUrl = null;
		Bitmap bm = null;
		String u = null;
		String tu = null;
		switch (pt) {
		case Conf.PICTURETYPEH:
			u = Conf.HPicDirURL + fn;
			tu = Matteo.TempPicDirectory + Conf.PICTURETYPESTRH + fn;
			break;
		case Conf.PICTURETYPEM:
			u = Conf.MPicDirURL + fn;
			tu = Matteo.TempPicDirectory + Conf.PICTURETYPESTRM + fn;
			break;
		case Conf.PICTURETYPEL:
			u = Conf.LPicDirURL + fn;
			tu = Matteo.TempPicDirectory + Conf.PICTURETYPESTRL + fn;
			break;
		case Conf.PICTURETYPES:
			u = Conf.SPicDirURL + fn;
			tu = Matteo.TempPicDirectory + Conf.PICTURETYPESTRS + fn;
			break;
		case Conf.PICTURETYPEI:
			String n=fn.replace("jpg", "png").replace("jpeg", "png");
			u = Conf.IPicDirURL + n;
			tu = Matteo.TempPicDirectory + Conf.PICTURETYPESTRI + n;
			break;
		}
		File f = new File(tu);
		if (f.exists()) {
			bm = BitmapFactory.decodeFile(tu);
			return bm;
		}
		try {
			picUrl = new URL(u);
			bm = BitmapFactory.decodeStream(picUrl.openStream());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bm;
	}
	
	public static String GetDistanceText(int d) {
		if (d < 1000)
			return d + "m";
		String ds = Integer.toString(d);
		StringBuilder sb = new StringBuilder();
		for (int i = ds.length() - 1; i >= 0; i--) {
			if (ds.charAt(i) == '0') {
				continue;
			}
			if ((ds.length() - i) == 3) {
				sb.insert(0, '.');
			}
			sb.insert(0, ds.charAt(i));
		}
		sb.append("km");
		return sb.toString();
	}
}
