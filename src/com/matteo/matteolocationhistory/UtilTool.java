package com.matteo.matteolocationhistory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

public class UtilTool {
	public static boolean isGpsEnabled(LocationManager locationManager) {
		boolean isOpenGPS = locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
		boolean isOpenNetwork = locationManager
				.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
		if (isOpenGPS || isOpenNetwork) {
			return true;
		}
		return false;
	}

	/**
	 * 鏍规嵁鍩虹珯淇℃伅鑾峰彇缁忕含搴�
	 * 
	 * 鍘熺悊鍚慼ttp://www.google.com/loc/json鍙戦�乭ttp鐨刾ost璇锋眰锛屾牴鎹甮oogle杩斿洖鐨勭粨鏋滆幏鍙栫粡绾
	 * 害
	 * 
	 * @param cellIds
	 * @return
	 * @throws Exception
	 */
	public static Location callGear(Context ctx, ArrayList<CellInfo> cellIds)
			throws Exception {
		String result = "";
		JSONObject data = null;
		if (cellIds == null || cellIds.size() == 0) {
			UtilTool.alert(ctx, "cell request param null");
			return null;
		}
		;

		try {
			result = UtilTool.getResponseResult(ctx,
					"http://www.google.com/loc/json", cellIds);

			if (result.length() <= 1)
				return null;
			data = new JSONObject(result);
			data = (JSONObject) data.get("location");

			Location loc = new Location(LocationManager.NETWORK_PROVIDER);
			loc.setLatitude((Double) data.get("latitude"));
			loc.setLongitude((Double) data.get("longitude"));
			loc.setAccuracy(Float.parseFloat(data.get("accuracy").toString()));
			loc.setTime(UtilTool.getUTCTime());
			return loc;
		} catch (JSONException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 鎺ユ敹Google杩斿洖鐨勬暟鎹牸寮�
	 * 
	 * 鍑哄弬锛歿"location":{"latitude":26.0673834,"longitude":119.3119936,
	 * "address":
	 * {"country":"盲赂颅氓聸陆","country_code":"CN","region":"莽娄聫氓禄潞莽聹聛","city"
	 * :"莽娄聫氓路聻氓赂聜",
	 * "street":"盲潞聰盲赂聙盲赂颅猫路炉","street_number":"128氓聫路"},"accuracy":935.0},
	 * "access_token":"2:xiU8YrSifFHUAvRJ:aj9k70VJMRWo_9_G"}
	 * 璇锋眰璺緞锛歨ttp://maps.google
	 * .cn/maps/geo?key=abcdefg&q=26.0673834,119.3119936
	 * 
	 * @param cellIds
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 * @throws Exception
	 */
	public static String getResponseResult(Context ctx, String path,
			ArrayList<CellInfo> cellInfos) throws UnsupportedEncodingException,
			MalformedURLException, IOException, ProtocolException, Exception {
		String result = "";
		Log.i(ctx.getApplicationContext().getClass().getSimpleName(),
				"in param: " + getRequestParams(cellInfos));
		InputStream inStream = UtilTool.sendPostRequest(path,
				getRequestParams(cellInfos), "UTF-8");
		if (inStream != null) {
			byte[] datas = UtilTool.readInputStream(inStream);
			if (datas != null && datas.length > 0) {
				result = new String(datas, "UTF-8");
				// Log.i(ctx.getClass().getSimpleName(),
				// "receive result:"+result);//鏈嶅姟鍣ㄨ繑鍥炵殑缁撴灉淇℃伅
				Log.i(ctx.getApplicationContext().getClass().getSimpleName(),
						"google cell receive data result:" + result);
			} else {
				Log.i(ctx.getApplicationContext().getClass().getSimpleName(),
						"google cell receive data null");
			}
		} else {
			Log.i(ctx.getApplicationContext().getClass().getSimpleName(),
					"google cell receive inStream null");
		}
		return result;
	}

	/**
	 * 鎷艰json璇锋眰鍙傛暟锛屾嫾瑁呭熀绔欎俊鎭�
	 * 
	 * 鍏ュ弬锛歿'version': '1.1.0','host':
	 * 'maps.google.com','home_mobile_country_code': 460,
	 * 'home_mobile_network_code': 14136,'radio_type': 'cdma','request_address':
	 * true, 'address_language': 'zh_CN','cell_towers':[{'cell_id':
	 * '12835','location_area_code': 6, 'mobile_country_code':
	 * 460,'mobile_network_code': 14136,'age': 0}]}
	 * 
	 * @param cellInfos
	 * @return
	 */
	public static String getRequestParams(List<CellInfo> cellInfos) {
		StringBuffer sb = new StringBuffer("");
		sb.append("{");
		if (cellInfos != null && cellInfos.size() > 0) {
			sb.append("'version': '1.1.0',"); // google api 鐗堟湰[蹇匽
			sb.append("'host': 'maps.google.com',"); // 鏈嶅姟鍣ㄥ煙鍚峓蹇匽
			sb.append("'home_mobile_country_code': "
					+ cellInfos.get(0).getMobileCountryCode() + ","); // 绉诲姩鐢ㄦ埛鎵�灞炲浗瀹朵唬鍙穂閫�
																		// 涓浗460]
			sb.append("'home_mobile_network_code': "
					+ cellInfos.get(0).getMobileNetworkCode() + ","); // 绉诲姩绯荤粺鍙风爜[榛樿0]
			sb.append("'radio_type': '" + cellInfos.get(0).getRadioType()
					+ "',"); // 淇″彿绫诲瀷[閫� gsm|cdma|wcdma]
			sb.append("'request_address': true,"); // 鏄惁杩斿洖鏁版嵁[蹇匽
			sb.append("'address_language': 'zh_CN',"); // 鍙嶉鏁版嵁璇█[閫� 涓浗
														// zh_CN]
			sb.append("'cell_towers':["); // 绉诲姩鍩虹珯鍙傛暟瀵硅薄[蹇匽
			for (CellInfo cellInfo : cellInfos) {
				sb.append("{");
				sb.append("'cell_id': '" + cellInfo.getCellId() + "',"); // 鍩虹珯ID[蹇匽
				sb.append("'location_area_code': "
						+ cellInfo.getLocationAreaCode() + ","); // 鍦板尯鍖哄煙鐮乕蹇匽
				sb.append("'mobile_country_code': "
						+ cellInfo.getMobileCountryCode() + ",");
				sb.append("'mobile_network_code': "
						+ cellInfo.getMobileNetworkCode() + ",");
				sb.append("'age': 0"); // 浣跨敤濂戒箙鐨勬暟鎹簱[閫� 榛樿0琛ㄧず浣跨敤鏈�鏂扮殑鏁版嵁搴揮
				sb.append("},");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * 鑾峰彇UTC鏃堕棿
	 * 
	 * UTC + 鏃跺尯宸� 锛� 鏈湴鏃堕棿(鍖椾含涓轰笢鍏尯)
	 * 
	 * @return
	 */
	public static long getUTCTime() {
		// 鍙栧緱鏈湴鏃堕棿
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		// 鍙栧緱鏃堕棿鍋忕Щ閲�
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		// 鍙栧緱澶忎护鏃跺樊
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
		// 浠庢湰鍦版椂闂撮噷鎵ｉ櫎杩欎簺宸噺锛屽嵆鍙互鍙栧緱UTC鏃堕棿
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		return cal.getTimeInMillis();
	}

	/**
	 * 鍒濆鍖栵紝璁板緱鏀惧湪onCreate()鏂规硶閲屽垵濮嬪寲锛岃幏鍙栧熀绔欎俊鎭�
	 * 
	 * @return
	 */
	public static ArrayList<CellInfo> init(Context ctx) {
		ArrayList<CellInfo> cellInfos = new ArrayList<CellInfo>();

		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		// 缃戠粶鍒跺紡
		int type = tm.getNetworkType();
		/**
		 * 鑾峰彇SIM鍗＄殑IMSI鐮� SIM鍗″敮涓�鏍囪瘑锛欼MSI 鍥介檯绉诲姩鐢ㄦ埛璇嗗埆鐮侊紙IMSI锛欼nternational
		 * Mobile Subscriber Identification Number锛夋槸鍖哄埆绉诲姩鐢ㄦ埛鐨勬爣蹇楋紝
		 * 鍌ㄥ瓨鍦⊿IM鍗′腑锛屽彲鐢ㄤ簬鍖哄埆绉诲姩鐢ㄦ埛鐨勬湁鏁堜俊鎭
		 * �侷MSI鐢盡CC銆丮NC銆丮SIN缁勬垚锛屽叾涓璏CC涓虹Щ鍔ㄥ浗瀹跺彿鐮侊紝鐢�3浣嶆暟瀛楃粍鎴愶紝
		 * 鍞竴鍦拌瘑鍒Щ鍔ㄥ鎴锋墍灞炵殑鍥藉锛屾垜鍥戒负460锛汳NC涓虹綉缁渋d锛岀敱2浣嶆暟瀛楃粍鎴愶紝
		 * 鐢ㄤ簬璇嗗埆绉诲姩瀹㈡埛鎵�褰掑睘鐨勭Щ鍔ㄧ綉缁滐紝涓
		 * 浗绉诲姩涓�00锛屼腑鍥借仈閫氫负01,涓浗鐢典俊涓�03锛汳SIN涓虹Щ鍔ㄥ鎴疯瘑鍒爜锛岄噰鐢ㄧ瓑闀�11浣嶆暟瀛楁瀯鎴愩��
		 * 鍞竴鍦拌瘑鍒浗鍐匞SM绉诲姩閫氫俊缃戜腑绉诲姩瀹㈡埛銆傛墍浠ヨ鍖哄垎鏄Щ鍔ㄨ繕鏄仈閫氾紝鍙渶鍙栧緱SIM鍗′腑鐨凪NC瀛楁
		 * 鍗冲彲
		 */
		String imsi = tm.getSubscriberId();
		alert(ctx, "imsi: " + imsi);
		// 涓轰簡鍖哄垎绉诲姩銆佽仈閫氳繕鏄數淇★紝鎺ㄨ崘浣跨敤imsi鏉ュ垽鏂�(涓囦笉寰楀繁鐨勬儏鍐典笅鐢╣etNetworkType()鍒ゆ柇锛屾瘮濡俰msi涓虹┖鏃�)
		if (imsi != null && !"".equals(imsi)) {
			alert(ctx, "imsi");
			if (imsi.startsWith("46000") || imsi.startsWith("46002")) {// 鍥犱负绉诲姩缃戠粶缂栧彿46000涓嬬殑IMSI宸茬粡鐢ㄥ畬锛屾墍浠ヨ櫄鎷熶簡涓�涓�46002缂栧彿锛�134/159鍙锋浣跨敤浜嗘缂栧彿
				// 涓浗绉诲姩
				mobile(cellInfos, tm);
			} else if (imsi.startsWith("46001")) {
				// 涓浗鑱旈��
				union(cellInfos, tm);
			} else if (imsi.startsWith("46003")) {
				// 涓浗鐢典俊
				cdma(cellInfos, tm);
			}
		} else {
			alert(ctx, "type");
			// 鍦ㄤ腑鍥斤紝鑱旈�氱殑3G涓篣MTS鎴朒SDPA锛岀數淇＄殑3G涓篍VDO
			// 鍦ㄤ腑鍥斤紝绉诲姩鐨�2G鏄疎GDE锛岃仈閫氱殑2G涓篏PRS锛岀數淇＄殑2G涓篊DMA
			// String OperatorName = tm.getNetworkOperatorName();

			// 涓浗鐢典俊
			if (type == TelephonyManager.NETWORK_TYPE_EVDO_A
					|| type == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| type == TelephonyManager.NETWORK_TYPE_CDMA
					|| type == TelephonyManager.NETWORK_TYPE_1xRTT) {
				cdma(cellInfos, tm);
			}
			// 绉诲姩(EDGE锛�2.75G锛夋槸GPRS锛�2.5G锛夌殑鍗囩骇鐗堬紝閫熷害姣擥PRS瑕佸揩銆傜洰鍓嶇Щ鍔ㄥ熀鏈湪鍥藉唴鍗囩骇鏅強EDGE锛岃仈閫氬垯鍦ㄥぇ鍩庡競閮ㄧ讲EDGE銆�)
			else if (type == TelephonyManager.NETWORK_TYPE_EDGE
					|| type == TelephonyManager.NETWORK_TYPE_GPRS) {
				mobile(cellInfos, tm);
			}
			// 鑱旈��(EDGE锛�2.75G锛夋槸GPRS锛�2.5G锛夌殑鍗囩骇鐗堬紝閫熷害姣擥PRS瑕佸揩銆傜洰鍓嶇Щ鍔ㄥ熀鏈湪鍥藉唴鍗囩骇鏅強EDGE锛岃仈閫氬垯鍦ㄥぇ鍩庡競閮ㄧ讲EDGE銆�)
			else if (type == TelephonyManager.NETWORK_TYPE_GPRS
					|| type == TelephonyManager.NETWORK_TYPE_EDGE
					|| type == TelephonyManager.NETWORK_TYPE_UMTS
					|| type == TelephonyManager.NETWORK_TYPE_HSDPA) {
				union(cellInfos, tm);
			}
		}

		return cellInfos;
	}

	/**
	 * 鐢典俊
	 * 
	 * @param cellInfos
	 * @param tm
	 */
	private static void cdma(ArrayList<CellInfo> cellInfos, TelephonyManager tm) {
		CdmaCellLocation location = (CdmaCellLocation) tm.getCellLocation();
		CellInfo info = new CellInfo();
		info.setCellId(location.getBaseStationId());
		info.setLocationAreaCode(location.getNetworkId());
		info.setMobileNetworkCode(String.valueOf(location.getSystemId()));
		info.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
		info.setRadioType("cdma");
		cellInfos.add(info);

		// 鍓嶉潰鑾峰彇鍒扮殑閮芥槸鍗曚釜鍩虹珯鐨勪俊鎭紝鎺ヤ笅鏉ュ啀鑾峰彇鍛ㄥ洿閭昏繎鍩虹珯淇℃伅浠ヨ緟鍔╅�氳繃鍩虹珯瀹氫綅鐨勭簿鍑嗘��
		// 鑾峰緱閭昏繎鍩虹珯淇℃伅
		List<NeighboringCellInfo> list = tm.getNeighboringCellInfo();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			CellInfo cell = new CellInfo();
			cell.setCellId(list.get(i).getCid());
			cell.setLocationAreaCode(location.getNetworkId());
			cell.setMobileNetworkCode(String.valueOf(location.getSystemId()));
			cell.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
			cell.setRadioType("cdma");
			cellInfos.add(cell);
		}
	}

	/**
	 * 绉诲姩
	 * 
	 * @param cellInfos
	 * @param tm
	 */
	private static void mobile(ArrayList<CellInfo> cellInfos,
			TelephonyManager tm) {
		GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
		CellInfo info = new CellInfo();
		info.setCellId(location.getCid());
		info.setLocationAreaCode(location.getLac());
		info.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
		info.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
		info.setRadioType("gsm");
		cellInfos.add(info);

		// 鍓嶉潰鑾峰彇鍒扮殑閮芥槸鍗曚釜鍩虹珯鐨勪俊鎭紝鎺ヤ笅鏉ュ啀鑾峰彇鍛ㄥ洿閭昏繎鍩虹珯淇℃伅浠ヨ緟鍔╅�氳繃鍩虹珯瀹氫綅鐨勭簿鍑嗘��
		// 鑾峰緱閭昏繎鍩虹珯淇℃伅
		List<NeighboringCellInfo> list = tm.getNeighboringCellInfo();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			CellInfo cell = new CellInfo();
			cell.setCellId(list.get(i).getCid());
			cell.setLocationAreaCode(location.getLac());
			cell.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
			cell.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
			cell.setRadioType("gsm");
			cellInfos.add(cell);
		}
	}

	/**
	 * 鑱旈��
	 * 
	 * @param cellInfos
	 * @param tm
	 */
	private static void union(ArrayList<CellInfo> cellInfos, TelephonyManager tm) {
		GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
		CellInfo info = new CellInfo();
		// 缁忚繃娴嬭瘯锛岃幏鍙栬仈閫氭暟鎹互涓嬩袱琛屽繀椤诲幓鎺夛紝鍚﹀垯浼氬嚭鐜伴敊璇紝閿欒绫诲瀷涓篔SON Parsing Error
		// info.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
		// info.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
		info.setCellId(location.getCid());
		info.setLocationAreaCode(location.getLac());
		info.setMobileNetworkCode("");
		info.setMobileCountryCode("");
		info.setRadioType("gsm");
		cellInfos.add(info);

		// 鍓嶉潰鑾峰彇鍒扮殑閮芥槸鍗曚釜鍩虹珯鐨勪俊鎭紝鎺ヤ笅鏉ュ啀鑾峰彇鍛ㄥ洿閭昏繎鍩虹珯淇℃伅浠ヨ緟鍔╅�氳繃鍩虹珯瀹氫綅鐨勭簿鍑嗘��
		// 鑾峰緱閭昏繎鍩虹珯淇℃伅
		List<NeighboringCellInfo> list = tm.getNeighboringCellInfo();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			CellInfo cell = new CellInfo();
			cell.setCellId(list.get(i).getCid());
			cell.setLocationAreaCode(location.getLac());
			cell.setMobileNetworkCode("");
			cell.setMobileCountryCode("");
			cell.setRadioType("gsm");
			cellInfos.add(cell);
		}
	}

	/**
	 * 鎻愮ず
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void alert(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 鍙戦�乸ost璇锋眰锛岃繑鍥炶緭鍏ユ祦
	 * 
	 * @param path
	 *            璁块棶璺緞
	 * @param params
	 *            json鏁版嵁鏍煎紡
	 * @param encoding
	 *            缂栫爜
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	public static InputStream sendPostRequest(String path, String params,
			String encoding) throws UnsupportedEncodingException,
			MalformedURLException, IOException, ProtocolException {
		byte[] data = params.getBytes(encoding);
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		// application/x-javascript text/xml->xml鏁版嵁
		// application/x-javascript->json瀵硅薄
		// application/x-www-form-urlencoded->琛ㄥ崟鏁版嵁
		conn.setRequestProperty("Content-Type",
				"application/x-javascript; charset=" + encoding);
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));
		conn.setConnectTimeout(5 * 1000);
		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		outStream.close();
		if (conn.getResponseCode() == 200)
			return conn.getInputStream();
		return null;
	}

	/**
	 * 鍙戦�乬et璇锋眰
	 * 
	 * @param path
	 *            璇锋眰璺緞
	 * @return
	 * @throws Exception
	 */
	public static String sendGetRequest(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		InputStream inStream = conn.getInputStream();
		byte[] data = readInputStream(inStream);
		String result = new String(data, "UTF-8");
		return result;
	}

	/**
	 * 浠庤緭鍏ユ祦涓鍙栨暟鎹�
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();// 缃戦〉鐨勪簩杩涘埗鏁版嵁
		outStream.close();
		inStream.close();
		return data;
	}

}