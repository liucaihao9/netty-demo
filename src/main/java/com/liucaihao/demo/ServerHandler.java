package com.liucaihao.demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 服务端业务逻辑处理类
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 接受请求之后的处理类
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        System.out.println(message.getData());
        String content = "netty 后台处理业务逻辑";
//        String content = "hello , this is netty server sdfhjdsnflksnlfslksflksnfnsklfnksfksnfksnfks dsnjfsndflkns";
        Header header=new Header((byte)0, (byte)1, (byte)1, (byte)1, (byte)0, "713f17ca614361fb257dc6741332caf2",content.getBytes("UTF-8").length, 1);
        Message message1 = new Message(header , content);
        ctx.writeAndFlush(message1);
    }

    /**
     * 读取完成之后的处理方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server 读取完成");
    }

    /**
     * 异常捕获处理方法
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
