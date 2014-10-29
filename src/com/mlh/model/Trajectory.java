package com.mlh.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Matteo
 *�켣�࣬�̳���Location�����Location����һЩ��켣��ص���Ϣ
 */
public class Trajectory extends Location implements Parcelable{
	//private Location MLocation;
	/**
	 * �켣�����������硰2014-10-28 15:20:20 1.7km��
	 */
	private String Description;
	/**
	 * ���������ʼ��ľ���
	 */
	private double Distance;
	/**
	 * ����һ��λ�õľ���
	 */
	private double Length;
	/**
	 * �ڶ�̬չʾ�켣�У�ÿ0.1���ƶ���γ��
	 */
	private double LatPace;
	/**
	 * �ڶ�̬չʾ�켣�У�ÿ0.1���ƶ��ľ���
	 */
	private double LngPace;
	/**
	 * ����һ��λ�õ�ʱ����
	 */
	private long TimeSpan;
	/**
	 * �����λ�õ�ʱ���
	 */
	private long Duration;
	/**
	 *�ڶ�̬չʾ�켣�У�����һ��λ��֮���չʾ��Ҫ�Ĵ���
	 */
	private int PaceNum;
	
	public Trajectory(Location loc){
		super(loc);
	}
	
	public String getDescription(){
		return this.Description;
	}
	
	public double getDistance(){
		return this.Distance;
	}
	
	public double getLength(){
		return this.Length;
	}
	
	public double getLatPace(){
		return this.LatPace;
	}
	
	public double getLngPace(){
		return this.LngPace;
	}
	
	public long getTimeSpan(){
		return this.TimeSpan;
	}
	
	public long getDuration(){
		return this.Duration;
	}
	
	public int getPaceNum(){
		return this.PaceNum;
	}
	
	public void setDescription(String desc){
		 this.Description=desc;
	}
	
	public void setDistance(double distance){
		 this.Distance=distance;
	}
	
	public void setLength(double len){
		 this.Length=len;
	}
	
	public void setLatPace(double latpace){
		 this.LatPace=latpace;
	}
	
	public void setLngPace(double lngpace){
		 this.LngPace=lngpace;
	}
	
	public void setTimeSpan(long ts){
		 this.TimeSpan=ts;
	}
	
	public void setDuration(long dura){
		 this.Duration=dura;
	}
	
	public void setPaceNum(int pn){
		this.PaceNum=pn;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		//super.writeToParcel(dest, flags);
		super.writeToParcel(dest, flags);
		dest.writeString(this.Description);
		dest.writeDouble(this.Distance);
		dest.writeDouble(this.Length);
		dest.writeLong(this.TimeSpan);
		dest.writeLong(this.Duration);
	}
	
	public static final Parcelable.Creator<Trajectory> CREATOR = new Parcelable.Creator<Trajectory>() {
		public Trajectory createFromParcel(Parcel in) {
			Location loc=Location.CREATOR.createFromParcel(in);
			Trajectory traj=new Trajectory(loc);
			traj.setDescription(in.readString());
			traj.setDistance(in.readDouble());
			traj.setLength(in.readDouble());
			traj.setTimeSpan(in.readLong());
			traj.setDuration(in.readLong());
			return traj;
		}

		public Trajectory[] newArray(int size) {
			return new Trajectory[size];
		}
	};
}
