package com.mlh.communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.mlh.Config;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author Matteo
 *HTTP�����࣬����ʵ������ֻ�ṩ�����������
 */
public class HttpConnnection {
	
	/**
	 * ����URL��ȡXML�ļ�
	 * @param url ��ȡXML�ļ��ĵ�ַ
	 * @return XML�ַ���
	 */
	public static String getXml(String url) {
		HttpGet httpRequest = new HttpGet(url);
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

	/**
	 * ��һ�����߳��л�ȡXML�ļ�����ͨ��Handler����
	 * @param url ��ȡXML�ļ��ĵ�ַ
	 * @param hdl һ��Handler����ȡXML��ͨ����Handler���д���
	 */
	public static void getXMLByThread(final String url, final Handler hdl) {
		Thread tr = new Thread(new Runnable() {

			@Override
			public void run() {
				HttpConnnection.getXml(url, hdl);
			}
		});
		tr.start();
	}

	/**
	 * ��ȡXML�ļ�����ͨ��Handler����
	 * @param url ��ȡXML�ļ��ĵ�ַ
	 * @param hdl һ��Handler����ȡXML��ͨ����Handler���д���
	 */
	public static void getXml(String url,Handler hdl){
		String xmlStr=HttpConnnection.getXml(url);
		Message msg = new Message();
		msg.what = Config.WHAT_XML;
		Bundle bdl = new Bundle();
		bdl.putString(Config.XML, xmlStr);
		msg.setData(bdl);
		hdl.sendMessage(msg);
	}
	
	/**
	 * ͨ��URL��ȥͼƬ
	 * @param url ͼƬ����
	 * @return Bitmap
	 */
	public static Bitmap getImage(String url) {
		URL picUrl = null;
		Bitmap bm = null;
		try {
			picUrl = new URL(url);
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
	
	/**
	 * �ϴ��ļ���ָ�����ӣ�����ȡ������Ϣ
	 * @param strurl �����ļ���ַ
	 * @param filepath �����ļ�·��
	 * @return �������˷��ص���Ϣ
	 */
	public static String uploadFile(String strurl,String filepath){
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		StringBuilder sb=new StringBuilder();
		try {
			URL url = new URL(strurl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			// ����ÿ�δ��������С��������Ч��ֹ�ֻ���Ϊ�ڴ治�����
			// �˷���������Ԥ�Ȳ�֪�����ݳ���ʱ����û�н����ڲ������ HTTP �������ĵ�����
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// �������������
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// ʹ��POST����
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
					+ filepath.substring(filepath.lastIndexOf("/") + 1)
					+ "\"" + end);
			dos.writeBytes(end);

			FileInputStream fis = new FileInputStream(filepath);
			byte[] buffer = new byte[1024]; // 1k
			int count = 0;
			// ��ȡ�ļ�
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();

			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();

			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();
			while(result!=null){
				sb.append(result+"\n");
				result=br.readLine();
			}
			httpURLConnection.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			// setTitle(e.getMessage());
			return null;
		}
		return sb.toString();
	}
	
	/**
	 * POSTһ��XML�ַ�����ָ����ַ����������Ӧ����
	 * @param strUrl ����XML�ļ���ַ
	 * @param strXml XML�ļ�����
	 * @return �������˷��ص���Ϣ
	 */
	public static String uploadXmlStr(String strUrl,String strXml){
		StringBuilder sb=new StringBuilder();
		try{
		URL url = new URL(strUrl);

		byte[] xmlbyte = strXml.toString().getBytes("UTF-8");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setDoOutput(true);// �������
		conn.setDoInput(true);
		conn.setUseCaches(false);// ��ʹ�û���
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");// ά�ֳ�����
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Content-Length",
				String.valueOf(xmlbyte.length));
		conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
		conn.setRequestProperty("X-ClientType", "2");// �����Զ����ͷ��Ϣ

		conn.getOutputStream().write(xmlbyte);
		conn.getOutputStream().flush();
		conn.getOutputStream().close();

		if (conn.getResponseCode() != 200)
			throw new RuntimeException("����urlʧ��");

		InputStream is = conn.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String result = br.readLine();
		while(result!=null){
			sb.append(result+"\n");
			result=br.readLine();
		}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}

	/**
	 * ����UTF8����
	 * @param s ��Ҫ���������
	 * @return ������
	 */
	public static String encode(String s) {
		try {
			return URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return "";
		}
	}
}
