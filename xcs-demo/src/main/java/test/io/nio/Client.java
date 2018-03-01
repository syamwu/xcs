package test.io.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 阻塞式I/O创建的客户端
 * 
 * @author yangtao__anxpp.com
 * @version 1.0
 */
public class Client {
    // 默认的端口号
    private static int DEFAULT_SERVER_PORT = 12335;
    private static String DEFAULT_SERVER_IP = "127.0.0.1";

    public static void send(String expression) {
        send(DEFAULT_SERVER_PORT, expression);
    }

    public static void send(int port, String expression) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket(DEFAULT_SERVER_IP, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.write(expression);
            out.write(System.getProperty("line.separator"));
            out.flush();
            System.out.println("发送：" + expression);
            Thread.sleep(5000);
            out.write(expression);
            out.write(System.getProperty("line.separator"));
            out.flush();
            System.out.println("___结果为1：" + expression + "=" + in.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 一下必要的清理工作
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }
    }

    static class BioClientTest implements Runnable {

        final char operators[] = { '+', '-', '*', '/' };

        @Override
        public void run() {

            Random random = new Random(System.currentTimeMillis());
            while (true) {
                String expression = random.nextInt(10) + "" + operators[random.nextInt(4)] + (random.nextInt(10) + 1);
                Client.send(expression);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
        
        BioClientTest b1 = new BioClientTest();
        BioClientTest b2 = new BioClientTest();
        BioClientTest b3 = new BioClientTest();
        BioClientTest b4 = new BioClientTest();
        BioClientTest b5 = new BioClientTest();
        BioClientTest b6 = new BioClientTest();
        
        ExecutorService exc = Executors.newCachedThreadPool();
        exc.submit(b1);
//        exc.submit(b2);
//        exc.submit(b3);
//        exc.submit(b4);
//        exc.submit(b5);
//        exc.submit(b6);
//        exc.submit(b1);
//        exc.submit(b2);
//        exc.submit(b3);
//        exc.submit(b4);
//        exc.submit(b5);
//        exc.submit(b6);
//        exc.submit(b1);
//        exc.submit(b2);
//        exc.submit(b3);
//        exc.submit(b4);
//        exc.submit(b5);
//        exc.submit(b6);
        
        
        exc.shutdown();
        
    }
}
