package com.xchushi.fw.common.util;

import com.xchushi.fw.common.constant.StringConstant;

public class StringUtil {
    
    public static final String POINT_REG = "\\.";

    public static String getRootPacke(String className) {
        String[] split = className.split(POINT_REG);
        if (split.length >= 3) {
            return split[0] + StringConstant.POINT + split[1];
        }
        return split[0];
    }

    public static String getRootPacke(String className, int rootlen) {
        String[] split = className.split("\\.");
        int loopLen = split.length >= rootlen ? rootlen : split.length;
        String result = split[0];
        for (int i = 1; i < loopLen; i++) {
            result = result + StringConstant.POINT + split[i];
        }
        return result;
    }
    
    public static boolean isRelevantRootPacke(StackTraceElement[] sts, String... checkRootPackage) {
        //System.out.println("---" + sts[0].getClassName());
        if (sts == null || sts.length < 1 || checkRootPackage == null) {
            return false;
        }
        int[] rootLens = new int[checkRootPackage.length];
        for (int i = 0; i < rootLens.length; i++) {
            rootLens[i] = checkRootPackage[i].split(POINT_REG).length;
        }
        for (StackTraceElement st : sts) {
           // System.out.println(st.getClassName());
            for (int i = 0; i < checkRootPackage.length; i++) {
                if (checkRootPackage[i].equals(getRootPacke(st.getClassName(), rootLens[i]))) {
                    return true;
                }
            }
        }
        return false;
    }

}
