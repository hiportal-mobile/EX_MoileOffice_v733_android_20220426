package com.ex.group.folder.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ex.group.folder.categoryList.AppdataList;
import com.ex.group.folder.retrofitclient.pojo.RequestInitInfo;

import java.util.ArrayList;
import java.util.List;

public class APPINFODB extends SQLiteOpenHelper {
    public final String TAG = "==DBHELPER==";

    public String TAG(String msg) {
        return TAG + "==" + msg + "==";
    }

    public final Context mContext;
    public static final String DBNM = "APPINFOLIST";
    public static final int DBVS = 3;
    public final String TBNM = "APP_INFO";

    public enum VS{
        PACKAGENAME,
        APPID,
        APPNAME,
        APPVER,
        TEXT}


    public final String[] ITEM_COLUMN = {
            (VS.APPID.name()+" "+VS.TEXT.name()+","),
            (VS.APPNAME.name()+" "+VS.TEXT.name()+","),
            (VS.APPVER.name()+" "+VS.TEXT.name())};

    public APPINFODB(Context context) {
        super(context, DBNM, null, DBVS);
        this.mContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //TBNM 테이블 생성

        String appInfo = "CREATE TABLE " + TBNM + " (" + VS.PACKAGENAME.name()+" TEXT PRIMARY KEY, ";
        for (int i = 0; i < ITEM_COLUMN.length; i++) {
            appInfo += ITEM_COLUMN[i];
        }
        appInfo += " ) ";
        db.execSQL(appInfo);
        Log.e(TAG("CREATE TABLE TBNM\n"), appInfo);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void DROPTABLE() throws SQLiteException {
        SQLiteDatabase db;
        db=getWritableDatabase();
        db.beginTransaction();
       db.execSQL("DELETE FROM "+TBNM);
       db.setTransactionSuccessful();
       db.endTransaction();
    }

    public void insert(String TBNM, ContentValues cVal) throws SQLiteException {
        getWritableDatabase().insert(TBNM, null, cVal);
    }
    public void update(String TBNM, ContentValues cVal,String where){
        SQLiteDatabase db;
        db=getWritableDatabase();
        db.update(TBNM,cVal,where,null);
    }

    public Cursor getQuery(String TBNM) throws SQLiteException {
        //순차적으로 데이터를 긁어온다.
        SQLiteDatabase db;
        db=getReadableDatabase();
        return db.query(TBNM, null, null, null, null, null, null);
    }

    public void INSERT_APPITEM(String packageName,
                               String appId,
                               String appName,
                               String appVer) throws SQLiteException {

        //app 아이템을 서버로부터 받아오는 작업

        ContentValues cVal = new ContentValues();
        getWritableDatabase().beginTransaction();
        try{

            cVal.put(VS.PACKAGENAME.name(), packageName);
            cVal.put(VS.APPID.name(), appId);
            cVal.put(VS.APPNAME.name(), appName);
            cVal.put(VS.APPVER.name(),appVer);

            Log.e(TAG("INSERT TABLE " + TBNM + " : "), String.valueOf(cVal));
            getWritableDatabase().insert(TBNM,null,cVal);
            getWritableDatabase().setTransactionSuccessful();
            getWritableDatabase().endTransaction();
        }catch (Exception e){e.printStackTrace();
            getWritableDatabase().endTransaction();}

    }


    //전체 리스트를 가져온다.
    public List<AppdataList> getAppInfoList(){

        List<AppdataList> appInfoList = new ArrayList<>();
        getWritableDatabase().beginTransaction();
       try{

       Cursor cursor =this.getQuery(TBNM);
       if(cursor !=null){
           int size = cursor.getCount();
           Log.e(TAG("size"),String.valueOf(size));
           if(size>0){
               int count =0;
               cursor.moveToFirst();
               while (!cursor.isAfterLast()){
                   count++;
                   Log.e(TAG("\ngetAppdataList"),
                           "==============  "+count+"  ================"
                                   +"\nAppName= "+cursor.getString(VS.APPNAME.ordinal())
                                   +"\nAppID ="+cursor.getString(VS.APPID.ordinal())
                                   +"\nPackageName:"+cursor.getString(VS.PACKAGENAME.ordinal()));
                   AppdataList item = new AppdataList();
                   String appId = cursor.getString(VS.APPID.ordinal());
                   String appName = cursor.getString(VS.APPNAME.ordinal());
                   String packageName = cursor.getString(VS.PACKAGENAME.ordinal());
                   String appVer = cursor.getString(VS.APPVER.ordinal());

                   item.setAppNm(appName);
                   item.setAppId(appId);
                   item.setPackageNm(packageName);
                   item.setAppVer(appVer);
                   appInfoList.add(item);
                   cursor.moveToNext();
               }
           }cursor.close();
            getWritableDatabase().setTransactionSuccessful();
            getWritableDatabase().endTransaction();
           }
       }catch (Exception e){e.printStackTrace();
       getWritableDatabase().endTransaction();}

       return appInfoList;
    }


}