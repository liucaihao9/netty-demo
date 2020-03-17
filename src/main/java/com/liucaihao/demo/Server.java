package com.liucaihao.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;

public class Server {
    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port" , "8007"));

    public static void main(String[] args) throws CertificateException, SSLException, InterruptedException {
        System.out.println("Server stall");
        final SslContext sslContext;
        if (SSL){
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(ssc.certificate() , ssc.privateKey()).build();
        }else {
            sslContext = null;
        }


        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup , workerGroup).channel(NioServerSocketChannel.class).
                    childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //在此处添加多个ChannelHandler
                            ChannelPipeline channelPipeline = ch.pipeline();
                            if (sslContext != null){
                                channelPipeline.addLast(sslContext.newHandler(ch.alloc()));
                            }
                            channelPipeline.addLast(new MessageDecoder());
                            channelPipeline.addLast(new MessageEncoder());
                            channelPipeline.addLast(new ServerHandler());
                            channelPipeline.addLast(new StringEncoder(Charset.forName("UTF-8")));
                            channelPipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(PORT).sync();
            System.out.println("服务端启动成功");
            future.channel().closeFuture().sync();
            System.out.println("服务端关闭");
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
