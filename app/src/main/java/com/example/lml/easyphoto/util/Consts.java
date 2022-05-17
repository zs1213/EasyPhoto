package com.example.lml.easyphoto.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;


public class Consts {
    public static LinkedHashMap<String, String> riskClassSource = new LinkedHashMap<String, String>();
    public static LinkedHashMap<String, String> riskSource0 = new LinkedHashMap<String, String>();
    public static LinkedHashMap<String, String> riskSource1 = new LinkedHashMap<String, String>();
    public static LinkedHashMap<String, String> pay = new LinkedHashMap<String, String>();
    public static LinkedHashMap<String, String> publicty = new LinkedHashMap<String, String>();
    public static LinkedHashMap<String, String> insuranceCertificate = new LinkedHashMap<String, String>();
    public static LinkedHashMap<String, String> clause = new LinkedHashMap<String, String>();

    public static LinkedHashMap<String, String> risk = new LinkedHashMap<String, String>();
    public static LinkedHashMap<String, String> massifLevel = new LinkedHashMap<String, String>();
    public static LinkedHashMap<String, String> massifType = new LinkedHashMap<String, String>();
    public static LinkedHashMap<String, String> soilType = new LinkedHashMap<String, String>();
    public static LinkedHashMap<String, String> chuping = new LinkedHashMap<String, String>();
    public static LinkedHashMap<String, String> riskReson = new LinkedHashMap<String, String>();

    //灾害等级
    public static LinkedHashMap<String, String> disasterLevel = new LinkedHashMap<String, String>();
    //生育期
    public static LinkedHashMap<String, String> growthPeriod = new LinkedHashMap<String, String>();
    //承保种类
    public static LinkedHashMap<String, String> insuranceChoice = new LinkedHashMap<String, String>();
    //搜索条件
    public static LinkedHashMap<String, String> seachWhere = new LinkedHashMap<String, String>();
    //查验地类型
    public static LinkedHashMap<String, String> inspectionPlace = new LinkedHashMap<String, String>();
    //证件类型
    public static LinkedHashMap<String, String> idType = new LinkedHashMap<String, String>();
    //权属性质
    public static LinkedHashMap<String, String> ownerShip = new LinkedHashMap<String, String>();
    //灌溉条件
    public static LinkedHashMap<String, String> irrigationType = new LinkedHashMap<String, String>();
    //银行类别
    public static LinkedHashMap<String, String> bankType = new LinkedHashMap<String, String>();
    //保单类别
    public static LinkedHashMap<String, String> policyCategory = new LinkedHashMap<String, String>();

    static {
        bankType.put("402", "农村信用合作社");
        bankType.put("103", "中国农业银行");
        bankType.put("102", "中国工商银行");
        bankType.put("105", "中国建设银行");
        bankType.put("203", "中国农业发展银行");
        bankType.put("313", "城市商业银行");
        bankType.put("314", "农村商业银行");
        bankType.put("317", "农村合作银行");
        bankType.put("322", "上海农商银行");
        bankType.put("403", "中国邮政储蓄银行");
        bankType.put("633", "泰华农民银行");

    }

    static {
        policyCategory.put("01", "团体投保--统保");
        policyCategory.put("02", "个体投保-合作社/家庭农场/种植大户/企业");
        policyCategory.put("03", "团体投保-专业合作组织");
    }

    static {
        riskClassSource.put("0", "粮油作物");
        riskClassSource.put("1", "大棚作物");
    }

    static {
        insuranceChoice.put("0", "种植业");
        insuranceChoice.put("1", "大棚");
    }

    static {
        seachWhere.put("0", "村名或乡名");
        seachWhere.put("1", "农户姓名");
        seachWhere.put("2", "身份证号");
    }

    static {
        /*inspectionPlace.put("0","岗地");
        inspectionPlace.put("1","平地");
		inspectionPlace.put("2","洼地");
		inspectionPlace.put("3","河套地");*/
        inspectionPlace.put("013", "旱地");
        inspectionPlace.put("011", "水田");
        inspectionPlace.put("012", "水浇地");
        inspectionPlace.put("021", "果园");
        inspectionPlace.put("022", "茶园");
        inspectionPlace.put("023", "其他园地");
        inspectionPlace.put("031", "有林地");
        inspectionPlace.put("032", "灌木林地");
        inspectionPlace.put("033", "其他林地");
        inspectionPlace.put("041", "天然牧草地");
        inspectionPlace.put("042", "人工牧草地");
        inspectionPlace.put("043", "其他草地");
        inspectionPlace.put("099", "其他");
    }

    static {
        idType.put("01", "身份证号");
        idType.put("04", "社会信用代码");
        idType.put("71", "组织机构代码");
        idType.put("72", "税务登记证");
        idType.put("73", "营业执照号");
        idType.put("99", "其它");
    }

    static {
        ownerShip.put("0", "自有");
        ownerShip.put("1", "承包");
    }

    static {
        irrigationType.put("0", "有");
        irrigationType.put("1", "无");
    }


    //安邦
    static {
        riskSource0.put("HLJ311401", "黑龙江省玉米种植成本保险条款");
        riskSource0.put("HLJ310101", "黑龙江省水稻种植成本保险条款");
        riskSource0.put("HLJ312601", "黑龙江省大豆种植成本保险条款");
        riskSource0.put("HLJ314001", "黑龙江省葵花籽种植成本保险条款");
        riskSource0.put("HLJ310701", "黑龙江省小麦种植成本保险条款");
        riskSource0.put("HLJ310403", "黑龙江省马铃薯种植成本保险条款");
    }

    //安华
    /*static {
        riskSource0.put("JL00102", "吉林省农作物（玉米）种植成本保险条款（2016版）");
        riskSource0.put("JL00201", "吉林省农作物（水稻）种植成本保险条款（2016版）");
        riskSource0.put("JL00202", "吉林省农作物（大豆）种植成本保险条款（2016版）");
        riskSource0.put("JL00302", "吉林省农作物（花生）种植成本保险条款（2016版）");
        riskSource0.put("JL00301", "吉林省农作物（葵花籽）种植成本保险条款（2016版）");
        riskSource0.put("LN00102", "辽宁省农作物（玉米）种植成本保险条款（2016版）");
        riskSource0.put("LN00201", "辽宁省农作物（水稻）种植成本保险条款（2016版）");
        riskSource0.put("LN00202", "辽宁省农作物（大豆）种植成本保险条款（2016版）");
        riskSource0.put("LN00302", "辽宁省农作物（花生）种植成本保险条款（2016版）");
        riskSource0.put("NMG00202", "内蒙古农作物（大豆）种植成本保险条款（2016版）");
        riskSource0.put("NMG00203", "内蒙古农作物（旱地马铃薯）种植成本保险条款（2016版）");
        riskSource0.put("NMG00204", "内蒙古农作物（旱地小麦）种植成本保险条款（2016版）");
        riskSource0.put("NMG00205", "内蒙古农作物（旱地玉米）种植成本保险条款（2016版）");
        riskSource0.put("NMG00206", "内蒙古农作物（葵花籽）种植成本保险条款（2016版）");
        riskSource0.put("NMG00207", "内蒙古农作物（水稻）种植成本保险条款（2016版）");
        riskSource0.put("NMG00208", "内蒙古农作物（水地玉米）种植成本保险条款（2016版）");
        riskSource0.put("NMG00209", "内蒙古农作物（甜菜）种植成本保险条款（2016版）");
        riskSource0.put("NMG00210", "内蒙古农作物（油菜）种植成本保险条款（2016版）");
        riskSource0.put("NMG00211", "内蒙古农作物（胡麻）种植成本保险条款（2016版）");
        riskSource0.put("NMG00212", "内蒙古农作物（水地马铃薯）种植成本保险条款（2016版）");
        riskSource0.put("NMG00213", "内蒙古农作物（水地小麦）种植成本保险条款（2016版）");
    }*/
    //平安
    /*static {
		riskSource0.put("HLJ10001", "黑龙江省玉米种植成本保险条款");
        riskSource0.put("HLJ10002", "黑龙江省水稻种植成本保险条款");
        riskSource0.put("HLJ10003", "黑龙江省大豆种植成本保险条款");
	}*/
    //学习
    /*static {
        riskSource0.put("JL00102", "吉林省农作物（玉米）种植成本保险条款（2016版）");
        riskSource0.put("JL00201", "吉林省农作物（水稻）种植成本保险条款（2016版）");
        riskSource0.put("JL00202", "吉林省农作物（大豆）种植成本保险条款（2016版）");
        riskSource0.put("JL00302", "吉林省农作物（花生）种植成本保险条款（2016版）");
        riskSource0.put("JL00301", "吉林省农作物（葵花籽）种植成本保险条款（2016版）");
        riskSource0.put("LN00102", "辽宁省农作物（玉米）种植成本保险条款（2016版）");
        riskSource0.put("LN00201", "辽宁省农作物（水稻）种植成本保险条款（2016版）");
        riskSource0.put("LN00202", "辽宁省农作物（大豆）种植成本保险条款（2016版）");
        riskSource0.put("LN00302", "辽宁省农作物（花生）种植成本保险条款（2016版）");
        riskSource0.put("NMG00202", "内蒙古农作物（大豆）种植成本保险条款（2016版）");
        riskSource0.put("NMG00203", "内蒙古农作物（旱地马铃薯）种植成本保险条款（2016版）");
        riskSource0.put("NMG00204", "内蒙古农作物（旱地小麦）种植成本保险条款（2016版）");
        riskSource0.put("NMG00205", "内蒙古农作物（旱地玉米）种植成本保险条款（2016版）");
        riskSource0.put("NMG00206", "内蒙古农作物（葵花籽）种植成本保险条款（2016版）");
        riskSource0.put("NMG00207", "内蒙古农作物（水稻）种植成本保险条款（2016版）");
        riskSource0.put("NMG00208", "内蒙古农作物（水地玉米）种植成本保险条款（2016版）");
        riskSource0.put("NMG00209", "内蒙古农作物（甜菜）种植成本保险条款（2016版）");
        riskSource0.put("NMG00210", "内蒙古农作物（油菜）种植成本保险条款（2016版）");
        riskSource0.put("NMG00211", "内蒙古农作物（胡麻）种植成本保险条款（2016版）");
        riskSource0.put("NMG00212", "内蒙古农作物（水地马铃薯）种植成本保险条款（2016版）");
        riskSource0.put("NMG00213", "内蒙古农作物（水地小麦）种植成本保险条款（2016版）");
    }*/

    static {
        riskSource1.put("HIA", "烟叶种植保险（吉林）");
        riskSource1.put("HPL", "人参种植保险");
        riskSource1.put("HPK", "辣椒种植保险（吉林）");
        riskSource1.put("HPO", "日光大棚、日光温室保险（吉林）");
        riskSource1.put("HPY", "日光温室大棚保险（辽宁）");
        riskSource1.put("HTA", "商业性日光温室大棚保险（辽宁）");
        riskSource1.put("HIB", "辽宁省烟叶种植保险（辽宁）");
        riskSource1.put("HIC", "辽宁省晒烟种植保险（辽宁）");
        riskSource1.put("HSA", "食用菌种植成本保险（辽宁）");

    }

    static {
        pay.put("0", "自缴");
        pay.put("1", "垫付");
    }

    static {
        publicty.put("0", "已看公示");
        publicty.put("1", "没去看公示");
        publicty.put("2", "没看到公示");
    }

    static {
        insuranceCertificate.put("0", "是");
        insuranceCertificate.put("1", "否");
    }

    static {
        clause.put("0", "是");
        clause.put("1", "否");
    }

    static {
		/*risk.put("0001", "玉米");
		risk.put("0002", "水稻");
		risk.put("0003", "大豆");
		risk.put("0004", "葵花籽");
		risk.put("0005", "小麦");
		risk.put("0006", "马铃薯");
		risk.put("0007", "棉花");
		risk.put("0008", "花生");
		risk.put("0009", "辣椒");
		risk.put("0010", "烟叶");
		risk.put("0011", "人参");
		risk.put("0012", "大棚");
		risk.put("0013", "温室大棚");
		risk.put("0014", "日光大棚");
		risk.put("0015", "水地玉米");
		risk.put("0016", "旱地玉米");
		risk.put("0017", "油菜");
		risk.put("0018", "胡麻");
		risk.put("0019", "甜菜");
		risk.put("0020", "西瓜");
		risk.put("0021", "葡萄");
		risk.put("0022", "食用菌");
		risk.put("0023", "番茄");
		risk.put("0024", "紫花苜蓿");
		risk.put("0025", "烟种");*/
        risk.put("3114", "玉米");
        risk.put("3101", "水稻");
        risk.put("3126", "大豆");
//        risk.put("3107", "小麦种植保险");
//        risk.put("3104", "马铃薯种植保险");
//        risk.put("3119", "南果梨种植保险");
//        risk.put("0036", "谷子");
//        risk.put("0037", "糜子");
//        risk.put("0027", "辣椒");
//        risk.put("0028", "大蒜");
//        risk.put("0029", "毛葱");
//        risk.put("0030", "荞麦");
//        risk.put("0031", "红小豆");
//        risk.put("0032", "绿小豆");
//        risk.put("0033", "大棚香瓜");
//        risk.put("0034", "地膜西瓜");
//        risk.put("0035", "露地西瓜");
//        risk.put("3103", "水稻制种种植保险");
//        risk.put("3105", "香蕉树风灾保险");
//        risk.put("3106", "甘蔗种植保险");
//        risk.put("3108", "油菜种植保险");
//        risk.put("3109", "芒果种植保险");
//        risk.put("3110", "橡胶树风灾指数保险");
//        risk.put("3111", "橡胶树风灾保险");
//        risk.put("3112", "高粱种植保险");
//        risk.put("3125", "烟叶种植保险");
        //risk.put("3140", "葵花籽种植保险");
//        risk.put("3141", "大棚及瓜菜种植保险");
//        risk.put("3157", "花生种植保险");
        //risk.put("3202", "奶牛养殖保险");
        //risk.put("3220", "能繁母猪养殖保险");
        //risk.put("3225", "育肥猪养殖保险");
        //risk.put("3301", "森林综合保险(公益林)");
        //risk.put("3302", "森林综合保险(商品林)");
//        risk.put("0001", "玉米");
//        risk.put("0002", "水稻");
//        risk.put("0003", "大豆");
//        risk.put("0004", "葵花籽");
//        risk.put("0005", "小麦");
//        risk.put("0006", "马铃薯");
//        risk.put("0025", "谷子");
//        risk.put("0026", "糜子");
//        risk.put("0027", "辣椒");
//        risk.put("0028", "大蒜");
//        risk.put("0029", "毛葱");
//        risk.put("0030", "荞麦");
//        risk.put("0031", "红小豆");
//        risk.put("0032", "绿小豆");
//        risk.put("0033", "大棚香瓜");
//        risk.put("0034", "地膜西瓜");
//        risk.put("0035", "露地西瓜");
    }

    static {
        massifLevel.put("0003", "高");
        massifLevel.put("0002", "中");
        massifLevel.put("0001", "低");
    }

    static {
        massifType.put("0001", "普通");
        massifType.put("0002", "高保障");
        massifType.put("0003", "大灾");
        massifType.put("0004", "边缘户");
    }

    static {
        soilType.put("0001", "黑钙土");
        soilType.put("0002", "草甸土");
        soilType.put("0003", "风沙土");
        soilType.put("0004", "棕壤土");
        soilType.put("0005", "白浆土");
        soilType.put("0006", "暗棕壤");
        soilType.put("0007", "沼泽土");
    }

    static {
        chuping.put("0", "是");
        chuping.put("1", "否");
    }

    static {
        riskReson.put("0017", "未成灾");
        riskReson.put("0005", "内涝");
        riskReson.put("0003", "暴雨");
        riskReson.put("0004", "洪水");
        riskReson.put("0002", "风灾");
        riskReson.put("0006", "旱灾");
        riskReson.put("0013", "冰雹");
        riskReson.put("0001", "雹灾");
        riskReson.put("0007", "冻灾");
        riskReson.put("0008", "病虫害");
        riskReson.put("0009", "泥石流");
        riskReson.put("0010", "山体滑坡");
        riskReson.put("0011", "雪灾");
        riskReson.put("0012", "雨灾");
        riskReson.put("0014", "暴雨形成的洪涝");
        riskReson.put("0015", "六级（含）以上风");
        riskReson.put("0016", "低温冻害");
    }

    static {
        disasterLevel.put("L00", "未成灾");
        disasterLevel.put("L01", "轻灾");
        disasterLevel.put("L02", "中灾");
        disasterLevel.put("L03", "重灾");
        disasterLevel.put("L04", "绝产");
    }

    static {
        growthPeriod.put("L00", "出苗期");
        growthPeriod.put("L01", "拔节期");
        growthPeriod.put("L02", "抽穗期");
        growthPeriod.put("L03", "灌浆期");
        growthPeriod.put("L04", "成熟期");
    }

    public static String[] getStrNames(HashMap<String, String> mp) {
        Set<String> set = mp.keySet();
        String[] str = new String[set.size()];
        int i = 0;
        for (String s : set) {
            str[i++] = mp.get(s);
        }
        return str;
    }

    public static String[] getStrNames(TreeMap<String, String> mp) {
        Set<String> set = mp.keySet();
        String[] str = new String[set.size()];
        int i = 0;
        for (String s : set) {
            str[i++] = mp.get(s);
        }
        return str;
    }

    public static String[] getStrNames(LinkedHashMap<String, String> mp) {
        Set<String> set = mp.keySet();
        String[] str = new String[set.size()];
        int i = 0;
        for (String s : set) {
            str[i++] = mp.get(s);
        }
        return str;
    }

    public static String getValueByName(HashMap<String, String> mp, String name) {
        Set<String> set = mp.keySet();
        for (String s : set) {
            if (mp.get(s).equals(name))
                return s;
        }
        return null;
    }

    public static String getValueByNameOfRisk(HashMap<String, String> mp, String name) {
        if (name.contains(",")) {
            String str[] = name.split(",");
            String res = "";
            for (int i = 0; i < str.length; i++) {
                String names = str[i];
                Set<String> sets = mp.keySet();
                for (String ss : sets) {
                    if (mp.get(ss).equals(names)) {
                        if (i==str.length-1) {
                            res+=ss;
                        }else {
                            res+=ss+",";
                        }
                    }
                }
            }
            return res;
        } else {
            Set<String> set = mp.keySet();
            for (String s : set) {
                if (mp.get(s).equals(name))
                    return s;
            }
        }

        return null;
    }

    public static String getValueByName(TreeMap<String, String> mp, String name) {
        Set<String> set = mp.keySet();
        for (String s : set) {
            if (mp.get(s).equals(name))
                return s;
        }
        return null;
    }
}
