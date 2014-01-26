package com.anlong.fileserver.socket;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.stream.StreamIoHandler;

/**
 * @Title: FileSocketHandle.java
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author liq  
 * @date 2013-4-17 
 * @version V1.0   
 * @Description:
 */
public class FileSocketHandle  extends StreamIoHandler {


	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		super.sessionIdle(session, status);
	}

	@Override
	public void sessionOpened(IoSession session) {
		super.sessionOpened(session);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}

	@Override
	protected void processStreamIo(IoSession session, InputStream in,OutputStream out) {
		System.out.println("---------接收到文件流请求，开启线程---------|||");
		new FileSocketThread(session,in,out).start(); 
	}
	
	@Override
	public void messageReceived(IoSession session, Object buf) {
		super.messageReceived(session, buf);
		System.out.println("收到数据："+buf.toString());
	}

}
