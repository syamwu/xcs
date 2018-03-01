package test.netty.nio;

import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TestNIO {
    public static void main(String[] args) {

        // testBuff();
        // testBuff02();
         testSelector();
    }

    private static void testBuff() {
        RandomAccessFile aFile;
        try {
            aFile = new RandomAccessFile("E:\\data.txt", "rw");

            FileChannel inChannel = aFile.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(2);
            int bytesRead = inChannel.read(buf);
            while (bytesRead != -1) {
                System.out.print("  Read " + bytesRead);
                buf.flip();
                while (buf.hasRemaining()) {
                    System.out.print((char) buf.get());
                }

                buf.clear();
                bytesRead = inChannel.read(buf);
            }
            aFile.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void testBuff01() {
        ByteBuffer b = ByteBuffer.allocate(15); // 15个字节大小的缓冲区
        System.out.println("limit=" + b.limit() + " capacity=" + b.capacity() + " position=" + b.position());
        for (int i = 0; i < 10; i++) {
            // 存入10个字节数据
            b.put((byte) i);
        }
        System.out.println("limit=" + b.limit() + " capacity=" + b.capacity() + " position=" + b.position());
        b.flip(); // 重置position
        System.out.println("limit=" + b.limit() + " capacity=" + b.capacity() + " position=" + b.position());
        for (int i = 0; i < 10; i++) {
            System.out.print(b.get());
        }
        System.out.println();
        System.out.println("limit=" + b.limit() + " capacity=" + b.capacity() + " position=" + b.position());
        b.flip();
        System.out.println("limit=" + b.limit() + " capacity=" + b.capacity() + " position=" + b.position());
        for (int i = 0; i < 5; i++) {
            System.out.print(b.get());
        }
        System.out.println();
        System.out.println("limit=" + b.limit() + " capacity=" + b.capacity() + " position=" + b.position());
    }

    @SuppressWarnings("resource")
    private static void testSelector() {
        try {
            ServerSocketChannel channel = ServerSocketChannel.open();
            channel.socket().bind(new InetSocketAddress(1978));
            final Selector selector = Selector.open();
            // 设置非阻塞
            channel.configureBlocking(false);
            SelectionKey key = channel.register(selector, SelectionKey.OP_ACCEPT);

            // 服务端线程
            new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            int readyChannels = selector.select();
                            if (readyChannels == 0)
                                continue;
                            Set selectedKeys = selector.selectedKeys();
                            Iterator keyIterator = selectedKeys.iterator();
                            while (keyIterator.hasNext()) {
                                SelectionKey keyrow = (SelectionKey) keyIterator.next();
                                if (keyrow.isAcceptable()) {
                                    // a connection was accepted by a
                                    // ServerSocketChannel.
                                    System.out.println("selector isAcceptable");
                                    SocketChannel clientChannel = ((ServerSocketChannel) keyrow.channel()).accept();
                                    clientChannel.configureBlocking(false);
                                    clientChannel.register(keyrow.selector(), SelectionKey.OP_READ,
                                            ByteBuffer.allocate(16));
                                } else if (keyrow.isConnectable()) {
                                    // a connection was established with a
                                    // remote server.
                                    System.out.println("selector isConnectable");
                                } else if (keyrow.isReadable()) {
                                    System.out.println("selector isReadable");
                                    // 获得与客户端通信的信道
                                    SocketChannel clientChannel = (SocketChannel) keyrow.channel();

                                    // 得到内存中的buffer对象（对象复用减少对象新建）并设置空状态clear
                                    ByteBuffer buffer = (ByteBuffer) keyrow.attachment();
                                    buffer.clear();

                                    // 从channel读取信息获得读取的字节数
                                    long bytesRead = clientChannel.read(buffer);
                                    System.out.println(bytesRead);
                                    if (bytesRead == -1) {
                                        // 没有读取到内容的情况
                                        clientChannel.close();
                                    } else {
                                        // 将缓冲区准备为数据传出状态
                                        buffer.flip();

                                        // 将字节转化为为UTF-16的字符串
                                        String receivedString = Charset.forName("UTF-16").newDecoder().decode(buffer)
                                                .toString();

                                        // 控制台打印出来
                                        System.out.println("接收到来自" + clientChannel.socket().getRemoteSocketAddress()
                                                + "的信息:" + receivedString);

                                        // 准备发送的文本
                                        String sendString = "你好,客户端. @" + new Date().toString() + "，已经收到你的信息"
                                                + receivedString;
                                        buffer = ByteBuffer.wrap(sendString.getBytes("UTF-16"));
                                        clientChannel.write(buffer);

                                        // 设置为下一次读取做准备
                                        keyrow.interestOps(SelectionKey.OP_READ);
                                    }
                                } else if (keyrow.isWritable()) {
                                    // a channel is ready for writing
                                    // ByteBuffer buf = ByteBuffer.allocate(10);
                                    // String str = "333";
                                    // buf.put(str.getBytes());
                                    // int s = ((SocketChannel)
                                    // keyrow.channel()).write(buf);
                                    // System.out.println("selector02 writ:" +
                                    // str);
                                    System.out.println("selector isWritable");
                                }
                                keyIterator.remove();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            Thread.sleep(2000);
            final SocketChannel channel02 = SocketChannel.open(new InetSocketAddress("127.0.0.1", 1978));
            final Selector selector02 = Selector.open();
            // 设置非阻塞
            channel02.configureBlocking(false);
            SelectionKey key02 = channel02.register(selector02, SelectionKey.OP_READ);

            // 客户端线程
            new Thread(new Runnable() {
                public void run() {
                    try {
                        while (selector02.select() > 0) {
                            // 遍历每个有可用IO操作Channel对应的SelectionKey
                            for (SelectionKey sk : selector02.selectedKeys()) {

                                // 如果该SelectionKey对应的Channel中有可读的数据
                                if (sk.isReadable()) {
                                    // 使用NIO读取Channel中的数据
                                    SocketChannel sc = (SocketChannel) sk.channel();
                                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                                    sc.read(buffer);
                                    buffer.flip();

                                    // 将字节转化为为UTF-16的字符串
                                    String receivedString = Charset.forName("UTF-16").newDecoder().decode(buffer)
                                            .toString();

                                    // 控制台打印出来
                                    System.out.println("接收到来自服务器" + sc.socket().getRemoteSocketAddress() + "的信息:"
                                            + receivedString);

                                    // 为下一次读取作准备
                                    sk.interestOps(SelectionKey.OP_READ);
                                } else if (sk.isWritable()) {
                                    System.out.println("selector02 isWritable");
                                } else if (sk.isAcceptable()) {
                                    System.out.println("selector02 isAcceptable");
                                } else if (sk.isConnectable()) {
                                    System.out.println("selector02 isConnectable");
                                }

                                // 删除正在处理的SelectionKey
                                selector02.selectedKeys().remove(sk);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            // 发送内容到服务器
            new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            String message = "12啊飒飒发胜多负少的范德萨范德萨发斯蒂芬递四方速递范德萨范德萨杀得死防守打法胜多负少3";
                            // 根据实际byte大小分配buffer
                            ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes("UTF-16"));
                            // 自定义buffer大小
                            ByteBuffer writeBuffer1 = ByteBuffer.allocateDirect(1024);
                            writeBuffer1.put(message.getBytes("UTF-16"));
                            // clear这一步很关键，用以将buff指针放到头，这样后面读数据才能被channel读出来
                            writeBuffer1.clear();
                            channel02.write(writeBuffer);
                            System.out.println("selector02 writ:" + message);
                            System.out.println();
                            Thread.sleep(2000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void testChannel() {
        try {
            NioServerSocketChannel channel = new NioServerSocketChannel();
            channel.write(ByteBuffer.allocateDirect(200));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
