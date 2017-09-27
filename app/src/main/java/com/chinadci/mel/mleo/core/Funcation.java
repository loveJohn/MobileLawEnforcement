package com.chinadci.mel.mleo.core;

public class Funcation {
	int id;
	int icon;
	int label;
	String command;

	boolean module = false;
	int moduleId = -1;

	public Funcation(int id, int icon, int label, String command) {
		this.id = id;
		this.icon = icon;
		this.label = label;
		this.command = command;
	}

	public boolean hasModule() {
		return this.module;
	}

	public void setModule(int moduleId) {
		if (moduleId > -1) {
			module = true;
			this.moduleId = moduleId;
		}
	}

	public int getId() {
		return this.id;
	}

	public int getIconRes() {
		return this.icon;
	}

	public int getLabelRes() {
		return this.label;
	}

	public String getCommand() {
		return this.command;
	}

	public int getModule() {
		return this.moduleId;
	}
}
