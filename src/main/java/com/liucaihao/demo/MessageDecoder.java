package com.liucaihao.demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    public static final int HEAD_LENGTH = 45;

    public static final byte PACKAGE_TAG = 0x01;

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        if (in.readableBytes() < HEAD_LENGTH){
            int length = in.readableBytes();
            byte[] bytes = new byte[length];
            in.readBytes(bytes);
            System.out.println(new String(bytes , "UTF-8"));
            throw new CorruptedFrameException("包长度问题");
        }
        byte tag = in.readByte();
        if (tag != PACKAGE_TAG){
            throw new CorruptedFrameException("标志错误");
        }
        byte encode = in.readByte();
        byte encrypt = in.readByte();
        byte extend1 = in.readByte();
        byte extend2 = in.readByte();
        byte[] sessionBytes = new byte[32];
        in.readBytes(sessionBytes);
        String sessionId = new String(sessionBytes , "UTF-8");
        int length = in.readInt();
        int cammand = in.readInt();
        Header header = new Header(tag,encode, encrypt, extend1, extend2, sessionId, length, cammand);
        byte[] data = new byte[length];
        in.readBytes(data);
        Message message = new Message(header,new String(data,"UTF-8"));
        out.add(message);
    }
}
