package com.example.lml.easyphoto.db;

import java.util.List;

public class BaseManager {

    /**
     * 生成查询参数String[] args
     * @param list
     * @return String[] args
     */
    public static String[] queryArgs(List<String> list) {
//        String[] args = null;
//        if (param != null && !param.isEmpty()) {
//            args = new String[param.size()];
//            for (int i = 0; i < param.size(); i++) {
//                args[i] = param.get(i);
//            }
//        }
//        return args;
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static boolean notEmpty(Object o) {
        return o != null && !o.toString().trim().equals("");
    }
}
