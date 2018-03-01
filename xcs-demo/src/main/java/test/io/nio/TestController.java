package test.io.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.xcs.utils.StreamUtils;

import io.netty.handler.codec.http.FullHttpResponse;
import test.io.nio.to.request.NioHttpHeader;
import test.io.nio.to.request.NioHttpRequest;
import test.io.nio.to.response.NioHttpResponse;

public class TestController implements NioHttpProcess<NioHttpResponse> {

    @Override
    public NioHttpResponse process(NioHttpRequest nioHttpRequest) {
//        NioHttpHeader[] nhn = new NioHttpHeader[1];
//        nhn[0] = new NioHttpHeader("Content-Type", "text/plain; charset=UTF-8");
//        //nhn[1] = new NioHeaderNode("Content-Type", "text/plain; charset=UTF-8");
//        //nhn[0] = new NioHeaderNode("Content-Type", "text/plain; charset=UTF-8");
//        FullHttpResponse fhr =  NioHttpResponse.createResponseHeader(nhn);
//        NioHttpResponse nhr = new NioHttpResponse();
//        nhr.setHeader(NioHttpResponse.response2Header(fhr));
//        try { 
//           // nhr.setBody(new FileInputStream("e:\\data.txt"));
//            nhr.setBody(StreamUtils.file2string("e:\\data.txt"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return nhr;
        return null;
    }

}
