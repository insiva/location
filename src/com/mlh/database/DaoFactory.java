package com.mlh.database;

import com.mlh.model.AlbumDao;
import com.mlh.model.LocationDao;
import com.mlh.model.PictureDao;
import com.mlh.model.RouteDao;

public class DaoFactory {
	private static LocationDao locDao;
	private static PictureDao picDao;
	private static AlbumDao albumDao;
	private static RouteDao routeDao;

	public static LocationDao getLocationDaoInstance() {
		if (DaoFactory.locDao == null) {
			DaoFactory.locDao = new LocationDao();
		}
		return DaoFactory.locDao;
	}

	public static PictureDao getPictureDaoInstance() {
		if (DaoFactory.picDao == null) {
			DaoFactory.picDao = new PictureDao();
		}
		return DaoFactory.picDao;
	}

	public static AlbumDao getAlbumDaoInstance() {
		if (DaoFactory.albumDao == null) {
			DaoFactory.albumDao = new AlbumDao();
		}
		return DaoFactory.albumDao;
	}

	public static RouteDao getRouteDaoInstance() {
		if (DaoFactory.routeDao == null) {
			DaoFactory.routeDao = new RouteDao();
		}
		return DaoFactory.routeDao;
	}
}
