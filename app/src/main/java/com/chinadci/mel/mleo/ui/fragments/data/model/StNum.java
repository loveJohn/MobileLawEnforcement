package com.chinadci.mel.mleo.ui.fragments.data.model;

public class StNum {
	private String id;
	private String name;
	private int num;
	private String quid;
	private boolean isShowDetails = false;
	public String getQuid() {
		return quid;
	}
	public void setQuid(String quid) {
		this.quid = quid;
	}
	public boolean isShowDetails() {
		return isShowDetails;
	}
	public void setShowDetails(boolean isShowDetails) {
		this.isShowDetails = isShowDetails;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public StNum(String id,String name, int num,boolean isShowDetails,String quid) {
		super();
		this.id = id;
		this.name = name;
		this.num = num;
		this.isShowDetails = isShowDetails;
		this.quid = quid;
	}
}
