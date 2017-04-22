package com.rfview.maze.client;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.rfview.events.MatrixDataEvent;
import com.rfview.listeners.EventListener;
import com.rfview.maze.Datagrid;
import com.rfview.maze.Entry;

public class RFMazeConnector extends IoHandlerAdapter {

	private IoConnector connector;
	private static IoSession session;

	private final String hardware;

    private final List<EventListener> listeners = new LinkedList<EventListener>();
	private final String host;
	private final int port;
	private ConnectFuture connFuture = null;
    boolean end_loop = false;
	private final Logger serverLogger = Logger.getLogger("serverTrace");
	private final Logger logger = Logger.getLogger(RFMazeConnector.class.getName());

	private int offset[] = null;
	private Entry matrix[][] = null;
	private AtomicBoolean stopConnection = new AtomicBoolean(false);

	public RFMazeConnector(String hardware, String host, int port) {
		this.hardware = hardware;
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getMatrixName() {
		return matrixName;
	}

	public void setMatrixName(String matrixName) {
		logger.debug("setMatrixName() " + matrixName);
		this.matrixName = matrixName;
	}

	public char getMatrixType() {
		return matrixType;
	}

	public void setMatrixType(char matrixType) {
		logger.debug("setMatrixType() " + matrixType);
		this.matrixType = matrixType;
	}

	public int getMaxInputs() {
		return maxInputs;
	}

	public void setMaxInputs(int maxInputs) {
		this.maxInputs = maxInputs;
		if (maxOutputs > 1 && matrix == null) {
			matrix = new Entry[maxInputs][maxOutputs];
			Datagrid.getInstance().setMatrix(hardware, matrix);
		}
		offset = new int[maxInputs];
	}

	public int getMaxOutputs() {
		return maxOutputs;
	}

	public void setMaxOutputs(int maxOutputs) {
		if (maxInputs > 1 && matrix == null) {
			matrix = new Entry[maxInputs][maxOutputs];
		}
		this.maxOutputs = maxOutputs;
		offset = new int[maxOutputs];
	}

	public void setMatrixValue(int x, int y, int v) {
		if (x >= 0 && y >= 0) {
			Entry e = new Entry(x, y, null, v, false);
			matrix[x][y] = e;
		}
	}

	public void setOffsetValue(int x, int v) {
		if (x >= 0) {
			offset[x] = v;
		}
	}

	private String matrixName;
	private char matrixType;
	private int maxInputs;
	private int maxOutputs;

	public IoSession getSession() {
		return connFuture.getSession();
	}

	public void start() {
		doConnection();
	}

	public void doConnection() {

		quietlyClose(connector);
		connector = new NioSocketConnector();
			
		connector.getFilterChain().addLast("simpleStringDecoder", new ProtocolCodecFilter(new TextLineCodecFactory()));
		connector.setHandler(this);

		connFuture = connector.connect(new InetSocketAddress(host, port));
		connFuture.awaitUninterruptibly();
		session = connFuture.getSession();
	}

	public IoConnector getConnector() {
		return connector;
	}

	private void quietlyClose(IoConnector conn) {
		if (conn != null) {
			session.closeNow();
			session.getCloseFuture().awaitUninterruptibly();
			conn.dispose();
			connFuture.cancel();
		}
	}
	
	public void stop() {
		logger.info("RFMazeConnector() stop");
		session.closeNow();
		session.getCloseFuture().awaitUninterruptibly();
		connector.dispose();
		connFuture.cancel();
		stopConnection.set(true);
		logger.info("RFMazeConnector stop() exit");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("exceptionCaught() ", cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String receiveData = message.toString().replaceAll("\\s*RFMAZE>\\s*", "").trim();
		serverLogger.info("INCOMING [" + receiveData + "]");
		if (!receiveData.isEmpty()) {
			for (String token : receiveData.split("\r")) {
				String data = token.replaceAll("[\r\n]", "");
				if (!data.endsWith("dBmEnD")) {
					logger.info("RECV: [ " + data + " ] from " + hardware);
				}
                MatrixDataEvent event = new MatrixDataEvent(this.getClass().getName(), data);
                for (EventListener listener : listeners) {
                    listener.handleEvent(event);
                }
			}
		}
	}

	public void addListener(EventListener listener) {
		listeners.add(listener);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		serverLogger.info("OUTGOING [ " + message + "]");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.warn("sessionClosed()");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("sessionCreated()");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
	}
}
