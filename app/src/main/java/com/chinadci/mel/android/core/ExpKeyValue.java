package com.chinadci.mel.android.core;

//二级KeyValue
public class ExpKeyValue {		
	Object key;
	Object value;
	Object sub;

	public ExpKeyValue(Object k, Object v,Object s) {
		// TODO Auto-generated constructor stub
		key = k;
		value = v;
		sub=s;
	}

	public Object getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}
	
	public Object getSub(){
		return sub;
	}
}
