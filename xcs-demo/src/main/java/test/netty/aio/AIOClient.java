package test.netty.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * AsynchronousSocketChannel
 */
public class AIOClient implements Runnable {

    private AsynchronousSocketChannel client;
    private String host;
    private int port;

    public AIOClient(String host, int port) throws IOException {
        this.client = AsynchronousSocketChannel.open();
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        try {
            new Thread(new AIOClient("127.0.0.1", 12345)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            client.connect(new InetSocketAddress(host, port), null, new CompletionHandler<Void, Object>() {
                public void completed(Void result, Object attachment) {
                    String msg = "client test msg-" + Math.random();
                    client.write(ByteBuffer.wrap(msg.getBytes()), this, new CompletionHandler<Integer, Object>() {
                        public void completed(Integer result, Object attachment) {
                            System.out.println(result);
                            System.out.println("client read data: " + new String(byteBuffer.array()));
                            String msg = "client test msg-" + Math.random();
                            client.read(byteBuffer, this, new CompletionHandler<Integer, Object>() {
                                public void completed(Integer result, Object attachment) {
                                    System.out.println(result);
                                    System.out.println("client read data: " + new String(byteBuffer.array()));
                                }

                                public void failed(Throwable exc, Object attachment) {
                                    System.out.println("read faield");
                                }
                            });
                        }

                        public void failed(Throwable exc, Object attachment) {
                            System.out.println("read faield");
                        }
                    });
                    System.out.println("client send data:" + msg);
                }

                public void failed(Throwable exc, Object attachment) {
                    System.out.println("client send field...");
                }
            });
            Thread.sleep(100000);
            // final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            // client.read(byteBuffer, this, new CompletionHandler<Integer,
            // Object>() {
            // public void completed(Integer result, Object attachment) {
            // System.out.println(result);
            // System.out.println("client read data: " + new
            // String(byteBuffer.array()));
            // String msg = "client test msg-" + Math.random();
            // //client.write(ByteBuffer.wrap(msg.getBytes()));
            // }
            //
            // public void failed(Throwable exc, Object attachment) {
            // System.out.println("read faield");
            // }
            // });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
