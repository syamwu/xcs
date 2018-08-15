package syamwu.xchushi.fw.common.util;

import java.util.UUID;

public class UUIDUtils {

    /**
     * 生成32位具有时间顺序的UUID
     * 
     * @return
     */
    public static String getUUID32() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成36位具有时间顺序的UUID
     * 
     * @return
     */
    public static String getUUID36() {
        return UUID.randomUUID().toString();
    }

    public static String getRandomUUID32() {
        String uuid = java.util.UUID.randomUUID().toString().replaceAll("-", "");
        return uuid;
    }

}
