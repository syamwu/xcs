package test.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import test.io.nio.task.NioHttpTask;

/**
 * NIO服务端
 * 
 * @author yangtao__anxpp.com
 * @version 1.0
 */
public class ServerHandler implements Runnable {
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private volatile boolean started;
    final static int DEFAULT_BUFFER_LENGTH = 8192;
    public static boolean isFree = false;
    public static boolean needSleep = false;
    public static long allCount = 0;
    public static long secondCount = 0;
    
    public static int kkstatus = 0;
    public static int selectCount = 0;

    /**
     * 构造方法
     * 
     * @param port
     *            指定要监听的端口号
     */
    public ServerHandler(int port) {
        try {
            // 创建选择器
            selector = Selector.open();
            // 打开监听通道
            serverChannel = ServerSocketChannel.open();
            // 如果为 true，则此通道将被置于阻塞模式；如果为 false，则此通道将被置于非阻塞模式
            serverChannel.configureBlocking(false);// 开启非阻塞模式
            // 绑定端口 backlog设为1024
            serverChannel.socket().bind(new InetSocketAddress(port), 1024 * 1024);
            //serverChannel.socket().bind(new InetSocketAddress(port));
            // 监听客户端连接请求
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            // 标记服务器已开启
            started = true;
            System.out.println("服务器已启动，端口号：" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        started = false;
    }

    @Override
    public void run() {
        // 循环遍历selector
        while (started) {
            try {
                selectCount = selector.selectNow();
                if (selectCount <= 0) {
                    isFree = true;
                    if(needSleep){
                        Thread.sleep(1);
                    }
                    continue;
                }
                Set<SelectionKey> keys = selector.selectedKeys();
                if(keys == null || keys.isEmpty()){
                    continue;
                }
                Iterator<SelectionKey> it = keys.iterator();
                if(it == null){
                    continue;
                }
                SelectionKey key = null;
                for(;;){
                    if(!it.hasNext()){
                        isFree = true;
                        break;
                    }else{
                        isFree = false;
                        key = it.next();
                        it.remove();
                        try {
                            // 处理新接入的请求消息
                            if(key.isValid()){
                                if (key.isAcceptable()) {
                                    //System.out.println("isAcceptable:" + key.interestOps());
                                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                                    SocketChannel sc = ssc.accept();
                                    // 设置为非阻塞的
                                    sc.configureBlocking(false);
                                    // 注册为读
                                    sc.register(selector, SelectionKey.OP_READ , new NioAttachment(allCount));
                                    allCount++;
                                    secondCount++;
                                }else{
                                    handleInputANDOutput(key);
                                }
                            }else{
                                key.cancel();
                                key.channel().close();
                            }
                        } catch (Exception e) {
                            key.cancel();
                            key.channel().close();
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        // selector关闭后会自动释放里面管理的资源
        if (selector != null)
            try {
                selector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    
    //public static AtomicInteger hh = new AtomicInteger(0);

    private void handleInputANDOutput(SelectionKey key) throws Exception {
        if (key.isValid()) {
            // 读消息
            if (key.isReadable()) {
                //System.out.println("isReadable:" + key.interestOps());
                //单线程IO模式
//                NioAttachment nac = (NioAttachment) key.attachment();
//                SocketChannel sc = (SocketChannel) key.channel();
//                nac.initResponse();
//                doWrite(sc, nac.getResult());
//                sc.shutdownOutput();
//                key.cancel();
//                sc.close();
                
                //多线程IO模式
                NioAttachment nac = (NioAttachment) key.attachment();
                if (nac.canr) {
                    nac.canr = false;
                    //hh.addAndGet(1);
                    NioThreadExecutor.getExecutorService().execute(new NioHttpTask(key));
                }
                
            }else if(key.isWritable()){
                //System.out.println("isWritable:" + key.interestOps());
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
                //System.out.println("isWritable");
                //获取附加参数
//                NioAttachment att = (NioAttachment) key.attachment();
//                if (att.isCanw()) {
//                    SocketChannel sc = (SocketChannel) key.channel();
//                    //System.out.println(att.getConnetIndex()+"结束");
//                    doWrite(sc, att.getResult());
//                    sc.shutdownOutput();
//                    key.cancel();
//                    sc.close();
//                }
            }else{
                //System.out.println("UnDefineStatus:" + key.interestOps());
            }
                
        }
    }
    
 // 异步发送应答消息
    private void doWrite(SocketChannel channel, String response) throws IOException {
        // 将消息编码为字节数组
        byte[] bytes = response.getBytes();
        // 根据数组容量创建ByteBuffer
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        // 将字节数组复制到缓冲区
        writeBuffer.put(bytes);
        // flip操作
        writeBuffer.flip();
        // 发送缓冲区的字节数组
        channel.write(writeBuffer);
        // ****此处不含处理“写半包”的代码
    }

    
}
