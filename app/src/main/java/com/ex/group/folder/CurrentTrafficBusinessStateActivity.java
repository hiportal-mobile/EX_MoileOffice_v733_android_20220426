package com.ex.group.folder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ex.group.folder.dialog.CommonDialog_oneButton;
import com.ex.group.folder.retrofitclient.APIClient;
import com.ex.group.folder.retrofitclient.APIInterface;
import com.ex.group.folder.retrofitclient.pojo.RequestTrafficBusiness;
import com.ex.group.folder.utility.BaseActivity;
import com.ex.group.folder.utility.dateCalculater;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentTrafficBusinessStateActivity extends BaseActivity {

    private TextView text_date;
    private TextView text_total_toll;
    private TextView text_total_traffic;
    private TextView text_hipass_share;
    private TextView text_death_toll;
    private TextView text_total_death_toll;
    private TextView text_total_death_toll_lastyear;
    private TextView text_death_toll_variation;

    private ImageView fab_back;
    private String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_traffic_business_state);
        Calendar cal = new GregorianCalendar(Locale.KOREA);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년MM월dd일");
        date = simpleDateFormat.format(cal.getTime()).toString();


        InitUI();
        getInfo();

    }

    public void InitUI(){

        text_date =findViewById(R.id.text_date);
        text_total_toll = findViewById(R.id.text_total_toll);
        text_total_traffic = findViewById(R.id.text_total_traffic);
        text_hipass_share = findViewById(R.id.text_hipass_share);
        text_death_toll = findViewById(R.id.text_death_toll);
        text_total_death_toll = findViewById(R.id.text_total_death_toll);
        text_total_death_toll_lastyear = findViewById(R.id.text_total_death_toll_lastyear);
        text_death_toll_variation =findViewById(R.id.text_death_toll_variation);

        fab_back =findViewById(R.id.fab_back);
        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void getInfo(){

        APIInterface api ;
        api = APIClient.getClient().create(APIInterface.class);

        final Call<RequestTrafficBusiness> rerquestTraffic = api.requestTrafficandBusiness
                (getSharedString("USERID"),date);
        rerquestTraffic.enqueue(new Callback<RequestTrafficBusiness>() {
            @Override
            public void onResponse(Call<RequestTrafficBusiness> call, Response<RequestTrafficBusiness> response) {
                if(response.body().resultMsg.equals("OK")){


                    text_date.setText(date);

                    text_total_toll.setText(response.body().yesterdaySales);
                    text_total_traffic.setText(response.body().yseterdayTraffic);
                    text_hipass_share.setText(response.body().hipassUseRate);
                    Log.e("yesterday :  ",
                            String.valueOf(response.body().yesterdayCnt));
                    text_death_toll.setText(response.body().yesterdayCnt);
                    text_total_death_toll.setText(response.body().nowCnt);
                    text_death_toll_variation.setText(response.body().increasePt);
                    text_total_death_toll_lastyear.setText(response.body().lastYearCnt);

                }else{
                    CommonDialog_oneButton cdo =  new CommonDialog_oneButton(CurrentTrafficBusinessStateActivity.this, "", "조회 결과가 없습니다.", false, null);
                    cdo.show();
                }
            }

            @Override
            public void onFailure(Call<RequestTrafficBusiness> call, Throwable t) {
                CommonDialog_oneButton cdo =  new CommonDialog_oneButton(CurrentTrafficBusinessStateActivity.this, getString(R.string.networkError), getString(R.string.check_network), false, null);
                cdo.show();
            }
        });


    }
}
