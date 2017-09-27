package com.chinadci.mel.mleo.ui.fragments.data.model;

public class Patrols_TH {
	
	private String id;
	private String sqry;		//申请人员
	private String sqsj;		//申请时间
	private String sqyy;		//申请原因
	private String thry;		//处理人员
	private String thsj;		//处理时间
	private String thyy;		//处理说明
	public String getId() {
		return id;
	}
	public String getSqry() {
		return sqry;
	}
	public void setSqry(String sqry) {
		this.sqry = sqry;
	}
	public String getSqsj() {
		return sqsj;
	}
	public void setSqsj(String sqsj) {
		this.sqsj = sqsj;
	}
	public String getSqyy() {
		return sqyy;
	}
	public void setSqyy(String sqyy) {
		this.sqyy = sqyy;
	}
	public String getThry() {
		return thry;
	}
	public void setThry(String thry) {
		this.thry = thry;
	}
	public String getThsj() {
		return thsj;
	}
	public void setThsj(String thsj) {
		this.thsj = thsj;
	}
	public String getThyy() {
		return thyy;
	}
	public void setThyy(String thyy) {
		this.thyy = thyy;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String toString(){
		return "id="+id+",sqry="+sqry+",sqsj="+sqsj+",sqyy="+sqyy+",thry="+thry+",thsj="+thsj+",thyy="+thyy;
	}

}
