package com.xcs.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpUtils {
    public static final String WEB_PATTERN = "(\\w+):\\/\\/([^\\:.]+)\\.66ba\\.com\\.cn(:?\\d*)";

    public static String locationCookie;

    public static String requestContentType = "text/html;charset:utf-8;";

    public static String filedir = "D:\\upload\\code\\";

    // Post请求
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {

            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/html;charset:utf-8;");
            conn.setRequestProperty("Connection", "Keep-Alive");//
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStreamWriter outstream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out = new PrintWriter(outstream);
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static String sendGet(String url) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {

            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.setRequestProperty("User-Agent:", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");//
            conn.connect();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static Map<String, Object> sendGetXF(String url) {
        return sendGetXF(url, false);
    }

    public static Map<String, Object> sendGetXF(String url, boolean showDetail) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        putListString("Cookie", locationCookie, map);
        putListString("Content-Type", requestContentType, map);
        return sendGetXF(url, map, showDetail);
    }

    public static Map<String, Object> sendGetXF(String url, Map<String, List<String>> requestProperties,
            boolean showDetail) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        Map<String, Object> resMap = new LinkedHashMap<String, Object>();
        long time = System.currentTimeMillis();
        try {
            if (showDetail)
                System.out.println("--------------------------sendGetXFStart:" + url + "--------------------------");
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(false);
            if (requestProperties != null)
                for (Entry<String, List<String>> entry : requestProperties.entrySet()) {
                    String property = entry.getKey();
                    int i = 0;
                    String content = "";
                    for (Iterator<String> iterator = entry.getValue().iterator(); iterator.hasNext(); i++) {
                        if (i == 0) {
                            content += (String) iterator.next();
                        } else {
                            content += ";" + (String) iterator.next();
                        }
                    }
                    conn.setRequestProperty(property, content);
                }
            if (showDetail) {
                System.out.println("*******RequestHeader*******");
                Map<String, List<String>> reqMap = conn.getRequestProperties();
                for (Entry<String, List<String>> entry : reqMap.entrySet()) {
                    System.out.println("Key : " + entry.getKey() + " ,Value : " + entry.getValue());
                }
            }
            conn.connect();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            Map<String, List<String>> map = conn.getHeaderFields();
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            resMap.putAll(map);
            resMap.put("body", result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            resMap.put("UseTime", (System.currentTimeMillis() - time) + "ms");
        }
        if (showDetail) {
            System.out.println("*******Resposnse*******");
            for (Entry<String, Object> entry : resMap.entrySet()) {
                System.out.println("Key : " + entry.getKey() + " ,Value : " + entry.getValue());
            }
            System.out.println();
        }
        return resMap;
    }

    public static Map<String, Object> sendPostXF(String url) {
        return sendPostXF(url, null);
    }

    public static Map<String, Object> sendPostXF(String url, boolean showDetail) {
        return sendPostXF(url, null, "", showDetail);
    }

    public static Map<String, Object> sendPostXF(String url, String param) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        putListString("Cookie", locationCookie, map);
        putListString("Content-Type", requestContentType, map);
        return sendPostXF(url, map, param, true);
    }

    public static Map<String, Object> sendPostXF(String url, Map<String, List<String>> requestProperties, byte[] param,
            boolean showDetail) {
        PrintStream out = null;
        BufferedReader in = null;
        String result = "";
        Map<String, Object> resMap = new LinkedHashMap<String, Object>();
        long time = System.currentTimeMillis();
        try {
            if (showDetail)
                System.out.println("--------------------------sendPostXFStart:" + url + "--------------------------");
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("POST");
            if (requestProperties != null)
                for (Entry<String, List<String>> entry : requestProperties.entrySet()) {
                    String property = entry.getKey();
                    int i = 0;
                    String content = "";
                    for (Iterator<String> iterator = entry.getValue().iterator(); iterator.hasNext(); i++) {
                        if (i == 0) {
                            content += (String) iterator.next();
                        } else {
                            content += ";" + (String) iterator.next();
                        }
                    }
                    conn.setRequestProperty(property, content);
                }
            if (showDetail) {
                System.out.println("*******RequestHeader*******");
                Map<String, List<String>> reqMap = conn.getRequestProperties();
                for (Entry<String, List<String>> entry : reqMap.entrySet()) {
                    System.out.println("Key : " + entry.getKey() + " ,Value : " + entry.getValue());
                }
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            if (param != null) {
                // OutputStreamWriter outstream = new
                // OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                // out = new PrintWriter(outstream);
                out = new PrintStream(conn.getOutputStream());
                out.write(param);
                out.flush();
            }
            // in = new BufferedReader(new
            // InputStreamReader(conn.getInputStream()));
            Map<String, List<String>> map = conn.getHeaderFields();
            // String line;
            // while ((line = in.readLine()) != null) {
            // result += line;
            // }
            result = StreamUtils.inputStream2string(conn.getInputStream());
            resMap.putAll(map);
            resMap.put("body", result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            resMap.put("UseTime", (System.currentTimeMillis() - time) + "ms");
        }
        if (showDetail) {
            System.out.println("*******Resposnse*******");
            for (Entry<String, Object> entry : resMap.entrySet()) {
                System.out.println("Key : " + entry.getKey() + " ,Value : " + entry.getValue());
            }
            System.out.println();
        }
        return resMap;
    }

    public static Map<String, Object> sendPostXF(String url, Map<String, List<String>> requestProperties, String param,
            boolean showDetail) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        Map<String, Object> resMap = new LinkedHashMap<String, Object>();
        long time = System.currentTimeMillis();
        try {
            if (showDetail)
                System.out.println("--------------------------sendPostXFStart:" + url + "--------------------------");
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("POST");
            if (requestProperties != null)
                for (Entry<String, List<String>> entry : requestProperties.entrySet()) {
                    String property = entry.getKey();
                    int i = 0;
                    String content = "";
                    for (Iterator<String> iterator = entry.getValue().iterator(); iterator.hasNext(); i++) {
                        if (i == 0) {
                            content += (String) iterator.next();
                        } else {
                            content += ";" + (String) iterator.next();
                        }
                    }
                    conn.setRequestProperty(property, content);
                }
            if (showDetail) {
                System.out.println("*******RequestHeader*******");
                Map<String, List<String>> reqMap = conn.getRequestProperties();
                for (Entry<String, List<String>> entry : reqMap.entrySet()) {
                    System.out.println("Key : " + entry.getKey() + " ,Value : " + entry.getValue());
                }
                System.out.println("Body:");
                System.out.println(param);
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            if (param != null) {
                OutputStreamWriter outstream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                out = new PrintWriter(outstream);
                out.print(param);
                out.flush();
            }
            // in = new BufferedReader(new
            // InputStreamReader(conn.getInputStream()));
            Map<String, List<String>> map = conn.getHeaderFields();
            // String line;
            // while ((line = in.readLine()) != null) {
            // result += line;
            // }
            result = StreamUtils.inputStream2string(conn.getInputStream());
            resMap.putAll(map);
            resMap.put("body", result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            resMap.put("UseTime", (System.currentTimeMillis() - time) + "ms");
        }
        if (showDetail) {
            System.out.println("*******Resposnse*******");
            for (Entry<String, Object> entry : resMap.entrySet()) {
                System.out.println("Key : " + entry.getKey() + " ,Value : " + entry.getValue());
            }
            System.out.println();
        }
        return resMap;
    }

    public static Map<String, Object> sendURLToInputStream(String url) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        putListString("Cookie", locationCookie, map);
        putListString("Content-Type", requestContentType, map);
        return sendURLToInputStream(url, map);
    }

    public static Map<String, Object> sendURLToInputStream(String url, Map<String, List<String>> requestProperties) {
        PrintWriter out = null;
        InputStream in = null;
        Map<String, Object> resMap = new LinkedHashMap<String, Object>();
        long time = System.currentTimeMillis();
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            if (requestProperties != null)
                for (Entry<String, List<String>> entry : requestProperties.entrySet()) {
                    String property = entry.getKey();
                    int i = 0;
                    String content = "";
                    for (Iterator<String> iterator = entry.getValue().iterator(); iterator.hasNext(); i++) {
                        if (i == 0) {
                            content += (String) iterator.next();
                        } else {
                            content += ";" + (String) iterator.next();
                        }
                    }
                    conn.setRequestProperty(property, content);
                }
            conn.connect();
            in = conn.getInputStream();
            Map<String, List<String>> map = conn.getHeaderFields();
            ContentType contentTpye = getContentType(map.get("Content-Type"));
            String filePath = filedir + System.currentTimeMillis() + contentTpye.suffix();
            StreamUtils.inputStream2file(in, filePath);
            resMap.putAll(map);
            resMap.put("body", "ToFilePath:" + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            resMap.put("UseTime", (System.currentTimeMillis() - time) + "ms");
        }
        return resMap;
    }

    public static ContentType getContentType(List<String> contentTypeList) {
        if (contentTypeList == null) {
            return ContentType.NULL;
        }
        for (Iterator<String> iterator = contentTypeList.iterator(); iterator.hasNext();) {
            String string = (String) iterator.next();
            if (string.equals("image/jpeg")) {
                return ContentType.IMAGE;
            } else if (string.equals("text/html")) {
                return ContentType.TEXT;
            } else {
                if (!iterator.hasNext()) {
                    return ContentType.NULL;
                }
            }
        }
        return ContentType.NULL;
    }

    @SuppressWarnings("unchecked")
    public static String getHeaderFielded(Map<String, Object> map, String fieldName) {
        List<String> headerField = (List<String>) map.get(fieldName);
        if (headerField == null) {
            return "";
        }
        String result = "";
        int i = 0;
        for (Iterator<String> iterator = headerField.iterator(); iterator.hasNext(); i++) {
            if (i == 0) {
                result += (String) iterator.next();
            } else {
                result += "," + (String) iterator.next();
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static String getHeaderFieldedOne(Map<String, Object> map, String fieldName) {
        List<String> headerField = (List<String>) map.get(fieldName);
        if (headerField == null) {
            return "";
        }
        String result = "";
        String reg = "(" + fieldName + "=)(.*?)(;|\\z)";
        for (Iterator<String> iterator = headerField.iterator(); iterator.hasNext();) {
            result += (String) iterator.next();
            Matcher m = Pattern.compile(reg).matcher(result);
            while (m.find()) {
                result = m.group(0);
            }
            break;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static String getCookie(Map<String, Object> map, String cookieName) {
        List<String> headerField = (List<String>) map.get("Set-Cookie");
        if (headerField == null) {
            headerField = (List<String>) map.get("Cookie");
            if (headerField == null) {
                headerField = (List<String>) map.get("cookie");
                if (headerField == null) {
                    return "";
                }
            }
        }
        String result = "";
        String reg = "(" + cookieName + "=)(.*?)(;|\\z)";
        Matcher m = Pattern.compile(reg).matcher(headerField.toString());
        while (m.find()) {
            result = m.group(0);
        }
        return result;
    }

    public static void putListString(String key, String content, Map<String, List<String>> map) {
        if (content == null) {
            return;
        }
        String[] contents = content.split(";");
        List<String> lists = new ArrayList<String>();
        for (int i = 0; i < contents.length; i++) {
            lists.add(contents[i]);
        }
        map.put(key, lists);
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }

    /**
     * post方式请求服务器(https协议)
     * 
     * @param url
     *            请求地址
     * @param content
     *            参数
     * @param charset
     *            编码
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static String doSslPost(String url, String content, String charset) throws Exception {

        HttpsURLConnection conn = null;

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());

            URL console = new URL(url);
            conn = (HttpsURLConnection) console.openConnection();
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.connect();
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.write(content.getBytes(charset));
            // 刷新、关闭
            out.flush();
            out.close();
            InputStream is = conn.getInputStream();
            if (is != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                is.close();
                return new String(outStream.toByteArray(), "utf-8");
            }
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
    }
    
    /**
     * get方式请求服务器(https协议)
     * 
     * @param url
     *            请求地址
     * @param content
     *            参数
     * @param charset
     *            编码
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static String doSslGet(String url, String charset)
            throws NoSuchAlgorithmException, KeyManagementException,
            IOException {
        
        try{
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
                        new java.security.SecureRandom());
            URL console = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.connect();
            
            InputStream is = conn.getInputStream();
            if (is != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                is.close();
                return new String(outStream.toByteArray(),charset);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return "提交get异常:"+ex.getMessage();
        }
      
        return null;
    }
    
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static void main(String[] args) {

        //
        // Timestamp ss = new Timestamp(System.currentTimeMillis());
        // System.out.println(ss.toString().substring(0, 19));
        // //System.out.println(ss.getTime());
        // Date da = new Date(System.currentTimeMillis());

        // System.out.println("[/u2601][/u2601][/u2601][/u2600][/u2614][/u1F382]");

        // String message = "";
        // String str = "2.2.62";
        // String str1 = "2.3.61";
        // System.out.println(str.compareTo(str1));
        // try {
        // message = StreamUtils.inputStream2string(new FileInputStream(file));
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // }
        //
        // System.out.println(System.currentTimeMillis());

        // String regex3 = "(<img)(.*)({1,1}?>)";
        // String regex3 =
        // "<img(.*?</img>|.*?/>|.*?>)|<video(.*?</video>|.*?/>|.*?>)";
        // String newstr3 = message.replaceAll(regex3, "");
        // System.out.println(newstr3);
        // System.out.println("3.0".compareTo("2.1.6"));
        // System.out.println(new Timestamp(1506590228000l));

        // HttpUtils.putListString("Connection", "Keep-Alive", mapthis2);
        // HttpUtils.putListString("User-Agent", "Apache-HttpClient/4.1.1 (java
        // 1.5)", mapthis2);
        int a = 1;
        sendPostXF("http://www.kcrj.net:12017/",
                "<program><function_id>yygh001</function_id><aab324>441600</aab324><pageno>1</pageno></program>");
        if (a == 1) {
            return;
        }
        System.out.println(HttpUtils.class.getClassLoader().toString());
        try {
            byte[] bytes = StreamUtils.file2byte(
                    "E:\\yunyigit\\front_guangzhongyiyao\\target\\classes\\cn\\yunyichina\\TestLoader.class");
            Map<String, List<String>> mapthis = new HashMap<String, List<String>>();
            HttpUtils.putListString("clsname", "cn.yunyichina.TestLoader", mapthis);
            HttpUtils.putListString("Content-Type", "text/xml", mapthis);
            sendPostXF("http://127.0.0.1:12335/testclsloader", mapthis, bytes, true);
            if (a == 1) {
                return;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Map<String, List<String>> mapthis2 = new HashMap<String, List<String>>();
        HttpUtils.putListString("Content-Type", "text/xml;charset=UTF-8", mapthis2);
        // HttpUtils.putListString("SOAPAction",
        // "\"http://tempuri.org/getDocTime\"", mapthis2);
        // HttpUtils.putListString("Content-Length", "570", mapthis2);
        // HttpUtils.putListString("Connection", "Keep-Alive", mapthis2);
        // HttpUtils.putListString("User-Agent", "Apache-HttpClient/4.1.1 (java
        // 1.5)", mapthis2);
        sendPostXF("http://121.15.139.21:7094/ServiceOfHis.asmx", mapthis2,
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\"><soapenv:Header/><soapenv:Body><tem:getDocTime><!--Optional:--><tem:request><![CDATA[<request><params><branchCode></branchCode><scheduleDate>2017-06-23</scheduleDate><deptCode>1001</deptCode><doctorCode>9546</doctorCode>   <timeFlag>0</timeFlag></params></request>]]></tem:request></tem:getDocTime></soapenv:Body></soapenv:Envelope>",
                true);
        if (a == 1) {
            return;
        }

        for (int i = 0; i < 100; i++) {
            Map<String, List<String>> mapthis3 = new HashMap<String, List<String>>();
            // HttpUtils.putListString("Accept", "*/*; q=0.01", mapthis);
            // HttpUtils.putListString("Accept-Encoding", "gzip, deflate",
            // mapthis);
            // HttpUtils.putListString("Accept-Language", "zh-CN,zh;q=0.8",
            // mapthis);
            // HttpUtils.putListString("Connection", "keep-alive", mapthis);
            // HttpUtils.putListString("Cookie",
            // "YF-Ugrow-G0=57484c7c1ded49566c905773d5d00f82;
            // SUB=_2AkMvu-2Sf8NxqwJRmP4QxW7raI1-zQHEieKZ5xxJJRMxHRl-yT83qnRetRBQYKQLXWuKATeRZ4Z7WCcOo0DlDQ..;
            // SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9W5.xE8V7Aj6NJcE35LzuduF;
            // login_sid_t=63dc9ce0789962ee0b885d09a66b1853;
            // YF-V5-G0=3717816620d23c89a2402129ebf80935;
            // WBStorage=02e13baf68409715|undefined; _s_tentry=-;
            // Apache=1455624069702.75.1491558963064;
            // SINAGLOBAL=1455624069702.75.1491558963064;
            // ULV=1491558963068:1:1:1:1455624069702.75.1491558963064:",
            // mapthis);
            // HttpUtils.putListString("User-Agent", "Mozilla/5.0 (Windows NT
            // 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)
            // Chrome/56.0.2924.87 Safari/537.36", mapthis);
            // HttpUtils.putListString("Referer", "http://weibo.com/", mapthis);
            // HttpUtils.putListString("Host", "weibo.com", mapthis);
            // HttpUtils.putListString("X-Requested-With", "XMLHttpRequest",
            // mapthis);
            // sendGetXF("http://weibo.com/a/aj/transform/loadingmoreunlogin?ajwvr=6&category=0&page="+i+"&lefnav=0&__rnd="+System.currentTimeMillis(),mapthis,
            // true);
            HttpUtils.putListString("Connection", "keep-alive", mapthis3);
            HttpUtils.putListString("Content-Type", "text/plain; charset=utf-8", mapthis3);
            HttpUtils.putListString("Cookie", "aliyungf_tc=AQAAAJREflRtRAcAOsaLd6EcKjHx/+dO", mapthis3);
            HttpUtils.putListString("Cookie2", "$Version=1", mapthis3);
            HttpUtils.putListString("Accept-Encoding", "gzip", mapthis3);
            HttpUtils.putListString("ZYP", "mid=4546680", mapthis3);
            HttpUtils.sendPostXF("http://tbapi.ixiaochuan.cn/post/detail?sign=c7041bef701b9abc0169de1e0adbf2d8",
                    mapthis3,
                    "{\"token\":\"T9K7Ny1rKuSu3lkvBbokhLxPr2RqP_gZUsrQblfg7ZXo-8YM=\",\"pid\":21752888,\"from\":\"index\",\"h_av\":\"3.5.3\",\"h_dt\":0,\"h_os\":23,\"h_model\":\"MI NOTE LTE\",\"h_did\":\"867389023660631_02:00:00\",\"h_nt\":1,\"h_m\":4546680,\"h_ch\":\"me\",\"h_ts\":1495766158145}",
                    true);
        }

        HttpUtils.locationCookie = "JSESSIONID=54D7ED14CF49B05278EBC216AC10C3FA";

        Map<String, List<String>> mapthis4 = new HashMap<String, List<String>>();
        HttpUtils.putListString("Connection", "keep-alive", mapthis4);
        HttpUtils.putListString("Content-Type", "text/plain; charset=utf-8", mapthis4);
        HttpUtils.putListString("Cookie", "aliyungf_tc=AQAAAJREflRtRAcAOsaLd6EcKjHx/+dO", mapthis4);
        HttpUtils.putListString("Cookie2", "$Version=1", mapthis4);
        HttpUtils.putListString("Accept-Encoding", "gzip", mapthis4);
        HttpUtils.putListString("ZYP", "mid=4546680", mapthis4);
        // HttpUtils.putListString("Content-Type","application/x-www-form-urlencoded;
        // charset=UTF-8", mapthis);
        // HttpUtils.putListString("header",
        // "{\"version\":\"V2\",\"uid\":1378,\"terminal\":{\"alias\":\"ALLB\",\"resolution\":\"1280_720_14_1.5\",\"quaVersion\":\"V2\",\"imei\":\"860525584000\"},\"sign\":\"SFASSD24541SSDAWSF12421SD\",\"token\":\"dgwJasd232go2\",\"timestamp\":\"1485000000\",\"network\":\"wifi\"}",
        // mapthis);
        // HttpUtils.putListString("header",
        // "{\"businessType\":\"\",\"clientType\":\"app\",\"clientVersion\":\"android\",\"network\":3,\"nocache\":\"\",\"pageNo\":\"\",\"pageSize\":\"\",\"requestId\":5,\"requestIp\":\"\",\"requestLayer\":\"\",\"requestUserId\":\"\",\"screenHeight\":1280,\"screenWidth\":720,\"sign\":\"\",\"signature\":\"\",\"timestamp\":1492762845,\"token\":\"EQ2xD6hVIReabaapIqT2l72QaGcdg6lB\",\"uid\":2241,\"validField\":\"\",\"version\":\"\",\"terminal\":{\"rootStatus\":\"0\",\"model\":\"LenovoP1c72\",\"cpuCoresNum\":\"2\",\"macAdress\":\"08:00:27:00:D8:CC\",\"romName\":\"\",\"cpuName\":\"ARMv7
        // Processor rev 0
        // (v7l)\",\"androidIdSdCard\":\"08002700D8CC0000\",\"androidId\":\"08002700D8CC0000\",\"versionCode\":\"2160000\",\"resolution\":\"1280_720_14_1.5\",\"versionStatus\":\"OFF_V2\",\"buildNo\":\"0000\",\"quaVersion\":\"V2\",\"fingerprint\":\"\",\"ramTotalSize\":\"1514\",\"imsi2\":\"\",\"rom\":\"4.4.2_19\",\"channelId\":\"0825\",\"alias\":\"ALLB\",\"imei\":\"864394010080028\",\"imei2\":\"\",\"romVersion\":\"\",\"versionName\":\"2.1.6\",\"product\":\"taurus\",\"cpuMaxFreq\":\"2000000\",\"manufacturer\":\"LENOVO\",\"cpuMinFreq\":\"2000000\",\"brand\":\"Lenovo\",\"imsi\":\"460070800270021\"}}",
        // mapthis);
        // HttpUtils.putListString("X-Forwarded-For", "123.2.2.2", mapthis);

        Map<String, Object> resMap = new LinkedHashMap<String, Object>();

        // resMap =
        // HttpUtils.sendGetXF("http://192.168.10.50:666/posts/postsDetail.do?postsId=25405",mapthis,true);
        // resMap =
        // HttpUtils.sendPostXF("http://192.168.10.50:666/portal/trainList.do",
        // mapthis, "", true);
        // resMap =
        // HttpUtils.sendPostXF("http://192.168.10.50:666/activity/insert.do",
        // mapthis,
        // "{\"data\":{\"actname\":\"sfasfasf\",\"actdesc\":\"发发发发发撒反反复复方法付付付付付付付付付付付付付付付付付付付付付付付付付付a\",\"imgurl\":\"http://test.66ba.com.cn/6c36e599ffc34fe9a60f2730bd97b7f0.png,http://test.66ba.com.cn/6c36e599ffc34fe9a60f2730bd97b7f0.png,http://test.66ba.com.cn/6c36e599ffc34fe9a60f2730bd97b7f0.png,http://test.66ba.com.cn/6c36e599ffc34fe9a60f2730bd97b7f0.png\",\"btime\":\"1493101487\",\"etime\":\"1493101487\",\"address\":\"广东省深圳市福田区农轩路55号\",\"mapid\":\"119092a37cbfa4991ef74db9\",\"latitude\":\"22.546054\",\"longtitude\":\"114.025974\",\"pro\":\"广东省\",\"ct\":\"深圳市\",\"reg\":\"福田区\",\"stre\":\"农轩路\",\"ismobileneed\":\"1\",\"acttype\":\"2\",\"type\":\"0\",\"pro\":\"广东省\",\"cit\":\"深圳市\",\"reg\":\"宝安区\"}}",
        // true);
        resMap = HttpUtils.sendPostXF("http://tbapi.ixiaochuan.cn/post/detail?sign=c7041bef701b9abc0169de1e0adbf2d8",
                mapthis4,
                "{\"token\":\"T9K7Ny1rKuSu3lkvBbokhLxPr2RqP_gZUsrQblfg7ZXo-8YM=\",\"pid\":21752888,\"from\":\"index\",\"h_av\":\"3.5.3\",\"h_dt\":0,\"h_os\":23,\"h_model\":\"MI NOTE LTE\",\"h_did\":\"867389023660631_02:00:00\",\"h_nt\":1,\"h_m\":4546680,\"h_ch\":\"me\",\"h_ts\":1495766158145}",
                true);

        // System.out.println((String)resMap.get("body"));

    }

}
