package com.ex.group.mail.widget;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;


import com.ex.group.mail.util.Global;
import com.skt.pe.common.data.SKTUtil;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import com.ex.group.folder.R;


/**
 * 관계사 App 둘러보기 화면
 * @author june
 *
 */
public class FileListDialog extends Dialog {

	private ArrayList<Map<String, Object>> data ;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.8f;
		getWindow().setAttributes(lpWindow);

		setContentView(R.layout.mail_file_dialog);
	
		setFileUI(data);
		
	}

	public FileListDialog(Context context, ArrayList<Map<String, Object>> data) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		this.context = context;
		this.data = data;
	}
	
	private void setFileUI(ArrayList<Map<String, Object>> data) {
		int xmlcount = 0;
		xmlcount = data.size();
		if(xmlcount > 0) {
			LinearLayout layout = (LinearLayout)findViewById(R.id.FILEATTLIST);
			layout.removeAllViews();
			
			for(int a = 0 ; a < xmlcount ; a++){
				final String m_FileName =  data.get(a).get("fileName").toString();
				final String m_FileId 	=  data.get(a).get("fileId").toString();
			
				LinearLayout tempLayout = (LinearLayout) LayoutInflater.from(this.context).inflate(R.layout.mail_filelist, null);
				Button txtFileName	= (Button) tempLayout.findViewById(R.id.filebtn);
				
				txtFileName.setText(m_FileName);
				txtFileName.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//SKTUtil.viewSecurityImage(context, m_FileName, m_FileId);
						DocuzenViewLauncher((Activity)context,m_FileName,m_FileId);
						Log.i("FileListDialog", "file name : "+m_FileName+"\nfileID : "+m_FileId);
					}
				});
				layout.addView(tempLayout);
			}
		}
	}

	// 문서뷰어 연동
	public static void DocuzenViewLauncher(Activity activity, String file_name, String file_url) {
		Intent intent = new Intent(activity, handyhis.dz.viewer.DzImageViewer.DzImageViewer.class);

		Log.i("파일명", file_name);
		Log.i("파일패스", file_url);


		try{

			String ext = file_name.substring(file_name.lastIndexOf("."));
			long tmp = System.currentTimeMillis();

			String tmpName = String.valueOf(tmp) + ext;


			Log.d("AUDIT", "################## ext : "+ext);
			Log.d("AUDIT", "################## tmpName : "+tmpName);

			String temp = Global.DZCSURLPREFIX;
			//temp += "fileName=" + URLEncoder.encode(tmpName, "utf-8") + "&filePath=" + URLEncoder.encode(file_url, "utf-8");
			temp += "fileName=" + URLEncoder.encode(tmpName, "euc-kr") + "&filePath=" + URLEncoder.encode(file_url, "euc-kr");
			//temp += "fileName=" + tmpName + "&filePath=" + file_url;
			//temp += "fileName=" + URLEncoder.encode(tmpName) + "&filePath=" + URLEncoder.encode(file_url);

//					temp += "fileName=" + tmpName + "&filePath=" +file_url;
//					temp = URLEncoder.encode(temp, "utf-8");
//					temp += "fileName=" + tmpName + "&filePath=" + file_url;

			Log.d("AUDIT", "################## temp : "+temp);
			Log.d("AUDIT", "################## baseDzcsUrl : "+Global.DZCSURL);


			Log.d("AUDIT", "################## baseDzcsUrl : "+Global.DZCSURL);
			Log.d("AUDIT", "################## URL : "+temp);

			intent.putExtra("baseDzcsUrl", Global.DZCSURL);
			intent.putExtra("URL", temp);
//					intent.putExtra("URL", "toiphoneapp://callDocumentFunction?fileName=1533889680636.hwp&filePath=http://eai.ex.co.kr:8088/jsp/com/gt/DownLoadTest.jsp?filename=pi/MSDS고시최종(20080110).hwp");
//

			Log.d("AUDIT", "################## Decode : "+ URLDecoder.decode(temp, "utf-8"));
			activity.startActivity(intent);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
