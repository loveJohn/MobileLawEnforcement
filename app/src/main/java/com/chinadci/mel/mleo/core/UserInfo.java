package com.chinadci.mel.mleo.core;

import android.graphics.Bitmap;

/**
 * 
 * @ClassName UserInfo
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:30:37
 * 
 */
public class UserInfo {
	String userId = "";
	String name = "";
	String chName = "";
	String tel = "";

	String role = "";
	String rzcode = "";
	String rzname = "";

	int sex;
	Bitmap photoBitmap;

	public void setAdminName(String s) {
		rzname = s;
	}

	public String getAdminName() {
		return rzname;
	}

	public void setSex(int s) {
		sex = s;
	}

	public void setUserId(String str) {
		userId = str;
	}

	public void setRole(String str) {
		role = str;
	}

	public void setTerritory(String str) {
		rzcode = str;

	}

	public void setName(String str) {
		name = str;
	}

	public void setChName(String str) {
		chName = str;
	}

	public void setTel(String str) {
		tel = str;
	}

	public void setPhoto(Bitmap bm) {
		photoBitmap = bm;
	}

	public String getName() {
		return name;
	}

	public String getChName() {
		return chName;
	}

	public String getTel() {
		return tel;
	}

	public Bitmap getPhoto() {
		return photoBitmap;
	}

	public String getUserId() {
		return userId;
	}

	public String getRole() {
		return role;
	}

	public String getTerritory() {
		return rzcode;
	}

	public int getSex() {
		return sex;
	}
}
