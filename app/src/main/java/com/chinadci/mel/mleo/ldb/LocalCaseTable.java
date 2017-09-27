package com.chinadci.mel.mleo.ldb;
/**
 * 
* @ClassName LocalCaseTable 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:44:18 
*
 */
public interface LocalCaseTable extends CaseTable {
	public final String field_admin = "admin";//所在村居
	public final String field_status = "status";
	public final String field_x = "x";
	public final String field_y = "y";
	public final String name = "LOCAL_CASE";
}
