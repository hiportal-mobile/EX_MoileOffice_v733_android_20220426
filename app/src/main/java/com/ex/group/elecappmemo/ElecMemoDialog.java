package com.ex.group.elecappmemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ex.group.folder.MainActivity;
import com.ex.group.folder.R;

import static android.app.Activity.RESULT_OK;

public class ElecMemoDialog extends Dialog{
	private static final String TAG = ElecMemoDialog.class.getSimpleName();

	private Context mContext;

	public ElecMemoDialog(Context context, String title, String content, boolean cancelable, @Nullable  View.OnClickListener confirmListener)
	{
		super(context, R.style.DialogTheme);
		initialize(context, title, content, confirmListener);
		setCancelable(cancelable);
	}

	private void initialize(final Context context, String title, final String content, @Nullable View.OnClickListener confirmListener){
		mContext	= context;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.common_dialog_negative);
		
		TextView tv_title = (TextView)this.findViewById(R.id.tv_title);
		TextView tv_content = (TextView)this.findViewById(R.id.tv_content);
		TextView btnConfirm = (TextView)this.findViewById(R.id.btn_confirm);


		if(confirmListener==null){

			btnConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					Intent intent = new Intent(context, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("app", "N");
					((Activity)context).startActivityForResult(intent, RESULT_OK);
					dismiss();
				}
			});
		}else {
			btnConfirm.setOnClickListener(confirmListener);
		}


		tv_title.setText(title);
		tv_content.setText(content);
	}




}
