package com.chinadci.mel.mleo.ui.fragments.base;

import com.chinadci.mel.mleo.ui.fragments.ContentFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class BaseV4Fragment4Content extends ContentFragment {

	View mView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = super.onCreateView(inflater, container, savedInstanceState);
		return mView;
	}
	/**
	 * 显示toast消息
	 */
	public void showToastMsg(Context ctxt,String msg){
		Toast.makeText(ctxt, msg, Toast.LENGTH_SHORT).show();
	}	
	
	/**
	 * intent跳转函数
	 * */
	public void intentTO(Context ctxt,Class<?> c){
		Intent intent = new Intent();
		intent.setClass(ctxt,c);
		startActivity(intent);  
	}
	/** 
	 *显示dialog
	 */
	public void showDialog(Context ctxt,String title ,
			String msg,String ok,DialogInterface.OnClickListener onClickListener1,
			String cancel,DialogInterface.OnClickListener onClickListener2){
		Dialog dialog = new AlertDialog.Builder(ctxt)//
		.setTitle(title)//
		.setMessage(msg)//
		.setPositiveButton(ok,onClickListener1)//
		.setNegativeButton(cancel,onClickListener2)//
		.create();//创建
		dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失  
		dialog.show();
	}
}
