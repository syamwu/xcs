package xcs.common.util;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class MessageUtil {

    public static List<String> messageToList(String message, String split) {
        if(message == null || message.length() < 1){
            return null;
        }
        List<String> list = new ArrayList<String>();
        String[] msgArray = message.split(split);
        for (int i = 0; i < msgArray.length; i++) {
            list.add(msgArray[i]);
        }
        return list;
    }
    
    public static String messageToListStr(String message, String split) {
        return JSON.toJSONString(messageToList(message, split));
    }
    
}
