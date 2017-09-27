package com.chinadci.mel.mleo.core;


public class FuncationGroup {
	private static final long serialVersionUID = 1L;
	String groupName;
	int funcationIds[];

	public FuncationGroup(String name, int funcationIds[]) {
		this.groupName = name;
		this.funcationIds = funcationIds;
	}

	public String getGroupName() {
		return groupName;
	}

	public int[] getFuncationIds() {
		return this.funcationIds;
	}

	public int getFuncationId(int index) {
		return this.funcationIds[index];
	}
}
