package com.rfview.comm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SendData {

    private static Charset charset = Charset.forName("UTF-8");
    private static CharsetDecoder decoder = charset.newDecoder();
    private static SocketChannel socketChannel = null;
    private ByteBuffer sendBuffer = ByteBuffer.allocate(64 * 1024);
    private static Selector selector;
    private boolean isSend = false;
    private boolean done = false;
    static int compteBuf = 0;

    // Constructor
    public SendData(Selector select, SocketChannel sock) throws IOException {
        socketChannel = sock;
        selector = select;
    }

    // create a non-Blocking socketChannel
    public static SocketChannel createSocketChannel(String hostName, int port)
            throws IOException {
        SocketChannel client = SocketChannel.open();
        client.configureBlocking(false);
        client.connect(new InetSocketAddress(hostName, port));
        return client;
    }

    public static String bb_to_str(ByteBuffer buffer) {
        String data = "";
        try {
            int old_position = buffer.position();
            data = decoder.decode(buffer).toString();
            buffer.position(old_position);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return data;
    }

    // Method that receive a command line and set the Buffer 'sendBuffer'
    public void receiveFromUser(String command) {
        try {
            InputStream is = new ByteArrayInputStream(command.getBytes("UTF-8"));
            BufferedReader localReader = new BufferedReader(
                    new InputStreamReader(is));
            String msg = null;

            while ((msg = localReader.readLine()) != null) {
                isSend = true;
                synchronized (sendBuffer) {
                    sendBuffer.put(encode(msg + "\r\n"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read and write on the socketChannel
    public void talk() throws IOException {
        try {
            socketChannel.register(selector, SelectionKey.OP_READ
                    | SelectionKey.OP_WRITE);
            while (selector.select() > 0) {
                @SuppressWarnings("rawtypes")
                Set readyKeys = selector.selectedKeys();
                @SuppressWarnings("rawtypes")
                Iterator it = readyKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = null;
                    try {
                        key = (SelectionKey) it.next();
                        it.remove();
                        if (key.isWritable()) {
                            if (done == true) {
                                break;
                            }
                            send(key);
                            done = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (done == true) {
                    break;
                }
            }
        } catch (NullPointerException e) {
            done = true;
        } catch (ClosedChannelException e1) {
            done = true;
        } catch (CancelledKeyException e2) {
            done = true;
        } catch (ClosedSelectorException e2) {
            done = true;
        }
    }

    // Method that send data
    public void send(SelectionKey key) throws IOException {
        if (!isSend) {
            return;
        }
        SocketChannel socketChannel = (SocketChannel) key.channel();
        // socketChannel = (SocketChannel) key.channel();
        synchronized (sendBuffer) {
            sendBuffer.flip();
            socketChannel.write(sendBuffer);
            sendBuffer.compact();
        }
        isSend = false;
    }

    public ByteBuffer encode(String str) {
        return charset.encode(str);
    }

    public String decode(ByteBuffer buffer) {
        CharBuffer charBuffer = charset.decode(buffer);
        return charBuffer.toString();
    }
}