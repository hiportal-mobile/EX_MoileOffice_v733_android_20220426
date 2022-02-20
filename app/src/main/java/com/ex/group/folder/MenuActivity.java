package com.ex.group.folder;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.ex.group.folder.dialog.CommonDialog_oneButton;
import com.ex.group.folder.retrofitclient.APIClient;
import com.ex.group.folder.retrofitclient.APIInterface;
import com.ex.group.folder.retrofitclient.pojo.MenuListInfo;
import com.ex.group.folder.utility.BaseActivity;
import com.ex.group.folder.utility.dateCalculater;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ex.group.folder.service.FirebaseMessagingService.NOTI_ID;

public class MenuActivity extends BaseActivity {

    private static final String TAG = "===MenuActivity===";

    private final String TAG(String content) {
        return TAG + " (" + content + ")";
    }

    ;

    public ImageView button_prev;       //메인액티비티로 돌아가는 버튼이다.
    public TextView menu_date;          //식단의 날짜를 표시한다.
    public ViewPager diet_chart;        //식단을 표시한다.
    public TextView menu_back_today;    //오늘의 식단표를 표시한다.
    public TextView select_menu_group;

    public LottieAnimationView lottie_leftside;
    public LottieAnimationView lottie_rightside;
    final static String GROUP_MAIN = "본사";
    final static String GROUP_LAB = "도로교통연구원";

    public MenuAdapter menuAdapter;
    public String getDeparture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        NotificationManager noman = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        ;
        try {
            noman.cancel(NOTI_ID);
        } catch (Exception e) {
        }

        dateCalculater dateCal;
        dateCal = new dateCalculater();

        dateCal.getMonday();
        dateCal.getFriday();
        Log.e(TAG("getMonday"), ":" + dateCal.getMonday());
        Log.e(TAG("getFriday"), ":" + dateCal.getFriday());
        Log.e(TAG("today"), ":" + dateCal.getDayOfWeek());


        setInitUI();
        getMenuList(getGroupCode());
        lottieControl();

    }

    public String getGroupCode() {
        //그룹 선택이 안돼 있으면 그룹선택을 강제로 본사로 한다. 만약에 선택이 돼있다면 그에 맞게 뷰를 설정 해준다.
        if (getSharedString("MENUGROUP").equals("")) {
            setSharedString("MENUGROUP", GROUP_MAIN);
        }
        if (getSharedString("MENUGROUP").equals(GROUP_MAIN)) {
            getDeparture = "100000";
            select_menu_group.setText(GROUP_MAIN);
        } else if (getSharedString("MENUGROUP").equals(GROUP_LAB)) {
            getDeparture = "401600";
            select_menu_group.setText(GROUP_LAB);
        }

        return getDeparture;
    }

    public void setInitUI() {
        button_prev = findViewById(R.id.fab_back);
        menu_date = findViewById(R.id.menu_date);
        diet_chart = findViewById(R.id.diet_chart);
        menu_back_today = findViewById(R.id.menu_back_today);
        select_menu_group = findViewById(R.id.menu_group);

        lottie_leftside = findViewById(R.id.lottie_leftside);
        lottie_rightside = findViewById(R.id.lottie_rightside);
        lottie_leftside.setAnimation("leftside_arrows.json");
        lottie_rightside.setAnimation("rightside_arrows.json");
        lottie_leftside.setVisibility(View.INVISIBLE);
        lottie_rightside.setVisibility(View.INVISIBLE);


        button_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        select_menu_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final List<String> ListItems = new ArrayList<>();
                ListItems.add(GROUP_MAIN);
                ListItems.add(GROUP_LAB);
                final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                builder.setTitle(getString(R.string.menu_Select_group));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setSharedString("MENUGROUP", items[which].toString());
                        getMenuList(getGroupCode());

                    }
                });
                builder.show();


            }
        });

        menu_back_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final dateCalculater dateCal;
                dateCal = new dateCalculater();
                diet_chart.setAdapter(menuAdapter);
                diet_chart.setCurrentItem(dateCal.getDayOfWeek() - 2);
            }
        });


    }

    public void lottieControl() {
        if (diet_chart.getCurrentItem() == 0) {

            lottie_rightside.setVisibility(View.VISIBLE);
            lottie_rightside.setRepeatCount(100);
            lottie_rightside.playAnimation();
        } else if (diet_chart.getCurrentItem() == 4) {

            lottie_leftside.setVisibility(View.VISIBLE);
            lottie_leftside.setRepeatCount(100);
            lottie_leftside.playAnimation();
        } else {

            lottie_rightside.setVisibility(View.VISIBLE);
            lottie_rightside.setRepeatCount(100);
            lottie_rightside.playAnimation();

            lottie_leftside.setVisibility(View.VISIBLE);
            lottie_leftside.setRepeatCount(100);
            lottie_leftside.playAnimation();

        }


        diet_chart.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    Log.e(TAG("onpageScrolled"), "position : " + position + " positionOffset : " + String.valueOf(positionOffset) + "  positionOffsetPixels" + String.valueOf(positionOffsetPixels));
                    if (positionOffsetPixels < 0) {

                        lottie_rightside.setVisibility(View.VISIBLE);
                        lottie_rightside.setRepeatCount(100);
                        lottie_rightside.playAnimation();
                    }
                }

            }

            @Override
            public void onPageSelected(int position) {
                Log.e(TAG("onPageSelected"), "position : " + position);

                if (position == 0) {
                    lottie_leftside.setVisibility(View.INVISIBLE);
                    lottie_leftside.cancelAnimation();

                    lottie_rightside.setVisibility(View.VISIBLE);
                    lottie_rightside.setRepeatCount(100);
                    lottie_rightside.playAnimation();
                } else if (position == 4) {
                    lottie_rightside.setVisibility(View.INVISIBLE);
                    lottie_rightside.cancelAnimation();

                    lottie_leftside.setVisibility(View.VISIBLE);
                    lottie_leftside.setRepeatCount(100);
                    lottie_leftside.playAnimation();

                } else {
                    lottie_leftside.setVisibility(View.VISIBLE);
                    lottie_rightside.setVisibility(View.VISIBLE);
                    lottie_leftside.setRepeatCount(100);
                    lottie_rightside.setRepeatCount(100);
                    lottie_leftside.playAnimation();
                    lottie_rightside.playAnimation();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e(TAG("onPageScrollStateChanged"), "state: " + state);
                if (state == 2) {
                    lottie_leftside.cancelAnimation();
                    lottie_rightside.cancelAnimation();
                    lottie_leftside.setVisibility(View.INVISIBLE);
                    lottie_rightside.setVisibility(View.INVISIBLE);
                }
            }


        });
        /*lottie_leftside.playAnimation();
        lottie_rightside.playAnimation();
        lottie_leftside.setRepeatCount(100);
        lottie_rightside.setRepeatCount(100);*/


    }

    public void getMenuList(String departure) {
        final dateCalculater dateCal;
        dateCal = new dateCalculater();

        APIInterface api;
        api = APIClient.getClient().create(APIInterface.class);

        final Call<MenuListInfo> get_menu_list = api.menu_list_info(getSharedString("USERID"), getDeparture, dateCal.getMonday(), dateCal.getFriday());
        get_menu_list.enqueue(new Callback<MenuListInfo>() {
            @Override
            public void onResponse(Call<MenuListInfo> call, Response<MenuListInfo> response) {
                String result = response.body().result;
                String resultMsg = response.body().resultMsg;

                if (resultMsg.equals("OK")) {

                    List<MenuListInfo.MenuList> menuList = response.body().menuList;
                    ArrayList<MenuListInfo.MenuList> treeset = new ArrayList<MenuListInfo.MenuList>();
                    TreeSet<MenuListInfo.MenuList> tree = new TreeSet<MenuListInfo.MenuList>();
                    ArrayList<Integer> dayList = new ArrayList<>();
                    for (int i = 0; i < menuList.size(); i++) {
                        int day = dateCal.GapofDate(menuList.get(i).getMealDt(),dateCal.getMonday());
                        Calendar Meal =Calendar.getInstance();
                        Calendar Monday =Calendar.getInstance();

                        dayList.add(day);
                    }//날짜를 통해 index를 가져온다.
                    ArrayList<Integer> addList = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        boolean count = false;
                        for (int j = 0; j < dayList.size(); j++) {
                            if (dayList.get(j) == i) {
                                count = true;
                            }
                        }
                        if (count == false) {
                            addList.add(i);
                        }
                    }
                    for (int i = 0; i < addList.size(); i++) {
                        MenuListInfo.MenuList menlist = new MenuListInfo.MenuList();
                        menlist.setRunYN("N");
                        menlist.setMealDt(String.valueOf(Integer.parseInt(dateCal.getMonday()) + addList.get(i)));

                        menuList.add(addList.get(i), menlist);
                    }

                    for (int i = 0; i < menuList.size(); i++) {
                        tree.add(menuList.get(i));
                        Log.e(TAG("menuListSize : "), String.valueOf(menuList.size()));
                    }
                    Iterator it = tree.iterator();
                    while (it.hasNext()) {
                        treeset.add((MenuListInfo.MenuList) it.next());
                    }
                    for (MenuListInfo.MenuList men : treeset) {
                        Log.e(TAG("treeset"), men.getMealDt());
                    }

                    menuAdapter = new MenuAdapter(treeset);
                    menuAdapter.getCount();
                    diet_chart.setAdapter(menuAdapter);
                    diet_chart.setCurrentItem(dateCal.getDayOfWeek() - 2);
                }

            }

            @Override
            public void onFailure(Call<MenuListInfo> call, Throwable t) {
                CommonDialog_oneButton cdo = new CommonDialog_oneButton(MenuActivity.this, getString(R.string.networkError), getString(R.string.check_network), false, null);
                cdo.show();
            }
        });

    }

    public class MenuAdapter extends PagerAdapter {

        ArrayList<MenuListInfo.MenuList> menuList;


        public MenuAdapter(ArrayList<MenuListInfo.MenuList> list) {
            this.menuList = list;

        }


        @Override
        public int getCount() {
            return menuList.size(); // 데이터 수만큼 수정해야한다.,
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            Log.e(TAG("destroy item"), String.valueOf(position));
            container.removeView((View) object);
        }


        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.e(TAG("instantiateItem : "), String.valueOf(position));
            LayoutInflater inflater = LayoutInflater.from(MenuActivity.this);
            if (menuList.get(position).getRunYN().equals("Y")) {
                View view = inflater.inflate(R.layout.menu_chart, container, false);

                //String 선언 및 초기화
                String date = menuList.get(position).getMealDt();
                String brfst1 = menuList.get(position).getBreakfast1();
                String brfst2 = menuList.get(position).getBreakfast2();
                String lnch1 = menuList.get(position).getLaunch1();
                String lnch2 = menuList.get(position).getLaunch2();
                String dnnr1 = menuList.get(position).getDinner1();
                String dnnr2 = menuList.get(position).getDinner2();
                String runYn = menuList.get(position).getRunYN();

                //view 선언 및 초기화
                TextView breakfast1 = (TextView) view.findViewById(R.id.breakfast1);
                TextView breakfast2 = (TextView) view.findViewById(R.id.breakfast2);
                TextView launch1 = (TextView) view.findViewById(R.id.launch1);
                TextView launch2 = (TextView) view.findViewById(R.id.launch2);
                TextView dinner1 = (TextView) view.findViewById(R.id.dinner1);
                TextView dinner2 = (TextView) view.findViewById(R.id.dinner2);
                TextView menu_date = (TextView) view.findViewById(R.id.menu_date);


                //view 에 식단 주입

                breakfast1.setText(brfst1);
                breakfast2.setText(brfst2);
                launch1.setText(lnch1);
                launch2.setText(lnch2);
                dinner1.setText(dnnr1);
                dinner2.setText(dnnr2);


                //날짜 받아오기


                String year = date.substring(0, 4);
                String month = date.substring(4, 6);
                String date_of_month = date.substring(6, 8);
                String day_of_week;
                switch (position) {
                    case 0:
                        day_of_week = "월요일";
                        break;
                    case 1:
                        day_of_week = "화요일";
                        break;
                    case 2:
                        day_of_week = "수요일";
                        break;
                    case 3:
                        day_of_week = "목요일";
                        break;
                    case 4:
                        day_of_week = "금요일";
                        break;
                    default:
                        day_of_week = "";
                }

                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(year + "년 " + month + "월 " + date_of_month + "일 " + day_of_week);
                menu_date.setText(stringBuilder.toString());


                container.addView(view);

                return (view);
            } else {

                View view = inflater.inflate(R.layout.menu_chart_notrun, container, false);

                //String 선언 및 초기화
                String date = menuList.get(position).getMealDt();
                String brfst1 = menuList.get(position).getBreakfast1();
                String brfst2 = menuList.get(position).getBreakfast2();
                String lnch1 = menuList.get(position).getLaunch1();
                String lnch2 = menuList.get(position).getLaunch2();
                String dnnr1 = menuList.get(position).getDinner1();
                String dnnr2 = menuList.get(position).getDinner2();
                String runYn = menuList.get(position).getRunYN();

                //view 선언 및 초기화
                TextView breakfast1 = (TextView) view.findViewById(R.id.breakfast1);
                TextView breakfast2 = (TextView) view.findViewById(R.id.breakfast2);
                TextView launch1 = (TextView) view.findViewById(R.id.launch1);
                TextView launch2 = (TextView) view.findViewById(R.id.launch2);
                TextView dinner1 = (TextView) view.findViewById(R.id.dinner1);
                TextView dinner2 = (TextView) view.findViewById(R.id.dinner2);
                TextView menu_date = (TextView) view.findViewById(R.id.menu_date);
                TextView textview_notrun = (TextView) view.findViewById(R.id.textview_notrun);


                //view 에 식단 주입안함

                //textView 표출
                textview_notrun.setVisibility(View.VISIBLE);

                //날짜 받아오기


                String year = date.substring(0, 4);
                String month = date.substring(4, 6);
                String date_of_month = date.substring(6, 8);
                String day_of_week;
                switch (position) {
                    case 0:
                        day_of_week = "월요일";
                        break;
                    case 1:
                        day_of_week = "화요일";
                        break;
                    case 2:
                        day_of_week = "수요일";
                        break;
                    case 3:
                        day_of_week = "목요일";
                        break;
                    case 4:
                        day_of_week = "금요일";
                        break;
                    default:
                        day_of_week = "";
                }

                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(year + "년 " + month + "월 " + date_of_month + "일 " + day_of_week);
                menu_date.setText(stringBuilder.toString());


                container.addView(view);

                return (view);
            }
        }
    }
}
