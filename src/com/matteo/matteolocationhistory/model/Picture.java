package com.matteo.matteolocationhistory.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.xml.sax.Attributes;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.matteo.matteolocationhistory.Matteo;
import com.matteo.matteolocationhistory.MatteoHistory;
import com.matteo.matteolocationhistory.R;

public class Picture implements Parcelable  {
	public int Guid,LocGuid;
	public String Name,PicPath;
	public MtLocation Loc;
	public Date CreateTime;
	public Bitmap SBmPic=null,HBmPic=null,MBmPic=null,LBmPic=null,IBmPic=null;

	public Picture(int g, boolean by_network) {
		this.Guid = g;
		if (by_network) {
			this.InitByNetwork();
		} else {
			String sql="select * from picture where guid="+this.Guid;
			Cursor cur = Matteo.MtDb.Query(sql);
			cur.moveToFirst();
			this.InitByDb(cur);
			this.Loc=new MtLocation(this.LocGuid);
		}
	}
	
	public Picture(Cursor cur){
		this.InitByDb(cur);
		this.Loc=new MtLocation(this.LocGuid);
	}
	
	private Picture(){
		
	}
	
	public Picture(Attributes attrs){
		this.Guid=Integer.parseInt(attrs.getValue("guid"));
		this.LocGuid=Integer.parseInt(attrs.getValue("loc_guid"));
		this.Name=attrs.getValue("filename");
		//this.
		this.CreateTime=Fun.ParseDate(attrs.getValue("createtime"));
		this.PicPath = Matteo.PictureDirectory + this.Name;
		this.Loc=new MtLocation(attrs);
	}

	private void InitByNetwork() {

	}

	public Uri GetUri() {
		return Uri.fromFile(new File(this.PicPath));
	}

	public Uri GetUri(int t) {
		Uri u=null;
		String p=null;
		switch(t){
		case Conf.PICTURETYPEH:
			p=Conf.HPicDirURL+this.Name;
			break;
		case Conf.PICTURETYPEM:
			p=Conf.MPicDirURL+this.Name;
			break;
		case Conf.PICTURETYPEL:
			p=Conf.LPicDirURL+this.Name;
			break;
		case Conf.PICTURETYPES:
			p=Conf.SPicDirURL+this.Name;
			break;
		default:
			return null;
		}
		File f=new File(p);
		u=Uri.fromFile(f);
		return u;
	}

	private void InitByDb(Cursor cur) {
		int g = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnGuid));
		this.Guid = cur.getInt(g);
		g = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnPicName));
		this.Name = cur.getString(g);
		g = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnCreateTime));
		this.CreateTime = Fun.ParseDate(cur.getString(g));
		g = cur.getColumnIndex(Matteo.ThisApplication
				.getString(R.string.ColumnLocGuid));
		this.LocGuid=cur.getInt(g);
		this.PicPath = Matteo.PictureDirectory + this.Name;
	}
	
	public void Upload(){
		ArrayList<Picture> ary=new ArrayList<Picture>();
		ary.add(this);
		Message msg=new Message();
		Bundle bdl=new Bundle();
		msg.what=Conf.UPLOADWHATPICTURE;
		bdl.putParcelableArrayList(Conf.PICTURE, ary);
		msg.setData(bdl);
		UploadThread.sendMessage(msg);
	}

	public void BeginUpload(){
		String end = "\r\n";
	    String twoHyphens = "--";
	    String boundary = "******";
	    try
	    {
	      URL url = new URL(this.GetRecvURL());
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
	          + this.PicPath.substring(this.PicPath.lastIndexOf("/") + 1)
	          + "\""
	          + end);
	      dos.writeBytes(end);
	 
	      FileInputStream fis = new FileInputStream(this.PicPath);
	      byte[] buffer = new byte[1024]; // 1k
	      int count = 0;
	      // 读取文件
	      while ((count = fis.read(buffer)) != -1)
	      {
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
	      if(result.equals(Conf.SUCCESS)){
	    	  String sql="update picture set uploaded=1 where guid="+this.Guid;
	    	  Matteo.MtDb.Execute(sql);
	      }
	      while(result!=null){
	    	  System.out.println(result);
	    	  result = br.readLine();
	      }
	      isr.close();
	      br.close();
	      is.close();
	      dos.close();
	      httpURLConnection.disconnect();
	 
	    } catch (Exception e)
	    {
	      e.printStackTrace();
	      //setTitle(e.getMessage());
	    }
	  }
	
	private String GetRecvURL()
	{
		String u=Conf.RecvPicURL
				+"?filename="+Fun.Encode(this.Name)
				+"&guid="+this.Guid
				+"&lguid="+this.LocGuid
				+"&createtime="+Fun.Encode(Fun.ParseDate(this.CreateTime, "yyyy-MM-dd HH:mm:ss"));
		return u;
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
		dest.writeInt(this.LocGuid);
		dest.writeString(this.Name);
		dest.writeString(this.PicPath);
		dest.writeString(Fun.ParseDate(this.CreateTime));
		dest.writeParcelable(this.Loc, PARCELABLE_WRITE_RETURN_VALUE);
	}
	
	public Bitmap GetSImage(){
		if(this.SBmPic!=null)
			return this.SBmPic;
		this.SBmPic=Fun.GetImage(this.Name,Conf.PICTURETYPES);
		return this.SBmPic;
	}
	
	public void GetHImage(){
		if(this.HBmPic!=null)
			return;
		this.HBmPic=Fun.GetImage(this.Name,Conf.PICTURETYPEH);
	}
	
	public void GetMImage(){
		if(this.MBmPic!=null)
			return;
		this.MBmPic=Fun.GetImage(this.Name,Conf.PICTURETYPEM);
	}
	
	public void GetLImage(){
		if(this.LBmPic!=null)
			return;
		this.LBmPic=Fun.GetImage(this.Name,Conf.PICTURETYPEL);
	}
	
	public Bitmap GetIImage(){
		if(this.IBmPic!=null)
			return this.IBmPic;
		this.IBmPic=Fun.GetImage(this.Name,Conf.PICTURETYPEI);
		return this.IBmPic;
	}
	
	public void RecycleSImage(){
		if(this.SBmPic!=null){
			this.SBmPic.recycle();
			this.SBmPic=null;
		}
	}
	
	public void RecycleHImage(){
		if(this.HBmPic!=null){
			this.HBmPic.recycle();
			this.HBmPic=null;
		}
	}
	
	public void RecycleMImage(){
		if(this.MBmPic!=null){
			this.MBmPic.recycle();
			this.MBmPic=null;
		}
	}
	
	public void RecycleLImage(){
		if(this.LBmPic!=null){
			this.LBmPic.recycle();
			this.LBmPic=null;
		}
	}
	
	public LatLng GetLatLng(){
		return new LatLng(this.Loc.Latitude,this.Loc.Longitude);
	}
	
	public boolean FileExists(int t){
		File f=new File(this.GetTempPath(t));
		return f.exists();
	}
	
	public String GetTempPath(int t){
		String p=null;
		switch(t){
		case Conf.PICTURETYPEH:
			p=Matteo.TempPicDirectory+Conf.PICTURETYPESTRH+this.Name;
			break;
		case Conf.PICTURETYPEM:
			p=Matteo.TempPicDirectory+Conf.PICTURETYPESTRM+this.Name;
			break;
		case Conf.PICTURETYPEL:
			p=Matteo.TempPicDirectory+Conf.PICTURETYPESTRL+this.Name;
			break;
		case Conf.PICTURETYPES:
			p=Matteo.TempPicDirectory+Conf.PICTURETYPESTRS+this.Name;
			break;
		}
		return p;
	}
	
	public Uri GetTempUri(int t){
		String p=this.GetTempPath(t);
		return Uri.fromFile(new File(p));
	}
	
	public static Picture CreateFromParcel(Parcel src){
		Picture pic=new Picture();
		pic.Guid=src.readInt();
		pic.LocGuid=src.readInt();
		pic.Name=src.readString();
		pic.PicPath=src.readString();
		pic.CreateTime=Fun.ParseDate(src.readString());
		pic.Loc=src.readParcelable(MtLocation.class.getClassLoader());
		return pic;
	}
	
	public static final Parcelable.Creator<Picture> CREATOR = new Creator<Picture>() {
		 
        @Override
        public Picture[] newArray(int size) {
            return null;
        }
 
        @Override
        public Picture createFromParcel(Parcel source) {
        	Picture pic = Picture.CreateFromParcel(source); 
            return pic;
        }
    };
}
