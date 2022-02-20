package com.ex.group.folder.utility;

import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class dateCalculater {

    public Calendar calendar = new GregorianCalendar(Locale.KOREA);

    // 현재날짜 구하기
    public Calendar doCurrentDate() {

        // 현재 날짜 구하기

        return calendar;
    }

    public int GapofDate(String meal, String mon){
        int retnum = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            Date mondat =format.parse(mon);
            Date mealdat =format.parse(meal);

            long diff = mealdat.getTime()-mondat.getTime();
            long diffDays = diff/(24*60*60*1000);

            return (int)diffDays;
        } catch (ParseException e) {
            e.printStackTrace();

            return retnum;
        }



    }


    //현재 요일 구하기
    public int getDayOfWeek() {

        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            return 6;
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == 7) {
            return 6;
        } else {
            return calendar.get(Calendar.DAY_OF_WEEK);
        }
    }

    //월요일 캘린더 구하기
    public String getMonday() {

        Calendar monCal = new GregorianCalendar(Locale.KOREA);
        monCal.add(Calendar.DAY_OF_YEAR, (-getDayOfWeek() + 2));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String getMondayDate = dateFormat.format(monCal.getTime()).toString();
        dateFormat = null;
        return getMondayDate;


    }

    //금요일 캘린더 구하기
    public String getFriday() {
        Calendar friCal = new GregorianCalendar(Locale.KOREA);
        friCal.add(Calendar.DAY_OF_YEAR, (-getDayOfWeek() + 2 + 4));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String getFridayDate = dateFormat.format(friCal.getTime()).toString();
        dateFormat = null;
        return getFridayDate;
    }


    //시간 차 구하기 포맷을 받아 DATE로 변환 시키기


    public static boolean isNew(String dateFomat){

       //dateFomat으로 작성 된 String 값을 변환 하는 작업이다.
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date day =simpleDateFormat.parse(dateFomat);
            long daytime = day.getTime();

            Calendar rightnow = Calendar.getInstance();
            Date now = rightnow.getTime();
            long nowtime = now.getTime();

            long gap = nowtime-daytime;

            gap = gap/60000;    //밀리 세컨을 분으로 변환 함

            if(gap<=300){
                return true;
            }
            else{
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return  false;
        }

    }


}
