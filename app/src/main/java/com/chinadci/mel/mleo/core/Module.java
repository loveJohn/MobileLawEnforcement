package com.chinadci.mel.mleo.core;


public class Module {
	int id;
	int title;
	String content;
	String tool;

	public Module(int id, int titleRes, String content, String tool) {
		this.id = id;
		this.title = titleRes;
		this.content = content;
		this.tool = tool;
	}

	public int getId() {
		return this.id;
	}

	public int getTitleRes() {
		return this.title;
	}

	public String getContent() {
		return this.content;
	}

	public String getTool() {
		return this.tool;
	}
}
