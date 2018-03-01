package test.netty.aio4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xcs.utils.HttpUtils;

public class TestHttp {

    public static void main(String[] args) {
        Map<String, List<String>> mapthis4 = new HashMap<String, List<String>>();
        HttpUtils.putListString("Connection", "keep-alive", mapthis4);
        HttpUtils.sendGetXF("http://127.0.0.1:12345/", mapthis4, true);
        
    }

}
