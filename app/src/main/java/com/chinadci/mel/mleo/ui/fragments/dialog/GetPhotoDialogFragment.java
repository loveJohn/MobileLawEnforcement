package com.chinadci.mel.mleo.ui.fragments.dialog;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.chinadci.mel.R;

@SuppressLint("ValidFragment")
public class GetPhotoDialogFragment extends DialogFragment {

  private OnClickListener leftOnClickListener;
  private OnClickListener rightOnClickListener;

  public GetPhotoDialogFragment(OnClickListener leftOnClickListener,OnClickListener rightOnClickListener) {
    this.leftOnClickListener = leftOnClickListener;
    this.rightOnClickListener = rightOnClickListener;
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, 0);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.select_text_layout2, container, false);
    getDialog().setTitle("添加方式");
    ImageButton left = (ImageButton) view.findViewById(R.id.btn_ic_ic_photo);
    left.setOnClickListener(leftOnClickListener);
    ImageButton right = (ImageButton) view.findViewById(R.id.btn_ic_ic_pic);
    right.setOnClickListener(rightOnClickListener);
    return view;
  }
}
