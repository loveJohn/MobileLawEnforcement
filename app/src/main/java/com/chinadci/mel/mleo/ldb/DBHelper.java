package com.chinadci.mel.mleo.ldb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chinadci.mel.android.core.ExpKeyValue;
import com.chinadci.mel.android.core.KeyValue;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.Dbs;

/**
 * 
 * @ClassName DBHelper
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:31:03
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	// SQL创建用户表
	final String CREATE_TABLE_USERS = "CREATE TABLE [USERS] ([id] INTEGER PRIMARY KEY, [name] VARCHAR2(25) NOT NULL UNIQUE, [chName] VARCHAR2(50), [sex] VARCHAR2(25) DEFAULT 0, [age] INTEGER, [tel] VARCHAR2(15), [photo] BLOB(100), [role] VARCHAR2(50),  [territory] VARCHAR2(30),[rights] TEXT)";
	// SQL创建参数同步数据表
	final String CREATE_TABLE_ASYN = "CREATE TABLE [SYNC] ([id] INTEGER PRIMARY KEY, [name] VARCHAR2(50), [time] DATETEXT(25))";
	// SQL创建参数初判地类数据表
	final String CREATE_TABLE_LANDUSAGE = "CREATE TABLE [LAND_USAGE] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL,[note] VARCHAR2(100))";
	// SQL创建参数巡查结果数据表
	final String CREATE_TABLE_DEALRESULT = "CREATE TABLE [DEAL_RESULT] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL,[note] VARCHAR2(100));";
	// SQL创建参数线索来源数据表
	final String CREATE_TABLE_CLUESOURCE = "CREATE TABLE [CLUE_SOURCE] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL,[note] VARCHAR2(100))";
	// SQL创建参数项目类型数据表
	final String CREATE_TABLE_PROJTYPE = "CREATE TABLE [PROJ_TYPE] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL,[note] VARCHAR2(100))";
	// SQL创建参数违建类型数据表
	final String CREATE_TABLE_ILLEGALTYPE = "CREATE TABLE [ILLEGAL_TYPE] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL,[note] VARCHAR2(100))";
	// SQL创建参数违法状态数据表
	final String CREATE_TABLE_ILLEGALSTATUS = "CREATE TABLE [ILLEGAL_STATUS] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL, [note] VARCHAR2(100))";
	// SQL创建参数违法主体数据表
	final String cREATE_TABLE_ILLEGALSUBJECT = "CREATE TABLE [ILLEGAL_SUBJECT] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL, [note] VARCHAR2(100))";
	// SQL创建参数核查结果数据表
	final String CREATE_TABLE_INSPECTRESULT = "CREATE TABLE [INSPECT_RESULT] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL, [note] VARCHAR2(100))";
	// SQL创建参数案件紧急程度数据表
	final String CREATE_TABLE_CASEURGENCY = "CREATE TABLE [CASE_URGENCY] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL,[note] VARCHAR2(100))";
	// SQL创建参数案件来源数据表
	final String CREATE_TABLE_CASESOURCE = "CREATE TABLE [CASE_SOURCE] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL,[note] VARCHAR2(100));";
	// SQL创建参数行政区划数据表
	final String CREATE_TABLE_ADMINREGION = "CREATE TABLE [ADMIN_REGION] ([code] VARCHAR2(25) NOT NULL UNIQUE, [name] VARCHAR2(100) NOT NULL, [parentCode] VARCHAR2(25), [shape] TEXT, [centre] TEXT, [minX] NUMERIC, [minY] NUMERIC, [maxX] NUMERIC, [maxY] NUMERIC, [level] INT);";
	// SQL创建参数调查结果数据表
	final String CREATE_TABLE_SURVEYRESULT = "CREATE TABLE [SURVEY_RESULT] ([id] VARCHAR2(50) NOT NULL UNIQUE, [name] VARCHAR2(100) NOT NULL, [note] TEXT)";
	// SQL创建本地案件数据表
	final String CREATE_TABLE_LOCALCASE = "CREATE TABLE [LOCAL_CASE] ([id] VARCHAR2(50) NOT NULL UNIQUE,[user] VARCHAR2(25),[parties] VARCHAR2(50),[source] VARCHAR2(100),[projType] VARCHAR2(100),[illegalSubject] VARCHAR2(100),[illegalType] VARCHAR2(100),[illegalStatus] VARCHAR2(100),[illegalArea] NUMBER,[landUsage] VARCHAR2(100),[address] VARCHAR2(100),[notes] TEXT,[redline] TEXT,[mTime] DATETEXT(25),[admin] VARCHAR2(200),[x] NUMERIC,[y] NUMERIC,[status] INTEGER DEFAULT 0)";
	// SQL创建案件巡查数据表
	final String CREATE_TABLE_CASEPATROL = "CREATE TABLE [CASE_PATROL] ([id] VARCHAR2(25),[caseId] VARCHAR2(50) NOT NULL,[user] VARCHAR2(50),[redline] TEXT,[mTime] DATETEXT(50),[stopNotice] TEXT,[stopInfo] TEXT,[pullPlan] TEXT,[pullInfo] TEXT,[regCase] TEXT,[deal] VARCHAR2(50),[stopNoticeNo] VARCHAR2,[stopNoticeDate] DATETEXT,[pullPlanDate] DATETEXT,[pullPlanNum] NUMBER,[pullPlanPerson] VARCHAR2(100),[caseDocumentNo] VARCHAR2(100),[govDate] DATETEXT(50),[caseDocumentDate] DATETEXT(50),[notes] TEXT,[status] INTEGER(1) DEFAULT 2,[sendDate] DATETEXT(50))";
	// SQL创建案件核查数据表
	final String CREATE_TABLE_CASEINSPECTION = "CREATE TABLE [CASE_INSPECTION] ([id] VARCHAR2(25) UNIQUE,[caseId] VARCHAR2(100) NOT NULL,[user] VARCHAR2(100),[parties] VARCHAR2(100),[tel] VARCHAR2(20),[illegalSubject] VARCHAR2(100),[illegalType] VARCHAR2(50),[illegalStatus] VARCHAR2(50),[illegalArea] NUMBER,[landUsage] VARCHAR2(50),[inspectResult] VARCHAR2(50),[notes] TEXT,[mTime] DATETEXT(25),[status] INTEGER(1) DEFAULT 0)";
	// SQL创建案件基础信息数据表
	final String CREATE_TABLE_INSPECTIONCASE = "CREATE TABLE [INSPECTION_CASE] ([id] VARCHAR2(50) NOT NULL,[user] VARCHAR2(50),[parties] VARCHAR2(50),[source] VARCHAR2(100),[projType] VARCHAR2(100),[illegalSubject] VARCHAR2(100),[illegalType] VARCHAR2(100),[illegalStatus] VARCHAR2(100),[illegalArea] NUMBER,[landUsage] VARCHAR2(100),[address] VARCHAR2(100),[notes] TEXT,[redline] TEXT,[mTime] DATETEXT(25),[location] TEXT,[bh] VARCHAR2(50),[status] INTEGER,[statusText] VARCHAR2(100),[inUser] VARCHAR2(50),[analysis] TEXT)";
	// SQL创建案件附件数据表
	final String CREATE_TABLE_CASEANNEXES = "CREATE TABLE [CASE_ANNEXES] ([id] INTEGER PRIMARY KEY,[caseId] VARCHAR2(50),[tagId] VARCHAR2(50),[tag] VARCHAR2(50),[name] VARCHAR2(200),[path] VARCHAR2(200),[uri] VARCHAR2(25))";
	// SQL插入行政区数据表原始数据
	final String INSERT_ADMIN_ONSET = "INSERT INTO [ADMIN_REGION](code,name)VALUES('350000','福建省')";

	// 创建本地非法开矿巡查日志附件数据表
	final String CREATE_TABLE_MILPATROLAX = "CREATE TABLE [MIL_PATROL_ANNEXES] ([id] INTEGER PRIMARY KEY,[caseId] VARCHAR2(50),[tagId] VARCHAR2(50),[tag] VARCHAR2(50),[name] VARCHAR2(200),[path] VARCHAR2(200),[uri] VARCHAR2(25))";
	// SQL创建案件跟踪案件信息数据表
	final String CREATE_TABLE_TRACKCASE = "CREATE TABLE [TRACK_CASE] ([id] VARCHAR2(50) NOT NULL,[user] VARCHAR2(50),[parties] VARCHAR2(50),[source] VARCHAR2(100),[projType] VARCHAR2(100),[illegalSubject] VARCHAR2(100),[illegalType] VARCHAR2(100),[illegalStatus] VARCHAR2(100),[illegalArea] NUMBER,[landUsage] VARCHAR2(100),[address] VARCHAR2(100),[notes] TEXT,[redline] TEXT,[mTime] DATETEXT(25),[location] TEXT,[bh] VARCHAR2(50),[inUser] VARCHAR2(50),[status] INTEGER,[statusText] VARCHAR2(100),[analysis] TEXT)";
	
	final String CREATE_TABLE_TRACKCASE_2 = "CREATE TABLE [TRACK_CASE_2] ([id] VARCHAR2(50) NOT NULL,[user] VARCHAR2(50),[parties] VARCHAR2(50),[source] VARCHAR2(100),[projType] VARCHAR2(100),[illegalSubject] VARCHAR2(100),[illegalType] VARCHAR2(100),[illegalStatus] VARCHAR2(100),[illegalArea] NUMBER,[landUsage] VARCHAR2(100),[address] VARCHAR2(100),[notes] TEXT,[redline] TEXT,[mTime] DATETEXT(25),[location] TEXT,[bh] VARCHAR2(50),[inUser] VARCHAR2(50),[status] INTEGER,[statusText] VARCHAR2(100),[analysis] TEXT)";
	
	// SQL创建案件跟踪附件数据表
	final String CREATE_TABLE_TRACKANNEXES = "CREATE TABLE [TRACK_ANNEXES] ([id] INTEGER PRIMARY KEY,[caseId] VARCHAR2(50),[tagId] VARCHAR2(50),[tag] VARCHAR2(50),[name] VARCHAR2(200),[path] VARCHAR2(200),[uri] VARCHAR2(200))";
	// SQL创建案件跟踪巡查数据表
	final String CREATE_TABLE_TRACKPATROL = "CREATE TABLE [TRACK_PATROL] ([id] VARCHAR2(25),[caseId] VARCHAR2(50) NOT NULL,[user] VARCHAR2(50),[redline] TEXT,[mTime] DATETEXT(50),[stopNotice] TEXT,[stopInfo] TEXT,[pullPlan] TEXT,[pullInfo] TEXT,[regCase] TEXT,[deal] VARCHAR2(50),[stopNoticeNo] VARCHAR2,[stopNoticeDate] DATETEXT,[pullPlanDate] DATETEXT,[pullPlanNum] NUMBER,[pullPlanPerson] VARCHAR2(100),[caseDocumentNo] VARCHAR2(100),[govDate] DATETEXT(50), [caseDocumentDate] DATETEXT(50),[notes] TEXT,[inUser] VARCHAR2(50))";
	// SQL创建案件跟踪案件核查信息表
	final String CREATE_TABLE_TRACKINSPECTION = "CREATE TABLE [TRACK_INSPECTION] ([id] VARCHAR2(25) UNIQUE,[caseId] VARCHAR2(100) NOT NULL,[user] VARCHAR2(100),[parties] VARCHAR2(100),[tel] VARCHAR2(20),[illegalSubject] VARCHAR2(100),[illegalType] VARCHAR2(50),[illegalStatus] VARCHAR2(50), [illegalArea] NUMBER,[landUsage] VARCHAR2(50),[inspectResult] VARCHAR2(50), [notes] TEXT,[status] INTEGER(1) DEFAULT 2,[mTime] DATETEXT(50))";
	// SQL创建案件跟踪案件信息数据表
	final String CREATE_TABLE_OVERSEERCASE = "CREATE TABLE [OVERSEER_CASE] ([id] VARCHAR2(50) NOT NULL,[user] VARCHAR2(50),[parties] VARCHAR2(50),[source] VARCHAR2(100),[projType] VARCHAR2(100),[illegalSubject] VARCHAR2(100),[illegalType] VARCHAR2(100),[illegalStatus] VARCHAR2(100),[illegalArea] NUMBER,[landUsage] VARCHAR2(100),[address] VARCHAR2(100),[notes] TEXT,[redline] TEXT,[mTime] DATETEXT(25),[location] TEXT,[bh] VARCHAR2(50),[inUser] VARCHAR2(50),[status] INTEGER,[statusText] VARCHAR2(100),[analysis] TEXT)";
	// SQL创建案件跟踪附件数据表
	final String CREATE_TABLE_OVERSEERANNEXES = "CREATE TABLE [OVERSEER_ANNEXES] ([id] INTEGER PRIMARY KEY,[caseId] VARCHAR2(50),[tagId] VARCHAR2(50),[tag] VARCHAR2(50),[name] VARCHAR2(200),[path] VARCHAR2(200),[uri] VARCHAR2(200))";
	// SQL创建案件跟踪巡查数据表
	final String CREATE_TABLE_OVERSEERPATROL = "CREATE TABLE [OVERSEER_PATROL] ([id] VARCHAR2(25),[caseId] VARCHAR2(50) NOT NULL,[user] VARCHAR2(50),[redline] TEXT,[mTime] DATETEXT(50),[stopNotice] TEXT,[stopInfo] TEXT,[pullPlan] TEXT,[pullInfo] TEXT,[regCase] TEXT,[deal] VARCHAR2(50),[stopNoticeNo] VARCHAR2,[stopNoticeDate] DATETEXT,[pullPlanDate] DATETEXT,[pullPlanNum] NUMBER,[pullPlanPerson] VARCHAR2(100),[caseDocumentNo] VARCHAR2(100),[govDate] DATETEXT(50),[caseDocumentDate] DATETEXT(50),[notes] TEXT,[inUser] VARCHAR2(50))";
	// SQL创建案件跟踪案件核查信息表
	final String CREATE_TABLE_OVERSEERINSPECTION = "CREATE TABLE [OVERSEER_INSPECTION] ([id] VARCHAR2(25) UNIQUE,[caseId] VARCHAR2(100) NOT NULL,[user] VARCHAR2(100),[parties] VARCHAR2(100),[tel] VARCHAR2(20),[illegalSubject] VARCHAR2(100),[illegalType] VARCHAR2(50),[illegalStatus] VARCHAR2(50), [illegalArea] NUMBER,[landUsage] VARCHAR2(50),[inspectResult] VARCHAR2(50), [notes] TEXT,[status] INTEGER(1) DEFAULT 2,[mTime] DATETEXT(50))";
	// 创建服务器附件缓存表
	final String CREATE_TABLE_WEBANNEX = "CREATE TABLE [WEB_ANNEX] ([id] INTEGER PRIMARY KEY,[caseId] VARCHAR2(50),[tagId] VARCHAR2(50),[tag] VARCHAR2(50),[name] VARCHAR2(200),[path] VARCHAR2(200),[uri] VARCHAR2(200))";
	// 创建本地附件缓存表
	final String CREATE_TABLE_LOCALANNEX = "CREATE TABLE [LOCAL_ANNEX] ([id] INTEGER PRIMARY KEY,[status] INTEGER DEFAULT 0,[caseId] VARCHAR2(50),[tagId] VARCHAR2(50),[tag] VARCHAR2(50),[name] VARCHAR2(200),[path] VARCHAR2(200));";

	// 创建案件状态缓存表
	final String CREATE_TABLE_CASESITUATION = "CREATE TABLE [CASE_SITUATION] ([id] INTEGER PRIMARY KEY,[caseId] VARCHAR2(50),[stopNotice] TEXT,[stopSituation] TEXT,[pullPlan] TEXT,[pullSituation] TEXT,[caseSurvey] TEXT,[arguments] TEXT);";

	// SQL 创建本地非法矿产巡查日志数据表
	final String CREATE_TABLE_MILPATROL = "CREATE TABLE [MIL_PATROL] ([id] VARCHAR2(25) NOT NULL,[user] VARCHAR2(50),[ffckbh] VARCHAR2(50),[zzwsbh] VARCHAR2(50),[haszz] INTEGER,[line] TEXT,[hasMining] INTEGER,[exception] TEXT,[notes] TEXT,[location] TEXT,[redline] TEXT NOT NULL,[logTime] DATETEXT,[xcrzxl] VARCHAR2(25),[szcj] VARCHAR2(50),[ajzt] VARCHAR2(50),[wfztmc] VARCHAR2(50))";
	// SQL 创建本地非法矿产核查日志数据表
	final String CREATE_TABLE_MILPATROLHC = "CREATE TABLE [MIL_HC] ([id] VARCHAR2(25) NOT NULL,[caseId] VARCHAR2(25),[hcrmc] VARCHAR2(50),[sfffckd] INTEGER,[wfztxz] INTEGER,[sftzffkc] INTEGER,[hcsj] DATETEXT,[wfztmc] VARCHAR2(500),[fkckz] VARCHAR2(50),[ffkcfs] VARCHAR2(50),[hccomment] TEXT,[status] INTEGER DEFAULT 0,[sfljqd] INTEGER)";
	// SQL创建非法采矿方式
	final String CREATE_TABLE_FFKCFS = "CREATE TABLE [MiL_FFKCFS] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL, [note] VARCHAR2(100))";
	// SQL创建非法矿种
	final String CREATE_TABLE_FKCKZ = "CREATE TABLE [MiL_FKCKZ] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL, [note] VARCHAR2(100))";
	// SQL创建非法采矿点
	final String CREATE_TABLE_FFCKD = "CREATE TABLE [Mil_FFCKD] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL,[pid] VARCHAR2(25) NOT NULL, [note] VARCHAR2(100))";
	// SQL创建非法采矿点
	final String CREATE_TABLE_WEBMILPATROL = "CREATE TABLE [WEBMINPATROL_CASE] ([id] VARCHAR2(25) NOT NULL,[user] VARCHAR2(50),[ffckbh] VARCHAR2(50),[zzwsbh] VARCHAR2(50),[haszz] INTEGER,[line] TEXT,[hasMining] INTEGER,[exception] TEXT,[notes] TEXT,[location] TEXT,[redline] TEXT NOT NULL,[logTime] DATETEXT,[inUser] VARCHAR2(25),[xcrzxl] VARCHAR2(25),[szcj] VARCHAR2(50),[ajzt] VARCHAR2(50),[wfztmc] VARCHAR2(50))";
	// SQL创建非法采矿点历史信息
	final String CREATE_TABLE_TRACEMINERAL = "CREATE TABLE [TRACk_MILERAL_PATROL] ([id] VARCHAR2(25) NOT NULL,[user] VARCHAR2(50),[ffckbh] VARCHAR2(50),[zzwsbh] VARCHAR2(50),[haszz] INTEGER,[line] TEXT,[hasMining] INTEGER,[exception] TEXT,[notes] TEXT,[location] TEXT,[redline] TEXT NOT NULL,[logTime] DATETEXT,[inUser] VARCHAR2(25),[xcrzxl] VARCHAR2(25),[szcj] VARCHAR2(50),[ajzt] VARCHAR2(50),[wfztmc] VARCHAR2(50),[status] VARCHAR2(50))";
	// SQL创建非法采矿点附件信息
	final String CREATE_TABLE_TRACKMINERALANNEXES = "CREATE TABLE [TRACK_MINERAL_ANNEXES] ([id] INTEGER PRIMARY KEY,[caseId] VARCHAR2(50),[tagId] VARCHAR2(50),[tag] VARCHAR2(50),[name] VARCHAR2(200),[path] VARCHAR2(200),[uri] VARCHAR2(200))";
	// SQL创建非法采矿点核查信息TRACK_MINERAL_ANNEXES
	final String CREATE_TABLE_TRACEMINERALHC = "CREATE TABLE [TRACK_MINERAL_HC] ([id] VARCHAR2(25) NOT NULL,[caseId] VARCHAR2(25),[hcrmc] VARCHAR2(50),[sfffckd] INTEGER,[wfztxz] INTEGER,[sftzffkc] INTEGER,[hcsj] DATETEXT,[wfztmc] VARCHAR2(500),[fkckz] VARCHAR2(50),[ffkcfs] VARCHAR2(50),[hccomment] TEXT,[status] INTEGER DEFAULT 0)";
	// SQL创建签到上传数据表
	final String CREATE_TABLE_SIGN = "CREATE TABLE [SIGN] ([id] VARCHAR2(25) NOT NULL, [user] VARCHAR2(25),[type] VARCHAR2(25),[time] DATETEXT(25),[x] NUMERIC,[y] NUMERIC,[accuracy] INTEGER,[admin] VARCHAR2(25),[address] VARCHAR2(500),[cause] VARCHAR2(500),[notes] TEXT,[status] INTEGER DEFAULT 0)";
	// SQL创建参数线索来源数据表
	final String CREATE_TABLE_SIGNCAUSE = "CREATE TABLE [SIGN_CAUSE] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL,[note] VARCHAR2(100))";

	/**
	 * 数据库版本升级,版本1升级到版本2; 在所有PATROL表中添加govDate字段
	 */
	final String ADD_GOVDATE_CASEPATROL = "ALTER TABLE [CASE_PATROL] ADD [govDate] DATETEXT(50)";
	final String ADD_GOVDATE_TRACKPATROL = "ALTER TABLE [TRACK_PATROL] ADD [govDate] DATETEXT(50)";
	final String ADD_GOVDATE_OVERSEERPATROL = "ALTER TABLE [OVERSEER_PATROL] ADD [govDate] DATETEXT(50)";

	/**
	 * 数据库版本升级,版本2升级到版本3;在所有INSPECTION表中添加[illegalArea] NUMBER
	 */
	final String ADD_AREA_CASEINSPECTION = "ALTER TABLE [CASE_INSPECTION] ADD [illegalArea] NUMBER";
	final String ADD_AREA_TRACKINSPECTION = "ALTER TABLE [TRACK_INSPECTION] ADD [illegalArea] NUMBER";
	final String ADD_AREA_OVERSEERINSPECTION = "ALTER TABLE [OVERSEER_INSPECTION] ADD [illegalArea] NUMBER";

	/**
	 * 数据库版本升级,版本4升级到5时;在所有案件信息表中添加 [analysis] TEXT存储一张图分析结果
	 */
	final String ADD_ANALYSIS_INSPECTIONCASE = "ALTER TABLE [INSPECTION_CASE] ADD [analysis] TEXT";
	final String ADD_ANALYSIS_OVERSEERCASE = "ALTER TABLE [OVERSEER_CASE] ADD [analysis] TEXT";
	final String ADD_ANALYSIS_TRACKCASE = "ALTER TABLE [TRACK_CASE] ADD [analysis] TEXT";
	final String ADD_ANALYSIS_TRACKCASE_2 = "ALTER TABLE [TRACK_CASE_2] ADD [analysis] TEXT";

	/**
	 * 数据库版本升级,版本5升级到6时;在所有案件信息表中添加 [sendDate] DATETEXT转发时间,与是否立即取缔
	 */
	final String ADD_SENDDATE_CASEPATROL = "ALTER TABLE [CASE_PATROL] ADD [sendDate] DATETEXT(50)";
	final String ADD_LJQD_MIL_HC = "ALTER TABLE [MIL_HC] ADD [sfljqd] INTEGER";
	
	 // SQL创建参数初判地类数据表
    public static final String CREATE_TABLE_LANDUSAGE_WP = "CREATE TABLE [LAND_USAGE_WP] ([id] VARCHAR2(25) NOT NULL UNIQUE,[name] VARCHAR2(100) NOT NULL,[sub] VARCHAR2(200),[note] VARCHAR2(100))";
 	
	
	static DBHelper instance;// 单例
	static Lock instanceLock = new ReentrantLock();// 单例锁
	static final String DB_NAME = "mango";// 数据库名
	static final int DB_VERSION = 8;// 数据库版本
	
	/**
	 * 
	 */
		

	/**
	 * 
	 * @Title: getDbHelper
	 * @Description: TODO
	 * @param context
	 * @return
	 * @throws
	 */
	public static DBHelper getDbHelper(Context context) {
		if (instance == null) {
			instanceLock.lock();
			try {
				instance = new DBHelper(context);
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				instanceLock.unlock();
			}
		}
		return instance;
	}

	/**
	 * 
	 * @Title: destroy
	 * @Description: TODO
	 * @throws
	 */
	public static void destroy() {
		if (instance != null) {
			instance.close();
			instance = null;
		}
	}

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// tables in version 1
		db.execSQL(CREATE_TABLE_USERS);
		db.execSQL(CREATE_TABLE_ASYN);
		db.execSQL(CREATE_TABLE_LANDUSAGE);
		db.execSQL(CREATE_TABLE_DEALRESULT);
		db.execSQL(CREATE_TABLE_ILLEGALTYPE);
		db.execSQL(CREATE_TABLE_ILLEGALSTATUS);
		db.execSQL(CREATE_TABLE_CASEURGENCY);
		db.execSQL(CREATE_TABLE_CASESOURCE);
		db.execSQL(CREATE_TABLE_ADMINREGION);
		db.execSQL(CREATE_TABLE_SURVEYRESULT);
		db.execSQL(CREATE_TABLE_LOCALCASE);
		db.execSQL(CREATE_TABLE_CASEPATROL);
		db.execSQL(CREATE_TABLE_CASEINSPECTION);
		db.execSQL(CREATE_TABLE_INSPECTIONCASE);
		db.execSQL(CREATE_TABLE_CASEANNEXES);
		db.execSQL(INSERT_ADMIN_ONSET);

		db.execSQL(CREATE_TABLE_MILPATROL);
		db.execSQL(CREATE_TABLE_MILPATROLHC);
		db.execSQL(CREATE_TABLE_FFKCFS);
		db.execSQL(CREATE_TABLE_FKCKZ);
		db.execSQL(CREATE_TABLE_FFCKD);
		db.execSQL(CREATE_TABLE_WEBMILPATROL);
		db.execSQL(CREATE_TABLE_TRACEMINERAL);
		db.execSQL(CREATE_TABLE_TRACKMINERALANNEXES);
		db.execSQL(CREATE_TABLE_TRACEMINERALHC);

		db.execSQL(CREATE_TABLE_MILPATROLAX);
		db.execSQL(CREATE_TABLE_TRACKCASE);
		db.execSQL(CREATE_TABLE_TRACKCASE_2);
		db.execSQL(CREATE_TABLE_TRACKANNEXES);
		db.execSQL(CREATE_TABLE_TRACKPATROL);
		db.execSQL(CREATE_TABLE_TRACKINSPECTION);
		db.execSQL(CREATE_TABLE_WEBANNEX);
		db.execSQL(CREATE_TABLE_LOCALANNEX);

		db.execSQL(CREATE_TABLE_CLUESOURCE);
		db.execSQL(cREATE_TABLE_ILLEGALSUBJECT);
		db.execSQL(CREATE_TABLE_INSPECTRESULT);
		db.execSQL(CREATE_TABLE_PROJTYPE);

		db.execSQL(CREATE_TABLE_OVERSEERANNEXES);
		db.execSQL(CREATE_TABLE_OVERSEERCASE);
		db.execSQL(CREATE_TABLE_OVERSEERINSPECTION);
		db.execSQL(CREATE_TABLE_OVERSEERPATROL);

		db.execSQL(CREATE_TABLE_CASESITUATION);
		db.execSQL(CREATE_TABLE_SIGN);
		db.execSQL(CREATE_TABLE_SIGNCAUSE);

		db.execSQL(CREATE_TABLE_LANDUSAGE_WP);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		/**
		 * 数据库版本升级；
		 * 版本2：CASE_PATROL/OVERSEER_PATROL/TRACK_PATROL三个表中添加字段govDate
		 * DATETEXT(50),存储报告政府时间;
		 */
		if (oldVersion < 2) {
			db.execSQL(ADD_GOVDATE_CASEPATROL);
			db.execSQL(ADD_GOVDATE_OVERSEERPATROL);
			db.execSQL(ADD_GOVDATE_TRACKPATROL);
		}

		/**
		 * 数据库版本升级；
		 * 版本3：CASE_INSPECTION/TRACK_INSPECTION/
		 * OVERSEER_INSPECTION三个表中添加字段[illegalArea] NUMBER,存储违法用地面积数据
		 */
		if (oldVersion < 3) {
			db.execSQL(ADD_AREA_CASEINSPECTION);
			db.execSQL(ADD_AREA_OVERSEERINSPECTION);
			db.execSQL(ADD_AREA_TRACKINSPECTION);
		}

		/**
		 * 数据库版本升级；
		 * 版本4： 添加矿产已办工作功能模块,数据库添加矿产已办工作附件表[TRACK_MINERAL_ANNEXES
		 * 矿产已办工作核查表[TRACK_MINERAL_HC]
		 */
		if (oldVersion < 4) {
			db.execSQL(CREATE_TABLE_TRACKMINERALANNEXES);
			db.execSQL(CREATE_TABLE_TRACEMINERALHC);
		}

		/**
		 * 数据库版本升级；
		 * 版本5：在INSPECTION_CASE/OVERSEER_CASE/TRACK_CASE/TRACK_CASE_2表中添加
		 * [analysis] TEXT存储一张图分析结果
		 * 
		 */
		if (oldVersion < 5) {
			db.execSQL(ADD_ANALYSIS_INSPECTIONCASE);
			db.execSQL(ADD_ANALYSIS_OVERSEERCASE);
			db.execSQL(ADD_ANALYSIS_TRACKCASE);
			db.execSQL(ADD_ANALYSIS_TRACKCASE_2);
		}

		/**
		 * 数据库版本升级;
		 * 版本6：添加SIGN、SIGN_CAUSE两个表存储签到信息与签到异常类型
		 */
		if (oldVersion < 6) {
			db.execSQL(CREATE_TABLE_SIGNCAUSE);
			db.execSQL(CREATE_TABLE_SIGN);
		}
		
		/**
		 * 数据库版本升级;
		 * 版本7：添加CASEPATROL两个表存储签到信息与签到异常类型
		 */
		if (oldVersion < 7) {
			db.execSQL(ADD_SENDDATE_CASEPATROL);
			db.execSQL(ADD_LJQD_MIL_HC);
		}
		
		if (oldVersion < 8) {
			db.execSQL(CREATE_TABLE_LANDUSAGE_WP);
		}
		
	}

	/**
	 * 
	 * @Title: queryCount
	 * @Description: TODO
	 * @param table
	 * @param columns
	 * @param selection
	 * @param args
	 * @return
	 * @throws Exception
	 * @throws
	 */
	public int queryCount(String table, String columns[], String selection,
			String args[]) throws Exception {
		try {
			int count = 0;
			Cursor cursor = getReadableDatabase().query(table, columns,
					selection, args, null, null, null);
			if (cursor != null) {
				count = cursor.getCount();
				cursor.close();
			}
			return count;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @Title: insert
	 * @Description: TODO
	 * @param table
	 * @param values
	 * @return
	 * @throws Exception
	 * @throws
	 */
	public long insert(String table, ContentValues values) throws Exception {
		try {
			long row = getWritableDatabase().insertOrThrow(table, null, values);
			return row;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @Title: update
	 * @Description: TODO
	 * @param table
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 * @throws Exception
	 * @throws
	 */
	public int update(String table, ContentValues values, String whereClause,
			String whereArgs[]) throws Exception {
		try {
			int rows = getWritableDatabase().update(table, values, whereClause,
					whereArgs);
			return rows;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @Title: delete
	 * @Description: TODO
	 * @param table
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 * @throws Exception
	 * @throws
	 */
	public int delete(String table, String whereClause, String whereArgs[])
			throws Exception {
		try {
			int rows = getWritableDatabase().delete(table, whereClause,
					whereArgs);
			return rows;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @Title: queryMaxValue
	 * @Description: TODO
	 * @param table
	 * @param column
	 * @param selection
	 * @param args
	 * @return
	 * @throws Exception
	 * @throws
	 */
	public ContentValues queryMaxValue(String table, String column,
			String selection, String args[]) throws Exception {
		Cursor cursor = null;
		ContentValues res = null;
		try {
			String sql = new StringBuffer("SELECT MAX(").append(column)
					.append(") AS maxv FROM ").append(table).append(" WHERE ")
					.append(selection).toString();
			cursor = getReadableDatabase().rawQuery(sql, args);
			if (cursor != null && cursor.getCount() > 0) {
				res = new ContentValues();
				if (cursor.moveToFirst()) {
					int ind = cursor.getColumnIndex("maxv");
					int ctype = cursor.getType(ind);
					switch (ctype) {
					case Cursor.FIELD_TYPE_BLOB:
						byte b[] = cursor.getBlob(ind);
						res.put(column, b);
						break;
					case Cursor.FIELD_TYPE_FLOAT:
						Float f = cursor.getFloat(ind);
						res.put(column, f);
						break;
					case Cursor.FIELD_TYPE_INTEGER:
						int i = cursor.getInt(ind);
						res.put(column, i);
						break;
					case Cursor.FIELD_TYPE_NULL:
						res.put(column, "");
						break;
					case Cursor.FIELD_TYPE_STRING:
						String s = cursor.getString(ind);
						res.put(column, s);
						break;
					default:
						break;
					}
				}
			}
			return res;
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * 
	 * @Title: queryMinValue
	 * @Description: TODO
	 * @param table
	 * @param column
	 * @param selection
	 * @param args
	 * @return
	 * @throws Exception
	 * @throws
	 */
	public ContentValues queryMinValue(String table, String column,
			String selection, String args[]) throws Exception {
		Cursor cursor = null;
		ContentValues res = null;
		try {
			String sql = new StringBuffer("SELECT MIN(").append(column)
					.append(") AS minv FROM ").append(table).append(" WHERE ")
					.append(selection).toString();
			cursor = getReadableDatabase().rawQuery(sql, args);
			if (cursor != null && cursor.getCount() > 0) {
				res = new ContentValues();
				if (cursor.moveToFirst()) {
					int ind = cursor.getColumnIndex("minv");
					int ctype = cursor.getType(ind);
					switch (ctype) {
					case Cursor.FIELD_TYPE_BLOB:
						byte b[] = cursor.getBlob(ind);
						res.put(column, b);
						break;
					case Cursor.FIELD_TYPE_FLOAT:
						Float f = cursor.getFloat(ind);
						res.put(column, f);
						break;
					case Cursor.FIELD_TYPE_INTEGER:
						int i = cursor.getInt(ind);
						res.put(column, i);
						break;
					case Cursor.FIELD_TYPE_NULL:
						res.put(column, "");
						break;
					case Cursor.FIELD_TYPE_STRING:
						String s = cursor.getString(ind);
						res.put(column, s);
						break;
					default:
						break;
					}
				}
			}
			return res;
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * 
	 * @Title: doQuery
	 * @Description: TODO
	 * @param table
	 * @param columns
	 * @param selection
	 * @param args
	 * @return
	 * @throws Exception
	 * @throws
	 */
	public ContentValues doQuery(String table, String columns[],
			String selection, String[] args) throws Exception {
		Cursor cursor = null;
		ContentValues res = null;
		try {
			cursor = getReadableDatabase().query(table, columns, selection,
					args, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				res = new ContentValues();
				for (String c : columns) {
					try {
						int index = cursor.getColumnIndex(c);
						int ctype = cursor.getType(index);
						switch (ctype) {
						case Cursor.FIELD_TYPE_BLOB:
							byte b[] = cursor.getBlob(index);
							res.put(c, b);
							break;

						case Cursor.FIELD_TYPE_FLOAT:
							Float f = cursor.getFloat(index);
							res.put(c, f);
							break;

						case Cursor.FIELD_TYPE_INTEGER:
							int i = cursor.getInt(index);
							res.put(c, i);
							break;

						case Cursor.FIELD_TYPE_NULL:
							res.put(c, "");
							break;

						case Cursor.FIELD_TYPE_STRING:
							String s = cursor.getString(index);
							res.put(c, s);
							break;

						default:
							break;
						}
					} catch (Exception e) {
						// TODO: handle exception
						continue;
					}
				}
			}
			return res;
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * 
	 * @Title: doQuery
	 * @Description: TODO
	 * @param table
	 * @param columns
	 * @param selection
	 * @param args
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 * @throws Exception
	 * @throws
	 */
	public ArrayList<ContentValues> doQuery(String table, String columns[],
			String selection, String[] args, String groupBy, String having,
			String orderBy, String limit) throws Exception {
		Cursor cursor = null;
		ArrayList<ContentValues> res = null;
		try {
			cursor = getReadableDatabase().query(table, columns, selection,
					args, groupBy, having, orderBy, limit);
			if (cursor != null && cursor.getCount() > 0) {
				res = new ArrayList<ContentValues>();
				while (cursor.moveToNext()) {
					ContentValues values = new ContentValues();
					for (String c : columns) {
						try {
							int index = cursor.getColumnIndex(c);
							int ctype = cursor.getType(index);
							switch (ctype) {
							case Cursor.FIELD_TYPE_BLOB:
								byte b[] = cursor.getBlob(index);
								values.put(c, b);
								break;
							case Cursor.FIELD_TYPE_FLOAT:
								Float f = cursor.getFloat(index);
								values.put(c, f);
								break;
							case Cursor.FIELD_TYPE_INTEGER:
								int i = cursor.getInt(index);
								values.put(c, i);
								break;
							case Cursor.FIELD_TYPE_NULL:
								values.put(c, "");
								break;
							case Cursor.FIELD_TYPE_STRING:
								String s = cursor.getString(index);
								values.put(c, s);
								break;
							default:
								break;
							}
						} catch (Exception e) {
							continue;
						}
					}
					res.add(values);
				}
			}
			return res;
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * 
	 * @Title: getUserNameArray
	 * @Description: TODO
	 * @param context
	 * @return
	 * @throws Exception
	 * @throws
	 */
	public String[] getUserNameArray() throws Exception {
		String[] names = null;
		Cursor cursor = null;
		try {
			String columns[] = new String[] { UserTable.field_name };
			cursor = getReadableDatabase().query(UserTable.name, columns, null,
					null, null, null, UserTable.field_name + " asc");
			if (cursor != null && cursor.getCount() > 0) {
				int count = cursor.getCount();
				names = new String[count];
				int i = 0;
				while (cursor.moveToNext()) {
					try {
						int index = cursor.getColumnIndex("name");
						String name = cursor.getString(index);
						names[i++] = name;
					} catch (Exception e) {
						continue;
					}
				}
			}
			return names;
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}
	
	
	public List<KeyValue> getLoginedUser() throws Exception {
		List<KeyValue> userKvList=null;
		Cursor cursor = null;
		try {
			String columns[] = new String[] { UserTable.field_name,UserTable.field_chName};
			cursor = getReadableDatabase().query(UserTable.name, columns, null,null, null, null, UserTable.field_name + " asc");
			if (cursor != null && cursor.getCount() > 0) {
				int count = cursor.getCount();
				userKvList=new ArrayList<>();
				while (cursor.moveToNext()) {
					try {
						int nIndex = cursor.getColumnIndex(UserTable.field_name);
						String name = cursor.getString(nIndex);
						int cIndex = cursor.getColumnIndex(UserTable.field_chName);
						String chName = cursor.getString(cIndex);
						KeyValue kv=new KeyValue(chName,chName);
						userKvList.add(kv);
					} catch (Exception e) {
						continue;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return userKvList;
	}
	
	public boolean deleteUserLoadLog(String chName){
		
		try {
			String delWhere = new StringBuffer(UserTable.field_chName).append("=?").toString();
			String args[] = new String[] { chName };
			int row=delete(UserTable.name,delWhere, args);
			if(row>-1){
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	

	/**
	 * 
	 * @Title: getParameters
	 * @Description: TODO
	 * @param table
	 * @return
	 * @throws Exception
	 * @throws
	 */
	public ArrayList<KeyValue> getParameters(String table) throws Exception {
		ArrayList<KeyValue> kvs = null;
		String sql;
		Cursor cursor = null;
		try {
			sql = new StringBuffer("select * from ").append(table).toString();
			cursor = getReadableDatabase().rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				kvs = new ArrayList<KeyValue>();
				while (cursor.moveToNext()) {
					try {
						int idIndex = cursor
								.getColumnIndex(ParmeterTable.field_id);
						int nameIndex = cursor
								.getColumnIndex(ParmeterTable.field_name);
						String id = cursor.getString(idIndex);
						String name = cursor.getString(nameIndex);
						KeyValue kv = new KeyValue(id, name);
						kvs.add(kv);
					} catch (Exception e) {
						// TODO: handle exception
						continue;
					}
				}
			}
			return kvs;
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}
	
	public ArrayList<ExpKeyValue> getWpParameters(String table) throws Exception {
		ArrayList<ExpKeyValue> kvs = null;
		String sql;
		Cursor cursor = null;
		try {
			sql = new StringBuffer("select * from ").append(table).toString();
			cursor = getReadableDatabase().rawQuery(sql, null);
			Log.i("ydzf", "getWpParameters cusor="+cursor);
			Log.i("ydzf", "getWpParameters cusor size="+cursor.getCount());
			if (cursor != null && cursor.getCount() > 0) {
				kvs = new ArrayList<ExpKeyValue>();
				while (cursor.moveToNext()) {
					try {
						int idIndex = cursor.getColumnIndex(LandUsageWpTable.field_id);
						int nameIndex = cursor.getColumnIndex(LandUsageWpTable.field_name);
						int subIndex = cursor.getColumnIndex(LandUsageWpTable.field_sub);
						
						String id = cursor.getString(idIndex);
						String name = cursor.getString(nameIndex);
						String sub = cursor.getString(subIndex);
						ExpKeyValue kv = new ExpKeyValue(id, name,sub);
						kvs.add(kv);
					} catch (Exception e) {
						// TODO: handle exception
						continue;
					}
				}
			}
			return kvs;
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * 
	 * @Title getUserAdmin
	 * @Description TODO
	 * @param name
	 * @return String[]
	 */
	public String[] getUserAdmin(String name) {
		try {
			String adminCode = "";
			String adminName = "";
			String selection = "name=?";
			String args[] = new String[] { name };
			String columns[] = new String[] { "territory" };
			Cursor cursor = getReadableDatabase().query("USERS", columns,
					selection, args, null, null, null);
			while (cursor.moveToFirst()) {
				int findex = cursor.getColumnIndex("territory");
				adminCode = cursor.getString(findex);
				adminName = queryAdminFullName(adminCode);
				break;
			}
			return new String[] { adminCode, adminName };
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 
	 * @Title: queryAdminFullName
	 * @Description: TODO
	 * @param context
	 * @param code
	 * @return
	 * @throws
	 */
	public String queryAdminFullName(String code) {
		String reString = "";
		try {
			String selection = "code=?";
			String args[] = new String[] { code };
			String columns[] = new String[] { "code", "name", "parentCode" };
			reString = queryAdminParent(AdminTable.name, columns, selection,
					args);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return reString;
	}

	String queryAdminParent(String table, String columns[], String selection,
			String args[]) throws Exception {
		String reString = "";
		Cursor cursor = null;
		try {
			cursor = getReadableDatabase().query(table, columns, selection,
					args, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				int nameIndex = cursor.getColumnIndex("name");
				int parentIndex = cursor.getColumnIndex("parentCode");
				String name = cursor.getString(nameIndex);
				reString += name;
				String parentCode = cursor.getString(parentIndex);
				if (parentCode != null) {
					reString = queryAdminParent(table, columns, selection,
							new String[] { parentCode }) + reString;
				}
			}
			return reString;
		} catch (Exception e) {
			throw e;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}
}
