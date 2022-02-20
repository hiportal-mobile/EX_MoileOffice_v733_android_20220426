package com.ex.group.elecappmemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.folder.MainActivity;
import com.ex.group.folder.R;
import com.ex.group.folder.retrofitclient.APIClient;
import com.ex.group.folder.retrofitclient.APIInterface;
import com.ex.group.folder.retrofitclient.pojo.MemoNoticePopInfo;
import com.skt.pe.common.data.SKTWebUtil;
import com.skt.pe.common.exception.SKTException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ex.group.folder.utility.CommonUtil.checkNull;
import static com.ex.group.folder.utility.CommonUtil.fontSizeForTablet;
import static com.ex.group.folder.utility.CommonUtil.getIsTablet;
import static com.ex.group.folder.utility.CommonUtil.getScreenHeight;
import static com.ex.group.folder.utility.CommonUtil.getScreenSize;
import static com.ex.group.folder.utility.CommonUtil.getScreenWidth;
import static com.ex.group.folder.utility.CommonUtil.setPrefString;

/**
 * Created by EJY on 2021-04-21.
 * 메모보고 공지사항 Dialog
 */

public class NoticeMemoDialog extends Dialog {
    private String TAG = NoticeMemoDialog.class.getSimpleName();
    private Context context;
    LinearLayout ll_notc_popup, ll_notc_top, section_attfl_no, ll_notc_ctnt;
    ImageView iv_speaker;
    TextView tv_notc_top, tv_notc_titl;
    WebView tv_notc_ctnt;
    TextView tv_checkbox, btn_close;
    CheckBox cb_notice_pop;

    private ListView section_attfl_listview = null;
    private ListViewAdapter adapter = null;

    private String notcSeq = "";

//    private int fontSizeForTablet = 25;
    private int screenSize = 0;


    public NoticeMemoDialog(Context mContext) {
        super(mContext, R.style.CustomDialog);
        this.context = mContext;
        screenSize = getScreenSize(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_memo_notice);

        Log.d(TAG, "[EJY] onCreate()");

        ll_notc_popup = (LinearLayout) findViewById(R.id.ll_notc_popup);
        ll_notc_ctnt = (LinearLayout) findViewById(R.id.ll_notc_ctnt);
        iv_speaker = (ImageView) findViewById(R.id.iv_speaker);
        ll_notc_top = (LinearLayout) findViewById(R.id.ll_notc_top);
        tv_notc_top = (TextView) findViewById(R.id.tv_notc_top);
        tv_notc_titl = (TextView) findViewById(R.id.tv_notc_titl);
        tv_notc_ctnt = (WebView) findViewById(R.id.tv_notc_ctnt);
        cb_notice_pop = (CheckBox) findViewById(R.id.cb_notice_pop);
        tv_checkbox = (TextView) findViewById(R.id.tv_checkbox);
        btn_close = (TextView) findViewById(R.id.btn_close);
        Log.d(TAG, "[EJY] onCreate() - screenSize : "+screenSize);
        LinearLayout.LayoutParams ll_notc_popup_params = (LinearLayout.LayoutParams) ll_notc_popup.getLayoutParams();


        //2021-04-20 [EJY] Tablet 인 경우 글자크기 설정
        if(getIsTablet(context)) {
            tv_notc_top.setTextSize(Dimension.SP, screenSize+fontSizeForTablet+5);
            tv_notc_titl.setTextSize(Dimension.SP, screenSize+fontSizeForTablet-5);
            tv_checkbox.setTextSize(Dimension.SP, screenSize+fontSizeForTablet-13);
            btn_close.setTextSize(Dimension.SP, screenSize+fontSizeForTablet+3);

//            LinearLayout.LayoutParams ll_notc_popup_params = (LinearLayout.LayoutParams) ll_notc_popup.getLayoutParams();
            ll_notc_popup_params.width = ll_notc_popup_params.width + screenSize + fontSizeForTablet;
//            ll_notc_popup_params.width = getScreenWidth(context) / 2;

            //스피커 이미지 조정
            LinearLayout.LayoutParams iv_speaker_params = (LinearLayout.LayoutParams) iv_speaker.getLayoutParams();
            iv_speaker_params.width = screenSize+fontSizeForTablet + 30;
            iv_speaker_params.height = screenSize+fontSizeForTablet + 30;

        } else {
            ll_notc_popup_params.width = ll_notc_popup_params.width + screenSize;
            ll_notc_popup_params.width = getScreenWidth(context) * 7/8;
        }

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "[EJY] onCreate() - cb_notice_pop.isChecked : "+cb_notice_pop.isChecked());
                Log.d(TAG, "[EJY] onCreate() - cnotcSeq : "+notcSeq);
                if(cb_notice_pop.isChecked()) {
                    setPrefString(context, "CHECKED_NOTC_"+notcSeq, "Y");  //Y : 다시보지않기 O
                } else {
                    setPrefString(context, "CHECKED_NOTC_"+notcSeq, "N");  //N : 다시보지않기 X
                }
                dismiss();
            }
        });

        section_attfl_no = (LinearLayout) findViewById(R.id.section_attfl_no);
        section_attfl_listview = (ListView) findViewById(R.id.section_attfl_listview);
        adapter = new ListViewAdapter();
    }


    //공지사항 구분, 제목, 내용 설정
    public void setTextView(String memoNotcSeq, String clssCd, String title, String content){
        this.notcSeq = memoNotcSeq;
        if(checkNull(clssCd).equals("01")){
            tv_notc_top.setText("일 반");
            ll_notc_top.setBackground(context.getResources().getDrawable(R.drawable.custom_dialog_title_01));
            ll_notc_top.setBackground(context.getResources().getDrawable(R.drawable.custom_dialog_title_01));

        } else {
            tv_notc_top.setText("긴 급");
            ll_notc_top.setBackground(context.getResources().getDrawable(R.drawable.custom_dialog_title_02));
        }

        tv_notc_titl.setText(checkNull(title));

        //내용 - 웹뷰로 표시
        try {
            SKTWebUtil.loadWebView(context, tv_notc_ctnt, checkNull(content));
        } catch (SKTException e) {
            e.printStackTrace();
        }
    }


    //공지사항 첨부파일 설정
    public void setAttflInfo(String attflSeq) {
        Log.d(TAG, "[EJY] setAttflInfo() - attflSeq : "+attflSeq);

        if(!attflSeq.equals("")) {
            APIInterface apiInterface;
            apiInterface = APIClient.getClient().create(APIInterface.class);

            final Call<MemoNoticePopInfo> requestNoticeAttflPopupCall = apiInterface.request_notice_attfl_popup_info(attflSeq);
            requestNoticeAttflPopupCall.enqueue(new Callback<MemoNoticePopInfo>() {
                @Override
                public void onResponse(Call<MemoNoticePopInfo> call, Response<MemoNoticePopInfo> response) {
                    Log.d(TAG, "[EJY] setAttflInfo() - response.body : "+response.body());

                    String result = response.body().result;
                    String resultMessage = response.body().resultMessage;
                    List<MemoNoticePopInfo.AttflData> attflData = response.body().attfl;

                    Log.d(TAG, "[EJY] setAttflInfo() - result : "+result);
                    Log.d(TAG, "[EJY] setAttflInfo() - resultMessage : "+resultMessage);
                    Log.d(TAG, "[EJY] setAttflInfo() - noticeData : "+attflData.size());

                    if(attflData.size() > 0) {
                        section_attfl_listview.setVisibility(View.VISIBLE);
                        section_attfl_no.setVisibility(View.GONE);
                        for(int i=0; i<attflData.size(); i++) {
                            adapter.addItem(new MemoNoticePopInfo.AttflData(attflData.get(i).getORTX_FLNM()));
                        }
                        //리스트뷰에 Adapter 설정
                        section_attfl_listview.setAdapter(adapter);

                        if(attflData.size() > 1) {
                            LinearLayout.LayoutParams ll_notc_ctnt_params = (LinearLayout.LayoutParams) ll_notc_ctnt.getLayoutParams();
                            ll_notc_ctnt_params.height = ll_notc_ctnt_params.height - (screenSize+25);
                            section_attfl_listview.setMinimumHeight(70);
                        }
                    } else {
                        section_attfl_listview.setVisibility(View.GONE);
                        section_attfl_no.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<MemoNoticePopInfo> call, Throwable t) {
                }

            });
        } else {
            section_attfl_listview.setVisibility(View.GONE);
            section_attfl_no.setVisibility(View.VISIBLE);
        }

    }


    /* 리스트뷰 어댑터 */
    public class ListViewAdapter extends BaseAdapter {
        ArrayList<MemoNoticePopInfo.AttflData> items = new ArrayList<MemoNoticePopInfo.AttflData>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(MemoNoticePopInfo.AttflData item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final MemoNoticePopInfo.AttflData attflItem = items.get(position);

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_memo_notice_attfl, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            TextView tv_attfl = (TextView) convertView.findViewById(R.id.tv_attfl);

            //2021-04-20 [EJY] Tablet 인 경우 글자크기 설정
            if(getIsTablet(context)) {
                tv_attfl.setTextSize(Dimension.SP, screenSize+fontSizeForTablet-14);
                Log.d(TAG, "screenSize : "+screenSize);
                LinearLayout.LayoutParams iv_icon_params = (LinearLayout.LayoutParams) iv_icon.getLayoutParams();
                iv_icon_params.width = screenSize+fontSizeForTablet + 5;
                iv_icon_params.height = screenSize+fontSizeForTablet + 5;
            }

            iv_icon.setImageResource(R.drawable.icon_file);
            tv_attfl.setText(attflItem.getORTX_FLNM());
            tv_attfl.setPaintFlags(tv_attfl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);  //TextView 에 밑줄

            //각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPop();
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }


    private void showPop() {
        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        final LayoutInflater layoutInflater = getLayoutInflater();

        View customMessage = layoutInflater.inflate(R.layout.common_dialog_negative, null);
        final TextView btn_confirm = (TextView) customMessage.findViewById(R.id.btn_confirm);
        final TextView dialog_title = (TextView)customMessage.findViewById(R.id.tv_title);
        final TextView dialog_content = (TextView)customMessage.findViewById(R.id.tv_content);

        //2021-04-20 [EJY] Tablet 인 경우 글자크기 설정
        if(getIsTablet(context)) {
            btn_confirm.setTextSize(Dimension.SP, screenSize+fontSizeForTablet-5);
            dialog_title.setTextSize(Dimension.SP, screenSize+fontSizeForTablet-5);
            dialog_content.setTextSize(Dimension.SP, screenSize+fontSizeForTablet-15);

        }

        dialog_title.setText("알 림");
        dialog_content.setText("첨부파일은 메모보고 메뉴에서 확인해주세요. ");
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.setCancelable(false);
        dialog.setContentView(customMessage);
        dialog.show();
    }

}
