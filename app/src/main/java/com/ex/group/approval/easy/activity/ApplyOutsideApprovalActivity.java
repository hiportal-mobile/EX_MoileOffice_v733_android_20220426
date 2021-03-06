package com.ex.group.approval.easy.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.folder.R;
import com.ex.group.approval.easy.constant.ApprovalConstant;
import com.ex.group.approval.easy.dialog.WheelDatePickerDialogHelper;
import com.ex.group.approval.easy.dialog.WheelTimePickerDialogHelper;
import com.ex.group.approval.easy.dialog.ifaces.PEDialogInterface;
import com.ex.group.approval.easy.primitive.DraftFormPrimitive;
import com.ex.group.approval.easy.primitive.DraftPrimitive;
import com.ex.group.approval.easy.util.ActivityLauncher;
import com.ex.group.approval.easy.widget.CodeSpinner;
import com.ex.group.mail.data.EmailFileListData;
import com.skt.pe.common.activity.ifaces.CommonUI;
import com.skt.pe.common.conf.Environ;
import com.skt.pe.common.conf.EnvironManager;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.dialog.SKTDialog;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.primitive.Primitive;
import com.skt.pe.common.primitive.util.PrimitiveList;
import com.skt.pe.common.service.Parameters;
import com.skt.pe.common.widget.MemberEditText;
import com.skt.pe.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class ApplyOutsideApprovalActivity extends ApprovalCommonActivity implements OnClickListener, CommonUI {
    private final String TAG = "ApplOutActivity";
    private Button selectDate = null;
    private Button selectStartTime = null;
    private Button selectEndTime = null;
    private DraftFormPrimitive dformPrim = null;
    private DraftPrimitive draftPrim = null;

    private LinearLayout layout_occasion; // ??????, ????????? Hidden Layout

    private final static String activityTitle = "???????????????";

    private CodeSpinner csFormCode;

    // kbr 2022.04.18
    private final int UPLOAD_FILE = 1006;
    private ArrayList<EmailFileListData> uploadList = new ArrayList<EmailFileListData>();


    // 2015-06-26 Join ?????? - ?????? ??? ?????? ?????? ??????
//	private String[] childName;
//	private String[] childJmdrbeonho;
    // 2015-06-26 Join ?????? ???

    @Override
    protected int assignLayout() {
        return R.layout.easy_apply_outside;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onCreateX(Bundle savedInstanceState) {
        setSubTitle(activityTitle);

        Log.d("ApplyOutsideApprovalActivity", "ApplyOutsideApprovalActivity onCreaet");
        Intent intent = getIntent();
        dformPrim = (DraftFormPrimitive) intent.getSerializableExtra(ApprovalConstant.IntentArg.PRIMITIVE);
        Log.d(TAG, "onCreateX() - dformPrim : " + dformPrim);
        Map<String, Object> o = (Map<String, Object>) getLastNonConfigurationInstance();
        if (o != null) {
            draftPrim = (DraftPrimitive) o.get("primitive");
        } else {
            draftPrim = new DraftPrimitive();
        }

        layout_occasion = (LinearLayout) findViewById(R.id.layout_occasion);

        draftPrim.setDraftKind(DraftPrimitive.DraftKind.OUTSIDE);

        getEmpInfo();
    }


    //?????? ?????? ????????? ?????? ??????
    public void getEmpInfo() {
        Parameters params = new Parameters(COMMON_APPROVALSTAGING_RESTFULCLIENT);
        try {
            params.put("S_API_URL", "/searchOutEmpInfo");
            params.put("S_APP_URL", "/searchOutEmpInfo");
            params.put("S_EMP_ID", "" + SKTUtil.getGMPAuth(ApplyOutsideApprovalActivity.this).get(AuthData.ID_ID));
//			params.put("S_EMP_ID", "19805014");

		/*	//??????????????????
			Map<String, String> gmpAuth = SKTUtil.getGMPAuthPwd(ApplyVacationApprovalActivity.this);
			params.put("authKey", URLEncoder.encode((String)gmpAuth.get("AUTHKEY")) + "&");
			params.put("companyCd", URLEncoder.encode((String)gmpAuth.get("COMPANY_CD")) + "&");
			params.put("encPwd", URLEncoder.encode((String)gmpAuth.get("ENC_PWD")) + "&");*/

            Environ environ = EnvironManager.getEnviron(ApplyOutsideApprovalActivity.this);
            String url = environ.getProtocol() + "://" + environ.getHost() + ":" + environ.getPort() + environ.FILE_ATTACHMENT + "?" + params.toString() + "&" + environ.getEnvironParam();

            new JsonAction(COMMON_SEARCHOUTEMPINFO, url, this).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //???????????? ????????????
    public void getsearchOutcheck() {
        Parameters params = new Parameters(COMMON_APPROVALSTAGING_RESTFULCLIENT);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat format_time = new SimpleDateFormat("HHmm");

            params.put("S_API_URL", "/searchOutCheck");
            params.put("S_APP_URL", "/searchOutCheck");
            params.put("S_EMP_ID", "" + SKTUtil.getGMPAuth(ApplyOutsideApprovalActivity.this).get(AuthData.ID_ID));
            params.put("S_ATTEND_CD", csFormCode.getValue());
            params.put("S_OUTNG_YMD", format.format(draftPrim.getTargetDate()));
            params.put("S_OUTNG_STA_HM", format_time.format(draftPrim.getFromTime()));
            params.put("S_OUTNG_END_HM", format_time.format(draftPrim.getUntilTime()));
            params.put("S_COMP_EMP_ID", S_COMP_EMP_ID);
            params.put("S_OUTNG_HOUR_CHILD_PER_NO", "");

            params.put("S_ATTEND_APPL_YMD", format.format(new Date()));
            params.put("S_NOTE", draftPrim.getDescript());
            params.put("S_OUTNG_PLACE", draftPrim.getLocation());
            params.put("S_PROC_CLASS", "");

            // kbr 2022.04.22 - ????????? ?????? ??????
            if (uploadList.size() > 0) {
                params.put("S_FILE_INFO", uploadList.get(0).getM_szContent());  // ????????? (?????? ??????)
            }

            /*
            * kbr 2022.04.20
            * params????????? multipart??? ??????????????????.
            * */


//			params.put("S_EMP_ID", "19805014");//?????????????????? ?????????(????????? ????????? ????????? ?????? ????????? ????????? ??????)
            Environ environ = EnvironManager.getEnviron(ApplyOutsideApprovalActivity.this);
            String url = environ.getProtocol() + "://" + environ.getHost() + ":" + environ.getPort() + environ.FILE_ATTACHMENT + "?" + params.toString() + "&" + environ.getEnvironParam();
            draftPrim.setUrl(url);
            /*
                http://128.200.121.68:9000/emp_ex/service.pe?
                primitive=COMMON_APPROVALSTAGING_RESTFULCLIENTSSL
                S_OUTNG_END_HM=2100
                S_ATTEND_CD=22100
                S_COMP_EMP_ID=
                S_OUTNG_STA_HM=1800
                S_APP_URL=%2FsearchOutCheck
                S_EMP_ID=99999999
                S_OUTNG_PLACE=test1
                S_OUTNG_YMD=20220419
                S_OUTNG_HOUR_CHILD_PER_NO=
                S_ATTEND_APPL_YMD=20220419
                S_API_URL=%2FsearchOutCheck
                S_NOTE=test1
                S_PROC_CLASS=
                mdn=01073939080
                appId=null
                appVer=5.2.3
                lang=ko
                groupCd=EX
            */
            //2021.07 1???????????????(??????)
            System.out.println("---------------------------POST_NM : " + POST_NM);
            String postNm = POST_NM.replaceAll("???", "");
            draftPrim.setPOST_NM(postNm);

            if (url.contains("S_FILE_INFO")) { // ????????? ?????? ??????
                new JsonAction(COMMON_SEARCHOUTCHECK, url, this, params.get("S_FILE_INFO")).execute();
            } else {    // ????????? ?????? ??????
                new JsonAction(COMMON_SEARCHOUTCHECK, url, this).execute();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    String EMP_ID = "";
    String EMP_NM = "";//?????????
    String DPT_NM = "";//?????????
    String EMPGRADE_NM = "";//?????????
    String POST_NM = "";//?????????
    String OFFI_OUT_ACC_TIME = "";//??????????????????
    String[] CHILD_PER_NO_LIST = null;//???????????????????????? ??????
    String[] CHILD_NM_LIST = null;//???????????? ?????????
    String[] OUTNG_HOUR_CHILD_PER_NO_LIST = null;//????????????????????????

    String[] outname = null;
    String[] outcode = null;
    String[] outname_nochild = null;
    String[] outcode_nochild = null;


    String S_COMP_EMP_ID = "";//?????????

    String S_PROC_CLASS = "";

    //2021.07 ???????????? ???????????? ????????????
    String GEN_TIME_TEXT = "";
    String GEN_TIME_YN = "";
    String GEN_TIME_MIN = "";


    @Override
    protected void onJsonActionPost(String primitive, String result) {
        Log.d("onJsonActionPost", " primitive = " + primitive);
        Log.d("onJsonActionPost", " result = " + result);
        try {
            if (primitive.equals(COMMON_SEARCHOUTEMPINFO)) {
				/*{"Data":[{"CHILD_NM_LIST":"?????????,?????????","CHILD_PER_NO_LIST":"120124-*******,150609-*******","EMP_GRADE_NM":"3???","OUTNG_HOUR_CHILD_PER_NO_LIST":"20120124,20150609","EMP_ID":"19805014","DPTNM":"????????? ???????????????","EMP_NM"
				:"?????????","POST_NM":"??????", "ACC_TIME":"4??????30???"}],"Etc":[],"Msg":[{"result":"1000","retMsg":""}]}*/
                /*{"Data":[{"CHILD_NM_LIST":"","CHILD_PER_NO_LIST":"","EMP_GRADE_NM":"4???","OUTNG_HOUR_CHILD_PER_NO_LIST":"","EMP_ID":"21603226","DPTNM":"????????? ???????????????","EMP_NM":"?????????","POST_NM":"??????","ACC_TIME":"8??????0???"}],"Etc":[],"Msg":[{"result":"1000","retMsg":""}]}*/

                JSONObject obj = new JSONObject(result);
                JSONArray jsonArray = obj.getJSONArray("Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject temp = jsonArray.getJSONObject(i);

                    EMP_NM = temp.getString("EMP_NM");
                    DPT_NM = temp.getString("DPTNM");
                    EMPGRADE_NM = temp.getString("EMP_GRADE_NM");
                    POST_NM = temp.getString("POST_NM");
                    OFFI_OUT_ACC_TIME = temp.getString("ACC_TIME");
                    CHILD_PER_NO_LIST = temp.getString("CHILD_PER_NO_LIST").split(",");
                    CHILD_NM_LIST = temp.getString("CHILD_NM_LIST").split(",");
                    OUTNG_HOUR_CHILD_PER_NO_LIST = temp.getString("OUTNG_HOUR_CHILD_PER_NO_LIST").split(",");
                    Log.d("onJsonActionPost", " result = 1");
                    draftPrim.setEmpGradeNm(EMPGRADE_NM.replace("???", ""));
                    //2021.06 ?????????????????? ????????????
                    GEN_TIME_TEXT = temp.getString("GEN_TIME_TEXT");
                    GEN_TIME_YN = temp.getString("GEN_TIME_YN");
                    GEN_TIME_MIN = temp.getString("GEN_TIME_MIN");

                    break;
                }


                outname = obj.getString("outname").split(",");
                outcode = obj.getString("outcode").split(",");
                outname_nochild = obj.getString("outname_nochild").split(",");
                outcode_nochild = obj.getString("outcode_nochild").split(",");

                Log.d("onJsonActionPost", " result = 2");
                initUI();
                Log.d("onJsonActionPost", " result = 3");
            } else {    // ?????? ???????????? ??????
                if (primitive.equals(COMMON_SEARCHOUTCHECK)) {
                    JSONObject obj = new JSONObject(result);
                    JSONArray jsonArray = obj.getJSONArray("Etc");
                    JSONArray jsonArray_msg = obj.getJSONArray("Msg");
                    String msg_result = "";

                    for (int i = 0; i < jsonArray_msg.length(); i++) {
                        JSONObject temp = jsonArray_msg.getJSONObject(i);
                        msg_result = temp.getString("result");
                    }

                    if (msg_result.equals("1000")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject temp = jsonArray.getJSONObject(i);
                            S_PROC_CLASS = temp.getString("PROC_CLASS");
                            draftPrim.setPROC_CLASS(S_PROC_CLASS);

                            Parameters params = new Parameters(COMMON_APPROVALSTAGING_RESTFULCLIENT);
                            try {
                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                                SimpleDateFormat format_time = new SimpleDateFormat("HHmm");

                                params.put("S_EMP_ID", "" + SKTUtil.getGMPAuth(ApplyOutsideApprovalActivity.this).get(AuthData.ID_ID));
                                params.put("S_ATTEND_CD", csFormCode.getValue());
                                params.put("S_OUTNG_YMD", format.format(draftPrim.getTargetDate()));
                                params.put("S_OUTNG_STA_HM", format_time.format(draftPrim.getFromTime()));
                                params.put("S_OUTNG_END_HM", format_time.format(draftPrim.getUntilTime()));

                                params.put("S_COMP_EMP_ID", S_COMP_EMP_ID);
                                params.put("S_OUTNG_HOUR_CHILD_PER_NO", "");

                                params.put("S_ATTEND_APPL_YMD", format.format(new Date()));
                                params.put("S_NOTE", draftPrim.getDescript());
                                params.put("S_OUTNG_PLACE", draftPrim.getLocation());
                                params.put("S_PROC_CLASS", S_PROC_CLASS);
                                params.put("S_API_URL", "/saveOutAppl");
                                params.put("S_APP_URL", "/saveOutAppl");

                                Environ environ = EnvironManager.getEnviron(ApplyOutsideApprovalActivity.this);
                                String url = environ.getProtocol() + "://" + environ.getHost() + ":" + environ.getPort() + environ.FILE_ATTACHMENT + "?" + params.toString() + "&" + environ.getEnvironParam();
                                draftPrim.setUrl(url);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }

                        String message = validationUI();
                        if (message == null) {
                            ActivityLauncher.launchApprovalLine(ApplyOutsideApprovalActivity.this, draftPrim, ApprovalConstant.EasyApprovalType.OUTSIDE, activityTitle);
                        } else {
                            alert(message);
                        }
                    } else {
                        JSONObject temp_obj = (JSONObject) jsonArray_msg.get(0);

                        alert("??????", temp_obj.getString("retMsg"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }, false);

                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     * ?????? ???????????? ??? List ???????????? ???????????? ????????? ??????...
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("primitive", draftPrim);

        return map;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onReceive(Primitive primitive, SKTException e) {
        super.onReceive(primitive, e);
    }

    // kbr 2022.04.18
    private void setUploadUI() {
        if (uploadList != null && uploadList.size() > 0) {
            findViewById(R.id.upload_fileLayout).setVisibility(View.VISIBLE);
            final LinearLayout layout = (LinearLayout) findViewById(R.id.upload_FILEATTLIST);
            layout.removeAllViews();
            for (int i = 0; i < uploadList.size(); i++) {
                LinearLayout tempLayout = (LinearLayout) LayoutInflater.from(
                        this).inflate(R.layout.mail_file_upload_list_item, null);
                final Button fileCheckBtn = (Button) tempLayout
                        .findViewById(R.id.FILE_CHECK_BUTTON);
                if (uploadList.get(i).getM_szIsEcm().equals("true")) {
                    fileCheckBtn.setBackground(getResources().getDrawable(R.drawable.mail_check_on));
                } else {
                    fileCheckBtn.setBackground(getResources().getDrawable(R.drawable.mail_check_off));
                }
                final int fileIndex = i;
                fileCheckBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (uploadList.get(fileIndex).getM_szIsEcm()
                                .equals("true")) {
                            uploadList.remove(fileIndex);
                            setUploadUI();

                        } else {
                            uploadList.get(fileIndex).setM_szIsEcm("true");
                            fileCheckBtn.setBackground(getResources().getDrawable(R.drawable.mail_check_on));
                        }
                    }
                });
                TextView txtFileName = (TextView) tempLayout
                        .findViewById(R.id.FILE_NAME);
                txtFileName.setText(uploadList.get(i).getM_szName());
                layout.addView(tempLayout);
            }
        } else {
            findViewById(R.id.upload_fileLayout).setVisibility(View.GONE);
        }
    }

    private String[] getRealFilePath(Uri uriPath) {

        Cursor cursor = getContentResolver().query(uriPath, null, null, null, null);
        int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();

        String fullPath = cursor.getString(index); // ????????? ?????? ??????
        String fileName = fullPath.substring(fullPath.lastIndexOf("/") + 1);

        return new String[] { fullPath, fileName };
    }

    @Override
    protected void onActivityResultX(int requestCode, int resultCode, Intent intent) {
        super.onActivityResultX(requestCode, resultCode, intent);
        /*
         * ????????? ????????? ?????? ?????? ??? ?????? ??????...
         */
        Log.d("onActivityResultX", "onActivityResultX " + requestCode + ":" + resultCode);


        if (requestCode == ApprovalConstant.RequestCode.FINISH) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        } else if (requestCode == ApprovalConstant.RequestCode.SELECT_COOPERATOR) {
            if (intent != null) {
                String[] names = null;
                String[] empIds = null;

                names = (String[]) intent.getStringArrayExtra("names");
                empIds = (String[]) intent.getStringArrayExtra("empids");

                for (int i = 0; i < names.length; i++) {
                    SKTUtil.log("TEST", "(" + i + ")" + names[i] + ": " + empIds[i]);
                    Log.d(TAG, "(" + i + ") " + names[i] + " : " + empIds[i]);
                    if ("".equals(S_COMP_EMP_ID)) {
                        S_COMP_EMP_ID = empIds[i];
                    } else {
                        S_COMP_EMP_ID = S_COMP_EMP_ID + "," + empIds[i];
                    }
                }

                MemberEditText tvCooperator = (MemberEditText) findViewById(R.id.apply_outside_textview_cooperator);
                for (int i = 0; i < names.length; i++) {
                    if (tvCooperator.isExistValue(empIds[i]) == false)
                        tvCooperator.addText(names[i], empIds[i]);
                }

                tvCooperator.requestFocus();
                tvCooperator.setText(tvCooperator.toString());
//				tvCooperator.setText(tvCooperator.toShortString());
            }
        /*
        * kbr 2022.04.18
        * ?????? ??????
        * */
        } else if (requestCode == UPLOAD_FILE && resultCode == RESULT_OK) {
            if (intent.getClipData() == null) {     // 1???
                Log.d(TAG, " - onActivityResultX : single choice");
                Uri fileUri = intent.getData();
                if (StringUtil.isNull(fileUri.getPath())) {
                    return;
                }
                String[] file = getRealFilePath(fileUri); // {?????? ?????? ??????, ?????? ??????}
                for (int a = 0; a < uploadList.size(); a++) {
                    String uploadPath = uploadList.get(a).getM_szContent(); // getM_szContent() : ?????? ?????? ??????(?????? ??????)
                    if (file[0].equals(uploadPath)) {
                        Toast.makeText(this, "?????? ????????? ?????? ????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                EmailFileListData filelist = new EmailFileListData();
                filelist.setM_szName(file[1]);
                filelist.setM_szContent(file[0]);
                filelist.setM_szIsEcm("true");

                uploadList.add(filelist);
            } else {    // 2??? ??????
                Log.d(TAG, " - onActivityResultX : multiple choice");
                ClipData clipData = intent.getClipData();

                if (clipData.getItemCount() + uploadList.size() > 20) {
                    Toast.makeText(this, "?????? ????????? 1?????? ???????????????.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Uri fileUri = null;
                    String[] file = null;
                    int dupNum = 0;
                    boolean dupChk;
                    for (int a = 0; a < clipData.getItemCount(); a++) {
                        dupChk = false;
                        fileUri = clipData.getItemAt(a).getUri();
                        if (StringUtil.isNull(fileUri.getPath())) {
                            continue;
                        }
                        file = getRealFilePath(fileUri); // {?????? ?????? ??????, ?????? ??????}
                        Log.d("EmailWriteActivity", " - " + dupNum + " file path : " + file[0]);
                        Log.d("EmailWriteActivity", " - " + dupNum + " file name : " + file[1]);
                        for (int b = 0; b < uploadList.size(); b++) {
                            String uploadPath = uploadList.get(b).getM_szContent(); // getM_szContent() : ?????? ?????? ??????(?????? ??????)
                            if (file[0].equals(uploadPath)) {
                                ++dupNum;
                                dupChk = true;
                                break;
                            }
                        }
                        if (!dupChk) {
                            EmailFileListData filelist = new EmailFileListData();
                            filelist.setM_szName(file[1]);
                            filelist.setM_szContent(file[0]);
                            filelist.setM_szIsEcm("true");

                            Log.d("EmailWriteActivity", " - " + dupNum + " file list content : " + filelist.getM_szContent());
                            Log.d("EmailWriteActivity", " - " + dupNum + " file list name : " + filelist.getM_szName());
                            Log.d("EmailWriteActivity", " - " + dupNum + " file list isecm : " + filelist.getM_szIsEcm());

                            uploadList.add(filelist);
                        }
                    }
                    if (dupNum > 0) {
                        Toast.makeText(this, "?????? ????????? ????????? ????????? " + dupNum + "??? ????????????.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            setUploadUI();
            Log.d("EmailWriteActivity", " - uploadList : " + uploadList.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // ????????? ?????? = ????????? ??????
            case R.id.apply_outside_button_membersearch:
                // 0-??????, 1-??????, 2-??????, 3-??????, 4-??????
                if ("3".equals(csFormCode.getValue()) || "??????".equals(csFormCode.getValue())) {
                    Toast.makeText(ApplyOutsideApprovalActivity.this, "??????????????? ?????? ????????? ???????????? ????????? ????????? ?????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }

                super.selectMemberSearch(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityLauncher.launchMemberSearch(ApplyOutsideApprovalActivity.this, getSelectMemberSearchType(), ActivityLauncher.MemberSelectType.MULTI, ApprovalConstant.RequestCode.SELECT_COOPERATOR);
                    }
                }, "cooperator");
                break;
            case R.id.apply_outside_button_target_date:
                super.selectDate(new PEDialogInterface.OnClickListener() {

                    @Override
                    public void onClick(Object date, String formattedDate) {
                        selectDate.setText(formattedDate);
                        draftPrim.setTargetDate((Date) date);
                    }
                }, draftPrim.getTargetDate());
                break;
            case R.id.apply_outside_button_starttime:
                super.selectTime(new PEDialogInterface.OnClickListener() {
                    @Override
                    public void onClick(Object date, String formattedDate) {
					/*if (((Date) date).compareTo(draftPrim.getUntilTime()) >= 0) {
						alert("?????? ??????????????? ???????????? ???????????? ??????????????? ????????????.");
					} else {
						selectStartTime.setText(formattedDate);
						selectEndTime.setText(formattedDate);
						// 2015-03-09 Join ?????? ?????? - ?????? ??????????????? ???????????? ?????? ??????????????? ?????? ??????????????? ???????????? ??????
						draftPrim.setFromTime((Date)date);
						draftPrim.setUntilTime((Date)date);
						// 2015-03-09 Join ?????? ???
					}*/
                        selectStartTime.setText(formattedDate);
                        selectEndTime.setText(formattedDate);

                        // 2015-03-09 Join ?????? ?????? - ?????? ??????????????? ???????????? ?????? ??????????????? ?????? ??????????????? ???????????? ??????
                        draftPrim.setFromTime((Date) date);
                        draftPrim.setUntilTime((Date) date);
                        // 2015-03-09 Join ?????? ???
                    }
                }, draftPrim.getFromTime());
                break;
            case R.id.apply_outside_button_endtime:
                super.selectTime(new PEDialogInterface.OnClickListener() {
                    @Override
                    public void onClick(Object date, String formattedDate) {
                        if (draftPrim.getFromTime().compareTo((Date) date) >= 0) {
                            alert("?????? ??????????????? ???????????? ????????? ??????????????? ????????????.");
                        } else {
                            selectEndTime.setText(formattedDate);

                            //2021.07 ?????????????????? ????????????
                            System.out.println("???????????? == " + (Date) date);

                            draftPrim.setUntilTime((Date) date);
                        }
                    }
                }, draftPrim.getUntilTime());
                break;
            case R.id.apply_outside_button_ok:

                // 2015-06-26 Join ?????? - ?????? ??? ?????? ?????? ??????
                // 0-??????, 1-??????, 2-??????, 3-??????, 4-??????
//			if("3".equals(csFormCode.getValue()) || "??????".equals(csFormCode.getValue())) {
//				Toast.makeText(ApplyOutsideApprovalActivity.this, "?????? ???????????? ?????? Dialog!!", Toast.LENGTH_SHORT).show();
//			}
                // 2015-06-26 Join ?????? ???

                //2021.07 ?????????????????? ????????????
                SimpleDateFormat format_time_h = new SimpleDateFormat("HH");
                SimpleDateFormat format_time_m = new SimpleDateFormat("mm");

                long startTimeH = 0;
                long endTimeH = 0;
                long startTimeM = 0;
                long endTimeM = 0;
                long hour = 60;

                long hourGap = 0;
                long minuteGap = 0;

                long totalTime = 0;

                long genTime = 0;


                if ("Y".equals(GEN_TIME_YN)) {
                    startTimeH = Long.parseLong(format_time_h.format(draftPrim.getFromTime()));
                    endTimeH = Long.parseLong(format_time_h.format(draftPrim.getUntilTime()));
                    startTimeM = Long.parseLong(format_time_m.format(draftPrim.getFromTime()));
                    endTimeM = Long.parseLong(format_time_m.format(draftPrim.getUntilTime()));
                    hour = 60;

                    hourGap = (endTimeH - startTimeH) * hour;
                    minuteGap = endTimeM - startTimeM;
                    totalTime = hourGap + minuteGap;
                    genTime = Long.parseLong(GEN_TIME_MIN);
                }

                // 2021.09 test ?????????????????? ???????????? ?????? ????????? ????????????
                // GEN_TIME_YN = "N";
                if (genTime > 10) {
                    if ("Y".equals(GEN_TIME_YN)) {
                        if ("22200".equals(csFormCode.getValue()) || "??????".equals(csFormCode.getValue())) {
                            //Toast.makeText(ApplyOutsideApprovalActivity.this, "???????????? ??????????????? ?????? ?????? ??????(????????????)????????? ??????????????????.", Toast.LENGTH_LONG).show();
                            alert("???????????? ??????????????? ?????? ?????? ??????(????????????)????????? ??????????????????.");
                            return;
                        } else if (totalTime > genTime) {
                            //Toast.makeText(ApplyOutsideApprovalActivity.this, "???????????? ?????????????????? ??????????????? ???????????????. ????????? ?????? ??????????????????.", Toast.LENGTH_LONG).show();
                            System.out.println("================================================= ?????? ");
                            alert("???????????? ?????????????????? ???????????? ??????????????? ???????????????. \n????????? ?????????????????????.");
                            return;
                        }
                    }
                } else {
                    if ("22800".equals(csFormCode.getValue()) || "??????(????????????)".equals(csFormCode.getValue())) {
                        alert("??????(????????????)??? ???????????? ??? ????????????. \n????????? ????????? ?????????. ");
                        //Toast.makeText(ApplyOutsideApprovalActivity.this, "??????????????? ????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        String message = validationUI();
                        if (message == null) {
                            Log.e("?????????123", "234234");
                            getsearchOutcheck();
                        } else {
                            alert(message);
                        }

                    }
                }

                String message = validationUI();
                if (message == null) {
                    Log.e("?????????123", "345345");

                    /*
                     * kbr 2022.04.19
                     * ??? ????????? ?????? validation??? ?????? ????????? ????????? ????????? ?????? ????????? ?????? ??? ????????? ????????? ????????? ????????????.
                     * */
                    getsearchOutcheck();
                } else {
                    alert(message);
                }

                break;

            case R.id.apply_outside_button_cancel:
                finish();
                break;

            //2021.08 ?????????????????? ????????????
		/*case R.id.apply_outside_button_use_owncar_yes:
		case R.id.apply_outside_textview_use_owncar_yes:
			selectUserOwnCarButton(true);
			break;
		case R.id.apply_outside_button_use_owncar_no:
		case R.id.apply_outside_textview_use_owncar_no:
			selectUserOwnCarButton(false);
			break;*/

            // kbr 2022.04.18
            case R.id.apply_outside_button_file:
                if (uploadList.size() < 1) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");

                    startActivityForResult(intent, UPLOAD_FILE);
                } else {
                    SKTDialog d = new SKTDialog(this);
                    d.getDialog("?????? ????????? 1?????? ???????????????.").show();
                }

                break;

            case R.id.FILE_CHECK_BUTTON:
                Log.d(TAG, "++++++++++++++++++++++++++++ file check button");
                setUploadUI();
                break;
        } // switch
    }

    @Override
    public void initUI() {
        // ????????? ??????
        Button btn1 = (Button) findViewById(R.id.apply_outside_button_membersearch);
        btn1.setOnClickListener(this);

        // ????????? ??????
        initDateUI();

        Button btnOk = (Button) findViewById(R.id.apply_outside_button_ok);
        btnOk.setOnClickListener(this);
        Button btnCancel = (Button) findViewById(R.id.apply_outside_button_cancel);
        btnCancel.setOnClickListener(this);

        // kbr 2022.04.18
        Button btnFile = (Button) findViewById(R.id.apply_outside_button_file);
        btnFile.setOnClickListener(this);

        //2021.08 ?????????????????? ????????????
	/*	Button btnYes = (Button) findViewById(R.id.apply_outside_button_use_owncar_yes);
		TextView tvYes = (TextView) findViewById(R.id.apply_outside_textview_use_owncar_yes);
		btnYes.setOnClickListener(this);
		tvYes.setOnClickListener(this);
		Button btnNo = (Button) findViewById(R.id.apply_outside_button_use_owncar_no);
		TextView tvNo = (TextView) findViewById(R.id.apply_outside_textview_use_owncar_no);
		btnNo.setOnClickListener(this);
		tvNo.setOnClickListener(this)*/
        ;

        TextView myName = (TextView) findViewById(R.id.apply_outside_textview_myname);
        myName.setText(EMP_NM);

        TextView outDate = (TextView) findViewById(R.id.apply_outside_textview_outdate);
        outDate.setText(OFFI_OUT_ACC_TIME);

        //2021.06 ?????????????????? ????????????
        TextView genTime = (TextView) findViewById(R.id.apply_outside_textview_gentime);
        genTime.setText(GEN_TIME_TEXT);


		/*
		TextView carNumber = (TextView) findViewById(R.id.apply_outside_textview_car_number);
		carNumber.setText(dformPrim.getFormInfo().getCarNumber());*/

        /*
         * formCode????????? ????????? ?????? ???????????? '??????' ????????? ?????? ????????? Spinner??? ??????
         */

        csFormCode = (CodeSpinner) findViewById(R.id.apply_outside_spinner_formcode);


        final String[] formCodeValues;
        final String[] formCodeCodes;
        if (null == OUTNG_HOUR_CHILD_PER_NO_LIST || OUTNG_HOUR_CHILD_PER_NO_LIST.length > 0) {
//			formCodeValues = getResources().getStringArray(R.array.easyaproval_apply_outside_spinner_formcode_name);
//			formCodeCodes = getResources().getStringArray(R.array.easyaproval_apply_outside_spinner_formcode_code);

            formCodeValues = outname;
            formCodeCodes = outcode;
            // 2015-06-26 Join ?????? - ?????? ??? ?????? ?????? ??????
//			childName = dformPrim.getFormInfo().getChildName().split("@");
//			childJmdrbeonho = dformPrim.getFormInfo().getChildJmdrbeonho().split("@");
            // 2015-06-26 Join ?????? ???
        } else {
//			formCodeValues = getResources().getStringArray(R.array.easyaproval_apply_outside_spinner_formcode_name_without_child);
//			formCodeCodes = getResources().getStringArray(R.array.easyaproval_apply_outside_spinner_formcode_code_without_child);
            formCodeValues = outname_nochild;
            formCodeCodes = outcode_nochild;
        }

        //2021.06 ?????????????????? ???????????? TEST
        csFormCode.setValues(formCodeValues, formCodeCodes);

        // 2021.11 ??????, ???????????? ????????? VISIBLE ??????
        /*
        * 0 : ??????    22100
        * 1 : ??????    22200
        * 2 : ??????    22300
        * 3 : ??????    22600
        * 4 : ??????(????????????)  22800
        * */
        csFormCode.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                if (i == 1 || i == 4) {
                if (csFormCode.getCode(i).equals("22200") || csFormCode.getCode(i).equals("22800")) {
                    layout_occasion.setVisibility(View.GONE);
                } else {
                    layout_occasion.setVisibility(View.VISIBLE);
                }
            }
        });

        MemberEditText cooperator = (MemberEditText) findViewById(R.id.apply_outside_textview_cooperator);
        //2021.08 ???????????????????????????
        //selectUserOwnCarButton(false);
    }

    private void initDateUI() {
        Date currentDate = new Date(System.currentTimeMillis());
        selectDate = (Button) findViewById(R.id.apply_outside_button_target_date);
        selectDate.setOnClickListener(this);

        selectDate.setText(WheelDatePickerDialogHelper.generateDateFormat(currentDate));
        draftPrim.setTargetDate(currentDate);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);
        Date fromTime = (new GregorianCalendar(0, 0, 0, currentCalendar.get(Calendar.HOUR_OF_DAY) + 1, 0, 0)).getTime();
        selectStartTime = (Button) findViewById(R.id.apply_outside_button_starttime);
        selectStartTime.setOnClickListener(this);

        selectStartTime.setText(WheelTimePickerDialogHelper.generateDateFormat(fromTime));
        draftPrim.setFromTime(fromTime);

        int untilHour = currentCalendar.get(Calendar.HOUR_OF_DAY) + 4;
        int untilMinute = 0;
        if (untilHour > 23) {
            untilHour = 23;
            untilMinute = 30;
        }

        Date untilTime = (new GregorianCalendar(0, 0, 0, untilHour > 23 ? 23 : untilHour, untilMinute, 0)).getTime();
        selectEndTime = (Button) findViewById(R.id.apply_outside_button_endtime);
        selectEndTime.setOnClickListener(this);

        selectEndTime.setText(WheelTimePickerDialogHelper.generateDateFormat(untilTime));
        draftPrim.setUntilTime(untilTime);
    }

    private void selectUserOwnCarButton(boolean isYes) {
        //2021.08 ?????????????????? ????????????
		/*Button yesButton = (Button) findViewById(R.id.apply_outside_button_use_owncar_yes);
		Button noButton = (Button) findViewById(R.id.apply_outside_button_use_owncar_no);
		

//		 * ???????????? ????????? ????????? ????????????????????? false??? ?????????.

		if (isYes == true *//*&& dformPrim.getFormInfo().getCarNumber().length() > 0*//*) {
			yesButton.setBackgroundResource(R.drawable.easy_radio_on);
			noButton.setBackgroundResource(R.drawable.easy_radio_off);
			draftPrim.setIsOwnCar(true);
		} else {
			yesButton.setBackgroundResource(R.drawable.easy_radio_off);
			noButton.setBackgroundResource(R.drawable.easy_radio_on);
			draftPrim.setIsOwnCar(false);
		}*/
    }

    @Override
    public void resetUI() {
    }

    @Override
    public String validationUI() {
        return setDraftPrimitive();
    }

    private String setDraftPrimitive() {
        // ????????????
        CodeSpinner formCode = (CodeSpinner) findViewById(R.id.apply_outside_spinner_formcode);
        draftPrim.setFormCode(formCode.getValue());
        draftPrim.setTag(DraftPrimitive.Tags.FORM_CODE_NAME, formCode.getText().toString());


        // ??????
		/* EditText descript = (EditText) findViewById(R.id.apply_outside_edittext_descript);
		String descriptText = descript.getText().toString();
		if (descript.getText().toString().trim().length() == 0)
				return "????????? ???????????? ???????????????.";
*/
        // 2021.09 ?????? / ??????(????????????) ?????? ?????? ???????????? ???????????? ????????????
        EditText descript = (EditText) findViewById(R.id.apply_outside_edittext_descript);
        String descriptText = descript.getText().toString();

        if (descript.getText().toString().trim().length() == 0) {
            if (!"22800".equals(csFormCode.getValue()) && !"22200".equals(csFormCode.getValue())) {
                System.out.println("------------- ?????? : " + csFormCode.getValue().toString());
                return "????????? ???????????? ???????????????.";
            } else {
                System.out.println("------------------ descriptText : " + descriptText);
                descriptText = " ";
            }
        }

        /*draftPrim.setDescript(descript.getText().toString());*/
        draftPrim.setDescript(descriptText);

        // ?????????
        EditText location = (EditText) findViewById(R.id.apply_outside_edittext_location);
        String locationText = location.getText().toString();
		 /*if (location.getText().toString().trim().length() == 0)
			return "???????????? ???????????? ???????????????.";*/
        // 2021.09 ?????? / ??????(????????????) ?????? ?????? ????????? ???????????? ????????????
        if (location.getText().toString().trim().length() == 0) {
            if (!"22800".equals(csFormCode.getValue()) && !"22200".equals(csFormCode.getValue())) {
                return "???????????? ???????????? ???????????????.";
            } else {
                System.out.println("------------------ locationText : " + locationText);
                locationText = " ";
            }
        }
        /*draftPrim.setLocation(location.getText().toString());*/
        draftPrim.setLocation(locationText);

        // ?????????
        MemberEditText tvCooperator = (MemberEditText) findViewById(R.id.apply_outside_textview_cooperator);

        // 0-??????, 1-??????, 2-??????, 3-??????, 4-??????
        if (("3".equals(csFormCode.getValue()) || "??????".equals(csFormCode.getValue())) && !"".equals(tvCooperator.getText().toString())) {
            tvCooperator.setText("");
            tvCooperator.clearData();
            draftPrim.setCooperator("");
            return "??????????????? ?????? ????????? ???????????? ????????? ????????? ?????? ??? ????????????.";
        }

        PrimitiveList cooperatorList = new PrimitiveList();
        for (String cooperator : tvCooperator.getValues()) {
            cooperatorList.add(cooperator);
        }

        Log.d(TAG, "cooperatorList.toString() ============ " + cooperatorList.toString());

        draftPrim.setCooperator(cooperatorList.toString());

        //2021.06 ?????????????????? ????????????
        draftPrim.setGEN_TIME_YN(GEN_TIME_YN);

        if (isValidTargetDate() == false)
            return "???????????? ??????????????? ???????????? ????????? ??????????????? ????????????.";

        // ???????????? ??????????????? ????????? ????????? ??? ?????? ???????????? ????????????.
        SKTUtil.log("TEST", "DraftPrimitive: " + draftPrim.toParameterString());

        return null;
    }

    private boolean isValidTargetDate() {
        Calendar targetDate = Calendar.getInstance();
        Calendar fromTime = Calendar.getInstance();

        targetDate.setTime(draftPrim.getTargetDate());
        fromTime.setTime(draftPrim.getFromTime());

//		SKTUtil.log("TEST", "targetDate: " + DateUtil.format(targetDate.getTime()));
//		SKTUtil.log("TEST", "fromTime: " + DateUtil.format(fromTime.getTime()));

        targetDate.set(Calendar.HOUR_OF_DAY, fromTime.get(Calendar.HOUR_OF_DAY));
        targetDate.set(Calendar.MINUTE, fromTime.get(Calendar.MINUTE));

        Date currentDate = new Date(System.currentTimeMillis());
//		SKTUtil.log("TEST", "Summed targetDate: " + DateUtil.format(targetDate.getTime()));
//		SKTUtil.log("TEST", "Current Date: " + DateUtil.format(currentDate));

        return targetDate.getTime().after(currentDate);
    }


}