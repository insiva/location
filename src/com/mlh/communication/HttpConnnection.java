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
 *HTTP连接类，不能实例化，只提供相关联网函数
 */
public class HttpConnnection {
	
	/**
	 * 根据URL获取XML文件
	 * @param url 获取XML文件的地址
	 * @return XML字符串
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
	 * 在一个新线程中获取XML文件，并通过Handler处理
	 * @param url 获取XML文件的地址
	 * @param hdl 一个Handler，获取XML后，通过此Handler进行处理
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
	 * 获取XML文件，并通过Handler处理
	 * @param url 获取XML文件的地址
	 * @param hdl 一个Handler，获取XML后，通过此Handler进行处理
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
	 * 通过URL过去图片
	 * @param url 图片连接
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
	 * 上传文件至指定连接，并获取返回信息
	 * @param strurl 接收文件地址
	 * @param filepath 本地文件路径
	 * @return 服务器端返回的信息
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
			// 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
			// 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// 允许输入输出流
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// 使用POST方法
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
			// 读取文件
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
	 * POST一个XML字符串至指定地址，并返回相应内容
	 * @param strUrl 接收XML文件地址
	 * @param strXml XML文件内容
	 * @return 服务器端返回的信息
	 */
	public static String uploadXmlStr(String strUrl,String strXml){
		StringBuilder sb=new StringBuilder();
		try{
		URL url = new URL(strUrl);

		byte[] xmlbyte = strXml.toString().getBytes("UTF-8");
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
	 * 进行UTF8编码
	 * @param s 需要编码的内容
	 * @return 编码结果
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
