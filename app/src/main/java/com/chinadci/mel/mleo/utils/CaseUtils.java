package com.chinadci.mel.mleo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ldb.AnnexTable;
import com.chinadci.mel.mleo.ldb.CaseSituationTable;
import com.chinadci.mel.mleo.ldb.CaseTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectTable;
import com.chinadci.mel.mleo.ldb.PatrolTable;
import com.chinadci.mel.mleo.ldb.TrackCaseTable;
import com.chinadci.mel.mleo.ldb.WebAnnexTable;
import com.chinadci.mel.mleo.ldb.WebCaseTable;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.PatrolsType;
import com.chinadci.mel.mleo.ui.fragments.data.model.Patrols_CH;
import com.chinadci.mel.mleo.ui.fragments.data.model.Patrols_SP;
import com.chinadci.mel.mleo.ui.fragments.data.model.Patrols_TH;

/**
 */
public class CaseUtils {
	private static Lock instanceLock = new ReentrantLock();
	private volatile static CaseUtils instance;

	/**
	 * 
	 * @Title: getInstance
	 * @return TeachingUtils
	 */
	public static CaseUtils getInstance() {
		if (instance == null) {
			instanceLock.lock();
			try {
				instance = new CaseUtils();
			} catch (Exception e) {
			} finally {
				instanceLock.unlock();
			}
		}
		return instance;
	}

	//————————————————————————————
	//保留矿产用
	public String storeCaseFulldata(Context context, String inUser, int status,
			JSONObject obj, String caseTable, String annexTable,
			String patrolTable, String inspectTable) throws Exception {

		try {
			// 保存案件信息
			JSONObject caseJsonObject = obj.getJSONObject("case");
			String caseId = storeCaseData2(context, inUser, status,
					caseJsonObject, caseTable, annexTable);

			// 保存核查信息
			try {
				JSONObject inspectionObject = obj.getJSONObject("inspection");
				if (inspectionObject != null)
					storeInspectData(context, inspectionObject, caseId,
							inspectTable);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// 保存处理结果信息
			try {
				JSONArray patrolJsonArray = obj.getJSONArray("patrols");
				if (patrolJsonArray != null && patrolJsonArray.length() > 0) {
					for (int i = 0; i < patrolJsonArray.length(); i++) {
						storePatrolData2(context, caseId,
								patrolJsonArray.getJSONObject(i), patrolTable,
								annexTable);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}
	
	//保留矿产用
	public String storeCaseData2(Context context, String inUser, int status,
			JSONObject obj, String caseTable, String annexTable)
			throws Exception {
		try {
			ContentValues caseBaseValues = caseJson2ContentValues(obj);
			String caseId = caseBaseValues.getAsString(WebCaseTable.field_id);

			// 此案件已在本地缓存过的附件
			ArrayList<ContentValues> annexValues = DBHelper
					.getDbHelper(context).doQuery(
							annexTable,
							new String[] { AnnexTable.field_path },
							new StringBuffer(AnnexTable.field_tag)
									.append("=? and ")
									.append(AnnexTable.field_tagId)
									.append("=?").toString(),
							new String[] { caseTable, caseId }, null, null,
							null, null);
			if (annexValues != null && annexValues.size() > 0) {
				for (ContentValues annex : annexValues) {
					String filePath = annex.getAsString(AnnexTable.field_path);
					File file = new File(filePath);
					if (file != null && file.exists())
						file.delete();
				}
			}
			int delAnnexCount = DBHelper.getDbHelper(context).delete(
					annexTable,
					new StringBuffer(AnnexTable.field_tag).append("=? and ")
							.append(AnnexTable.field_tagId).append("=?")
							.toString(), new String[] { caseTable, caseId });

			// 删除案件已在本地的缓存
			int delCaseCount = DBHelper.getDbHelper(context).delete(
					caseTable,
					new StringBuffer(CaseTable.field_id).append("=?")
							.toString(), new String[] { caseId });

			// 保存案件基础信息
			caseBaseValues.put(WebCaseTable.field_inUser, inUser);
			caseBaseValues.put(TrackCaseTable.field_status, status);
			DBHelper.getDbHelper(context).insert(caseTable, caseBaseValues);

			// 保存案件的附件
			try {
				JSONArray annexesJson = obj.getJSONArray("annexes");
				if (annexesJson != null && annexesJson.length() > 0)
					storeAnnex(context, annexesJson, annexTable, caseId,
							caseTable, caseId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}
	
	//保留矿产用
	public String storePatrolData2(Context context, String caseId,
			JSONObject patrolJson, String patrolTable, String annexTable)
			throws Exception {
		try {
			ContentValues patrolValues = patrolJson2ContentValues(patrolJson);
			String patrolId = patrolValues.getAsString(PatrolTable.field_id);

			// 此处理结果已在本地缓存过的附件
			ArrayList<ContentValues> annexValues = DBHelper
					.getDbHelper(context).doQuery(
							annexTable,
							new String[] { AnnexTable.field_path },
							new StringBuffer(AnnexTable.field_tag)
									.append("=? and ")
									.append(AnnexTable.field_tagId)
									.append("=?").toString(),
							new String[] { patrolTable, patrolId }, null, null,
							null, null);
			if (annexValues != null && annexValues.size() > 0) {
				for (ContentValues annex : annexValues) {
					String filePath = annex.getAsString(AnnexTable.field_path);
					File file = new File(filePath);
					if (file != null && file.exists())
						file.delete();
				}
			}
			int delAnnexCount = DBHelper.getDbHelper(context)
					.delete(annexTable,
							new StringBuffer(AnnexTable.field_tag)
									.append("=? and ")
									.append(AnnexTable.field_tagId)
									.append("=?").toString(),
							new String[] { patrolTable, patrolId });

			// 删除处理结果已在本地的缓存
			int delPatrolCount = DBHelper.getDbHelper(context).delete(
					patrolTable,
					new StringBuffer(PatrolTable.field_id).append("=?")
							.toString(), new String[] { patrolId });

			// 保存处理情况数据到缓存表
			patrolValues.put(PatrolTable.field_caseId, caseId);
			DBHelper.getDbHelper(context).insert(patrolTable, patrolValues);

			// 保存处理结果附件
			try {
				JSONArray annexes = patrolJson.getJSONArray("annexes");
				if (annexes != null && annexes.length() > 0)
					storeAnnex(context, annexes, annexTable, caseId,
							patrolTable, patrolId);
			} catch (Exception e) {
				throw e;
			}

			return patrolId;
		} catch (Exception e) {
			throw e;
		}
	}
	//————————————————————————————
	
	public String storeCaseFulldata2(Context context, String inUser, int status,
			JSONObject obj, String caseTable, String annexTable,
			String patrolTable, String inspectTable) throws Exception {

		try {
			// 保存案件信息
			JSONObject caseJsonObject = obj.getJSONObject("case");
			String caseId = storeCaseData(context, inUser, status,
					caseJsonObject, caseTable, annexTable);

			// 保存核查信息
			try {
				JSONObject inspectionObject = obj.getJSONObject("inspection");
				if (inspectionObject != null)
					storeInspectData(context, inspectionObject, caseId,
							inspectTable);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// 保存处理结果信息
			try {
				JSONArray patrolJsonArray = obj.getJSONArray("patrols");
				if (patrolJsonArray != null && patrolJsonArray.length() > 0) {
					for (int i = 0; i < patrolJsonArray.length(); i++) {
						storePatrolData(context, caseId,
								patrolJsonArray.getJSONObject(i), patrolTable,
								annexTable);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}

	public String storeCaseFulldata(Context context, String inUser,
			JSONObject obj, String caseTable, String annexTable,
			String patrolTable, String inspectTable) throws Exception {
		try {
			// 保存案件信息
			JSONObject caseJsonObject = obj.getJSONObject("case");
			String caseId = storeCaseData(context, inUser, caseJsonObject,
					caseTable, annexTable);

			// 保存核查信息
			try {
				JSONObject inspectionObject = obj.getJSONObject("inspection");
				if (inspectionObject != null)
					storeInspectData(context, inspectionObject, caseId,
							inspectTable);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// 保存处理结果信息
			try {
				JSONArray patrolJsonArray = obj.getJSONArray("patrols");
				Log.i("ydzf", "patrolJsonArray_size="+patrolJsonArray.length());
				if (patrolJsonArray != null && patrolJsonArray.length() > 0) {
					for (int i = 0; i < patrolJsonArray.length(); i++) {
						try{
							storePatrolData(context, caseId,
									patrolJsonArray.getJSONObject(i), patrolTable,
									annexTable);
						}catch(Exception e){
							Log.i("ydzf", "patrolJsonArray_for_exception="+i);		//patrol中存在空的object，需要跳过去
							continue;
						}
					}
				}
			} catch (Exception e) {
				Log.i("ydzf", "patrolJsonArray save exception="+e.getMessage());
				e.printStackTrace();
			}

			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String storeCaseFulldata_Wpzf(Context context, String inUser,
			JSONObject obj, String caseTable, String annexTable,
			String patrolTable, String inspectTable) throws Exception {
		try {
			// 保存案件信息
			JSONObject caseJsonObject = obj.getJSONObject("case");
			String caseId = storeCaseData(context, inUser, caseJsonObject,
					caseTable, annexTable);

			// 保存核查信息
			try {
				JSONObject inspectionObject = obj.getJSONObject("inspection");
				int status=2;
				try {
					if(obj.has("canEdit")&&obj.optBoolean("canEdit")){
						status = 1;	
						//2017 04 20
						DbUtil.deleteINSPECTIONEDITDbDatasByBh(context, caseId);
						String keyIllegalSubject = obj.optJSONObject("inspection").optJSONObject("illegalSubject").optString("key");
						String parties = obj.optJSONObject("inspection").optString("parties");
						String tel = obj.optJSONObject("inspection").optString("tel");
						String keyIllegalType = obj.optJSONObject("inspection").optJSONObject("illegalType").optString("key");
						String keyIllegalStatus = obj.optJSONObject("inspection").optJSONObject("illegalStatus").optString("key");
						String mj =obj.optJSONObject("inspection").optString("illegalArea") ;
						if(TextUtils.isEmpty(mj)){
							mj = "";
						}
						String keyLandUsage = obj.optJSONObject("inspection").optJSONObject("landUsage").optString("key") ;
						String notes = obj.optJSONObject("inspection").optString("survey");
						String user = obj.optJSONObject("inspection").optJSONObject("user").optString("name");
						String ddte = obj.optJSONObject("inspection").optString("patrolTime");
						DbUtil.insertINSPECTIONEDITDbDatas(context, caseId,
								keyIllegalSubject, parties, tel, keyIllegalType,
								keyIllegalStatus, mj, keyLandUsage, notes, user, ddte);
						if(obj.optJSONObject("inspection").has("jsdt")){
							DbUtil.deleteINSPECTIONEDITJSDTDbDatasByBh(context, caseId);
							DbUtil.insertINSPECTIONEDITJSDTDbDatas(context, caseId,  obj.optJSONObject("inspection").optJSONObject("jsdt").optString("key"), obj.optJSONObject("inspection").optJSONObject("jsdt").optString("value"));
						}
					}
				} catch (Exception e) {
				}
				if (inspectionObject != null)
					storeInspectData_wpzf(context, inspectionObject, caseId,
							inspectTable,status);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// 保存处理结果信息
			try {
				JSONArray patrolJsonArray = obj.getJSONArray("patrols");
				if (patrolJsonArray != null && patrolJsonArray.length() > 0) {
					for (int i = 0; i < patrolJsonArray.length(); i++) {
						try{
							JSONObject objjj = patrolJsonArray.getJSONObject(i);
							String id = objjj.optString("id");
							if(objjj.has("annexes")){		//附件信息
								storePatrolData(context, caseId,
										objjj, patrolTable,
										annexTable);
								DbUtil.deletePatrolsTypeById(context, id);
								PatrolsType pType = new PatrolsType();
								pType.setId(id);
								pType.setTitle(objjj.optString("title"));
								pType.setType("1");
								DbUtil.insertPatrolsType(context, pType);
							}else if(objjj.has("spjg")){	//审批结果
								JSONObject opopop = objjj.getJSONObject("spjg");
								DbUtil.deletePatrolsTypeById(context, id+"_jg");
								PatrolsType pType = new PatrolsType();
								pType.setId(id+"_jg");
								pType.setTitle("审核结果");
								pType.setType("2");
								DbUtil.insertPatrolsType(context, pType);
								DbUtil.deletePatrolsSPById(context, id+"_jg");
								Patrols_SP sPatrols_SP = new Patrols_SP();
								sPatrols_SP.setId(id+"_jg");
								sPatrols_SP.setSpry(opopop.optString("spry"));
								sPatrols_SP.setSpsj(opopop.optString("spsj"));
								sPatrols_SP.setSpsm(opopop.optString("spsm"));
								DbUtil.insertPatrolsSP(context, sPatrols_SP);
							}else if(objjj.has("chyy")){		//撤回处理
								DbUtil.deletePatrolsTypeById(context, id+"_ch");
								PatrolsType pType = new PatrolsType();
								pType.setId(id+"_ch");
								pType.setTitle("撤回说明");
								pType.setType("3");
								DbUtil.insertPatrolsType(context, pType);
								DbUtil.deletePatrolsCHById(context, id+"_ch");
								Patrols_CH sPatrols_CH = new Patrols_CH();
								sPatrols_CH.setId(id+"_ch");
								sPatrols_CH.setChry(objjj.optString("chry"));
								sPatrols_CH.setChsj(objjj.optString("chsj"));
								sPatrols_CH.setChyy(objjj.optString("chyy"));
								DbUtil.insertPatrolsCH(context, sPatrols_CH);
							}else if(objjj.has("sqxx")){		//退回处理		//modify teng.guo 20170719
								DbUtil.deletePatrolsTypeById(context, id+"_th");
								PatrolsType pType = new PatrolsType();
								pType.setId(id+"_th");
								pType.setTitle("申请退回");
								pType.setType("1");
								DbUtil.insertPatrolsType(context, pType);
								
								DbUtil.deletePatrolsTHById(context, id+"_th");
								Patrols_TH sPatrols_TH = new Patrols_TH();
								sPatrols_TH.setId(id+"_th");
								
								JSONObject sqxxObj=objjj.getJSONObject("sqxx");
								if(sqxxObj.has("sqry")){
									sPatrols_TH.setSqry(sqxxObj.optString("sqry"));
								}
								if(sqxxObj.has("sqsj")){
									sPatrols_TH.setSqsj(sqxxObj.optString("sqsj"));
								}
								if(sqxxObj.has("sqyy")){
									sPatrols_TH.setSqyy(sqxxObj.optString("sqyy"));
								}
								if(objjj.has("cljg")){
									JSONObject cljgObj=objjj.getJSONObject("cljg");
									if(cljgObj.has("clry")){
										sPatrols_TH.setThry(cljgObj.optString("clry"));
									}
									if(cljgObj.has("clsj")){
										sPatrols_TH.setThsj(cljgObj.optString("clsj"));
									}
									if(cljgObj.has("clsm")){
										sPatrols_TH.setThyy(cljgObj.optString("clsm"));
									}
								}
								DbUtil.insertPatrolsTH(context, sPatrols_TH);
							}
						}catch(Exception e){
							continue;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("ydzf", "caseUtils exception="+e.getMessage());
			}

			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}

	public String storeCaseData(Context context, String inUser, int status,
			JSONObject obj, String caseTable, String annexTable)
			throws Exception {
		try {
			ContentValues caseBaseValues = caseJson2ContentValues(obj);
			String caseId = caseBaseValues.getAsString(WebCaseTable.field_id);

			// 此案件已在本地缓存过的附件
			ArrayList<ContentValues> annexValues = DBHelper
					.getDbHelper(context).doQuery(
							annexTable,
							new String[] { AnnexTable.field_path },
							new StringBuffer(AnnexTable.field_tag)
									.append("=? and ")
									.append(AnnexTable.field_tagId)
									.append("=?").toString(),
							new String[] { caseTable, caseId }, null, null,
							null, null);
			if (annexValues != null && annexValues.size() > 0) {
				for (ContentValues annex : annexValues) {
					String filePath = annex.getAsString(AnnexTable.field_path);
					File file = new File(filePath);
					if (file != null && file.exists())
						file.delete();
				}
			}
			int delAnnexCount = DBHelper.getDbHelper(context).delete(
					annexTable,
					new StringBuffer(AnnexTable.field_tag).append("=? and ")
							.append(AnnexTable.field_tagId).append("=?")
							.toString(), new String[] { caseTable, caseId });

			// 删除案件已在本地的缓存
			int delCaseCount = DBHelper.getDbHelper(context).delete(
					caseTable,
					new StringBuffer(CaseTable.field_id).append("=?")
							.toString(), new String[] { caseId });

			// 保存案件基础信息
			caseBaseValues.put(WebCaseTable.field_inUser, inUser);
			caseBaseValues.put(TrackCaseTable.field_status, status);
			DBHelper.getDbHelper(context).insert(caseTable, caseBaseValues);

			// 保存案件的附件
			try {
				JSONArray annexesJson = obj.getJSONArray("annexes");
				if (annexesJson != null && annexesJson.length() > 0)
					storeAnnex2(context, annexesJson, annexTable, caseId,
							caseTable, caseId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @Title storeCaseData
	 * @param context
	 * @param inUser
	 * @param obj
	 * @param caseTable
	 * @param annexTable
	 * @throws Exception
	 *             void
	 */
	public String storeCaseData(Context context, String inUser, JSONObject obj,
			String caseTable, String annexTable) throws Exception {
		try {
			ContentValues caseBaseValues = caseJson2ContentValues(obj);
			String caseId = caseBaseValues.getAsString(WebCaseTable.field_id);

			// 此案件已在本地缓存过的附件
			ArrayList<ContentValues> annexValues = DBHelper
					.getDbHelper(context).doQuery(
							annexTable,
							new String[] { AnnexTable.field_path },
							new StringBuffer(AnnexTable.field_tag)
									.append("=? and ")
									.append(AnnexTable.field_tagId)
									.append("=?").toString(),
							new String[] { caseTable, caseId }, null, null,
							null, null);
			if (annexValues != null && annexValues.size() > 0) {
				for (ContentValues annex : annexValues) {
					String filePath = annex.getAsString(AnnexTable.field_path);
					File file = new File(filePath);
					if (file != null && file.exists())
						file.delete();
				}
			}
			int delAnnexCount = DBHelper.getDbHelper(context).delete(
					annexTable,
					new StringBuffer(AnnexTable.field_tag).append("=? and ")
							.append(AnnexTable.field_tagId).append("=?")
							.toString(), new String[] { caseTable, caseId });

			// 删除案件已在本地的缓存
			int delCaseCount = DBHelper.getDbHelper(context).delete(
					caseTable,
					new StringBuffer(CaseTable.field_id).append("=?")
							.toString(), new String[] { caseId });

			// 保存案件基础信息
			caseBaseValues.put(WebCaseTable.field_inUser, inUser);
			DBHelper.getDbHelper(context).insert(caseTable, caseBaseValues);

			// 保存案件的附件
			try {
				JSONArray annexesJson = obj.getJSONArray("annexes");
				if (annexesJson != null && annexesJson.length() > 0)
					storeAnnex2(context, annexesJson, annexTable, caseId,
							caseTable, caseId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}

	public String storePatrolData(Context context, String caseId,
			JSONObject patrolJson, String patrolTable, String annexTable)
			throws Exception {
		try {
			ContentValues patrolValues = patrolJson2ContentValues(patrolJson);
			String patrolId = patrolValues.getAsString(PatrolTable.field_id);

			// 此处理结果已在本地缓存过的附件
			ArrayList<ContentValues> annexValues = DBHelper
					.getDbHelper(context).doQuery(
							annexTable,
							new String[] { AnnexTable.field_path },
							new StringBuffer(AnnexTable.field_tag)
									.append("=? and ")
									.append(AnnexTable.field_tagId)
									.append("=?").toString(),
							new String[] { patrolTable, patrolId }, null, null,
							null, null);
			if (annexValues != null && annexValues.size() > 0) {
				for (ContentValues annex : annexValues) {
					String filePath = annex.getAsString(AnnexTable.field_path);
					File file = new File(filePath);
					if (file != null && file.exists())
						file.delete();
				}
			}
			int delAnnexCount = DBHelper.getDbHelper(context)
					.delete(annexTable,
							new StringBuffer(AnnexTable.field_tag)
									.append("=? and ")
									.append(AnnexTable.field_tagId)
									.append("=?").toString(),
							new String[] { patrolTable, patrolId });

			// 删除处理结果已在本地的缓存
			int delPatrolCount = DBHelper.getDbHelper(context).delete(
					patrolTable,
					new StringBuffer(PatrolTable.field_id).append("=?")
							.toString(), new String[] { patrolId });

			// 保存处理情况数据到缓存表
			patrolValues.put(PatrolTable.field_caseId, caseId);
			DBHelper.getDbHelper(context).insert(patrolTable, patrolValues);
			

			// 保存处理结果附件
			try {
				JSONArray annexes = patrolJson.getJSONArray("annexes");
				if (annexes != null && annexes.length() > 0){
					Log.i("ydzf", "CaseUtils patrolValues insert");
					storeAnnex2(context, annexes, annexTable, caseId,
							patrolTable, patrolId);
				}
			} catch (Exception e) {
				throw e;
			}

			return patrolId;
		} catch (Exception e) {
			Log.i("ydzf", "storePatrolData_exception="+e.getMessage());
			throw e;
		}
	}

	/**
	 * 
	 * @Title storeInspectData
	 * @param context
	 * @param object
	 * @param caseId
	 * @param inspectTable
	 * @throws Exception
	 *             void
	 */
	public String storeInspectData(Context context, JSONObject object,
			String caseId, String inspectTable) throws Exception {
		try {
			ContentValues inspectValues = inspectJson2ContentValues(object);

			// 删除核查结果已在本地的缓存
			int delInspectCount = DBHelper.getDbHelper(context).delete(
					inspectTable,
					new StringBuffer(InspectTable.field_caseId).append("=?")
							.toString(), new String[] { caseId });
			inspectValues.put(InspectTable.field_caseId, caseId);
			inspectValues.put(InspectTable.field_status, 2);
			// 缓存此核查结果
			DBHelper.getDbHelper(context).insert(inspectTable, inspectValues);
			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String storeInspectData_wpzf(Context context, JSONObject object,
			String caseId, String inspectTable,int status) throws Exception {
		try {
			ContentValues inspectValues = inspectJson2ContentValues(object);

			// 删除核查结果已在本地的缓存
			int delInspectCount = DBHelper.getDbHelper(context).delete(
					inspectTable,
					new StringBuffer(InspectTable.field_caseId).append("=?")
							.toString(), new String[] { caseId });
			inspectValues.put(InspectTable.field_caseId, caseId);
			inspectValues.put(InspectTable.field_status, status);
			// 缓存此核查结果
			DBHelper.getDbHelper(context).insert(inspectTable, inspectValues);
			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @Title downAnnex
	 * @Description 保存网络下载的附件
	 * @param path
	 * @param uri
	 * @throws Exception
	 *             void
	 */
	public void downAnnex(String path, String uri) throws Exception{
		try{
			File targetFile = new File(path);
			OutputStream outStream = new FileOutputStream(targetFile);
			URL url = new URL(uri);

			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

			urlConn.setConnectTimeout(10000);
			InputStream inputStream = urlConn.getInputStream();

			byte[] buffer = new byte[1024];
			int readLen = 0;
			while ((readLen = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, readLen);
			}

			outStream.flush();
			inputStream.close();
		} catch(Exception e){
			e.printStackTrace();
			Log.i("ydzf","CaseUtils downAnnex e="+e.getMessage());
			throw e;
		}
	}
	
	/**
	 * 
	 * @Title saveWebAnnex
	 * @Description 保存网络下载的附件
	 * @param context
	 * @param annexesJson
	 * @param annexTalbe
	 * @param caseId
	 * @param tag
	 * @param tagId
	 * @throws Exception
	 *             void
	 */
	public void storeAnnex2(Context context, JSONArray annexesJson,
			String annexTalbe, String caseId, String tag, String tagId)
			throws Exception {
		try {
			if (annexesJson != null && annexesJson.length() > 0){
				for (int n = 0; n < annexesJson.length(); n++) {
					try {
						JSONObject annexJson = annexesJson.getJSONObject(n);
						String ext = annexJson.getString("extension");
						String name = annexJson.getString("name");
						String uri = annexJson.getString("uri");

						String rePath = new StringBuffer(
								context.getString(R.string.dir_annex))
								.append("/")
								.append(UUID.randomUUID().toString())
								.append(ext).toString();
						String filePath = new StringBuffer(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()).append("/").append(rePath)
								.toString();
						
						ContentValues values = new ContentValues();
						values.put(WebAnnexTable.field_caseId, caseId);
						values.put(WebAnnexTable.field_uri, uri);
						values.put(WebAnnexTable.field_name, name);
						values.put(WebAnnexTable.field_path, filePath);
						values.put(WebAnnexTable.field_tagId, tagId);
						values.put(WebAnnexTable.field_tag, tag);
						DBHelper.getDbHelper(context)
								.insert(annexTalbe, values);
						Log.i("ydzf", "CaseUtils storeAnnex2 insert annex");

					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @Title saveWebAnnex
	 * @Description 保存网络下载的附件
	 * @param context
	 * @param annexesJson
	 * @param annexTalbe
	 * @param caseId
	 * @param tag
	 * @param tagId
	 * @throws Exception
	 *             void
	 */
	public void storeAnnex(Context context, JSONArray annexesJson,
			String annexTalbe, String caseId, String tag, String tagId)
			throws Exception {
		try {
			if (annexesJson != null && annexesJson.length() > 0)
				for (int n = 0; n < annexesJson.length(); n++) {
					try {
						JSONObject annexJson = annexesJson.getJSONObject(n);
						String ext = annexJson.getString("extension");
						String name = annexJson.getString("name");
						String uri = annexJson.getString("uri");

						String rePath = new StringBuffer(
								context.getString(R.string.dir_annex))
								.append("/")
								.append(UUID.randomUUID().toString())
								.append(ext).toString();
						String filePath = new StringBuffer(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()).append("/").append(rePath)
								.toString();
						File targetFile = new File(filePath);
						OutputStream outStream = new FileOutputStream(
								targetFile);

						URL url = new URL(uri);

						HttpURLConnection urlConn = (HttpURLConnection) url
								.openConnection();

						urlConn.setConnectTimeout(10000);
						InputStream inputStream = urlConn.getInputStream();

						byte[] buffer = new byte[1024];
						int readLen = 0;
						while ((readLen = inputStream.read(buffer)) != -1) {
							outStream.write(buffer, 0, readLen);
						}

						outStream.flush();
						inputStream.close();
						ContentValues values = new ContentValues();
						values.put(WebAnnexTable.field_caseId, caseId);
						values.put(WebAnnexTable.field_uri, uri);
						values.put(WebAnnexTable.field_name, name);
						values.put(WebAnnexTable.field_path, filePath);
						values.put(WebAnnexTable.field_tagId, tagId);
						values.put(WebAnnexTable.field_tag, tag);
						DBHelper.getDbHelper(context)
								.insert(annexTalbe, values);

					} catch (Exception e) {
					}

				}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @Title storeCaseSituation
	 * @param context
	 * @param caseId
	 * @param obj
	 * @return
	 * @throws Exception
	 *             long
	 */
	public long storeCaseSituation(Context context, String caseId,
			JSONObject obj) throws Exception {
		try {
			long row = -1;
			int delCount = DBHelper.getDbHelper(context).delete(
					CaseSituationTable.name,
					new StringBuffer(CaseSituationTable.field_caseId).append(
							"=?").toString(), new String[] { caseId });
			ContentValues values = caseSituationJson2ContentValues(caseId, obj);
			if (values != null && values.size() > 0) {
				row = DBHelper.getDbHelper(context).insert(
						CaseSituationTable.name, values);
			}
			return row;
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 
	 * @Title inspectJson2ContentValues
	 * @param obj
	 * @return ContentValues
	 */
	public ContentValues inspectJson2ContentValues(JSONObject obj) {
		ContentValues values = new ContentValues();

		// 违法主体
		try {
			String subject = obj.getJSONObject("illegalSubject").getString(
					"value");
			values.put(InspectTable.field_illegalSubject, subject);
		} catch (Exception e) {
		}

		// 个人/单位
		try {
			String parties = obj.getString("parties");
			values.put(InspectTable.field_parties, parties);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 电话
		try {
			String tel = obj.getString("tel");
			values.put(InspectTable.field_tel, tel);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 违法类型
		try {
			String illegalType = obj.getJSONObject("illegalType").getString(
					"value");
			values.put(InspectTable.field_illegalType, illegalType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 违法现状
		try {
			String illegalStatus = obj.getJSONObject("illegalStatus")
					.getString("value");
			values.put(InspectTable.field_illegalStatus, illegalStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 违法用地面积
		try {
			double illegalArea = obj.getDouble("illegalArea");
			values.put(InspectTable.field_illegalArea, illegalArea);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 地类
		try {
			String landUsage = obj.getJSONObject("landUsage")
					.getString("value");
			values.put(InspectTable.field_landUsage, landUsage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 成果说明
		try {
			String notes = obj.getString("survey");
			values.put(InspectTable.field_notes, notes);
		} catch (Exception e) {
		}

		// 核查结果
		try {
			String surveyResult = obj.getJSONObject("patrolResult").getString(
					"value");
			values.put(InspectTable.field_inspectResult, surveyResult);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 核查人
		try {
			String user = obj.getJSONObject("user").getString("chname");
			values.put(InspectTable.field_user, user);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 核查时间
		try {
			String inspectTime = obj.getString("patrolTime");
			if (inspectTime != null && !inspectTime.equals("null")
					&& !inspectTime.equals(""))
				values.put(InspectTable.field_mTime, inspectTime);
		} catch (Exception e) {
		}
		return values;
	}

	/**
	 * 
	 * @Title patrolJson2ContentValues
	 * @param obj
	 * @return ContentValues
	 */
	public ContentValues patrolJson2ContentValues(JSONObject obj) {
		ContentValues values = new ContentValues();

		// 编号
		try {
			String id = obj.getString("id");
			values.put(PatrolTable.field_id, id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 处理情况
		try {
			String s = obj.getJSONObject("clqk").getString("value");// obj.getString("clqk");
			values.put(PatrolTable.field_deal, s);
		} catch (Exception e) {
		}

		// 制止通知书编号
		try {
			String s = obj.getString("zztzsbh");
			values.put(PatrolTable.field_stopNoticeNo, s);
		} catch (Exception e) {
		}

		// 下达时间
		try {
			String s = obj.getString("xdsj");
			values.put(PatrolTable.field_stopNoticeDate, s);
		} catch (Exception e) {
		}

		// 计划拆除时间
		try {
			String s = obj.getString("jhccsj");
			values.put(PatrolTable.field_pullPlanDate, s);
		} catch (Exception e) {
		}

		// 组织人数
		try {
			String s = obj.getString("zzrs");
			values.put(PatrolTable.field_pullPlanNum, Integer.parseInt(s));
		} catch (Exception e) {
		}

		// 拆除负责人
		try {
			String s = obj.getString("ccfzr");
			values.put(PatrolTable.field_pullPlanPerson, s);
		} catch (Exception e) {
		}

		// 立案决定书文号
		try {
			String s = obj.getString("lajdswh");
			values.put(PatrolTable.field_caseDocumentNo, s);
		} catch (Exception e) {
		}

		// 发文时间
		try {
			String s = obj.getString("fawsj");
			values.put(PatrolTable.field_caseDocumentDate, s);
		} catch (Exception e) {
		}

		// 报告政府时间
		try {
			String s = obj.getString("bgzfsj");
			values.put(PatrolTable.field_govDate, s);
		} catch (Exception e) {
		}
		// 转送时间
		try {
			String s = obj.getString("zssj");
			values.put(PatrolTable.field_sendDate, s);
		} catch (Exception e) {
		}
		// 红线
		try {
			JSONObject redlineObject = obj.getJSONObject("hx");
			if (redlineObject != null && redlineObject != JSONObject.NULL)
				values.put(PatrolTable.field_redline, redlineObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 备注
		try {
			String notes = obj.getString("bz");
			values.put(PatrolTable.field_notes, notes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 处理上报人
		try {
			String user = obj.getJSONObject("user").getString("chname");
			values.put(PatrolTable.field_user, user);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 处理上报时间
		try {
			String patrolTime = obj.getString("patrolTime");
			if (patrolTime != null && !patrolTime.equals("")
					&& !patrolTime.equals("null"))
				values.put(PatrolTable.field_mTime, patrolTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	public ContentValues caseJson2ImspectionContentValues(JSONObject obj) {
		ContentValues values = new ContentValues();
		// 案件ID
		try {
			values.put(InspectTable.field_caseId, obj.getString("id"));
		} catch (Exception e) {
		}

		// 违法主体
		try {
			String illegalSubject = obj.getJSONObject("illegalSubject")
					.getString("key");
			values.put(InspectTable.field_illegalSubject, illegalSubject);
		} catch (Exception e) {
		}

		// 当事人
		try {
			String parties = obj.getString("parties");
			values.put(InspectTable.field_parties, parties);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 违法类型
		try {
			String illegalType = obj.getJSONObject("illegalType").getString(
					"key");
			values.put(InspectTable.field_illegalType, illegalType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 违法面积
		try {
			double illegalArea = obj.getDouble("illegalArea");
			values.put(InspectTable.field_illegalArea, illegalArea);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 违法状态
		try {
			String illegalStatus = obj.getJSONObject("illegalStatus")
					.getString("key");
			values.put(InspectTable.field_illegalStatus, illegalStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 地类
		try {
			String landUsage = obj.getJSONObject("landUsage")
					.getString("key");
			values.put(InspectTable.field_landUsage, landUsage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return values;
	}

	/**
	 * 
	 * @Title caseJson2ContentValues
	 * @param obj
	 * @return ContentValues
	 */
	public ContentValues caseJson2ContentValues(JSONObject obj) {
		ContentValues values = new ContentValues();

		// 案件ID
		try {
			values.put(WebCaseTable.field_id, obj.getString("id"));
		} catch (Exception e) {
		}

		// 案件编号
		try {
			values.put(WebCaseTable.field_bh, obj.getString("case_bh"));
		} catch (Exception e) {
		}

		// 案件状态
		try {
			String statusText = obj.getJSONObject("status").getString("value");
			int status = obj.getJSONObject("status").getInt("key");
			values.put(WebCaseTable.field_status, status);
			values.put(WebCaseTable.field_status_text, statusText);
		} catch (Exception e) {
		}

		// 违法主体
		try {
			String illegalSubject = obj.getJSONObject("illegalSubject")
					.getString("value");
			values.put(WebCaseTable.field_illegalSubject, illegalSubject);
		} catch (Exception e) {
		}

		// 当事人
		try {
			String parties = obj.getString("parties");
			values.put(WebCaseTable.field_parties, parties);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 详细地址
		try {
			String addrerss = obj.getString("address");
			values.put(WebCaseTable.field_address, addrerss);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 备注
		try {
			String notes = obj.getString("notes");
			values.put(WebCaseTable.field_notes, notes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//一张图分析结果
		try{
			String analysis = obj.getString("analysis");
			values.put(WebCaseTable.field_analysis, analysis);
		}catch (Exception e){
			e.printStackTrace();
		}

		// 案件位置
		try {
			String location = obj.getString("location").toString();
			// 判定xy是否为空
			if (location != null && !location.equals("")
					&& !location.equals("null"))
				values.put(WebCaseTable.field_location, location);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 红线
		try {
			JSONObject redlineObject = obj.getJSONObject("redline");
			if (redlineObject != null && redlineObject != JSONObject.NULL)
				values.put(WebCaseTable.field_redline, redlineObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 违法类型
		try {
			String illegalType = obj.getJSONObject("illegalType").getString(
					"value");
			values.put(WebCaseTable.field_illegalType, illegalType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 违法面积
		try {
			double illegalArea = obj.getDouble("illegalArea");
			values.put(WebCaseTable.field_illegalArea, illegalArea);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 项目类型
		try {
			String projType = obj.getJSONObject("projectType").getString(
					"value");
			values.put(WebCaseTable.field_projType, projType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 违法状态
		try {
			String illegalStatus = obj.getJSONObject("illegalStatus")
					.getString("value");
			values.put(WebCaseTable.field_illegalStatus, illegalStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 地类
		try {
			String landUsage = obj.getJSONObject("landUsage")
					.getString("value");
			values.put(WebCaseTable.field_landUsage, landUsage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 线索来源
		try {
			String source = obj.getJSONObject("source").getString("value");
			values.put(WebCaseTable.field_source, source);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 上报人
		try {
			String user = obj.getJSONObject("user").getString("chname");
			values.put(WebCaseTable.field_user, user);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 上报时间
		try {
			String reportTime = obj.getString("reportTime");
			if (reportTime != null && !reportTime.equals("null")
					&& !reportTime.equals(""))
				values.put(WebCaseTable.field_mTime, reportTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	public ContentValues caseSituationJson2ContentValues(String caseId,
			JSONObject object) {
		ContentValues values = new ContentValues();
		values.put(CaseSituationTable.field_caseId, caseId);

		// 制止通知书
		try {
			String s = object.getString("zztzs");
			values.put(CaseSituationTable.field_stopNotice, s);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 制止情况
		try {
			String s = object.getString("zzqk");
			values.put(CaseSituationTable.field_stopSituation, s);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 拆除计划
		try {
			String s = object.getString("ccjh");
			values.put(CaseSituationTable.field_pullPlan, s);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 拆除情况
		try {
			String s = object.getString("ccqk");
			values.put(CaseSituationTable.field_pullSituation, s);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 立案查处
		try {
			String s = object.getString("lacc");
			values.put(CaseSituationTable.field_caseSurvey, s);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 处理情况下拉选择内容
		try {
			JSONArray args = object.getJSONArray("clqk");
			if (args != null && args.length() > 0)
				values.put(CaseSituationTable.field_arguments, args.toString());
		} catch (Exception e) {
		}

		return values;

	}
}
