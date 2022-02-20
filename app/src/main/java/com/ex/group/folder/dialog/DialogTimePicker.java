package com.ex.group.folder.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ex.group.folder.R;
import com.ex.group.folder.utility.CTimePicker;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;

public class DialogTimePicker extends Dialog {
    private final String TAG = "==DialogTimePicker==";

    private final String TAG(String tag) {
        return TAG + "(" + tag + ")";
    }

    Time startTime;
    Time finishTime;


    private Context mContext;

    public View.OnClickListener confirmlistener;
    public View.OnClickListener cancellistener;

    public CTimePicker timepicker;
    protected TextView tv_start;
    protected TextView tv_end;
    protected View underline_start;
    protected View underline_end;
    protected TextView btn_close;
    protected TextView btn_confirm;

    public int SH;
    public int SM;
    public int EH;
    public int EM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.timepicker_dialog);
        timepicker = findViewById(R.id.time_picker);
        timepicker.setIs24HourView(false);
        timepicker.setEnabled(true);

        tv_start = findViewById(R.id.tv_start);
        tv_end = findViewById(R.id.tv_end);
        underline_start = findViewById(R.id.underline_start);
        underline_end = findViewById(R.id.underline_end);
        btn_close = findViewById(R.id.btn_close);
        btn_confirm = findViewById(R.id.btn_confirm);

        setTimePickerState(getSharedString("TIMEPICKER_STATE"));
        StateClick();
        timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Vibrator vib = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
                vib.vibrate(15);

                    if (getSharedString("TIMEPICKER_STATE").equals("START")) {

                        SH = timepicker.getCurrentHour();
                        SM = timepicker.getCurrentMinute();

                        setSharedString("START_HOUR",String.valueOf(SH));
                        setSharedString("START_MINUTE",String.valueOf(SM));



                    } else {
                        EH = timepicker.getCurrentHour();
                        EM = timepicker.getCurrentMinute();

                        setSharedString("END_HOUR",String.valueOf(EH));
                        setSharedString("END_MINUTE",String.valueOf(EM));
                    }


            }
        });




    }


    public String getSharedString(String name) {
        SharedPreferences user = mContext.getSharedPreferences("USERINFO", MODE_PRIVATE);
        if (!user.contains(name)) {
            setSharedString(name, "");
        }
        final String value = user.getString(name, "");
        Log.e(TAG("GET_SHARED" + name), value);
        return value;
    }

    public void setSharedString(String name, String value) {
        SharedPreferences user = mContext.getSharedPreferences("USERINFO", MODE_PRIVATE);
        SharedPreferences.Editor edit = user.edit();
        edit.putString(name, value);
        Log.e(TAG("SET_SHARED" + name), value);
        edit.commit();

    }

    public void setTimePickerState(String state) {
        SimpleDateFormat currenthh = new SimpleDateFormat("kk");
        String getCurrTime = currenthh.format(new Date());
        Log.e(TAG("currenthh"), getCurrTime);
        int currentHOUR = Integer.parseInt(getCurrTime) % 24;
        int after2HOUR = (currentHOUR + 2) % 24;
        SimpleDateFormat currentmm = new SimpleDateFormat("mm");
        String getCurrTime2 = currentmm.format(new Date());
        int currentMINUTE = Integer.parseInt(getCurrTime2) % 60;


        switch (state) {

            case "START":

                tv_start.setTextColor(Color.parseColor("#052850"));
                underline_start.setBackgroundColor(Color.parseColor("#5f5f5f"));
                tv_end.setTextColor(Color.parseColor("#e0e0e0"));
                underline_end.setBackgroundColor(Color.parseColor("#ffffff"));


                if (getSharedString("START_HOUR").equals("")) {
                    timepicker.setCurrentHour(currentHOUR);
                    timepicker.setCurrentMinute(currentMINUTE);
                } else {
                    int hour = Integer.parseInt(getSharedString("START_HOUR"));
                    int minute = Integer.parseInt(getSharedString("START_MINUTE"));

                    timepicker.setCurrentHour(hour);
                    timepicker.setCurrentMinute(minute);

                }

                SH = timepicker.getCurrentHour();
                SM = timepicker.getCurrentMinute();
                EH= Integer.parseInt(getSharedString("END_HOUR"));
                EM =Integer.parseInt(getSharedString("END_MINUTE"));

                break;

            case "END":

                tv_end.setTextColor(Color.parseColor("#052850"));
                underline_end.setBackgroundColor(Color.parseColor("#5f5f5f"));
                tv_start.setTextColor(Color.parseColor("#e0e0e0"));
                underline_start.setBackgroundColor(Color.parseColor("#ffffff"));

                if (getSharedString("START_HOUR").equals("")) {

                    timepicker.setCurrentHour(after2HOUR);
                    timepicker.setCurrentMinute(currentMINUTE);
                } else {
                    int hour = Integer.parseInt(getSharedString("END_HOUR"));
                    int minute = Integer.parseInt(getSharedString("END_MINUTE"));

                    timepicker.setCurrentHour(hour);
                    timepicker.setCurrentMinute(minute);

                }

                SH = Integer.parseInt(getSharedString("START_HOUR"));
                SM = Integer.parseInt(getSharedString("START_MINUTE"));
                EH = timepicker.getCurrentHour();
                EM = timepicker.getCurrentMinute();

                break;

        }


    }

    public void changelistener(String state) {
        switch (state) {

            case "START":

                tv_start.setTextColor(Color.parseColor("#052850"));
                underline_start.setBackgroundColor(Color.parseColor("#5f5f5f"));
                tv_end.setTextColor(Color.parseColor("#e0e0e0"));
                underline_end.setBackgroundColor(Color.parseColor("#ffffff"));

                timepicker.setCurrentHour(SH);
                timepicker.setCurrentMinute(SM);
                break;
            case "END":

                tv_end.setTextColor(Color.parseColor("#052850"));
                underline_end.setBackgroundColor(Color.parseColor("#5f5f5f"));
                tv_start.setTextColor(Color.parseColor("#e0e0e0"));
                underline_start.setBackgroundColor(Color.parseColor("#ffffff"));

                timepicker.setCurrentHour(EH);
                timepicker.setCurrentMinute(EM);
                break;

        }

    }

    public void StateClick() {
        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSharedString("TIMEPICKER_STATE", "START");
                changelistener("START");

            }
        });

        tv_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSharedString("TIMEPICKER_STATE", "END");
                changelistener("END");
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_confirm.setOnClickListener(confirmlistener);
    }


    public DialogTimePicker(Context context, View.OnClickListener confirmlistener) {
        super(context, R.style.Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.mContext = context;
        this.confirmlistener = confirmlistener;
    }


}
