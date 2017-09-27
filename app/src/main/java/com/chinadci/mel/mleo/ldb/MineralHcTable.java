package com.chinadci.mel.mleo.ldb;

public interface MineralHcTable {
	public final String name = "MIL_HC";
	public final String field_id = "id";// 编号
	public final String field_caseId = "caseId";// 编号
	public final String field_hcsj="hcsj";// 核查时间，形如2014-05-01hcsj
	public final String field_hcrmc="hcrmc";// 核查人员名称
	public final String field_sfffckd="sfffckd";// 是否非法采矿点，0：否，1：是 
	public final String field_wfztxz="wfztxz";// 违法主体性质，1：个人，2：企业；若无则为空 
	public final String field_wfztmc="wfztmc";// 违法主体名称 
	public final String field_fkckz="fkckz";// 非法开采矿种代码 
	public final String field_ffkcfs="ffkcfs";// 非法开采方式代码 
	public final String field_sftzffkc="sftzffkc";// 是否停止非法开采，0：否，1：是
	public final String field_hccomment = "hccomment";// 备注
	public final String field_status = "status";
	public final String field_sfljqd="sfljqd";//是否立即取缔
}
