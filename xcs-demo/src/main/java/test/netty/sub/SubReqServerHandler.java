package test.netty.sub;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqServerHandler extends ChannelHandlerAdapter{  
    
    public void channelActive(ChannelHandlerContext ctx){  
        System.out.println("--------------------------------handler channelActive------------");  
          
//      for(int i = 0; i<10; i++){  
//          SubscribeReq req = new SubscribeReq();  
//          req.setAddress("深圳JJYY");  
//          req.setPhoneNumber("13888886666");  
//          req.setProductName("Netty Book");  
//          req.setSubReqID(i);  
//          req.setUserName("XXYY");  
//          ctx.write(req);  
//      }  
//      ctx.flush();  
    }  
      
    public void channelRead(ChannelHandlerContext ctx, Object msg)  
        throws Exception{  
          
//          SubscribeReq reqx = new SubscribeReq();  
//          reqx.setAddress("*****深圳蛇口");  
//          reqx.setPhoneNumber("13888886666");  
//          reqx.setProductName("Netty Book");  
//          reqx.setSubReqID(6666);  
//          reqx.setUserName("XXYY");  
//          ctx.write(reqx);  
//          ctx.flush();  
//        
            SubscribeResp resp = new SubscribeResp();  
            resp.setnSubReqID(555);  
            resp.setRespCode(0);  
            resp.setDesc("-------Netty book order succeed, 3days later, sent to the designated address");  
            ctx.writeAndFlush(resp);    // 反馈消息  
            //ctx.write(resp);  
            //ctx.flush();  
          
            SubscribeReq req = (SubscribeReq)msg;   // 订购内容  
            if("XXYY".equalsIgnoreCase(req.getUserName())){  
                System.out.println("接收到的数据: [  " + req.toString() + "   ]");  
            }  
              
    }  
      
    @Override  
    public  void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){  
        System.out.println("---------------exceptionCaught 网络异常，关闭网络");  
        cause.printStackTrace();  
        ctx.close();  
    }  
}
