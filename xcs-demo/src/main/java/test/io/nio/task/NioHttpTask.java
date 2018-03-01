package test.io.nio.task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import test.io.nio.NioAttachment;
import test.io.nio.Server;
import test.io.nio.TestController;
import test.io.nio.to.HttpBodyType;
import test.io.nio.to.request.NioHttpBody;
import test.io.nio.to.request.NioHttpHeader;
import test.io.nio.to.request.NioHttpRequest;
import test.io.nio.to.request.NioRequest;
import test.io.nio.to.response.NioHttpResponse;

public class NioHttpTask implements Runnable {

    private NioAttachment nac;

    private SelectionKey key;

    private SocketChannel sc;
    
    private static int HTTP_HEAD_MAXLEGTH = 8091;
    
    private static int HTTP_BODY_LEGTH = 2048;

    public NioHttpTask(SelectionKey key) {
        this.nac = (NioAttachment) key.attachment();
        this.sc = (SocketChannel) key.channel();
        this.key = key;
    }

    @Override
    public void run() {
        try {
            // System.out.println(key);
            ByteBuffer bufferHead = ByteBuffer.allocate(HTTP_HEAD_MAXLEGTH);
            ByteBuffer bufferBody = null;
            byte[] bodyBytes = null;
            Object processReult = null;
            boolean isHead = true;
            NioRequest nr = null;
            for (;;) {
                if(isHead){
                    int readBytes = sc.read(bufferHead);
                    if (readBytes > 0) {
                        
                        bufferHead.flip();
                        byte[] bytes = new byte[bufferHead.remaining()];
                        bufferHead.get(bytes);
                        bufferHead.clear();
                        
                        Server.bq.add(new String(bytes,"UTF-8"));
                        CheckHeader ch = httpBytesTestHead(bytes);
                        if(ch.isHttpHeader()){
                            NioHttpHeader nioHeaderNode = NioHttpHeader.initNioHeaderNode(bytes);
                            nr = NioHttpRequest.initHttpRequest(nioHeaderNode);
                        }else{
                            throw new RuntimeException("Can't resolve this head");
                        }
                        //strbu.append(new String(bytes, "UTF-8"));
                        //Thread.sleep(123);
                        
                        if (readBytes < HTTP_HEAD_MAXLEGTH) {
                            //processReult = testController.process(NioHttpRequest.initHttpRequest(strbu.toString()));
                            if(ch.bodyIndex > 0 && ch.bodyIndex <= bytes.length){
                                byte[] body = new byte[bytes.length - ch.bodyIndex];
                                System.arraycopy(bytes, ch.bodyIndex, body, 0, bytes.length - ch.bodyIndex);
                                Server.bq.add(new String(body,"UTF-8"));
                                nr.setNioBody(new NioHttpBody(body));
                            }
                            processReult = nr.doRequest(nr);
                            break;
                        }
                        isHead = false;
                        // nac.matchHttpRequest(expression);
                        // nac.initResponse();
                    } else if (readBytes <= 0) {
                        // Server.bq.add("---------read end--------");
//                        processReult = testController.process(NioHttpRequest.initHttpRequest(strbu.toString()));
//                        break;
                        throw new RuntimeException("Can't read this channel");
                    }
                }else{
                    if(bufferBody == null){
                        bufferBody =  ByteBuffer.allocate(HTTP_BODY_LEGTH);
                    }
                    int readBytes = sc.read(bufferHead);
                    if (readBytes > 0) {
                        
                        bufferBody.flip();
                        byte[] bytes = new byte[bufferBody.remaining()];
                        bufferBody.get(bytes);
                        bufferBody.clear();
                        
                        Server.bq.add(new String(bytes,"UTF-8"));
                        
                        bodyBytes = concat(bodyBytes, bytes);
                        if (readBytes < HTTP_BODY_LEGTH) {
                            Server.bq.add(new String(bodyBytes,"UTF-8"));
                            nr.setNioBody(new NioHttpBody(bodyBytes));
                            processReult = nr.doRequest(nr);
                            break;
                        }
                        // nac.matchHttpRequest(expression);
                        // nac.initResponse();
                    } else if (readBytes <= 0) {
                        // Server.bq.add("---------read end--------");
//                        processReult = testController.process(NioHttpRequest.initHttpRequest(strbu.toString()));
//                        break;
                        Server.bq.add(new String(bodyBytes,"UTF-8"));
                        nr.setNioBody(new NioHttpBody(bodyBytes));
                        processReult = nr.doRequest(nr);
                        break;
                    }
                    
                }
            }
            
            doFlush(processReult);
            
            // sc.shutdownInput();
            // sc.shutdownOutput();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            key.cancel();
            try {
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private CheckHeader httpBytesTestHead(byte[] inBytes){
        int p = 0;
        CheckHeader ch = new CheckHeader();
        for (int i = 0; i <= inBytes.length;) {
            if (p >= 2) {
                ch.httpHeader = true;
                ch.bodyIndex = i;
            }
            if (i <= inBytes.length - 2 && inBytes[i] == 13 && inBytes[i + 1] == 10) {
                p++;
                i = i + 2;
                continue;
            } else {
                p = (p <= 0) ? 0 : (p - 1);
            }
            i++;
        }
        return ch;
    }
    
    public static class CheckHeader{
        boolean httpHeader = false;
        int bodyIndex = -1;
        public boolean isHttpHeader(){
            return httpHeader;
        }
    }

    private void doWrite(String response, String characterName) throws IOException {
        byte[] bytes = response.getBytes(characterName);
        doWrite(bytes);
    }
    
    private void doWrite(byte[] bytes) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        doWrite(writeBuffer);
    }
    
    private void doWrite(InputStream in) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.allocate(HTTP_HEAD_MAXLEGTH);
        byte[] buff = new byte[HTTP_HEAD_MAXLEGTH];
        while (in.read(buff, 0, buff.length) > 0) {
            writeBuffer.put(buff);
            doWrite(writeBuffer);
        }
    }
    
    private void doWrite(ByteBuffer writeBuffer) throws IOException {
        writeBuffer.flip();
        sc.write(writeBuffer);
        writeBuffer.clear();
    }

    private void doFlush(Object obj) throws IOException {
        if (obj instanceof NioHttpResponse) {
            NioHttpResponse nhr = (NioHttpResponse) obj;
            if (nhr != null) {
                doWrite(nhr.getHeader(), "ASCII");
                if(nhr.getBody() != null){
                    if (nhr.getHttpBodyType() == HttpBodyType.TEXT) {
                        doWrite((String) nhr.getBody(), nhr.getCharsetName());
                    }else if(nhr.getHttpBodyType() == HttpBodyType.BYTE){
                        doWrite((byte[]) nhr.getBody());
                    }else if(nhr.getHttpBodyType() == HttpBodyType.STREAM){
                        doWrite((InputStream) nhr.getBody());
                    }
                }
            }
        }
    }
    
    public static <T> byte[] concat(byte[] bs, byte[] bs2) {  
        byte[] result = Arrays.copyOf(bs, bs.length + bs2.length);  
        System.arraycopy(bs2, 0, result, bs.length, bs2.length);  
        return result;
      }  
    
    public static void main(String[] args) {
        System.out.println("jjj".getBytes());
        System.out.println(new String(concat("jjkj".getBytes(),concat("jjkj".getBytes(),"jjj".getBytes()))));
        
        System.out.println("\r\n".getBytes()[0]);
    }
    
//    public static void main(String[] args) {
//        InputStream in;
//        try {
//            in = new FileInputStream("E:\\kkk.PNG");
//            ByteBuffer writeBuffer = ByteBuffer.allocate(DEFAULT_BUFFLEN);
//            byte[] buff = new byte[DEFAULT_BUFFLEN];
//            while (in.read(buff, 0, buff.length) > 0) {
//                System.out.print(new String(buff));
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

}
