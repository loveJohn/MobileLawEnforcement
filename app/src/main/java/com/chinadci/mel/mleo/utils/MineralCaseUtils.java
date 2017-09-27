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

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.MilPatrolAnnexesTable;
import com.chinadci.mel.mleo.ldb.MilPatrolTable;
import com.chinadci.mel.mleo.ldb.MineralHcTable;
import com.chinadci.mel.mleo.ldb.WebAnnexTable;
import com.chinadci.mel.mleo.ldb.WebMinPatrolTable;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;

public class MineralCaseUtils {
	private static Lock instanceLock = new ReentrantLock();
	private volatile static MineralCaseUtils instance;

	/**
	 * 
	 * @Title: getInstance
	 * @Description: TODO
	 * @return TeachingUtils
	 */
	public static MineralCaseUtils getInstance() {
		if (instance == null) {
			instanceLock.lock();
			try {
				instance = new MineralCaseUtils();
			} catch (Exception e) {
			} finally {
				instanceLock.unlock();
			}
		}
		return instance;
	}

	public String storeMineralFulldata(Context context, String inUser,
			JSONObject obj, String webMineralTable, String annexTable,
			String MineralHcTable) throws Exception {
		try {
			// 保存案件信息
			JSONObject caseJsonObject = obj.getJSONObject("case");
			String caseId = storeMineralData(context, inUser, caseJsonObject,
					webMineralTable, annexTable);

			

			// 保存处理结果信息
			try {
				JSONArray patrolJsonArray = obj.getJSONArray("patrols");
				if (patrolJsonArray != null && patrolJsonArray.length() > 0) {
					for (int i = 0; i < patrolJsonArray.length(); i++) {
						storeMineralHcData(context, caseId,
								patrolJsonArray.getJSONObject(i), MineralHcTable,
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
	
	
	public String storeTrackMineralFulldata(Context context, String inUser,
			JSONObject obj, String webMineralTable, String annexTable,
			String MineralHcTable,int status) throws Exception {
		try {
			// 保存案件信息
			JSONObject caseJsonObject = obj.getJSONObject("case");
			String caseId = storeTrackMineralData(context, inUser, caseJsonObject,
					webMineralTable, annexTable,status);

			

			// 保存处理结果信息
			try {
				JSONObject patrolJson = caseJsonObject.getJSONObject("kchc");
					storeTrackMineralHcData(context, caseId,
							patrolJson, MineralHcTable,
							annexTable);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String storeMineralData(Context context,String inUser,JSONObject obj,
			String mineralTable,String annexTable) throws Exception
	{
		try
		{
			ContentValues caseBaseValues=MinseralJson2ContentValues(obj);
			caseBaseValues.put(WebMinPatrolTable.field_inUser, inUser);
			String caseId=caseBaseValues.getAsString(MilPatrolTable.field_id);
			//此案件已在本地缓存过的附件
			ArrayList<ContentValues> annexValues=DBHelper
					.getDbHelper(context).doQuery(
							annexTable, new String[] { MilPatrolAnnexesTable.field_path },
							new StringBuffer(MilPatrolAnnexesTable.field_tag)
							.append("=? and ")
							.append(MilPatrolAnnexesTable.field_tagId)
							.append("=?").toString(),
					new String[] { mineralTable, caseId }, null, null,
					null, null);
			if (annexValues != null && annexValues.size() > 0) {
				for (ContentValues annex : annexValues) {
					String filePath = annex.getAsString(MilPatrolAnnexesTable.field_path);
					File file = new File(filePath);
					if (file != null && file.exists())
						file.delete();
				}
			}
			
			int delAnnexCount = DBHelper.getDbHelper(context).delete(
					annexTable,
					new StringBuffer(MilPatrolAnnexesTable.field_tag).append("=? and ")
							.append(MilPatrolAnnexesTable.field_tagId).append("=?")
							.toString(), new String[] { mineralTable, caseId });

			// 删除案件已在本地的缓存
			int delCaseCount = DBHelper.getDbHelper(context).delete(
					mineralTable,
					new StringBuffer(WebMinPatrolTable.field_id).append("=?")
							.toString(), new String[] { caseId });
			// 保存案件基础信息
			caseBaseValues.put(WebMinPatrolTable.field_inUser, inUser);
			DBHelper.getDbHelper(context).insert(mineralTable, caseBaseValues);

			// 保存案件的附件
			try {
				JSONArray annexesJson = obj.getJSONArray("annexes");
				if (annexesJson != null && annexesJson.length() > 0)
					storeAnnex(context, annexesJson, annexTable, caseId,
							mineralTable, caseId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String storeTrackMineralData(Context context,String inUser,JSONObject obj,
			String mineralTable,String annexTable,int status) throws Exception
	{
		try
		{
			ContentValues caseBaseValues=MinseralJson2ContentValues(obj);
			caseBaseValues.put(WebMinPatrolTable.field_inUser, inUser);
			String caseId=caseBaseValues.getAsString(MilPatrolTable.field_id);
			//此案件已在本地缓存过的附件
			ArrayList<ContentValues> annexValues=DBHelper
					.getDbHelper(context).doQuery(
							annexTable, new String[] { MilPatrolAnnexesTable.field_path },
							new StringBuffer(MilPatrolAnnexesTable.field_tag)
							.append("=? and ")
							.append(MilPatrolAnnexesTable.field_tagId)
							.append("=?").toString(),
					new String[] { mineralTable, caseId }, null, null,
					null, null);
			if (annexValues != null && annexValues.size() > 0) {
				for (ContentValues annex : annexValues) {
					String filePath = annex.getAsString(MilPatrolAnnexesTable.field_path);
					File file = new File(filePath);
					if (file != null && file.exists())
						file.delete();
				}
			}
			
			int delAnnexCount = DBHelper.getDbHelper(context).delete(
					annexTable,
					new StringBuffer(MilPatrolAnnexesTable.field_tag).append("=? and ")
							.append(MilPatrolAnnexesTable.field_tagId).append("=?")
							.toString(), new String[] { mineralTable, caseId });

			// 删除案件已在本地的缓存
			int delCaseCount = DBHelper.getDbHelper(context).delete(
					mineralTable,
					new StringBuffer(WebMinPatrolTable.field_id).append("=?")
							.toString(), new String[] { caseId });
			// 保存案件基础信息
			caseBaseValues.put(WebMinPatrolTable.field_inUser, inUser);
			caseBaseValues.put(WebMinPatrolTable.field_status, status);
			DBHelper.getDbHelper(context).insert(mineralTable, caseBaseValues);

			// 保存案件的附件
			try {
				JSONArray annexesJson = obj.getJSONArray("annexes");
				if (annexesJson != null && annexesJson.length() > 0)
					storeAnnex(context, annexesJson, annexTable, caseId,
							mineralTable, caseId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return caseId;
		} catch (Exception e) {
			throw e;
		}
	}
	public String storeTrackMineralHcData(Context context,String caseId,JSONObject mineralHcJson,
			String mineralHcTable,String annexTable) throws Exception
	{
		try {
			ContentValues patrolValues = minseralTrackHcJson2ContentValues(mineralHcJson);
			String patrolId = patrolValues.getAsString(MineralHcTable.field_id);

			// 此处理结果已在本地缓存过的附件
			ArrayList<ContentValues> annexValues = DBHelper
					.getDbHelper(context).doQuery(
							annexTable,
							new String[] { MilPatrolAnnexesTable.field_path },
							new StringBuffer(MilPatrolAnnexesTable.field_tag)
									.append("=? and ")
									.append(MilPatrolAnnexesTable.field_tagId)
									.append("=?").toString(),
							new String[] { mineralHcTable, patrolId }, null, null,
							null, null);
			if (annexValues != null && annexValues.size() > 0) {
				for (ContentValues annex : annexValues) {
					String filePath = annex.getAsString(MilPatrolAnnexesTable.field_path);
					File file = new File(filePath);
					if (file != null && file.exists())
						file.delete();
				}
			}
			int delAnnexCount = DBHelper.getDbHelper(context)
					.delete(annexTable,
							new StringBuffer(MilPatrolAnnexesTable.field_tag)
									.append("=? and ")
									.append(MilPatrolAnnexesTable.field_tagId)
									.append("=?").toString(),
							new String[] { mineralHcTable, patrolId });

			// 删除处理结果已在本地的缓存
			int delPatrolCount = DBHelper.getDbHelper(context).delete(
					mineralHcTable,
					new StringBuffer(MineralHcTable.field_id).append("=?")
							.toString(), new String[] { patrolId });

			// 保存处理情况数据到缓存表
			patrolValues.put(MineralHcTable.field_caseId, caseId);
			DBHelper.getDbHelper(context).insert(mineralHcTable, patrolValues);

			// 保存处理结果附件
			try {
				JSONArray annexes = mineralHcJson.getJSONArray("annexes");
				if (annexes != null && annexes.length() > 0)
					storeAnnex(context, annexes, annexTable, caseId,
							mineralHcTable, patrolId);
			} catch (Exception e) {
				throw e;
			}

			return patrolId;
		} catch (Exception e) {
			throw e;
		}
	}
	public String storeMineralHcData(Context context,String caseId,JSONObject mineralHcJson,
			String mineralHcTable,String annexTable) throws Exception
	{
		try {
			ContentValues patrolValues = minseralHcJson2ContentValues(mineralHcJson);
			String patrolId = patrolValues.getAsString(MineralHcTable.field_id);

			// 此处理结果已在本地缓存过的附件
			ArrayList<ContentValues> annexValues = DBHelper
					.getDbHelper(context).doQuery(
							annexTable,
							new String[] { MilPatrolAnnexesTable.field_path },
							new StringBuffer(MilPatrolAnnexesTable.field_tag)
									.append("=? and ")
									.append(MilPatrolAnnexesTable.field_tagId)
									.append("=?").toString(),
							new String[] { mineralHcTable, patrolId }, null, null,
							null, null);
			if (annexValues != null && annexValues.size() > 0) {
				for (ContentValues annex : annexValues) {
					String filePath = annex.getAsString(MilPatrolAnnexesTable.field_path);
					File file = new File(filePath);
					if (file != null && file.exists())
						file.delete();
				}
			}
			int delAnnexCount = DBHelper.getDbHelper(context)
					.delete(annexTable,
							new StringBuffer(MilPatrolAnnexesTable.field_tag)
									.append("=? and ")
									.append(MilPatrolAnnexesTable.field_tagId)
									.append("=?").toString(),
							new String[] { mineralHcTable, patrolId });

			// 删除处理结果已在本地的缓存
			int delPatrolCount = DBHelper.getDbHelper(context).delete(
					mineralHcTable,
					new StringBuffer(MineralHcTable.field_id).append("=?")
							.toString(), new String[] { patrolId });

			// 保存处理情况数据到缓存表
			patrolValues.put(MineralHcTable.field_caseId, caseId);
			DBHelper.getDbHelper(context).insert(mineralHcTable, patrolValues);

			// 保存处理结果附件
			try {
				JSONArray annexes = mineralHcJson.getJSONArray("annexes");
				if (annexes != null && annexes.length() > 0)
					storeAnnex(context, annexes, annexTable, caseId,
							mineralHcTable, patrolId);
			} catch (Exception e) {
				throw e;
			}

			return patrolId;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	public ContentValues MinseralJson2ContentValues(JSONObject obj) 
	{
		ContentValues values = new ContentValues();
		try {
			
			String user = obj.getString("xcry");
			values.put(WebMinPatrolTable.field_id, obj.getString("id"));
			values.put(WebMinPatrolTable.field_user, user);
			// 巡查时间
			try {
				String xcsj = obj.getString("txsj");
				if (xcsj != null && !xcsj.equals("null") && !xcsj.equals(""))
					values.put(WebMinPatrolTable.field_logTime, xcsj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 违法主体名称
			try {
				String wfztmc = obj.getString("wfztmc");
				values.put(WebMinPatrolTable.field_wfztmc, wfztmc);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 巡查线路
			try {
				String xcxl = obj.getString("xcxl");
				if (xcxl != null && !xcxl.equals("null") && !xcxl.equals(""))
					values.put(WebMinPatrolTable.field_line, xcxl);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 是否发现非法采矿点
			try {
				String sffxffckd = obj.getString("sffxffckd");
				if (sffxffckd != null && !sffxffckd.equals("null")
						&& !sffxffckd.equals(""))
					values.put(WebMinPatrolTable.field_hasMining, sffxffckd);// ? 1: 0
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 是否发现非法采矿点
			try {
				String fxwt = obj.getString("fxwt");
				if (fxwt != null && !fxwt.equals("null") && !fxwt.equals(""))
					values.put(WebMinPatrolTable.field_exception, fxwt);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 备注
			try {
				String bz = obj.getString("bz");
				if (bz != null && !bz.equals("null") && !bz.equals(""))
					values.put(WebMinPatrolTable.field_notes, bz);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				String location = obj.getString("location").toString();
				// 判定xy是否为空
				if (location != null && !location.equals("")
						&& !location.equals("null"))
					values.put(WebMinPatrolTable.field_location, location);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// redline
			try {
				JSONObject redlineObject = obj.getJSONObject("redline");
				if (redlineObject != null && redlineObject != JSONObject.NULL)
					values.put(WebMinPatrolTable.field_redline,
							redlineObject.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 非法采矿点编号
			try {
				String ffckdbh = obj.getString("ffckdbh");
				if (ffckdbh != null && !ffckdbh.equals("null")
						&& !ffckdbh.equals(""))
					values.put(WebMinPatrolTable.field_ffckbh, ffckdbh);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 是否制止非法行为
			try {

				String sfzzffxw = obj.getString("sfzzffxw");
				if (sfzzffxw != null && !sfzzffxw.equals("null")
						&& !sfzzffxw.equals(""))
					values.put(WebMinPatrolTable.field_haszz, sfzzffxw);// ? 1 : 0
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 制止文书编号
			try {
				String zzwhbh = obj.getString("zzwhbh");
				if (zzwhbh != null && !zzwhbh.equals("null")
						&& !zzwhbh.equals(""))
					values.put(WebMinPatrolTable.field_zzwsbh, zzwhbh);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 巡查日志编号
			try {
				String xcrzlx = obj.getJSONObject("xcrzlx")
						.getString("value");
				if (xcrzlx != null && !xcrzlx.equals("null")
						&& !xcrzlx.equals(""))
					values.put(WebMinPatrolTable.field_xcrzxl, xcrzlx);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 所在村居
			try {
				String szcj = obj.getString("szcj");
				if (szcj != null && !szcj.equals("null")
						&& !szcj.equals(""))
					values.put(WebMinPatrolTable.field_szcj, szcj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 案件状态
			try {
				String ajzt = obj.getJSONObject("status").getString("value");
				if (ajzt != null && !ajzt.equals("null")
						&& !ajzt.equals(""))
					values.put(WebMinPatrolTable.field_ajzt, ajzt);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 案件状态
			try {
				String ajzt = obj.getJSONObject("status").getString("value");
				if (ajzt != null && !ajzt.equals("null")
						&& !ajzt.equals(""))
					values.put(WebMinPatrolTable.field_ajzt, ajzt);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}
	
	
	public ContentValues minseralHcJson2ContentValues(JSONObject obj) {
		ContentValues values = new ContentValues();
		values.put(MineralHcTable.field_status, 2);
		try {
			String id = obj.getString("id");
			values.put(MineralHcTable.field_id, id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 人员名称
		try {
			String hcrmc = obj.getString("hcrmc");
			values.put(MineralHcTable.field_hcrmc, hcrmc);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 是否非法采矿点，0：否，1：是
		try {
			String sfffckd = obj.getString("sfffckd");
			values.put(MineralHcTable.field_sfffckd,
					sfffckd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 是否制止非法行为
		try {

			String sfljqd = obj.getString("sfljqd");
			if (sfljqd != null && !sfljqd.equals("null")
					&& !sfljqd.equals(""))
				values.put(MineralHcTable.field_sfljqd, sfljqd);// ? 1 : 0
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 违法主体性质，1：个人，2：企业
		try {
			String wfztxz = obj.getString("wfztxz");
			values.put(MineralHcTable.field_wfztxz,
					wfztxz.equals("1") ? "个人" : "企业");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 违法主体名称
		try {
			String wfztmc = obj.getString("wfztmc");
			values.put(MineralHcTable.field_wfztmc, wfztmc);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 非法开采方式
		try {
			String ffkcfs = obj.getJSONObject("ffkcfs")
					.getString("value");// obj.getString("ffkcfs");

			values.put(MineralHcTable.field_ffkcfs, ffkcfs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 是否停止非法开采，0：否，1：是
		try {
			String sftzffkc = obj.getString("sftzffkc");
			values.put(MineralHcTable.field_sftzffkc,
					sftzffkc.equals("0"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 备注
		try {
			String hccomment = obj.getString("hccomment");
			values.put(MineralHcTable.field_hccomment,
					hccomment);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 核查时间，形如2014-05-01
		try {
			String hcsj = obj.getString("hcsj");
			values.put(MineralHcTable.field_hcsj, hcsj);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 非法开采矿种
		try {
			String fkckz = obj.getJSONObject("fkckz")
					.getString("value");// obj.getString("fkckz");
			values.put(MineralHcTable.field_fkckz, fkckz);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return values;
	}
	
	public ContentValues minseralTrackHcJson2ContentValues(JSONObject obj) {
		ContentValues values = new ContentValues();
		values.put(MineralHcTable.field_status, 2);
		try {
			String id = obj.getString("HCBH");
			values.put(MineralHcTable.field_id, id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 人员名称
		try {
			String hcrmc = obj.getString("HCRMC");
			values.put(MineralHcTable.field_hcrmc, hcrmc);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 是否非法采矿点，0：否，1：是
		try {
			String sfffckd = obj.getString("SFFFCKD");
			values.put(MineralHcTable.field_sfffckd,
					sfffckd);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 违法主体性质，1：个人，2：企业
		try {
			String wfztxz = obj.getString("WFZTXZ");
			values.put(MineralHcTable.field_wfztxz,
					wfztxz);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 违法主体名称
		try {
			String wfztmc = obj.getString("WFZTMC");
			values.put(MineralHcTable.field_wfztmc, wfztmc);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 非法开采方式
		try {
			String ffkcfs = obj.getString("FFKCFS");// obj.getString("ffkcfs");

			values.put(MineralHcTable.field_ffkcfs, ffkcfs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 是否停止非法开采，0：否，1：是
		try {
			String sftzffkc = obj.getString("SFTZFFKC");
			values.put(MineralHcTable.field_sftzffkc,
					sftzffkc);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 备注
		try {
			String hccomment = obj.getString("HCCOMMENT");
			values.put(MineralHcTable.field_hccomment,
					hccomment);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 核查时间，形如2014-05-01
		try {
			String hcsj = obj.getString("HCSJ");
			values.put(MineralHcTable.field_hcsj, hcsj);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 非法开采矿种
		try {
			String fkckz = obj.getString("FKCKZ");// obj.getString("fkckz");
			values.put(MineralHcTable.field_fkckz, fkckz);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return values;
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
						// TODO: handle exception
						e.printStackTrace();
					}

				}
		} catch (Exception e) {
			throw e;
		}
	}
}
