package com.chinadci.mel.mleo.ui.fragments.data.model;

public class InspectionGetTask {
	private String bh;
	private String redline;
	private String x;
	private String y;
	private String ajly;
	private String dz;
	private String entiryString;
	public String getBh() {
		return bh;
	}
	public void setBh(String bh) {
		this.bh = bh;
	}
	public String getRedline() {
		return redline;
	}
	public void setRedline(String redline) {
		this.redline = redline;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getAjly() {
		return ajly;
	}
	public void setAjly(String ajly) {
		this.ajly = ajly;
	}
	public String getDz() {
		return dz;
	}
	public void setDz(String dz) {
		this.dz = dz;
	}
	public String getEntiryString() {
		return entiryString;
	}
	public void setEntiryString(String entiryString) {
		this.entiryString = entiryString;
	}
	public InspectionGetTask(String bh, String redline, String x, String y,
			String ajly, String dz, String entiryString) {
		super();
		this.bh = bh;
		this.redline = redline;
		this.x = x;
		this.y = y;
		this.ajly = ajly;
		this.dz = dz;
		this.entiryString = entiryString;
	}
}
