package com.example.lml.easyphoto.util;

import android.content.Context;

/**
 * 数据本地存取
 * @author czl
 *
 */
public class SharePreferencesTools {
	/**
	 * 保存数据到本地
	 * @param context
	 * @param fileName
	 * @param strName
	 * @param strValue
	 */
	public static void saveString(Context context, String fileName, String strName, String strValue){
		context.getSharedPreferences(fileName, 0).edit().putString(strName, strValue).commit();
	}
	/**
	 * 取出本地数据
	 * @param context
	 * @param fileName
	 * @param strName
	 * @param strValue
	 * @return
	 */
	public static String getValue(Context context, String fileName, String strName, String strValue){
		
		return context.getSharedPreferences(fileName, 0).getString(strName, strValue);
	}
}
