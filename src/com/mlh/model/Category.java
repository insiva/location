package com.mlh.model;

import org.xml.sax.Attributes;

import android.os.Parcel;
import android.os.Parcelable;

import com.mlh.Config;

/**
 * @author Matteo
 *照片类别类，照片有日期和地点两个类别
 */
public class Category implements Parcelable {
	public static final String CATEGORY="CATEGORY";
	public static final String CATEGORY_VALUE="CATEGORYVALUE";
	public static final String CATEGORY_TYPE="CATEGORYTYPE";
	/**
	 * 照片的日期类别
	 */
	public static final int DAY_TYPE=0;
	/**
	 * 照片的地点类别
	 */
	public static final int SITE_TYPE=1;
	public static final String PAGE_DAY=Config.HOST_MOBILE+"daylist.php";
	public static final String PAGE_SITE=Config.HOST_MOBILE+"sitelist.php";
	private String CategoryValue,Province,City,District;
	private int Count;
	private int Type;

	public Category(){
		
	}
	
	public Category(String desc,int c){
		this.CategoryValue=desc;
		this.Count=c;
	}
	/**
	 * 通过XML中的Attributes进行初始化
	 * @param atts
	 */
	public Category(Attributes atts){
		this.CategoryValue=atts.getValue("value");
		this.Count=Integer.parseInt(atts.getValue("count"));
		this.Type=Integer.parseInt(atts.getValue("type"));
		if(this.Type==Category.SITE_TYPE){
			this.Province=atts.getValue("province");
			this.City=atts.getValue("city");
			this.District=atts.getValue("district");
		}
	}
	
	public void setCategoryValue(String desc){
		this.CategoryValue=desc;
	}
	
	public String getCategoryValue(){
		return this.CategoryValue;
	}
	
	public void setProvince(String prov){
		this.Province=prov;
	}
	
	public void setCity(String city){
		this.City=city;
	}
	
	public void setDistrict(String dist){
		this.District=dist;
	}
	
	public int getType(){
		return this.Type;
	}
	
	/**
	 * 获取类别的描述
	 * @return
	 */
	public String getDescription(){
		if(this.Type==Category.DAY_TYPE)
		{
			return this.CategoryValue+"，"+Count+"张照片";
		}
		if(this.Province.equals(this.City))
			return this.City+this.District+"，"+Count+"张照片";
		return this.Province+this.City+this.District+"，"+Count+"张照片";
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(this.CategoryValue);
		dest.writeString(this.Province);
		dest.writeString(this.City);
		dest.writeString(this.District);
		dest.writeInt(this.Count);
		dest.writeInt(this.Type);
	};

	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
		public Category createFromParcel(Parcel in) {
			Category cat=new Category();
			cat.CategoryValue=in.readString();
			cat.Province=in.readString();
			cat.City=in.readString();
			cat.District=in.readString();
			cat.Count=in.readInt();
			cat.Type=in.readInt();
			return cat;
		}

		public Category[] newArray(int size) {
			return new Category[size];
		}
	};
}
