package com.chinadci.mel.mleo.ldb;
/**
 * 
* @ClassName LocalAnnexTable 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:44:15 
*
 */
public interface LocalAnnexTable extends AnnexTable {
	public final String name = "LOCAL_ANNEX";
	public final String field_status = "status";// 0表示没有发送,1表示已经发送
}
