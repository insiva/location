package com.mlh.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.utils.DistanceUtil;
import com.mlh.Config;
import com.mlh.communication.ITask;
import com.mlh.communication.UploadTask;
import com.mlh.database.DaoFactory;


public class Route extends ArrayList<Trajectory> implements ITask,Parcelable {
	public static final String ROUTE="ROUTE";
	public static final String RECEIVE_ROUTE_PAGE=Config.HOST_MOBILE+"recvloc.php";
	public static final String PAGE_ROUTE_HISTORY=Config.HOST_MOBILE+"trajhistory.php";
	private static final long serialVersionUID = 1L;
	private double MaxDistance;
	private Trajectory FirstTraj,LastTraj;
	public Route(){
		this.MaxDistance=0;
		this.FirstTraj=null;
		this.LastTraj=null;
	}
	
	public boolean add(Location loc){
		double distance=0,length=0;long timespan=0,dura=0;
		Trajectory traj=new Trajectory(loc);
		if(this.size()>0){
			Trajectory ltraj=this.get(0);
			distance=DistanceUtil.getDistance(ltraj.getLatLng(), traj.getLatLng());
			this.MaxDistance=this.MaxDistance>distance?this.MaxDistance:distance;
			dura=traj.getCreateTime().getTime()-ltraj.getCreateTime().getTime();
			ltraj=this.get(this.size()-1);
			length=DistanceUtil.getDistance(ltraj.getLatLng(), traj.getLatLng());
			timespan=traj.getCreateTime().getTime()-ltraj.getCreateTime().getTime();
		}else if(this.size()==0){
			this.FirstTraj=new Trajectory(traj);
		}
		int distanceI=(int)Math.ceil(distance);
		String desc=traj.getCreateTimeStr()+"\n"+Route.getDistanceText(distanceI);
		traj.setDescription(desc);
		traj.setLength(length);
		traj.setDuration(dura);
		traj.setTimeSpan(timespan);
		traj.setDistance(distance);
		this.LastTraj=traj;
		return super.add(traj);
	}
	
	@Override
	public boolean add(Trajectory traj){
		return super.add(traj);
	}
	
	@Override
	public void clear(){
		this.MaxDistance=0;
		this.FirstTraj=null;
		this.LastTraj=null;
		super.clear();
	}
	
	public long getDuration(){
		return this.LastTraj.getCreateTime().getTime()-this.FirstTraj.getCreateTime().getTime();
	}
	
	public static String getDistanceText(int d) {
		if (d < 1000)
			return d + "m";
		int k=d/1000;
		int h=(d%1000)/100;
		String s=k+"."+h+"km";
		return s;
	}

	public long getStartTime(){
		return this.FirstTraj.getCreateTime().getTime();
	}

	public long getEndTime(){
		return this.LastTraj.getCreateTime().getTime();
	}
	
	@Override
	public void upload() {
		// TODO Auto-generated method stub
		DaoFactory.getRouteDaoInstance().upload(this);
	}
	
	public static void sendUnUploadedToUploadTask() {
		// TODO Auto-generated method stub
		Route route=DaoFactory.getRouteDaoInstance().getUnUploadedLocation();
		UploadTask.addToUploadingQueue(route);
	}
	
	public double getMaxDistance(){
		return this.MaxDistance;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeDouble(this.MaxDistance);
		dest.writeParcelable(this.FirstTraj, PARCELABLE_WRITE_RETURN_VALUE);
		dest.writeParcelable(this.LastTraj, PARCELABLE_WRITE_RETURN_VALUE);
		for(int i=0;i<this.size();i++){
			dest.writeParcelable(this.get(i), PARCELABLE_WRITE_RETURN_VALUE);
		}
	}
	
	public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
		public Route createFromParcel(Parcel in) {
			Route route=new Route();
			route.MaxDistance=in.readDouble();
			route.FirstTraj=in.readParcelable(Trajectory.class.getClassLoader());
			route.LastTraj=in.readParcelable(Trajectory.class.getClassLoader());
			Trajectory traj=in.readParcelable(Trajectory.class.getClassLoader());
			while(traj!=null){
				route.add(traj);
				traj=in.readParcelable(Trajectory.class.getClassLoader());
			}
			return route;
		}

		public Route[] newArray(int size) {
			return new Route[size];
		}
	};
}
