package com.liucaihao.demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 客户端业务逻辑处理类
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 客户端连接服务器成功之后调用的业务逻辑
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive！");
        String content = "hello world , this is netty client";
        Header header = new Header((byte)0, (byte)1, (byte)1, (byte)1, (byte)0, "713f17ca614361fb257dc6741332caf2",content.getBytes("UTF-8").length, 1);
        Message message = new Message(header , content);
        ctx.writeAndFlush(message);
    }

    /**
     * 客户端接收到数据之后调用的业务逻辑
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        System.out.println(message.getData());
        throw new NullPointerException("测试异常处理");
    }

    /**
     * 客户端完成时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
