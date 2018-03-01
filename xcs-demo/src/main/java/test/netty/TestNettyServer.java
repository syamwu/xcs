package test.netty;


import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;


/** 
 * Netty 服务端代码 
 *  
 * @author lihzh 
 * @alia OneCoder 
 * @blog http://www.coderli.com 
 */  
public class TestNettyServer {  
  
    public static void main(String args[]) {  
        // Server服务启动器  
        ServerBootstrap bootstrap = new ServerBootstrap(  
                new NioServerSocketChannelFactory(  
                        Executors.newCachedThreadPool(),  
                        Executors.newCachedThreadPool()));  
        // 设置一个处理客户端消息和各种消息事件的类(Handler)  
        bootstrap  
                .setPipelineFactory(new ChannelPipelineFactory() {  
                    @Override  
                    public ChannelPipeline getPipeline()  
                            throws Exception {  
                        return Channels  
                                .pipeline(new HelloServerHandler());  
                    }  
                });  
        // 开放8000端口供客户端访问。  
        bootstrap.bind(new InetSocketAddress(1234));  
    }  
  
    private static class HelloServerHandler extends  
            SimpleChannelHandler {  
  
        /** 
         * 当有客户端绑定到服务端的时候触发，打印"Hello world, I'm server." 
         *  
         * @alia OneCoder 
         * @author lihzh 
         */  
        @Override  
        public void channelConnected(  
                ChannelHandlerContext ctx,  
                ChannelStateEvent e) {  
            System.out.println("Hello world, I'm server.");
            System.out.println(e.getValue());
        }  
    }  
} 
