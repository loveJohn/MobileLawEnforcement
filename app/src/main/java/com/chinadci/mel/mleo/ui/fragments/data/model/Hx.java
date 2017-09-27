package com.chinadci.mel.mleo.ui.fragments.data.model;

public class Hx {
	private String hx;
	private String name;
	
	private String ajKey;
	
	public String getAjKey() {
		return ajKey;
	}

	public void setAjKey(String ajKey) {
		this.ajKey = ajKey;
	}

	public String getHx() {
		return hx;
	}

	public void setHx(String hx) {
		this.hx = hx;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Hx(String hx, String name,String ajKey) {
		super();
		this.hx = hx;
		this.name = name;
		this.ajKey = ajKey;
	}

}
