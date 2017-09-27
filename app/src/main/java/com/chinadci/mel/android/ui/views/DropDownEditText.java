package com.chinadci.mel.android.ui.views;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import com.chinadci.mel.R;
import com.chinadci.mel.android.core.KeyValue;


/**
 * Created by LoveExtra on 2017/8/25.
 */
public class DropDownEditText extends EditText {
	
	public static final int HANDLE_NULL=0x000;
    public static final int HANDLE_SELECT=0x001;
    public static final int HANDLE_DELETE=0x002;

    Context context;
    ListView listView;
    PopupWindow popup;

    List<KeyValue> keyValueList;
    OnSelectedHandleListener selectedHandleListener;
    BaseAdapter adapter;
    int selectedIndex=-1;

    int popupHeight;
    int spacing;

    public DropDownEditText(Context context) {
        this(context,null);
    }

    public DropDownEditText(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public DropDownEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setOnSelectedHandleListener(OnSelectedHandleListener listener){
        selectedHandleListener=listener;
    }

    private void initView(Context c) {
        context = c;
        setEllipsize(TextUtils.TruncateAt.END);
        //setOnFocusChangeListener(focusChangeListener);
        setOnClickListener(clickListener);
        popupHeight = (int) (120 * context.getResources().getDisplayMetrics().density);
        spacing = (int) (8 * context.getResources().getDisplayMetrics().density);
    }

    OnFocusChangeListener focusChangeListener=new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            performPop();
        }
    };

    OnClickListener clickListener = new OnClickListener() {
        public void onClick(View v) {
            performPop();
        }
    };

    void performPop() {
        if (popup==null){
            popup = new PopupWindow(listView, getMeasuredWidth(), popupHeight);
            popup.setTouchable(true);
            popup.setOutsideTouchable(true);
            setFocusableInTouchMode(true);
            popup.setBackgroundDrawable(new BitmapDrawable());
        }
        if (popup.isShowing()){
            popup.dismiss();
        }else {
            popup.showAsDropDown(this);
            notifyUpdate();
        }
        DropDownEditText.this.setFocusable(true);
        DropDownEditText.this.setFocusableInTouchMode(true);
        DropDownEditText.this.requestFocus();
    }

    public void setSelectedItem(int i){
        if (keyValueList!=null&&keyValueList.size()>i){
            selectedIndex=i;
            setText(keyValueList.get(i).getKey().toString());
            if (selectedHandleListener!=null){
                selectedHandleListener.onSelectedHandle(DropDownEditText.this,keyValueList.get(selectedIndex).getKey(),HANDLE_SELECT);
            }
        }
    }

    public void setSelectedItem(Object k){
        if (keyValueList!=null&&keyValueList.size()>0){
            for (int i=0;i<keyValueList.size();i++){
                if (keyValueList.get(i).getKey().equals(k)){
                    selectedIndex=i;
                    setText(keyValueList.get(i).getKey().toString());
                    if (selectedHandleListener!=null){
                        selectedHandleListener.onSelectedHandle(DropDownEditText.this,keyValueList.get(selectedIndex).getKey(),HANDLE_SELECT);
                        break;
                    }
                }
            }
        }
    }

    public Object getSelectedItem(){
        if (keyValueList!=null&&selectedIndex>-1&&keyValueList.size()>selectedIndex){
            return keyValueList.get(selectedIndex);
        }else {
            return null;
        }
    }
    
    public void removeKeyValue(int position){
    	if(keyValueList!=null&&keyValueList.size()>position){
    		keyValueList.remove(position);
    	}
    }

    public void notifyUpdate(){
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    public void setDate(List<KeyValue> kvs){
        if (kvs==null||kvs.size()==0){
            return;
        }
        if (keyValueList!=null){
            keyValueList.clear();
        }
        keyValueList=kvs;
        adapter=new EditDropDownAdapter(context,keyValueList);
        listView=new ListView(context);
        listView.setAdapter(adapter);
        listView.setBackgroundResource(R.drawable.bg_autotextview);
        listView.setDividerHeight(1);
        listView.setDivider(context.getResources().getDrawable(R.color.darkgreen));
    }

    public boolean isPopupShowing() {
        return popup.isShowing();
    }

    public void dismissDropDown() {
       /* InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.displayCompletions(this, null);
        }*/
    	if(popup!=null){
    		popup.dismiss();
    	}
    }

    class EditDropDownAdapter extends BaseAdapter{

        Context c;
        List<KeyValue> kvList;
        ViewHolder holder;

        public EditDropDownAdapter(Context c,List<KeyValue> kvList){
            this.c=c;
            this.kvList=kvList;
        }

        @Override
        public int getCount() {
            return kvList.size();
        }

        @Override
        public Object getItem(int position) {
            return kvList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
        	 convertView = LayoutInflater.from(c).inflate(R.layout.item_edit_list, null);
             TextView tv= (TextView) convertView.findViewById(R.id.tv_item_edit_list);
             ImageView imb= (ImageView) convertView.findViewById(R.id.btn_item_edit_list);
             Log.i("ydzf","getView position="+position+",key="+kvList.get(position).getKey()+",value="+kvList.get(position).getValue().toString());
             tv.setText(kvList.get(position).getValue().toString());
             /*tv.setTag(kvList.get(position));
             imb.setTag(position);*/
             tv.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     String str=kvList.get(position).getKey().toString();
                     DropDownEditText.this.setText(str);
                     selectedIndex=position;
                     if (selectedHandleListener!=null){
                         selectedHandleListener.onSelectedHandle(DropDownEditText.this,kvList.get(position),HANDLE_SELECT);
                     }
                     dismissDropDown();
                 }
             });
            imb.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (selectedHandleListener!=null){
                         selectedHandleListener.onSelectedHandle(v,kvList.get(position),HANDLE_DELETE);
                     }
                     kvList.remove(position);
                     EditDropDownAdapter.this.notifyDataSetChanged();
                 }
             });
           
             return convertView;
        }

        class ViewHolder{
            TextView tv;
            ImageView imb;
        }
    }

    public interface OnSelectedHandleListener{
        
        public void onSelectedHandle(View view, Object value, int operate);
    }
}
