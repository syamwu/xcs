package test.netty.aio03;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class Client implements Runnable {
    private AsynchronousSocketChannel channel;
    private Helper helper;
    private CountDownLatch latch;
    private final Queue<ByteBuffer> queue = new LinkedList<ByteBuffer>();
    private boolean writing = false;

    public Client(AsynchronousChannelGroup channelGroup, CountDownLatch latch)
            throws IOException, InterruptedException {
        this.latch = latch;
        helper = new Helper();
        initChannel(channelGroup);
    }

    private void initChannel(AsynchronousChannelGroup channelGroup) throws IOException {
        // 在默认channel group下创建一个socket channel
        channel = AsynchronousSocketChannel.open(channelGroup);
        // 设置Socket选项
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int sleepTime = Integer.parseInt("1");
        Helper.sleep(sleepTime);

        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup
                .withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
        // 只能跑一个线程，第二个线程connect会挂住，暂时不明原因
        final int THREAD_NUM = 1;
        CountDownLatch latch = new CountDownLatch(THREAD_NUM);

        // 创建个多线程模拟多个客户端，模拟失败，无效
        // 只能通过命令行同时运行多个进程来模拟多个客户端
        for (int i = 0; i < THREAD_NUM; i++) {
            Client c = new Client(channelGroup, latch);
            Thread t = new Thread(c);
            System.out.println(t.getName() + "---start");
            t.start();
            // 让主线程等待子线程处理再退出, 这对于异步调用无效
            // t.join();
        }

        latch.await();

        if (channelGroup != null) {
            channelGroup.shutdown();
        }
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "---run");

        // 连接服务器
        channel.connect(new InetSocketAddress("localhost", 8383), null, new CompletionHandler<Void, Void>() {
            final ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);

            @Override
            public void completed(Void result, Void attachment) {
                // 连接成功后, 异步调用OS向服务器写一条消息
                try {
                    // channel.write(CharsetHelper.encode(CharBuffer.wrap(helper.getWord())));
                    writeStringMessage(helper.getWord());
                } catch (CharacterCodingException e) {
                    e.printStackTrace();
                }

                // helper.sleep();//等待写异步调用完成
                readBuffer.clear();
                // 异步调用OS读取服务器发送的消息
                channel.read(readBuffer, null, new CompletionHandler<Integer, Object>() {

                    @Override
                    public void completed(Integer result, Object attachment) {
                        try {
                            // 异步读取完成后处理
                            if (result > 0) {
                                readBuffer.flip();
                                CharBuffer charBuffer = CharsetHelper.decode(readBuffer);
                                String answer = charBuffer.toString();
                                System.out.println(Thread.currentThread().getName() + "---" + answer);
                                readBuffer.clear();

                                String word = helper.getWord();
                                if (word != null) {
                                    // 异步写
                                    // channel.write(CharsetHelper.encode(CharBuffer.wrap(word)));
                                    writeStringMessage(word);
                                    // helper.sleep();//等待异步操作
                                    channel.read(readBuffer, null, this);
                                } else {
                                    // 不想发消息了，主动关闭channel
                                    shutdown();
                                }
                            } else {
                                // 对方已经关闭channel，自己被动关闭，避免空循环
                                shutdown();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    /**
                     * 读取失败处理
                     * 
                     * @param exc
                     * @param attachment
                     */
                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        System.out.println("client read failed: " + exc);
                        try {
                            shutdown();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
            }

            /**
             * 连接失败处理
             * 
             * @param exc
             * @param attachment
             */
            @Override
            public void failed(Throwable exc, Void attachment) {
                System.out.println("client connect to server failed: " + exc);

                try {
                    shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void shutdown() throws IOException {
        if (channel != null) {
            channel.close();
        }

        latch.countDown();
    }

    /**
     * Enqueues a write of the buffer to the channel. The call is asynchronous
     * so the buffer is not safe to modify after passing the buffer here.
     *
     * @param buffer
     *            the buffer to send to the channel
     */
    private void writeMessage(final ByteBuffer buffer) {
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
            writeFromQueue();
        }
    }

    private void writeFromQueue() {
        ByteBuffer buffer;

        synchronized (queue) {
            buffer = queue.poll();
            if (buffer == null) {
                writing = false;
            }
        }

        // No new data in buffer to write
        if (writing) {
            writeBuffer(buffer);
        }
    }

    private void writeBuffer(ByteBuffer buffer) {
        channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()) {
                    channel.write(buffer, buffer, this);
                } else {
                    // Go back and check if there is new data to write
                    writeFromQueue();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
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
    public void writeStringMessage(String msg) throws CharacterCodingException {
        writeMessage(Charset.forName("UTF-8").newEncoder().encode(CharBuffer.wrap(msg)));
    }
}
