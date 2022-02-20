package com.ex.group.folder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.ex.group.folder.dialog.DialogTimePicker;
import com.ex.group.folder.utility.BaseActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlarmSettingActivity extends BaseActivity {

    private String TAG = AlarmSettingActivity.class.getSimpleName();

    public Switch switch_businessMsg;
    public Switch switch_holiday;
    public Switch switch_elec;
    public Switch switch_memo;


    public Switch swtich_receive;
    public TextView select_menu_group;
    public ImageView button_prev;
    final String GROUP_MAIN="본사";
    final String GROUP_LAB="도로교통연구원";
    final String GROUP_NOTHING="미설정";


    public TextView ban_title;
    public Switch switch_ban;
    public TextView text_start,textStartTime;
    public TextView text_end,textEndTime;
    public LinearLayout setStartTime;
    public LinearLayout setEndTime;

    public TextView text_vibration;
    public TextView text_ring;
    public TextView text_both;
    public RadioButton pattern_vibration;
    public RadioButton pattern_ring;
    public RadioButton pattern_both;

    private final String BOTH ="BOTH";
    private final String RING ="RING";
    private final String VIBE ="VIBRATION";

    public DialogTimePicker DTP;

    private final String ELECAPP = "elecapp";
    private final String MEMOAPP = "memo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alaram_setting);

        Log.d(TAG, "onCreate() - TOPIC_ELECAPP : "+getState("TOPIC_ELECAPP"));
        Log.d(TAG, "onCreate() - TOPIC_MEMOAPP : "+getState("TOPIC_MEMOAPP"));



        setAlarmPattern();
        setInitUI();
        setPatternRadioButton(getSharedString("ALARM_PATTERN"));

        setTime(getSharedString("START_HOUR"),getSharedString("START_MINUTE"),textStartTime,"S");
        setTime(getSharedString("END_HOUR"),getSharedString("END_MINUTE"),textEndTime,"E");
        //setting Alaram Activity was Created by PJS
    }

    public void setInitUI(){
        button_prev=findViewById(R.id.button_prev);
        switch_businessMsg=findViewById(R.id.switch_businessMsg);
        switch_holiday=findViewById(R.id.switch_holiday);
        switch_ban=findViewById(R.id.switch_ban);
        select_menu_group=findViewById(R.id.select_menu_group);

        switch_elec=findViewById(R.id.switch_elec);
        switch_memo=findViewById(R.id.switch_memo);

        ban_title=findViewById(R.id.ban_title);

        text_start=findViewById(R.id.text_start);
        textStartTime=findViewById(R.id.textStartTime);

        text_end =findViewById(R.id.text_end);
        textEndTime =findViewById(R.id.textEndTime);
        setStartTime =findViewById(R.id.setStartTime);
        setEndTime = findViewById(R.id.setEndTime);


        text_vibration = findViewById(R.id.text_vibration);
        text_ring = findViewById(R.id.text_ring);
        text_both = findViewById(R.id.text_both);
        pattern_vibration =findViewById(R.id.pattern_vibration);
        pattern_ring =findViewById(R.id.pattern_ring);
        pattern_both = findViewById(R.id.pattern_both);



        button_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

/*▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄*/


/*▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄*/

        // Shared 값에 따라 설정값을 바꿔준다.
        //2021.06 TEST
        System.out.println("----------------TOPIC_ELECAPP " + getState("TOPIC_ELECAPP"));

        //메모보고 (메모보고 설정, 미설정)
        if(getState("TOPIC_MEMOAPP")){
            switch_memo.setChecked(true);
            FirebaseMessaging.getInstance().subscribeToTopic(MEMOAPP);
        }else{
            switch_memo.setChecked(false);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(MEMOAPP);
        }

        //전자결재 (전자결재 설정, 미설정)
        if(getState("TOPIC_ELECAPP")){
            switch_elec.setChecked(true);
            FirebaseMessaging.getInstance().subscribeToTopic(ELECAPP);
        }else{switch_elec.setChecked(false);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(ELECAPP);}

        //경조사(경조사 설정, 미설정)
        if(getState("TOPIC_HOLIDAY")){
            switch_holiday.setChecked(true);
            FirebaseMessaging.getInstance().subscribeToTopic("holiday");
        }else{switch_holiday.setChecked(false);
            FirebaseMessaging.getInstance().unsubscribeFromTopic("holiday");}


        //경영메세지(경영메세지 설정, 미설정)
        if(getState("TOPIC_BUSINESS")){
            switch_businessMsg.setChecked(true);
            FirebaseMessaging.getInstance().subscribeToTopic("businessMessage");
        }else{
            switch_businessMsg.setChecked(false);
            FirebaseMessaging.getInstance().unsubscribeFromTopic("businessMessage");
        }

        //메뉴(본사 설정, 연구 설정, 미설정)
        if(getState("TOPIC_MENU")){
            select_menu_group.setText(GROUP_MAIN);
            select_menu_group.setBackground(getResources().getDrawable(R.drawable.menu_slide_easylogin_setting_c));
        }
        else if(getState("TOPIC_MENURESEARCH")){
            select_menu_group.setText(GROUP_LAB);
            select_menu_group.setBackground(getResources().getDrawable(R.drawable.menu_slide_easylogin_setting_c));
        }
        else if(getState("TOPIC_MENUNOTHING")){
            select_menu_group.setText(GROUP_NOTHING);
            select_menu_group.setBackground(getResources().getDrawable(R.drawable.menu_slide_easylogin_setting));
        }


        //알림 방해금지 시간대 설정(설정 미설정)
        if(getState("MESSAGE_BANTIME")) {
            switch_ban.setChecked(true);
            setBanColor(true);
        }else{
            switch_ban.setChecked(false);
            setBanColor(false);
        }




 /*▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄*/

        //CallBack 부분

        switch_memo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vibrate(15);
                clickMemoApp();
                if(getState("TOPIC_MEMOAPP")){
                    switch_memo.setChecked(true);
                }else{switch_memo.setChecked(false);}
            }
        });

        switch_elec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vibrate(15);
                clickElecApp();
                if(getState("TOPIC_ELECAPP")){
                    switch_elec.setChecked(true);
                }else{switch_elec.setChecked(false);}
            }
        });


        switch_holiday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vibrate(15);
                clickHoliday();
                if(getState("TOPIC_HOLIDAY")){
                    switch_holiday.setChecked(true);
                }else{switch_holiday.setChecked(false);}
            }
        });

        switch_businessMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vibrate(15);
                clickBusiness();
                if(getState("TOPIC_BUSINESS")){
                    switch_businessMsg.setChecked(true);
                }else{
                    switch_businessMsg.setChecked(false);
                }
            }
        });

        select_menu_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> ListItems = new ArrayList<>();
                ListItems.add(GROUP_NOTHING);
                ListItems.add(GROUP_MAIN);
                ListItems.add(GROUP_LAB);
                final CharSequence[] items =ListItems.toArray(new String[ListItems.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmSettingActivity.this);
                builder.setTitle(getString(R.string.menu_Select_group));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            //shared 값 설정
                            setSharedString("TOPIC_MENU","N");
                            setSharedString("TOPIC_MENURESEARCH","N");
                            setSharedString("TOPIC_MENUNOTHING","Y");

                            //UI 설정
                            select_menu_group.setText(items[which]);
                            select_menu_group.setBackground(getResources().getDrawable(R.drawable.menu_slide_easylogin_setting));

                            //푸시 수신설정
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("menu");
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("menuResearch");
                        }
                        else if(which==1){

                            setSharedString("TOPIC_MENU","Y");
                            setSharedString("TOPIC_MENURESEARCH","N");
                            setSharedString("TOPIC_MENUNOTHING","N");

                            select_menu_group.setText(items[which]);
                            select_menu_group.setBackground(getResources().getDrawable(R.drawable.menu_slide_easylogin_setting_c));

                            FirebaseMessaging.getInstance().subscribeToTopic("menu");
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("menuResearch");

                        }
                        else if(which==2){

                            setSharedString("TOPIC_MENU","N");
                            setSharedString("TOPIC_MENURESEARCH","Y");
                            setSharedString("TOPIC_MENUNOTHING","N");

                            select_menu_group.setText(items[which]);
                            select_menu_group.setBackground(getResources().getDrawable(R.drawable.menu_slide_easylogin_setting_c));

                            FirebaseMessaging.getInstance().unsubscribeFromTopic("menu");
                            FirebaseMessaging.getInstance().subscribeToTopic("menuResearch");
                        }


                    }
                });builder.show();
            }
        });




        switch_ban.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vibrate(15);
                setBanColor(isChecked);
                if(isChecked){
                    setSharedString("MESSAGE_BANTIME","Y");
                }else{
                    setSharedString("MESSAGE_BANTIME","N");
                }


            }
        });


        // FLOW TIMEPICKER #1 STATE▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄

        setStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSharedString("TIMEPICKER_STATE","START");
                DTP = new DialogTimePicker(AlarmSettingActivity.this,new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            vibrate(21);
                        //시간 값을 저장해주는 클릭 리스너 입니다. 시간 값 저장  이후 텍스트 값또한 저장한다.


                        setTime(getSharedString("START_HOUR"),getSharedString("START_MINUTE"),textStartTime,"S");
                        setTime(getSharedString("END_HOUR"),getSharedString("END_MINUTE"),textEndTime,"E");
                        DTP.dismiss();

                    }
                });
                DTP.setCanceledOnTouchOutside(false);
                DTP.show();


            }
        });

        setEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSharedString("TIMEPICKER_STATE","END");
                DTP = new DialogTimePicker(AlarmSettingActivity.this,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                vibrate(21);


                                setTime(getSharedString("START_HOUR"),getSharedString("START_MINUTE"),textStartTime,"S");
                                setTime(getSharedString("END_HOUR"),getSharedString("END_MINUTE"),textEndTime,"E");
                                DTP.dismiss();
                            }
                        });
                DTP.setCanceledOnTouchOutside(false);
                DTP.show();


            }
        });


       /*▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄

       라디오 버튼들에 대한 클릭메세지 설정
        */

       pattern_both.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               vibrate(15);
               setPatternRadioButton(BOTH);
           }
       });
       pattern_vibration.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               vibrate(15);
               setPatternRadioButton(VIBE);
           }
       });
       pattern_ring.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               vibrate(15);
               setPatternRadioButton(RING);
           }
       });

    }



/*▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄*/

/*
    * click()메소드의 역할
    * 버튼을 클릭하거나 스위치를 돌리거나 스피너를 선택 할 때 에  따라서 즉각적으로
    * FIREBASE 메세지 Topic의 subscribe/unsubscribe를 적용해준다.
    *
    * */

    public void clickElecApp(){
        if(getSharedString("TOPIC_ELECAPP").equals("Y")){
            FirebaseMessaging.getInstance().unsubscribeFromTopic(ELECAPP);
            setSharedString("TOPIC_ELECAPP","N");
        }else{
            FirebaseMessaging.getInstance().subscribeToTopic(ELECAPP);
            setSharedString("TOPIC_ELECAPP","Y");
        }
    }

    public void clickMemoApp(){
        if(getSharedString("TOPIC_MEMOAPP").equals("Y")){
            FirebaseMessaging.getInstance().unsubscribeFromTopic(MEMOAPP);
            setSharedString("TOPIC_MEMOAPP","N");
        }else{
            FirebaseMessaging.getInstance().subscribeToTopic(MEMOAPP);
            setSharedString("TOPIC_MEMOAPP","Y");
        }
    }

    public void clickBusiness(){
        if(getSharedString("TOPIC_BUSINESS").equals("Y")){
            FirebaseMessaging.getInstance().unsubscribeFromTopic("businessMessage");
            setSharedString("TOPIC_BUSINESS","N");

        }else {
            FirebaseMessaging.getInstance().subscribeToTopic("businessMessage");
            setSharedString("TOPIC_BUSINESS", "Y");
        }
    }
    public void clickHoliday(){
        if(getSharedString("TOPIC_HOLIDAY").equals("Y")){
            FirebaseMessaging.getInstance().unsubscribeFromTopic("holiday");
            setSharedString("TOPIC_HOLIDAY","N");
        }else{
            FirebaseMessaging.getInstance().subscribeToTopic("holiday");
            setSharedString("TOPIC_HOLIDAY","Y");
        }
    }


//▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄

//알림 방식을 설정하는 방법에 대한 메소드 진동, 소리 , 진동 +소리  세가지에 대한 라디오 버튼의 조건 설정
// shared 의 값에 따라 설정을 달리 해주면 된다. 초기 설정 값은 뭘로 하지?
// 초기값은 진동으로 설정한다.

    public void setAlarmPattern(){
        if(getSharedString("ALARM_PATTERN").equals("")){//알림방식이 설정돼 있지 않다면 알림 방식을 두개다로 바꿔준다.
           setSharedString("ALARM_PATTERN",BOTH);

        }else{
        }





    }

//알림 패턴에 따라 라디오 버튼의 색깔을 정해 주는 곳 이다.
    public void setPatternRadioButton(String  alarmPattern){
        switch (alarmPattern){

            case (VIBE):

                pattern_ring.setChecked(false);
                pattern_vibration.setChecked(true);
                pattern_both.setChecked(false);
                text_vibration.setTextColor(getResources().getColor(R.color.login_id_edit_text));
                text_both.setTextColor(Color.parseColor("#c0c0c0"));
                text_ring.setTextColor(Color.parseColor("#c0c0c0"));

                setSharedString("ALARM_PATTERN",VIBE);


                break;
            case (RING):

                pattern_ring.setChecked(true);
                pattern_vibration.setChecked(false);
                pattern_both.setChecked(false);
                text_ring.setTextColor(getResources().getColor(R.color.login_id_edit_text));
                text_both.setTextColor(Color.parseColor("#c0c0c0"));
                text_vibration.setTextColor(Color.parseColor("#c0c0c0"));

                setSharedString("ALARM_PATTERN",RING);



                break;
            case (BOTH):

                pattern_ring.setChecked(false);
                pattern_vibration.setChecked(false);
                pattern_both.setChecked(true);
                text_both.setTextColor(getResources().getColor(R.color.login_id_edit_text));
                text_vibration.setTextColor(Color.parseColor("#c0c0c0"));
                text_ring.setTextColor(Color.parseColor("#c0c0c0"));

                setSharedString("ALARM_PATTERN",BOTH);



                break;
        }

    }


/*▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄*/


    //Shared 값이 "Y" 가 아니면 모두  false 를 return 한다.

    public boolean getState(String str){

        //2021.07 어플접속시 기본값 전자결재 & 메모보고 ON
        System.out.println("############################ str1111 ====== " + str);
        System.out.println("############################ str ====== " + getSharedString(str).toString());
        SharedPreferences user =getSharedPreferences("USERINFO",MODE_PRIVATE);
        System.out.println("############################ user ====== "+user.getAll());

        /*if ( "".equals(getSharedString("TOPIC_ELECAPP")) || getSharedString("TOPIC_ELECAPP") == null){
            setSharedString("TOPIC_ELECAPP","Y");
        } else if ( "".equals(getSharedString("TOPIC_MEMOAPP")) || getSharedString("TOPIC_MEMOAPP") == null){
            setSharedString("TOPIC_MEMOAPP","Y");
        }*/

        if(getSharedString(str).equals("Y")){
            return true;
        }else{
            return false;
        }
    }


    // 방해 금지 모드가 설정 돼있을때는 타임 피커를 설정 할 수 있게 해주지만 만약에 방해금지 모드가 설정 돼있지
    //않은 경우에는 타임 피커를 설정 할 수 없게 한다.
    public void setBanColor(boolean switch_state){
        if(switch_state){// 방해금지 모드가 켜졌을때
            ban_title.setTextColor(getResources().getColor(R.color.login_id_edit_text));
            text_start.setTextColor(Color.parseColor("#000000"));
            textStartTime.setTextColor(Color.parseColor("#000000"));
            text_end.setTextColor(Color.parseColor("#000000"));
            textEndTime.setTextColor(Color.parseColor("#000000"));

            setStartTime.setClickable(true);
            setEndTime.setClickable(true);


        }
        else{ // 스위치가 꺼져있을때 이다.
            ban_title.setTextColor(Color.parseColor("#d3d3d3"));
            text_start.setTextColor(Color.parseColor("#d3d3d3"));
            textStartTime.setTextColor(Color.parseColor("#d3d3d3"));
            text_end.setTextColor(Color.parseColor("#d3d3d3"));
            textEndTime.setTextColor(Color.parseColor("#d3d3d3"));

            setStartTime.setClickable(false);
            setEndTime.setClickable(false);


        }

    }

    //시간 값을 받아서 오전 오후로 나눠 계산하는 구간이다.
    public void setTime(String hour, String minute ,TextView view,String state){
        SimpleDateFormat currenthh = new SimpleDateFormat("kk");
        String getCurrTime = currenthh.format(new Date());

        int currentHOUR = Integer.parseInt(getCurrTime) % 24;
        int after2HOUR = (currentHOUR + 2) % 24;
        SimpleDateFormat currentmm = new SimpleDateFormat("mm");
        String getCurrTime2 = currentmm.format(new Date());
        int currentMINUTE = Integer.parseInt(getCurrTime2) % 60;






        String text_H;
        String text_M;
        String text_AMPM;
        int h;
        int m;
        if(getSharedString("START_HOUR").equals("")){
           if(state.equals("S")){
               h=currentHOUR;
               m=currentMINUTE;
               setSharedString("START_HOUR",String.valueOf(currentHOUR));
               setSharedString("START_MINUTE",String.valueOf(currentMINUTE));
               setSharedString("END_HOUR",String.valueOf(after2HOUR));
               setSharedString("END_MINUTE",String.valueOf(currentMINUTE));
           }else{
               h=after2HOUR;
               m=currentMINUTE;
           }

        }else {

            h = Integer.parseInt(hour);
            m = Integer.parseInt(minute);
        }
        if(h/12==1){
            text_AMPM ="PM ";

            h=h%12;
            if(h==0) {
                h = 12;
            }

            text_H = String.valueOf(h);
            text_M = String.valueOf(m);

            if(h<10){
                text_H= "0"+text_H;
            }
            if(m<10){
                text_M="0"+text_M;
            }

        }
        else{
            text_AMPM="AM ";
            h=h%12;
            if(h==0) {
                h = 12;
            }
            text_H = String.valueOf(h);
            text_M = String.valueOf(m);


            if(h<10){
                text_H= "0"+text_H;
            }
            if(m<10){
                text_M="0"+text_M;
            }
        }

        view.setText(text_AMPM+text_H+":"+text_M);


    }
}
