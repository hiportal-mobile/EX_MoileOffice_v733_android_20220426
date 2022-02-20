package com.ex.group.elecappmemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ex.group.folder.MainActivity;
import com.ex.group.folder.R;

import static android.app.Activity.RESULT_OK;

public class DialogActivity extends Activity {
	private static final String TAG = DialogActivity.class.getSimpleName();

	private Context context = DialogActivity.this;

	TextView tv_title, tv_content, btnConfirm;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);

		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_content = (TextView)findViewById(R.id.tv_content);
		btnConfirm = (TextView)findViewById(R.id.btn_confirm);

		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String content = intent.getStringExtra("content");

		tv_title.setText(title);
		tv_content.setText(content);

		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("app", "N");
				startActivityForResult(intent, RESULT_OK);
				finish();
			}
		});
	}

}
