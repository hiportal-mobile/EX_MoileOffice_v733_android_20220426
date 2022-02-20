package com.ex.group.folder.retrofitclient;

import com.ex.group.approval.easy.util.RetrieveAppInfo;
import com.ex.group.folder.TopicActivity;
import com.ex.group.folder.retrofitclient.pojo.ElecMemoAlarmInfo;
import com.ex.group.folder.retrofitclient.pojo.MemoNoticePopInfo;
import com.ex.group.folder.retrofitclient.pojo.MenuListInfo;
import com.ex.group.folder.retrofitclient.pojo.RequestInitInfo;
import com.ex.group.folder.retrofitclient.pojo.RequestLauncherInfo;
import com.ex.group.folder.retrofitclient.pojo.RequestLogin;
import com.ex.group.folder.retrofitclient.pojo.RequestMailCount;
import com.ex.group.folder.retrofitclient.pojo.RequestTrafficBusiness;
import com.ex.group.folder.retrofitclient.pojo.StackRunningAppLog;
import com.ex.group.folder.retrofitclient.pojo.TopicListInfo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

//Retrofit의 가장 큰 특징은 플러그인 방식이다.

//  https://shiny-corolla.tistory.com/48  글을 참고하면 도움이 된다.

public  interface APIInterface {

    @GET("retrieveAppInfo.do")
    Call<RetrieveAppInfo> doGetRetrieveAppInfo(
            @Query("userId") String userId,
            @Query("userType") String userType,
            @Query("platformCd") String platformCd,
            @Query("deviceType") String deviceType
    );

    @GET("images/fuurestudio-university-logo.png")
    Call<ResponseBody>downloadFile();


    @POST("retrieveLauncherInfo.do")   //런처의 업데이트 정보를 받아온다.
    Call<RequestLauncherInfo> request_launcher_info();


    @FormUrlEncoded
    @POST("retrieveLoginInfo.do")  //Login Activity로그인 인증
    Call<RequestLogin> request_user_info(@Field("userId") String userId, @Field("pwd") String pwd,@Field("platformCd") String platformCd
            ,@Field("mdn") String mdn,@Field("deviceNm") String deviceNm, @Field("deviceGubun") String deviceGubun);

    @FormUrlEncoded
    @POST("retrieveUnreadCount.do")  //Login Activity로그인 인증
    Call<RequestMailCount> request_unread_mail_count(@Field("userId") String userId, @Field("platformCd") String platformCd);

    @FormUrlEncoded
    @POST("mobilerelay/retrieveInitInfo.do")  // MainActivity 초기정보 조회 //운영
    Call<RequestInitInfo> request_initial_info(@Field("userId")String userId, @Field("userType")String userType, @Field("platformCd")String platformCd,//운영
                                               @Field("deviceType")String deviceType);//운영

    @FormUrlEncoded//테스트 test
    @POST("test_android.jsp")  // MainActivity 초기정보 조회//테스트 test
    Call<RequestInitInfo> request_initial_info_test(@Field("userId")String userId, @Field("userType")String userType, @Field("platformCd")String platformCd,//테스트 test
                                               @Field("deviceType")String deviceType);//테스트 test

    @FormUrlEncoded
    @POST("mobileAlarmCount.do")  // 전자결재, 메모보고 알림카운트 조회 JSJ write
    Call<ElecMemoAlarmInfo> request_elecmemo_alarm_count(@Field("userId")String userId);//JSJ write

    @FormUrlEncoded
    @POST("mobileElecWaitCount.do")  // 전자결재 대기함 카운트 조회 EJY write
    Call<ElecMemoAlarmInfo> request_elec_wait_count(@Field("userId")String userId);//EJY write

    @FormUrlEncoded
    @POST("mobileMemoWaitCount.do")  // 메모보고 대기함 카운트 조회 EJY write
    Call<ElecMemoAlarmInfo> request_memo_wait_count(@Field("userId")String userId);//EJY write

    @FormUrlEncoded
    @POST("retrieveExecuteAppLog.do")  //App 실행시, 서버에 접속 로그를 기록
    Call<StackRunningAppLog> stack_running_app_info(@Field("userId")String userId, @Field("deviceNm")String deviceNm, @Field("mdn")String mdn,
                                                   @Field("appId")String appId,@Field("deviceGubun")String deviceGubun, @Field("userType")String userType);

    @FormUrlEncoded
    @POST("retrieveTableSelection.do")//식단표를 1주일 단위로 조회해 온다.
    Call<MenuListInfo> menu_list_info(@Field("userId")String userId, @Field("deptCd")String deptCd, @Field("mealStDt")String mealStDt, @Field("mealEndDt")String mealEndDt);

    @FormUrlEncoded
    @POST("retrieveNews.do")  //  주요뉴스를 조회 해오는 것
    Call<TopicListInfo> topic_list_info(@Field("userId")String userId);

    @FormUrlEncoded
    @POST("retrieveTrafficSales.do")  // 교통/영업현황을 조회 하는 것
    Call<RequestTrafficBusiness> requestTrafficandBusiness(@Field("userId")String userId, @Field("salesDt")String salesDt);

    @FormUrlEncoded
    @POST("noticePopup.do")  // 메모보고 공지사항 조회 EJY write
    Call<MemoNoticePopInfo> request_notice_popup_info(@Field("userId")String userId);//EJY write
    //http://store.ex.co.kr/mobilerelay/noticePopup.do

    @FormUrlEncoded
    @POST("noticeAttflForPopup.do")  // 메모보고 공지사항 첨부파일 조회 EJY write
    Call<MemoNoticePopInfo> request_notice_attfl_popup_info(@Field("attflSeq")String userId);//EJY write
    //http://store.ex.co.kr/mobilerelay/noticeAttflForPopup.do?attflSeq=10

}
