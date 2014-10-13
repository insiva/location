package com.matteo.matteolocationhistory.model;

public class Conf {
	public static final String SUCCESS="1";
	public static final String FAIL="0";
	public static final String HostURL="http://192.168.1.104/locationhistory/";
	public static final String HostMobileURL=Conf.HostURL+"mobile/";
	public static final String FileDirURL=Conf.HostURL+"files/";
	public static final String PicDirURL=Conf.FileDirURL+"picture/";
	public static final String HPicDirURL=Conf.PicDirURL+"h/";
	public static final String MPicDirURL=Conf.PicDirURL+"m/";
	public static final String LPicDirURL=Conf.PicDirURL+"l/";
	public static final String SPicDirURL=Conf.PicDirURL+"s/";
	public static final String IPicDirURL=Conf.PicDirURL+"i/";
	public static final String RecvPicURL=Conf.HostMobileURL+"recvpic.php";
	public static final String RecvLocURL=Conf.HostMobileURL+"recvloc.php";
	public static final String PicListURL=Conf.HostMobileURL+"piclist.php";
	public static final String DayListURL=Conf.HostMobileURL+"daylist.php";
	public static final String SiteListURL=Conf.HostMobileURL+"sitelist.php";
	public static final String PicHistoryURL=Conf.HostMobileURL+"pichistory.php";
	public static final String MainURL=Conf.HostMobileURL+"main.php";
	public static final String XML="XML";
	public static final String PICTURE="PICTURE";
	public static final String PICTUREARRAY="PICTUREARRAY";
	public static final String PICTUREINDEX="PICTUREINDEX";
	public static final String CATEGORYTYPE="CATEGORYTYPE";
	public static final int PICTURETLOADED=0x300;
	public static final int PICTURETYPEH=0x301;
	public static final int PICTURETYPEM=0x302;
	public static final int PICTURETYPEL=0x303;
	public static final int PICTURETYPES=0x304;
	public static final int PICTURETYPEI=0x305;
	public static final String PICTURETYPESTRH="H";
	public static final String PICTURETYPESTRM="M";
	public static final String PICTURETYPESTRL="L";
	public static final String PICTURETYPESTRS="S";
	public static final String PICTURETYPESTRI="I";
	public static final boolean NOTLOG=false;
	public static final int UPLOADWHAT=0x200;
	public static final int UPLOADWHATPICTURE=0x201;
	public static final int UPLOADWHATLOCATION=0x202;
	public static final int GETXMLWHAT=0x101;
	public static final String 	DateF="yyyy-MM-dd HH:mm:ss";
	public static final String AddrTypeAll="all";
	public static final int DayType=0;
	public static final int SiteType=1;
	
	public static String V="0";
}

