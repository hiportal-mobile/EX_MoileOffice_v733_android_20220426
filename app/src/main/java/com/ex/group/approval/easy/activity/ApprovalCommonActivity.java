package com.ex.group.approval.easy.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ex.group.board.custom.CommonProgress;
import com.ex.group.folder.R;
import com.ex.group.approval.easy.adapter.ApprovalLineViewAdapter;
import com.ex.group.approval.easy.adapter.AttachFileViewAdapter;
import com.ex.group.approval.easy.constant.ApprovalConstant;
import com.ex.group.approval.easy.dialog.WheelDatePickerDialogHelper;
import com.ex.group.approval.easy.dialog.WheelGuntaeDialog;
import com.ex.group.approval.easy.dialog.WheelTimePickerDialogHelper;
import com.ex.group.approval.easy.dialog.ifaces.PEDialogInterface;
import com.ex.group.approval.easy.domain.AttachFile;
import com.ex.group.approval.easy.domain.PrivateLine;
import com.ex.group.approval.easy.domain.SancLine;
import com.ex.group.approval.easy.domain.VocCodeTree;
import com.ex.group.approval.easy.primitive.DraftFormPrimitive;
import com.ex.group.approval.easy.primitive.VacCodePrimitive;
import com.ex.group.approval.easy.util.DateUtils;
import com.ex.group.mail.activity.EmailWriteActivity;
import com.ex.group.mail.util.HttpConnection;
import com.skt.pe.common.activity.PECommonActivity;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.primitive.Primitive;
import com.skt.pe.util.DateUtil;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

public abstract class ApprovalCommonActivity extends PECommonActivity {
	private final String TAG = "ApprovalCommonActivity";
	public int selectApprovalType = OptionMenuSelectValue.OUTSIDE_FORM;
	private int selectMemberSearchType = 0;
	private int selectPrivateLine = 0;
	private boolean[] selectExceptDate = null;

	public static final String COMMON_APPROVALSTAGING_RESTFULCLIENT = "COMMON_APPROVALSTAGING_RESTFULCLIENTSSL";//??????
//	public static final String COMMON_APPROVALSTAGING_RESTFULCLIENT = "COMMON_APPROVALSTAGING_RESTFULCLIENT2";//????????? test
	//??????
	public static final String COMMON_SEARCHOUTEMPINFO = "COMMON_SEARCHOUTEMPINFO";
	public static final String COMMON_SEARCHOUTCHECK = "COMMON_SEARCHOUTCHECK";
	public static final String COMMON_SEARCHOUTAPPL = "COMMON_SEARCHOUTAPPL";
//??????
	public static final String COMMON_SEARCHHOLGOINGEMPINFO = "COMMON_SEARCHHOLGOINGEMPINFO";
	public static final String COMMON_SEARCHHOLGOINGCHECK = "COMMON_SEARCHHOLGOINGCHECK";
	public static final String COMMON_SEARCHATTENDLIST = "COMMON_SEARCHATTENDLIST";
    public static final String COMMON_SEARCHVACAPPL = "COMMON_SEARCHVACAPPL";
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	protected void setTitle(String title) {
		TextView tv = (TextView) findViewById(R.id.common_ex_title_textview_title);
		if (tv != null)
			tv.setText(title);
	}
	
	protected void setSubTitle(String title) {
		TextView tv = (TextView) findViewById(R.id.common_ex_middle_textview_title);
		if (tv != null)
			tv.setText(title);
		TextView tvDate = (TextView) findViewById(R.id.common_ex_middle_textview_date);
		if (tvDate != null) {
			String today = DateUtils.getDate(System.currentTimeMillis());
			tvDate.setText(today);
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.easy_common, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.common_menu_applyAprproval:
			selectApprovalType(new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = null;
					Primitive prim = null;
					switch (getSelectApprovalType()) {
						case OptionMenuSelectValue.OUTSIDE_FORM:
							intent = new Intent(ApprovalCommonActivity.this, ApplyOutsideApprovalActivity.class);
							DraftFormPrimitive dformPrim = new DraftFormPrimitive();
							dformPrim.setTag(ApprovalConstant.Tags.INTENT, intent);
							prim = dformPrim;
							break;
							
						case OptionMenuSelectValue.VACATION_FORM:
//							Toast.makeText(ApprovalCommonActivity.this, "?????? ????????? ?????? ????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
							intent = new Intent(ApprovalCommonActivity.this, ApplyVacationApprovalActivity.class);
							VacCodePrimitive vcPrim = new VacCodePrimitive(ApprovalCommonActivity.this);
							vcPrim.setTag(ApprovalConstant.Tags.INTENT, intent);
							prim = vcPrim;
							break;
					}
					if (intent != null) {
						executePrimitive(prim);
					}
				}
			});
			break;
		case R.id.common_menu_refresh:
			onRefresh();
			break;
		}
		return true;
	}
	
	@Override
	protected void onReceive(Primitive primitive, SKTException e) {
		if (e == null) {
			if (primitive instanceof DraftFormPrimitive || primitive instanceof VacCodePrimitive) {
				Intent intent = (Intent) primitive.getTag(ApprovalConstant.Tags.INTENT);
				primitive.removeTag(ApprovalConstant.Tags.INTENT);
				intent.putExtra(ApprovalConstant.IntentArg.PRIMITIVE, primitive);
				startActivityForResult(intent, ApprovalConstant.RequestCode.REFRESH);
			}
		}
	}
	
	@Override
	protected void onActivityResultX(int requestCode, int resultCode, Intent data) {
		if (requestCode == ApprovalConstant.RequestCode.REFRESH)
			onRefresh();
		if (requestCode == ApprovalConstant.RequestCode.FINISH) {
			if (resultCode == ApprovalConstant.ResponseCode.OK) 
				finish();
		}
	}
	
	protected void onRefresh() {
		selectApprovalType = OptionMenuSelectValue.OUTSIDE_FORM;
		SKTUtil.log(TAG, "Common onRefresh");
	}

	@Override
	protected void onCreateX(Bundle bundle) {
		userPref = getSharedPreferences("USERINFO", MODE_PRIVATE);
		boolean resultPermission =false;
		resultPermission = permissionCheck();
	}

	public void selectMemberSearch(DialogInterface.OnClickListener positiveListener, String caller) {
		// 2015-06-25 Join ?????? ?????? - ????????? ????????? ?????? ??????
//		String title = "????????? ??????";
		String title = "";
		// cooperator : ???????????????(ApplyOutsideApprovalActivity.java) - ????????? ?????? ???
		if("cooperator".equals(caller)) {
			title = "????????? ???????????? ??????";
		} 
		// approval : ????????? ??????(ApplyApprovalLineActivity.java) - ????????? ?????? ???
		else {
			title = "????????? ???????????? ??????";
		}
		// 2015-06-25 Join ?????? ???
		
		int stringArrayId = R.array.easyaproval_common_search_type;
		super.selectSingleChoiceDialog(title, stringArrayId, selectMemberSearchType, searchSelectListener, positiveListener, null);
	}
	
	public void selectApprovalType(DialogInterface.OnClickListener positiveListener) {
		String title = "?????? ????????????";
		int stringArrayId = R.array.easyaproval_common_approval_type;
		super.selectSingleChoiceDialog(title, stringArrayId, selectApprovalType, approvalSelectListener, positiveListener, null);
	}
	
	public void selectPrivateLine(List<PrivateLine> lineList, DialogInterface.OnClickListener positiveListener) {
		String title = "????????? ????????????";
		String[] stringArray = new String[lineList.size()];
		for (int i=0; i<stringArray.length; i++) {
			stringArray[i] = lineList.get(i).getAliasDesc();
		}
		super.selectSingleChoiceDialog(title, stringArray, 0, privateLineSelectListener, positiveListener, null);
	}
	
	public void selectExceptDate(Date fromDate, Date untilDate, boolean[] initData, final PEDialogInterface.OnClickListener onClickListener) {
		// ???????????? ??????????????? ????????? ????????? ????????? ?????????.
		if (fromDate.after(untilDate) == true)
			return;
		
		String listItems[] = generateListData(fromDate, untilDate);
	    
		String title = "?????? ????????? ??????";
		
		selectExceptDate = new boolean[listItems.length];
		for (int i=0; i<selectExceptDate.length; i++) {
			if (initData == null) {
				// 2015-03-05 Join ?????? ?????? - ????????? ?????? ?????? ????????? ?????? ??????  
				//selectExceptDate[i] = true;
				selectExceptDate[i] = false;
				// 2015-03-05 Join ?????? ???
			}
			else
				selectExceptDate[i] = initData[i];
		}
		super.selectMultiChoiceDialog(title, listItems, selectExceptDate, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				selectExceptDate[which] = isChecked;
			}
		}, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickListener.onClick(selectExceptDate, null);
			}
		}, null);
	}
	
	private String[] generateListData(Date fromDate, Date untilDate) {
		int diffDays = DateUtil.diffDate(fromDate, untilDate) + 1;
		String[] listData = new String[diffDays];
		
		for (int i=0; i<diffDays; i++) {
			listData[i] = formatDate(DateUtil.addDate(fromDate, i));
		}
		
		return listData;
	}
	
	private String formatDate(Date date) {
		String dateFormat = "yyyy???MM???dd???(E)";
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(date).toString();
	}
	
	public void selectDate(final PEDialogInterface.OnClickListener wheelListener, Date initDate) {
		if (initDate == null)
			initDate = new Date(System.currentTimeMillis());
		
		LayoutInflater dialogLayout = getLayoutInflater();
		View selectDateView = dialogLayout.inflate( R.layout.easy_date_dialog, null );
		final WheelDatePickerDialogHelper helper = new WheelDatePickerDialogHelper(selectDateView, initDate);
		super.customViewDialog(null, selectDateView, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d("WHEELDATE", "Ok : " + helper.toString());
				wheelListener.onClick(helper.getCurrentDate(), helper.toString());
			}
			
		}, null);
	}
	
	public void selectTime(final PEDialogInterface.OnClickListener timeListener, Date initTime) {
		if (initTime == null)
			initTime = new Date(System.currentTimeMillis());
		
		LayoutInflater dialogLayout = getLayoutInflater();
		View selectTimeView = dialogLayout.inflate( R.layout.easy_time_dialog, null );
		final WheelTimePickerDialogHelper helper = new WheelTimePickerDialogHelper(selectTimeView, initTime);
		super.customViewDialog(null, selectTimeView, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d("WHEELDATE", "Ok : " + helper.toString());
				timeListener.onClick(helper.getCurrentDate(), helper.toString());
			}
		}, null);
	}
	
	public void selectGuntaeCode(final PEDialogInterface.OnClickListener guntaeListener, VocCodeTree tree) {
		WheelGuntaeDialog dialog = new WheelGuntaeDialog(ApprovalCommonActivity.this, tree);
		dialog.setOnClickListener(new PEDialogInterface.OnClickListener() {
			@Override
			public void onClick(Object obj, String arg) {
				guntaeListener.onClick(obj, arg);
			}
			
		});
		dialog.show();
	}
	public void viewApprovalLine(List<SancLine> bossList) {
//		LinearLayout dialogView = new LinearLayout(ApprovalCommonActivity.this);
//		dialogView.setBackgroundColor(Color.WHITE);
//		int viewLength = LinearLayout.LayoutParams.FILL_PARENT;
//		ListView listView = new ListView(ApprovalCommonActivity.this);
//		dialogView.addView(listView, new LinearLayout.LayoutParams(viewLength, viewLength));
		
		ApprovalLineViewAdapter adaptor = null;
		adaptor = new ApprovalLineViewAdapter(ApprovalCommonActivity.this, R.layout.easy_apply_line_listview_signed, bossList);
		
		super.customListViewDialog("?????? ?????? ??????", adaptor, null);
	}
	
	public void viewAttachFile(List<AttachFile> attachFileList) {
		AttachFileViewAdapter adaptor = null;
		adaptor = new AttachFileViewAdapter(ApprovalCommonActivity.this, R.layout.easy_common_listview_approval, attachFileList);
		
		super.customListViewDialog("????????????", adaptor, null);
	}
	
	protected int getSelectApprovalType() {
		return this.selectApprovalType;
	}
	
	protected int getSelectMemberSearchType() {
		return this.selectMemberSearchType;
	}
	
	protected int getSelectPrivateLine() {
		return this.selectPrivateLine;
	}
	
	
	DialogInterface.OnClickListener approvalSelectListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.d(TAG, "which =========== " + which);
			selectApprovalType = which;
		}
	};
	
	DialogInterface.OnClickListener searchSelectListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			selectMemberSearchType = which;
		}
	};
	
	DialogInterface.OnClickListener privateLineSelectListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			selectPrivateLine = which;
		}
	};
	
	public static class DialogType {
		public final static int DIALOG_APPROVAL_TYPE = 1;
		public final static int DIALOG_MEMBER_TYPE = 2;
		public final static int DIALOG_DATE = 3;
		public final static int DIALOG_TIME = 4;
	}
	
	public static class OptionMenuSelectValue {
		public final static int OUTSIDE_FORM = 0;
		public final static int VACATION_FORM = 1;
	}
	
	public void ApprovalCommonActivity(){
		
	}


	SharedPreferences userPref;
	public boolean permissionCheck() {
		if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
			if (       checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
					|| checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
					|| checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
					|| checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
					|| checkSelfPermission(Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED)
			{
				requestPermissions(permissions, PERMISSIONS_REQUEST_READ_PHONE_STATE);
				return false;
			}
			else {
				if("".equals(userPref.getString("FIRSTRUN",""))){
					requestPermissions(permissions, PERMISSIONS_REQUEST_READ_PHONE_STATE);
					SharedPreferences.Editor edit = userPref.edit();
					edit.putString("FIRSTRUN", "N");
					edit.apply();
				}return true;
			}
		}else{
			return true;
		}
	}
	@Override
	public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
		return super.shouldShowRequestPermissionRationale(permission);
	}
	public static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;
	public static String[] permissions = {
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_SMS,
			Manifest.permission.WRITE_CONTACTS/*,
														Manifest.permission.REQUEST_INSTALL_PACKAGES*/
	};

	public boolean hasAllPermissionGranted(int[] grantResults) {
		boolean check = false;
		for (int result : grantResults) {
			//   Log.i(TAG, "result..."+result);
			if (result == PackageManager.PERMISSION_DENIED) {
				return false;
			}
		}
		return true;
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch(requestCode){
			case 2:
				Log.v("","=\n\n=======================onRequestPermissionResult=======================");
				if(hasAllPermissionGranted(grantResults)){}

				break;
		}
	}
	
//	public static class MemberSearchValue {
//		public final static int MEMBER_SEARCH = 0;
//		public final static int ORGANIZATIION_SEARCH = 1;
//	}



	/*??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????*/
	//ApprovalCommonActivity ??? ???????????? ?????? ?????? ??????
	public class JsonAction extends AsyncTask<String, Void, String> {
		// --------------------------------------------------------------------------------------------
		// #region ???????????? ?????? ??????
		// ?????? ?????? Progressbar
//        ProgressDialog progressDialog;
		CommonProgress progressDialog;
		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;

		String jsonObject = null;

		String primitive = "";
		String url = "";
		Context context;

		String fileName = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog = new CommonProgress(context);
			progressDialog.show();

			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialogInterface) {
					String closeStr = "";
					if (os != null) {
						try {
							os.close();
							closeStr = closeStr + "os";
						} catch (Exception e) {
						}
					}

					if (is != null) {
						try {
							is.close();
							closeStr = closeStr + "is";
						} catch (Exception e) {
						}
					}

					if (baos != null) {
						try {
							baos.close();
							closeStr = closeStr + "baos";
						} catch (Exception e) {
						}
					}

					if (conn != null) {
						conn.disconnect();
						closeStr = closeStr + "conn";
					}
					cancel(true);//AsyncTask ??????


				}
			});

		}

		// primitive ??? ?????? ?????? ?????? ???????????????, URL??? ???????????????.
		//url ?????? ?????? ) http://localhost:8080?primitive=PRIMITIVE_LOGIN&id=hong&pw=1234
		public JsonAction(String primitive, String url, Context context) {
			this.primitive = primitive;
			this.url = url;
			this.context = context;
		}

		public JsonAction(String primitive, String url, Context context, String fileName) {
			this.primitive = primitive;
			this.url = url;
			this.context = context;
			this.fileName = fileName;
		}

		private void multipartUpload() {
			DataOutputStream dos = null;
			try {
				File f = new File(fileName);
				int fileSize = (int)f.length();
				FileInputStream mFileInputStream = new FileInputStream(fileName);

				conn.setRequestMethod("POST");
				conn.setConnectTimeout(15000);
				conn.setReadTimeout(15000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");

				int responseCode = conn.getResponseCode();	// ????????? ????????? ???????????? ??????????????? ?????????.

				Log.d(TAG, TAG + " ACTION responsecode  " + responseCode + "----" + conn.getResponseMessage());
				if (responseCode == HttpURLConnection.HTTP_OK) {    // ?????? ????????? ????????? ??????
					dos = new DataOutputStream(conn.getOutputStream());
					dos.writeBytes("--*****\r\n");

					// ?????? ????????? ?????? ??????
					JSONObject jsonRequest = new JSONObject();
					for (String params : url.split("\\?")[1].split("&")) {
						jsonRequest.put(params.split("=")[0], params.split("=")[1]);
					}

					dos.writeBytes("\"Content-Disposition: form-data; name=\"params\"" + "\r\n");
					dos.writeBytes("\r\n");
					dos.writeBytes(jsonObject.toString());
					dos.writeBytes("\r\n");
					dos.writeBytes("--*****\r\n");

					// ?????? ????????? ?????? ??????
					String extFileName = URLEncoder.encode(fileName.substring(fileName.lastIndexOf("/") + 1), "utf-8");
					dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + extFileName + "\"" + "\r\n");
					dos.writeBytes("\r\n");
					int bytesAvailable = mFileInputStream.available();
					int maxBufferSize = 1024;
					int bufferSize = Math.min(bytesAvailable, maxBufferSize);
					byte[] buffer = new byte[bufferSize];
					int bytesRead = 0;

					while ((bytesRead = mFileInputStream.read(buffer, 0, buffer.length)) != -1) {    // ?????? ????????? body??? ??????
						dos.write(buffer, 0, bytesRead);
					}

					dos.writeBytes("\r\n");
					dos.writeBytes("--*****--\r\n");
					mFileInputStream.close();
					dos.flush();

					BufferedReader response = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
					String line = null;
					String responseJson = "";

					while ((line = response.readLine()) != null) {
						responseJson += line;
					}

					jsonObject = responseJson;
				}
			} catch (Exception e) {
			}
		}

		private void dataConnection() {
			try {
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(15000);
				conn.setReadTimeout(15000);
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.setDoInput(true);

				int responseCode = conn.getResponseCode();	// ????????? ????????? ???????????? ??????????????? ?????????.
				Log.d(TAG, TAG + " ACTION responsecode  " + responseCode + "----" + conn.getResponseMessage());
				if (responseCode == HttpURLConnection.HTTP_OK) {    // ?????? ????????? ????????? ??????
					is = conn.getInputStream();    // ?????? ??????
					baos = new ByteArrayOutputStream();    // ????????? ????????? ??????.
					byte[] byteBuffer = new byte[1024];
					byte[] byteData = null;
					int nLength = 0;

					while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {    // ?????????.
						baos.write(byteBuffer, 0, nLength);
					}
					byteData = baos.toByteArray();
					String response = new String(byteData, "utf-8");
					Log.d("JsonAction", TAG + "responseData  = " + response);

					if (response == null || response.equals("")) {
						Log.e("JsonAction", TAG + "Response is NULL!!");
					} else {
						if (response.trim().equals("")) {
							Log.e("JsonAction", TAG + "Response is NULL!!");
						}
					}

					Map<String, List<String>> headers = conn.getHeaderFields();    // ?????? ??????
					Iterator<String> it = headers.keySet().iterator();
					while (it.hasNext()) {
						String key = it.next();
						List<String> values = headers.get(key);
						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < values.size(); i++) {
							sb.append(";" + values.get(i));
						}
					}
					jsonObject = response;
				}
			} catch (Exception e) {
			}
		}

		@Override
		protected String doInBackground(String... arg0) {

			try {
//				StringBuffer body = new StringBuffer();
//				body.append(url);
				URL connUrl = new URL(url);//??????

//				Log.i(TAG, TAG + "URL : = " + body.toString());
//				Log.i(TAG, TAG + "URL : = " + url);

				conn = (HttpURLConnection) connUrl.openConnection();

				if (!fileName.isEmpty()) {
					multipartUpload();
				} else {
					dataConnection();
				}

			} catch (IOException e) {
				e.printStackTrace();

				//????????? test
//				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ApprovalCommonActivity.this);//????????? test
//				alertDialog.setTitle("??????");//????????? test
//				alertDialog.setMessage(""+e.toString());//????????? test
//				alertDialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {//????????? test
//					@Override//????????? test
//					public void onClick(DialogInterface dialogInterface, int i) {//????????? test
//						dialogInterface.dismiss();//????????? test
//					}
//				});//????????? test
//				alertDialog.setCancelable(true);//????????? test
//				alertDialog.show();//????????? test

			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (Exception e) {
					}
				}

				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
					}
				}

				if (baos != null) {
					try {
						baos.close();
					} catch (Exception e) {
					}
				}

				if (conn != null) {
					conn.disconnect();
				}
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (null != progressDialog && null != context) {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			onJsonActionPost(primitive, result);
		}
	}

	protected abstract void onJsonActionPost(String primitive, String result);


	public final String nullCheck(String nullStr) {
		if (nullStr != null && !nullStr.equals("")) {
			if("null".equals(nullStr)){
				return "";
			}
			return nullStr;
		}
		return "";
	}

	public void alert(String title, String message, DialogInterface.OnClickListener listener, boolean cancelable) {
		(new AlertDialog.Builder(this)).setTitle(title).setMessage(message).setPositiveButton("??????", listener).setCancelable(cancelable).show();
	}

}
