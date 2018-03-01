package test.netty.simplenio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

public class TCPServer {
    // 缓冲区大小
    private static final int BufferSize = 1024;

    // 超时时间，单位毫秒
    private static final int TimeOut = 3000;

    // 本地监听端口
    private static final int ListenPort = 1978;

    public static void main(String[] args) throws IOException {
        // 创建选择器
        Selector selector = Selector.open();

        // 打开监听信道
        ServerSocketChannel listenerChannel = ServerSocketChannel.open();

        // 与本地端口绑定
        listenerChannel.socket().bind(new InetSocketAddress(ListenPort));

        // 设置为非阻塞模式
        listenerChannel.configureBlocking(false);

        // 将选择器绑定到监听信道,只有非阻塞信道才可以注册选择器.并在注册过程中指出该信道可以进行Accept操作
        listenerChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 创建一个处理协议的实现类,由它来具体操作
        TCPProtocol protocol = new TCPProtocolImpl(BufferSize);

        // 反复循环,等待IO
        while (true) {
            // 等待某信道就绪(或超时)
            if (selector.select(TimeOut) == 0) {
                System.out.print("独自等待.");
                continue;
            }

            // 取得迭代器.selectedKeys()中包含了每个准备好某一I/O操作的信道的SelectionKey
            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();

            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next();

                try {
                    if (key.isAcceptable()) {
                        // 有客户端连接请求时
                        protocol.handleAccept(key);
                    }

                    if (key.isReadable()) {
                        // 从客户端读取数据
                        protocol.handleRead(key);
                    }

                    if (key.isValid() && key.isWritable()) {
                        // 客户端可写时
                        protocol.handleWrite(key);
                    }
                } catch (IOException ex) {
                    // 出现IO异常（如客户端断开连接）时移除处理过的键
                    keyIter.remove();
                    continue;
                }

                // 移除处理过的键
                keyIter.remove();
            }
        }
    }
}