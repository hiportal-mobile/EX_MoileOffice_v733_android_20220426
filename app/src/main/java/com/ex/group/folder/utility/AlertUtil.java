package com.ex.group.folder.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.view.View;


/**
 * 이 클래스는 알럿메시지를 표출하기 위해서 사용하는 클래스입니다.
 * RootActivity에 모두 구현하면 상위클래스위 소스코드가 복잡해지므로 이곳에 구현합니다.
 * 
 * 이클래스를 이용하므로써 팝업 디자인 변경등을 한번에 하기 좋다.
 * 개발자들은 가이드대로 코딩에 기를 기울이자.
 * 
 * @date 2012.08.20
 * @author kswksk
 */
public class AlertUtil {
	@SuppressWarnings("unused")
	private Dialog alert;
	private Context context;
	private View systemView = null;

	private OnClickListener finish = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			((Activity) context).finish();
		}
	};

	private OnCancelListener cancelListener = new OnCancelListener() {

		@Override
		public void onCancel(DialogInterface dialog) {
			((Activity) context).finish();
		}
	};

	/**
	 * 생성자
	 * @param context
	 */
	public AlertUtil(Context context) {
		this.context = context;
	}

	/**
	 * 안드로이드 컨텍스트를 설정한다.
	 * @param context 컨텍스트
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * 팝업객체를 초기화 한다.
	 */
	private void initAlert() {
		if (alert != null) {
			// 기존에 떠있는 팝업을 닫을지는 고민해보자!
			// alert.cancel();
			alert.dismiss();
		}

		alert = null;
		systemView = null;
	}

	/**
	 * 다이얼로그 표출
	 * @param dialog
	 */
	public void showDialog(Dialog dialog) {
		cancel();
		this.alert = dialog;
		this.alert.show();
	}

	/**
	 * 팝업을 닫는다.
	 */
	public void cancel() {
		if (this.alert != null) {
			// this.alert.cancel();
			this.alert.dismiss();
		}
	}

	public void setCanceledOnTouchOutside(boolean flag){
		alert.setCanceledOnTouchOutside(flag);
	}
	public void setCancelable(boolean flag){
		alert.setCancelable(flag);
	}


	public boolean isShowing(){
        return alert.isShowing();
    }



}
