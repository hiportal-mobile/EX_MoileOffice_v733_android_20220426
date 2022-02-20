package com.ex.group.elecappmemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ex.group.approval.easy.activity.ApprovalMainActivity;
import com.ex.group.board.activity.BoardListActivity;
import com.ex.group.folder.MainActivity;
import com.ex.group.folder.R;
import com.ex.group.folder.WebViewActivity;
import com.ex.group.folder.utility.BaseActivity;
import com.ex.group.mail.activity.EmailMainActivity;

import java.util.ArrayList;

import static com.ex.group.elecappmemo.Global.ELEC_URL;
import static com.ex.group.elecappmemo.Global.MEMO_URL;

//2021-03-30 [EJY] 상단 메뉴바 더보기 액티비티 - 상단 메뉴바 제거로 현재 사용안함
public class MenuMoreActivity extends BaseActivity implements View.OnClickListener {
    String TAG = MenuMoreActivity.class.getSimpleName();

    LinearLayout ll_goHome, ll_goClose;
    GridView gridview = null;
    MenuAdapter adapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        getWindow().setAttributes(layoutParams);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_menumore);

        ll_goHome = (LinearLayout) findViewById(R.id.ll_goHome);
        ll_goClose = (LinearLayout) findViewById(R.id.ll_goClose);

        ll_goHome.setOnClickListener(this);
        ll_goClose.setOnClickListener(this);

        gridview = (GridView) findViewById(R.id.gridview);
        adapter = new MenuAdapter();

        Log.d(TAG, "[EJY] appList_onVpn.size : "+ MainActivity.appList_onVpn.size());
        for(int i=0; i<MainActivity.appList_onVpn.size(); i++) {
            String appNm = MainActivity.appList_onVpn.get(i).getAppNm();
            int resId = MainActivity.appList_onVpn.get(i).getDrawableicon3();
            String packageNm = MainActivity.appList_onVpn.get(i).getPackageNm();
            String hybridUrl = MainActivity.appList_onVpn.get(i).getHybridUrl();
            String hybridYn = MainActivity.appList_onVpn.get(i).getHybridYn();
            adapter.addItem(new MenuItem(Integer.toString(i), appNm, resId, packageNm, hybridUrl, hybridYn));
        }

        gridview.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_goHome:
                Intent intent = new Intent(MenuMoreActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("app", "N");
                startActivity(intent);
                finish();
                break;
            case R.id.ll_goClose:
                finish();
                overridePendingTransition(R.anim.animation_slide_in_right,R.anim.animation_slide_out_right);
                break;
        }
    }

    /* 메뉴 그리드 어뎁터 */
    class MenuAdapter extends BaseAdapter {
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(MenuItem item) {
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
            final MenuItem menuItem = items.get(position);
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.menumore_list_item, viewGroup, false);

                TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);

                tv_name.setText(menuItem.getName());
                iv_icon.setImageResource(menuItem.getResId());
                Log.d(TAG, "getView() - [ "+position+" ] "+menuItem.getName());
            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (menuItem.getName().equals("외출휴가")) {
                        Intent intent = new Intent(context, ApprovalMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else if (menuItem.getName().equals("게시판")) {
                        Intent intent = new Intent(context, BoardListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else if (menuItem.getName().equals("사내메일")) {
                        Intent intent = new Intent(context, EmailMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else if (menuItem.getName().equals("전자결재")) {
                        Intent intent = new Intent(context, ElecMemoAppWebViewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("hybridUrl", ELEC_URL);
                        startActivity(intent);
                        finish();
                    } else if (menuItem.getName().equals("메모보고")) {
                        Intent intent = new Intent(context, ElecMemoAppWebViewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("hybridUrl", MEMO_URL);
                        startActivity(intent);
                        finish();
                    } else if (menuItem.getName().equals("직원검색")) {
                        Intent intent = new Intent(context, WebViewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("hybridUrl", menuItem.getHybridUrl() + getSharedString("USERDEPARTURECODE"));
                        Log.v("hybridSelect", " hybridSelect  :::    " + menuItem.getHybridUrl());
                        startActivity(intent);
                        finish();
                        //FIXED APP 이 아닌 경우에는 하이브리드 여부를 판단해준다.
                    } else {
                        if (menuItem.getHybridYn().equals("N")) {

                            Log.v("packageName", "::::" + menuItem.getPackageNm());
                            try {
                                Intent intent = new Intent(menuItem.getPackageNm() + ".LAUNCH_MAIN");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("userId", getSharedString("USERID"));
                                startActivity(intent);
                                finish();

                            } catch (Exception e) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(menuItem.getPackageNm());
                                intent.putExtra("userId", getSharedString("USERID"));
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Intent intent = new Intent(context, WebViewActivity.class);
                            intent.putExtra("hybridUrl", menuItem.getHybridUrl() + getSharedString("USERDEPARTURECODE"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Log.v("hybridSelect", "   :::    " + menuItem.getHybridUrl());
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }

    /* 아이템에 넣을 데이 정의 */
    public class MenuItem {
        String num;
        String name;
        int resId;
        String packageNm;
        String hybridUrl;
        String hybridYn;

        public MenuItem(String num, String name, int resId, String packageNm, String hybridUrl, String hybridYn) {
            this.num = num;
            this.name = name;
            this.resId = resId;
            this.packageNm = packageNm;
            this.hybridUrl = hybridUrl;
            this.hybridYn = hybridYn;
        }

        public String getNum() {
            return num;
        }
        public void setNum(String num) {
            this.num = num;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public int getResId() {
            return resId;
        }
        public void setResId(int resId) {
            this.resId = resId;
        }

        public String getPackageNm() {
            return packageNm;
        }
        public void setPackageNm(String packageNm) {
            this.packageNm = packageNm;
        }

        public String getHybridUrl() {
            return hybridUrl;
        }
        public void setHybridUrl(String hybridUrl) {
            this.hybridUrl = hybridUrl;
        }

        public String getHybridYn() {
            return hybridYn;
        }
        public void setHybridYn(String hybridYn) {
            this.hybridYn = hybridYn;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.animation_slide_in_right,R.anim.animation_slide_out_right);
    }
}
