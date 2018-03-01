package test.netty.sub;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqClientHanler extends ChannelHandlerAdapter {

    public SubReqClientHanler() {

    }

    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("----------------handler channelActive-----准备发送十个数据-------");

        for (int i = 0; i < 10; i++) {
            // ctx.write(subReq(i));
            SubscribeReq req = new SubscribeReq();
            req.setAddress("深圳蛇口");
            req.setPhoneNumber("13888886666");
            req.setProductName("Netty Book");
            req.setSubReqID(i);
            req.setUserName("XXYY");
            ctx.write(req);
        }
        ctx.flush();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("--------channelRead---服务器发来的数据为：[" + msg + "]");
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("----------------handler channelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(
                "--------------------------------------------------------------------------handler exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }

}
