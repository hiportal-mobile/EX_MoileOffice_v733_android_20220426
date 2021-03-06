package com.ex.group.mail.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamsecurity.util.DEFCrypto;
import com.dreamsecurity.util.InvalidFormatException;
import com.ex.group.mail.data.EmailDatabaseHelper;
import com.ex.group.mail.data.EmailMainListData;
import com.ex.group.mail.data.EmailReceiveListData;
import com.ex.group.mail.data.ExceptionWrapper;
import com.ex.group.mail.service.EmailReceiveThread;
import com.ex.group.mail.util.EmailClientUtil;
import com.ex.group.mail.util.HttpConnection;
import com.ex.group.mail.widget.EmailReceiveListAdapter;
import com.ex.group.mail.widget.EmailSecurityDialog;
import com.skt.pe.common.activity.SKTActivity;
import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.conf.EnvironManager;
import com.skt.pe.common.conf.Resource;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.dialog.DialogButton;
import com.skt.pe.common.dialog.SKTDialog;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.service.Controller;
import com.skt.pe.common.service.Parameters;
import com.skt.pe.common.service.XMLData;
import com.skt.pe.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import com.ex.group.folder.R;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * ?????? ????????? ??????
 *
 * @author sjsun5318
 *
 */
public class EmailReceiveActivity extends SKTActivity implements
        OnItemClickListener, OnClickListener, OnKeyListener {

    String TAG = "EmailReceiveActivity";

	private EmailReceiveListAdapter adapter = null;
	private EmailReceiveListAdapter adapter_search = null;
	private EmailReceiveListAdapter adapter_del = null;
	private LinearLayout moreBtn = null;
	private LinearLayout receivelayout = null;
	private LinearLayout receivelayout_search = null;
	private LinearLayout receivelayout_del = null;
	private LinearLayout searchlayout = null; // ?????? ????????????
	private LinearLayout prioritylayout = null; // ?????? ????????? ?????? ????????????
	private Intent intent = null;
	private ListView receiveList = null;
	private ListView receiveList_search = null;
	private ListView receiveList_del = null;
	private TextView lastupdata = null;
	private EditText searchEdit = null;
	private EmailMainListData mainData = null;
	private Menu mMenu = null;
	private ImageButton cm_send_email_btn = null;	// kbr 2022.04.13
	private int receivePosition = 0;
	private int searchPosition = 0;
	private int pageNumber = 1; // ?????? ?????????
	private int totalPageCnt = 1; // ?????? ????????????
	private int unReadPageCnt = 1; // ????????? ????????????
	private int selectedIndex = -1; // ?????????Index
	private int searchPageNumber = 1;
	private int searchTotalPageCnt = 0;
	private int totalItemCnt = 0; // ?????? ?????????
	private int search = 0;
	private int isdel = 0;

	private String messageText = "";
	private String boxName = "";
	private String boxType = "";
	private String m_szCurSearch = null;
	private Button SearchTypeBtn = null;

	// private boolean detailFlag = false;
	private List<String> checkedMailId;
	private List<String> checkedMailChangekey;

	private BroadcastReceiver r;

	private final int TOTAL = 0;
	private final int UNREAD = 1;
	private final int FIRSTID = 2;
	private final int LASTID = 3;
	private final int BRM_DOC_HOST = 4;
	private final int MAIN_BRMHOST = 5;
	private final int TOTAL_SIZE = 6;

	private final int CHECK = 0;
	private final int READMAIL = 1;
	private final int BLINK = 2;
	private final int ATTACH = 3;
	private final int TITLE = 4;
	private final int SENDER = 5;
	private final int RECIEVEDATE = 6;
	private final int SIZE = 7;
	private final int IMPORTANT = 8;
	private final int WORK = 9;

	private final int P_CHECK = 0;
	private final int P_RECVNUM = 1;
	private final int P_ATTACH = 2;
	private final int P_TITLE = 3;
	private final int P_SENDER = 4;
	private final int P_SAVEDATE = 5;
	private final int P_SIZE = 6;
	private final int P_BOXNAME = 7;

	/**
	 * EmailReceiveThread ?????? ???????????? handling
	 */
	private Handler mHandler = new Handler(new Callback() {
		@SuppressWarnings("unchecked")
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == EmailClientUtil.START_RUNABLE) {
				// UI
				ArrayList<EmailReceiveListData> list = (ArrayList<EmailReceiveListData>) msg.obj;
				Log.i("EmailReceiveActivity", "list.size====>>>>"+list.size());
				if (pageNumber == 1) {
					clearAdapter(adapter);
				}
				for (EmailReceiveListData data : list) {
					adapter.add(data);
				}
				adapter.notifyDataSetChanged();
				Log.d("xxxxxxxxxxxxx", "   " + receivePosition);
				receivePosition = receiveList.getFirstVisiblePosition();
				Log.d("xxxxxxxxxxxxx", "   " + receivePosition);
				if (list.size() > 0) {
					mainData.setUnreadCnt(list.get(0).getUnreadCnt());
				}
				setUI();

			} else if (msg.what == EmailClientUtil.SKT_EXCEPTION) {
				// ???????????? ??????

				progressUI(false);

				// Error
				SKTException e = (SKTException) msg.obj;
				onCommonError(e);
				e.alert(EmailReceiveActivity.this);
			}
			return false;
		}
	});

	private void setUpdateStart() {
		findViewById(R.id.update_progress).setVisibility(View.VISIBLE);
		findViewById(R.id.updateBtn).setVisibility(View.GONE);
	}

	private void setUpdateEnd() {
		findViewById(R.id.update_progress).setVisibility(View.GONE);
		findViewById(R.id.updateBtn).setVisibility(View.VISIBLE);
	}

	private void progressUI(boolean progress) {
		if (!progress) {
			setUpdateEnd();
			// setFooterview(R.string.MORE_TEXT_1);
			// TextView text = (TextView)moreBtn.findViewById(R.id.moreButton);
			// text.setText(getResources().getString(R.string.more_button));

			if (mMenu != null) {
				mMenu.findItem(R.id.MENUREFRESH).setEnabled(true);
				// mMenu.findItem(R.id.MENUSET).setEnabled(true);
			}

			setBtnUI((Button) findViewById(R.id.ALLDel), true);
			setBtnUI((Button) findViewById(R.id.Del), true);

		} else {
			setBtnUI((Button) findViewById(R.id.ALLDel), false);
			setBtnUI((Button) findViewById(R.id.Del), false);
		}
	}

	/**
	 * ??????????????? ????????? SEQ null ??????
	 *
	 * @return
	 */
	private String getSeq() {
		return mainData == null ? "" : mainData.getId();
	}

	/**
	 * onDestroy ?????????
	 */
	@Override
	protected void onDestroy() {

		if (r != null) {
			unregisterReceiver(r);
		}
		super.onDestroy();
	}

	/**
	 * onCreate ?????????
	 *
	 * @param savedInstanceState
	 */
	@Override
	public void onCreateX(Bundle savedInstanceState) {
		try {

			receiveList = (ListView) findViewById(R.id.receiveList);
			receiveList_search = (ListView) findViewById(R.id.receiveList_search);
			receiveList_del = (ListView) findViewById(R.id.receiveList_del);
			lastupdata = (TextView) findViewById(R.id.lastupdate);
			searchEdit = (EditText) findViewById(R.id.SearchInputText);
			moreBtn = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.mail_board_more, null);
			receivelayout = (LinearLayout) findViewById(R.id.receivelayout);
			receivelayout_search = (LinearLayout) findViewById(R.id.receivelayout_search);
			receivelayout_del = (LinearLayout) findViewById(R.id.receivelayout_del);
			searchlayout = (LinearLayout) findViewById(R.id.lly_searchlayout);
			prioritylayout = (LinearLayout) findViewById(R.id.priority_layout);
			SearchTypeBtn = (Button) findViewById(R.id.SearchTypeBtn);
			adapter = new EmailReceiveListAdapter(this, R.layout.mail_list_item);
			adapter_search = new EmailReceiveListAdapter(this, R.layout.mail_list_item);
			adapter_del = new EmailReceiveListAdapter(this, R.layout.mail_list_item);

			cm_send_email_btn = (ImageButton) findViewById(R.id.common_send_email_btn);	// kbr 2022.04.13

			messageText = "??? ???????????? ????????????.";

			EmailClientUtil.companyCd = StringUtil.isNull(SKTUtil.getCheckedCompanyCd(this)) ? EnvironManager.getTestCompanyCd() : SKTUtil.getCheckedCompanyCd(this);
			EmailClientUtil.mdn = StringUtil.isNull(SKTUtil.getMdn(this)) ? "mdn": SKTUtil.getMdn(this);

			receiveList.setAdapter(adapter);
			receiveList_search.setAdapter(adapter_search);
			receiveList_del.setAdapter(adapter_del);

			findViewById(R.id.updateBtn).setOnClickListener(this);
			findViewById(R.id.btn_sesarch_selector).setOnClickListener(this);
			moreBtn.setOnClickListener(this);
			SearchTypeBtn.setOnClickListener(this);
			receiveList.setOnItemClickListener(this);
			receiveList_search.setOnItemClickListener(this);
			receiveList_del.setOnItemClickListener(this);

			cm_send_email_btn.setOnClickListener(this);	// kbr 2022.04.13

			findViewById(R.id.ALLDel).setOnClickListener(this);
			findViewById(R.id.Del).setOnClickListener(this);

			// Build.MODEL

			searchEdit.setOnClickListener(this);
			searchEdit.setOnKeyListener(this);

			// registerForContextMenu((ListView)findViewById(R.id.receiveList));

			if (EmailReceiveThread.getRunning()) {
				EmailReceiveThread.runningThread.interrupt();
			}

			if (!EmailDatabaseHelper.existReceiveTableList()) {
				synchronized (EmailReceiveActivity.class) {
					EmailDatabaseHelper helper = new EmailDatabaseHelper(this);
					helper.setReceiveTableList();
					helper.close();
				}
			}

			Intent intent = getIntent();

			if (intent.getParcelableExtra("mainData") != null) {

				mainData = (EmailMainListData) intent
						.getParcelableExtra("mainData");
				boxType = mainData.getBoxType();
				Log.i("EmailReceiveActivity", "boxType : "+boxType);

				/**
				 * ???????????????, ?????????????????? ?????? box name : boxType ???????????????-????????? : I1 ??????????????? : I2
				 * ??????????????? : S ????????? : P
				 * ?????? ?????????????????? ????????? ??????
				 * **/
				if ("I2".equals(boxType)) {
					prioritylayout.setVisibility(View.VISIBLE);
				}

				if ("I1".equals(boxType) || "I2".equals(boxType) || "S".equals(boxType)) {
					searchlayout.setVisibility(View.VISIBLE);
				} else {
					searchlayout.setVisibility(View.GONE);
				}
			}
			// Ui??????
			r = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String id = "";
					int COMMAND = 0;
					String cnt = "";
					ExceptionWrapper e = null;
					if (intent.getAction().equals("EMAIL_CLIENT")) {
						if (intent.getStringExtra("SEQ") != null) {
							id = intent.getStringExtra("SEQ");
						}

						if (intent.getSerializableExtra("EXCEPTION") != null) {
							e = (ExceptionWrapper) intent.getSerializableExtra("EXCEPTION");
						}
						COMMAND = intent.getIntExtra("COMMAND", 0);
					}

					if (getSeq() != null && !getSeq().equals("")
							&& !getSeq().equals(id)) {
						return;
					}

					if (COMMAND == EmailClientUtil.START_RUNABLE) {
						setUpdateStart();

						if (mMenu != null) {
							mMenu.findItem(R.id.MENUREFRESH).setEnabled(false);
							// mMenu.findItem(R.id.MENUSET).setEnabled(false);
						}
						setBtnUI((Button) findViewById(R.id.ALLDel), false);
						setBtnUI((Button) findViewById(R.id.Del), false);

					} else if (COMMAND == EmailClientUtil.END_RUNABLE) {
						setUpdateEnd();

						// setFooterview(R.string.MORE_TEXT_1);
						receivePosition = receiveList.getFirstVisiblePosition();
						if (isdel == 0 && search == 0) {
							Log.d("xxxxxxxxxxxxxx",
									"xxxxxxx   " + intent.getStringExtra("CNT"));

							// if(boxType.equals("I1")) {
							// EmailDatabaseHelper helper = new
							// EmailDatabaseHelper(EmailReceiveActivity.this);
							// try {
							// helper.updateTable(getSeq(), new
							// String[]{"BOX_UNREAD_COUNT"} , new
							// String[]{intent.getStringExtra("CNT")});
							//
							// } catch (Exception e3) {
							// e3.printStackTrace();
							// } finally {
							// helper.close();
							// }
							// }
							setList(id);
						}

						if (mMenu != null) {
							mMenu.findItem(R.id.MENUREFRESH).setEnabled(true);
						}
						setBtnUI((Button) findViewById(R.id.ALLDel), true);
						setBtnUI((Button) findViewById(R.id.Del), true);

					} else if (COMMAND == EmailClientUtil.NO_TABLE_ERROR) {
						// ???????????? ??????
						progressUI(false);
					} else if (COMMAND == EmailClientUtil.SKT_EXCEPTION) {
						// ???????????? ??????
						progressUI(false);
						onCommonError(e.getE());
					}
				}
			};

			registerReceiver(r, new IntentFilter("EMAIL_CLIENT"));

			setUpdateStart();
			messageText = " "+ getResources().getString(R.string.mail_LODING_MESSAGE);

			if (EmailReceiveThread.getRunning()) {
				EmailReceiveThread.runningThread.interrupt();
			}

			// ????????? ?????????
			if (mainData == null || !EmailDatabaseHelper.existReceiveTableList(mainData.getId())) {
				EmailReceiveThread thread = new EmailReceiveThread(this, mHandler, mainData, 1, EmailReceiveThread.TYPE_1);
				thread.start();
				Log.i("EmailReceiveActivity", "????????? ?????????");
				setUI();
				// ????????? ??????
			} else {
				// DB Select, UI
				setAdapterList(mainData.getId());
				Log.i("EmailReceiveActivity", "????????? ??????");
				EmailReceiveThread thread = new EmailReceiveThread(this, mHandler, mainData, 1, EmailReceiveThread.TYPE_2);
				thread.start();

			}

			// searchlayout.setVisibility(View.GONE);
		} catch (SKTException e) {
			SKTUtil.runGMPLogin(this);
		}
	}

	private void setBtnUI(Button btn, boolean bool) {
		if (bool) {
			// btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_picture_selector1));
			btn.setTextColor(getResources().getColorStateList(
					R.color.mail_dateil_btn_text_selector));
			btn.setEnabled(bool);
		} else {
			// btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_softkey_d));
			btn.setTextColor(getResources().getColor(R.color.mail_disable_btn_text));
			btn.setEnabled(bool);
		}
	}

/*
	 * (non-Javadoc)
	 *
	 * @see com.skt.pe.common.activity.SKTActivity#OnAuthDone()
	 */
	/*@Override
	public void OnAuthDone() {
		blocking(-1);
		onCreateX(null);
	}
*/
	/**
	 * ???????????? adapter ??????
	 *
	 * @param id
	 */
	public void setAdapterList(String id) {

		if (mainData != null) {
			lastupdata.setText(mainData.getUpdatedate());
		}
		// setFooterview(R.string.MORE_TEXT_1);
		ArrayList<EmailReceiveListData> list = null;
		EmailDatabaseHelper helper = new EmailDatabaseHelper(
				EmailReceiveActivity.this);
		try {
			list = helper.selectReceiveTable(id, 1, pageNumber * 20);
		} finally {
			helper.close();
		}

		if (list != null && list.size() > 0) {
			totalPageCnt = Integer.parseInt(list.get(0).getTotalCnt());
			clearAdapter(adapter);
			for (EmailReceiveListData data : list) {
				adapter.add(data);
			}
		}

		adapter.notifyDataSetChanged();
		setUI();
	}

	/**
	 * ?????? ????????? ?????? ???????????? ??????
	 *
	 * @param a_szprim
	 * @param date
	 * @return
	 * @throws SKTException
	 */
	public XMLData getXmlData(String a_szprim) throws SKTException {
		Parameters params = new Parameters(a_szprim);

		if (a_szprim.equals(EmailClientUtil.COMMON_MAIL_LIST)) {
			/*
			 * String searchText = SearchTypeBtn.getText().toString();
			 * //2014-02-06 JSJ ?????? ?????????,?????? ?????? params.put("empId",
			 * EmailClientUtil.id); params.put("empName",
			 * EmailClientUtil.empNm); params.put("searchType",
			 * searchText.equals("??????") ? "A2" : "A1"); params.put("searchWord",
			 * m_szCurSearch); params.put("ID", mainData.getBoxId());
			 * params.put("page",Integer.toString(searchPageNumber));
			 * params.put("mailBoxType" , "F"); params.put("countPerPage" ,
			 * Integer.toString(EmailClientUtil.COUNTPERPAGE));
			 */

		} else if (a_szprim.equals(EmailClientUtil.COMMON_MAIL_ISREAD)) {
			// 2014-02-06 JSJ ?????? ?????????,?????? ??????
			params.put("empId", EmailClientUtil.id);
			params.put("empName", EmailClientUtil.empNm);
			params.put("ID", adapter.getItem(selectedIndex).getMailId());
			params.put("CHANGEKEY", adapter.getItem(selectedIndex)
					.getMailChangeKey());
			params.put("ISREAD", adapter.getItem(selectedIndex).getIsRead()
					.equals("true") ? "false" : "true");

		} else if (EmailClientUtil.COMMON_MAILADV_DEL.equals(a_szprim)) {

			params.put("userID", EmailClientUtil.nedmsID);

			String paramMailId = setParamMaild(checkedMailId);
			params.put("mailID", paramMailId);

			// params.put("getBoxType", mainData.getBoxType());
			if (mainData.getBoxType() != null
					&& mainData.getBoxType().equals("D")) {
				// ????????????
				params.put("isHardDel", "Y");
			} else {
				// ??????
				if ("sent".equals(EmailClientUtil.getMailFolderKind(mainData
						.getBoxType()))) {
					params.put("mailFolderKind", "send"); // sent => send??? ??????
				} else {
					params.put("mailFolderKind", EmailClientUtil
							.getMailFolderKind(mainData.getBoxType()));
				}
				params.put("isHardDel", "N");
			}

		} else if (EmailClientUtil.COMMON_MAIL_DEL.equals(a_szprim)) {
			// 2014-02-06 JSJ ?????? ?????????,?????? ??????
			params.put("empId", EmailClientUtil.id);
			params.put("empName", EmailClientUtil.empNm);
			String[] mailIds = new String[checkedMailId.size()];
			String[] changeKeys = new String[checkedMailChangekey.size()];
			mailIds = checkedMailId.toArray(mailIds);
			changeKeys = checkedMailChangekey.toArray(changeKeys);
			params.put("ID", mailIds);
			params.put("CHANGEKEY", changeKeys);
			params.put("mailFolderId", mainData.getBoxId());

			if (mainData.getBoxType() != null
					&& mainData.getBoxType().equals("D")) {
				params.put("isHardDel", "true");
			} else {
				params.put("isHardDel", "false");
			}
		} else if (EmailClientUtil.COMMON_MAILADV_LIST.equals(a_szprim)) {
			// String editSearch = searchEdit.getText().toString();
Log.i("EmailReceiveActivity", "userID =============="+ EmailClientUtil.nedmsID);
			params.put("userID", EmailClientUtil.nedmsID);
			String mailFolderKind = EmailClientUtil.getMailFolderKind(mainData.getBoxType());
			params.put("mailFolderKind", mailFolderKind);

			if (m_szCurSearch != null && !"".equals(m_szCurSearch)&& m_szCurSearch.length() > 1) {

				String searchBtn = SearchTypeBtn.getText().toString();

				if ("?????????".equals(searchBtn)) {
					searchBtn = "sender";
				} else {
					searchBtn = "title";
				}

				params.put("paging", searchPageNumber + "");
				params.put("searchKind", searchBtn);
				params.put("searchText", m_szCurSearch);
			} else {
				params.put("paging", Integer.toString(pageNumber));
			}
		}

		Controller controller = new Controller(this);
		return controller.request(params, true);
	}

	/**
	 * ??????????????? ????????? ??????
	 *
	 * @param xmlData
	 */
	public void setListUI(XMLData xmlData) {
		try {
			/*
			 * String total = xmlData.get("total"); //???????????? ??? String unread =
			 * xmlData.get("unread"); //????????? ??? ?????? String total_size =
			 * xmlData.get("TOTAL_SIZE"); //??? ????????????
			 */
			/*
			 * ArrayList<EmailReceiveListData> listData = new
			 * ArrayList<EmailReceiveListData>(); xmlData.setList("otherinfo");
			 * searchTotalPageCnt = Integer.parseInt(xmlData.get("TOTAL_SIZE"));
			 *
			 * xmlData.setList("record");
			 *
			 * for(int i=0; i<xmlData.size(); i++) {
			 *
			 * EmailReceiveListData data = new EmailReceiveListData();
			 * data.setMdn(mainData.getMdn());
			 * data.setCompanyCd(mainData.getCompanyCd());
			 * data.setBoxType(mainData.getBoxType());
			 * data.setParentBoxType(mainData.getParentBoxType());
			 * data.setBoxId(mainData.getBoxId());
			 * data.setBoxChangKey(mainData.getBoxChangeKey());
			 * data.setBoxNmae(mainData.getBoxName());
			 * //data.setTotalCnt(mainData.getTotalCnt());
			 * data.setUpdateDate(mainData.getUpdatedate());
			 * data.setMailId(StringUtil.isNull(xmlData.get(i, "mailId")) ? "" :
			 * xmlData.get(i, "mailId"));
			 * data.setMailChangeKey(StringUtil.isNull(xmlData.get(i,
			 * "changeKey")) ? "" : xmlData.get( i , "changeKey"));
			 * data.setMailSubject(StringUtil.isNull(xmlData.get(i,
			 * "mailTitle")) ? "????????????" :xmlData.get(i, "mailTitle"));
			 * data.setHasAttachments(StringUtil.isNull(xmlData.get(i,
			 * "hasAttachments")) ? "" : xmlData.get( i , "hasAttachments"));
			 * data.setReceivedDate(StringUtil.isNull(xmlData.get(i,
			 * "receivedDate")) ? "" : xmlData.get( i , "receivedDate"));
			 * data.setSendDate(StringUtil.isNull(xmlData.get(i, "sendDate")) ?
			 * "" : xmlData.get( i , "sendDate"));
			 * data.setMailType(StringUtil.isNull(xmlData.get(i, "mailType")) ?
			 * "" : xmlData.get(i, "mailType"));
			 * data.setIsRead(StringUtil.isNull(xmlData.get(i, "isRead")) ? "" :
			 * xmlData.get( i , "isRead"));
			 *
			 * XMLData childXmlData = xmlData.getChild(i);
			 *
			 * //???????????? childXmlData.setList("fromInfo");
			 *
			 * if(childXmlData != null) {
			 * data.setFromInfo(EmailClientUtil.setValue
			 * (childXmlData.get("name"))); }
			 *
			 * //???????????? XMLData tolist = childXmlData.getChild("toList");
			 * if(tolist != null) { tolist.setList("userInfo"); String toList =
			 * ""; for(int a = 0 ; a < tolist.size() ; a ++){ toList +=
			 * EmailClientUtil.setValue(tolist.get(a,"name"))+";"; }
			 * data.setToList(toList); }
			 */
			ArrayList<EmailReceiveListData> listData = new ArrayList<EmailReceiveListData>();
			xmlData.setList("otherinfo");

			searchTotalPageCnt = Integer.parseInt(xmlData.get("total"));

			String total = xmlData.get("total"); // ???????????? ???
			String unread = xmlData.get("unread"); // ????????? ??? ??????
			String total_size = xmlData.get("TOTAL_SIZE"); // ??? ????????????

			int int_total_size = 0;
			int int_unread = 0;
			if (total_size != null && !"".equals(total_size))
				int_total_size = Integer.parseInt(total_size);
			if (unread != null && !"".equals(unread))
				int_unread = Integer.parseInt(unread);

			xmlData.setList("record");

			String boxType = mainData.getBoxType(); // "I1", "I2"
			// if("I1".equals(mainData.getBoxType())||
			// "I2".equals(mainData.getBoxType())){
			// listData.addAll(getBoxTypeReceiveData(xmlData,
			// mainData.getBoxType()));
			// }else{
			for (int i = 0; i < xmlData.size(); i++) {
				EmailReceiveListData data = new EmailReceiveListData();

				data.setMdn(mainData.getMdn()); // ????????? ?????????
				data.setCompanyCd(mainData.getCompanyCd()); // EX
				data.setBoxType(mainData.getBoxType()); // I2
				data.setParentBoxType(mainData.getParentBoxType()); // I2
				data.setBoxId(mainData.getBoxId()); // 1 ????????????
				data.setBoxChangKey(mainData.getBoxChangeKey()); // ?
				data.setBoxNmae(mainData.getBoxName()); // ???????????????-??????
				data.setUpdateDate(mainData.getUpdatedate());
				if ("I1".equals(mainData.getBoxType())) {
					// ????????? ???????????????? ????????? ????????? unread ?????? /5 = ???????????????
					// if(int_unread>6){
					if (int_unread > 15) {
						// data.setTotalCnt((int_unread/5)+"");
						data.setTotalCnt((int_unread / 14) + "");
					} else {
						data.setTotalCnt("1");
					}

				} else if ("I2".equals(mainData.getBoxType())) {
					// ?????? ???????????????? ?????? ????????? total_size - unread /5 = ???????????????
					// data.setTotalCnt(((int_total_size - int_unread)/5)+"");
					data.setTotalCnt(((int_total_size - int_unread) / 14) + "");
				} else {
					data.setTotalCnt(StringUtil.isNull(total) ? "" : total);
				}
				data.setMailChangeKey(""); // ???????????? ??????

				XMLData childXmlData = xmlData.getChild(i);
				childXmlData.setList("userdata");
				data.setMailId(EmailClientUtil.setValue(childXmlData
						.get("msgId"))); // mailId <= msgId

				// ///////////////////////////////////////////////////////////////////////
				// mailType setting
				StringBuffer mailType = new StringBuffer();
				// ??????,????????????,????????????,???????????????,????????????(?????????,?????????),
				if ("1".equals(EmailClientUtil.setValue(childXmlData
						.get("isFast"))))
					mailType.append("isFast;");
				if ("1".equals(EmailClientUtil.setValue(childXmlData
						.get("reply"))))
					mailType.append("isReply;");
				if ("1".equals(EmailClientUtil.setValue(childXmlData
						.get("secret"))))
					mailType.append("isSecret;");
				// ///////////////////////////////////////////////////////////////////////

				childXmlData.setList("field");

				if ("1".equals(StringUtil.isNull(childXmlData.getContent(WORK))))
					mailType.append("isWork;");

				if ("P".equals(boxType)) {
					data.setMailSubject(StringUtil.isNull(childXmlData
							.getContent(P_TITLE)) ? "????????????" : childXmlData
							.getContent(P_TITLE));
					data.setHasAttachments(StringUtil.isNull(childXmlData
							.getContent(P_ATTACH)) ? "" : childXmlData
							.getContent(P_ATTACH)); // true, false // 0????????????
													// 1???????????? ??????????????? 0 ????????? 11 ????????????
													// ????????????.
					data.setReceivedDate(StringUtil.isNull(childXmlData
							.getContent(P_SAVEDATE)) ? "" : childXmlData
							.getContent(P_SAVEDATE));
					data.setSendDate(""); // ???????????? ????????????????????? ???????????????
					data.setUnreadCnt(EmailClientUtil.setValue(total_size));
					data.setFromInfo(StringUtil.isNull(childXmlData
							.getContent(P_SENDER)) ? "" : childXmlData
							.getContent(P_SENDER));
				} else {
					data.setMailSubject(StringUtil.isNull(childXmlData
							.getContent(TITLE)) ? "????????????" : childXmlData
							.getContent(TITLE));
					data.setHasAttachments(StringUtil.isNull(childXmlData
							.getContent(ATTACH)) ? "" : childXmlData
							.getContent(ATTACH)); // true, false // 0???????????? 1????????????
													// ??????????????? 0 ????????? 11 ???????????? ????????????.
					data.setReceivedDate(StringUtil.isNull(childXmlData
							.getContent(RECIEVEDATE)) ? "" : childXmlData
							.getContent(RECIEVEDATE));
					data.setSendDate(""); // ???????????? ????????????????????? ???????????????

					if ("I1".equals(boxType)) {
						data.setIsRead("0"); // true, false // 1??????, 0?????????
					} else if ("I2".equals(boxType)) {
						data.setIsRead("1"); // true, false // 1??????, 0?????????
					} else {
						data.setIsRead(StringUtil.isNull(childXmlData
								.getContent(READMAIL)) ? "" : childXmlData
								.getContent(READMAIL)); // true, false // 1??????,
														// 0?????????
					}

					data.setMailType(mailType.toString());
					data.setUnreadCnt(EmailClientUtil.setValue(total_size));
					data.setFromInfo(StringUtil.isNull(childXmlData
							.getContent(SENDER)) ? "" : childXmlData
							.getContent(SENDER));
				}
				if ("S".equals(boxType))
					data.setToList(setToList(childXmlData.getContent(SENDER)));

				listData.add(data);
			}

			for (EmailReceiveListData data : listData) {
				adapter_search.add(data);
			}

			setSearchUI();

		} catch (SKTException e) {
			e.alert("??????", this, new DialogButton(0) {
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			});
		}
	}

	/**
	 * ????????? ?????????
	 *
	 * @param adapter
	 */
	private void clearAdapter(EmailReceiveListAdapter adapter) {
		if (adapter.getCount() > 0) {
			adapter.clear();
		}
	}

	/**
	 * ????????? adapter ??????
	 *
	 * @param id
	 */
	private void setList(String id) {
		// receiveList.setSelection(receivePosition);

		ArrayList<EmailReceiveListData> list = null;
		EmailDatabaseHelper helper = new EmailDatabaseHelper(
				EmailReceiveActivity.this);
		try {
			mainData = helper.getMainTableData(id);
			list = helper.selectReceiveTable(id, 1, pageNumber * 20);
		} finally {
			helper.close();
		}
		if (list != null && list.size() > 0) {
			totalPageCnt = Integer.parseInt(list.get(list.size() - 1)
					.getTotalCnt());
			Log.d("xxxxxx", "xxxxxxx  " + totalPageCnt);
		} else {
			messageText = "??? ???????????? ????????????.";
		}
		/*
		 * // ???????????????, ?????????????????? ??????
		 * if("I1".equals(mainData.getBoxType())||"I2".equals
		 * (mainData.getBoxType())||"S".equals(mainData.getBoxType())) {
		 * searchlayout.setVisibility(View.VISIBLE); }else{
		 * searchlayout.setVisibility(View.GONE); }
		 */

		clearAdapter(adapter);
		lastupdata.setText(mainData.getUpdatedate());
		for (EmailReceiveListData data : list) {
			adapter.add(data);
		}
		// adapter.notifyDataSetChanged();
		// receiveList.setSelection(receivePosition);
		setUI();

	}

	/**
	 * ??????????????? UI ??????
	 */
	private void setUI() {

		receivelayout.setVisibility(View.VISIBLE);
		receivelayout_search.setVisibility(View.GONE);
		receivelayout_del.setVisibility(View.GONE);
		findViewById(R.id.updateBtn).setClickable(true);
		searchEdit.setText("");
		TextView title = (TextView) findViewById(R.id.settitle);
		TextView textview = (TextView) findViewById(R.id.textnolist);


		if (receiveList.getFooterViewsCount() > 0) {
			// setFooterview(R.string.MORE_TEXT_1);
			// TextView btnText =
			// (TextView)receiveList.findViewById(R.id.moreButton);
			// btnText.setText(getResources().getString(R.string.more_button));
		}

		if (mainData == null) {
			title.setText(getResources().getString(R.string.mail_boxName_I));
		} else {
			title.setText(mainData.getBoxName());

		}

		addMoreButtonInFooter(receiveList);

		adapter.notifyDataSetChanged();
		receiveList.setSelection(receivePosition);
		Log.d("xxxxxxxxxxxxx", "setUI receivePosition=>" + receivePosition);

		if (adapter == null || adapter.getCount() <= 0) {
			Log.i("EmailReceiveActivity", "adpater is null");
			receivelayout.setVisibility(View.GONE);
			textview.setVisibility(View.VISIBLE);
			if (mainData != null && mainData.getBoxName() != null) {
				textview.setText(mainData.getBoxName() + messageText);
			} else {
				textview.setText(getResources().getString(R.string.mail_boxName_I)
						+ messageText);
			}
		} else {
			Log.i("EmailReceiveActivity", "adpater is not null");
			textview.setVisibility(View.GONE);
		}
	}

	/**
	 * ????????????????????? UI ??????
	 */
	private void setSearchUI() {
		receivelayout.setVisibility(View.GONE);
		receivelayout_search.setVisibility(View.VISIBLE);
		receivelayout_del.setVisibility(View.GONE);
		findViewById(R.id.updateBtn).setClickable(true);
		TextView title = (TextView) findViewById(R.id.settitle);
		TextView textview = (TextView) findViewById(R.id.textnolist);

		if (receiveList_search.getFooterViewsCount() > 0) {
			// setFooterview(R.string.MORE_TEXT_1);
			// TextView btnText =
			// (TextView)receiveList_search.findViewById(R.id.moreButton);
			// btnText.setText(getResources().getString(R.string.more_button));
		}

		if (mainData == null) {
			title.setText(getResources().getString(R.string.mail_boxName_I));
		} else {
			title.setText(mainData.getBoxName());
		}

		addMoreButtonInFooter(receiveList_search);
		adapter_search.notifyDataSetChanged();

		if (adapter_search == null || adapter_search.getCount() <= 0) {
			receivelayout_search.setVisibility(View.GONE);
			textview.setVisibility(View.VISIBLE);
			textview.setText(getResources().getString(R.string.mail_search_notice));

		} else {
			textview.setVisibility(View.GONE);
		}
	}

	/**
	 * ????????????????????? UI ??????
	 */
	private void setDelUI() {

		receivelayout.setVisibility(View.GONE);
		receivelayout_search.setVisibility(View.GONE);
		receivelayout_del.setVisibility(View.VISIBLE);
		findViewById(R.id.updateBtn).setClickable(true);
		TextView title = (TextView) findViewById(R.id.settitle);

		if (mainData == null) {
			title.setText(getResources().getString(R.string.mail_boxName_I));
		} else {
			title.setText(mainData.getBoxName());
		}

		adapter_del.notifyDataSetChanged();

	}

	/**
	 * ????????? ?????? ??????
	 *
	 * @param list
	 */
	private void addMoreButtonInFooter(ListView list) {
		if (search == 0) {
			list.removeFooterView(moreBtn);
			Log.d("xxxxxx", "xxxxxxx  pageNumber" + pageNumber);
			Log.d("xxxxxx", "xxxxxxx  totalPageCnt" + totalPageCnt);
			if (pageNumber < totalPageCnt) {
				list.addFooterView(moreBtn);
				list.setAdapter(adapter);
				list.setSelection(receivePosition);
			}
		} else {
			list.removeFooterView(moreBtn);
			if (searchPageNumber < searchTotalPageCnt) {
				if (moreBtn != null) {
					list.addFooterView(moreBtn);
					list.setAdapter(adapter_search);
				}
			}
			list.setSelection(searchPosition);
		}
	}

	/**
	 * ????????? ????????? ????????? ?????????
	 *
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */

    String m_id = "";
    String m_pwd = "";
    String m_name = "";
    String m_mobile = "";
    String m_type = "";

	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                            long arg3) {
		final ProgressDialog progressDlg = new ProgressDialog(
				EmailReceiveActivity.this);
		progressDlg.setOwnerActivity(EmailReceiveActivity.this);
		progressDlg.setCancelable(false);
		progressDlg.setMessage(Resource.getString(EmailReceiveActivity.this,
				Resource.RES_RETRIEVING_ID));

		if (isdel == 0 && search == 0) {
			if (adapter.getItem(arg2).getMailType().indexOf("isSecret") > -1) {
				final EmailSecurityDialog dia = new EmailSecurityDialog(this);
				Button btn1 = dia.getOkBtn();
				btn1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {/*
						// TODO Auto-generated method stub

						String pwd = dia.getEditTextString();
						// Log.d("xxxxxxxxxxxxx", "xxxxxxxxxxxxx" + pwd);

						// if(!StringUtil.isNull(pwd)) {
						progressDlg.show();

						Parameters param = new Parameters("COMMON_MO_LEGACY");
						param.put("pwd", pwd);

						Controller controller = new Controller(
								EmailReceiveActivity.this);
						XMLData data;
						try {
							data = controller.request(param, true);
							if (!data.get("result").equals("1000")) {
								Toast.makeText(
										EmailReceiveActivity.this,
										getResources().getString(
												R.string.mail_secret_mail_fail),
										Toast.LENGTH_SHORT).show();
								return;
							}
						} catch (SKTException e) {
							Toast.makeText(
									EmailReceiveActivity.this,
									getResources().getString(
											R.string.mail_secret_mail_fail),
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
							return;
						} finally {
							progressDlg.dismiss();
						}

						// }

						String[] mailIds = new String[adapter.getCount()];
						String[] changeKeys = new String[adapter.getCount()];

						for (int i = 0; i < adapter.getCount(); i++) {
							mailIds[i] = adapter.getItem(i).getMailId();
							changeKeys[i] = adapter.getItem(i)
									.getMailChangeKey();
							Log.i("list changeKey", changeKeys[i]);
						}
						Log.d("", "EmailDetailActivity type check 1");

						Intent intent = new Intent(EmailReceiveActivity.this,
								EmailDetailActivity.class);
						Log.d("", "EmailDetailActivity type check 1");
						intent.putExtra("receive", adapter.getItem(arg2));
						intent.putExtra("mailIds", mailIds);
						intent.putExtra("changeKeys", changeKeys);
						intent.putExtra("curIndex", arg2);
						intent.putExtra("mailId", adapter.getItem(0)
								.getMailId());
						intent.putExtra("id", mainData.getId());
						intent.putExtra("boxName", mainData.getBoxName());

						receivePosition = arg2;
						selectedIndex = arg2;
						startActivityForResult(intent, 101);
						dia.dismiss();
					}
				});

				Button btn2 = dia.getCanBtn();
				btn2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dia.dismiss();
					}
				});
				dia.show();
			} else {
				// mailIds, changeKeys ????????????
				String[] mailIds = new String[adapter.getCount()];
				String[] changeKeys = new String[adapter.getCount()];
				for (int i = 0; i < adapter.getCount(); i++) {
					mailIds[i] = adapter.getItem(i).getMailId();
					changeKeys[i] = adapter.getItem(i).getMailChangeKey();
					Log.i("?????? or ?????? ?????????  changeKey", changeKeys[i]);
				}
				Intent intent = new Intent(EmailReceiveActivity.this,
						EmailDetailActivity.class);
				Log.d("", "EmailDetailActivity type check 2");

				intent.putExtra("receive", adapter.getItem(arg2));
				intent.putExtra("mailIds", mailIds);
				intent.putExtra("changeKeys", changeKeys);
				intent.putExtra("curIndex", arg2);
				intent.putExtra("id", mainData.getId());
				intent.putExtra("boxName", mainData.getBoxName());

				receivePosition = arg2;
				selectedIndex = arg2;

				startActivityForResult(intent, 101);
			}
		} else {
			if (isdel > search) {

				EmailReceiveListData item = adapter_del.getItem(arg2);
				ImageView button = (ImageView) arg1
						.findViewById(R.id.CHECK_BUTTON);
				if (item.isDel()) {
					button.setBackgroundResource(R.drawable.mail_check_off);
					item.setDel(false);
					checkItem(arg2, false);
				} else {
					button.setBackgroundResource(R.drawable.mail_check_on);
					item.setDel(true);
					checkItem(arg2, true);
				}

			} else {
				// if(adapter_search.getItem(arg2).getMailType().equals("PRTT"))
				// {
				if (adapter.getItem(arg2).getMailType().indexOf("isSecret") > -1) {
					final EmailSecurityDialog dia = new EmailSecurityDialog(
							this);
					Button btn1 = dia.getOkBtn();
					btn1.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							String pwd = dia.getEditTextString();
							Log.d("xxxxxxxxxxxxx", "xxxxxxxxxxxxx" + pwd);
							if (!StringUtil.isNull(pwd)) {
								progressDlg.show();

								Parameters param = new Parameters(
										"COMMON_MO_LEGACY");
								param.put("pwd", pwd);

								Controller controller = new Controller(
										EmailReceiveActivity.this);

								XMLData data;
								try {
									data = controller.request(param, true);
									if (!data.get("result").equals("1000")) {
										return;
									}
								} catch (SKTException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									return;
								} finally {
									progressDlg.dismiss();
								}
							}

							String[] mailIds = new String[adapter.getCount()];
							String[] changeKeys = new String[adapter.getCount()];

							for (int i = 0; i < adapter.getCount(); i++) {
								mailIds[i] = adapter.getItem(i).getMailId();
								changeKeys[i] = adapter.getItem(i)
										.getMailChangeKey();
							}

							Intent intent = new Intent(
									EmailReceiveActivity.this,
									EmailDetailActivity.class);
							Log.d("", "EmailDetailActivity type check 3");
							intent.putExtra("receive", adapter.getItem(arg2));
							intent.putExtra("mailIds", mailIds);
							intent.putExtra("changeKeys", changeKeys);
							intent.putExtra("curIndex", arg2);
							intent.putExtra("id", mainData.getId());
							intent.putExtra("boxName", mainData.getBoxName());

							receivePosition = arg2;
							selectedIndex = arg2;
							startActivityForResult(intent, 101);
							dia.dismiss();
						}
					});

					Button btn2 = dia.getCanBtn();
					btn2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							dia.dismiss();
						}
					});
					dia.show();
				} else {
					String[] mailIds = new String[adapter_search.getCount()];
					String[] changeKeys = new String[adapter_search.getCount()];

					for (int i = 0; i < adapter_search.getCount(); i++) {
						mailIds[i] = adapter_search.getItem(i).getMailId();
						changeKeys[i] = adapter_search.getItem(i)
								.getMailChangeKey();
					}

					Intent intent = new Intent(EmailReceiveActivity.this,
							EmailDetailActivity.class);
					Log.d("", "EmailDetailActivity type check 4");
					intent.putExtra("receive", adapter_search.getItem(arg2));
					intent.putExtra("mailIds", mailIds);
					intent.putExtra("changeKeys", changeKeys);
					intent.putExtra("curIndex", arg2);
					intent.putExtra("id", mainData.getId());
					intent.putExtra("boxName", mainData.getBoxName());

					receivePosition = arg2;
					selectedIndex = arg2;
					startActivityForResult(intent, 101);
				}
			}
		}
	}
*/


                        // TODO Auto-generated method stub

                        String pwd = dia.getEditTextString();
                        // Log.d("xxxxxxxxxxxxx", "xxxxxxxxxxxxx" + pwd);

                        // if(!StringUtil.isNull(pwd)) {
                        progressDlg.show();
                        Log.d("EmailReceiveActivity", TAG+" >> -----:: id : " + EmailClientUtil.id);
                        Log.d("EmailReceiveActivity", TAG+" >> -----:: pwd : " + pwd);
						/*Parameters param = new Parameters("COMMON_MO_LEGACY");
						param.put("pwd", pwd);
						Log.d("EmailReceiveActivity", TAG+" >> -----:: param : " + param);*/

                        try {

                            String swbeonho = EmailClientUtil.id;
                            String password = DEFCrypto.getInstance().getSha256Def(pwd);

                            Log.d(TAG, TAG+" >> -----:: [ swbeonho : "+swbeonho+" ], [ password : "+password+" ]");
                            Log.d(TAG, TAG+" >> -----:: [ mdn : "+EmailClientUtil.mdn+" ]");
                            Log.d(TAG, TAG+" >> -----:: [ MODEL : "+Build.MODEL+" ], [ BRAND : "+ Build.BRAND+" ]");


                            //????????? ?????? ??????
                            HttpConnection.HttpRequest request = new HttpConnection.HttpRequest();
                            request.url_header = HttpConnection.URL_HEADER;
                            request.url_tail = HttpConnection.URL_LOGININFO;

                            request.arg.add("userId");
                            request.arg.add(swbeonho);
                            request.arg.add("pwd");
                            request.arg.add(password);
                            request.arg.add("mdn");
                            request.arg.add(EmailClientUtil.mdn);
                            request.arg.add("deviceNm");
                            request.arg.add(Build.MODEL);
                            request.arg.add("deviceGubun");
                            request.arg.add(Build.BRAND);
                            request.arg.add("platformCd");
                            request.arg.add("A");
                            m_id = swbeonho;
                            m_pwd = password;

                            request.listener = new HttpConnection.IHttpResult() {

                                @Override
                                public void onHttpResult(String url, String json) {
                                    JSONParser jp = new JSONParser();
                                    try {
                                        JSONObject jsonobject = (JSONObject)jp.parse(json);
                                        Log.d(TAG, TAG+" >> -----:: url === "+url);
                                        Log.d(TAG, TAG+" >> -----:: jsonobject === "+jsonobject.toString());

                                        //????????? ??????
                                        //if(jsonobject.get("result").equals("1")){
                                        if(jsonobject.get("resultMsg").equals("OK")){
                                            if(jsonobject.containsKey("userNm")){
                                                m_name = (String)jsonobject.get("userNm");
                                                m_mobile = (String)jsonobject.get("mobile");
                                                m_type = (String)jsonobject.get("userType");
                                                Log.d(TAG, TAG+" >> -----:: [ m_name : "+m_name+" ] [ m_mobile : "+m_mobile+" ] [ m_type : "+m_type+" ]");
                                                Log.d(TAG, TAG+" >> -----:: ?????? ?????? ");

                                                // ???????????? ??????????????? ??????
                                                goSecretMailDetail(arg2, dia);
                                            }
                                            //???????????? ??????
                                        }else if(jsonobject.get("result").equals("0")){
                                            Log.d(TAG, TAG+" >> -----:: - ???????????? ");
                                            Toast.makeText(
                                                    EmailReceiveActivity.this,
                                                    getResources().getString(
                                                            R.string.mail_secret_mail_fail),
                                                    Toast.LENGTH_SHORT).show();

                                            return;
                                            //showToast("??????????????? ???????????? ????????????.");
                                        }else{
                                            Log.d(TAG, TAG+" >> -----:: - ???????????? ");
                                            Toast.makeText(
                                                    EmailReceiveActivity.this,
                                                    getResources().getString(
                                                            R.string.mail_secret_mail_fail),
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                            //showToast("????????? ???????????? ????????????.");
                                        }

                                    } catch (ParseException e) {
                                        Toast.makeText(
                                                EmailReceiveActivity.this,
                                                getResources().getString(
                                                        R.string.mail_secret_mail_fail),
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                        //showToast("???????????? ?????? ??????????????? ???????????? ????????????.");
                                    }finally {
                                        progressDlg.dismiss();
                                    }
                                }
                            };

                            HttpConnection conn = new HttpConnection(request);
                            conn.request();

						/*Controller controller = new Controller(
								EmailReceiveActivity.this);
						XMLData data;
						try {
							data = controller.request(param, true);
							if (!data.get("result").equals("1000")) {
								Toast.makeText(
										EmailReceiveActivity.this,
										getResources().getString(
												R.string.secret_mail_fail),
										Toast.LENGTH_SHORT).show();
								return;
							}
						} catch (SKTException e) {
							Toast.makeText(
									EmailReceiveActivity.this,
									getResources().getString(
											R.string.secret_mail_fail),
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
							return;
						} finally {
							progressDlg.dismiss();
						}*/

						/*String[] mailIds = new String[adapter.getCount()];
						String[] changeKeys = new String[adapter.getCount()];

						for (int i = 0; i < adapter.getCount(); i++) {
							mailIds[i] = adapter.getItem(i).getMailId();
							changeKeys[i] = adapter.getItem(i)
									.getMailChangeKey();
							Log.i("list changeKey", changeKeys[i]);
						}
						Log.d("", "EmailDetailActivity type check 1");

						Intent intent = new Intent(EmailReceiveActivity.this,
								EmailDetailActivity.class);
						Log.d("", "EmailDetailActivity type check 1");
						intent.putExtra("receive", adapter.getItem(arg2));
						intent.putExtra("mailIds", mailIds);
						intent.putExtra("changeKeys", changeKeys);
						intent.putExtra("curIndex", arg2);
						intent.putExtra("mailId", adapter.getItem(0)
								.getMailId());
						intent.putExtra("id", mainData.getId());
						intent.putExtra("boxName", mainData.getBoxName());

						receivePosition = arg2;
						selectedIndex = arg2;
						startActivityForResult(intent, 101);
						dia.dismiss();*/
                        } catch (InvalidFormatException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }

                });

                Button btn2 = dia.getCanBtn();
                btn2.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        dia.dismiss();
                    }
                });
                dia.show();
            } else {
                // mailIds, changeKeys ????????????
                String[] mailIds = new String[adapter.getCount()];
                String[] changeKeys = new String[adapter.getCount()];
                for (int i = 0; i < adapter.getCount(); i++) {
                    mailIds[i] = adapter.getItem(i).getMailId();
                    changeKeys[i] = adapter.getItem(i).getMailChangeKey();
                    Log.i("?????? or ?????? ?????????  changeKey", changeKeys[i]);
                }
                Intent intent = new Intent(EmailReceiveActivity.this,
                        EmailDetailActivity.class);
                Log.d("", "EmailDetailActivity type check 2");

                intent.putExtra("receive", adapter.getItem(arg2));
                intent.putExtra("mailIds", mailIds);
                intent.putExtra("changeKeys", changeKeys);
                intent.putExtra("curIndex", arg2);
                intent.putExtra("id", mainData.getId());
                intent.putExtra("boxName", mainData.getBoxName());

                receivePosition = arg2;
                selectedIndex = arg2;

                startActivityForResult(intent, 101);
            }
        } else {
            if (isdel > search) {

                EmailReceiveListData item = adapter_del.getItem(arg2);
                ImageView button = (ImageView) arg1
                        .findViewById(R.id.CHECK_BUTTON);
                if (item.isDel()) {
                    button.setBackgroundResource(R.drawable.mail_check_off);
                    item.setDel(false);
                    checkItem(arg2, false);
                } else {
                    button.setBackgroundResource(R.drawable.mail_check_on);
                    item.setDel(true);
                    checkItem(arg2, true);
                }

            } else {

            	System.out.println("------------------------ ????????? isdel : " + isdel );
				System.out.println("------------------------ ????????? search : " + search );


                // if(adapter_search.getItem(arg2).getMailType().equals("PRTT"))
                // {
				try {
					if (adapter.getItem(arg2).getMailType().indexOf("isSecret") > -1) {
						final EmailSecurityDialog dia = new EmailSecurityDialog(
								this);
						Button btn1 = dia.getOkBtn();
						btn1.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								String pwd = dia.getEditTextString();
								Log.d("xxxxxxxxxxxxx", "xxxxxxxxxxxxx" + pwd);
								Log.d("EmailReceiveActivity", "----- pwd : " + pwd);
								if (!StringUtil.isNull(pwd)) {
									progressDlg.show();

									Parameters param = new Parameters(
											"COMMON_MO_LEGACY");
									param.put("pwd", pwd);

									Controller controller = new Controller(
											EmailReceiveActivity.this);

									XMLData data;
									try {
										data = controller.request(param, true);
										if (!data.get("result").equals("1000")) {
											return;
										}
									} catch (SKTException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										return;
									} finally {
										progressDlg.dismiss();
									}
								}

								String[] mailIds = new String[adapter.getCount()];
								String[] changeKeys = new String[adapter.getCount()];

								for (int i = 0; i < adapter.getCount(); i++) {
									mailIds[i] = adapter.getItem(i).getMailId();
									changeKeys[i] = adapter.getItem(i)
											.getMailChangeKey();
								}

								Intent intent = new Intent(
										EmailReceiveActivity.this,
										EmailDetailActivity.class);
								Log.d("", "EmailDetailActivity type check 3");
								intent.putExtra("receive", adapter.getItem(arg2));
								intent.putExtra("mailIds", mailIds);
								intent.putExtra("changeKeys", changeKeys);
								intent.putExtra("curIndex", arg2);
								intent.putExtra("id", mainData.getId());
								intent.putExtra("boxName", mainData.getBoxName());

								receivePosition = arg2;
								selectedIndex = arg2;
								startActivityForResult(intent, 101);
								dia.dismiss();
							}
						});

						Button btn2 = dia.getCanBtn();
						btn2.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								dia.dismiss();
							}
						});
						dia.show();
					} else {
						String[] mailIds = new String[adapter_search.getCount()];
						String[] changeKeys = new String[adapter_search.getCount()];

						for (int i = 0; i < adapter_search.getCount(); i++) {
							mailIds[i] = adapter_search.getItem(i).getMailId();
							changeKeys[i] = adapter_search.getItem(i)
									.getMailChangeKey();

						}

						Intent intent = new Intent(EmailReceiveActivity.this,
								EmailDetailActivity.class);
						Log.d("", "EmailDetailActivity type check 4");
						intent.putExtra("receive", adapter_search.getItem(arg2));
						intent.putExtra("mailIds", mailIds);
						intent.putExtra("changeKeys", changeKeys);
						intent.putExtra("curIndex", arg2);
						intent.putExtra("id", mainData.getId());
						intent.putExtra("boxName", mainData.getBoxName());

						receivePosition = arg2;
						selectedIndex = arg2;
						startActivityForResult(intent, 101);
					}
				}catch (IndexOutOfBoundsException e){
					System.out.println("---------------------------------- index error ");
					e.printStackTrace();

				}
            }
        }
    }
	/**
	 * onClick ????????? ?????????<br>
	 * - ?????? ?????? - ?????? ?????? - ?????? ?????? ?????? - ????????? ?????? - Update ??????
	 *
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		receivePosition = receiveList.getFirstVisiblePosition();
		// if(v.getId()==R.id.ALLDel){
		// EmailClientUtil.hideSoftInputWindow(v);
		// m_szCurSearch = searchEdit.getText().toString();
		// char[] digit = m_szCurSearch.toCharArray();
		// int len = digit.length;
		//
		// if (len < 2) {
		// SKTDialog dlg = new SKTDialog(this, SKTDialog.DLG_TYPE_1);
		// dlg.getDialog("????????? ?????? ??????", "???????????? 2??? ?????? ??????????????? ?????????.", new
		// DialogButton(0) {
		// public void onClick(DialogInterface dialog, int which) {
		// }
		// }).show();
		// return;
		// }
		//
		// clearAdapter(adapter_search);
		// if(receiveList_search.getFooterViewsCount() > 0) {
		// receiveList_search.removeFooterView(moreBtn);
		// }
		//
		// search = isdel + 1;
		// searchPageNumber = 1;
		// new Action(EmailClientUtil.COMMON_MAIL_LIST).execute("");
		//
		// }else
		//
		if (v.getId() == R.id.ALLDel) {

			if (adapter_del.getCount() <= 0) {

				final SKTDialog dlg2 = new SKTDialog(this, SKTDialog.DLG_TYPE_1);
				dlg2.getDialog(
						getResources().getString(R.string.mail_alert_title_del),
						getResources().getString(R.string.mail_del_message),
						new DialogButton(0) {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();

			} else {

				SKTDialog dlg = new SKTDialog(EmailReceiveActivity.this,
						SKTDialog.DLG_TYPE_2);
				dlg.getDialog(
						getResources().getString(R.string.mail_alert_title_del),
						boxName + " ????????? ???????????? ?????????.", new DialogButton(0) {
							public void onClick(DialogInterface dialog,
									int which) {
								checkedMailId = new ArrayList<String>();
								checkedMailChangekey = new ArrayList<String>();
								for (int i = 0; i < adapter_del.getCount(); i++) {
									checkedMailId.add(adapter_del.getItem(i)
											.getMailId());
									checkedMailChangekey.add(adapter_del
											.getItem(i).getMailChangeKey());
								}

								new Action(EmailClientUtil.COMMON_MAILADV_DEL)
										.execute("");
							}
						}, new DialogButton(0) {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
			}
		} else if (v.getId() == R.id.Del) {
			if (checkedMailId.size() <= 0) {
				final SKTDialog dlg2 = new SKTDialog(this, SKTDialog.DLG_TYPE_1);
				dlg2.getDialog(
						getResources().getString(R.string.mail_alert_title_del),
						"????????? ????????? ????????????.", new DialogButton(0) {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
			} else {
				final SKTDialog dlg = new SKTDialog(EmailReceiveActivity.this,
						SKTDialog.DLG_TYPE_2);
				dlg.getDialog(
						getResources().getString(R.string.mail_alert_title_del),
						getResources().getString(R.string.mail_del_message),
						new DialogButton(0) {
							public void onClick(DialogInterface dialog,
									int which) {
								if (checkedMailId.size() <= 0) {
								} else {
									new Action(
											EmailClientUtil.COMMON_MAILADV_DEL)
											.execute("");
								}
							}
						}, new DialogButton(0) {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
			}
		} else if (v.getId() == R.id.moreBtnlayout) {
			// ?????? ???????????? ?????? ?????????????
			if (search == 0) {
				if (!EmailReceiveThread.getRunning(mainData.getId())) {
					pageNumber++;
					// setFooterview(R.string.MORE_TEXT_2);
					EmailReceiveThread thread = new EmailReceiveThread(this,
							mHandler, mainData, pageNumber,
							EmailReceiveThread.TYPE_2);
					thread.start();
				}
			} else {
				// setFooterview(R.string.MORE_TEXT_2);
				searchPageNumber++;
				new Action(EmailClientUtil.COMMON_MAILADV_LIST).execute("");
			}
		} else if (v.getId() == R.id.updateBtn) {
			m_szCurSearch = searchEdit.getText().toString();
			if (search != 0) {
				clearAdapter(adapter_search);
				if (receiveList_search.getFooterViewsCount() > 0) {
					receiveList_search.removeFooterView(moreBtn);
				}

				search = isdel + 1;
				searchPageNumber = 1;
				new Action(EmailClientUtil.COMMON_MAILADV_LIST).execute("");
				return;
			}

			if (!EmailReceiveThread.getRunning(mainData.getId())) {
				search = 0;
				isdel = 0;
				pageNumber = 1;
				EmailReceiveThread thread = new EmailReceiveThread(this,
						mHandler, mainData, pageNumber,
						EmailReceiveThread.TYPE_2);
				thread.start();
			}
		} else if (v.getId() == R.id.SearchInputText) {
			searchEdit.setText("");
		} else if (v.getId() == R.id.SearchTypeBtn) {
			if (SearchTypeBtn.getText().toString().equals("??????")) {
				SearchTypeBtn.setText("?????????");
			} else {
				SearchTypeBtn.setText("??????");
			}
		} else if (v.getId() == R.id.btn_sesarch_selector) {
			EmailClientUtil.hideSoftInputWindow(v);
			m_szCurSearch = searchEdit.getText().toString();
			char[] digit = m_szCurSearch.toCharArray();
			int len = digit.length;

			if (len < 2) {
				SKTDialog dlg = new SKTDialog(this, SKTDialog.DLG_TYPE_1);
				dlg.getDialog("????????? ?????? ??????", "???????????? 2??? ?????? ??????????????? ?????????.",
						new DialogButton(0) {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
				return;
			}

			clearAdapter(adapter_search);
			if (receiveList_search.getFooterViewsCount() > 0) {
				receiveList_search.removeFooterView(moreBtn);
			}

			search = isdel + 1;
			searchPageNumber = 1;
			new Action(EmailClientUtil.COMMON_MAILADV_LIST).execute("");

			// kbr 2022.04.13
		} else if (v.getId() == R.id.common_send_email_btn) {
			intent = new Intent(this, EmailWriteActivity.class);
			intent.putExtra("aaa", "aaa");
			intent.putExtra("refer", "main");
			intent.putExtra("empName", EmailClientUtil.empNm);
			intent.putExtra("empId", EmailClientUtil.id);
			startActivity(intent);
		}

	}

	/**
	 * ?????? ?????? ?????????<br>
	 * - ?????? ??????<br>
	 * - ?????? ?????? ??????<br>
	 * - ?????? ??????<br>
	 * - ?????? /????????? ??????
	 *
	 * @param primitive
	 *            ?????????
	 * @param args
	 *            ????????????
	 */
	@Override
	protected XMLData onAction(String primitive, String... args)
			throws SKTException {
		try {
			if (primitive.equals(EmailClientUtil.COMMON_MAIL_DEL)) {
				return getXmlData(primitive);
			} else {
				return getXmlData(primitive);
			}
		} catch (SKTException e) {
			if ("5402".equals(e.getErrCode())) {
				String[] ids = new String[checkedMailId.size()];
				String[] changekeys = new String[checkedMailChangekey.size()];
				ids = checkedMailId.toArray(ids);
				changekeys = checkedMailChangekey.toArray(changekeys);

				EmailDatabaseHelper helper = new EmailDatabaseHelper(
						EmailReceiveActivity.this);
				try {
					int count = helper.deleteReceiveTable(getSeq(), ids, changekeys);
					if (count > 0) {
						helper.updateTable(getSeq(), new String[] { "TOTAL_COUNT" }, new String[] { Integer.toString(totalItemCnt - count) });
					}
				} finally {
					helper.close();
				}
			}

			throw e;
		}
	}

	/**
	 * ?????? ????????? UI ??????<br>
	 * - ?????? ??????<br>
	 * - ?????? ?????? ??????<br>
	 * - ?????? ??????<br>
	 * - ?????? /????????? ??????
	 *
	 * @param primitive
	 *            ?????????
	 */
	@Override
	protected void onActionPost(String primitive, XMLData result, SKTException e) {
		if (e != null) {
			if (e.getErrCode().equals("5402")) {

				String[] ids = new String[checkedMailId.size()];
				String[] changekeys = new String[checkedMailChangekey.size()];
				ids = checkedMailId.toArray(ids);
				changekeys = checkedMailChangekey.toArray(changekeys);

				EmailDatabaseHelper helper = new EmailDatabaseHelper(
						EmailReceiveActivity.this);
				try {
					int count = helper.deleteReceiveTable(getSeq(), ids,
							changekeys);

					if (count > 0) {
						helper.updateTable(
								getSeq(),
								new String[] { "TOTAL_COUNT" },
								new String[] { Integer.toString(totalItemCnt
										- count) });
					}
				} finally {
					helper.close();
				}
				if (isdel > search && search > 0) {
					checkedMailId.clear();
					checkedMailChangekey.clear();
					clearAdapter(adapter_del);
					clearAdapter(adapter_search);
					if (receiveList_search.getFooterViewsCount() > 0) {
						receiveList_search.removeFooterView(moreBtn);
					}
					isdel = 0;
					searchPageNumber = 1;
					new Action(EmailClientUtil.COMMON_MAILADV_LIST).execute("");

				} else if (search > 0 && isdel == 0) {
					checkedMailId.clear();
					checkedMailChangekey.clear();
					clearAdapter(adapter_search);
					if (receiveList_search.getFooterViewsCount() > 0) {
						receiveList_search.removeFooterView(moreBtn);
					}
					searchPageNumber = 1;
					new Action(EmailClientUtil.COMMON_MAILADV_LIST).execute("");
				} else {
					search = 0;
					isdel = 0;
					clearAdapter(adapter_del);
					clearAdapter(adapter_search);
					setList(mainData.getId());
				}
			} else {
				e.alert("??????", this, new DialogButton(0) {

					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
			}

		} else {

			if (EmailClientUtil.COMMON_MAILADV_LIST.equals(primitive)) {

				setListUI(result);

			} else if (EmailClientUtil.COMMON_MAILADV_DEL.equals(primitive)) {

				String[] ids = new String[checkedMailId.size()];
				String[] changekeys = new String[checkedMailChangekey.size()];
				ids = checkedMailId.toArray(ids);
				changekeys = checkedMailChangekey.toArray(changekeys);

				EmailDatabaseHelper helper = new EmailDatabaseHelper(
						EmailReceiveActivity.this);
				try {
					int count = helper.deleteReceiveTable(getSeq(), ids,
							changekeys);

					if (count > 0) {
						helper.updateTable(
								getSeq(),
								new String[] { "TOTAL_COUNT" },
								new String[] { Integer.toString(totalItemCnt
										- count) });
					}
				} finally {
					helper.close();
				}

				if (isdel > search && search > 0) {
					checkedMailId.clear();
					checkedMailChangekey.clear();
					clearAdapter(adapter_del);
					clearAdapter(adapter_search);
					if (receiveList_search.getFooterViewsCount() > 0) {
						receiveList_search.removeFooterView(moreBtn);
					}
					isdel = 0;
					searchPageNumber = 1;
					new Action(EmailClientUtil.COMMON_MAILADV_LIST).execute("");

				} else if (search > 0 && isdel == 0) {
					checkedMailId.clear();
					checkedMailChangekey.clear();
					clearAdapter(adapter_search);
					if (receiveList_search.getFooterViewsCount() > 0) {
						receiveList_search.removeFooterView(moreBtn);
					}
					searchPageNumber = 1;
					new Action(EmailClientUtil.COMMON_MAILADV_LIST).execute("");
				} else {
					search = 0;
					isdel = 0;
					clearAdapter(adapter_del);
					clearAdapter(adapter_search);
					setList(mainData.getId());
				}

			} else if (EmailClientUtil.COMMON_MAIL_ISREAD.equals(primitive)) {
				registerForContextMenu((ListView) findViewById(R.id.receiveList));
				EmailDatabaseHelper helper = new EmailDatabaseHelper(
						EmailReceiveActivity.this);
				try {
					String isread = adapter.getItem(selectedIndex).getIsRead()
							.equals("true") ? "false" : "true";
					helper.updateReceiveTable(getSeq(),
							adapter.getItem(selectedIndex).getMailId(), adapter
									.getItem(selectedIndex).getMailChangeKey(),

							new String[] { "IS_READ" }, new String[] { isread });
				} finally {
					helper.close();
				}
				setList(mainData.getId());
			}
		}
	}

	/**
	 * ?????? ????????? UI ??????
	 *
	 * @param primitive
	 *            ?????????
	 */
	@Override
	protected int onActionPre(String primitive) {

		int ret = 0;

		if (EmailClientUtil.COMMON_MAILADV_LIST.equals(primitive))
			ret = Action.SERVICE_RETRIEVING;

		return ret;
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
		if (requestCode == 101) {

			if (search > 0) {
				// Log.e("ksh6327", "search > 0");
				clearAdapter(adapter_search);
				if (receiveList_search.getFooterViewsCount() > 0) {
					receiveList_search.removeFooterView(moreBtn);
				}
				search = isdel + 1;
				new Action(EmailClientUtil.COMMON_MAILADV_LIST).execute("");
			} else {
				// Log.e("ksh6327", "boxType=>"+boxType);
				if (boxType.equals("I1")) {
					EmailDatabaseHelper helper = new EmailDatabaseHelper(
							EmailReceiveActivity.this);
					String id = adapter.getItem(selectedIndex).getMailId();
					String changekey = adapter.getItem(selectedIndex)
							.getMailChangeKey();
					try {
						int count = helper
								.deleteReceiveTable(getSeq(),
										new String[] { id },
										new String[] { changekey });
						int unreadcnt = Integer.parseInt(mainData
								.getUnreadCnt());
						if (unreadcnt > 0) {
							if (count > 0) {
								helper.updateTable(getSeq(),
										new String[] { "BOX_UNREAD_COUNT" },
										new String[] { Integer
												.toString(unreadcnt - 1) });
							}
						}
					} finally {
						helper.close();
					}
				}

				setList(mainData.getId());

				// ?????? ??????????????? ?????? ??? ?????? ??????
				if (!EmailReceiveThread.getRunning(mainData.getId())) {

					if (data != null) {
						String dback = data.getExtras().getString("DetailBack");
						if (!"Back".equals(dback)) {
							search = 0;
							isdel = 0;
							pageNumber = 1;
							EmailReceiveThread thread = new EmailReceiveThread(this, mHandler, mainData, pageNumber, EmailReceiveThread.TYPE_2);
							thread.start();
						} else {
							// Log.e("ksh6327", "?????? ??????????????? ??? ?????? ?????? ?????? ??????");
						}
					} else {
						search = 0;
						isdel = 0;
						pageNumber = 1;
						EmailReceiveThread thread = new EmailReceiveThread(this, mHandler, mainData, pageNumber, EmailReceiveThread.TYPE_2);
						thread.start();
					}
				}
			}
		}
	}

	/**
	 * ?????? ?????? ??????
	 *
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		if (mMenu == null) {
			inflater.inflate(R.menu.mail_menu, menu);
			this.mMenu = menu;
		}

		if (EmailReceiveThread.getRunning(mainData == null ? null : mainData
				.getId())) {
			menu.findItem(R.id.MENUREFRESH).setEnabled(false);
			// menu.findItem(R.id.MENUSET).setEnabled(false);
		}

		if (boxType != null && boxType.equals("D")) {

			menu.findItem(R.id.MENUWRITE).setVisible(false);

		}
		return true;
	}

	/**
	 * ?????? ?????? ?????? ????????? ?????????<br>
	 * - ???????????? - ???????????? - ?????? - ?????????
	 *
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.MENUSET) {
			checkedMailId = new ArrayList<String>();
			checkedMailChangekey = new ArrayList<String>();
			clearAdapter(adapter_del);
			if (search == 0) {
				for (int a = 0; a < adapter.getCount(); a++) {
					adapter_del.add(adapter.getItem(a));
				}
			} else {
				for (int a = 0; a < adapter_search.getCount(); a++) {
					adapter_del.add(adapter_search.getItem(a));
				}
			}

			receiveList_del.setAdapter(adapter_del);
			receiveList_del.setSelection(receiveList.getFirstVisiblePosition());
			isdel = search + 1;
			adapter_del.setDel(1);
			adapter_del.notifyDataSetChanged();
			setDelUI();

		} else if (item.getItemId() == R.id.MENUWRITE) {
			receivePosition = receiveList.getFirstVisiblePosition();
			intent = new Intent(this, EmailWriteActivity.class);
			startActivity(intent);

		} else if (item.getItemId() == R.id.MENUREFRESH) {

			if (search != 0) {
				clearAdapter(adapter_search);
				if (receiveList_search.getFooterViewsCount() > 0) {
					receiveList_search.removeFooterView(moreBtn);
				}

				search = isdel + 1;
				searchPageNumber = 1;
				new Action(EmailClientUtil.COMMON_MAIL_LIST).execute("");
				return super.onOptionsItemSelected(item);
			}

			if (!EmailReceiveThread.getRunning(mainData.getId())) {
				search = 0;
				isdel = 0;
				pageNumber = 1;
				// checkEmailAddr();
				// TODO TYPE_2 ??????
				EmailReceiveThread thread = new EmailReceiveThread(this, mHandler, mainData, pageNumber, EmailReceiveThread.TYPE_2);
				thread.start();
			}

			// } else if (item.getItemId() == R.id.MENUMAILBOX) {
			//
			// Intent intent = new Intent(this,EmailMainActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);

			// } else if(item.getItemId()== R.id.MENU_SIGN_SETING) {
			//
			// EmailDialog di = new EmailDialog(this);
			// di.show();
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * ?????? ?????? ??????
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		super.onPrepareOptionsMenu(menu);
		if (isdel > search && isdel > 0) {

			menu.findItem(R.id.MENUSET).setVisible(false);
			menu.findItem(R.id.MENUWRITE).setVisible(false);
			menu.findItem(R.id.MENUREFRESH).setVisible(false);
			// menu.findItem(R.id.MENUMAILBOX).setVisible(false);
			// menu.findItem(R.id.MENU_SIGN_SETING).setVisible(false);

		} else if (boxType != null && boxType.equals("D")) {

			menu.findItem(R.id.MENUWRITE).setVisible(false);

		} else {

			menu.findItem(R.id.MENUWRITE).setVisible(true);
			menu.findItem(R.id.MENUSET).setVisible(true);
			menu.findItem(R.id.MENUREFRESH).setVisible(true);
			// menu.findItem(R.id.MENUMAILBOX).setVisible(true);
			// menu.findItem(R.id.MENU_SIGN_SETING).setVisible(true);

		}

		return true;
	}

	/**
	 * onBackPressed ?????????
	 */
	@Override
	public void onBackPressed() {
		if (isdel == 0 && search == 0) {
			super.onBackPressed();
		} else {
			if (isdel > search && search > 0) {
				checkedMailId.clear();
				checkedMailChangekey.clear();
				clearAdapter(adapter_del);
				clearAdapter(adapter_search);
				if (receiveList_search.getFooterViewsCount() > 0) {
					receiveList_search.removeFooterView(moreBtn);
				}
				isdel = 0;
				searchPageNumber = 1;
				new Action(EmailClientUtil.COMMON_MAIL_LIST).execute("");
			} else {
				search = 0;
				isdel = 0;
				clearAdapter(adapter_del);
				clearAdapter(adapter_search);
				setList(mainData.getId());
			}

			// if(isdel > search && search > 0) {
			//
			// checkedMailId.clear();
			// checkedMailChangekey.clear();
			// clearAdapter(adapter_del);
			//
			// clearAdapter(adapter_search);
			// if(receiveList_search.getFooterViewsCount() > 0) {
			// receiveList_search.removeFooterView(moreBtn);
			// }
			// isdel = 0;
			// new Action("COMMON_MAIL_LIST").execute("");
			//
			// } else if(search > isdel && isdel > 0) {
			//
			// checkedMailId = new ArrayList<String>();
			// checkedMailChangekey = new ArrayList<String>();
			// receiveList_del.setAdapter(adapter_del);
			// findViewById(R.id.ALLDel).setOnClickListener(this);
			// findViewById(R.id.Del).setOnClickListener(this);
			// search = 0;
			// adapter_del.setDel(1);
			// adapter_del.notifyDataSetChanged();
			// setDelUI();
			//
			// } else if((search > 0 && isdel == 0) || (isdel > 0 && search ==
			// 0)) {
			// search = 0 ;
			// isdel = 0 ;
			// setList(mainData.getId());
			// }
		}

	}

	/**
	 * ??? ???????????? ??????
	 *
	 * @param menu
	 * @param v
	 * @param menuInfo
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {

		switch (v.getId()) {

		case R.id.receiveList:
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.mail_listcontextmenu, menu);
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

			if (adapter.getItem(info.position).getIsRead().equals("true")) {
				menu.getItem(2).setVisible(false);
				menu.getItem(1).setVisible(true);
			} else {
				menu.getItem(2).setVisible(true);
				menu.getItem(1).setVisible(false);
			}
			if (!EmailReceiveThread.getRunning(mainData == null ? null
					: mainData.getId())) {
				for (int i = 0; i < menu.size(); i++) {
					menu.getItem(i).setEnabled(true);

				}
			} else {
				for (int i = 0; i < menu.size(); i++) {
					menu.getItem(i).setEnabled(false);
				}
			}

			if (mainData.getBoxType().equals("S")) {
				menu.getItem(1).setVisible(false);
				menu.getItem(2).setVisible(false);
			}
		}
	}

	/**
	 * ?????? ????????? - ?????? / ????????? - ??????
	 *
	 * @param item
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		selectedIndex = info.position;

		if (item.getItemId() == R.id.CMENUDELETE) {

			SKTDialog dlg = new SKTDialog(EmailReceiveActivity.this,
					SKTDialog.DLG_TYPE_2);
			dlg.getDialog(getResources().getString(R.string.mail_alert_title_del),
					getResources().getString(R.string.mail_del_message),
					new DialogButton(0) {
						public void onClick(DialogInterface dialog, int which) {
							checkedMailId = new ArrayList<String>();
							checkedMailChangekey = new ArrayList<String>();
							checkedMailId.add(adapter.getItem(selectedIndex)
									.getMailId());
							checkedMailChangekey.add(adapter.getItem(
									selectedIndex).getMailChangeKey());

							new Action(EmailClientUtil.COMMON_MAIL_DEL)
									.execute("");
						}
					}, new DialogButton(0) {
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();
		} else if (item.getItemId() == R.id.CMENUREPLY) { // ??????

			Intent intent = new Intent(this, EmailWriteActivity.class);
			intent.putExtra("list", "list");
			intent.putExtra("type", "reply");
			intent.putExtra("receive", adapter.getItem(selectedIndex));
			intent.putExtra("id", mainData.getId());
			receivePosition = selectedIndex;
			startActivityForResult(intent, 101);

		} else if (item.getItemId() == R.id.CMENUREPLYALL) { // ????????????

			Intent intent = new Intent(this, EmailWriteActivity.class);
			intent.putExtra("list", "list");
			intent.putExtra("type", "replyall");
			intent.putExtra("receive", adapter.getItem(selectedIndex));
			intent.putExtra("id", mainData.getId());
			receivePosition = selectedIndex;
			startActivityForResult(intent, 101);

		} else if (item.getItemId() == R.id.CMENUFORWARD) { // ??????

			Intent intent = new Intent(this, EmailWriteActivity.class);
			intent.putExtra("list", "list");
			intent.putExtra("type", "forward");
			intent.putExtra("receive", adapter.getItem(selectedIndex));
			intent.putExtra("id", mainData.getId());
			receivePosition = selectedIndex;
			startActivityForResult(intent, 101);

		} else if (item.getItemId() == R.id.CMENUUNREAD) { // ???????????????
			new Action(EmailClientUtil.COMMON_MAIL_ISREAD).execute("");

		} else if (item.getItemId() == R.id.CMENUREAD) {
			new Action(EmailClientUtil.COMMON_MAIL_ISREAD).execute("");
		}
		return true;
	}

	/**
	 * ?????? ????????? ??????
	 *
	 * @param a_position
	 * @param a_checked
	 */
	public void checkItem(int a_position, boolean a_checked) {

		if (a_checked) {
			checkedMailId.add(adapter_del.getItem(a_position).getMailId());
			checkedMailChangekey.add(adapter_del.getItem(a_position)
					.getMailChangeKey());
		} else {
			checkedMailId.remove(adapter_del.getItem(a_position).getMailId());
			checkedMailChangekey.remove(adapter_del.getItem(a_position)
					.getMailChangeKey());
		}
	}

	/**
	 * ????????? ??????????????? ????????????.
	 */
	@Override
	protected int assignLayout() {
		return R.layout.mail_list;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.skt.pe.common.activity.SKTActivity#onCommonError()
	 */
	@Override
	public void onCommonError(SKTException ex) {
		super.onCommonError(ex);

		if (ex != null) {
			if ("5404".equals(ex.getErrCode())) {
				ex.alert(this, new DialogButton(0) {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
			} else if (Constants.Status.E_NOT_CONNECT_SSLVPN_ID.equals(ex
					.getErrCode())) {
				SKTDialog dlg = new SKTDialog(this);
				dlg.getDialog(
						"??????",
						Resource.getString(this,
								Constants.Status.E_NOT_CONNECT_SSLVPN_ID),
						new DialogButton(0) {
							public void onClick(DialogInterface arg0, int arg1) {
								SKTUtil.closeApp(EmailReceiveActivity.this);
							}
						}).show();
			} else if (Constants.Status.E_NOT_INSTALL_SSLVPN_ID.equals(ex
					.getErrCode())) {
				SKTDialog dlg = new SKTDialog(this);
				dlg.getDialog(
						"??????",
						Resource.getString(this,
								Constants.Status.E_NOT_INSTALL_SSLVPN_ID),
						new DialogButton(0) {
							public void onClick(DialogInterface arg0, int arg1) {
								SKTUtil.closeApp(EmailReceiveActivity.this);
							}
						}).show();
			}
		}
	}

	@Override
	public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
		if (arg0.getId() == R.id.SearchInputText) {
			if (arg1 == KeyEvent.KEYCODE_ENTER
					&& arg2.getAction() == KeyEvent.ACTION_DOWN) {
				EmailClientUtil.hideSoftInputWindow(arg0);
				m_szCurSearch = searchEdit.getText().toString();
				char[] digit = m_szCurSearch.toCharArray();
				int len = digit.length;

				if (len < 2) {
					SKTDialog dlg = new SKTDialog(this, SKTDialog.DLG_TYPE_1);
					dlg.getDialog("????????? ?????? ??????", "???????????? 2??? ?????? ??????????????? ?????????.",
							new DialogButton(0) {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
					return false;
				}

				clearAdapter(adapter_search);
				if (receiveList_search.getFooterViewsCount() > 0) {
					receiveList_search.removeFooterView(moreBtn);
				}

				search = isdel + 1;
				searchPageNumber = 1;
				new Action(EmailClientUtil.COMMON_MAILADV_LIST).execute("");
			}

		}
		return false;
	}

	// private void setFooterview(int id) {
	// TextView view = (TextView)moreBtn.findViewById(R.id.moreButton);
	// view.setText(getResources().getString(id));
	// if(R.string.MORE_TEXT_1 == id) {
	// view.setCompoundDrawablesWithIntrinsicBounds
	// (R.drawable.morebtn_icon_selector,0,0,0);
	// } else {
	// view.setCompoundDrawablesWithIntrinsicBounds
	// (R.drawable.icon_loading_off,0,0,0);
	// }
	// }

	private String setParamMaild(List<String> checkedMaild) {
		String returnValue = "";
		for (int i = 0; i < checkedMaild.size(); i++) {
			if (i == 0) {
				returnValue = checkedMaild.get(i);
			} else {
				returnValue = returnValue + ";" + checkedMaild.get(i);
			}
		}
		return returnValue;
	}

	// ????????? ??????;?????? ???????????? ??????
	private String setToList(String param) {
		String returnValue = "";
		if (param.indexOf(",") > -1) {
			String[] splitString = param.split(",");
			for (int i = 0; i < splitString.length; i++) {
				if (i == 0) {
					if (splitString[i].indexOf("/") > -1) {
						returnValue = splitString[i].substring(0,
								splitString[i].indexOf("/"));
					} else {
						returnValue = splitString[i];
					}
				} else {
					if (splitString[i].indexOf("/") > -1) {
						returnValue = returnValue
								+ ";"
								+ splitString[i].substring(0,
										splitString[i].indexOf("/"));
					} else {
						returnValue = returnValue + ";" + splitString[i];
					}
				}
			}
		} else {
			if (param.indexOf("/") > -1) {
				returnValue = param.substring(0, param.indexOf("/"));
			} else {
				returnValue = replaceValue(param);
			}
		}
		if (StringUtil.isNull(returnValue))
			returnValue = "????????????";
		return returnValue;
	}

	private String replaceValue(String param) {
		String returnValue = "";
		int leftTag = param.indexOf("<");
		int rightTag = param.indexOf(">");
		if (leftTag > -1 && rightTag > -1 && leftTag < rightTag) {
			returnValue = param.substring(0, leftTag);
		} else {
			returnValue = param;
		}
		return returnValue;
	}

    public void goSecretMailDetail(int arg2, EmailSecurityDialog dia){
        String[] mailIds = new String[adapter.getCount()];
        String[] changeKeys = new String[adapter.getCount()];

        for (int i = 0; i < adapter.getCount(); i++) {
            mailIds[i] = adapter.getItem(i).getMailId();
            changeKeys[i] = adapter.getItem(i)
                    .getMailChangeKey();
            Log.i("list changeKey", changeKeys[i]);
        }
        Log.d("", "EmailDetailActivity type check 1");

        Intent intent = new Intent(EmailReceiveActivity.this,
                EmailDetailActivity.class);
        Log.d("", "EmailDetailActivity type check 1");
        intent.putExtra("receive", adapter.getItem(arg2));
        intent.putExtra("mailIds", mailIds);
        intent.putExtra("changeKeys", changeKeys);
        intent.putExtra("curIndex", arg2);
        intent.putExtra("mailId", adapter.getItem(0)
                .getMailId());
        intent.putExtra("id", mainData.getId());
        intent.putExtra("boxName", mainData.getBoxName());

        receivePosition = arg2;
        selectedIndex = arg2;
        startActivityForResult(intent, 101);
        dia.dismiss();
    }


}
