package com.ex.group.approval.easy.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.approval.easy.activity.ApplyVacationApprovalActivity;
import com.ex.group.approval.easy.activity.ApprovalCommonActivity;
import com.ex.group.approval.easy.primitive.DraftPrimitive;
import com.ex.group.folder.R;
import com.skt.pe.common.conf.Environ;
import com.skt.pe.common.conf.EnvironManager;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.service.Parameters;

import org.json.JSONArray;
import org.json.JSONObject;

public class WheelGuntaeDialogActivity extends ApprovalCommonActivity {


    String[] ATTEND_CD_LIST ;//근태코드
    String[] ATTEND_NM_LIST;//근태명
    String[] LIMIT_D_CNT_LIST;//한도일수
    String[] CHILD_ATTEND_CNT_LIST;//하위근태존재여부
    String[] ANL_END_YM_LIST;////연차종료년월ex201805
    String[] PK_ATTEND_NM_LIST;//근태명

    String[] ATTEND_CD_LIST2 ;//근태코드
    String[] ATTEND_NM_LIST2;//근태명
    String[] LIMIT_D_CNT_LIST2;//한도일수
    String[] CHILD_ATTEND_CNT_LIST2;//하위근태존재여부
    String[] ANL_END_YM_LIST2;////연차종료년월ex201805
    String[] PK_ATTEND_NM_LIST2;//근태명

    String[] ATTEND_CD_LIST3;//근태코드
    String[] ATTEND_NM_LIST3;//근태명
    String[] LIMIT_D_CNT_LIST3;//한도일수
    String[] CHILD_ATTEND_CNT_LIST3;//하위근태존재여부
    String[] ANL_END_YM_LIST3;////연차종료년월ex201805
    String[] PK_ATTEND_NM_LIST3;//근태명



    Spinner guntae_spin_level1;
    Spinner guntae_spin_level2;
    Spinner guntae_spin_level3;

    String COMMON_SEARCHATTENDLIST1 ="COMMON_SEARCHATTENDLIST1";
    String COMMON_SEARCHATTENDLIST2 ="COMMON_SEARCHATTENDLIST2";
    String COMMON_SEARCHATTENDLIST3 ="COMMON_SEARCHATTENDLIST3";

    String S_ATTEND_CD = "";
    String S_ATTEND_NM = "";
    String LIMIT_D_CNT = "";
    TextView btn_ok;

    @Override
    protected void onCreateX(Bundle bundle) {
        super.onCreateX(bundle);
        guntae_spin_level1 = (Spinner)findViewById(R.id.guntae_spin_level1);
        guntae_spin_level2 = (Spinner)findViewById(R.id.guntae_spin_level2);
        guntae_spin_level3 = (Spinner)findViewById(R.id.guntae_spin_level3);
        btn_ok = (TextView) findViewById(R.id.btn_ok);

        //변경된곳2021.04
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("S_ATTEND_CD", S_ATTEND_CD);
                intent.putExtra("S_ATTEND_NM", S_ATTEND_NM);
                intent.putExtra("LIMIT_D_CNT", LIMIT_D_CNT);
                // 2021.04 연차가 남아있을 경우 적치를 사용 할 수 없음

                if(S_ATTEND_CD.equals("11600")){

                    for( int i = 0; i < ATTEND_CD_LIST2.length ; i++ ){

                        if( !LIMIT_D_CNT_LIST2[i].equals("0") && !ATTEND_CD_LIST2[i].equals("11600")){
                            Toast.makeText(getApplicationContext(),"연차와 적치 소진 후 사용할 수 있습니다.", Toast.LENGTH_LONG).show();
                            break;
                        } else if ( ATTEND_CD_LIST2[i].equals("11600")){
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }else if( !S_ATTEND_CD.equals("11200") ){
                    if( !S_ATTEND_CD.equals("11600") ){
                        if( ATTEND_CD_LIST2[0].equals("11200") && !LIMIT_D_CNT_LIST2[0].equals("0")){
                            Toast.makeText(getApplicationContext(),"연차 소진 후 적치를 사용할 수 있습니다.", Toast.LENGTH_LONG).show();
                        }else{
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }else{
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        getAttendList(COMMON_SEARCHATTENDLIST1, "1", "22000");

    }




    public void getAttendList(String primitive, String S_ATTEND_CLASS, String S_PARREND_ATTEND_CD){
        Parameters params = new Parameters(COMMON_APPROVALSTAGING_RESTFULCLIENT);
        try {
            params.put("S_API_URL", "/searchAttendList");
            params.put("S_APP_URL", "/searchAttendList");
            params.put("S_ATTEND_CLASS", S_ATTEND_CLASS);
            params.put("S_PARREND_ATTEND_CD", S_PARREND_ATTEND_CD);
            params.put("S_EMP_ID", ""+ SKTUtil.getGMPAuth(WheelGuntaeDialogActivity.this).get(AuthData.ID_ID));
            Environ environ = EnvironManager.getEnviron(WheelGuntaeDialogActivity.this);
            String url = environ.getProtocol() + "://" + environ.getHost() + ":" + environ.getPort()+environ.FILE_ATTACHMENT+"?"+params.toString()+"&"+environ.getEnvironParam();

            new JsonAction(primitive, url, this).execute();

        }catch(Exception e){
            e.printStackTrace();
        }


    }



    @Override
    protected void onJsonActionPost(String primitive, String result) {
        try {
            Log.d("onJsonActionPost","result = " +primitive +"\n"+ result);
            JSONObject obj = new JSONObject(result);
            JSONArray jsonArray = obj.getJSONArray("Data");

            if(COMMON_SEARCHATTENDLIST1.equals(primitive)) {
                for (int i=0; i<jsonArray.length();i++){
                    JSONObject temp = jsonArray.getJSONObject(i);

                    ATTEND_CD_LIST = temp.getString("ATTEND_CD_LIST").split(",");
                    ATTEND_NM_LIST = temp.getString("ATTEND_NM_LIST").split(",");
                    LIMIT_D_CNT_LIST = temp.getString("LIMIT_D_CNT_LIST").split(",");
                    CHILD_ATTEND_CNT_LIST = temp.getString("CHILD_ATTEND_CNT_LIST").split(",");
//					ANL_END_YM_LIST = temp.getString("ANL_END_YM_LIST").split(",");
                    PK_ATTEND_NM_LIST = temp.getString("PK_ATTEND_NM_LIST").split(",");
                }
                setSpinItem1();
            }else if(COMMON_SEARCHATTENDLIST2.equals(primitive)){
                for (int i=0; i<jsonArray.length();i++){
                    JSONObject temp = jsonArray.getJSONObject(i);

                    ATTEND_CD_LIST2 = temp.getString("ATTEND_CD_LIST").split(",");
                    ATTEND_NM_LIST2 = temp.getString("ATTEND_NM_LIST").split(",");
                    LIMIT_D_CNT_LIST2 = temp.getString("LIMIT_D_CNT_LIST").split(",");
                    CHILD_ATTEND_CNT_LIST2 = temp.getString("CHILD_ATTEND_CNT_LIST").split(",");
//					ANL_END_YM_LIST2 = temp.getString("ANL_END_YM_LIST").split(",");
                    PK_ATTEND_NM_LIST2 = temp.getString("PK_ATTEND_NM_LIST").split(",");
                }
                setSpinItem2();
            }else if(COMMON_SEARCHATTENDLIST3.equals(primitive)){
                for (int i=0; i<jsonArray.length();i++){
                    JSONObject temp = jsonArray.getJSONObject(i);

                    ATTEND_CD_LIST3 = temp.getString("ATTEND_CD_LIST").split(",");
                    ATTEND_NM_LIST3 = temp.getString("ATTEND_NM_LIST").split(",");
                    LIMIT_D_CNT_LIST3 = temp.getString("LIMIT_D_CNT_LIST").split(",");
                    CHILD_ATTEND_CNT_LIST3 = temp.getString("CHILD_ATTEND_CNT_LIST").split(",");
//					ANL_END_YM_LIST3 = temp.getString("ANL_END_YM_LIST").split(",");
                    PK_ATTEND_NM_LIST3 = temp.getString("PK_ATTEND_NM_LIST").split(",");
                }
                setSpinItem3();

                if(ATTEND_CD_LIST3[0] == ""){
                    guntae_spin_level3.setVisibility(View.GONE);
                }else{
                    guntae_spin_level3.setVisibility(View.VISIBLE);
                }

            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void setSpinItem1(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(WheelGuntaeDialogActivity.this, android.R.layout.simple_spinner_item , ATTEND_NM_LIST){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if(position == getCount()){
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setText(getItem(getCount()));
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount();
            }

        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        guntae_spin_level1.setAdapter(adapter);
        guntae_spin_level1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                getAttendList(COMMON_SEARCHATTENDLIST2, "2", ATTEND_CD_LIST[position]);
                S_ATTEND_CD = ATTEND_CD_LIST[position];
                S_ATTEND_NM = ATTEND_NM_LIST[position];
                LIMIT_D_CNT = LIMIT_D_CNT_LIST[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    public void setSpinItem2(){
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(WheelGuntaeDialogActivity.this, android.R.layout.simple_spinner_item , ATTEND_NM_LIST2){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if(position == getCount()){
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setText(getItem(getCount()));
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount();
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        guntae_spin_level2.setAdapter(adapter);
        guntae_spin_level2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                getAttendList(COMMON_SEARCHATTENDLIST3, "3", ATTEND_CD_LIST2[position]);
                if(!"".equals(ATTEND_CD_LIST2[position])){
                    S_ATTEND_CD = ATTEND_CD_LIST2[position];
                    S_ATTEND_NM = ATTEND_NM_LIST2[position];
                    LIMIT_D_CNT = LIMIT_D_CNT_LIST2[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    public void setSpinItem3(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(WheelGuntaeDialogActivity.this, android.R.layout.simple_spinner_item , ATTEND_NM_LIST3){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if(position == getCount()){
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setText(getItem(getCount()));
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount();
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        guntae_spin_level3.setAdapter(adapter);
        guntae_spin_level3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                if(!"".equals(ATTEND_CD_LIST3[position])){
                    S_ATTEND_CD = ATTEND_CD_LIST3[position];
                    S_ATTEND_NM = ATTEND_NM_LIST3[position];
                    LIMIT_D_CNT = LIMIT_D_CNT_LIST3[position];
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }


    @Override
    protected int assignLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        return R.layout.easy_guntae_dialog_activity;
    }
}
