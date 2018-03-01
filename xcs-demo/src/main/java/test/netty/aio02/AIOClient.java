package test.netty.aio02;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIOClient implements Runnable {

    static AsynchronousSocketChannel client = null;

    public static void main(String... args) throws Exception {
        client = AsynchronousSocketChannel.open();
        client.connect(new InetSocketAddress("localhost", 9888));

        ExecutorService ex = Executors.newCachedThreadPool();
        AIOClient a1 = new AIOClient();
        AIOClient a2 = new AIOClient();
        AIOClient a3 = new AIOClient();
        AIOClient a4 = new AIOClient();
        ex.submit(a1);
        ex.submit(a2);
        ex.submit(a3);
        ex.submit(a4);
        
        //ex.shutdown();
    }

    @Override
    public void run() {
        try {
            System.out.println("get:" + client.write(ByteBuffer.wrap("test".getBytes())).get());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}