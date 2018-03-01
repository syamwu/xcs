package test.io.nio;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;

public class Server {
    private static int DEFAULT_PORT = 12335;
    private static ServerHandler serverHandler;
    public static BlockingQueue<String> bq = new LinkedBlockingQueue<String>();

    public static void start() {
        start(DEFAULT_PORT);
    }

    public static synchronized void start(int port) {
        if (serverHandler != null)
            serverHandler.stop();
        serverHandler = new ServerHandler(port);
        NioThreadExecutor.init();
        new Thread(serverHandler, "Server").start();
    }

    public static void main(String[] args) {
        start();
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (;;) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("all:" + ServerHandler.allCount);
                    System.out.println("scount:" + ServerHandler.secondCount);
                    System.out.println("needSleep:" + ServerHandler.needSleep);
                    System.out.println("isFree:" + ServerHandler.isFree);
                    System.out.println("selectCount:" + ServerHandler.selectCount);
                    System.out.println();
                    ServerHandler.secondCount = 0;
                }
            }
        }).start();
        
        
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (;;) {
                    try {
                        if(!bq.isEmpty()){
                            //System.out.println(bq.poll());
                            System.out.println(JSON.toJSONString(bq.poll()));
                        }else{
                            Thread.sleep(1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (;;) {
                    try {
                        Thread.sleep(100);
                        if(ServerHandler.isFree){
                            ServerHandler.needSleep = true;
                        }else{
                            ServerHandler.needSleep = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        
    }
}
