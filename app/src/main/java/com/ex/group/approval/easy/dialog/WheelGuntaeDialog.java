package com.ex.group.approval.easy.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;

import com.ex.group.folder.R;
import com.ex.group.approval.easy.addressbook.data.GuntaeCdVo;
import com.ex.group.approval.easy.dialog.ifaces.PEDialogInterface;
import com.ex.group.approval.easy.domain.VocCodeTree;
import com.skt.pe.widget.wheelview.OnScrollStopListener;
import com.skt.pe.widget.wheelview.WheelView;

public class WheelGuntaeDialog extends Dialog implements OnScrollStopListener, OnItemClickListener {
	private WheelView wvLevel1, wvLevel2, wvLevel3;
//	private String selectedIndex = null;
	private PEDialogInterface.OnClickListener guntaeListener;
	
//	private GuntaeCodeManager guntaeCode;
	private VocCodeTree tree;
	private ArrayList<GuntaeCdVo> vo1;//대
	private ArrayList<GuntaeCdVo> vo2;//중
	private ArrayList<GuntaeCdVo> vo3;//소
	public WheelGuntaeDialog(Context context, VocCodeTree tree) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.easy_guntae_dialog);
		
		this.tree = tree;
//		guntaeCode = new GuntaeCodeManager(getContext());
//		selectedIndex = "1_1";
		
		this.loadWheelData();
		
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int pixels = (int) ((metrics.density * WheelSpec.height) + 0.5f);
		
		wvLevel1 = (WheelView)findViewById(R.id.guntae_dialog_wheelview_level1);
		wvLevel1.setRowHeight(pixels);
		wvLevel1.setRowNum(WheelSpec.wheelRowNum);
		wvLevel1.setTextSize(WheelSpec.textSize);
//		wvLevel1.setRepeat(false);
		wvLevel1.setItems(tree.getNames("1"));
		wvLevel1.setSelection(0);
		wvLevel1.setOnScrollStopListener(this);
		
		wvLevel2 = (WheelView)findViewById(R.id.guntae_dialog_wheelview_level2);
		wvLevel2.setRowHeight(pixels);
		wvLevel2.setRowNum(WheelSpec.wheelRowNum);
		wvLevel2.setTextSize(WheelSpec.textSize);
		wvLevel2.setRepeat(false);
		wvLevel2.setItems(tree.getNames("1_1"));
		wvLevel2.setSelection(0);
		wvLevel2.setOnScrollStopListener(this);
		wvLevel2.setOnItemClickListener(this);
	}

	
	public WheelGuntaeDialog(Context context, ArrayList<GuntaeCdVo> vo1, ArrayList<GuntaeCdVo> vo2,ArrayList<GuntaeCdVo> vo3) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.easy_guntae_dialog);
		
		this.vo1 = vo1;
//		guntaeCode = new GuntaeCodeManager(getContext());
//		selectedIndex = "1_1";
		
		this.loadWheelData();
		
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int pixels = (int) ((metrics.density * WheelSpec.height) + 0.5f);
		
		wvLevel1 = (WheelView)findViewById(R.id.guntae_dialog_wheelview_level1);
		wvLevel1.setRowHeight(pixels);
		wvLevel1.setRowNum(WheelSpec.wheelRowNum);
		wvLevel1.setTextSize(WheelSpec.textSize);
		String[] voData;
		voData = new String[vo1.size()];
		for (int i = 0; i < vo1.size(); i++) {
			voData[i] = vo1.get(i).getAttendNm();
			Log.d("attendName :::::: ", voData[i]);
		}
		wvLevel1.setRepeat(false);
		wvLevel1.setItems(voData);
		wvLevel1.setSelection(0);
		wvLevel1.setOnScrollStopListener(this);

		wvLevel2 = (WheelView)findViewById(R.id.guntae_dialog_wheelview_level2);
		wvLevel2.setRowHeight(pixels);
		wvLevel2.setRowNum(WheelSpec.wheelRowNum);
		wvLevel2.setTextSize(WheelSpec.textSize);
		wvLevel2.setRepeat(false);
		String[] voData2;
		voData2 = new String[vo2.size()];
		for (int i = 0; i < vo2.size(); i++) {
			voData[i] = vo2.get(i).getAttendNm();
			Log.d("attendName :::::: ", voData2[i]);
		}
		wvLevel2.setItems(voData2);
		wvLevel2.setSelection(0);
		wvLevel2.setOnScrollStopListener(this);
		//wvLevel2.setOnItemClickListener(this);
		
		wvLevel3 = (WheelView)findViewById(R.id.guntae_dialog_wheelview_level3);
		wvLevel3.setRowHeight(pixels);
		wvLevel3.setRowNum(WheelSpec.wheelRowNum);
		wvLevel3.setTextSize(WheelSpec.textSize);
		wvLevel3.setRepeat(false);
		String[] voData3;
		voData3 = new String[vo3.size()];
		for (int i = 0; i < vo3.size(); i++) {
			voData3[i] = vo3.get(i).getAttendNm();
			Log.d("attendName :::::: ", voData3[i]);
		}
		wvLevel3.setItems(voData3);
		wvLevel3.setSelection(0);
		wvLevel3.setOnScrollStopListener(this);
		//wvLevel3.setOnItemClickListener(this);
	}
	
	@Override
	public void onScrollStop(AbsListView view, int arg1) {
		switch (view.getId()) {
			case R.id.guntae_dialog_wheelview_level1:
			//	String[] names = tree.getNames(getSelectedLevel1Index());
				String[] names =new String[vo1.size()];	
				for (int i = 0; i < names.length; i++) {
					names[i] = vo1.get(i).getAttendNm();
				}
				if (names != null)
					wvLevel2.setItems(names);
				break;
//			case R.id.guntae_dialog_wheelview_level2:
//				selectedIndex = guntaeCode.makeIndex(selectedIndex, wvLevel2.getIndex() + 1);
//				break;
		}
//		Log.d("TEST", "Wheel Changed: " + selectedIndex);
	}

	int selectList = 0;
	
	@Override
	public void onItemClick(AdapterView<?> v, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.guntae_dialog_wheelview_level2) {
//			Log.d("TEST", "Wheel Click : " + getSelectedIndex());
//			GuntaeCodeList selectCode = guntaeCode.getNextList(getSelectedIndex());
			String[] names = tree.getNames(this.getSelectedLevel2Index());
			if (names == null) {
				guntaeListener.onClick(v, getSelectedLevel2Index());
				dismiss();
			} else {
//				Log.d("TEST", "Guntae List: " + names.toString());
				
				new AlertDialog.Builder(getContext())
					.setSingleChoiceItems(names, selectList, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							selectList = which;
						}
					})
					.setPositiveButton("확인", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							/*
							 * 리스트의 선택은 인덱스가 0부터 시작하지만 근태코드 자료구조는 1부터 시작해서 '+1'로 보정함
							 */
//							selectedIndex = guntaeCode.makeIndex(selectedIndex, selectList + 1);
							guntaeListener.onClick(dialog, getSelectedLevel3Index(selectList + 1));
							dismiss();
						}
					})
					.setNegativeButton("취소", null)
					.show();
			}
		}	
	}
	
	public void setOnClickListener(PEDialogInterface.OnClickListener guntaeListener) {
		this.guntaeListener = guntaeListener;
	}
	
	public String getSelectedLevel1Index() {
		return VocCodeTree.makeIndex(wvLevel1.getIndex() + 1);
	}
	
	public String getSelectedLevel2Index() {
		return VocCodeTree.makeIndex(wvLevel1.getIndex() + 1, wvLevel2.getIndex() + 1);
	}
	
	public String getSelectedLevel3Index(int index) {
		return VocCodeTree.makeIndex(wvLevel1.getIndex() + 1, wvLevel2.getIndex() + 1, index);
	}
	
	
	
	private void loadWheelData() {
	}
	
	protected class WheelSpec {
		protected final static int wheelRowNum = 5;
		protected final static int textSize = 15;
		protected final static int height = 70;
	}
}