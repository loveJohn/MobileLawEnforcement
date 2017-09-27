package com.chinadci.mel.mleo.ldb;
/**
 * 
* @ClassName CaseTable 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:43:25 
*
 */
public interface CaseTable {
	public final String field_id = "id";// 编号
	public final String field_user = "user";// 用户
	public final String field_parties = "parties";// 当事人
	public final String field_illegalSubject="illegalSubject";//违法主体
	public final String field_source = "source";// 线索来源
	public final String field_projType = "projType";// 项目类型
	public final String field_illegalType = "illegalType";// 违法类型
	public final String field_illegalStatus = "illegalStatus";// 违法状态
	public final String field_illegalArea = "illegalArea";// 违法用地面积
	public final String field_landUsage = "landUsage";// 初判地类
	public final String field_address = "address";// 详细地址
	public final String field_notes = "notes";// 备注
	public final String field_redline = "redline";// 红线
	public final String field_mTime = "mTime";// 时间
	public final String field_analysis = "analysis";//一张图分析结果
}
