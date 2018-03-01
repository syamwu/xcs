package test.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

public class TestNettyHttp {

    public static void main(String[] args) {
        try {
            new TestNettyHttp().start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(2); // 这个是用于serversocketchannel的eventloop
        EventLoopGroup workerGroup = new NioEventLoopGroup(2000); // 这个是用于处理accept到的channel
        try {
            ServerBootstrap b = new ServerBootstrap(); // 构建serverbootstrap对象
            b.group(bossGroup, workerGroup); // 设置时间循环对象，前者用来处理accept事件，后者用于处理已经建立的连接的io
            b.channel(NioServerSocketChannel.class); // 用它来建立新accept的连接，用于构造serversocketchannel的工厂类
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch)
                        throws Exception {
//                    //HTTP 请求消息解码器
//                    ch.pipeline().addLast("http-decoder",
//                            new HttpRequestDecoder());
//                    //HttpObjectAggregator解码器，将多个消息转换成单一的FullHttpRequest或者FullHTtpResponse，原因是HTTP解码器在每个HTTP消息中会生成多个消息对象（HttpRequst、HttpResponse、Httpontent/LastHttpContent）。
//                    ch.pipeline().addLast("http-aggregator",
//                            new HttpObjectAggregator(65536));
//                    //HTTP响应结卖钱
//                    ch.pipeline().addLast("http-encoder",
//                            new HttpResponseEncoder());
//                    //Chunked handler它的作用是支持异步发送大的码流但不占用过多的内存，防止发生java内存溢出错误。
//                    ch.pipeline().addLast("http-chunked",
//                            new ChunkedWriteHandler());
//                    ch.pipeline().addLast("httpServerHandler",
//                            new HttpReceiveHandler());
                    ChannelPipeline pipeline = ch.pipeline();
                    
                    // 40秒没有数据读入，发生超时机制
                    pipeline.addLast(new ReadTimeoutHandler(40));

                    // 40秒没有输入写入，发生超时机制
                    pipeline.addLast(new WriteTimeoutHandler(40));

                    /**
                     * http-request解码器
                     * http服务器端对request解码
                     */
                    pipeline.addLast("decoder", new HttpRequestDecoder(1024 * 1024 * 8,8192,8192));

                    /**
                     * http-response解码器
                     * http服务器端对response编码
                     */
                    pipeline.addLast("encoder", new HttpResponseEncoder());

                    /**
                     * HttpObjectAggregator会把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse。
                     */
                    pipeline.addLast("aggregator", new HttpObjectAggregator(1024 * 1024 * 8));

                    /**
                     * 压缩
                     * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
                     * while respecting the "Accept-Encoding" header.
                     * If there is no matching encoding, no compression is done.
                     */
                    pipeline.addLast("deflater", new HttpContentCompressor());

                    pipeline.addLast("handler", new HttpServerHandler());
                }
            });
            // bind方法会创建一个serverchannel，并且会将当前的channel注册到eventloop上面，
            // 会为其绑定本地端口，并对其进行初始化，为其的pipeline加一些默认的handler
            ChannelFuture f = b.bind(80).sync();
            f.channel().closeFuture().sync(); // 相当于在这里阻塞，直到serverchannel关闭
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
