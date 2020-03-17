package com.liucaihao.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Message {

    private Header header;

    private String data;

    public Message(Header header, String data) {
        this.header = header;
        this.data = data;
    }

    public Message(Header header) {
        this.header = header;
    }

    public byte[] toByte(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(MessageDecoder.PACKAGE_TAG);
        out.write(header.getEncode());
        out.write(header.getEncrypt());
        out.write(header.getExtend1());
        out.write(header.getExtend2());
        byte[] bb = new byte[32];
        byte[] bb2 = header.getSessionId().getBytes();
        for (int i = 0; i < bb2.length; i++) {
            bb[i] = bb2[i];
        }
        try {
            out.write(bb);
            byte[] bbb = data.getBytes("UTF-8");
            out.write(intToByte(data.length()));
            out.write(intToByte(header.getCammand()));
            out.write(bbb);
            out.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * int转字节存储
     * @param newInt
     * @return
     */
    public static byte[] intToByte(int newInt){
        byte[] intByte = new byte[4];
        intByte[0] = (byte) ((newInt >> 24) & 0xFF);
        intByte[1] = (byte) ((newInt >> 16) & 0xFF);
        intByte[2] = (byte) ((newInt >> 8) & 0xFF);
        intByte[3] = (byte) (newInt & 0xFF);
        return intByte;
    }

    /**
     * 字节转int
     * @param src
     * @param offset
     * @return
     */
    public static int bytesToInt(byte[] src , int offset){
        int value;
        value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8) | ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /*public static void main(String[] args) {
        ByteBuf heapBuffer = Unpooled.buffer(8);
        System.out.println(heapBuffer);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(intToByte(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = out.toByteArray();
        heapBuffer.writeBytes(data);
        System.out.println(heapBuffer);
        int a = heapBuffer.readInt();
        System.out.println(a);
    }*/
}
