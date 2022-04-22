package com.ex.group.mail.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.mail.addressbook.activity.AddressTabActivity;
import com.ex.group.mail.addressbook.activity.BaseActivity;
import com.ex.group.mail.data.EmailDetailData;
import com.ex.group.mail.data.EmailFileListData;
import com.ex.group.mail.data.EmailWriteSQLite;
import com.ex.group.mail.data.FileContents;
import com.ex.group.mail.data.NameMail;
import com.ex.group.mail.util.EmailClientUtil;
import com.skt.pe.common.activity.SKTActivity;
import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.conf.EnvironManager;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.data.SKTWebUtil;
import com.skt.pe.common.dialog.DialogButton;
import com.skt.pe.common.dialog.SKTDialog;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.service.Controller;
import com.skt.pe.common.service.Parameters;
import com.skt.pe.common.service.XMLData;
import com.skt.pe.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ex.group.folder.R;
/**
 * 메일 작성 화면
 * 
 * @author sjsun5318
 *
 */
public class EmailWriteActivity extends SKTActivity implements OnClickListener,
        OnFocusChangeListener, OnKeyListener, TextWatcher {

	private final String TAG = "EmailWriteActivity";

	private final String MAIL_TYPE_REPLY = "reply";
	private final String MAIL_TYPE_REPLYALL = "replyall";
	private final String MAIL_TYPE_FORWARD = "forward";
	private final String MAIL_TYPE_SIDE = "side";
	private final String MAIL_TYPE_RESEND = "resend";

	private final String RCPT_TO = "1";
	private final String RCPT_CC = "2";
	private final String RCPT_BCC = "3";

	private final int UPLOAD_FILE = 1006;

	private EmailDetailData detailData = null;
	private NameMail fromInfo = null;
	private NameMail[] toListData = null;
	private NameMail[] ccListData = null;
	private NameMail[] bccListData = null;
	private ArrayList<String> fileNameList = null;
	private ArrayList<EmailFileListData> uploadList = new ArrayList<EmailFileListData>();
	private HashMap<String, String> realAddrMap = null;

	private AutoCompleteTextView et1 = null;
	private AutoCompleteTextView et2 = null;
	private AutoCompleteTextView et3 = null;

	private String type = "";
	private String toList = "";
	private String ccList = "";
	private String bccList = "";
	private String subject = "";
	private String body = "";
	private String openFlag = "false";

	private String vip = "";
	private Button[] checkBtn = null;
	private String mailType = "";
	private String boxid = "";
	private String boxtype = "";
	private String[] uploadkey = null;
	
	
	private Button secretOptionChkBtn = null;
	private Button replyOptionChkBtn = null;
	private Button fastOptionChkBtn = null;
	private Button hideOptionChkBtn = null;
	private Button sendMyselfBtn = null;	// kbr 2022.04.11
	private Button sendButton = null;
	
	private Button addrButton = null;
	private Button addrButton2 = null;
	private Button addrButton3 = null;
	
	private String isWork = "1";

	private ProgressDialog progDialog;
	private ProgressThread progThread;

	private String mailID = "";
	private EditText bodyText = null;

	private ArrayList<String> fileIdList = null;
	private ArrayList<FileContents> filecontents = null;

	// kbr 2022.04.11
	private String empName = null;
	private String empId = null;
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * onCreate 메소드
	 * 
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreateX(Bundle savedInstanceState) {

		EnvironManager.setNeedEncPwd(true);

		bodyText = (EditText) findViewById(R.id.textBody);
		bodyText.setOnFocusChangeListener(this);

		realAddrMap = new HashMap<String, String>();

		findViewById(R.id.top_send).setOnClickListener(this);
		findViewById(R.id.top_cancel).setOnClickListener(this);
		
		et1 = (AutoCompleteTextView) findViewById(R.id.textTo);
		et2 = (AutoCompleteTextView) findViewById(R.id.textCc);
		et3 = (AutoCompleteTextView) findViewById(R.id.textBcc);

		checkBtn = new Button[2];
		checkBtn[0] = (Button) findViewById(R.id.check1);
		checkBtn[0].setOnClickListener(this);
		checkBtn[1] = (Button) findViewById(R.id.check2);
		checkBtn[1].setOnClickListener(this);

		secretOptionChkBtn = (Button) findViewById(R.id.check21);
		replyOptionChkBtn = (Button) findViewById(R.id.check22);
		fastOptionChkBtn = (Button) findViewById(R.id.check23);
		hideOptionChkBtn = (Button) findViewById(R.id.check24);
		sendMyselfBtn = (Button) findViewById(R.id.check31);	// kbr 2022.04.11
		sendButton = (Button) findViewById(R.id.requestBtn);
		
		addrButton = (Button) findViewById(R.id.AddrButton);
		addrButton2 = (Button) findViewById(R.id.AddrButton2);
		addrButton3 = (Button) findViewById(R.id.AddrButton3);
		
		addrButton.setOnClickListener(this);
		addrButton2.setOnClickListener(this);
		addrButton3.setOnClickListener(this);

		secretOptionChkBtn.setOnClickListener(this);
		replyOptionChkBtn.setOnClickListener(this);
		fastOptionChkBtn.setOnClickListener(this);
		hideOptionChkBtn.setOnClickListener(this);
		sendMyselfBtn.setOnClickListener(this);	// kbr 2022.04.11
		sendButton.setOnClickListener(this);

		TextView settitle = (TextView) findViewById(R.id.settitle);
		Intent intent = getIntent();

		//2021.07 메일 TEST
		type = (String) intent.getStringExtra("type");


		if (MAIL_TYPE_REPLY.equals(type) || MAIL_TYPE_FORWARD.equals(type)
				|| MAIL_TYPE_RESEND.equals(type)) {
			mailID = intent.getStringExtra("mailID");
			if (!MAIL_TYPE_REPLY.equals(type))
				filecontents = intent
						.getParcelableArrayListExtra("fileContentList");
		}
		// 답장, 전달, 전체답장
		if (intent.getStringExtra("list") != null
				&& "list".equals(intent.getStringExtra("list"))) {
			/*
			 * try { EmailReceiveListData receive =
			 * (EmailReceiveListData)intent.getParcelableExtra("receive");
			 * Parameters params = new
			 * Parameters(EmailClientUtil.COMMON_MAIL_CONTENT); params.put("ID",
			 * receive.getMailId()); params.put("CHANGEKEY",
			 * receive.getMailChangeKey()); Controller controller = new
			 * Controller(this); XMLData xmlData; xmlData =
			 * controller.request(params); detailData = new
			 * EmailDetailData(xmlData);
			 * 
			 * XMLData childXmlFileData = xmlData.getChild("attachmentsList");
			 * 
			 * childXmlFileData.setList("fileAttachment"); fileNameList = new
			 * ArrayList<String>();
			 * 
			 * for(int i=0; i<childXmlFileData.size(); i++) {
			 * if(!StringUtil.isNull(childXmlFileData.get(i,"id"))){
			 * fileNameList
			 * .add(StringUtil.isNull(childXmlFileData.get(i,"name")) ? "" :
			 * childXmlFileData.get(i,"name")); } }
			 * 
			 * fromInfo = detailData.getFromInfo(); toListData =
			 * detailData.getToList(); ccListData = detailData.getCcList();
			 * bccListData = detailData.getBccList(); } catch (SKTException e) {
			 * e.printStackTrace(); }
			 */
		} else {

			detailData = (EmailDetailData) intent.getParcelableExtra("detailData");
			fromInfo = (NameMail) intent.getParcelableExtra("fromInfo");

			Parcelable[] t = null;

			t = (Parcelable[]) intent.getParcelableArrayExtra("to");
			if (t != null) {
				toListData = new NameMail[t.length];
				for (int i = 0; i < t.length; i++) {
					toListData[i] = (NameMail) t[i];
					Log.i("EmailWriteActivity", "mail ============ "+toListData[i].getMail());
					Log.i("EmailWriteActivity", "name ============ "+toListData[i].getName());
				}
			}
			t = (Parcelable[]) intent.getParcelableArrayExtra("cc");
			if (t != null) {
				ccListData = new NameMail[t.length];
				for (int i = 0; i < t.length; i++) {
					ccListData[i] = (NameMail) t[i];
				}
			}
			t = (Parcelable[]) intent.getParcelableArrayExtra("bcc");
			if (t != null) {
				bccListData = new NameMail[t.length];
				for (int i = 0; i < t.length; i++) {
					bccListData[i] = (NameMail) t[i];
				}
			}
			fileNameList = intent.getStringArrayListExtra("fileNameList");
			fileIdList = intent.getStringArrayListExtra("fileIdList");
			boxid = intent.getStringExtra("boxid");
			boxtype = intent.getStringExtra("boxtype");

			// kbr 2022.04.12
			empName = intent.getStringExtra("empName");
			empId = intent.getStringExtra("empId");
		}

		EditText t_name = (EditText) findViewById(R.id.textSubject);

		if (StringUtil.isNull(type)) { // 새글
			mailType = "ODNR";
			if (intent.getStringExtra("toList") != null) {
				toList = intent.getStringExtra("toList") + ";";
				et1.setText(toList);
			}

			if (intent.getStringExtra("subject") != null) {
				subject = intent.getStringExtra("subject");
				t_name.setText(subject);
			}
			/*
			 * if(intent.getStringExtra("body") != null){ WebView webView =
			 * (WebView)findViewById(R.id.textOrg); try {
			 * SKTWebUtil.loadWebView(this, webView,
			 * intent.getStringExtra("body")); } catch (SKTException e) {
			 * e.printStackTrace(); } }
			 */
		} else if (type.equals(MAIL_TYPE_REPLY)) { // 답장
			settitle.setText("답장");
			if (fromInfo.getMail() != null) {
				toList = StringUtil.isNull(fromInfo.getName()) ? fromInfo
						.getMail() : fromInfo.getName() + ";";
				et1.setText(toList);
				realAddrMap.put(
						StringUtil.isNull(fromInfo.getName()) ? fromInfo
								.getMail() : fromInfo.getName(), fromInfo
								.getMail());

			}
			// 제목
			subject = "[답장]:" + detailData.getMailTitle();
			t_name.setText(subject);
			// findViewById(R.id.patternLayout).setVisibility(View.GONE);
			// findViewById(R.id.bccBtn).setVisibility(View.GONE);
			findViewById(R.id.AddrButton).setVisibility(View.GONE);
		} else if (type.equals(MAIL_TYPE_REPLYALL)) { // 전체답장
			String realAddrto = "";
			if (fromInfo.getMail() != null) {
				toList = StringUtil.isNull(fromInfo.getName()) ? fromInfo
						.getMail() : fromInfo.getName() + ";";
				realAddrMap.put(
						StringUtil.isNull(fromInfo.getName()) ? fromInfo
								.getMail() : fromInfo.getName(), fromInfo
								.getMail());
			}

			if (toListData != null && toListData.length > 0) {

				for (int a = 0; a < toListData.length; a++) {
					if (toListData[a] != null
							&& !toListData[a].getMail().equals(
									EmailClientUtil.empNm)) {
						realAddrto = StringUtil.isNull(toListData[a].getName()) ? toListData[a]
								.getMail() : toListData[a].getName();
						toList += realAddrto + ";";
						Log.e("EmailWriteActivity", "realAddrTo ==================================== "+realAddrto);
						realAddrMap.put(realAddrto, toListData[a].getMail());
					}
				}
				findViewById(R.id.layoutCC).setVisibility(View.VISIBLE);
				et1.setText(toList);
				et1.setSelection(et1.getSelectionEnd());
			} else {
				// findViewById(R.id.layoutCC).setVisibility(View.GONE);
			}

			if (ccListData != null && ccListData.length > 0) {
				String realAddrCc = "";
				for (int a = 0; a < ccListData.length; a++) {
					realAddrCc = StringUtil.isNull(ccListData[a].getName()) ? ccListData[a]
							.getMail() : ccListData[a].getName();
					ccList += realAddrCc + ";";
					realAddrMap.put(realAddrCc, ccListData[a].getMail());
				}
				et2.setText(ccList);

			}

			if (bccListData != null && bccListData.length > 0) {
				String realAddrBcc = "";
				for (int a = 0; a < bccListData.length; a++) {
					realAddrBcc = StringUtil.isNull(bccListData[a].getName()) ? bccListData[a]
							.getMail() : bccListData[a].getName();
					bccList += realAddrBcc + ";";
					realAddrMap.put(realAddrBcc, bccListData[a].getMail());
				}
				et3.setText(bccList);
			}

			// 제목
			subject = "[답장]:" + detailData.getMailTitle();
			t_name.setText(subject);

		} else if (type.equals(MAIL_TYPE_FORWARD)) { // 전달
			mailType = "ODNR";
			settitle.setText("전달");
			subject = "[전달]:" + detailData.getMailTitle();
			t_name.setText(subject);

		} else if (type.equals(MAIL_TYPE_SIDE)) {
			String[] name = null;
			String[] email = null;
			String[] vvip = null;
			String names = "";
			String emails = "";
			String noEmail = "";

			name = intent.getStringArrayExtra("names");
			email = intent.getStringArrayExtra("emails");
			vvip = intent.getStringArrayExtra("vvip");

			for (int a = 0; a < name.length; a++) {
				if (StringUtil.isNull(email[a])) {
					noEmail += name[a] + "님 ";
				} else {
					realAddrMap.put(name[a], email[a]);
					names += name[a] + ";";
					emails += email[a] + ";";
					if (vvip[a].equals("true")) {
						vip = name[a];
					}
				}
			}

			if (!StringUtil.isNull(noEmail)) {
				SKTDialog dlg = new SKTDialog(this, SKTDialog.DLG_TYPE_1);
				dlg.getDialog(getResources().getString(R.string.mail_dialog_ok),
						noEmail + "의 메일 주소가  없습니다.", new DialogButton(0) {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
			}

			if (!StringUtil.isNull(toList)) {
				if (!toList.substring(toList.length() - 1, toList.length())
						.equals(";")) {
					toList += ";";
				}
			}
			toList = toList + names;
			et1.requestFocus();
			onFocusChangeTextView(et1, toList);
		} else if (type.equals(MAIL_TYPE_RESEND)) {
			mailType = "ODNR";
			settitle.setText("재발송");
			subject = "[재발송]:" + detailData.getMailTitle();
			t_name.setText(subject);

			String realAddrto = "";
			if (toListData != null && toListData.length > 0) {
				for (int a = 0; a < toListData.length; a++) {
					if (toListData[a] != null
							&& !toListData[a].getMail().equals(
									EmailClientUtil.empNm)) {
						realAddrto = EmailClientUtil
								.getNameString(StringUtil.isNull(toListData[a]
										.getName()) ? toListData[a].getMail()
										: toListData[a].getName());
						toList += realAddrto + ";";
						realAddrMap.put(realAddrto, toListData[a].getMail());
					}
				}
				et1.setText(toList);
				et1.setSelection(et1.getSelectionEnd());
			}
			String realAddrcc = "";
			if (ccListData != null && ccListData.length > 0) {
				for (int a = 0; a < ccListData.length; a++) {
					if (ccListData[a] != null
							&& !ccListData[a].getMail().equals(
									EmailClientUtil.empNm)) {
						realAddrcc = EmailClientUtil
								.getNameString(StringUtil.isNull(ccListData[a]
										.getName()) ? ccListData[a].getMail()
										: ccListData[a].getName());
						ccList += realAddrcc + ";";
						realAddrMap.put(realAddrcc, ccListData[a].getMail());
					}
				}
				et2.setText(ccList);
				et2.setSelection(et2.getSelectionEnd());
			}
			String realAddrbcc = "";
			if (bccListData != null && bccListData.length > 0) {
				for (int a = 0; a < bccListData.length; a++) {
					if (bccListData[a] != null
							&& !bccListData[a].getMail().equals(
									EmailClientUtil.empNm)) {
						realAddrbcc = EmailClientUtil
								.getNameString(StringUtil.isNull(bccListData[a]
										.getName()) ? bccListData[a].getMail()
										: bccListData[a].getName());
						bccList += realAddrbcc + ";";
						realAddrMap.put(realAddrbcc, bccListData[a].getMail());
					}
				}
				et3.setText(bccList);
				et3.setSelection(et3.getSelectionEnd());
			}
		}
		// 본문이 있으면 webView 넣어준다.(전달,답장,재전송시)
		if (detailData != null && !StringUtil.isNull(detailData.getBody())) {
			WebView webView = (WebView) findViewById(R.id.textOrg);
			webView.setVisibility(View.VISIBLE);
			try {
				SKTWebUtil.loadWebView(this, webView, true,
						detailData.getBody(),
						!StringUtil.isNull(detailData.getIsImg()));
			} catch (SKTException e) {
				e.printStackTrace();
			}
			/*
			 * //전달 if(type.equals(MAIL_TYPE_FORWARD)){ StringBuffer forwardText
			 * = new StringBuffer(); forwardText.append("\n\n");
			 * forwardText.append("----- 원본 메시지 -----\n");
			 * forwardText.append("보낸 사람 : "+fromInfo.getName()+"\n");
			 * forwardText.append("받은 사람 : "+toList+"\n");
			 * forwardText.append(detailData.getReceivedDate()+"\n");
			 * forwardText.append("제목 : 제목 쓰기3\n\n\n");
			 * forwardText.append(detailData.getBody());
			 * bodyText.setText(forwardText.toString()); }
			 * 
			 * 
			 * 보낸 사람 : 김기형/정보개발팀/정보개발팀/한국도로공사
			 * 
			 * 받은 사람 : 김기형/정보개발팀
			 * 
			 * 날짜 : Thu Aug 14 13:57:16 KST 2014
			 * 
			 * 제목 : 제목 쓰기3
			 */

		}
		// 파일
		if (fileNameList != null
				&& fileNameList.size() > 0
				&& (type.equals(MAIL_TYPE_FORWARD) || MAIL_TYPE_RESEND
						.equals(type))) {
			findViewById(R.id.fileLayout).setVisibility(View.VISIBLE);
			LinearLayout layout = (LinearLayout) findViewById(R.id.FILEATTLIST);
			for (int i = 0; i < fileNameList.size(); i++) {
				LinearLayout tempLayout = (LinearLayout) LayoutInflater.from(
						this).inflate(R.layout.mail_filelist, null);
				TextView txtFileName = (TextView) tempLayout
						.findViewById(R.id.filebtn);
				txtFileName.setText(fileNameList.get(i));
				layout.addView(tempLayout);
			}
		} else {
			findViewById(R.id.fileLayout).setVisibility(View.GONE);
		}

		// bodyText.setText("\n\n"+EmailClientUtil.getSigne(this,
		// "sign").toString());

		// TextView textSummary = (TextView)findViewById(R.id.textSummary);
		// textSummary.setText(EmailClientUtil.getSigne(this,
		// "sign").toString());

		setListener();
		// 자동완성 사용안함.
		// setAdapters();

		if (type != null && !type.equals(MAIL_TYPE_FORWARD)) {
			if (!type.equals(MAIL_TYPE_RESEND))
				findViewById(R.id.fileLayout).setVisibility(View.GONE);
			findViewById(R.id.textOrg).setVisibility(View.GONE);
		}
	}

	private void setAdapters() {
		EmailWriteSQLite m_dbHelper = new EmailWriteSQLite(this);
		SQLiteDatabase m_dbAddr = m_dbHelper.getWritableDatabase();
		Cursor cs = null;
		try {
			cs = m_dbAddr.query(EmailWriteSQLite.DATABASE_TABLE, new String[] {
					"_id", "name", "email" }, null, null, null, null,
					"name asc");

			EmailWriteAddrAdapter autoAdapter1 = new EmailWriteAddrAdapter(
					this, cs, et1);
			EmailWriteAddrAdapter autoAdapter2 = new EmailWriteAddrAdapter(
					this, cs, et2);
			EmailWriteAddrAdapter autoAdapter3 = new EmailWriteAddrAdapter(
					this, cs, et3);

			et1.setAdapter(autoAdapter1);
			et2.setAdapter(autoAdapter2);
			et3.setAdapter(autoAdapter3);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null) {
				cs.close();
			}
			m_dbAddr.close();
			m_dbHelper.close();
		}
	}

	/**
	 * EditText 이벤트
	 */
	private void setListener() {
		findViewById(R.id.AddrButton).setOnClickListener(this);
		findViewById(R.id.AddrButton2).setOnClickListener(this);
		findViewById(R.id.AddrButton3).setOnClickListener(this);
		findViewById(R.id.ccBtn).setOnClickListener(this);
		findViewById(R.id.bccBtn).setOnClickListener(this);

		et1.setDropDownBackgroundResource(R.color.mail_white);
		et1.setThreshold(1);
		et1.setOnItemClickListener(addrClickedListener);
		et1.addTextChangedListener(this);
		et1.setOnFocusChangeListener(this);
		et1.setOnKeyListener(this);

		et2.addTextChangedListener(this);
		et2.setOnFocusChangeListener(this);
		et2.setOnKeyListener(this);
		et2.setDropDownBackgroundResource(R.color.mail_white);
		et2.setThreshold(1);
		et2.setOnItemClickListener(addr2ClickedListener);

		et3.addTextChangedListener(this);
		et3.setOnFocusChangeListener(this);
		et3.setOnKeyListener(this);
		et3.setDropDownBackgroundResource(R.color.mail_white);
		et3.setThreshold(1);
		et3.setOnItemClickListener(addr3ClickedListener);

		LinearLayout focus = (LinearLayout) findViewById(R.id.contentBox);
		focus.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				EditText et4 = (EditText) findViewById(R.id.textBody);
				et4.requestFocus();
				final InputMethodManager imm = (InputMethodManager) EmailWriteActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(et4, InputMethodManager.SHOW_IMPLICIT);
				return true;
			}
		});
	}

	// private void autoTextViewFocus(AutoCompleteTextView view, int index) {
	// findViewById(R.id.textSubject).requestFocus();
	// view.requestFocus();
	// }

	/**
	 * 자동 완성 이벤트
	 * 
	 * @author sjsun5318
	 *
	 */
	public class EmailWriteAddrAdapter extends CursorAdapter implements
            Filterable {

		private Context mContext;
		private AutoCompleteTextView edit;
		private final int viewHeight = 80;
		private final int maxViewHeight = 320;

		public EmailWriteAddrAdapter(Context context, Cursor c,
                                     AutoCompleteTextView edit) {
			super(context, c);
			mContext = context;
			this.edit = edit;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			LinearLayout view = (LinearLayout) inflater.inflate(
					R.layout.mail_autotextlist, parent, false);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView text1 = (TextView) view.findViewById(R.id.text1);
			TextView text2 = (TextView) view.findViewById(R.id.text2);
			TextView delBtn = (TextView) view.findViewById(R.id.AUTO_LIST_DEL);
			final int cursorInx = cursor.getInt(0);
			final View autoView = view;

			delBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					autoListDel(autoView, cursorInx, 0);
				}
			});

			if (cursor.getString(1).equals(cursor.getString(2))) {
				text1.setText("");
			} else {
				text1.setText(cursor.getString(1));
			}
			text2.setText(cursor.getString(2));
		}

		@Override
		public String convertToString(Cursor cursor) {
			return StringUtil.isNull(cursor.getString(1)) ? cursor.getString(2)
					: cursor.getString(1);
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			if (getFilterQueryProvider() != null) {
				return getFilterQueryProvider().runQuery(constraint);
			}
			StringBuilder buffer = null;
			String[] args = null;
			if (constraint != null) {
				if (constraint.toString().indexOf(";") > -1) {
					String tempStr = constraint.toString().substring(
							constraint.toString().lastIndexOf(";") + 1);
					if (tempStr != null && !tempStr.trim().equals("")) {
						constraint = tempStr;
					}
				}

				buffer = new StringBuilder();
				buffer.append(" UPPER(name) like ? or UPPER(email) like ? ");
				args = new String[] {
						"%" + constraint.toString().toUpperCase() + "%",
						"%" + constraint.toString().toUpperCase() + "%" };

			}

			EmailWriteSQLite m_dbHelper2 = new EmailWriteSQLite(mContext);
			SQLiteDatabase m_dbAddr2 = m_dbHelper2.getWritableDatabase();
			Cursor cs = null;

			cs = m_dbAddr2.query(EmailWriteSQLite.DATABASE_TABLE, new String[] {
					"max(_id) as _id", "name", "email" }, buffer == null ? null
					: buffer.toString(), args, "name, email", null, "name asc");

			if (viewHeight * cs.getCount() > maxViewHeight) {
				edit.setDropDownHeight(maxViewHeight);
			} else {
				edit.setDropDownHeight(viewHeight * cs.getCount());
			}

			m_dbAddr2.close();
			m_dbHelper2.close();

			return cs;

		}

		private void autoListDel(View view, int cursor, int index) {
			EmailWriteSQLite m_dbHelper2 = new EmailWriteSQLite(mContext);
			SQLiteDatabase m_dbAddr2 = m_dbHelper2.getWritableDatabase();
			try {
				int cnt = m_dbHelper2.getWritableDatabase().delete(
						EmailWriteSQLite.DATABASE_TABLE, "_id = " + cursor,
						null);
				if (cnt > 0) {

					// this.runQueryOnBackgroundThread(edit.getText());
					edit.dismissDropDown();
					// ((EmailWriteActivity)mContext).autoTextViewFocus(edit,index);
					// edit.showDropDown();
					// this.notifyDataSetChanged();
					// ((EmailWriteActivity)mContext).setAdapters();
					// edit.requestFocus();

					// Thread.sleep(2000);
					// edit.showDropDown();
					// this(mContext, cs, edit);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				m_dbAddr2.close();
				m_dbHelper2.close();
			}
		}
	}

	// ((EmailWriteActivity)mContext).setAdapters();
	// this.newDropDownView(mContext, null, mParent);
	// this.runQueryOnBackgroundThread(this.edit.getText());

	/**
	 * 키패드 올리기
	 */
	public void showKeyPad() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	/**
	 * 받는 이 자동완성 선택시 이벤트
	 */
	private AdapterView.OnItemClickListener addrClickedListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

			Cursor c = (Cursor) arg0.getAdapter().getItem(arg2);
			if (c != null) {
				String mail = StringUtil.isNull(c.getString(1)) ? c
						.getString(2) : c.getString(1);
				// EditText e = (EditText)findViewById(R.id.textTo);
				toList += mail + ";";
				realAddrMap.put(mail, c.getString(2));
				et1.setText(toList);
				et1.setSelection(et1.getText().toString().length());
			}
			if (c != null) {
				c.close();
			}
		}
	};

	/**
	 * 참조 자동완성 선택시 이벤트
	 */
	private AdapterView.OnItemClickListener addr2ClickedListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
			Cursor c = (Cursor) arg0.getAdapter().getItem(arg2);
			if (c != null) {
				String mail = StringUtil.isNull(c.getString(1)) ? c
						.getString(2) : c.getString(1);
				ccList += mail + ";";
				realAddrMap.put(mail, c.getString(2));
				et2.setText(ccList);
				et2.setSelection(et2.getText().toString().length());
			}
			if (c != null) {
				c.close();
			}
		}
	};

	/**
	 * 숨은참조 자동완성 선택시 이벤트
	 */
	private AdapterView.OnItemClickListener addr3ClickedListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
			Cursor c = (Cursor) arg0.getAdapter().getItem(arg2);
			if (c != null) {
				String mail = StringUtil.isNull(c.getString(1)) ? c
						.getString(2) : c.getString(1);
				bccList += mail + ";";
				realAddrMap.put(mail, c.getString(2));
				et3.setText(bccList);
				et3.setSelection(et3.getText().toString().length());
			}
			if (c != null) {
				c.close();
			}
		}
	};

	/**
	 * onClick 이벤트 핸들러<br>
	 * - 수신자-구성원검색 - 참조자-구성원검색 - 숨은참조자-구성원검색 - 메일보내기 버튼 - 취소 버튼
	 * 
	 * @param v
	 */
	public void onClick(View v) {
		/*
		 * switch (v.getId()) {
		 * 
		 * case R.id.AddrButton: findViewById(R.id.textSubject).requestFocus();
		 * showCompanyDialog(101); break;
		 * 
		 * case R.id.AddrButton2: findViewById(R.id.textSubject).requestFocus();
		 * showCompanyDialog(102); break;
		 * 
		 * case R.id.AddrButton3: findViewById(R.id.textSubject).requestFocus();
		 * showCompanyDialog(103); break;
		 * 
		 * case R.id.ccBtn: finish(); break;
		 * 
		 * case R.id.requestBtn: EmailClientUtil.hideSoftInputWindow(v);
		 * findViewById(R.id.textSubject).requestFocus(); Log.i("write",
		 * "sendButton pressed"); sendMail(); break;
		 * 
		 * case R.id.bccBtn: // 첨부파일 갯수 제한 웹에서 5개 이상 첨부한 메일 전달하게되면 5개 걸려서 안나간다.
		 * 그래서 주석처리
		 * 
		 * boolean chkCnt = false; if(fileIdList!=null && uploadList != null &&
		 * MAIL_TYPE_FORWARD.equals(type)||MAIL_TYPE_RESEND.equals(type)){ int
		 * chk = uploadList.size()+fileIdList.size(); if(chk>=5)chkCnt = true; }
		 * 
		 * // if(uploadList != null && uploadList.size() >= 5 || chkCnt) {
		 * 
		 * if (uploadList != null && uploadList.size() >= 5) { SKTDialog d = new
		 * SKTDialog(this); d.getDialog("파일 첨부는 5개까지 가능합니다.").show(); return; }
		 * Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		 * intent.setType("video/*"); intent.setDataAndType(
		 * android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		 * "image/*"); startActivityForResult(intent, UPLOAD_FILE); break;
		 * 
		 * case R.id.check1: setCheckedDrawable((Button) v, checkBtn.length);
		 * mailType = "ODNR"; // 업무용 isWork = "1"; break;
		 * 
		 * case R.id.check2: setCheckedDrawable((Button) v, checkBtn.length);
		 * mailType = "PRTT"; // 개인용 isWork = "0"; break; // 옵션 선택(비밀, 답장요구, 긴급)
		 * case R.id.check21 : case R.id.check22 : case R.id.check23: Button
		 * optionChkBtn = (Button)findViewById(v.getId());
		 * if("1".equals(chkBoxDefaultValue2(optionChkBtn.getTag()))){ //
		 * Log.i("write", "checkButton : 1"); optionChkBtn.setTag("");
		 * optionChkBtn
		 * .setBackgroundDrawable(getResources().getDrawable(R.drawable
		 * .check_off)); } else{ // Log.i("write",
		 * "checkButton else............"); optionChkBtn.setTag("1");
		 * optionChkBtn
		 * .setBackgroundDrawable(getResources().getDrawable(R.drawable
		 * .check_on)); } break;
		 * 
		 * 
		 * case R.id.check24: Button optionChkBtn1 = (Button)
		 * findViewById(v.getId()); if
		 * ("0".equals(chkBoxDefaultValue(optionChkBtn1.getTag()))) {
		 * optionChkBtn1.setTag("1");
		 * optionChkBtn1.setBackgroundDrawable(getResources().getDrawable(
		 * R.drawable.check_off)); } else { optionChkBtn1.setTag("0");
		 * optionChkBtn1.setBackgroundDrawable(getResources().getDrawable(
		 * R.drawable.check_on)); } ; break; default: break; }
		 */
		if (v.getId() == R.id.AddrButton) { // 수신자-구성원검색
			findViewById(R.id.textSubject).requestFocus();
			showCompanyDialog(101);

		} else if (v.getId() == R.id.AddrButton2) { // 참조자-구성원검색
			findViewById(R.id.textSubject).requestFocus();
			showCompanyDialog(102);

		} else if (v.getId() == R.id.AddrButton3) { // 숨은참조자-구성원검색
			findViewById(R.id.textSubject).requestFocus();
			showCompanyDialog(103);

		} else if (v.getId() == R.id.ccBtn || v.getId() == R.id.top_cancel ) { // ccBtn
			finish();
		} else if (v.getId() == R.id.requestBtn || v.getId() == R.id.top_send ) { // 메일보내기.
			EmailClientUtil.hideSoftInputWindow(v);
			findViewById(R.id.textSubject).requestFocus();
			sendMail();

		}  
		
		
		else if (v.getId() == R.id.bccBtn) {
			// 첨부파일 갯수 제한 웹에서 5개 이상 첨부한 메일 전달하게되면 5개 걸려서 안나간다. 그래서 주석처리
			/*
			 * boolean chkCnt = false; if(fileIdList!=null && uploadList != null
			 * &&
			 * MAIL_TYPE_FORWARD.equals(type)||MAIL_TYPE_RESEND.equals(type)){
			 * int chk = uploadList.size()+fileIdList.size(); if(chk>=5)chkCnt =
			 * true; }
			 */
			// if(uploadList != null && uploadList.size() >= 5 || chkCnt) {

			if (uploadList != null && uploadList.size() >= 20) {
				SKTDialog d = new SKTDialog(this);
				d.getDialog("파일 첨부는 20개까지 가능합니다.").show();
				return;
			}
			/**마시맬로 버전에서 갤러리에서 사진 선택시 앱이 중지 되서 수정함
			 * 2016.03.16**/
//			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//			intent.setType("video/*");
//			intent.setDataAndType(	android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 	"image/*");
			
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
//
////			kbr 2022.03.31
			intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
			intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

//			startActivityForResult(intent, UPLOAD_FILE);	// 갤러리로 바로 이동
			
//			kbr 2022.04.04	갤러리 선택 탭 띄우기
			startActivityForResult(Intent.createChooser(intent, "선택해주세요."), UPLOAD_FILE);
//			startActivityForResult(Intent.createChooser(intent.setPackage("com.google.android.apps.photos"), "pick photo"), UPLOAD_FILE);	// 포토 app 으로 바로 연결

		} else if (v.getId() == R.id.check1) {
			setCheckedDrawable((Button) v);
			mailType = "ODNR"; // 업무용
			isWork = "1";
		} else if (v.getId() == R.id.check2) {
			setCheckedDrawable((Button) v);
			mailType = "PRTT"; // 개인용
			isWork = "0";
		} else if (v.getId() == R.id.check21 || v.getId() == R.id.check22 || v.getId() == R.id.check23) { // 비밀, 답장요구, 긴급

			Button optionChkBtn = (Button) findViewById(v.getId());
			if ("1".equals(chkBoxDefaultValue2(optionChkBtn.getTag()))) {
				optionChkBtn.setTag("");
				optionChkBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.mail_check_off));
			} else {
				optionChkBtn.setTag("1");
				optionChkBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.mail_check_on));
			}
			;
		} else if (v.getId() == R.id.check24) { // 동보 수신자 숨김
			Button optionChkBtn = (Button) findViewById(v.getId());
			if ("0".equals(chkBoxDefaultValue(optionChkBtn.getTag()))) {
				optionChkBtn.setTag("1");
				optionChkBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.mail_check_off));
			} else {
				optionChkBtn.setTag("0");
				optionChkBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.mail_check_on));
			}
			;
		// kbr 2022.04.18
		} else if (v.getId() == R.id.check31) {	// 내게쓰기

			Button optionChkBtn = (Button) findViewById(v.getId());
			if ("1".equals(chkBoxDefaultValue2(optionChkBtn.getTag()))) {	// 내게쓰기 off
				optionChkBtn.setTag("");
				optionChkBtn.setBackground(getResources().getDrawable(
						R.drawable.mail_check_off));
				if (realAddrMap.containsKey(empName)) {
					realAddrMap.remove(empName);
					String resultList = "";
					for (String name : toList.split(";")) {
						if (!name.equals(empName)) {
							resultList += (name + ";");
						}
					}
					toList = resultList;
				}
			} else {	// 내게쓰기 on
				optionChkBtn.setTag("1");
				optionChkBtn.setBackground(getResources().getDrawable(
						R.drawable.mail_check_on));
				if (!realAddrMap.containsKey(empName)) {
					realAddrMap.put(empName, empId);
					toList += (empName + ";");
					String resultList = "";
					String[] strArr = toList.split(";");
					int index = 0;
					for (int i=0; i<strArr.length; i++) {
						if (i < strArr.length - 1 && strArr[i].equals(empName)) {
							resultList = toList;
							break;
						}
						else if (i == strArr.length-1 && !strArr[i].equals(empName)) {
							toList += (empName + ";");
						}
					}
				}
			}
			;

			et1.requestFocus();
			onFocusChangeTextView(et1, toList);

		}

	}

	private void setCheckedDrawable(Button btn) {
		checkBtn[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.mail_radio_off));
		checkBtn[1].setBackgroundDrawable(getResources().getDrawable(R.drawable.mail_radio_off));
		btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.mail_radio_on));
	}

	/**
	 * 구성원 검색, 폰주소록 Dialog 띄우기
	 * 
	 * @param type
	 */
	private void showCompanyDialog(final int type) {
		final String[] list = getResources().getStringArray(
				R.array.mail_ADDRESS_TEXT);
		AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
		aBuilder.setTitle(getResources().getString(R.string.mail_addr_choose));
		aBuilder.setItems(list, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent approvalIntent = null;
				try {
					approvalIntent = new Intent(EmailWriteActivity.this, AddressTabActivity.class);
					approvalIntent.putExtra("isMultiSelect", true);
					approvalIntent.putExtra("tab", which);
					startActivityForResult(approvalIntent, type);
				} catch (Exception e) {

					e.printStackTrace();
					
					SKTDialog dlg = new SKTDialog(EmailWriteActivity.this,
							SKTDialog.DLG_TYPE_2);
					String msg = StringUtil.format(
							getResources().getString(R.string.mail_MemberSearch),
							Constants.CoreComponent.APP_NM_MEMBER);
					dlg.getDialog(getResources().getString(R.string.mail_dialog_ok),
							msg,

							new DialogButton(0) {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									SKTUtil.goMobileOffice(
											EmailWriteActivity.this,
											Constants.CoreComponent.APP_ID_MEMBER);
								}
							},

							new DialogButton(0) {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).show();
				}
			}
		});

		aBuilder.setCancelable(true);
		aBuilder.create().show();
	}

	/**
	 * 액션 처리 핸들러<br>
	 * - 메일 보내기 요청<br>
	 * 
	 * @param primitive
	 *            액션명
	 * @param args
	 *            파라미터
	 */
	@Override
	protected XMLData onAction(String primitive, String... args)
			throws SKTException {
		//2021.07 메일테스트
		return getXmlData(toList, ccList, bccList);
	}

	/**
	 * 액션 처리를 위한 파라민터 셋팅
	 * 
	 * @param a_to
	 * @param a_cc
	 * @param a_bcc
	 * @return
	 * @throws SKTException
	 */
	public XMLData getXmlData(String a_to, String a_cc, String a_bcc)
			throws SKTException {
		String prim = "";
		Parameters params = null;
		String senderId = EmailClientUtil.id;
		System.out.println("------------------------------------- mail TEST 1----------- : " + a_to);

		/*
		 * if(type != null && (type.equals("reply") || type.equals("replyall")))
		 * { prim = EmailClientUtil.COMMON_MAIL_REPLY; } else if(type != null &&
		 * type.equals("forward")) { prim = EmailClientUtil.COMMON_MAIL_FORWARD;
		 * } else if(type != null && type.equals("save")) { prim =
		 * EmailClientUtil.COMMON_MAIL_SAVE; } else if(type != null &&
		 * type.equals("resend")) { prim = EmailClientUtil.COMMON_MAIL_RESEND; }
		 * else { if(EmailClientUtil.ADV_TEST){ prim =
		 * EmailClientUtil.COMMON_MAILADV_SEND; }else{ prim =
		 * EmailClientUtil.COMMON_MAIL_SEND; } }
		 */
		prim = EmailClientUtil.COMMON_MAILADV_SEND;
		params = new Parameters(prim);
		params.put("userID", EmailClientUtil.nedmsID);
		if (uploadList.size() > 0) {
			// 전달 및 재발송일경우 첨부파일 포함된 경우 +첨부파일 할수 있다. fileIdList (전달, 재발송시 이미
			// 첨부되어있던 첨부파일)
			// 이미 첨부되어있던 첨부파일 fileId 안 넘기고 상세화면에서 file테그 안에 테그들을 String 배열로 서버에
			// 전송한다. 새로 첨부하는 파일은 기존대로 fileId 서버에 넘겨준다.
			if (MAIL_TYPE_FORWARD.equals(type) || MAIL_TYPE_RESEND.equals(type)
					&& fileIdList != null) {
				// 기존 첨부파일+새로된 첨부파일
				/*
				 * String[] fileId = null;
				 * 
				 * int fsize = fileIdList.size(); int usize = uploadkey.length;
				 * int totalsize = usize + fsize; fileId = new
				 * String[usize+fsize]; for (int i = 0; i < totalsize; i++) {
				 * if(i<fsize){ fileId[i] = fileIdList.get(i); }else{ fileId[i]
				 * = uploadkey[i-fsize]; } } params.put("fileID", fileId);
				 */
				params.put("fileContent", setFileContents(filecontents));
				params.put("fileID", uploadkey);

			} else {
				params.put("fileID", uploadkey);
			}

		} else {
			if (MAIL_TYPE_FORWARD.equals(type) || MAIL_TYPE_RESEND.equals(type)) {
				// String[] fileId = null;
				if (fileIdList != null) {
					/*
					 * fileId = new String[fileIdList.size()]; for (int i = 0; i
					 * < fileIdList.size(); i++) { fileId[i] =
					 * fileIdList.get(i); }; params.put("fileID", fileId);
					 */
					params.put("fileContent", setFileContents(filecontents));

				} else {
					params.put("fileID", "");
				}

			} else {
				params.put("fileID", "");
			}
		}

		// 전달, 회신, 재발신
		/*
		 * if(type!=null && !"".equals(type)){ params.put("kind", type);
		 * params.put("mailID", ""); }
		 */
		// 받는사람 셋팅
		System.out.println("------------------------ mail test 201 ");
		StringBuffer rcptTag = new StringBuffer();
		// 받는사람 셋팅
		rcptTag.append(setRcptTag(a_to, RCPT_TO));
		System.out.println("------------------------ mail test 202 ");
		// 참조셋팅
		rcptTag.append(setRcptTag(a_cc, RCPT_CC));
		System.out.println("------------------------ mail test 203 ");
		// 비밀참조 셋팅
		rcptTag.append(setRcptTag(a_bcc, RCPT_BCC));
		System.out.println("------------------------ mail test 204 ");

		StringBuffer postInfo = new StringBuffer();
		postInfo.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		postInfo.append("<NFMailInfoD><contentBinId></contentBinId>");
		postInfo.append("<contentUrl></contentUrl>");

		postInfo.append("<title>" + subject + "</title>");
		postInfo.append("<senderId>" + senderId + "</senderId>");
		postInfo.append("<senderDesc></senderDesc>");
		// 시간넣으면 예약발송이 된다. 빈값으로 넘길것.
		// postInfo.append("<sendTime>"+DateUtil.getNow()+"</sendTime>");
		// postInfo.append("<sendTime>"+DateUtil.getNow("yyyyMMdd HH:mm:ss")+"</sendTime>");
		postInfo.append("<sendTime></sendTime>");
		postInfo.append("<isHtml>1</isHtml>"); // html 0, text 1;
		postInfo.append("<isFast>"
				+ chkBoxDefaultValue2(fastOptionChkBtn.getTag()) + "</isFast>");
		postInfo.append("<isReply>"
				+ chkBoxDefaultValue2(replyOptionChkBtn.getTag())
				+ "</isReply>");
		postInfo.append("<isSecret>"
				+ chkBoxDefaultValue2(secretOptionChkBtn.getTag())
				+ "</isSecret>");
		postInfo.append("<isWork>" + isWork + "</isWork>"); // 업무용 = "1"; 개인용 =
															// "0"
		postInfo.append("<isHide>"
				+ chkBoxDefaultValue(hideOptionChkBtn.getTag()) + "</isHide>");

		postInfo.append(rcptTag.toString());

		Log.i("write", "postInfo:::::::::::" + postInfo.toString());

		params.put("postInfo", postInfo.toString());

		if (MAIL_TYPE_REPLY.equals(type) || MAIL_TYPE_FORWARD.equals(type)
				|| MAIL_TYPE_RESEND.equals(type)) {
			if (detailData != null && !StringUtil.isNull(detailData.getBody())) {
				String tempbody = body
						+ "<br><br><br>----- 원본 메시지 -----<br><br>"
						+ detailData.getBody();
				params.put("content", tempbody);
			} else {
				params.put("content", body);
			}

			params.put("kind", type);
			params.put("mailID", mailID);
		} else {
			params.put("content", body);
		}
		Controller controller = new Controller(this);
		return controller.request(params, true);
	}

	/**
	 * 액션 처리후 UI 설정<br>
	 * finish
	 * 
	 * @param primitive
	 *            액션명
	 */
	@Override
	protected void onActionPost(String primitive, XMLData result, SKTException e) {

		try {
			Log.d("", "primitiveprimitiveprimitive " + primitive);
			Log.d("", "primitiveprimitiveprimitive " + result.get("result"));
			Log.d("", "primitiveprimitiveprimitive " + result.get("nextNonce"));
			Log.d("",
					"primitiveprimitiveprimitive "
							+ result.get("resultMessage"));
		} catch (Exception e2) {
			// TODO: handle exception
		}

		if (e != null) {
			e.alert("확인", this, new DialogButton(0) {
				public void onClick(DialogInterface arg0, int arg1) {

				}
			});

		} else {

			SKTDialog dlg = new SKTDialog(this, SKTDialog.DLG_TYPE_1);
			dlg.getDialog("완료", "메일이 발송되었습니다.", new DialogButton(0) {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}).show();
		}
	}

	private void setUploadUI() {
		if (uploadList != null && uploadList.size() > 0) {
			findViewById(R.id.upload_fileLayout).setVisibility(View.VISIBLE);
			LinearLayout layout = (LinearLayout) findViewById(R.id.upload_FILEATTLIST);
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
				// kbr 2022.04.11
				TextView fileNum = (TextView) findViewById(R.id.fileNumber);
				fileNum.setText( uploadList.size()+ " / 20");
				layout.addView(tempLayout);
			}
		} else {
			findViewById(R.id.upload_fileLayout).setVisibility(View.GONE);
		}
	}

	/**
	 * 액션 처리전 UI 설정
	 * 
	 * @param primitive
	 *            액션명
	 */
	@Override
	protected int onActionPre(String primitive) {
		int ret = 0;
		ret = Action.SERVICE_SENDING;
		return ret;
	}

	private String getFileName(String path) {
		String filename = path.substring(path.lastIndexOf("/") + 1);
		return filename;
	}

	private String[] getRealImagePath(Uri uriPath) {
		String[] proj = { MediaStore.Images.Media.DATA };

		Cursor cursor = getContentResolver().query(uriPath, proj, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		String fullPath = cursor.getString(index); // 파일의 실제 경로
		String fileName = fullPath.substring(fullPath.lastIndexOf("/") + 1);

		return new String[] { fullPath, fileName };
	}


	/**
	 * onActivityResult
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResultX(int requestCode, int resultCode,
			Intent data) {
		if (data == null) {
			return;
		}
		// 첨부파일
		if (requestCode == UPLOAD_FILE) {
			if (resultCode == RESULT_OK) {
				// kbr 2022.03.31
				if (data.getClipData() == null) {	// 1장 고른 경우
					Log.d("EmailWriteActiivty", " - onActivityResult : single choice");
					Uri imgUri = data.getData();
					if (StringUtil.isNull(imgUri.getPath())) {
						return;
					}
					String[] file = getRealImagePath(imgUri); // {파일 실제 경로, 파일 이름}
					for (int a = 0; a < uploadList.size(); a++) {
						String uploadPath = uploadList.get(a).getM_szContent(); // getM_szContent() : 파일 실제 경로(이름 포함)
						if (file[0].equals(uploadPath)) {
							Toast.makeText(this, "동일 파일이 이미 있습니다.", Toast.LENGTH_SHORT).show();
							return;
						}
					}
					EmailFileListData filelist = new EmailFileListData();
					filelist.setM_szName(file[1]);
					filelist.setM_szContent(file[0]);
					filelist.setM_szIsEcm("true");

					uploadList.add(filelist);
				} else {	// 2장 이상 고른 경우
					Log.d("EmailWriteActiivty", " - onActivityResult : multiple choice");
					ClipData clipData = data.getClipData();

					// 총 이미지 개수 5개 초과
					if (clipData.getItemCount() + uploadList.size() > 20) {
						Toast.makeText(this, "파일 첨부는 20개까지 가능합니다.", Toast.LENGTH_SHORT).show();
						return;
					// 총 이미지 개수가 20개 이하
					} else {
						Uri imgUri = null;
						String[] file = null;
						int dupNum = 0;
						boolean dupChk;
						for (int a = 0; a < clipData.getItemCount(); a++) {
							dupChk = false;
							imgUri = clipData.getItemAt(a).getUri();
							if (StringUtil.isNull(imgUri.getPath())) {
								continue;
							}
							file = getRealImagePath(imgUri); // {파일 실제 경로, 파일 이름}
							Log.d("EmailWriteActivity", " - " + dupNum + " file path : " + file[0]);
							Log.d("EmailWriteActivity", " - " + dupNum + " file name : " + file[1]);
							for (int b = 0; b < uploadList.size(); b++) {
								String uploadPath = uploadList.get(b).getM_szContent(); // getM_szContent() : 파일 실제 경로(이름 포함)
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
								Log.d("EmailWriteActivity", " - " + dupNum + " file list id : " + filelist.getM_szId());
								Log.d("EmailWriteActivity", " - " + dupNum + " file list contentId : " + filelist.getM_szContentId());
								Log.d("EmailWriteActivity", " - " + dupNum + " file list isecm : " + filelist.getM_szIsEcm());

								uploadList.add(filelist);
							}
						}
						if (dupNum > 0) {
							Toast.makeText(this, "첨부 파일과 동일한 파일이 " + dupNum + "개 있습니다.", Toast.LENGTH_SHORT).show();
						}
					}
				}
				setUploadUI();
				Log.d("EmailWriteActivity", " - uploadList : " + uploadList.toString());
			}

		} else {

			if (resultCode == RESULT_OK) {
				String[] name = null;
				String[] email = null;
				String[] vvip = null;

				String names = "";
				String emails = "";
				String noEmail = "";

				String[] empId = null;
				String empIds = "";
				String[] nfuId = null;
				
				empId = (String[]) data.getStringArrayExtra("empids");
				nfuId = (String[]) data.getStringArrayExtra("nfuId");
						
				name = data.getStringArrayExtra("names");
				email = data.getStringArrayExtra("emails");
				vvip = data.getStringArrayExtra("vvip");

				Log.i("EmailWriteActivity", "empId:"+empId);
				Log.i("EmailWriteActivity", "nfuId:"+nfuId);
				Log.i("EmailWriteActivity", "name:"+name);
				Log.i("EmailWriteActivity", "email:"+email);
				Log.i("EmailWriteActivity", "vvip:"+vvip);
				
				if (name == null) {
					return;
				}

				for (int a = 0; a < name.length; a++) {
					if (StringUtil.isNull(email[a])) {
						noEmail += name[a] + " ";
					} else {
						// realAddrMap.put(name[a], email[a]);
						realAddrMap.put(name[a], empId[a]);
						names += name[a] + ";";
						emails += email[a] + ";";

						if (vvip != null && vvip[a].equals("true")) {
							vip = name[a];
						}
					}
				}

				noEmail = noEmail.length() > 0 ? noEmail.substring(0, noEmail.length() - 1) : noEmail.toString();

				if (!StringUtil.isNull(noEmail)) {
					SKTDialog dlg = new SKTDialog(this, SKTDialog.DLG_TYPE_1);
					dlg.getDialog("완료", noEmail + "의 메일 주소가  없습니다.",
							new DialogButton(0) {
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
				}

				if (requestCode == 101 || requestCode == 111) {
					/*
					 * toList = ""; Iterator<String> it =
					 * realAddrMap.keySet().iterator(); while(it.hasNext()) {
					 * String key = it.next(); Log.d("yyyyyyyyyyyyyy", key + "="
					 * + realAddrMap.get(key)); toList += key + ";"; }
					 */
					if (!StringUtil.isNull(toList)) {
						if (!toList.substring(toList.length() - 1, toList.length()).equals(";")) {
							toList += ";";
						}
					}

					if (names.indexOf(";") > -1) {
						String[] to = names.split(";");
						for (int i = 0; i < to.length; i++) {
							if (chkDub(to[i]))
								toList = toList + to[i] + ";";
						}
					} else {
						if (chkDub(names))
							toList = toList + names + ";";
					}

					// kbr 2022.04.11
					if (realAddrMap.containsKey(empName)) {
						if (!"1".equals(chkBoxDefaultValue2(sendMyselfBtn.getTag()))) {	// 내게쓰기 on
							sendMyselfBtn.setTag("1");
							sendMyselfBtn.setBackground(getResources().getDrawable(
									R.drawable.mail_check_on));
						}
					}

					et1.requestFocus();
					onFocusChangeTextView(et1, toList);

				} else if (requestCode == 102 || requestCode == 112) {
					if (!StringUtil.isNull(ccList)) {
						if (!ccList.substring(ccList.length() - 1,
								ccList.length()).equals(";")) {
							ccList += ";";
						}
					}

					if (names.indexOf(";") > -1) {
						String[] cc = names.split(";");
						for (int i = 0; i < cc.length; i++) {
							if (chkDub(cc[i]))
								ccList = ccList + cc[i] + ";";
						}
					} else {
						if (chkDub(names))
							ccList = ccList + names + ";";
					}

					et2.requestFocus();
					onFocusChangeTextView(et2, ccList);

				} else if (requestCode == 103 || requestCode == 113) {
					if (!StringUtil.isNull(bccList)) {
						if (!bccList.substring(bccList.length() - 1,
								bccList.length()).equals(";")) {
							bccList += ";";
						}
					}

					if (names.indexOf(";") > -1) {
						String[] bcc = names.split(";");
						for (int i = 0; i < bcc.length; i++) {
							if (chkDub(bcc[i]))
								bccList = bccList + bcc[i] + ";";
						}
					} else {
						if (chkDub(names))
							bccList = bccList + names + ";";
					}

					et3.requestFocus();
					onFocusChangeTextView(et3, bccList);

				}
			}
		}
	}

	private void setEditSelection(AutoCompleteTextView view, String list) {
		Editable etext = view.getText();
		int position = etext.length();
		Selection.setSelection(etext, position);
	}

	/**
	 * View 에 포커스 올때 이벤트 처리
	 * 
	 * @param v
	 * @param hasFocus
	 */
	public void onFocusChange(View v, boolean hasFocus) {
		if (v.getId() == R.id.textTo) {
			if (hasFocus) {
				onFocusChangeTextView(et1, toList);
				setEditSelection(et1, toList);

			} else {
				toList = et1.getText().toString();
				outFocusChangeTextView(et1, toList);
			}

		} else if (v.getId() == R.id.textCc) {
			if (hasFocus) {
				onFocusChangeTextView(et2, ccList);
				setEditSelection(et2, toList);

			} else {
				ccList = et2.getText().toString();
				outFocusChangeTextView(et2, ccList);
			}

		} else if (v.getId() == R.id.textBcc) {
			if (hasFocus) {
				onFocusChangeTextView(et3, bccList);
				setEditSelection(et3, toList);
			} else {
				bccList = et3.getText().toString();
				outFocusChangeTextView(et3, bccList);
			}
		}
	}

	private void onFocusChangeTextView(AutoCompleteTextView view, String list) {
		view.setText(list);
	}

	private void outFocusChangeTextView(AutoCompleteTextView view, String list) {
		String[] text = list.split(";");
		if (text.length > 1) {
			view.setText(text[0] + "외 " + (text.length - 1) + "명");
		}
	}

	/**
	 * 입력이 끝날때 이벤트 처리
	 * 
	 * @param keyCode
	 * @param event
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		/*
		 * EditText tv1 = (EditText)findViewById(R.id.textSubject);
		 * 
		 * if (keyCode == KeyEvent.KEYCODE_ENTER) { if (et1.hasFocus()) {
		 * et2.requestFocus(); return true; } else if (et2.hasFocus()) {
		 * et3.requestFocus(); tv1.requestFocus(); return true; } else if
		 * (et3.hasFocus()) { tv1.requestFocus(); return true; }
		 * 
		 * }
		 */
		return super.onKeyUp(keyCode, event);

	}

	/**
	 * 문자열 체크
	 */
	private void sendMail() {
		Log.i("write", "sendMail()");
		EditText a_name = (EditText) findViewById(R.id.textSubject);
		subject = a_name.getText().toString();

		EditText bodyText = (EditText) findViewById(R.id.textBody);
		// TextView text = (TextView)findViewById(R.id.textSummary);
		body = bodyText.getText().toString();

		SKTDialog dlg = new SKTDialog(this);

		if (toList.trim().equals("")) {
			dlg.getDialog("확인", "받는사람을  입력하세요.", new DialogButton(0) {
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			return;
		} /*
		 * else { if(!mailCk(toList,"받는 사람" , 1)) { return ; } }
		 */

		if (StringUtil.isNull(subject)) {
			dlg.getDialog("확인", "제목을  입력하세요.", new DialogButton(0) {
				public void onClick(DialogInterface dialog, int which) {
					EditText a_name = (EditText) findViewById(R.id.textSubject);
					a_name.setSelected(true);
				}
			}).show();
			return;
		}
		// 본문 입력

		if (body == null || "".equals(body.trim())) {
			dlg.getDialog("확인", "내용을 입력하세요.", new DialogButton(0) {
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			return;
		}

		// 수신자 공개 여부 선택 사용안함.
		/*
		 * if(toList.split(";").length > 1) { final String[] list = new
		 * String[]{"공개", "비공개"}; AlertDialog.Builder aBuilder = new
		 * AlertDialog.Builder(this); aBuilder.setTitle("수신자 공개 여부 선택");
		 * aBuilder.setItems(list, new DialogInterface.OnClickListener() {
		 * public void onClick(DialogInterface dialog, int which) { if(which ==
		 * 0) { openFlag = "true"; } else { openFlag = "false"; }
		 * if(uploadList.size() > 0) { ArrayList<EmailFileListData> reList = new
		 * ArrayList<EmailFileListData>(); for(int a = 0 ; a < uploadList.size()
		 * ; a ++) { EmailFileListData data = uploadList.get(a);
		 * if(data.getM_szIsEcm().equals("false")) { uploadList.remove(a); }
		 * else { reList.add(data); } } uploadList = reList; startUpload();
		 * 
		 * } else { new Action("").execute(); } } });
		 * 
		 * aBuilder.setCancelable(true); aBuilder.create().show();
		 * 
		 * } else {
		 */
		if (uploadList.size() > 0) {
			ArrayList<EmailFileListData> reList = new ArrayList<EmailFileListData>();
			for (int a = 0; a < uploadList.size(); a++) {
				EmailFileListData data = uploadList.get(a);
				if (data.getM_szIsEcm().equals("false")) {
					uploadList.remove(a);
				} else {
					reList.add(data);
				}
			}
			uploadList = reList;
			startUpload();
		} else {
			new Action("").execute();
		}
		// }
	}

	/**
	 * 문자열 체크
	 * 
	 * @param addr
	 * @param text
	 * @param type
	 * @return
	 */
	/*
	 * private boolean mailCk(String addr , String text , int type) {
	 * if(!StringUtil.isNull(addr)) { String[] d = addr.split(";");
	 * 
	 * for(int i=0; i<d.length; i++) {
	 * 
	 * if(!realAddrMap.containsKey(d[i])) {
	 * if(!d[i].matches("^[a-zA-Z0-9_+.-]+@([a-z0-9-]+\\.F)+[a-z0-9]{2,4}$")) {
	 * SKTDialog dia = new SKTDialog(this, SKTDialog.DLG_TYPE_1);
	 * dia.getDialog(text + "에 잘못된 문자가 있습니다.").show(); // if(type == 1) { //
	 * toList = ""; // } else if(type == 2) { // ccList = ""; // } else { //
	 * bccList = ""; // }
	 * 
	 * return false; } } } } return true;
	 * 
	 * }
	 */
	/**
	 * 설정할 레이아웃을 리턴한다.
	 */
	@Override
	protected int assignLayout() {
		return R.layout.mail_emailwrite;
	}

	/**
	 * 키 이벤트
	 * 
	 * @param v
	 * @param keyCode
	 * @param event
	 * @return
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		EditText textTo = (EditText) v;

		if (event.getAction() == KeyEvent.ACTION_DOWN) {

			if (keyCode == KeyEvent.KEYCODE_DEL) {

				String curText = textTo.getText().toString();
				String newText = "";
				String selText = "";

				if (curText != null && curText.length() > 0) {
					if (curText.indexOf(";") < 0) {
						return false;
					} else {
						int prevPos = 0;
						int nextPos = 0;

						int curCsPos = textTo.getSelectionEnd(); // cursor의
																	// 현재위치.

						if (curCsPos == 0)
							return false;

						String lastToken = curText.substring(curCsPos - 1,
								curCsPos);

						if (lastToken.equals(";")) {
							nextPos = curCsPos - 1;
							if (curCsPos == 1)
								return false;

							prevPos = curText.substring(0, curCsPos - 2)
									.lastIndexOf(";");
						} else {

							prevPos = curText.substring(0, curCsPos)
									.lastIndexOf(";");
							nextPos = curText.substring(curCsPos).indexOf(";");
							if (nextPos < 0)
								nextPos = curText.length();
							else {
								nextPos += curCsPos;
							}

						}
						selText = curText.substring(prevPos + 1, nextPos);

						if (realAddrMap.get(selText) == null
								|| realAddrMap.get(selText).equals("")) {
							return false;
						} else {
							realAddrMap.remove(selText);
							newText = curText.substring(0, prevPos + 1)
									+ curText.substring(nextPos + 1);
							if (v.getId() == R.id.textTo) {
								toList = newText;

								// kbr 2022.04.11
								if (selText.equals(empName)) {
									if ("1".equals(chkBoxDefaultValue2(sendMyselfBtn.getTag()))) {	// 내게쓰기 off
										sendMyselfBtn.setTag("");
										sendMyselfBtn.setBackground(getResources().getDrawable(
												R.drawable.mail_check_off));
									}
								}

								et1.requestFocus();
								onFocusChangeTextView(et1, toList);

							} else if (v.getId() == R.id.textCc) {
								ccList = newText;
								et2.requestFocus();
								onFocusChangeTextView(et2, ccList);

							} else if (v.getId() == R.id.textBcc) {
								bccList = newText;
								et3.requestFocus();
								onFocusChangeTextView(et3, bccList);

							}
							// textTo.setText(newText);

							Editable etext = textTo.getText();
							Selection.setSelection(etext, prevPos + 1);

							return true;
						}
					}
				}

				return false;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 받는이 없을시 버튼 dim 처리
	 */
	@Override
	public void afterTextChanged(Editable s) {
		// Button button = (Button)findViewById(R.id.requestBtn);
		// EditText e = (EditText)findViewById(R.id.textTo);
		// if(StringUtil.isNull(e.getText().toString())) {
		// button.setTextColor(getResources().getColor(R.color.disable_btn_text));
		// button.setBackgroundResource(R.drawable.btn_softkey_d);
		// button.setClickable(false);
		// }else{
		// button.setClickable(true);
		// button.setTextColor(getResources().getColorStateList(R.color.dateil_btn_text_selector));
		// button.setBackgroundResource(R.drawable.btn_softkey_selector);
		// }
	}

	/**
	 * 메일 입력 문자열 체크
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
		et1.setFilters(new InputFilter[] { new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				Log.d("xxxxxxxxxxx", "xxxxxxxxxxxx    " + source + "  " + dest);
				if (source.toString().getBytes().length > 3) {
					return source;
				} else {
					return "";
				}
			}
		} });
		et2.setFilters(new InputFilter[] { new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
				Log.d("xxxxxxxxxxx", "xxxxxxxxxxxx    " + source + "  " + dest);
				if (source.toString().getBytes().length > 3) {
					return source;
				} else {
					return "";
				}
			}
		} });
		et3.setFilters(new InputFilter[] { new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
				Log.d("xxxxxxxxxxx", "xxxxxxxxxxxx    " + source + "  " + dest);
				if (source.toString().getBytes().length > 3) {
					return source;
				} else {
					return "";
				}
			}
		} });
	}

	/**
	 * onTextChanged 메소드
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// Iterator<String> it = realAddrMap.keySet().iterator();
		// while(it.hasNext()) {
		// String key = it.next();
		// Log.d("yyyyyyyyyyyyyy", key + "=" + realAddrMap.get(key));
		// }
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				Log.d("","emailwrite handler msg 1");
				int progress = msg.getData().getInt("progress");
				progDialog.setProgress(progress);
			} else if (msg.arg1 == 2) {
				Log.d("","emailwrite handler msg 2");
				progDialog.dismiss();
				String uploadKey = msg.getData().getString("uploadKey");
				int file_index = msg.getData().getInt("file_index");
				uploadkey[file_index] = uploadKey;
				if (uploadList.size() - 1 == file_index) {
					new Action("").execute();
				} else {
					startProgressThread(file_index + 1);
				}
			}
		}
	};

	private void startUpload() {
		uploadkey = new String[uploadList.size()];
		if (uploadList.size() > 0) {
			startProgressThread(0);
		}
	}

	private void startProgressThread(int file_index) {
		progDialog = new ProgressDialog(EmailWriteActivity.this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progDialog.setMax(100);
		progDialog.setMessage("업로드 중 ...\n잠시 기다려주시기 바랍니다.");
		progDialog.setCancelable(false);
		progDialog.show();
		progThread = new ProgressThread(handler, file_index);
		progThread.start();

	}


	private class ProgressThread extends Thread {

		Handler mHandler;
		int file_index;

		ProgressThread(Handler h, int file_index) {
			mHandler = h;
			this.file_index = file_index;
		}


		@Override
		public void run() {
			String absolutePath = uploadList.get(file_index).getM_szContent();
			String uploadkey = "";
			Controller c = new Controller(EmailWriteActivity.this);
			XMLData result = null;
			try {
				Log.d("emailwrite","emailwrite absolutePath="+absolutePath);
				Log.d("emailwrite","emailwrite absolutePath="+absolutePath);
				c.setConnTimeout(60);
				c.setReadTimeout(60);
				result = c.MultiPartUpload(absolutePath, new Controller.OnProgressChanged() {

							@Override
							public void onProgressChanged(int progress) {
								Log.d("TEST", "Upload Progress: " + progress);
								Message msg = mHandler.obtainMessage();
								Bundle b = new Bundle();
								b.putInt("progress", progress);
								msg.setData(b);
								msg.arg1 = 1;
								mHandler.sendMessage(msg);
							}
						});
				uploadkey = result.get("uploadFileKey");

			} catch (SKTException e) {
				e.printStackTrace();
				uploadkey = "";
			}
			Log.d("emailwrite", "emailwrite uploadkey = " + uploadkey);

			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putString("uploadKey", uploadkey);
			b.putInt("file_index", file_index);
			msg.setData(b);
			msg.arg1 = 2;
			mHandler.sendMessage(msg);
		}
	}

	private String chkBoxDefaultValue(Object chkValue) {
		String returnValue = "0";
		if (null == chkValue || "".equals(chkValue.toString())) {
			returnValue = "1";
		} else {
			returnValue = chkValue.toString();
		}
		return returnValue;
	}

	/** 값이 1이 아니면 "" 넘긴다. 긴급여부,답장요구,비밀편지사용 **/
	private String chkBoxDefaultValue2(Object chkValue) {
		String returnValue = "";
		if (chkValue != null && "1".equals(chkValue.toString())) {
			returnValue = chkValue.toString();
		} else {
			returnValue = "";
		}
		return returnValue;
	}

	private String setRcptTag(String paramValue, String paramKind) {
		StringBuffer rcptTag = new StringBuffer();
		//2021.07 안드로이드 테스트
		if (!StringUtil.isNull(paramValue)) {

			String[] to = paramValue.trim().split(";");
			String[] cc = new String[to.length];
			for (int i = 0; i < to.length; i++) {
				if (!StringUtil.isNull(realAddrMap.get(to[i]))) {
					cc[i] = realAddrMap.get(to[i]);
					rcptTag.append("<rcpt><NFMailRecipientD>");
					rcptTag.append("<rcptKind>" + paramKind + "</rcptKind>"); // rcptKind
																				// 1
																				// 수신,
																				// 2
																				// 참조,
																				// 3
																				// 비밀참조
					rcptTag.append("<nodeKind>1</nodeKind>");
					rcptTag.append("<idOrEmail>" + cc[i].toString()+ "</idOrEmail>");
					rcptTag.append("<alias>" + to[i] + "</alias>");
					rcptTag.append("</NFMailRecipientD></rcpt>");


				}
			}
		}
		return rcptTag.toString();
	}

	// 중복확인
	private Boolean chkDub(String paramAdd) {
		Boolean returnValue = false;

		if (toList.indexOf(paramAdd) == -1 && ccList.indexOf(paramAdd) == -1
				&& bccList.indexOf(paramAdd) == -1)
			returnValue = true;

		return returnValue;
	}

	private String[] setFileContents(ArrayList<FileContents> paramValue) {
		String[] returnValue = null;
		if (paramValue != null) {
			returnValue = new String[paramValue.size()];

			for (int i = 0; i < returnValue.length; i++) {
				StringBuffer temp = new StringBuffer();
				FileContents fc = paramValue.get(i);

				temp.append("<file><NFSimpleAttachD>");
				temp.append("<oper>" + fc.getOper() + "</oper>");
				//temp.append(fc.getKind());
				temp.append("<kind>" + fc.getKind() +"</kind>");					//2021.07 메일 TEST
				temp.append("<id>" + fc.getId() + "</id>");
				temp.append("<seq>" + fc.getSeq() + "</seq>");
				temp.append("<name>" + fc.getName() + "</name>");
				temp.append("<desc>" + fc.getDesc() + "</desc>");
				temp.append("<len>" + fc.getLen() + "</len>");
				temp.append("<path>"  + "</path>");
				temp.append("<url>" + fc.getUrl() + "</url>");			//2021.07 메일 TEST
				temp.append("</NFSimpleAttachD></file>");


				returnValue[i] = temp.toString();
				System.out.println("------------------temp -------- " + temp.toString());
				System.out.println("------------------fc.getKind() -------- " + fc.getKind());

			}
		}
		//2021.07 메일 TEST
		System.out.println("---------------------- mail TEST returnValue " + returnValue.toString());
		return returnValue;
	}

}
