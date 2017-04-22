package com.rfview.comm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;

import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendRead {

    private static Charset charset = Charset.forName("UTF-8");
    private static SocketChannel socketChannel = null;
    
    private ByteBuffer sendBuffer = ByteBuffer.allocate(64 * 1024);
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(64 * 1024);

    private Logger logger = Logger.getLogger(SendRead.class.getName());
    
    public SendRead(SocketChannel sock) throws IOException {
        socketChannel = sock;
    }

    public static SocketChannel createSocketChannel(String hostName, int port)
            throws IOException {
        SocketChannel client = SocketChannel.open();
        client.configureBlocking(false);
        client.connect(new InetSocketAddress(hostName, port));
        return client;
    }

    public ByteBuffer encode(String str) {
        return charset.encode(str);
    }

    public String decode(ByteBuffer buffer) {
        CharBuffer charBuffer = charset.decode(buffer);
        return charBuffer.toString();
    }

    public void receiveFromUser(String command) {

        if (!socketChannel.isOpen()) {
            logger.log(Level.SEVERE, "Socket connection has lost!!");
            return;
        }

        try {
            InputStream is = new ByteArrayInputStream(command.getBytes("UTF-8"));
            BufferedReader localReader = new BufferedReader(
                    new InputStreamReader(is));
            String msg = localReader.readLine();

            synchronized (sendBuffer) {
                sendBuffer.put(encode(msg + "\r\n"));
            }

            synchronized (sendBuffer) {
                sendBuffer.flip();
                socketChannel.write(sendBuffer);
                sendBuffer.compact();
            }

        } catch (ClosedChannelException e) {
            logger.log(Level.SEVERE, "Socket has been closed!");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Socket connection has been lost!");
        }
    }

    public void send() {
        try {
            synchronized (sendBuffer) {
                sendBuffer.flip();
                socketChannel.write(sendBuffer);
                sendBuffer.compact();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "io exception", e);
        }
    }

    public void receive() {
        try {
            socketChannel.read(receiveBuffer);
            receiveBuffer.flip();
            String receiveData = decode(receiveBuffer);
            logger.fine("Receive : " + receiveData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
