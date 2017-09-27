package com.chinadci.mel.android.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chinadci.mel.R;
import com.chinadci.mel.android.core.ExpKeyValue;
import com.chinadci.mel.android.core.KeyValue;
import com.chinadci.mel.android.core.interfaces.ISelectedChanged;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class DropDownExpandableSpinner extends TextView{

	public static final String GROUP_KEY="groupKey";
	public static final String CHILD_KEY="childKey";
    PopupWindow popup;
    ExpandableListView listView;
    Context context;

    ArrayList<Map<String,String>> groupMvList;
    ArrayList<ArrayList<Map<String,String>>> childMvLists;
    
    ArrayList<ExpKeyValue> dateKvList;		//二级KeyValue
    
    ArrayList<KeyValue> groupKvList;
    ArrayList<ArrayList<KeyValue>> childKvList;

    ISelectedChanged selectedChangedListener;
    int selGroupIndex=-1;
    int selChildIndex=-1;
    SimpleExpandableListAdapter listAdapter;
    int popupHeight;
    int spacing;

    public void setSelectedItem(int groupIndex,int childIndex) {
    	if(groupIndex<0){
    		return;
    	}
        if (groupKvList!=null&&groupIndex>=0&&groupKvList.size()>groupIndex){
            if (childKvList!=null&&childIndex>=0&&childKvList.size()>childIndex){
                selGroupIndex=groupIndex;
                selChildIndex=childIndex;
                setText((CharSequence) childKvList.get(groupIndex).get(selChildIndex).getValue());
                if (selectedChangedListener!=null){
                    selectedChangedListener.onSelectedChanged(DropDownExpandableSpinner.this,childKvList.get(groupIndex).get(selChildIndex).getKey());
                }
            }else if (childIndex<0){
                selGroupIndex=groupIndex;
                selChildIndex=-1;
                setText((CharSequence) groupKvList.get(selGroupIndex).getValue());
                if (selectedChangedListener!=null){
                    selectedChangedListener.onSelectedChanged(DropDownExpandableSpinner.this,groupKvList.get(selGroupIndex).getKey());
                }
            }
        }
    }

    public void setSelectedItem(Object k) {     //设置key值
        if (k==null){
            return;
        }
        if (groupKvList!=null&&groupKvList.size()>0){
        	for(int i=0;i<groupKvList.size();i++){
        		KeyValue kv=groupKvList.get(i);
                if (kv!=null){
                	if (k.equals(kv.getKey())){
                        selGroupIndex=i;
                        selChildIndex=-1;
                        setText((CharSequence) kv.getValue());
                        if (selectedChangedListener!=null){
                            selectedChangedListener.onSelectedChanged(DropDownExpandableSpinner.this,kv.getKey());
                        }
                    }
                }
        	}
        	
        }
        if (childKvList!=null&&childKvList.size()>0){
            for (int i=0;i<childKvList.size();i++){
                ArrayList<KeyValue> kvs=childKvList.get(i);
                if (kvs!=null){
                    for (int j=0;j<kvs.size();j++){
                    	KeyValue kv=kvs.get(j);
                        if (kv!=null){
                            if (k.equals(kv.getKey())){
                                selGroupIndex=i;
                                selChildIndex=j;
                                setText((CharSequence) kv.getValue());
                                if (selectedChangedListener!=null){
                                    selectedChangedListener.onSelectedChanged(DropDownExpandableSpinner.this,kv.getKey());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Object getSelectedItem() {
    	if(selGroupIndex>-1){
    		if (selChildIndex>-1){
                return childKvList.get(selGroupIndex).get(selChildIndex);
            }else{
                return groupMvList.get(selGroupIndex);
            }
    	}else{
    		return null;
    	}
    }
    
    public Object getSelectedKey(){
    	KeyValue kv=(KeyValue) getSelectedItem();
    	if(kv!=null){
    		return kv.getKey();
    	}else{
    		return kv;
    	}
    }

    public Object getSelectedValue() {
    	KeyValue kv=(KeyValue) getSelectedItem();
    	if(kv!=null){
    		return kv.getValue();
    	}else{
    		return kv;
    	}
    }

    public DropDownExpandableSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public DropDownExpandableSpinner(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public DropDownExpandableSpinner(Context context) {
        this(context,null);
    }

    public void setSelectedChangedListener(ISelectedChanged listener) {
        selectedChangedListener = listener;
    }
    
    public void setDate(ArrayList<KeyValue> gList,ArrayList<ArrayList<KeyValue>> cLists){
    	if(gList==null||gList.size()<1){
    		return;
    	}
    	if(groupKvList==null){
    		groupKvList=gList;
    	}else{
    		groupKvList.clear();
    		groupKvList=gList;
    	}
        if(childKvList==null){
        	childKvList=cLists;
        }else{
        	childKvList.clear();
        	childKvList=cLists;
        }
        //定义父列表项List数据集合  
	    ArrayList<Map<String, String>> groups = new ArrayList<Map<String, String>>();  
        //定义子列表项List数据集合  
        ArrayList<ArrayList<Map<String, String>>> childs = new ArrayList<ArrayList<Map<String, String>>>();
        
        for(int i=0;i<groupKvList.size();i++){
        	KeyValue gKv=groupKvList.get(i);
        	
        	Map<String, String> gMap = new HashMap<String, String>();
        	if(gKv!=null){
        		gMap.put(GROUP_KEY, (String)gKv.getValue());
        	}else{
        		gMap.put(GROUP_KEY, null);
        	}
        	groups.add(gMap);
        	if(childKvList!=null&&childKvList.size()>i){
        		ArrayList<Map<String, String>> child = new ArrayList<Map<String, String>>();
        		if(childKvList.get(i)!=null){
        			for(int j=0;j<childKvList.get(i).size();j++){
        				KeyValue cKv=childKvList.get(i).get(j);
        				
        				Map<String, String> cMap = new HashMap<String, String>();
        				if(cKv!=null){
        					cMap.put(DropDownExpandableSpinner.CHILD_KEY,(String)cKv.getValue());
        				}else{
        					cMap.put(DropDownExpandableSpinner.CHILD_KEY,null);
        				}
     	                child.add(cMap);
        			}
        			childs.add(child);
        		}else{
        			childs.add(null);
        		}
        	}
        }
        setAdapterData(groups,childs);
    }
    
    
    public void setJsonData(ArrayList<ExpKeyValue> dateKvList){		//带 subJson
    	if(dateKvList==null||dateKvList.size()<1){
    		return;
    	}
    	this.dateKvList=dateKvList;		//只有一级KV，value为json。解析适配
    	if(groupKvList==null){
    		groupKvList=new ArrayList<KeyValue>();
    	}else{
    		groupKvList.clear();
    	}
        if(childKvList==null){
        	childKvList=new ArrayList<ArrayList<KeyValue>>();
        }else{
        	childKvList.clear();
        }
        
        //定义父列表项List数据集合  
	    ArrayList<Map<String, String>> groups = new ArrayList<Map<String, String>>();  
        //定义子列表项List数据集合  
        ArrayList<ArrayList<Map<String, String>>> childs = new ArrayList<ArrayList<Map<String, String>>>();
        
        for(int i=0;i<dateKvList.size();i++){
        	ExpKeyValue dKv=dateKvList.get(i);
        	KeyValue kv=new KeyValue(dKv.getKey(),dKv.getValue());
        	groupKvList.add(kv);		//groupList
        	
        	Map<String, String> gMap = new HashMap<String, String>();  
        	gMap.put(GROUP_KEY, (String)dKv.getValue());
        	groups.add(gMap); 
        	
        	String subJson=(String) dKv.getSub();
        	if(subJson!=null&&subJson.length()>0){		//解析sub
        		try {
        			ArrayList<KeyValue> subKvs=new ArrayList<KeyValue>();
        			ArrayList<Map<String, String>> child = new ArrayList<Map<String, String>>();
            		JSONArray subArr = new JSONArray(subJson);
            		if(subArr!=null&&subArr.length()>0){
            			for(int j=0;j<subArr.length();j++){
            				JSONObject oJsonObject = subArr.optJSONObject(j);
            				KeyValue subKv=new KeyValue(oJsonObject.optString("key"),oJsonObject.optString("value"));
            				subKvs.add(subKv);
            				
            				Map<String, String> cMap = new HashMap<String, String>();  
            				cMap.put(DropDownExpandableSpinner.CHILD_KEY, oJsonObject.optString("value"));
         	                child.add(cMap);
            			}
            			childKvList.add(subKvs);
            			childs.add(child);
            		}
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
        	}else{
        		childKvList.add(null);
        		childs.add(null);
        	}
        }
        setAdapterData(groups,childs);
    }

    public void setAdapterData(ArrayList<Map<String,String>> gList,ArrayList<ArrayList<Map<String,String>>> cLists){
        if (gList==null||gList.size()<=0||cLists==null||cLists.size()<=0){
            return;
        }
        if (groupMvList!=null){
            groupMvList.clear();
        }
        if (childMvLists!=null){
            for (ArrayList<Map<String,String>> kv:childMvLists){
                kv.clear();
            }
            childMvLists.clear();
        }
        groupMvList=gList;
        childMvLists=cLists;
       
        listAdapter=new SimpleExpandableListAdapter(context,groupMvList, R.layout.item_expandable_list_group,new String[]{GROUP_KEY},new int[]{R.id.item_group_tx},childMvLists,R.layout.item_expandable_list_child,new String[]{CHILD_KEY},new int[]{R.id.item_child_tx});
        
        listView = new ExpandableListView(context);
        listView.setAdapter(listAdapter);
        listView.setBackgroundResource(R.drawable.bg_autotextview);
        listView.setDividerHeight(1);
        listView.setDivider(context.getResources().getDrawable(R.color.darkgreen));

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (childMvLists.get(groupPosition)!=null&&childMvLists.get(groupPosition).size() > 0) {
                    return false;
                } else {
                    String tx = (String) groupMvList.get(groupPosition).get(GROUP_KEY);
                    setText(tx);
                    if (selectedChangedListener != null&&groupKvList!=null) {
                        selectedChangedListener.onSelectedChanged(DropDownExpandableSpinner.this, groupKvList.get(groupPosition).getKey());
                    }
                    popup.dismiss();
                    return true;
                }
            }
        });
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String tx = (String) childMvLists.get(groupPosition).get(childPosition).get(CHILD_KEY);
                setText(tx);
                if (selectedChangedListener != null&&childKvList!=null) {
                    selectedChangedListener.onSelectedChanged(DropDownExpandableSpinner.this, childKvList.get(groupPosition).get(childPosition).getKey());     //key
                }
                popup.dismiss();
                return true;
            }
        });
    }

    private void initView(Context c) {
        context = c;
        setEllipsize(TruncateAt.END);
        setOnClickListener(clickListener);
        popupHeight = (int) (120 * context.getResources().getDisplayMetrics().density);
        spacing = (int) (8 * context.getResources().getDisplayMetrics().density);
    }

    OnClickListener clickListener = new OnClickListener() {

        public void onClick(View v) {
        	Log.i("ydzf", "Expandable click");
            showPopup();
        }
    };

    void showPopup() {
        if (listView==null){
            return;
        }
        popup = new PopupWindow(listView, getMeasuredWidth(), popupHeight);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        if (popup != null) {
            if (!popup.isShowing()) {
                popup.showAsDropDown(this);
            } else {
                popup.dismiss();
            }
        }
    }

    class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

        ArrayList<Map<String,String>> gKvList;
        ArrayList<ArrayList<Map<String,String>>> cKvLists;
        Context c;
        
        public MyExpandableListViewAdapter(Context c,ArrayList<Map<String,String>> gKvList,ArrayList<ArrayList<Map<String,String>>> cKvLists){
        	this.c=c;
        	this.gKvList=gKvList;
        	this.cKvLists=cKvLists;
        }
    	
        //返回一级列表的个数
        @Override
        public int getGroupCount() {
            return gKvList.size();
        }

        //返回每个二级列表的个数
        @Override
        public int getChildrenCount(int groupPosition) { //参数groupPosition表示第几个一级列表
            return cKvLists.get(groupPosition).size();
        }

        //返回一级列表的单个item（返回的是对象）
        @Override
        public Object getGroup(int groupPosition) {
            return gKvList.get(groupPosition);
        }

        //返回二级列表中的单个item（返回的是对象）
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return cKvLists.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //每个item的id是否是固定？一般为true
        @Override
        public boolean hasStableIds() {
            return true;
        }

        //【重要】填充一级列表
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_list_group, null);
            }
            TextView tv_group = (TextView) convertView.findViewById(R.id.item_group_title);
            tv_group.setText((CharSequence) groupMvList.get(groupPosition).get(GROUP_KEY));
            convertView.setTag(groupMvList.get(groupPosition).get(GROUP_KEY));
            return convertView;
        }

        //【重要】填充二级列表
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_list_child, null);
            }
            TextView tv_child = (TextView) convertView.findViewById(R.id.item_child_title);
            tv_child.setText((CharSequence) childMvLists.get(groupPosition).get(childPosition).get(CHILD_KEY));
            convertView.setTag(childMvLists.get(groupPosition).get(childPosition).get(CHILD_KEY));
            return convertView;
        }

        //二级列表中的item是否能够被选中？可以改为true
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
