package test.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.xcs.utils.StreamUtils;

/**
 * 阻塞式I/O创建的客户端
 * 
 * @author yangtao__anxpp.com
 * @version 1.0
 */
public class Client {
    // 默认的端口号
    private static int DEFAULT_SERVER_PORT = 80;
    private static String DEFAULT_SERVER_IP = "www.baidu.com";

    public static void send(String expression) {
        send(DEFAULT_SERVER_PORT, expression);
    }
    

    public static void send(int port, String expression) {
        System.out.println("算术表达式为：" + expression);
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket(DEFAULT_SERVER_IP, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(expression);
            out.println(expression);
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

    public static void sendHttp(String expression) {
        System.out.println(expression);
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket(DEFAULT_SERVER_IP, DEFAULT_SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(expression);
            String str = in.readLine();
            while(str != null){
                System.out.println(str);
                str = in.readLine();
            }
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

    public static void main(String[] args) {
        sendHttp(StreamUtils.file2string("D:\\upload\\data2018-1-12 163306.txt"));
    }
}
