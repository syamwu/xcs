package all.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import com.xcs.utils.HttpUtils;
import com.xcs.utils.MD5Utils;

public class TestSDFY {
    
    
    public static void main(String[] args) {
        
        System.out.println("{\"".indexOf("{\""));
        System.out.println("[{\"".indexOf("{\""));
        
    }
    
    
    
    
    
    /**
     * 排序后组合字符串
     * 
     * @param data
     * @return
     */
    public static String coverMap2SignString(Map<String, String> data) {
        TreeMap<String, String> tree = new TreeMap<>();
        Iterator<Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> en = it.next();
            if ("sign".equals(en.getKey().trim())) {
                continue;
            }
            tree.put(en.getKey(), en.getValue());
        }

        it = tree.entrySet().iterator();
        StringBuffer sf = new StringBuffer();
        while (it.hasNext()) {
            Entry<String, String> en = it.next();
            sf.append(en.getKey()+"="+en.getValue()+"&");
        }

        return sf.toString();
    }

}
