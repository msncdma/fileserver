package com.anlong.fileserver.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.mina.core.session.IoSession;

/**
 * @Title: FileSocketThread.java
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author liq  
 * @date 2013-4-17 
 * @version V1.0   
 * @Description:文件处理类
 * 短时间内连续的上传请求，多线程可提高资源利用率及处理效率 
 */
public class FileSocketThread extends Thread{
	// 文件传输对象
	protected FileSendInfo fileSendInfo = null;
	// socket输入流
	protected DataInputStream dis = null;
	// socket输出流
	protected DataOutputStream dos = null;
	// session
	protected IoSession session = null;
	// 业务处理类
	private FileUpdate updateService;
	
	public FileSocketThread(IoSession session,InputStream in, OutputStream out) {
		this.session = session;
		dis = new DataInputStream(in);
		dos = new DataOutputStream(out);
	}

	
	@Override
	public void run() {
		try {
			fileSendInfo = new FileSendInfo();
			
			updateService = new FileUpdateCommonFile();
			//解析参数
			updateService.parseArgs(dis, dos, fileSendInfo);
			//执行上传
			updateService.execute(session, dis, dos, fileSendInfo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
