package com.ex.group.folder.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ex.group.folder.R;

import org.json.JSONArray;
import org.json.JSONException;

import static com.ex.group.folder.utility.ClientUtil.APPNAME;
import static com.ex.group.folder.utility.ClientUtil.EX_STORE_PACKAGE;
import static com.ex.group.folder.utility.ClientUtil.SGN_PACKAGE;
import static com.ex.group.folder.utility.ClientUtil.SSM_APP;
import static com.ex.group.folder.utility.ClientUtil.SSM_EXECUTE_CODE;
import static com.ex.group.folder.utility.ClientUtil.SSM_INSTALLER_PACKAGE;
import static com.ex.group.folder.utility.ClientUtil.SSM_PACKAGE;
import static com.ex.group.folder.utility.ClientUtil.V3_APK;
import static com.ex.group.folder.utility.ClientUtil.V3_APP;
import static com.ex.group.folder.utility.ClientUtil.V3_PACKAGE;


public class CommonUtil {


	public static final String TAG = "CommonUtil";

	public static String[] permissions = {
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_SMS,
			Manifest.permission.WRITE_CONTACTS/*,
														Manifest.permission.REQUEST_INSTALL_PACKAGES*/
	};


	ArrayList<AppInfoPackage> essentialList;
	ArrayList<AppInfoPackage> uninstalledList;
	SharedPreferences installShared;
	int total = 0;
	int cnt = 0;

	AppInfoPackage secuwayApp;
	AppInfoPackage installerApp;
	AppInfoPackage ssmApp;
	AppInfoPackage v3App;


	public static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;

	public void setEssentialApp() {
		essentialList = new ArrayList<AppInfoPackage>();

		secuwayApp = new AppInfoPackage();
		installerApp = new AppInfoPackage();
		ssmApp = new AppInfoPackage();
		secuwayApp = new AppInfoPackage();

		secuwayApp = new AppInfoPackage();
		secuwayApp.setApkName(ClientUtil.SGN_APK);
		secuwayApp.setAppName(ClientUtil.SGN_APP);
		secuwayApp.setPackageName(SGN_PACKAGE);

		installerApp = new AppInfoPackage();
		installerApp.setApkName(ClientUtil.SSM_INSTALLER_APK);
		installerApp.setAppName(ClientUtil.SSM_INSTALLER_APP);
		installerApp.setPackageName(SSM_INSTALLER_PACKAGE);

		ssmApp = new AppInfoPackage();
		ssmApp.setApkName("");
		ssmApp.setAppName(SSM_APP);
		ssmApp.setPackageName(SSM_PACKAGE);

		v3App = new AppInfoPackage();
		v3App.setApkName(V3_APK);
		v3App.setAppName(V3_APP);
		v3App.setPackageName(V3_PACKAGE);

		essentialList.add(secuwayApp);
		essentialList.add(installerApp);
		//   essentialList.add(ssmApp);
		essentialList.add(v3App);

	}
	public static String[]EssentialAppList={EX_STORE_PACKAGE,SSM_INSTALLER_PACKAGE,SSM_PACKAGE,V3_PACKAGE,SGN_PACKAGE};
	public static boolean isExistApp(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		try {
			pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
		} catch (Exception e) {
			return false;
		}
		return true;
	}



	public ArrayList<AppInfoPackage> checkEssential(Context context) {
		int cnt = 0;
		setEssentialApp();

		uninstalledList = new ArrayList<AppInfoPackage>();

		for (int i = 0; i < essentialList.size(); i++) {
			if (!isExistApp(context, essentialList.get(i).getPackageName()))
			{
				if ((SSM_INSTALLER_PACKAGE).equals(essentialList.get(i).getPackageName()))
				{ // SSM installer X
					if (!isExistApp(context, SSM_PACKAGE))
					{
						// SSM installer X, SSM ?????? ?????? X
						uninstalledList.add(essentialList.get(i));      //SSM installer ??????
					}
				}
				else
				{
					uninstalledList.add(essentialList.get(i));  //???????????? ?????? ??? ?????? ??????
				}
			}
			else
			{
				//SSM installer ??????, SSM ??????X
				if ((SSM_INSTALLER_PACKAGE).equals(essentialList.get(i)) && !isExistApp(context, SSM_PACKAGE))
				{
					uninstalledList.add(ssmApp);
				}
			}
		}

		for (int i = 0; i < uninstalledList.size(); i++) {
			Log.d(TAG, "????????? ??? " + i + "... " + uninstalledList.get(i).getPackageName());
		}

		return uninstalledList;
	}










	public boolean hasAllPermissionGranted(int[] grantResults) {
		boolean check = false;
		for (int result : grantResults) {
			//   Log.i(TAG, "result..."+result);
			if (result == PackageManager.PERMISSION_DENIED) {
				return false;
			}
		}
		return true;
	}


	public static String nullCheckParams(String mdn, String mac, String imei) {


		String errorMessage = "";

		if (!(mdn != null && mdn.length() > 0)) {
			errorMessage = "????????????";
		}

		if (!(mac != null && mdn.length() > 0)) {
			if (errorMessage.length() > 0) {
				errorMessage = errorMessage + ", Mac Address";
			} else {
				errorMessage = errorMessage + "Mac Address";
			}

		}

		if (!(imei != null && mdn.length() > 0)) {
			if (errorMessage.length() > 0) {
				errorMessage = errorMessage + ", IMEI";
			} else {
				errorMessage = errorMessage + "IMEI";
			}
		}

		if (errorMessage.length() > 0) {
			if (mac == null) {
				errorMessage = errorMessage + " ?????? ????????????. Wi-Fi??? ??? ??? ?????? ?????? ??? ?????????.";
			} else {
				errorMessage = errorMessage + " ?????? ????????????. ????????? ?????????.";
			}


			return errorMessage;
		}

		return "";
	}


	/**
	 * ??????????????? 010-xxxx-xxxx ???????????? ????????????. ( "-" ?????? ?????? )
	 * @return String 010-xxxx-xxxx (??????)
	 */
	public static String checkPhoneNumber(String phoneNumber) {

		String first = "";
		String second = "";
		String third = "";

		if (phoneNumber != null) {

			if (!phoneNumber.contains("-")) {

				if (phoneNumber.length() == 10) {
					first = phoneNumber.substring(0, 3);
					second = phoneNumber.substring(3, 6);
					third = phoneNumber.substring(6, phoneNumber.length());
				} else if (phoneNumber.length() == 11) {
					first = phoneNumber.substring(0, 3);
					second = phoneNumber.substring(3, 7);
					third = phoneNumber.substring(7, phoneNumber.length());
				} else {
					return "";
				}

				return first + "-" + second + "-" + third;
			} else {
				return phoneNumber;
			}
		}

		return "";
	}

	/**
	 * ?????? ?????? ??????
	 * @param
	 * @return
	 * @permission <uses-permission android:name="android.permission.READ_PHONE_STATE" />
	 */
	public static String getPhoneNumber(Context context) {
		TelephonyManager tManager = (TelephonyManager) context.getSystemService
				(Context.TELEPHONY_SERVICE);
//		return tManager != null ? tManager.getLine1Number() : "";
		String phoneNo = "";
		if(tManager != null) {
			phoneNo = tManager.getLine1Number();
			if(phoneNo.startsWith("+82")){
				phoneNo = phoneNo.replace("+82", "0");
			}
		}

		return phoneNo;
	}

	/**
	 * mdn??? ?????????
	 * @param context context
	 * @return mdn
	 */
	public static String getMdn(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

		}
		String mdn = tm.getLine1Number();
		if(mdn != null) {
			if(mdn.indexOf("+82") == 0) {
				mdn = mdn.replaceFirst("\\+82", "0");
			}
		} else {
			mdn = "";
		}
		Log.v("VARIABLE_FIND---->","2MDN : "+mdn);
		return mdn;
	}
	
	/**
	 * Mac Address??? ?????????.
	 * @param context
	 * @return
	 */
	public static String getMacAddr(Context context) {
		 try {
		        List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
		        for (NetworkInterface nif : all) {
		            if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

		            byte[] macBytes = nif.getHardwareAddress();
		            if (macBytes == null) {
						Log.v("VARIABLE_FIND---->","MACADRESS : NULL");
		                return "";
		            }

		            StringBuilder res1 = new StringBuilder();
                    Log.v("VARIABLE_FIND---->","MACADRESS : "+macBytes.toString());
		            for (byte b : macBytes) {
		                res1.append(Integer.toHexString(b & 0xFF) + ":");
		            }

		            if (res1.length() > 0) {
		                res1.deleteCharAt(res1.length() - 1);
		            }
					Log.v("VARIABLE_FIND---->","MACADRESS : "+res1.toString());
		            return res1.toString();
		        }
		    } catch (Exception ex) {
		        //handle exception
		    }
		    return "";
	}

	/**
	 * imsi??? ?????????.
	 * @param context context
	 * @return imsi
	 */
	public static String getImsi(Context context) {
		TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		return mTelephonyMgr.getSubscriberId(); 
	}

	/**
	 * imei??? ?????????
	 * @param context context
	 * @return imei
	 */
	public static String getImei(Context context) {
		String result = "";
		TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			Log.v("VARIABLE_FIND---->","IMEINUMBER : "+mTelephonyMgr.getDeviceId());
			result  = mTelephonyMgr.getDeviceId();
		} catch (Exception e) {
			result = "";
		}

//		return mTelephonyMgr.getDeviceId();
		return result;
	}
	
	/**
	 * 3G/LTE ????????? ????????? ?????????
	 * @param context context
	 * @return ????????? ??????
	 */
	public static NetworkInfo getMobileNetwork3G(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	}

	/***
	 * ???????????? ?????? ?????? ??????
	 * WI-Fi : 1
	 * MOBILE DATA : 0
	 */
	public static int getMobileData(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		
		int resValue = -1;
		if(activeNetwork != null){
			if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
				resValue = ConnectivityManager.TYPE_WIFI;	//1
			}
			if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
				resValue = ConnectivityManager.TYPE_MOBILE;	//0
			}
		}
		////LTE off ???
		
		return resValue;
		
	}
	
//	/**
//	 * TODO (jbyou)
//	 * 4G ????????? ????????? ?????????
//	 * @param context context
//	 * @return ????????? ??????
//	 */
//	public static NetworkInfo getMobileNetwork4G(Context context) {
//		Log.i(TAG, "=== getMobileNetwork4G ===");
//		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		return cm.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
//	}
	
	/**
	 * WIFI ????????? ????????? ?????????
	 * @param context context
	 * @return ????????? ??????
	 */
	public static NetworkInfo getWifiNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	}

	/**
	 * 3G ????????? ????????? ?????????
	 * @param context context
	 * @return ????????? ??????
	 */
	public static NetworkInfo getMobileNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	}

	/**
	 * ?????????????????? ?????????
	 * @param context context
	 * @return ???????????????
	 */
	public static String getAppId(Context context) {
		String appId = ""; 
		try { 
			PackageManager pm = context.getPackageManager();
			PackageInfo i = pm.getPackageInfo(context.getPackageName(), 0);
			appId = i.applicationInfo.loadDescription(pm) + "";
		} catch(NameNotFoundException e) { }
		return appId;
	}

	/* ??? ?????? ???????????? */
	public static String getVersionName(Context context, String packageName){
		PackageInfo pInfo = null;
		String versionName = "";
		if(isExistApp(context, packageName)){
			try {
				pInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
			}catch (Exception e){
				e.printStackTrace();
			}
			versionName = pInfo.versionName;
		}else{
		}
		return versionName;
	}

	/**
	 * ????????? ?????????
	 * @param context context
	 * @return ??????
	 */
	public static String getVersion(Context context) {
		String version = "";
		try { 
			PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			version = i.versionName + ""; 
		} catch(NameNotFoundException e) { }
		return version;
	}

	/**
	 * ??????????????? ?????????
	 * @param context context
	 * @return ????????????
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 1;
		try { 
			PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionCode = i.versionCode; 
		} catch(NameNotFoundException e) { }
		return versionCode;		
	}

	/**
	 * ???????????? ?????????
	 * @param context context
	 * @return ?????????
	 */
	public static String getAppName(Context context) {
		String appName = ""; 
		try { 
			PackageManager pm = context.getPackageManager();
			PackageInfo i = pm.getPackageInfo(context.getPackageName(), 0); 
			appName = i.applicationInfo.loadLabel(pm) + "";
		} catch(NameNotFoundException e) { }
		return appName;
	}

	/**
	 * ??????????????? ?????????
	 * @param context context
	 * @return ????????????
	 */
	public static String getLang(Context context) {
		return context.getResources().getConfiguration().locale.getLanguage();
	}

	/**
	 * ??????????????? ????????????
	 * @param context context
	 * @param appId ???????????????
	 * @return ????????????
	 */
	public static String isInstallPackage(Context context, String appId){
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> packages = manager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(PackageInfo pack : packages) {
			ApplicationInfo app = pack.applicationInfo;
			String b_appId = app.loadDescription(manager) + "";

			if(b_appId.equals(appId)) {
//				SKTUtil.log(android.util.Log.DEBUG, "packageInfo", pack.packageName + "(" + app.loadLabel(manager) + "/" + b_appId + "/" + pack.versionName + ")");
				return pack.versionName+"";
			}
		}

		return null;
	}

	/**
	 * ??????????????? ????????????
	 * @param context context
	 * @param pkgName ????????????
	 * @return ????????????
	 */
	public static String isInstallPackage_2(Context context, String pkgName){
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> packages = manager.getInstalledPackages(0);
		for(PackageInfo pack : packages) {
			ApplicationInfo app = pack.applicationInfo;
			String b_pkgName = app.packageName + "";

			if(b_pkgName.equals(pkgName)) {
//				SKTUtil.log(android.util.Log.DEBUG, "packageInfo", pack.packageName + "(" + app.loadLabel(manager) + "/" + app.loadDescription(manager) + "/" + pack.versionName + ")");
				return pack.versionName+"";
			}
		}

		return null;
	}

	/**
	 * ???????????? ?????????
	 * @param a_oView view
	 * @return ?????????????????????
	 */
	public static boolean hideKeyboard(View a_oView) {
		InputMethodManager imm = (InputMethodManager) a_oView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(a_oView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void showKeyboard(View a_oView) {
		InputMethodManager imm = (InputMethodManager) a_oView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInputFromWindow(a_oView.getApplicationWindowToken(),  InputMethodManager.SHOW_FORCED, 0); 
	}

	/**
	 * ???????????? ?????????
	 * @param activity activity
	 */
	public static void hideKeyboard(Activity activity) {
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	/********** [EJY] ?????? ******************************************************************************************/

	/**
	 * String null ??????
	 * @param object
	 * @return
	 */
	public static String checkNull(String object) {
		if (isNotNullCheck(object)) {
			return object.trim();
		} else {
			return "";
		}
	}
	public static boolean isNotNullCheck(String object) {
		return ((object != null) && !object.isEmpty());
	}

	public static String getDeviceId(Context context) {
		return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
	}

	public static String getDeviceModel() {
		return Build.MODEL;
	}

	public static String getDeviceOs() {
		return Build.VERSION.RELEASE.toString();
	}


	/**
	 * ????????? ??????
	 * @param Context
	 * @return boolean
	 */
	public static boolean getIsPhone(Context context) {
		//?????? ????????? ?????? ?????????
		int screenSizeType = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

		if(screenSizeType== Configuration.SCREENLAYOUT_SIZE_NORMAL || screenSizeType==Configuration.SCREENLAYOUT_SIZE_SMALL){
			return true;
		}
		return false;
	}

	public static int fontSizeForTablet = 25;

	/**
	 * ??????????????? ??????
	 * @param Context
	 * @return boolean
	 */
	public static boolean getIsTablet(Context context) {
		//?????? ????????? ?????? ?????????
		int screenSizeType = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

		if(screenSizeType==Configuration.SCREENLAYOUT_SIZE_XLARGE || screenSizeType==Configuration.SCREENLAYOUT_SIZE_LARGE) {
			return true;
		}
		return false;
	}

	/**
	 * ?????? ?????? ?????????
	 * @param Context
	 * @return int
	 */
	public static int getScreenSize(Context context) {
		Display dispaly = ((Activity)context).getWindowManager().getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		dispaly.getMetrics(displayMetrics);

		double WidthToInchs = displayMetrics.widthPixels / (double) displayMetrics.densityDpi;
		double HeightToInchs = displayMetrics.heightPixels / (double) displayMetrics.densityDpi;
		double screenSize = Math.sqrt(Math.pow(WidthToInchs, 2) + Math.pow(HeightToInchs, 2));
		Log.d(TAG, "isTablet ?  -->" + screenSize);
		return (int) Math.round(screenSize);
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		return width;
	}
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		return  height;
	}

	/**
	 * SharedPreferences
	 */
	private static SharedPreferences mPref ;//UTIL???
	/** SharedPreference ??????
	 * @param context
	 * @param  key
	 * @param value
	 */

	public static void setPrefString(Context context, String key, String value) {
		if(mPref == null) {
			mPref = PreferenceManager.getDefaultSharedPreferences(context);
		}
		SharedPreferences.Editor editor = mPref.edit();



		editor.putString(key, value);


		editor.commit();
	}

	/** SharedPreference ??? ????????????
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getPrefString(Context context,String key) {
		if(mPref == null) {
			mPref = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return mPref.getString(key, "");
	}

	/** SharedPreference ??????
	 * @param context
	 * @param  key
	 * @param value
	 */
	public static void setPrefInt(Context context, String key, int value) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/** SharedPreference ??? ????????????
	 * @param context
	 * @param key
	 * @return
	 */
	public static int getPrefInt(Context context,String key) {
		if(mPref == null) {
			mPref = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return mPref.getInt(key, 0);
	}

	/** SharedPreference ??????
	 * @param context
	 * @param  key
	 * @param value
	 */
	public static void setPrefBoolean(Context context, String key, Boolean value) {
		if(mPref == null) {
			mPref = PreferenceManager.getDefaultSharedPreferences(context);
		}
		//Log.d("","User Id Check : common3 =  "+value);
		SharedPreferences.Editor editor = mPref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/** SharedPreference ??? ????????????
	 * @param context
	 * @param key
	 * @return
	 */
	public static Boolean getPrefBoolean(Context context,String key) {
		if(mPref == null) {
			mPref = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return mPref.getBoolean(key, true);
	}

	/**
	 * SharedPreference ????????? ??????
	 * @param context
	 * @param key
	 * @param values
	 */
	private void setStringArrayPref(Context context, String key, ArrayList<String> values) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		JSONArray a = new JSONArray();
		for (int i = 0; i < values.size(); i++) {
			a.put(values.get(i));
		}
		if (!values.isEmpty()) {
			editor.putString(key, a.toString());
		} else {
			editor.putString(key, null);
		}
		editor.apply();
	}

	/**
	 * SharedPreference ??? ????????????
	 * @param context
	 * @param key
	 * @return
	 */
	private ArrayList<String> getStringArrayPref(Context context, String key) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String json = prefs.getString(key, null);
		ArrayList<String> urls = new ArrayList<String>();
		if (json != null) {
			try {
				JSONArray a = new JSONArray(json);
				for (int i = 0; i < a.length(); i++) {
					String url = a.optString(i);
					urls.add(url);
				}
			} catch (JSONException e) {
				System.out.println("[Exception] CommonUtil >> getStringArrayPref() ");
			}
		}
		return urls;
	}

	//***************************************************************************************************************************************************************//

	//2021-05-11 [EJY] App Data ??????
	public static void clearApplicationData(Context context) {
		Log.d(TAG, "[EJY] clearApplicationData()");
		File cache = context.getCacheDir();  //?????? ?????? ??????
		File appDir = new File(cache.getParent());  //App Data ????????? ?????? ?????? ????????? ?????????????????? ??????
		if(appDir.exists()) {
			String[] children = appDir.list();
			for(String s : children) {
//				if (!s.equals("lib") && !(s.equals("shared_prefs"))) {
					deleteDir(new File(appDir, s));
				Log.d(TAG, "[EJY] clearApplicationData() - File /data/data/"+context.getPackageName()+"/" + s + " DELETED");
//				}
			}
		}
	}

	public static boolean deleteDir(File dir) {
		if(dir != null && dir.isDirectory()) {
			String[] children = dir.list();

			//?????? ???????????? ??????????????? ??????
			for(int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if(!success) {
					return false;
				}
			}
		}
		//??????????????? ??????????????? ??????????????? ?????? ??????
		return dir.delete();
	}

	/*public static void clearSharedPref(Context context) {
		Log.d(TAG, "[EJY] clearSharedPref()");
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = mPref.edit();
		editor.clear();
		editor.commit();
	}*/

}
