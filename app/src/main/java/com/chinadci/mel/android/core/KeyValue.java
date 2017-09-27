package com.chinadci.mel.android.core;

/**
 * 
 * @ClassName KeyValue
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:29:08
 * 
 */
public class KeyValue {
	Object key;
	Object value;

	public KeyValue(Object k, Object v) {
		// TODO Auto-generated constructor stub
		key = k;
		value = v;
	}

	public Object getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}
	
	public String toString(){
		return "key="+key+",value="+value;
	}
}
