package com.xcs.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomUtils {
    
    private static final int DEFAULT_COUNT = 32;
    
    private static final char[] MYASCII = { 'A', '0', 'B', '1', 'j', 'u', 'w', 'C', '2', 'D', 'E', '4', 'F', '3', 'G',
            '7', 'H', '6', 'I', '5', 'J', 'K', 'L', 'c', 'l', 'm', 'n', 'M', 'v', 'N', 'o', 'p', 'f', 'g', 'h', 'q',
            't', 'O', 'P', 'Q', 'R', 'k', 'S', '9', 'T', 'r', 'U', 'V', 'W', 'X', 'Y', 's', 'Z', '8', 'a', 'b', 'd',
            'e', 'i', 'z' };

    // char[] myAscII = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
    // 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
    // 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
    // 'x', 'y', 'z', 'A', 'B', 'C', 'D',
    // 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
    // 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
    // 'Z' };
    
    public static String getRandom() {
       return getRandom(DEFAULT_COUNT);
    }

    public static String getRandom(int count) {
        StringBuilder ascII = new StringBuilder();
        for (int i = 0; i < count; i++) {
            ascII = ascII.append(getASCII(new Random().nextInt(59), new Random().nextInt(59)));
        }
        return ascII.toString();
    }

    public static char getASCII(int index, int offset) {
        int dev = index + offset;
        if (dev < 0) {
            return MYASCII[-(dev % MYASCII.length)];
        }
        if (dev >= MYASCII.length) {
            return MYASCII[dev % MYASCII.length];
        }
        return MYASCII[dev];
    }
    
    public static String getRandomASCII() {
        String nowDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String ascII = "";
        for (int i = 0; i < nowDate.length();) {
            ascII = ascII + getASCII(Integer.valueOf(nowDate.substring(i, i + 2)), 0);
            i = i + 2;
        }
        return ascII;
    }

}
