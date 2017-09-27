package com.chinadci.mel.mleo.ui.fragments.data.model;

public class SimpleAj_Wpzf {
	private String id;
	private String bh;
	private String xxdz;
	private String sj;
	private String ajly;
	private String hx;
	private String qybm;		//add teng.guo 20170626
	private double x;
	private double y;
	private String hxfxjg;
	
	private String jcbh;
	
	private String isSave;
	
	private String ajKey;
	
	private int wpWorker;
	
	private String isApprover;
	
	private String isRevoke;
	
	private String lastState;
	
	public String getLastState() {
		return lastState;
	}
	public void setLastState(String lastState) {
		this.lastState = lastState;
	}
	public boolean isChecker() {
		return wpWorker==1?true:false;
	}
	public void setChecker(int wpWorker) {
		this.wpWorker = wpWorker;
	}
	public String isApprover() {
		return isApprover;
	}
	public void setApprover(String isApprover) {
		this.isApprover = isApprover;
	}
	public String isRevoke() {
		return isRevoke;
	}
	public void setRevoke(String isRevoke) {
		this.isRevoke = isRevoke;
	}
	public String getAjKey() {
		return ajKey;
	}
	public void setAjKey(String ajKey) {
		this.ajKey = ajKey;
	}
	
	public String getIsSave() {
		return isSave;
	}

	public void setIsSave(String isSave) {
		this.isSave = isSave;
	}

	public String getHxfxjg() {
		return hxfxjg;
	}

	public void setHxfxjg(String hxfxjg) {
		this.hxfxjg = hxfxjg;
	}

	public String getJcbh() {
		return jcbh;
	}

	public void setJcbh(String jcbh) {
		this.jcbh = jcbh;
	}

	public String getHx() {
		return hx;
	}

	public void setHx(String hx) {
		this.hx = hx;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getXxdz() {
		return xxdz;
	}

	public void setXxdz(String xxdz) {
		this.xxdz = xxdz;
	}

	public String getSj() {
		return sj;
	}

	public void setSj(String sj) {
		this.sj = sj;
	}

	public String getAjly() {
		return ajly;
	}

	public void setAjly(String ajly) {
		this.ajly = ajly;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBh() {
		return bh;
	}

	public void setBh(String bh) {
		this.bh = bh;
	}
	public String getQybm() {
		return qybm;
	}
	public void setQybm(String qybm) {
		this.qybm = qybm;
	}
}
