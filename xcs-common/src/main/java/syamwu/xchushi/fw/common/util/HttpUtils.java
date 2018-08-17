package syamwu.xchushi.fw.common.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import syamwu.xchushi.fw.common.constant.HttpContentType;

public class HttpUtils {

    protected static final int TIME_OUT = 30_000;
    protected static final int MAX_LOG_LENGTH = 2000;

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) //连接超时
            .writeTimeout(10, TimeUnit.SECONDS) //写超时
            .readTimeout(30, TimeUnit.SECONDS) //读超时
            .build();

//    /**
//     * 发送http请求并进行gzip压缩
//     * 
//     * @param content
//     * @param url
//     * @return
//     * @throws IOException
//     * @author syam_wu
//     */
//    public static CloseableHttpResponse sendRequest(String content, String url, String charset, int timeOut, boolean gzip)
//            throws IOException {
//        if (gzip) {
//            Header[] headers = { new BasicHeader("Content-Type", "text/plain"),
//                    new BasicHeader("Content-Encoding", "gzip") };
//            ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
//            originalContent.write(content.getBytes(charset));
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
//            originalContent.writeTo(gzipOut);
//            gzipOut.finish();
//            ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray());
//            return sendRequest(content, url, headers, entity, timeOut);
//        } else {
//            Header[] headers = { new BasicHeader("Content-Type", "application/json"),new BasicHeader("Accept", "*/*") };
//            StringEntity entity = new StringEntity(content, charset);
//            return sendRequest(content, url, headers, entity, timeOut);
//        }
//        
//    }
//
//    protected static CloseableHttpResponse sendRequest(String content, String url, Header[] headers, HttpEntity entity,
//            int timeOut) throws IOException {
//        // TODO 这里其实不用每次都创建一个新的httpClient,可以使用PoolingHttpClientConnectionManager
//        // TODO 这里其实不用每次都创建一个新的httpClient,可以使用PoolingHttpClientConnectionManager
//        // TODO 这里其实不用每次都创建一个新的httpClient,可以使用PoolingHttpClientConnectionManager
//        CloseableHttpResponse response = null;
//        try{
//            CloseableHttpClient httpClient = HttpClients.createDefault();
//            HttpPost httpPost = new HttpPost(url);
//            httpPost.setHeaders(headers);
//            httpPost.setEntity(entity);
//            if (config == null) {
//                config = RequestConfig.custom().setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut)
//                        .setSocketTimeout(timeOut).build();
//            }
//            httpPost.setConfig(config);
//            response = httpClient.execute(httpPost);
//        } finally {
//            if (response != null) {
//                response.close();
//            }
//        }
//        return response;
//    }
    
    /**
     * 发送GET请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    
    /**
     * 发送POST请求
     *
     * @return
     * @throws IOException
     */
    public static Response postResponse(Request request, OkHttpClient client)
            throws IOException {
        return client.newCall(request).execute();
    }

    /**
     * 发送POST请求
     *
     * @return
     * @throws IOException
     */
    public static Response postResponse(String url, Headers headers, RequestBody body, OkHttpClient client)
            throws IOException {
        Request request = null;
        if (headers != null) {
            request = new Request.Builder().url(url).headers(headers).post(body).build();
        } else {
            request = new Request.Builder().url(url).post(body).build();
        }
        return postResponse(request, client);
    }
    
    /**
     * 发送POST请求
     *
     * @return
     * @throws IOException
     */
    public static Response postResponse(String url, String content, HttpContentType httpContentType, String charset,
            Integer timeOut, boolean gzip) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse(httpContentType.getContentType(charset)), content);
        Request request = new Request.Builder().url(url).post(body).build();
        if (gzip) {
            request = gzipRequest(request);
        }
        return postResponse(request, CLIENT);
    }
    
    private static Request gzipRequest(Request request) throws IOException {
        Request compressedRequest = request.newBuilder()
                .header("Content-Encoding", "gzip")
                .method(request.method(), gzip(request.body()))
                .build();
        return compressedRequest;
    }

    private static RequestBody gzip(final RequestBody body) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() {
                return -1; // 无法提前知道压缩后的数据大小
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }

        };
    }

    public static String postString(String url, String content, HttpContentType httpContentType) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse(httpContentType.getContentType()), content);
        Response response = postResponse(url, null, body, CLIENT);
        return response.body().string();
    }
    
    public static String postString(String url, String content) throws IOException {
        return postString(url, content, HttpContentType.TEXTHTML);
    }
    
    public static String getCharset(String contentType) {
        return getCharset(contentType, "UTF-8");
    }
    
    public static String getCharset(String contentType, String defaultCharset) {
        String result = null;
        if (contentType == null || contentType.trim().equals("")) {
            return defaultCharset;
        }
        String regex = "charset=((.*?;)|.*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(contentType);
        if (m.find()) {
            String regGroup = m.group(0);
            if (regGroup != null && regGroup.indexOf(";") > -1) {
                regGroup = regGroup.substring(0, regGroup.length() - 1);
            }
            result = regGroup.substring(regGroup.indexOf("=") + 1, regGroup.length());
        }
        if (result == null || result.trim().equals("")) {
            result = defaultCharset;
        }
        return result;
    }
    
}
