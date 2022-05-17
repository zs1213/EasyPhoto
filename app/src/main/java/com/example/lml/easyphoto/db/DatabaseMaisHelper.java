package com.example.lml.easyphoto.db;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseMaisHelper extends SDCardSQLiteOpenHelper{
	private  Context context;
	private static final String DATABASE_NAME = "aits";
	private static final String FREE_PHOTO ="create table FREE_PHOTO  (   ID VARCHAR(50)  not null,  fileName VARCHAR(100), folderName VARCHAR(100), filePath VARCHAR(100),state VARCHAR(100),createTime VARCHAR(100),province VARCHAR(100),city VARCHAR(100),townname VARCHAR(100),countrysidename VARCHAR(100),villagename VARCHAR(100),corporateName VARCHAR(100),remark VARCHAR(100),lon VARCHAR(100),lat VARCHAR(100),riskReason VARCHAR(100),riskCode VARCHAR(100),massifType VARCHAR(100),zhType VARCHAR(100),isTask VARCHAR(100),cnpjcl VARCHAR(100),chuPing VARCHAR(100),soilType VARCHAR(100),sscd VARCHAR(100),lonAndlat VARCHAR(20000),area VARCHAR(100),gisCode VARCHAR(100),serverPath VARCHAR(100),gisPath VARCHAR(100),constraint CLAIMS_PUBLICITY primary key (ID));";
	private static final String DK_INFO ="create table DK_INFO  (   " +
			"ID VARCHAR(50)  not null,  " +
			"taskId VARCHAR(100),   " +
			"userName VARCHAR(100)," +
			"isBusinessEntity VARCHAR(100)," +
			"policyNumber VARCHAR(100)," +
			"crop VARCHAR(100)," +
			"dkName VARCHAR(100)," +
			"drawArea VARCHAR(100)," +
			"changeArea VARCHAR(100)," +
			"state VARCHAR(100)," +
			"createTime VARCHAR(100)," +
			"subTime VARCHAR(100)," +
			"proposalNo VARCHAR(100)," +
			"certificateId VARCHAR(100)," +
			"customerIds VARCHAR(1000)," +
			"userId VARCHAR(100)," +
			"province VARCHAR(100)," +
			"city VARCHAR(100)," +
			"town VARCHAR(100)," +
			"countryside VARCHAR(100)," +
			"village VARCHAR(100)," +
			"provinceCode VARCHAR(100)," +
			"cityCode VARCHAR(100)," +
			"townCode VARCHAR(100)," +
			"countrysideCode VARCHAR(100)," +
			"villageCode VARCHAR(100),constraint CLAIMS_PUBLICITY primary key (ID));";
	private static final String DK_POINT ="create table DK_POINT  (   " +
			"ID VARCHAR(50)  not null,  " +
			"taskId VARCHAR(100),   " +
			"lon VARCHAR(100)," +
			"lat VARCHAR(100)," +
			"type VARCHAR(100)," +
			"number VARCHAR(100),constraint CLAIMS_PUBLICITY primary key (ID));";
	private static final String CUSTOMIZE_INFO ="create table CUSTOMIZE_INFO  (   " +
			"ID VARCHAR(50)  not null,  " +
			"taskId VARCHAR(100),   " +
			"userName VARCHAR(100)," +
			"isBusinessEntity VARCHAR(100)," +
			"policyNumber VARCHAR(100)," +
			"crop VARCHAR(100)," +
			"dkName VARCHAR(100)," +
			"remarks VARCHAR(1000)," +
			"drawArea VARCHAR(100)," +
			"changeArea VARCHAR(100)," +
			"state VARCHAR(100)," +
			"createTime VARCHAR(100)," +
			"subTime VARCHAR(100)," +
			"proposalNo VARCHAR(100)," +
			"certificateId VARCHAR(100)," +
			"customerIds VARCHAR(1000)," +
			"userId VARCHAR(100)," +
			"province VARCHAR(100)," +
			"city VARCHAR(100)," +
			"town VARCHAR(100)," +
			"countryside VARCHAR(100)," +
			"village VARCHAR(100)," +
			"provinceCode VARCHAR(100)," +
			"cityCode VARCHAR(100)," +
			"townCode VARCHAR(100)," +
			"countrysideCode VARCHAR(100)," +
			"villageCode VARCHAR(100),constraint CLAIMS_PUBLICITY primary key (ID));";
	private static final String CUSTOMIZE_POINT ="create table CUSTOMIZE_POINT  (   " +
			"ID VARCHAR(50)  not null,  " +
			"taskId VARCHAR(100),   " +
			"lon VARCHAR(100)," +
			"lat VARCHAR(100)," +
			"type VARCHAR(100)," +
			"number VARCHAR(100),constraint CLAIMS_PUBLICITY primary key (ID));";
	private static final String CUSTOMER ="create table CUSTOMER  (   " +
			"id VARCHAR(50)  not null,  " +
			"order_no INT(100),   " +
			"province_name VARCHAR(100)," +
			"city_name VARCHAR(100)," +
			"county_name VARCHAR(100)," +
			"country_name VARCHAR(100)," +
			"village_name VARCHAR(100)," +
			"proposal_no VARCHAR(100)," +
			"insured_method VARCHAR(100)," +
			"village_group  VARCHAR(100)," +
			"packet_no  VARCHAR(100)," +
			"insured_name VARCHAR(100)," +
			"certificate_type VARCHAR(100)," +
			"certificate_id VARCHAR(100)," +
			"isBusinessEntity VARCHAR(100)," +
			"policyNumber VARCHAR(100)," +
			"phone  VARCHAR(100)," +
			"massif_name VARCHAR(100)," +
			"massif_type VARCHAR(100)," +
			"crop_name VARCHAR(100)," +
			"insure_area VARCHAR(100)," +
			"bank_categories VARCHAR(100)," +
			"open_bank VARCHAR(100)," +
			"bank_card_no VARCHAR(100)," +
			"sum_quantity VARCHAR(100)," +
			"massif_no VARCHAR(100)," +
			"is_destitute VARCHAR(100)," +
			"office_id VARCHAR(100)," +
			"central_office VARCHAR(100)," +
			"submit_status VARCHAR(100)," +
			"create_by VARCHAR(100)," +
			"create_time VARCHAR(100)," +
			"update_time VARCHAR(100)," +
			"year_no INT,constraint CLAIMS_PUBLICITY primary key (id));";
	private static final String SIGN ="create table SIGN  (" +
			"ID VARCHAR(50)  not null,  " +
			"certificateId VARCHAR(100),   " +
			"createTime VARCHAR(100)," +
			"path VARCHAR(100)," +
			"proposalNo VARCHAR(100),constraint CLAIMS_PUBLICITY primary key (ID));";
	private static final String MASSIFSNAP ="create table MASSIFSNAP  (" +
			"ID VARCHAR(50)  not null,  " +
			"provinceName VARCHAR(100),   " +
			"cityName VARCHAR(100)," +
			"countyName VARCHAR(100)," +
			"countryName VARCHAR(100)," +
			"villageName VARCHAR(100)," +
			"certificateId VARCHAR(100),constraint CLAIMS_PUBLICITY primary key (ID));";

	public DatabaseMaisHelper(Context context, CursorFactory factory) throws NameNotFoundException {

		super(context, DATABASE_NAME, factory, context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
		this.context= context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		 
		try {
			db.execSQL(FREE_PHOTO);
			db.execSQL(DK_INFO);
			db.execSQL(DK_POINT);
			db.execSQL(CUSTOMER);
			db.execSQL(SIGN);
			db.execSQL(MASSIFSNAP);
			db.execSQL(CUSTOMIZE_INFO);
			db.execSQL(CUSTOMIZE_POINT);
		} catch (Exception e) {	e.printStackTrace();}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 		if(oldVersion<2) {
			try {//createTime VARCHAR(100),
				// province VARCHAR(100),
				// city VARCHAR(100),
				// townname VARCHAR(100),
				// countrysidename VARCHAR(100),
				// villagename VARCHAR(100),
				// corporateName VARCHAR(100),
				// remark VARCHAR(100)
				db.execSQL("ALTER TABLE FREE_PHOTO ADD createTime VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD province VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD city VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD townname VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD countrysidename VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD villagename VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD corporateName VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD remark VARCHAR(100);");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<3) {
			try {
				db.execSQL("ALTER TABLE FREE_PHOTO ADD lon VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD lat VARCHAR(100);");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<4) {
			try {
				db.execSQL("ALTER TABLE FREE_PHOTO ADD riskReason VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD riskCode VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD massifType VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD zhType VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD isTask VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD cnpjcl VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD chuPing VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD soilType VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD sscd VARCHAR(100);");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<5) {
			try {
				db.execSQL(DK_INFO);
				db.execSQL(DK_POINT);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<6) {
			try {
				db.execSQL("ALTER TABLE FREE_PHOTO ADD folderName VARCHAR(100);");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<7) {
			try {
				db.execSQL(CUSTOMER);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<8) {
			try {
				db.execSQL("ALTER TABLE DK_INFO ADD proposalNo VARCHAR(100);");
				db.execSQL("ALTER TABLE DK_INFO ADD certificateId VARCHAR(100);");
				db.execSQL("ALTER TABLE DK_INFO ADD customerIds VARCHAR(1000);");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<9) {
			try {
				db.execSQL(SIGN);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<10) {
			try {
				db.execSQL("ALTER TABLE DK_INFO ADD isBusinessEntity VARCHAR(100);");
				db.execSQL("ALTER TABLE DK_INFO ADD policyNumber VARCHAR(100);");
				db.execSQL("ALTER TABLE CUSTOMER ADD isBusinessEntity VARCHAR(100);");
				db.execSQL("ALTER TABLE CUSTOMER ADD policyNumber VARCHAR(100);");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<11) {
			try {
				db.execSQL(MASSIFSNAP);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<12) {
			try {
				db.execSQL("ALTER TABLE FREE_PHOTO ADD lonAndlat VARCHAR(20000);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD area VARCHAR(100);");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<13) {
			try {
				db.execSQL("ALTER TABLE FREE_PHOTO ADD gisCode VARCHAR(100);");
				db.execSQL("ALTER TABLE FREE_PHOTO ADD serverPath VARCHAR(100);");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<14) {
			try {
				db.execSQL(CUSTOMIZE_INFO);
				db.execSQL(CUSTOMIZE_POINT);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<15) {
			try {
				db.execSQL("ALTER TABLE FREE_PHOTO ADD gisPath VARCHAR(100);");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(oldVersion<16) {
			try {
				db.execSQL("ALTER TABLE CUSTOMIZE_INFO ADD remarks VARCHAR(1000);");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*if(oldVersion<3){
			db.execSQL("ALTER TABLE PRECISE_INSURANCE ADD F_CODE VARCHAR(50);");
			db.execSQL("ALTER TABLE PRECISE_ACCURATE ADD F_CODE VARCHAR(50);");
		}
		if(oldVersion<4){
			db.execSQL("ALTER TABLE PRECISE_SURVEY ADD FARMER_NAME VARCHAR(50);");
		}
		if(oldVersion<5){
			db.execSQL("create table PRECISE_MAP_PATH (ID VARCHAR(50) not null, PATH VARCHAR(200));");
		}
		if(oldVersion<6){//开户行
			db.execSQL("ALTER TABLE PRECISE_INSURANCE ADD BANK_DEPOSIT VARCHAR(50);");
		}
		if(oldVersion<7){//权属性质
			db.execSQL("ALTER TABLE PRECISE_INSURANCE ADD OWNERSHIP_NATURE VARCHAR(20);");
		}
		if(oldVersion<8){//保单类别
			db.execSQL("ALTER TABLE POLICY ADD POLICY_CATEGORY VARCHAR(20);");
		}
		if(oldVersion<9){//出险生育期
			db.execSQL("ALTER TABLE SCENE_REPORT ADD GROWTH_PERIOD VARCHAR(50);");
		}
		if(oldVersion<10){//垄数
			db.execSQL("ALTER TABLE PRECISE_INSURANCE ADD LONG_COUNT VARCHAR(30);");
		}
		if(oldVersion<11){//银行类别 gis地块上传标志
			db.execSQL("ALTER TABLE PRECISE_INSURANCE ADD BANK_TYPE VARCHAR(32);");
			db.execSQL("ALTER TABLE PRECISE_INSURANCE ADD DKSTATE VARCHAR(32);");
		}
		if (oldVersion < 12) {
			try { db.execSQL(FREE_TASK);} catch (Exception e) {	e.printStackTrace();}
			try { db.execSQL(FREE_POINT);} catch (Exception e) {	e.printStackTrace();}
			try { db.execSQL(FREE_PHOTO);} catch (Exception e) {	e.printStackTrace();}
			try { db.execSQL("ALTER TABLE PRECISE_SURVEY ADD massifLevel VARCHAR(5);");} catch (Exception e) {	e.printStackTrace();}
			try { db.execSQL("ALTER TABLE PRECISE_SURVEY ADD massifType VARCHAR(5);");} catch (Exception e) {	e.printStackTrace();}
			try { db.execSQL("ALTER TABLE PRECISE_SURVEY ADD soilType VARCHAR(5);");} catch (Exception e) {	e.printStackTrace();}
		}*/
	}
	@Override
	public void onOpen(SQLiteDatabase db) {

		super.onOpen(db);
	}

 }
