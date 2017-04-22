package com.rfview.maze.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rfview.events.Event;
import com.rfview.events.MatrixDataEvent;
import com.rfview.listeners.EventListener;
import com.rfview.utils.Util;

public class SimpleMazeClient {

    private static final String NEW_LINE = "\n";
    private static final String NEW_DELIMITOR = "\r\n";
    private static final Charset charset = Charset.forName("UTF-8");
    private final List<EventListener> listeners = new ArrayList<EventListener>();
    private final Logger logger = Logger.getLogger(SimpleMazeClient.class.getName());
    private SocketChannel socket;
    private final AtomicBoolean disconnect = new AtomicBoolean(false);
    
    public static String decode(ByteBuffer buffer) {
        CharBuffer charBuffer = charset.decode(buffer);
        return charBuffer.toString();
    }

    private final static ByteBuffer receiveBuffer = ByteBuffer.allocate(64 * 1024);

    public ByteBuffer encode(String str) {
        return charset.encode(str);
    }
    
    public void addListener(EventListener listener) {
        this.listeners.add(listener);
    }
    
    // Create non-blocking SocketChannel
    public SocketChannel createSocketChannel(String hostName, int port)
            throws IOException {
        socket = SocketChannel.open();
        socket.configureBlocking(false);
        socket.connect(new InetSocketAddress(hostName, port));
        return socket;
    }

    public void disconnect() {
        try {
            disconnect.set(true);
            socket.close();
        } catch (IOException e) {
        }
    }
    
    public void connect(String address, int port) throws IOException {

        // Create a selector and register two socket channels
        Selector selector = null;
        try {
            // Create the selector
            selector = Selector.open();

            // Create two non-blocking sockets. This method is implemented in
            // Creating a Non-Blocking Socket.
            logger.fine("Connect to server " +  address + " : " + port);
            SocketChannel sChannel1 = createSocketChannel(address, port);

            // Register the channel with selector, listening for all events
            sChannel1.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            
            int loop = 0;
            while ((!sChannel1.finishConnect()) && (loop < 30)) {
                logger.fine("waiting connection to finish...trial " + loop);
                Util.sleep(200);
                loop++;
            }
            if (loop >= 30) {
                return;
            }
        
            Util.sleep(300);
        } catch (IOException e) {
        }

        // Wait for events
        while (!disconnect.get()) {
            try {
                // Wait for an event
                selector.select();
            } catch (IOException e) {
                // Handle error with selector
                break;
            }

            // Get list of selection keys with pending events
            Iterator<?> it = selector.selectedKeys().iterator();

            // Process each key at a time
            while (it.hasNext()) {
                // Get the selection key
                SelectionKey selKey = (SelectionKey) it.next();
                // Remove it from the list to indicate that it is being
                // processed
                it.remove();
                try {
                    this.processSelectionKey(selKey);
                } catch (IOException e) {
                    // Handle error with channel and unregister
                    selKey.cancel();
                }
            }
        }
    }

    public void processSelectionKey(SelectionKey selKey)
            throws IOException {
        // Since the ready operations are cumulative,
        // need to check readiness for each operation
        if (selKey.isValid() && selKey.isConnectable()) {
            // Get channel with connection request
            SocketChannel sChannel = (SocketChannel) selKey.channel();
            boolean success = sChannel.finishConnect();
            if (!success) {
                // An error occurred; handle it
                // Unregister the channel with this selector
                selKey.cancel();
            }
        }
        if (selKey.isValid() && selKey.isReadable()) {
            // Get channel with bytes to read
            SocketChannel sChannel = (SocketChannel) selKey.channel();
            if (sChannel.isConnected()) {
                receive(selKey);
            }
            // See Reading from a SocketChannel
        }
        if (selKey.isValid() && selKey.isWritable()) {
            // Get channel that's ready for more bytes
            // See Writing to a SocketChannel
        }
    }

    // Method that receive data from the server
    public void receive(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int loop = 0;
        String receiveData = "";
        logger.fine("Received response from maze server");
        while ((socketChannel.read(receiveBuffer) > 0) && (loop < 100)) {
            socketChannel.read(receiveBuffer);
            receiveBuffer.flip();
            receiveData += decode(receiveBuffer);
            if (receiveData.indexOf(NEW_DELIMITOR) == -1) {
                try {
                    Thread.sleep(3);
                    loop++;
                } catch (InterruptedException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
        
        notifyListener(receiveData);
        String outputData = receiveData.substring(0, receiveData.indexOf(NEW_LINE) + 1);
        ByteBuffer temp = encode(outputData);
        receiveBuffer.position(temp.limit());
        receiveBuffer.compact();
    }

    private void notifyListener(String data) {    
        Event e = new MatrixDataEvent("matrix_client", data);
        for (EventListener l : listeners) {
            l.handleEvent(e);
        }
    }
}
