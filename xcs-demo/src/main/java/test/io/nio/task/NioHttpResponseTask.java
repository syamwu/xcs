package test.io.nio.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import test.io.nio.NioAttachment;

public class NioHttpResponseTask implements Runnable {

    private NioAttachment nac;

    private SelectionKey key;

    public NioHttpResponseTask(NioAttachment nac, SelectionKey key) {
        this.nac = nac;
        this.key = key;
    }

    @Override
    public void run() {
        NioAttachment att = (NioAttachment) key.attachment();
        if (att.isCanw()) {
            SocketChannel sc = (SocketChannel) key.channel();
            // System.out.println(att.getConnetIndex()+"结束");
            try {
                doWrite(sc, att.getResult());
                sc.shutdownOutput();
                key.cancel();
                sc.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
