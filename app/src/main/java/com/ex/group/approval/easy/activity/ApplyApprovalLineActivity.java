package com.ex.group.approval.easy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.approval.easy.primitive.ApprovePrimitive;
import com.ex.group.folder.R;
import com.ex.group.approval.easy.adapter.ApprovalLineAdaptor;
import com.ex.group.approval.easy.constant.ApprovalConstant;
import com.ex.group.approval.easy.constant.UserInfo;
import com.ex.group.approval.easy.domain.Member;
import com.ex.group.approval.easy.domain.PrivateState;
import com.ex.group.approval.easy.primitive.DraftPrimitive;
import com.ex.group.approval.easy.primitive.PrivateLineListPrimitive;
import com.ex.group.approval.easy.util.ActivityLauncher;
import com.skt.pe.common.activity.ifaces.CommonUI;
import com.skt.pe.common.conf.Environ;
import com.skt.pe.common.conf.EnvironManager;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.dialog.DialogButton;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.primitive.Primitive;
import com.skt.pe.common.primitive.util.PrimitiveList;
import com.skt.pe.common.service.Parameters;
import com.skt.pe.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApplyApprovalLineActivity extends ApprovalCommonActivity implements OnClickListener, CommonUI {
    private Handler handler = new Handler(new Handler.Callback() {
        public boolean handleMessage(final Message msg) {
            boolean ret = false;

            final int position = msg.arg1;
            switch (msg.what) {
                /*
                 * ????????? ?????? ??????
                 */
                case ApprovalLineAdaptor.What.ADD_ITEM:
                    ret = true;
                    selectMemberSearch(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityLauncher.launchMemberSearch(ApplyApprovalLineActivity.this, getSelectMemberSearchType(), ApprovalConstant.RequestCode.SELECT_LINE, ActivityLauncher.MemberSelectType.SINGLE, String.valueOf(position));
                        }
                    }, "approval");
                    break;
                case ApprovalLineAdaptor.What.DELETE_ITEM:
                    ret = true;

                    memberList.remove(position);
                    approvalLineAdapter.notifyDataSetChanged();
                    break;
                case ApprovalLineAdaptor.What.REFRESH:
                    approvalLineAdapter.notifyDataSetChanged();
                    break;
            }
            return ret;
        }
    });

    private DraftPrimitive draftPrim = null;
    private PrivateLineListPrimitive privateLinePrim = null;
    private ApprovalLineAdaptor approvalLineAdapter = null;
    private List<Member> memberList = new ArrayList<Member>();

    @Override
    protected int assignLayout() {
        return R.layout.easy_apply_line;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }


    @Override
    public void onCreateX(Bundle savedInstanceState) {
        Intent intent = getIntent();
        draftPrim = (DraftPrimitive) intent.getSerializableExtra(ApprovalConstant.IntentArg.PRIMITIVE);
        privateLinePrim = new PrivateLineListPrimitive();
        setSubTitle("???????????? ??????");
        initUI();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public Handler getHandler() {
        return this.handler;
    }

    @Override
    protected void onReceive(Primitive primitive, SKTException e) {
        super.onReceive(primitive, e);
        if (e == null) {
            if (primitive instanceof DraftPrimitive) {
                alert("?????? ????????? ?????????????????????.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(ApprovalConstant.ResponseCode.OK);
                        finish();
                    }
                });
            } else if (primitive instanceof PrivateLineListPrimitive) {
                // ?????????????????? DialogBox ?????? ?????????.
                privateLinePrim = (PrivateLineListPrimitive) primitive;
                if (privateLinePrim.getLineList().size() == 0) {
                    alert("???????????? ?????? ?????? ????????? ????????????.");
                } else {
                    selectPrivateLine(privateLinePrim.getLineList(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int selectPrivateLine = getSelectPrivateLine();

                            List<PrivateState> stateList = privateLinePrim.getLineList().get(selectPrivateLine).getStateList();
                            PrivateState pState = null;
                            Member member = null;
                            boolean isNewApprovalNone = false;
                            if (stateList.size() > 0) {
                                approvalLineAdapter.clear();
                                for (int i = 0; i < stateList.size(); i++) {
                                    member = new Member();
                                    pState = stateList.get(i);
                                    member.setEmpId(pState.getId());
                                    member.setName(pState.getName());
                                    member.setRole(pState.getRole());
                                    member.setSuffixDept(pState.getDeptName());
                                    member.setApprovalKind(pState.getKind());
                                    approvalLineAdapter.add(member);
                                    if (pState.getKind().equals(DraftPrimitive.ApprovalKind.AUTHORIZED) || pState.getKind().equals(DraftPrimitive.ApprovalKind.SUBSTITUTED)) {
                                        isNewApprovalNone = true;
                                    }
                                }
                                Member blank = new Member();
                                if (isNewApprovalNone == true) {
                                    blank.setApprovalKind(DraftPrimitive.ApprovalKind.NONE);
                                }
                                approvalLineAdapter.add(blank);
                                approvalLineAdapter.notifyDataSetChanged();
                            } else {
                                alert("????????? ??????????????? ????????? ???????????? ????????????.");
                            }
                        }
                    });
                }
            }
        } else {
            e.alert(this, new DialogButton(0) {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
    }

    @Override
    protected void onActivityResultX(int requestCode, int resultCode, Intent intent) {
        /*
         * ????????? ????????? ?????? ?????? ??? ?????? ??????...
         */
        if (intent != null) {
            if (requestCode == ApprovalConstant.RequestCode.SELECT_LINE) {
                Member selectedMember = new Member(intent);
                int position = StringUtil.intValue(intent.getStringExtra("arg1"), -1);
                if (position != -1) {
                    Member member = approvalLineAdapter.getItem(position);
                    member.setMember(selectedMember);
                    if (approvalLineAdapter.getCount() == 1 || position == approvalLineAdapter.getCount() - 1)
                        approvalLineAdapter.add(new Member());
                    approvalLineAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apply_line_button_ok:
                String validationMessage = validationUI();
                //2021.06 ????????????TEST
                if (validationMessage == null) {
                    alertYesNo("?????? ?????? ??????", "????????? ???????????????.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//							executePrimitive(draftPrim);
                            switch (getIntent().getIntExtra(IntentArg.APPROVAL_TYPE, ApprovalConstant.EasyApprovalType.OUTSIDE)) {
                                case ApprovalConstant.EasyApprovalType.OUTSIDE:
                                    Log.d("goDraft", "outside");
                                    saveOutAppl();//??????
                                    break;
                                case ApprovalConstant.EasyApprovalType.VACATION:
                                    Log.d("goDraft", "vacation");
                                    saveHolGoingAppl();//??????
                                    break;
                                default:
                                    Log.d("goDraft", "default");
                                    break;

                            }
                        }
                    }, null);


                } else {
                    alert(validationMessage);
                }
                break;
            case R.id.apply_line_button_cancel:
                finish();
                break;
            case R.id.apply_line_button_privateline:
                executePrimitive(privateLinePrim);
                break;
        }
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     * ?????? ???????????? ??? List ???????????? ???????????? ????????? ??????...
     */
//	@Override
//	public Object onRetainNonConfigurationInstance() {
//		Map<String, Object> map = new HashMap<String, Object>();
//
//		map.put("approvalLineAdapter", approvalLineAdapter);
//
//		return map;
//	}

    @Override
    public void initUI() {
        ListView lineList = (ListView) findViewById(R.id.apply_line_listview_line);

//		Map<String, Object> o = (Map<String, Object>) getLastNonConfigurationInstance();
//		if (o != null) {
//			approvalLineAdapter = (ApprovalLineAdaptor) o.get("approvalLineAdapter");
//		} else {
        // 2015-06-26 Join ?????? - ????????? ?????? ??? ??? ?????? ?????? ????????? ???????????? ???????????? ?????? ?????? ??????
        memberList.clear();
        UserInfo userInfo = UserInfo.getInstance();
        Intent intent = new Intent();
        intent.putExtra("name", userInfo.getUserName());
        intent.putExtra("empId", userInfo.getUserID());
        intent.putExtra("role", userInfo.getUserRole());
        intent.putExtra("suffixDept", userInfo.getUserSuffixDept());
        Member drafter = new Member(intent);
        drafter.setApprovalKind(DraftPrimitive.ApprovalKind.DRAFTER);
        Member blank = new Member();
        memberList.add(drafter);
        memberList.add(blank);
        // 2015-06-26 Join ?????? ???
        approvalLineAdapter = new ApprovalLineAdaptor(this, R.layout.easy_apply_line_listview_line, memberList);
//			approvalLineAdapter.add(new Member());
        approvalLineAdapter.notifyDataSetChanged();
//		}
        lineList.setAdapter(approvalLineAdapter);

        findViewById(R.id.apply_line_button_ok).setOnClickListener(this);
        findViewById(R.id.apply_line_button_cancel).setOnClickListener(this);
        findViewById(R.id.apply_line_button_privateline).setOnClickListener(this);

        switch (getIntent().getIntExtra(IntentArg.APPROVAL_TYPE, ApprovalConstant.EasyApprovalType.OUTSIDE)) {
            case ApprovalConstant.EasyApprovalType.OUTSIDE:
                showOutsideSummary();
                break;
            case ApprovalConstant.EasyApprovalType.VACATION:
                showVacationSummary();
                break;
        }
    }

    @Override
    public void resetUI() {

    }

    @Override
    public String validationUI() {
        //2021.07 1????????? ????????????
        Log.d("validationUI","validationUI = " + draftPrim.getEmpGradeNm());
        System.out.println("--------------??????");
        System.out.println("--------------?????? + draftPrim.getEmpGradeNm() == " + draftPrim.getPOST_NM());
        if("1".equals(draftPrim.getEmpGradeNm()) || "1".equals(draftPrim.getPOST_NM()) ){//1??? ?????????

        }else{//2???,3???,4???,5???,... ?????????
            if (memberList.size() < 3)// 2015-08-06 Join ?????? - ????????? ?????? ??? ??? ?????? ?????? ????????? ???????????? ???????????? ?????? ?????? if?????? 2 -> 3 ????????????
                return "??????????????? ?????? ???????????? ????????? ????????? ?????????.";
        }

        if (isDuplicateMember() == true) {
            return "??????????????? ????????? ????????? ????????????.";
        }


        PrimitiveList approvalStep = new PrimitiveList();
        PrimitiveList approvalKind = new PrimitiveList();
        Member member;
        for (int i = 0; i < memberList.size(); i++) {
            member = memberList.get(i);
            if (member.isNull() == false) {
                approvalStep.add(member.getEmpId());
                approvalKind.add(member.getApprovalKind());
            }
        }

        draftPrim.setApprovalStep(approvalStep.toString());
        draftPrim.setApprovalKind(approvalKind.toString());

        return null;
    }

    private boolean isDuplicateMember() {
        String empId = null;
        for (int i = 0; i < memberList.size(); i++) {
            empId = memberList.get(i).getEmpId();
            for (int j = i; j < memberList.size(); j++) {
                if (j == i) continue;
                if (empId.equals(memberList.get(j).getEmpId()))
                    return true;
            }
        }
        return false;
    }

    private void showOutsideSummary() {
        TextView summary = (TextView) findViewById(R.id.apply_line_textview_summary);

        // ????????? ??????
        SpannableStringBuilder sp = new SpannableStringBuilder(getIntent().getStringExtra(IntentArg.SUMMARY_TITLE));
        sp.setSpan(new ForegroundColorSpan(Color.rgb(0x16, 0xAD, 0xFE)), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		sp.setSpan(new AbsoluteSizeSpan(24), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        summary.setText(sp);

        StringBuffer sb = new StringBuffer();

        sb.append("(");
        sb.append((String) draftPrim.getTag(DraftPrimitive.Tags.FORM_CODE_NAME));
        sb.append(", ");
        sb.append(SummaryTargetDateFormat(draftPrim.getTargetDate()));
        sb.append(" ");
        sb.append(DraftPrimitive.formatTime(draftPrim.getFromTime()));
        sb.append("~");
        sb.append(DraftPrimitive.formatTime(draftPrim.getUntilTime()));
        sb.append(")");

        summary.append(sb.toString());

        TextView currentDate = (TextView) findViewById(R.id.apply_line_textview_currentdate);
        currentDate.setText(SummaryCurrentDateFormat(new Date(System.currentTimeMillis())));
    }

    private void showVacationSummary() {
        TextView summary = (TextView) findViewById(R.id.apply_line_textview_summary);

        // ????????? ??????
        SpannableStringBuilder sp = new SpannableStringBuilder(getIntent().getStringExtra(IntentArg.SUMMARY_TITLE));
        sp.setSpan(new ForegroundColorSpan(Color.rgb(0x16, 0xAD, 0xFE)), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		sp.setSpan(new AbsoluteSizeSpan(24), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        summary.setText(sp);
        Log.d("ApplyApprovalLine", "ApplyApprovalLineActivity " + (String) draftPrim.getTag(DraftPrimitive.Tags.FORM_CODE_NAME));
        StringBuffer sb = new StringBuffer();

        sb.append("\n");
        sb.append((String) draftPrim.getTag(DraftPrimitive.Tags.FORM_CODE_NAME));
        sb.append(" ");
        sb.append(SummaryTargetDateFormat(draftPrim.getFromDate()));
        sb.append("~");
        sb.append(SummaryTargetDateFormat(draftPrim.getUntilDate()));
        // 2015-03-05 Join ?????? - ??????????????? ?????? ???????????? ????????? ?????? ??????
//		sb.append("(*??? " + draftPrim.getTotalVacation() + "??? ??? " + draftPrim.getExceptCount() + "??? ??????))");
        int realVactionTerm = 0;        // ?????? ???????????? = ??? ???????????? - ????????????
        realVactionTerm = Integer.parseInt(draftPrim.getTotalVacation()) - Integer.parseInt(draftPrim.getExceptCount());
        if ("0".equals(draftPrim.getExceptCount())) {
            sb.append("(??? " + realVactionTerm + "???)");
        } else {
            sb.append("(??? " + realVactionTerm + "??? - " + draftPrim.getTotalVacation() + "??? ??? " + draftPrim.getExceptCount() + "??? ??????)");
        }

        summary.append(sb.toString());

//		
////		TextView targetDate = (TextView) findViewById(R.id.apply_line_textview_targetdate);
////		targetDate.setText(SummaryTargetDateFormat(DraftPrimitive.stringToDate(draftPrim.getTargetDate())));
//		
//		TextView fromTime = (TextView) findViewById(R.id.apply_line_textview_fromtime);
//		fromTime.setText(SummaryTargetDateFormat(draftPrim.getFromDate()));
//		
//		TextView untilTime = (TextView) findViewById(R.id.apply_line_textview_untiltime);
//		untilTime.setText(SummaryTargetDateFormat(draftPrim.getUntilDate()));
//		
        TextView currentDate = (TextView) findViewById(R.id.apply_line_textview_currentdate);
        currentDate.setText(SummaryCurrentDateFormat(new Date(System.currentTimeMillis())));
    }

    private String SummaryTargetDateFormat(Date inputDate) {
        SimpleDateFormat format = new SimpleDateFormat("MM??? dd???");
        return format.format(inputDate).toString();
    }

    private String SummaryCurrentDateFormat(Date inputDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        return format.format(inputDate).toString();
    }

    public static class IntentArg {
        public final static String APPROVAL_TYPE = "APPROVAL_TYPE";
        public final static String SUMMARY_TITLE = "SUMMARY_TITLE";
    }

    //?????? ?????? ?????? ??????
    public void saveHolGoingAppl() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat format_time = new SimpleDateFormat("HHmm");

            String appendUrl = "";
            appendUrl += "&approvalStep=" + draftPrim.getApprovalStep();
            appendUrl += "&approvalKind=" + draftPrim.getApprovalKind();

            appendUrl += "&descript=" + draftPrim.getDescript();
            appendUrl += "&telNum=" + draftPrim.getTelNum();
            appendUrl += "&systemType=" + draftPrim.getSystemType();
            appendUrl += "&draftKind=" + draftPrim.getDraftKind();

            appendUrl += "&fromDate=" + format.format(draftPrim.getFromDate());
            appendUrl += "&untilDate=" + format.format(draftPrim.getUntilDate());
            appendUrl += "&S_ANL_END_YMD=" + format.format(draftPrim.getUntilDate());
            appendUrl += "&targetDate=" + format.format(draftPrim.getTargetDate());
//			appendUrl += "&fromTime="+format_time.format(draftPrim.getFromTime());
//			appendUrl += "&untilTime="+format_time.format(draftPrim.getUntilTime());
            appendUrl += "&exclusiveDate=" + draftPrim.getExceptDay();
            appendUrl += "&location=" + draftPrim.getLocation();
            appendUrl += "&descript=" + draftPrim.getDescript();
//			appendUrl += "&isOwnCar="+draftPrim.getisOw();
            appendUrl += "&cooperator=" + draftPrim.getCooperator();

            String url = (draftPrim.getUrl() + appendUrl).replace("searchHolGoingCheck", "saveHolGoingAppl").replace("COMMON_APPROVALSTAGING_RESTFULCLIENT", "COMMON_APPROVALSTAGING_DRAFT");//??????
//			String url = (draftPrim.getUrl()+appendUrl).replace("searchHolGoingCheck","saveHolGoingAppl").replace("COMMON_APPROVALSTAGING_RESTFULCLIENT2", "COMMON_APPROVALSTAGING_DRAFT");//????????? test

            new JsonAction(COMMON_SEARCHVACAPPL, url, this).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //???????????? ?????? ??????
    public void saveOutAppl() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat format_time = new SimpleDateFormat("HHmm");

            //2021.07 ???????????? ???????????? ????????????
            String appendUrl = "";
            appendUrl += "&approvalStep=" + draftPrim.getApprovalStep();
            appendUrl += "&approvalKind=" + draftPrim.getApprovalKind();
            appendUrl += "&descript=" + draftPrim.getDescript();
            appendUrl += "&telNum=" + draftPrim.getTelNum();
            appendUrl += "&systemType=" + draftPrim.getSystemType();
            appendUrl += "&draftKind=" + draftPrim.getDraftKind();

//			appendUrl += "&fromDate="+format.format(draftPrim.getFromDate());
//			appendUrl += "&untilDate="+format.format(draftPrim.getUntilDate());
            appendUrl += "&targetDate=" + format.format(draftPrim.getTargetDate());
            appendUrl += "&fromTime=" + format_time.format(draftPrim.getFromTime());
            appendUrl += "&untilTime=" + format_time.format(draftPrim.getUntilTime());
            appendUrl += "&exclusiveDate=" + draftPrim.getExceptDay();


            appendUrl += "&location=" + draftPrim.getLocation();
            appendUrl += "&descript=" + draftPrim.getDescript();
//			appendUrl += "&isOwnCar="+draftPrim.getisOw();
            appendUrl += "&cooperator=" + draftPrim.getCooperator();
            System.out.println("------------ draftPrim.getFormCode() : " + draftPrim.getFormCode() + " draftPrim.getLocation() : " + draftPrim.getLocation().length() + "getDescript() : " + draftPrim.getDescript().length());

            if (draftPrim.getIsOwnCar()) {
                appendUrl += "&isOwnCar=Y";
            } else {
                appendUrl += "&isOwnCar=N";
            }


            // appendUrl += "&GEN_TIME_YN=" + draftPrim.getGEN_TIME_YN();

            String url = (draftPrim.getUrl() + appendUrl).replace("searchOutCheck", "saveOutAppl").replace("COMMON_APPROVALSTAGING_RESTFULCLIENT", "COMMON_APPROVALSTAGING_DRAFT");//??????
//			String url = (draftPrim.getUrl()+appendUrl).replace("searchOutCheck","saveOutAppl").replace("COMMON_APPROVALSTAGING_RESTFULCLIENT2", "COMMON_APPROVALSTAGING_DRAFT");//????????? test


            new JsonAction(COMMON_SEARCHOUTAPPL, url, this).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onJsonActionPost(String primitive, String result) {
//		{"Etc":[],"Msg":[{"result":"4010","retMsg":""}]}
        try {
            if (COMMON_SEARCHOUTAPPL.equals(primitive)) {
                JSONObject obj = new JSONObject(result);
                JSONArray jsonArray = obj.getJSONArray("Msg");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject temp = jsonArray.getJSONObject(i);
                    if ("1000".equals(temp.getString("result"))) {
                        Toast.makeText(ApplyApprovalLineActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_LONG).show();

                        alert("??????", "??????????????? ?????????????????????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }, false);

                    } else {
                        Toast.makeText(ApplyApprovalLineActivity.this, "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                    }
                }
            } else if (COMMON_SEARCHVACAPPL.equals(primitive)) {
                JSONObject obj = new JSONObject(result);
                JSONArray jsonArray = obj.getJSONArray("Msg");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject temp = jsonArray.getJSONObject(i);
                    if ("1000".equals(temp.getString("result"))) {
                        Toast.makeText(ApplyApprovalLineActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                        alert("??????", "??????????????? ?????????????????????.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }, false);

                    } else {
                        Toast.makeText(ApplyApprovalLineActivity.this, "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}