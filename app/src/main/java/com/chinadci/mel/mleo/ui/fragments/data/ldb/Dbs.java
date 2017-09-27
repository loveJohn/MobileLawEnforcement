package com.chinadci.mel.mleo.ui.fragments.data.ldb;

public class Dbs {
    public static final String CREATE_TABLE_STNUM = "CREATE TABLE STNUM ( " +
            "id VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "user VARCHAR(100) ," +
            "name VARCHAR(100) ," +
            "num VARCHAR(100) ," +
            "quid VARCHAR(100) ," +
            "isShowDetails VARCHAR(100) )";
    public static final String CREATE_TABLE_XZQHNUM = "CREATE TABLE XZQHNUM ( " +
            "id VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "user VARCHAR(100) ," +
            "ajid VARCHAR(100) ," +
            "xzqhid VARCHAR(100) ," +
            "pid VARCHAR(100) ," +
            "name VARCHAR(100) ," +
            "num VARCHAR(100) ," +
            "xzqudm VARCHAR(100) ," +
            "hasSub VARCHAR(100) )";
    public static final String CREATE_TABLE_XZQHNUM1 = "CREATE TABLE XZQHNUM1 ( " +
            "id VARCHAR(100)," +        //AUTOINCREMENT自增
            "user VARCHAR(100) ," +
            "ajid VARCHAR(100) ," +
            "xzqhid VARCHAR(100) ," +
            "pid VARCHAR(100) ," +
            "name VARCHAR(100) ," +
            "num VARCHAR(100) ," +
            "xzqudm VARCHAR(100) ," +
            "ajKey VARCHAR(100) ," +
            "hasSub VARCHAR(100) ,"+ 
            "PRIMARY KEY (id,ajKey) )";
    public static final String CREATE_TABLE_SIMPLEAJ = "CREATE TABLE SIMPLEAJ ( " +
            "id VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "ajid VARCHAR(100) ," +
            "xzqhid VARCHAR(100) ," +
            "bh VARCHAR(100) ," +
            "xxdz VARCHAR(100) ," +
            "sj VARCHAR(100) ," +
            "ajly VARCHAR(100) ," +
            "hx VARCHAR(1000) ," +
            "x VARCHAR(100) ," +
            "y VARCHAR(100) ," +
            "hxfxjg VARCHAR(500) ," +
            "jcbh VARCHAR(100) ," +
            "isSave VARCHAR(100) )";
    public static final String CREATE_TABLE_SIMPLEAJ1 = "CREATE TABLE SIMPLEAJ1 ( " +
            "id VARCHAR(100) ," +        //AUTOINCREMENT自增
            "ajid VARCHAR(100) ," +
            "xzqhid VARCHAR(100) ," +
            "bh VARCHAR(100) ," +
            "xxdz VARCHAR(100) ," +
            "sj VARCHAR(100) ," +
            "ajly VARCHAR(100) ," +
            "hx VARCHAR(1000) ," +
            "x VARCHAR(100) ," +
            "y VARCHAR(100) ," +
            "hxfxjg VARCHAR(500) ," +
            "jcbh VARCHAR(100) ," +
            "ajKey VARCHAR(100) ," +
            "isSave VARCHAR(100) ," +
            "isRevoke VARCHAR(100) ," +
            "isApprover VARCHAR(100) ," +
            "lastState VARCHAR(100) ," +
            "PRIMARY KEY (id,ajKey) )";
    public static final String CREATE_TABLE_SIMPLEAJ2 = "CREATE TABLE SIMPLEAJ2 ( " +
            "id VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "ajid VARCHAR(100) ," +
            "xzqhid VARCHAR(100) ," +
            "bh VARCHAR(100) ," +
            "xxdz VARCHAR(100) ," +
            "sj VARCHAR(100) ," +
            "ajly VARCHAR(100) ," +
            "hx VARCHAR(1000) ," +
            "x VARCHAR(100) ," +
            "y VARCHAR(100) ," +
            "hxfxjg VARCHAR(500) ," +
            "jcbh VARCHAR(100) ," +
            "isSave VARCHAR(100) )";
    public static final String CREATE_TABLE_INSPECTIONGETTASK = "CREATE TABLE INSPECTIONGETTASK ( " +
            "bh VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "redline VARCHAR(1000) ," +
            "x VARCHAR(100) ," +
            "y VARCHAR(100) ," +
            "ajly VARCHAR(100) ," +
            "dz VARCHAR(100) ," +
            "entiryString VARCHAR(2000) )";
    public static final String CREATE_TABLE_INSPECTIONGETTASK2 = "CREATE TABLE INSPECTIONGETTASK2 ( " +
            "bh VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "redline VARCHAR(1000) ," +
            "x VARCHAR(100) ," +
            "y VARCHAR(100) ," +
            "ajly VARCHAR(100) ," +
            "dz VARCHAR(100) ," +
            "jcbh VARCHAR(100) ," +
            "jcmj VARCHAR(100) ," +
            "zygdmj VARCHAR(100) ," +
            "xfsj VARCHAR(100) ," +
            "entiryString VARCHAR(2000) )";
    
    public static final String ADD_INSPECTIONGETTASK2_ZYGDMJ = "ALTER TABLE [INSPECTIONGETTASK2] ADD [zygdmj] VARCHAR(100)";
    
    public static final String CREATE_TABLE_PATROLS = "CREATE TABLE PATROLS ( " +
            "id VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "fxjg VARCHAR(1000) )";
    
    public static final String CREATE_TABLE_PATROLS_TYPE = "CREATE TABLE PATROLS_TYPE ( " +
            "id VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "title VARCHAR(100) ," +
            "type VARCHAR(100) )";
    
    public static final String CREATE_TABLE_PATROLS_SP = "CREATE TABLE PATROLS_SP ( " +
            "id VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "spry VARCHAR(100) ," +
            "spsj VARCHAR(100) ," +
            "spsm VARCHAR(100) )";
    
    public static final String CREATE_TABLE_PATROLS_CH = "CREATE TABLE PATROLS_CH ( " +
            "id VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "chry VARCHAR(100) ," +
            "chsj VARCHAR(100) ," +
            "chyy VARCHAR(100) )";
    
    public static final String CREATE_TABLE_PATROLS_TH = "CREATE TABLE PATROLS_TH ( " +
            "id VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "sqry VARCHAR(100) ," +
            "sqsj VARCHAR(100) ," +
            "sqyy VARCHAR(100) ," +
            "thry VARCHAR(100) ," +
            "thsj VARCHAR(100) ," +
            "thyy VARCHAR(100) )";
    
    public static final String CREATE_TABLE_clqk_now = "CREATE TABLE clqk_now ( " +
            "key VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "value VARCHAR(100) ," +
            "parent VARCHAR(500) )";
    
    public static final String CREATE_TABLE_WpzfAjNum = "CREATE TABLE WpzfAjNum ( " +
            "KEY VARCHAR(100)," +        //AUTOINCREMENT自增
            "BL_ZT VARCHAR(100) ," +
            "COUNT VARCHAR(100) ," +
            "xzqh VARCHAR(100) ,"+
            "PRIMARY KEY (KEY,xzqh)  )";
    
    public static final String CREATE_TABLE_INSPECTIONEDIT = "CREATE TABLE INSPECTIONEDIT ( " +
            "bh VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "wfztlb VARCHAR(100) ," +
            "wfztmc VARCHAR(100) ," +
            "phone VARCHAR(100) ," +
            "wjlx VARCHAR(100) ," +
            "wfxz VARCHAR(100) ," +
            "wfydmj VARCHAR(100) ," +
            "dl VARCHAR(100) ," +
            "cgsm VARCHAR(100) ," +
            "hcry VARCHAR(100) ," +
            "hcsj VARCHAR(100) )";
    
    public static final String CREATE_TABLE_INSPECTIONEDIT_JSDT = "CREATE TABLE INSPECTIONEDIT_JSDT ( " +
            "bh VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "key VARCHAR(100) ," +
            "value VARCHAR(100) )";
    
    public static final String CREATE_TABLE_JSDT = "CREATE TABLE JSDT ( " +
            "key VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "value VARCHAR(100) )";
    
    public static final String CREATE_TABLE_PHOTO = "CREATE TABLE PHOTO ( " +
            "photoPath VARCHAR(100) PRIMARY KEY ," +        //AUTOINCREMENT自增
            "xzb VARCHAR(100) ," +
            "yzb VARCHAR(100) ," +
            "fwj VARCHAR(100) ," +
            "pssj VARCHAR(100) )";
   
}
