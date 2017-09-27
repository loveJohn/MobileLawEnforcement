package com.chinadci.mel.mleo.ui.fragments.data.model;

public class SimpleAj_Wpzf_find {
	private String quid;
	private SimpleAj_Wpzf aj;
	public String getQuid() {
		return quid;
	}
	public void setQuid(String quid) {
		this.quid = quid;
	}
	public SimpleAj_Wpzf getAj() {
		return aj;
	}
	public void setAj(SimpleAj_Wpzf aj) {
		this.aj = aj;
	}
	public SimpleAj_Wpzf_find(String quid, SimpleAj_Wpzf aj) {
		super();
		this.quid = quid;
		this.aj = aj;
	}
}
