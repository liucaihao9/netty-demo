package com.liucaihao.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.nio.charset.Charset;

public class Client {
    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host" , "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port" , "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size" , "256"));

    public static void main(String[] args) throws Exception {
        System.out.println("client启动！");
        final SslContext sslContext;
        if (SSL){
            sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        }else {
            sslContext = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            if (sslContext != null){
                                channelPipeline.addLast(sslContext.newHandler(ch.alloc() , HOST , PORT));
                            }
                            channelPipeline.addLast(new MessageDecoder());
                            channelPipeline.addLast(new MessageEncoder());
                            channelPipeline.addLast(new ClientHandler());
                            channelPipeline.addLast(new StringEncoder(Charset.forName("UTF-8")));
                            channelPipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));
                            channelPipeline.addLast(new FixedLengthFrameDecoder(1000));
                        }
                    });
            ChannelFuture future = bootstrap.connect(HOST , PORT).sync();
            System.out.println("客户端启动成功");
            future.channel().closeFuture().sync();
            System.out.println("客户端关闭");
        }finally {
            group.shutdownGracefully();
        }
    }
}
