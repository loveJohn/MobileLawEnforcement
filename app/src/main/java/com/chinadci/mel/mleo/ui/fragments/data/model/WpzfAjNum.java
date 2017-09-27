package com.chinadci.mel.mleo.ui.fragments.data.model;

public class WpzfAjNum {
	private String KEY;
	private String BL_ZT;
	private int COUNT;
	private String xzqh;
	public String getKEY() {
		return KEY;
	}
	public void setKEY(String kEY) {
		KEY = kEY;
	}
	public String getBL_ZT() {
		return BL_ZT;
	}
	public void setBL_ZT(String bL_ZT) {
		BL_ZT = bL_ZT;
	}
	public int getCOUNT() {
		return COUNT;
	}
	public void setCOUNT(int cOUNT) {
		COUNT = cOUNT;
	}
	public String getXzqh() {
		return xzqh;
	}
	public void setXzqh(String xzqh) {
		this.xzqh = xzqh;
	}
	public WpzfAjNum(String kEY, String bL_ZT, int cOUNT, String xzqh) {
		super();
		KEY = kEY;
		BL_ZT = bL_ZT;
		COUNT = cOUNT;
		this.xzqh = xzqh;
	}
	
}
