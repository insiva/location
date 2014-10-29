package com.mlh.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.baidu.mapapi.model.LatLng;
import com.mlh.Config;
import com.mlh.communication.HttpConnnection;
import com.mlh.communication.ITask;
import com.mlh.database.DaoFactory;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

/**
 * @author Matteo
 *照片类
 */
public class Picture implements ITask,Parcelable{
	/**
	 * 正方形小图片标志，100x100
	 */
	public static final char SMALL_PICTURE_FLAG='S';
	/**
	 * 原始图片标志
	 */
	public static final char HEIGHT_PICTURE_FLAG='H';
	/**
	 * 中等图片标志，480x800
	 */
	public static final char MEDIUM_PICTURE_FLAG='M';
	/**
	 * 小图片标志，100x160
	 */
	public static final char LOW_PICTURE_FLAG='L';
	/**
	 * 图标标志
	 */
	public static final char ICON_PICTURE_FLAG='I';
	public static final int MIN_PICTURE_SIZE=128;
	public static final String RECEIVE_PICTURE_PAGE=Config.HOST_MOBILE+"recvpic.php";
	public static final String PICTURE_HISTORY_PAGE=Config.HOST_MOBILE+"pichistory.php";
	public static final String PAGE_PICTURE_LIST=Config.HOST_MOBILE+"piclist.php";
	public static String PicureDirectory;
	public static final String HPICTURE_HOST=Config.HOST_PICTURE+"h/";
	public static final String MPICTURE_HOST=Config.HOST_PICTURE+"m/";
	public static final String LPICTURE_HOST=Config.HOST_PICTURE+"l/";
	public static final String SPICTURE_HOST=Config.HOST_PICTURE+"s/";
	public static final String IPICTURE_HOST=Config.HOST_PICTURE+"i/";
	private long Guid;
	private String PictureName;
	private Location MLocation;
	private Date CreateTime;
	private Bitmap SBmPic = null, HBmPic = null, MBmPic = null, LBmPic = null,
			IBmPic = null;
	
	public long getGuid(){
		return this.Guid;
	}
	
	public long getLocationGuid(){
		return this.MLocation.getGuid();
	}
	
	public String getPictureName(){
		return this.PictureName;
	}
	
	public String getPicturePath(){
		return Config.getPictureDirectoryPath()+this.PictureName;
	}
	
	public Uri getUri(){
		File f=new File(this.getPicturePath());
		return Uri.fromFile(f);
	}
	
	public Location getLocation(){
		return this.MLocation;
	}
	public Date getCreateTime(){
		return this.CreateTime;
	}
	public String getCreateTimeStr(){
		return DateFormat.format(Config.DEFAULT_DATEFORMAT, this.CreateTime).toString();
	}
	
	public Bitmap getPictureBitMap(char flag){
		Bitmap bm=null;
		switch(flag){
		case Picture.HEIGHT_PICTURE_FLAG:
			bm=this.HBmPic;
			break;
		case Picture.MEDIUM_PICTURE_FLAG:
			bm=this.MBmPic;
			break;
		case Picture.LOW_PICTURE_FLAG:
			bm=this.LBmPic;
			break;
		case Picture.SMALL_PICTURE_FLAG:
			bm=this.SBmPic;
			break;
		case Picture.ICON_PICTURE_FLAG:
			bm=this.IBmPic;
			break;
		}
		return bm;
	}
	
	public void setGuid(long g){
		this.Guid=g;
	}
	
	public void setPictureName(String n){
		this.PictureName=n;
	}
	
	public void setCreateTime(Date ct){
		this.CreateTime=ct;
	}
	
	@SuppressLint("SimpleDateFormat")
	public void setCreateTime(String s){
		try {
			SimpleDateFormat sdf=new SimpleDateFormat(Config.DEFAULT_DATEFORMAT);
			this.CreateTime=sdf.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setLocation(Location loc){
		this.MLocation=loc;
	}
	
	/**
	 * 直接获得实例的Bitmap属性
	 */
	public Bitmap getImage(char flag){
		Bitmap bm=null;
		String picPath=null;
		switch(flag){
		case Picture.HEIGHT_PICTURE_FLAG:
			bm=this.HBmPic;
			picPath = Config.getTempDirectoryPath() + flag + this.PictureName;
			break;
		case Picture.MEDIUM_PICTURE_FLAG:
			bm=this.MBmPic;
			picPath = Config.getTempDirectoryPath() + flag + this.PictureName;
			break;
		case Picture.LOW_PICTURE_FLAG:
			bm=this.LBmPic;
			picPath = Config.getTempDirectoryPath() + flag + this.PictureName;
			break;
		case Picture.SMALL_PICTURE_FLAG:
			bm=this.SBmPic;
			picPath = Config.getTempDirectoryPath() + flag + this.PictureName;
			break;
		case Picture.ICON_PICTURE_FLAG:
			bm=this.IBmPic;
			String n=this.PictureName.replace("jpg", "png").replace("jpeg", "png");
			picPath = Config.getTempDirectoryPath() + flag + n;
			break;
		}
		if(bm!=null){
			return bm;
		}
		if (Picture.picureExists(picPath)) {
			bm = BitmapFactory.decodeFile(picPath);
			return bm;
		}
		return null;
	}
	
	/**
	 * 获取图片的Bitmap，如果本地不存在，则通过网络获取，并储存至本地
	 */
	public Bitmap fetchImage(char picFlag){
		Bitmap bm = null;
		String picUrl = null;
		String picPath = null;
		switch (picFlag) {
		case Picture.HEIGHT_PICTURE_FLAG:
			picUrl = Picture.HPICTURE_HOST + this.PictureName;
			picPath = Config.getTempDirectoryPath() + picFlag + this.PictureName;
			break;
		case Picture.MEDIUM_PICTURE_FLAG:
			picUrl = Picture.MPICTURE_HOST + this.PictureName;
			picPath = Config.getTempDirectoryPath() + picFlag + this.PictureName;
			break;
		case Picture.LOW_PICTURE_FLAG:
			picUrl = Picture.LPICTURE_HOST + this.PictureName;
			picPath = Config.getTempDirectoryPath() + picFlag + this.PictureName;
			break;
		case Picture.SMALL_PICTURE_FLAG:
			picUrl = Picture.SPICTURE_HOST + this.PictureName;
			picPath = Config.getTempDirectoryPath() + picFlag + this.PictureName;
			break;
		case Picture.ICON_PICTURE_FLAG:
			String n=this.PictureName.replace("jpg", "png").replace("jpeg", "png");
			picUrl = Picture.IPICTURE_HOST + n;
			picPath = Config.getTempDirectoryPath() + picFlag + n;
			break;
		}
		if (Picture.picureExists(picPath)) {
			bm = BitmapFactory.decodeFile(picPath);
			return bm;
		}
		bm=HttpConnnection.getImage(picUrl);
		File f = new File(picPath);
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
	
	/**
	 * 获取照片缓存的本地路径
	 */
	public String getTempPath(char picFlag){
		String p=null;
		switch(picFlag){
		case Picture.HEIGHT_PICTURE_FLAG:
			p = Config.getTempDirectoryPath() + picFlag + this.PictureName;
			break;
		case Picture.MEDIUM_PICTURE_FLAG:
			p = Config.getTempDirectoryPath() + picFlag + this.PictureName;
			break;
		case Picture.LOW_PICTURE_FLAG:
			p = Config.getTempDirectoryPath() + picFlag + this.PictureName;
			break;
		case Picture.SMALL_PICTURE_FLAG:
			p = Config.getTempDirectoryPath() + picFlag + this.PictureName;
			break;
		case Picture.ICON_PICTURE_FLAG:
			String n=this.PictureName.replace("jpg", "png").replace("jpeg", "png");
			p = Config.getTempDirectoryPath() + picFlag + n;
			break;
		}
		return p;
	}
	
	/**
	 * 确定本地是否存在此照片
	 */
	public boolean rawPicureExists(){
		String fp=this.getPicturePath();
		return Picture.picureExists(fp);
	}
	
	/**
	 * 确定某一路径的照片是否存在
	 */
	@SuppressWarnings("resource")
	public static boolean picureExists(String picPath){
		File f = new File(picPath);
		if(!f.exists())return false;
		FileInputStream fis=null; 
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} 
		int fileSize;
		try {
			fileSize = fis.available();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		if(fileSize<Picture.MIN_PICTURE_SIZE){
			f.delete();
			return false;
		}
		return true;
	}
	
	public void recycleImage(char flag){
		Bitmap bm=null;
		switch(flag){
		case Picture.HEIGHT_PICTURE_FLAG:
			bm=this.HBmPic;
			break;
		case Picture.MEDIUM_PICTURE_FLAG:
			bm=this.MBmPic;
			break;
		case Picture.LOW_PICTURE_FLAG:
			bm=this.LBmPic;
			break;
		case Picture.SMALL_PICTURE_FLAG:
			bm=this.SBmPic;
			break;
		case Picture.ICON_PICTURE_FLAG:
			bm=this.IBmPic;
			break;
		}
		if(bm!=null){
			bm.recycle();
		}
		bm=null;
	}
	
	/**
	 * 获取照片的GPS坐标
	 */
	public LatLng getLatLng(){
		return this.MLocation.getLatLng();
	}

	/**
	 * 上传照片，继承自ITask
	 * */
	@Override
	public void upload() {
		// TODO Auto-generated method stub
		DaoFactory.getPictureDaoInstance().upload(this);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeLong(this.Guid);
		dest.writeString(this.PictureName);
		dest.writeString(this.getCreateTimeStr());
		dest.writeParcelable(this.MLocation, PARCELABLE_WRITE_RETURN_VALUE);
	}
	
	public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
		public Picture createFromParcel(Parcel in) {
			Picture pic=new Picture();
			pic.setGuid(in.readLong());
			pic.setPictureName(in.readString());
			pic.setCreateTime(in.readString());
			pic.setLocation((Location) in.readParcelable(Location.class.getClassLoader()));
			return pic;
		}

		public Picture[] newArray(int size) {
			return new Picture[size];
		}
	};
}
