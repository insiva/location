package com.mlh.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.mlh.communication.ITask;
import com.mlh.communication.UploadTask;
import com.mlh.database.DaoFactory;

public class Album extends ArrayList<Picture> implements ITask, Parcelable {

	private static final long serialVersionUID = 1L;
	public static final String ALBUM="ALBUM";
	public static final String ALBUM_INDEX="ALBUM_INDEX";

	public static void uploadAll(){
		
	}
	
	@Override
	public boolean add(Picture pic){
		return super.add(pic);
	}
	
	@Override
	public void upload() {
		// TODO Auto-generated method stub
		DaoFactory.getAlbumDaoInstance().upload(this);
	}
	

	public static void sendUnUploadedToUploadTask() {
		// TODO Auto-generated method stub
		
		Album album=DaoFactory.getAlbumDaoInstance().getUnUploadedPicture();
		UploadTask.addToUploadingQueue(album);
	}
	
	public void readXml(String xmlStr){
		DaoFactory.getAlbumDaoInstance().readXml(this, xmlStr);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		//dest.write
		for(int i=0;i<this.size();i++){
			dest.writeParcelable(this.get(i), PARCELABLE_WRITE_RETURN_VALUE);
		}
	}
	
	public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
		public Album createFromParcel(Parcel in) {
			Album album=new Album();
			Picture pic=in.readParcelable(Picture.class.getClassLoader());
			while(pic!=null){
				album.add(pic);
				pic=in.readParcelable(Picture.class.getClassLoader());
			}
			return album;
		}

		public Album[] newArray(int size) {
			return new Album[size];
		}
	};
}
