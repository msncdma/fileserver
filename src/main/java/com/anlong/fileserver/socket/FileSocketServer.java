package com.anlong.fileserver.socket;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.anlong.fileserver.common.SystemGlobals;



/**
 * @Title: FileSocketServer.java 
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author liq   
 * @date 2013-05-28 下午03:29:09 
 * @version V1.0   
 * @Description: Socket服务(服务器端) 用于发送文件(基于Apache mina)
 * 保证每个请求为一个独立的线程，处理上传文件请求，并发模式
 */
public class FileSocketServer implements Runnable {
	private static Logger logger = Logger.getLogger(FileSocketServer.class);
	
	public void run() {
		// 建立一个无阻塞服务端socket,用nio
		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		// 创建接收过滤器 也就是你要传送对象的类型
		// DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

		// 设定对象传输工厂
		ObjectSerializationCodecFactory factory = new ObjectSerializationCodecFactory();

		// 设定后服务器可以接收大数据
		factory.setDecoderMaxObjectSize(Integer.MAX_VALUE);
		factory.setEncoderMaxObjectSize(Integer.MAX_VALUE);
		//这个用于打印日志,可以不写
		// chain.addLast("logging", new LoggingFilter());

		// 设定服务端消息处理器
		acceptor.setHandler(new FileSocketHandle());
		InetSocketAddress inetSocketAddress = null;

		try {
			int port = SystemGlobals.getIntValue("anlong.im.file.socket.port",8781);
			inetSocketAddress = new InetSocketAddress(port);
			acceptor.bind(inetSocketAddress);
//			logger.debug("File Socket Server Started;Port=" + PORT);
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
