package com.example.lml.easyphoto.camera;

import java.util.UUID;
public class StringUtil {
	/**
	 * 处理空字符串 
	 * @param str
	 * @return String
	 */
	public static String doEmpty(String str) {
		return doEmpty(str, "");
	}
	/**
	 * 处理空字符串
	 * @param str
	 * @param defaultValue
	 * @return String
	 */
	public static String doEmpty(String str, String defaultValue) {
		if (str == null || str.equalsIgnoreCase("null") 
				|| str.trim().equals("") || str.trim().equals("－请选择－")) {
			str = defaultValue;
		}else if(str.startsWith("null")){
			str = str.substring(4,str.length());
		}
		return str.trim();
	}
    
    /**
     * 请选择
     */
    final static String PLEASE_SELECT = "请选择...";
    
    public static boolean notEmpty(Object o) {
        return o != null && !"".equals(o.toString().trim())
                && !"null".equalsIgnoreCase(o.toString().trim())
                && !"undefined".equalsIgnoreCase(o.toString().trim())
                && !PLEASE_SELECT.equals(o.toString().trim());
    }
    public static boolean empty(Object o) {
        return o == null || "".equals(o.toString().trim())
                || "null".equalsIgnoreCase(o.toString().trim())
                || "undefined".equalsIgnoreCase(o.toString().trim())
                || PLEASE_SELECT.equals(o.toString().trim());
    }
    public static boolean num(Object o) {
    	int n = 0;
		try {
			n=Integer.parseInt(o.toString().trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
    	if(n>0){
    		return true;
    	}else{
    		return false;
    	}
    }
	//大于0
    public static boolean decimal(Object o) {
    	double n = 0;
    	try {
    		n=Double.parseDouble(o.toString().trim());
    	} catch (NumberFormatException e) {
    		e.printStackTrace();
    	}
    	if(n>0.0){
    		return true;
    	}else{
    		return false;
    	}
    }

	//动态生成数据库的主键id
	public static String getUUID(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}



}
