package test.netty.aio03;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritePendingException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;

public class XiaoNa {
    private final AsynchronousServerSocketChannel server;
    // 写队列，因为当前一个异步写调用还没完成之前，调用异步写会抛WritePendingException
    // 所以需要一个写队列来缓存要写入的数据，这是AIO比较坑的地方
    private final Queue<ByteBuffer> queue = new LinkedList<ByteBuffer>();
    private boolean writing = false;

    public static void main(String[] args) throws IOException {
        XiaoNa xiaona = new XiaoNa();
        xiaona.listen();
    }

    public XiaoNa() throws IOException {
        // 设置线程数为CPU核数
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup
                .withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
        server = AsynchronousServerSocketChannel.open(channelGroup);
        // 重用端口
        server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        // 绑定端口并设置连接请求队列长度
        server.bind(new InetSocketAddress(8383), 80);
    }

    public void listen() {
        System.out.println(Thread.currentThread().getName() + ": run in listen method");
        // 开始接受第一个连接请求
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel channel, Object attachment) {
                System.out.println(Thread.currentThread().getName() + ": run in accept completed method");

                // 先安排处理下一个连接请求，异步非阻塞调用，所以不用担心挂住了
                // 这里传入this是个地雷，小心多线程
                server.accept(null, this);
                // 处理连接读写
                handle(channel);
            }

            private void handle(final AsynchronousSocketChannel channel) {
                System.out.println(Thread.currentThread().getName() + ": run in handle method");
                // 每个AsynchronousSocketChannel，分配一个缓冲区
                final ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);
                readBuffer.clear();
                channel.read(readBuffer, null, new CompletionHandler<Integer, Object>() {

                    @Override
                    public void completed(Integer count, Object attachment) {
                        System.out.println(Thread.currentThread().getName() + ": run in read completed method");

                        if (count > 0) {
                            try {
                                readBuffer.flip();
                                // CharBuffer charBuffer =
                                // CharsetHelper.decode(readBuffer);
                                CharBuffer charBuffer = Charset.forName("UTF-8").newDecoder().decode(readBuffer);
                                String question = charBuffer.toString();
                                String answer = Helper.getAnswer(question);

                                // 写入也是异步调用，也可以使用传入CompletionHandler对象的方式来处理写入结果
                                channel.write(CharsetHelper.encode(CharBuffer.wrap(answer)));
                                try {
                                    channel.write(
                                            Charset.forName("UTF-8").newEncoder().encode(CharBuffer.wrap(answer)));
                                }catch (WritePendingException wpe) {
                                    /*
                                     * Unchecked exception thrown when an attempt is
                                     * made to write to an asynchronous socket
                                     * channel and a previous write has not
                                     * completed.
                                     */
                                    // 看来操作系统也不可靠
                                    // 休息一秒再重试，如果失败就不管了 
                                    Helper.sleep(1);
                                    channel.write(
                                            Charset.forName("UTF-8").newEncoder().encode(CharBuffer.wrap(answer)));
                                }

                                //writeStringMessage(channel, answer);

                                readBuffer.clear();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                // 如果客户端关闭socket，那么服务器也需要关闭，否则浪费CPU
                                channel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // 异步调用OS处理下个读取请求
                        // 这里传入this是个地雷，小心多线程
                        channel.read(readBuffer, null, this);
                    }

                    /**
                     * 服务器读失败处理
                     * 
                     * @param exc
                     * @param attachment
                     */
                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        exc.printStackTrace();
                        if (channel != null) {
                            try {
                                channel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
            }

            /**
             * 服务器接受连接失败处理
             * 
             * @param exc
             * @param attachment
             */
            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("server accept failed: " + exc);
            }

        });
    }

    /**
     * Enqueues a write of the buffer to the channel. The call is asynchronous
     * so the buffer is not safe to modify after passing the buffer here.
     *
     * @param buffer
     *            the buffer to send to the channel
     */
    private void writeMessage(final AsynchronousSocketChannel channel, final ByteBuffer buffer) {
        boolean threadShouldWrite = false;

        synchronized (queue) {
            queue.add(buffer);
            // Currently no thread writing, make this thread dispatch a write
            if (!writing) {
                writing = true;
                threadShouldWrite = true;
            }
        }

        if (threadShouldWrite) {
            writeFromQueue(channel);
        }
    }

    private void writeFromQueue(final AsynchronousSocketChannel channel) {
        ByteBuffer buffer;

        synchronized (queue) {
            buffer = queue.poll();
            if (buffer == null) {
                writing = false;
            }
        }

        // No new data in buffer to write
        if (writing) {
            writeBuffer(channel, buffer);
        }
    }

    private void writeBuffer(final AsynchronousSocketChannel channel, ByteBuffer buffer) {
        channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()) {
                    channel.write(buffer, buffer, this);
                } else {
                    // Go back and check if there is new data to write
                    writeFromQueue(channel);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println("server write failed: " + exc);
            }
        });
    }

    /**
     * Sends a message
     * 
     * @param string
     *            the message
     * @throws CharacterCodingException
     */
    private void writeStringMessage(final AsynchronousSocketChannel channel, String msg)
            throws CharacterCodingException {
        writeMessage(channel, Charset.forName("UTF-8").newEncoder().encode(CharBuffer.wrap(msg)));
    }
}