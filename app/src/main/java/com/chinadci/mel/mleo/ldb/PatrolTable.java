package com.chinadci.mel.mleo.ldb;

/**
 * 
 * @ClassName PatrolTable
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:44:38
 * 
 */
public interface PatrolTable {
	public final String field_id = "id";// 编号
	public final String field_caseId = "caseId";// 案件编号
	//public final String field_title="title";		//没有该字段，重构工程时要加上。自带title,不要for循环给patrol添加自定义【第X次处理】标题。
	public final String field_user = "user";// 用户
	public final String field_redline = "redline";// 红线
	public final String field_mTime = "mTime";// 时间

	public final String field_stopNotice = "stopNotice";// 制止通知书
	public final String field_stopInfo = "stopInfo";// 制止情况
	public final String field_pullPlan = "pullPlan";// 拆除计划
	public final String field_pullInfo = "pullInfo";// 拆除情况
	public final String field_regCase = "regCase";// 立案查处
	public final String field_deal = "deal";// 处理情况
	public final String field_notes = "notes";// 备注

	public final String field_stopNoticeNo = "stopNoticeNo";// 制止通知书编号
	public final String field_stopNoticeDate = "stopNoticeDate";// 制止通知书下发时间
	public final String field_pullPlanDate = "pullPlanDate";// 计划拆除时间
	public final String field_pullPlanNum = "pullPlanNum";// 拆除组织人数
	public final String field_pullPlanPerson = "pullPlanPerson";// 拆除负责人
	public final String field_caseDocumentNo = "caseDocumentNo";// 立案决定书文号
	public final String field_caseDocumentDate = "caseDocumentDate";// 立案决定书发文时间
	public final String field_govDate = "govDate";// 报告政府时间
	public final String field_sendDate="sendDate";

}
