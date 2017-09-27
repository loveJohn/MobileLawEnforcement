package com.chinadci.mel.mleo.ldb;

public interface MilPatrolBaseTable {
	public final String field_id = "id";
	public final String field_ajzt="ajzt";//案件状态
	public final String field_wfztmc="wfztmc";//违法主体名称
	public final String field_user = "user";
	public final String field_line = "line";
	public final String field_szcj = "szcj";//所在村居
	public final String field_hasMining = "hasMining"; //是否非法采矿
	public final String field_exception = "exception";
	public final String field_notes = "notes";  //备注
	public final String field_location = "location";
	public final String field_redline = "redline";
	public final String field_logTime = "logTime";  //添加时间
	public final String field_status = "status";
	public final String field_ffckbh = "ffckbh";  
	public final String field_zzwsbh = "zzwsbh";  
	public final String field_haszz = "haszz"; //是否制止
	public final String field_xcrzxl = "xcrzxl";//巡查日志类型
}
