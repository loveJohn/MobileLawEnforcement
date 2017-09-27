package com.chinadci.mel.mleo.ui.fragments.data.ldb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.chinadci.mel.mleo.ui.fragments.data.model.Hx;
import com.chinadci.mel.mleo.ui.fragments.data.model.InspectionEdit;
import com.chinadci.mel.mleo.ui.fragments.data.model.InspectionGetTask;
import com.chinadci.mel.mleo.ui.fragments.data.model.InspectionGetTask2;
import com.chinadci.mel.mleo.ui.fragments.data.model.Jsdt;
import com.chinadci.mel.mleo.ui.fragments.data.model.PatrolsType;
import com.chinadci.mel.mleo.ui.fragments.data.model.Patrols_CH;
import com.chinadci.mel.mleo.ui.fragments.data.model.Patrols_SP;
import com.chinadci.mel.mleo.ui.fragments.data.model.Patrols_TH;
import com.chinadci.mel.mleo.ui.fragments.data.model.SimpleAj;
import com.chinadci.mel.mleo.ui.fragments.data.model.SimpleAj2;
import com.chinadci.mel.mleo.ui.fragments.data.model.SimpleAj_Wpzf;
import com.chinadci.mel.mleo.ui.fragments.data.model.SimpleAj_Wpzf_find;
import com.chinadci.mel.mleo.ui.fragments.data.model.StNum;
import com.chinadci.mel.mleo.ui.fragments.data.model.XZQHNum;
import com.chinadci.mel.mleo.ui.fragments.data.model.XZQHNum_Wpzf;

public class DbUtil {
	
	public static void insertPhoto(Context context, String photoPath,String xzb,
			String yzb, String fwj, String pssj) {
		try {
			String sql = "INSERT INTO PHOTO VALUES(?,?,?,?,?)";
			String[] bindArgs = new String[] { photoPath,xzb, yzb, fwj, pssj };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	public static void deletePhotoByPath(Context context, String photoPath) {
		try {
			String sql = "DELETE FROM PHOTO where photoPath='" +photoPath + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	public static String getPHOTO_XZB_ByPath(Context context,String photoPath) {
		String sql;
		Cursor cursor = null;
		String result = null;
		try {
			sql = "select * from PHOTO where photoPath='" +photoPath + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						result = cursor
								.getString(cursor
										.getColumnIndex(ParmeterTable.PHOTO_PARMETER.field_xzb));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return result;
	}
	public static String getPHOTO_YZB_ByPath(Context context,String photoPath) {
		String sql;
		Cursor cursor = null;
		String result = null;
		try {
			sql = "select * from PHOTO where photoPath='" +photoPath + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						result = cursor
								.getString(cursor
										.getColumnIndex(ParmeterTable.PHOTO_PARMETER.field_yzb));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return result;
	}

	public static String getPHOTO_FWJ_ByPath(Context context,String photoPath) {
		String sql;
		Cursor cursor = null;
		String result = null;
		try {
			sql = "select * from PHOTO where photoPath='" +photoPath + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						result = cursor
								.getString(cursor
										.getColumnIndex(ParmeterTable.PHOTO_PARMETER.field_fwj));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return result;
	}
	
	public static String getPHOTO_PSSJ_ByPath(Context context,String photoPath) {
		String sql;
		Cursor cursor = null;
		String result = null;
		try {
			sql = "select * from PHOTO where photoPath='" +photoPath + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						result = cursor
								.getString(cursor
										.getColumnIndex(ParmeterTable.PHOTO_PARMETER.field_pssj));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return result;
	}
	
	public static void insertWpzfAjNum(Context context, String KEY,
			String BL_ZT, String COUNT, String xzqh) {
		try {
			String sql = "INSERT INTO WpzfAjNum VALUES(?,?,?,?)";
			String[] bindArgs = new String[] { KEY, BL_ZT, COUNT, xzqh };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deleteWpzfAjNumByKeyAndXzqh(Context context, String KEY,
			String xzqh) {
		try {
			String sql = "DELETE FROM WpzfAjNum where KEY='" + KEY
					+ "' and xzqh='" + xzqh + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static String getWpzfAjNumCountByKeyAndXzqh(Context context,
			String KEY, String xzqh) {
		String sql;
		Cursor cursor = null;
		String result = null;
		try {
			sql = "select * from WpzfAjNum where KEY='" + KEY + "' and xzqh='"
					+ xzqh + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						result = cursor
								.getString(cursor
										.getColumnIndex(ParmeterTable.WpzfAjNum_PARMETER.field_COUNT));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return result;
	}

	public static void insertclqk_now(Context context, String key,
			String value, String parent) {
		try {
			String sql = "INSERT INTO clqk_now VALUES(?,?,?)";
			String[] bindArgs = new String[] { key, value, parent };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deleteclqk_nowByKey(Context context, String key) {
		try {
			String sql = "DELETE FROM clqk_now where key='" + key + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static String getclqk_nowParentByKey(Context context, String key) {
		String sql;
		Cursor cursor = null;
		String result = null;
		try {
			sql = "select * from clqk_now where key='" + key + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						result = cursor
								.getString(cursor
										.getColumnIndex(ParmeterTable.clqk_now_PARMETER.field_parent));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return result;
	}

	public static Map<String, String> getclqk_nowValueByParent(Context context,
			String parent) {
		String sql;
		Cursor cursor = null;
		Map<String, String> result = null;
		try {
			sql = "select * from clqk_now where parent='" + parent + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				result = new LinkedHashMap<String, String>();
				while (cursor.moveToNext()) {
					try {
						result.put(
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.clqk_now_PARMETER.field_key)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.clqk_now_PARMETER.field_value)));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return result;
	}

	public static void insertPatrols(Context context, String id, String fxjg) {
		try {
			String sql = "INSERT INTO PATROLS VALUES(?,?)";
			String[] bindArgs = new String[] { id, fxjg };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void insertPatrolsType(Context context, PatrolsType pType) {
		try {
			String sql = "INSERT INTO PATROLS_TYPE VALUES(?,?,?)";
			String[] bindArgs = new String[] { pType.getId(), pType.getTitle(),
					pType.getType() };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void insertPatrolsSP(Context context, Patrols_SP sPatrols_SP) {
		try {
			String sql = "INSERT INTO PATROLS_SP VALUES(?,?,?,?)";
			String[] bindArgs = new String[] { sPatrols_SP.getId(),
					sPatrols_SP.getSpry(), sPatrols_SP.getSpsj(),
					sPatrols_SP.getSpsm() };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	public static void insertPatrolsCH(Context context, Patrols_CH sPatrols_CH) {
		try {
			String sql = "INSERT INTO PATROLS_CH VALUES(?,?,?,?)";
			String[] bindArgs = new String[] { sPatrols_CH.getId(),
					sPatrols_CH.getChry(), sPatrols_CH.getChsj(),
					sPatrols_CH.getChyy() };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	public static void insertPatrolsTH(Context context, Patrols_TH sPatrols_TH) {
		try {
			String sql = "INSERT INTO PATROLS_TH VALUES(?,?,?,?,?,?,?)";
			String[] bindArgs = new String[] { sPatrols_TH.getId(),
					sPatrols_TH.getSqry(),sPatrols_TH.getSqsj(),sPatrols_TH.getSqyy(),
					sPatrols_TH.getThry(), sPatrols_TH.getThsj(),sPatrols_TH.getThyy() };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
			Log.i("ydzf", "DbUtil ### insert PATROLS_TH done��sPatrols_TH="+sPatrols_TH.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			Log.i("ydzf", "exception="+e.getMessage());
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	public static void deletePatrolsSPById(Context context, String id) {
		try {
			String sql = "DELETE FROM PATROLS_SP where id='" + id + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	public static void deletePatrolsCHById(Context context, String id) {
		try {
			String sql = "DELETE FROM PATROLS_CH where id='" + id + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	public static void deletePatrolsTHById(Context context, String id) {
		try {
			String sql = "DELETE FROM PATROLS_TH where id='" + id + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deletePatrolsTypeById(Context context, String id) {
		try {
			String sql = "DELETE FROM PATROLS_TYPE where id='" + id + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deletePatrolsById(Context context, String id) {
		try {
			String sql = "DELETE FROM PATROLS where id='" + id + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static String getPatrolsFxjgByid(Context context, String id) {
		String sql;
		Cursor cursor = null;
		String result = null;
		try {
			sql = "select * from PATROLS where id='" + id + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						result = cursor
								.getString(cursor
										.getColumnIndex(ParmeterTable.PATROLS_PARMETER.field_fxjg));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return result;
	}

	public static PatrolsType getPatrolsTypeByid(Context context, String id) {
		String sql;
		Cursor cursor = null;
		PatrolsType result = null;
		try {
			sql = "select * from PATROLS_TYPE where id='" + id + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						result = new PatrolsType();
						result.setId(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_TYPE_PARMETER.field_id)));
						result.setTitle(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_TYPE_PARMETER.field_title)));
						result.setType(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_TYPE_PARMETER.field_type)));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return result;
	}

	public static Patrols_SP getPatrolsSPByid(Context context, String id) {
		String sql;
		Cursor cursor = null;
		Patrols_SP result = null;
		try {
			sql = "select * from PATROLS_SP where id='" + id + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						result = new Patrols_SP();
						result.setId(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_SP_PARMETER.field_id)));
						result.setSpry(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_SP_PARMETER.field_spry)));
						result.setSpsj(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_SP_PARMETER.field_spsj)));
						result.setSpsm(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_SP_PARMETER.field_spsm)));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return result;
	}
	
	public static Patrols_CH getPatrolsCHByid(Context context, String id) {
		String sql;
		Cursor cursor = null;
		Patrols_CH result = null;
		try {
			sql = "select * from PATROLS_CH where id='" + id + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						result = new Patrols_CH();
						result.setId(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_CH_PARMETER.field_id)));
						result.setChry(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_CH_PARMETER.field_chry)));
						result.setChsj(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_CH_PARMETER.field_chsj)));
						result.setChyy(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_CH_PARMETER.field_chyy)));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return result;
	}
	
	public static Patrols_TH getPatrolsTHByid(Context context, String id) {
		String sql;
		Cursor cursor = null;
		Patrols_TH result = null;
		try {
			sql = "select * from PATROLS_TH where id='" + id + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						result = new Patrols_TH();
						result.setId(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_TH_PARMETER.field_id)));
						result.setSqry(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_TH_PARMETER.field_sqry)));
						result.setSqsj(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_TH_PARMETER.field_sqsj)));
						result.setSqyy(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_TH_PARMETER.field_sqyy)));
						result.setThry(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_TH_PARMETER.field_thry)));
						result.setThsj(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_TH_PARMETER.field_thsj)));
						result.setThyy(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.PATROLS_TH_PARMETER.field_thyy)));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return result;
	}

	public static void insertStNumDbDatas(Context context, StNum item,
			String user) {
		try {
			String sql = "INSERT INTO STNUM VALUES(?,?,?,?,?,?)";
			String isShowDetails = "false";
			if (item.isShowDetails()) {
				isShowDetails = "true";
			}
			String[] bindArgs = new String[] { item.getId(), user,
					item.getName(), item.getNum() + "", item.getQuid(),
					isShowDetails };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deleteSTNUMDbDatasByUser(Context context, String user) {
		try {
			String sql = "DELETE FROM STNUM where user='" + user + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static List<StNum> getStNumByUser(Context context, String user) {
		String sql;
		Cursor cursor = null;
		List<StNum> itemList = new ArrayList<StNum>();
		StNum item;
		try {
			sql = "select * from STNUM where user='" + user + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						boolean isShowDetails = false;
						if (cursor
								.getString(
										cursor.getColumnIndex(ParmeterTable.STNUM_PARMETER.field_isShowDetails))
								.equals("true")) {
							isShowDetails = true;
						}
						item = new StNum(
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.STNUM_PARMETER.field_id)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.STNUM_PARMETER.field_name)),
								Integer.parseInt(cursor.getString(cursor
										.getColumnIndex(ParmeterTable.STNUM_PARMETER.field_num))),
								isShowDetails,
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.STNUM_PARMETER.field_quid)));
						itemList.add(item);
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return itemList;
	}

	public static void insertXZQHNumDbDatasWithAjKey(Context context,
			XZQHNum_Wpzf item, String user, String ajid, String xzqyid) {
		try {
			String sql = "INSERT INTO XZQHNUM1 VALUES(?,?,?,?,?,?,?,?,?,?)";
			String isHasSub = "false";
			if (item.isHasSub()) {
				isHasSub = "true";
			}
			String[] bindArgs = new String[] { item.getId(), user, ajid,
					xzqyid, item.getPid(), item.getName(), item.getNum() + "",
					item.getXzqudm(), item.getAjKey(), isHasSub };
			DBHelper.getDbHelper(context).getReadableDatabase().execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	//add teng.guo start
	public static void replaceXZQHNumDbDatasWithAjKey(Context context,
			XZQHNum_Wpzf item, String user, String ajid, String xzqyid) {
		try {
			String sql = "REPLACE INTO XZQHNUM1 VALUES(?,?,?,?,?,?,?,?,?,?)";
			String isHasSub = "false";
			if (item.isHasSub()) {
				isHasSub = "true";
			}
			String[] bindArgs = new String[] { item.getId(), user, ajid,
					xzqyid, item.getPid(), item.getName(), item.getNum() + "",
					item.getXzqudm(), item.getAjKey(), isHasSub };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	//add teng.guo end

	public static void deleteXZQHNumDbDatasByUserAndAjAndAjKey(Context context,
			String user, String ajid, String ajKey) {
		try {
			String sql = "DELETE FROM XZQHNUM1 where user='" + user
					+ "' and ajid='" + ajid + "' and ajKey='" + ajKey + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deleteXZQHNumDbDatasByXzqhAndAjAndAjKey(Context context,
			String xzqhid, String ajid, String ajKey) {
		try {
			String sql = "DELETE FROM XZQHNUM1 where xzqhid='" + xzqhid
					+ "' and ajid='" + ajid + "' and ajKey='" + ajKey + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static List<XZQHNum_Wpzf> getXZQHNumByUserAndAjAndAjKey(
			Context context, String user, String ajid, String ajKey) {
		String sql;
		Cursor cursor = null;
		List<XZQHNum_Wpzf> itemList = new ArrayList<XZQHNum_Wpzf>();
		XZQHNum_Wpzf item;
		try {
			sql = "select * from XZQHNUM1 where user='" + user + "' and ajid='"
					+ ajid + "' and ajKey='" + ajKey + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						boolean hasSub = false;
						if (cursor
								.getString(
										cursor.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_hasSub))
								.equals("true")) {
							hasSub = true;
						}
						item = new XZQHNum_Wpzf();
						item.setName(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_name)));
						item.setNum(Integer.parseInt(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_num))));
						item.setId(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_id)));
						item.setPid(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_pid)));
						item.setHasSub(hasSub);
						item.setXzqudm(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_xzqudm)));
						item.setAjKey(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_ajKey)));
						itemList.add(item);
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return itemList;
	}

	public static List<Object> getXZQHNumByXzquAndAjAndAjKey(Context context,
			String xzqhid, String ajid, String ajKey) {
		String sql;
		Cursor cursor = null;
		List<Object> itemList = new ArrayList<Object>();
		XZQHNum_Wpzf item;
		try {
			sql = "select * from XZQHNUM1 where xzqhid='" + xzqhid
					+ "' and ajid='" + ajid + "' and ajKey='" + ajKey + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						boolean hasSub = false;
						if (cursor
								.getString(
										cursor.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_hasSub))
								.equals("true")) {
							hasSub = true;
						}
						item = new XZQHNum_Wpzf();
						item.setName(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_name)));
						item.setNum(Integer.parseInt(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_num))));
						item.setId(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_id)));
						item.setPid(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_pid)));
						item.setHasSub(hasSub);
						item.setXzqudm(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_xzqudm)));
						item.setAjKey(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM1_PARMETER.field_ajKey)));
						itemList.add(item);
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return itemList;
	}

	public static void insertXZQHNumDbDatas(Context context, XZQHNum item,
			String user, String ajid, String xzqyid) {
		try {
			String sql = "INSERT INTO XZQHNUM VALUES(?,?,?,?,?,?,?,?,?)";
			String isHasSub = "false";
			if (item.isHasSub()) {
				isHasSub = "true";
			}
			String[] bindArgs = new String[] { item.getId(), user, ajid,
					xzqyid, item.getPid(), item.getName(), item.getNum() + "",
					item.getXzqudm(), isHasSub };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deleteXZQHNumDbDatasByUserAndAj(Context context,
			String user, String ajid) {
		try {
			String sql = "DELETE FROM XZQHNUM where user='" + user
					+ "' and ajid='" + ajid + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deleteXZQHNumDbDatasByXzqhAndAj(Context context,
			String xzqhid, String ajid) {
		try {
			String sql = "DELETE FROM XZQHNUM where xzqhid='" + xzqhid
					+ "' and ajid='" + ajid + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static List<XZQHNum> getXZQHNumByUserAndAj(Context context,
			String user, String ajid) {
		String sql;
		Cursor cursor = null;
		List<XZQHNum> itemList = new ArrayList<XZQHNum>();
		XZQHNum item;
		try {
			sql = "select * from XZQHNUM where user='" + user + "' and ajid='"
					+ ajid + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						boolean hasSub = false;
						if (cursor
								.getString(
										cursor.getColumnIndex(ParmeterTable.XZQHNUM_PARMETER.field_hasSub))
								.equals("true")) {
							hasSub = true;
						}
						item = new XZQHNum();
						item.setName(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM_PARMETER.field_name)));
						item.setNum(Integer.parseInt(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM_PARMETER.field_num))));
						item.setId(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM_PARMETER.field_id)));
						item.setPid(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM_PARMETER.field_pid)));
						item.setHasSub(hasSub);
						item.setXzqudm(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM_PARMETER.field_xzqudm)));
						itemList.add(item);
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return itemList;
	}

	public static List<Object> getXZQHNumByXzquAndAj(Context context,
			String xzqhid, String ajid) {
		String sql;
		Cursor cursor = null;
		List<Object> itemList = new ArrayList<Object>();
		XZQHNum item;
		try {
			sql = "select * from XZQHNUM where xzqhid='" + xzqhid
					+ "' and ajid='" + ajid + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						boolean hasSub = false;
						if (cursor
								.getString(
										cursor.getColumnIndex(ParmeterTable.XZQHNUM_PARMETER.field_hasSub))
								.equals("true")) {
							hasSub = true;
						}
						item = new XZQHNum();
						item.setName(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM_PARMETER.field_name)));
						item.setNum(Integer.parseInt(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM_PARMETER.field_num))));
						item.setId(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM_PARMETER.field_id)));
						item.setPid(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM_PARMETER.field_pid)));
						item.setHasSub(hasSub);
						item.setXzqudm(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.XZQHNUM_PARMETER.field_xzqudm)));
						itemList.add(item);
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return itemList;
	}

	public static void insertSimpleAjDbDatas(Context context, SimpleAj item,
			String ajid, String xzqyid) {
		try {
			String sql = "INSERT INTO SIMPLEAJ VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String[] bindArgs = new String[] { item.getId(), ajid, xzqyid,
					item.getBh(), item.getXxdz(), item.getSj(), item.getAjly(),
					item.getHx(), item.getX() + "", item.getY() + "",
					item.getHxfxjg(), item.getJcbh(), item.getIsSave() };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void insertSimpleAjDbDatasWithAjKey(Context context,
			SimpleAj_Wpzf item, String ajid, String xzqyid) {
		try {
			String sql = "INSERT INTO SIMPLEAJ1 VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String[] bindArgs = new String[] { item.getId(), ajid, xzqyid,
					item.getBh(), item.getXxdz(), item.getSj(), item.getAjly(),
					item.getHx(), item.getX() + "", item.getY() + "",
					item.getHxfxjg(), item.getJcbh(), item.getAjKey(),
					item.getIsSave(), item.isRevoke(), item.isApprover(),
					item.getLastState() };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	//add teng.guo start 
	public static void replaceSimpleAjDbDatasWithAjKey(Context context,
			SimpleAj_Wpzf item, String ajid, String xzqyid) {
		try {
			String sql = "REPLACE INTO SIMPLEAJ1 VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String[] bindArgs = new String[] { item.getId(), ajid, xzqyid,
					item.getBh(), item.getXxdz(), item.getSj(), item.getAjly(),
					item.getHx(), item.getX() + "", item.getY() + "",
					item.getHxfxjg(), item.getJcbh(), item.getAjKey(),
					item.getIsSave(), item.isRevoke(), item.isApprover(),
					item.getLastState() };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	//add teng.guo end 

	public static void insertSimpleAj2DbDatas(Context context, SimpleAj2 item) {
		try {
			String sql = "INSERT INTO SIMPLEAJ2 VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String[] bindArgs = new String[] { item.getId(), item.getAjid(),
					item.getXzqyid(), item.getBh(), item.getXxdz(),
					item.getSj(), item.getAjly(), item.getHx(),
					item.getX() + "", item.getY() + "", item.getHxfxjg(),
					item.getJcbh(), item.getIsSave() };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deleteSimpleAjDbDatasByXzqhAndAj(Context context,
			String xzqhid, String ajid) {
		try {
			String sql = "DELETE FROM SIMPLEAJ where xzqhid='" + xzqhid
					+ "' and ajid='" + ajid + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deleteSimpleAjDbDatasByXzqhAndAjAndAjKey(
			Context context, String xzqhid, String ajid, String ajKey) {
		try {
			String sql = "DELETE FROM SIMPLEAJ1 where xzqhid='" + xzqhid
					+ "' and ajid='" + ajid + "' and ajKey='" + ajKey + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deleteSimpleAj2DbDatasById(Context context, String id) {
		try {
			String sql = "DELETE FROM SIMPLEAJ2 where id='" + id + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static SimpleAj2 getSimpleAj2ById(Context context, String id) {
		String sql;
		Cursor cursor = null;
		SimpleAj2 item = null;
		try {
			sql = "select * from SIMPLEAJ2 where id='" + id + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						item = new SimpleAj2();
						item.setId(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_id)));
						item.setBh(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_bh)));
						item.setXxdz(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_xxdz)));
						item.setSj(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_sj)));
						item.setAjly(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_ajly)));
						item.setHx(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_hx)));
						item.setX(Double.parseDouble(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_x))));
						item.setY(Double.parseDouble(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_y))));
						item.setHxfxjg(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_hxfxjg)));
						item.setJcbh(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_jcbh)));
						item.setIsSave(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_isSave)));
						item.setIsSave("false");
						item.setXzqyid(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_xzqhid)));
						item.setAjid(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ2_PARMETER.field_ajid)));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return item;
	}

	public static List<Object> getSimpleAjByXzquAndAjAndAjKey(Context context,
			String xzqhid, String ajid, String ajKey) {
		String sql;
		Cursor cursor = null;
		List<Object> itemList = new ArrayList<Object>();
		SimpleAj_Wpzf item;
		try {
			sql = "select * from SIMPLEAJ1 where xzqhid='" + xzqhid
					+ "' and ajid='" + ajid + "' and ajKey='" + ajKey + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						item = new SimpleAj_Wpzf();
						item.setId(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_id)));
						item.setBh(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_bh)));
						item.setXxdz(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_xxdz)));
						item.setSj(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_sj)));
						item.setAjly(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_ajly)));
						item.setHx(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_hx)));
						item.setX(Double.parseDouble(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_x))));
						item.setY(Double.parseDouble(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_y))));
						item.setHxfxjg(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_hxfxjg)));
						item.setJcbh(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_jcbh)));
						item.setIsSave(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_isSave)));
						item.setAjKey(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_ajKey)));
						item.setRevoke(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_isRevoke)));
						item.setApprover(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_isApprover)));
						item.setLastState(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_lastState)));
						try {
							InspectionEdit inspectionEdit = DbUtil
									.getInspectionEditByBh(
											context,
											cursor.getString(cursor
													.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_id)));
							if (inspectionEdit != null) {
								item.setIsSave("true");
							} else {
								item.setIsSave("false");
							}
						} catch (Exception e) {
						}
						itemList.add(item);
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return itemList;
	}

	public static SimpleAj_Wpzf_find getSimpleAjById(Context context, String id) {
		String sql;
		Cursor cursor = null;
		SimpleAj_Wpzf item = null;
		String quid = null;
		try {
			sql = "select * from SIMPLEAJ1 where id='" + id + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						item = new SimpleAj_Wpzf();
						item.setId(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_id)));
						quid = cursor
								.getString(cursor
										.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_xzqhid));
						item.setBh(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_bh)));
						item.setXxdz(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_xxdz)));
						item.setSj(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_sj)));
						item.setAjly(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_ajly)));
						item.setHx(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_hx)));
						item.setX(Double.parseDouble(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_x))));
						item.setY(Double.parseDouble(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_y))));
						item.setHxfxjg(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_hxfxjg)));
						item.setJcbh(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_jcbh)));
						item.setIsSave(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_isSave)));
						item.setAjKey(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_ajKey)));
						item.setRevoke(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_isRevoke)));
						item.setApprover(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_isApprover)));
						item.setLastState(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_lastState)));
						try {
							InspectionEdit inspectionEdit = DbUtil
									.getInspectionEditByBh(
											context,
											cursor.getString(cursor
													.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_id)));
							if (inspectionEdit != null) {
								item.setIsSave("true");
							} else {
								item.setIsSave("false");
							}
						} catch (Exception e) {
						}
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		if (quid != null && item != null) {
			return new SimpleAj_Wpzf_find(quid, item);
		} else {
			return null;
		}
	}

	public static List<Object> getSimpleAjByXzquAndAj(Context context,
			String xzqhid, String ajid) {
		String sql;
		Cursor cursor = null;
		List<Object> itemList = new ArrayList<Object>();
		SimpleAj item;
		try {
			sql = "select * from SIMPLEAJ where xzqhid='" + xzqhid
					+ "' and ajid='" + ajid + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						item = new SimpleAj();
						item.setId(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_id)));
						item.setBh(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_bh)));
						item.setXxdz(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_xxdz)));
						item.setSj(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_sj)));
						item.setAjly(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_ajly)));
						item.setHx(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_hx)));
						item.setX(Double.parseDouble(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_x))));
						item.setY(Double.parseDouble(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_y))));
						item.setHxfxjg(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_hxfxjg)));
						item.setJcbh(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_jcbh)));
						item.setIsSave(cursor.getString(cursor
								.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_isSave)));
						try {
							InspectionEdit inspectionEdit = DbUtil
									.getInspectionEditByBh(
											context,
											cursor.getString(cursor
													.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_id)));
							if (inspectionEdit != null) {
								item.setIsSave("true");
							} else {
								item.setIsSave("false");
							}
						} catch (Exception e) {
						}
						itemList.add(item);
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return itemList;
	}

	public static List<Hx> getHxsByXzquAndAj_Wpzf(Context context,
			String xzqhid, String ajid) {
		String sql;
		Cursor cursor = null;
		List<Hx> itemList = new ArrayList<Hx>();
		try {
			sql = "select * from SIMPLEAJ1 where xzqhid='" + xzqhid
					+ "' and ajid='" + ajid + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						itemList.add(new Hx(
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_hx)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_jcbh)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.SIMPLEAJ1_PARMETER.field_ajKey))));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return itemList;
	}

	public static List<Hx> getHxsByXzquAndAj(Context context, String xzqhid,
			String ajid) {
		String sql;
		Cursor cursor = null;
		List<Hx> itemList = new ArrayList<Hx>();
		try {
			sql = "select * from SIMPLEAJ where xzqhid='" + xzqhid
					+ "' and ajid='" + ajid + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						itemList.add(new Hx(
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_hx)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.SIMPLEAJ_PARMETER.field_jcbh)),null));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return itemList;
	}

	public static void insertINSPECTIONEDITDbDatas(Context context, String bh,
			String wfztlb, String wfztmc, String phone, String wjlx,
			String wfxz, String wfydmj, String dl, String cgsm, String hcry,
			String hcsj) {
		try {
			String sql = "INSERT INTO INSPECTIONEDIT VALUES(?,?,?,?,?,?,?,?,?,?,?)";
			String[] bindArgs = new String[] { bh, wfztlb, wfztmc, phone, wjlx,
					wfxz, wfydmj, dl, cgsm, hcry, hcsj };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	public static void insertINSPECTIONEDITJSDTDbDatas(Context context, String bh,
			String key, String value) {
		try {
			String sql = "INSERT INTO INSPECTIONEDIT_JSDT VALUES(?,?,?)";
			String[] bindArgs = new String[] { bh, key, value };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	public static void insertJSDTDbDatas(Context context,
			String key, String value) {
		try {
			String sql = "INSERT INTO JSDT VALUES(?,?)";
			String[] bindArgs = new String[] {key, value };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	public static void deleteINSPECTIONEDITJSDTDbDatasByBh(Context context,
			String bh) {
		try {
			String sql = "DELETE FROM INSPECTIONEDIT_JSDT where bh='" + bh + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	public static void deleteJSDTDbDatas(Context context) {
		try {
			String sql = "DELETE FROM JSDT ";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deleteINSPECTIONEDITDbDatasByBh(Context context,
			String bh) {
		try {
			String sql = "DELETE FROM INSPECTIONEDIT where bh='" + bh + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}
	
	public static List<Jsdt> getJsdts(Context context) {
		String sql;
		Cursor cursor = null;
		List<Jsdt> itemList = null;
		try {
			sql = "select * from JSDT ";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				itemList = new ArrayList<Jsdt>();
				while (cursor.moveToNext()) {
					try {
						Jsdt item = new Jsdt(
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.JSDT_PARMETER.field_key)),
										cursor.getString(cursor
												.getColumnIndex(ParmeterTable.JSDT_PARMETER.field_value)));
						itemList.add(item);
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return itemList;
	}
	
	public static Jsdt getJsdtByKeyFromJsdts(Context context,
			String key) {
		String sql;
		Cursor cursor = null;
		Jsdt item = null;
		try {
			sql = "select * from JSDT where key='" + key + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						item = new Jsdt(
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.JSDT_PARMETER.field_key)),
										cursor.getString(cursor
												.getColumnIndex(ParmeterTable.JSDT_PARMETER.field_value)));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return item;
	}
	
	public static Jsdt getJsdtByBh(Context context,
			String bh) {
		String sql;
		Cursor cursor = null;
		Jsdt item = null;
		try {
			sql = "select * from INSPECTIONEDIT_JSDT where bh='" + bh + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						item = new Jsdt(
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONEDIT_JSDT_PARMETER.field_key)),
										cursor.getString(cursor
												.getColumnIndex(ParmeterTable.INSPECTIONEDIT_JSDT_PARMETER.field_value)));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return item;
	}

	public static InspectionEdit getInspectionEditByBh(Context context,
			String bh) {
		String sql;
		Cursor cursor = null;
		InspectionEdit item = null;
		try {
			sql = "select * from INSPECTIONEDIT where bh='" + bh + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						item = new InspectionEdit(
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONEDIT_PARMETER.field_bh)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONEDIT_PARMETER.field_wfztlb)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONEDIT_PARMETER.field_wfztmc)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONEDIT_PARMETER.field_phone)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONEDIT_PARMETER.field_wjlx)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONEDIT_PARMETER.field_wfxz)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONEDIT_PARMETER.field_wfydmj)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONEDIT_PARMETER.field_dl)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONEDIT_PARMETER.field_cgsm)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONEDIT_PARMETER.field_hcry)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONEDIT_PARMETER.field_hcsj)));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return item;
	}

	public static void insertINSPECTIONGETTASKDbDatas(Context context,
			String bh, String redline, String x, String y, String ajly,
			String dz, String entiryString) {
		try {
			String sql = "INSERT INTO INSPECTIONGETTASK VALUES(?,?,?,?,?,?,?)";
			String[] bindArgs = new String[] { bh, redline, x, y, ajly, dz,
					entiryString };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deleteINSPECTIONGETTASKDbDatasByBh(Context context,
			String bh) {
		try {
			String sql = "DELETE FROM INSPECTIONGETTASK where bh='" + bh + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static InspectionGetTask getInspectionGetTaskByBh(Context context,
			String bh) {
		String sql;
		Cursor cursor = null;
		InspectionGetTask item = null;
		try {
			sql = "select * from INSPECTIONGETTASK where bh='" + bh + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase()
					.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						item = new InspectionGetTask(
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK_PARMETER.field_bh)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK_PARMETER.field_redline)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK_PARMETER.field_x)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK_PARMETER.field_y)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK_PARMETER.field_ajly)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK_PARMETER.field_dz)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK_PARMETER.field_entiryString)));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return item;
	}

	public static void insertINSPECTIONGETTASK2DbDatas(Context context,
			String bh, String redline, String x, String y, String ajly,
			String dz, String jcbh, String jcmj, String zygdmj, String xfsj,
			String entiryString) {
		try {
			String sql = "INSERT INTO INSPECTIONGETTASK2 VALUES(?,?,?,?,?,?,?,?,?,?,?)";
			String[] bindArgs = new String[] { bh, redline, x, y, ajly, dz,
					jcbh, jcmj, zygdmj, xfsj, entiryString };
			DBHelper.getDbHelper(context).getReadableDatabase()
					.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static void deleteINSPECTIONGETTASK2DbDatasByBh(Context context,
			String bh) {
		try {
			String sql = "DELETE FROM INSPECTIONGETTASK2 where bh='" + bh + "'";
			SQLiteDatabase db = DBHelper.getDbHelper(context)
					.getReadableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
	}

	public static InspectionGetTask2 getInspectionGetTask2ByBh(Context context,
			String bh) {
		String sql;
		Cursor cursor = null;
		InspectionGetTask2 item = null;
		try {
			sql = "select * from INSPECTIONGETTASK2 where bh='" + bh + "'";
			cursor = DBHelper.getDbHelper(context).getReadableDatabase().rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					try {
						item = new InspectionGetTask2(
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK2_PARMETER.field_bh)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK2_PARMETER.field_redline)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK2_PARMETER.field_x)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK2_PARMETER.field_y)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK2_PARMETER.field_ajly)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK2_PARMETER.field_dz)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK2_PARMETER.field_jcbh)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK2_PARMETER.field_jcmj)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK2_PARMETER.field_zygdmj)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK2_PARMETER.field_xfsj)),
								cursor.getString(cursor
										.getColumnIndex(ParmeterTable.INSPECTIONGETTASK2_PARMETER.field_entiryString)));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (DBHelper.getDbHelper(context) != null) {
				DBHelper.destroy();
			}
		}
		return item;
	}
	
}
