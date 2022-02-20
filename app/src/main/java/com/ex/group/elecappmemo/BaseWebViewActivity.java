package com.ex.group.elecappmemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ex.group.approval.easy.activity.ApprovalMainActivity;
import com.ex.group.board.activity.BoardListActivity;
import com.ex.group.folder.MainActivity;
import com.ex.group.folder.R;
import com.ex.group.folder.WebViewActivity;
import com.ex.group.folder.categoryList.AppdataList;
import com.ex.group.folder.utility.AlertUtil;
import com.ex.group.mail.activity.EmailMainActivity;

import static com.ex.group.elecappmemo.Global.ELEC_URL;
import static com.ex.group.elecappmemo.Global.MEMO_URL;


public class BaseWebViewActivity extends AppCompatActivity implements View.OnClickListener {

    Activity activity;
    private static final int CUSTOM_DIALOG_MENU = 1900;
    public AlertUtil alert = new AlertUtil(this);
    GridView menu_grid;
    MenuGridAdapter gridadapter;

    public String getSharedString(String name){
        SharedPreferences user =getSharedPreferences("USERINFO",MODE_PRIVATE);
        if(!user.contains(name)){setSharedString(name,"");}
        final String value = user.getString(name,"");
//        logmaking("SHARED MAP get ",name+"]-["+value);
        return value;
    }
    public  void setSharedString(String name , String value){
        SharedPreferences user =getSharedPreferences("USERINFO",MODE_PRIVATE);
        SharedPreferences.Editor edit = user.edit();
        edit.putString(name,value);
//        logmaking("SHARED MAP edit ",name+"]-["+value);
        edit.commit();
    }

    //더보기 메뉴 없애기
    public void hideMenu(){
        alert.cancel();
    }

    //더보기 메뉴 만들기
    public void showMenu(String strMenuNotice) {
        final View view = LayoutInflater.from(this).inflate(R.layout.activity_popup_menu, null);
        final TextView txt_version = (TextView) view.findViewById(R.id.txt_version);

        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            //Log.e("","에러");
            Log.e("","에러");
        }
//        txt_version.setText("버전:" + info.versionName);

        ImageView btn_top_close = view.findViewById(R.id.btn_top_close_hp);
        btn_top_close.setOnClickListener(this);

        ImageView btn_menu_user_info_hp = view.findViewById(R.id.btn_menu_user_info_hp);
        btn_menu_user_info_hp.setOnClickListener(this);

        menu_grid = view.findViewById(R.id.menu_grid);

        setAppListGridView();
        showMenuAnim(getAlertUtil(), view, true);
    }


    public void setAppListGridView(){
        if(null == gridadapter){
            gridadapter = new MenuGridAdapter(activity);
        }
        menu_grid.setAdapter(gridadapter);
    }

    public class MenuGridAdapter extends BaseAdapter{
        private LayoutInflater inflater;
        private Context con;

        public MenuGridAdapter(Context con){
            this.con = con;
        }

        @Override
        public int getCount() {

            return MainActivity.appList_onVpn.size();
        }

        @Override
        public AppdataList getItem(int i) {
            return MainActivity.appList_onVpn.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final int pos = position;
            Log.i("BaseWebViewActivity", "BaseWebViewActivity position : " + position);
            if (convertView == null) {
                inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.elecmemo_menu_grid_item, viewGroup, false);
            }
            ImageView img_app_icon = convertView.findViewById(R.id.img_app_icon);
            TextView txt_appname = convertView.findViewById(R.id.txt_appname);
            txt_appname.setText(getItem(pos).getAppNm());


            img_app_icon.setImageResource(getItem(pos).getDrawableicon3());

            /*if (getItem(pos).getDrawableicon() == 0) {
                try {
                    img_app_icon.setImageDrawable(activity.getPackageManager().getApplicationIcon(getItem(pos).getPackageNm()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                img_app_icon.setImageResource(getItem(pos).getDrawableicon());
            }*/

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getItem(pos).getAppNm().equals("외출휴가")) {
                        Intent intent = new Intent(activity, ApprovalMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finish();
                    } else if (getItem(pos).getAppNm().equals("게시판")) {
                        Intent intent = new Intent(activity, BoardListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finish();
                    } else if (getItem(pos).getAppNm().equals("사내메일")) {
                        Intent intent = new Intent(activity, EmailMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finish();
                    } else if (getItem(pos).getAppNm().equals("전자결재")) {
                        Intent intent = new Intent(activity, ElecMemoAppWebViewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("hybridUrl", ELEC_URL);
                        activity.startActivity(intent);
                        activity.finish();
                    } else if (getItem(pos).getAppNm().equals("메모보고")) {
                        Intent intent = new Intent(activity, ElecMemoAppWebViewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("hybridUrl", MEMO_URL);
                        activity.startActivity(intent);
                        activity.finish();
                    } else if (getItem(pos).getAppNm().equals("직원검색")) {
                        Intent intent = new Intent(activity, WebViewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("hybridUrl", getItem(pos).getHybridUrl() + getSharedString("USERDEPARTURECODE"));
                        Log.v("hybridSelect", " hybridSelect  :::    " + getItem(pos).getHybridUrl());
                        activity.startActivity(intent);
                        activity.finish();
                        //FIXED APP 이 아닌 경우에는 하이브리드 여부를 판단해준다.
                    } else {
                        if (getItem(pos).getHybridYn().equals("N")) {

                            Log.v("packageName", "::::" + getItem(pos).getPackageNm());
                            try {
                                Intent intent = new Intent(getItem(pos).getPackageNm().toString() + ".LAUNCH_MAIN");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("userId", getSharedString("USERID"));
                                activity.startActivity(intent);
                                activity.finish();
                            } catch (Exception e) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(getItem(pos).getPackageNm().toString());
                                intent.putExtra("userId", getSharedString("USERID"));
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        } else {
                            Intent intent = new Intent(activity, WebViewActivity.class);
                            intent.putExtra("hybridUrl", getItem(pos).getHybridUrl() + getSharedString("USERDEPARTURECODE"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Log.v("hybridSelect", "   :::    " + getItem(pos).getHybridUrl());
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }
                }
            });

            return convertView;
        }
    }


    public AlertUtil getAlertUtil() {
        return alert;
    }

    private void showMenuAnim(AlertUtil alert, View customView, boolean isCancelable) {
        this.showCustomDialog(alert, customView, isCancelable, CUSTOM_DIALOG_MENU);
    }

    public void showCustomDialog(AlertUtil alert, View customView, boolean isCancelable, int dlgType) {
        Dialog dlg = new Dialog(this, R.style.CustomAlertNoFrame);

        if (dlgType == CUSTOM_DIALOG_MENU) {
            dlg.getWindow().setWindowAnimations(R.style.menu_animation);
        }

        dlg.setContentView(customView);
        dlg.setCancelable(isCancelable);
        dlg.setCanceledOnTouchOutside(isCancelable);
        Rect outRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dlg.getWindow().getAttributes());

        switch (dlgType) {
            case CUSTOM_DIALOG_MENU:    //팝업 메뉴
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.RIGHT;
                break;

            default:
                break;
        }

        dlg.getWindow().setAttributes(lp);
        alert.showDialog(dlg);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_top_close_hp:
                hideMenu();
                break;

            case R.id.btn_menu_user_info_hp:
                activity.finish();
                break;


        }



    }
}
