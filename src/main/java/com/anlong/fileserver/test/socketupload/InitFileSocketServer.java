package com.anlong.fileserver.test.socketupload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.anlong.fileserver.common.StaticValue;
import com.anlong.fileserver.common.Utils;



/**
 * @Title: InitFileSocketServer.java 
 * @Package com.anlong.msg.test.socketupload
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2014年1月15日 上午10:15:32 
 * @version V1.0   
 * @Description: 客户端初始化
 */
public class InitFileSocketServer {
	private static Logger logger = Logger.getLogger(InitFileSocketServer.class);
	
	private static OutputStream outputStream = null;
	private static InputStream inputStream = null;
	private static InetSocketAddress inetSocketAddress = null;
	private static Socket socket = null;
	private static String ip = "";
	private static int port = 0;
	
	/**
	 * Private constructor
	 */
	private InitFileSocketServer(){}
	
	/**
	 * get socket instance
	 */
	public static void init(String ipAddress,int socketPort) throws IOException {
		try {
			destroy();
			
			if(Utils.isNotNull(ipAddress) && Utils.isNotNull(socketPort)){
				ip = ipAddress;
				port = socketPort;
				openSocketConnection();
			}
			
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * Get socket instance
	 */
	public static Socket getInstance(){
		return socket;
	}
	
	/**
	 * destroy socket instance
	 */
	private static void destroy() {
		if (socket != null )
			socket = null;
	}
	
	/**
	 * close socket connection
	 */
	public static void closeSocketConnection(){
		if ( socket != null ){
			try {
				socket.close();
			} catch (IOException e) {
				logger.info("主动关闭Socket连接异常!");
				e.printStackTrace();
			}
			
			destroy();
		}
	}  
	
	/**
	 * create connection instance
	 */
	private static Socket openSocketConnection() throws IOException {
		try {
			logger.info("正在和服务器([" + ip + "]:[" + port + "])建立连接中!");
			//socket = new Socket(ip,port);
			socket = new Socket();   
			inetSocketAddress = new InetSocketAddress(ip, port);     
			socket.connect(inetSocketAddress, StaticValue.SOCKET_TIMEOUT);
			logger.info("和服务器([" + ip + "]:[" + port + "])已建立连接!");
		}catch(IOException e) {
			socket = null;
			// TODO 通知主线程连接超时
			logger.info("连接[" + ip + "]:[" + port + "]超时.");
			throw e;
		}
		return socket;
	}
	
	
	/**
	 * Get the input stream
	 */
	public static InputStream getInputStream() throws IOException {
		try {
			if ( socket != null ) 
				inputStream = socket.getInputStream();
		} catch (IOException e) {
			throw e;
		}
		return inputStream;
	}
	
	/**
	 * Get the output stream
	 */
	public static OutputStream getOutputStream() throws IOException {
		try {
			if ( socket != null )
				outputStream = socket.getOutputStream();
		} catch (IOException e) {
			throw e;
		}
		return outputStream;
	}
}
