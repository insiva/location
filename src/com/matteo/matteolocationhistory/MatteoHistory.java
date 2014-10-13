package com.matteo.matteolocationhistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import android.annotation.SuppressLint;
import android.database.Cursor;

@SuppressLint("SimpleDateFormat")
public class MatteoHistory extends ArrayList<MatteoLocation> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8689874654414193410L;
	private static final double MapRatio = 1.3;
	public Date StartDay, EndDay;
	public static final long DefaultSpan = 3 * 24 * 3600 * 1000;
	public static SimpleDateFormat DayFormatter = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static SimpleDateFormat DayFormatter2 = new SimpleDateFormat(
			"yyyy-M-d");
	public static SimpleDateFormat HourFormatter = new SimpleDateFormat(
			"yyyy-M-d,Hµã");
	public static SimpleDateFormat MinuteFormatter = new SimpleDateFormat(
			"yyyy-M-d H:m");
	public static SimpleDateFormat TimeFormatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public MatteoLocation FirstLocation, LastLocation;
	public double North, South, East, West;
	public LatLng CenterLatlng;
	public double MaxDistance;
	public long TimeSpan;
	public static final long DaySpan = 24 * 3600 * 1000;
	public static final long HourSpan = 3600 * 1000;
	public static final long MinuteSpan = 60 * 1000;

	public MatteoHistory() {
		this.EndDay = new Date();
		this.EndDay.setTime(this.EndDay.getTime() + 24 * 3600 * 1000);
		this.StartDay = new Date();
		this.StartDay
				.setTime(this.EndDay.getTime() - MatteoHistory.DefaultSpan);
		this.ReadHistory();
	}

	public MatteoHistory(Date SDay, Date EDay) {
		this.SetDaySpan(SDay, EDay);
	}

	public void SetDaySpan(Date SDay, Date EDay) {
		this.StartDay = SDay;
		this.EndDay = EDay;
		this.EndDay.setTime(this.EndDay.getTime() + 24 * 3600 * 1000);
		this.ReadHistory();
	}

	public void ReadHistory() {
		this.clear();
		this.TimeSpan = 0;
		this.FirstLocation = null;
		this.LastLocation = null;
		this.MaxDistance = 0;
		this.North = this.South = Matteo.BeijingLatitude;
		this.East = this.West = Matteo.BeijingLongitude;
		// this.CenterLatlng=new
		// LatLng(Matteo.BeijingLatitude,Matteo.BeijingLongitude);
		double latsum = 0, lonsum = 0, lat = 0, lon = 0;
		String sql = "select * from location_history where datetime(createtime)>datetime('"
				+ MatteoHistory.DayFormatter.format(StartDay)
				+ "') and datetime(createtime)<datetime('"
				+ MatteoHistory.DayFormatter.format(EndDay) + "')";
		Cursor cur = Matteo.MtDb.Query(sql);
		int gdi = -1, cti = -1, lati = -1, loni = -1, acri = -1, alti = -1;
		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
			if (this.FirstLocation == null) {
				gdi = cur.getColumnIndex(Matteo.ThisApplication
						.getString(R.string.ColumnGuid));
				cti = cur.getColumnIndex(Matteo.ThisApplication
						.getString(R.string.ColumnCreateTime));
				lati = cur.getColumnIndex(Matteo.ThisApplication
						.getString(R.string.ColumnLatitude));
				loni = cur.getColumnIndex(Matteo.ThisApplication
						.getString(R.string.ColumnLongitude));
				acri = cur.getColumnIndex(Matteo.ThisApplication
						.getString(R.string.ColumnAccuracy));
				alti = cur.getColumnIndex(Matteo.ThisApplication
						.getString(R.string.ColumnAltitude));
			}
			int gd = cur.getInt(gdi);
			String ct = cur.getString(cti);
			lat = cur.getDouble(lati);
			lon = cur.getDouble(loni);
			double acr = cur.getDouble(acri);
			double alt = cur.getDouble(alti);
			MatteoLocation mloc = new MatteoLocation(gd, lat, lon, acr, alt, ct);
			if (this.FirstLocation == null) {
				this.FirstLocation = mloc;
				this.FirstLocation.Distance = 0;
				this.North = this.South = lat;
				this.East = this.West = lon;
			} else {
				mloc.Distance = DistanceUtil.getDistance(mloc.Coordiante,
						this.FirstLocation.Coordiante);
				this.North = (this.North < lat ? lat : this.North);
				this.South = (this.South > lat ? lat : this.South);
				this.East = (this.East < lon ? lon : this.East);
				this.West = (this.West > lon ? lon : this.West);
			}
			this.add(mloc);
			latsum += lat;
			lonsum += lon;
			this.LastLocation = mloc;
			this.MaxDistance = this.MaxDistance < mloc.Distance ? mloc.Distance
					: this.MaxDistance;
		}
		if (latsum == 0) {
			lat = Matteo.BeijingLatitude;
		} else {
			lat = latsum / this.size();
		}
		if (lonsum == 0) {
			lon = Matteo.BeijingLongitude;
		} else {
			lon = lonsum / this.size();
		}
		if (this.LastLocation != null) {
			this.TimeSpan = this.LastLocation.CreateTime.getTime()
					- this.FirstLocation.CreateTime.getTime();
		}
		this.CenterLatlng = new LatLng(lat, lon);
		cur.close();
	}

	public float GetZoom() {
		double width = (this.East - this.West) * MapRatio;
		double height = (this.North - this.South) * MapRatio;
		if (width == 0 || height == 0)
			return 14f;
		double sw = (double) Matteo.ScreenHeight * MatteoMap.LatPerPixel19;
		double sh = (double) Matteo.ScreenWidth * MatteoMap.LonPerPixel19;
		double wzoom = 19 - Matteo.Log2(width / sw);
		double hzoom = 19 - Matteo.Log2(height / sh);
		double zoom = hzoom > wzoom ? wzoom : hzoom;
		zoom = zoom > 19 ? 19 : zoom;
		return (float) zoom;
	}

	public Date GetEndDay() {
		Date d = new Date();
		d.setTime(this.EndDay.getTime() - 24 * 3600 * 1000);
		return d;
	}

	public double GetDistanceUnit() {
		double md = this.GetMaxDistance();
		return md / HistoryPathView.DistanceUnitCount;
	}

	public double GetMaxDistance() {
		double ds = Matteo.GetNumberScale(this.MaxDistance);
		double r = this.MaxDistance / ds;
		int m = Matteo.GetUpperEven(r);
		return m * ds;
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

	public long GetTimeUnit() {
		double r = (double) this.TimeSpan
				/ (double) HistoryPathView.TimeUnitCount;
		long s = MinuteSpan;
		if (r > DaySpan) {
			s = DaySpan;
		} else if (r > HourSpan) {
			s = HourSpan;
		} else if (r > MinuteSpan) {
			s = MinuteSpan;
		} else {
			return MinuteSpan;
		}
		double rr = Math.ceil(r / (double) s);
		return (long) (rr * s);
	}

	public static String GetTimeScaleText(long t, long u) {
		Date d = new Date();
		d.setTime(t);
		if (u >= DaySpan) {
			return DayFormatter.format(d);
		} else if (u >= HourSpan) {
			return HourFormatter.format(d);
		} else {
			return MinuteFormatter.format(d);
		}
	}
}
