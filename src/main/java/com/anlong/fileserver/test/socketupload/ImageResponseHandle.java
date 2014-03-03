package com.anlong.fileserver.test.socketupload;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.anlong.fileserver.common.StaticValue;



public class ImageResponseHandle {
	private static Logger logger = Logger.getLogger(ImageResponseHandle.class);
	// 定时器
	private static Timer timer = null;
	// 是否已经停止定时器
	private static boolean timerCancel = false;
	// 开启定时器的时间
	private static long startTime = 0;
	// 停止定时器的时间
	private static long endTime = 0;
	// 是否超时
	private static boolean timeOut = false;
	InputStream inputStream = null;
	// 是否有可读流
	int isSize = 0;
	
	public void ImageDecode(Object request){
		
		try {
			if (request == null)
				return;
			

			
			logger.info("上传图片等待响应中...");
			
			inputStream = InitFileSocketServer.getInputStream();
			if (inputStream == null)
				return;
			
			
			// 消息字节大小,4个字节
			int msgSize = 0;
			
			// 定时等待响应
			getInstanceTimer(inputStream);
			while (!timerCancel) {
				//IMLog.dingjh("等待上传图片响应..");
				// 等待0.5秒
				Thread.sleep(500);
			}
			
			// TODO 超时处理
			if (timeOut){
				logger.info("等待上传图片响应超时!");
				
				// 关闭Socket连接
				closeFileSocket();
				return;
			}
			
		
			
			
			// 关闭Socket连接
			closeFileSocket();
		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(InitFileSocketServer.getInstance() != null){
				try {
					// TODO 关闭Socket
					InitFileSocketServer.closeSocketConnection();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	
	
	private void closeFileSocket(){
		try {
			if(InitFileSocketServer.getInstance() != null){
				try {
					// TODO 关闭Socket
					InitFileSocketServer.closeSocketConnection();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	 
	
	
	private static void getInstanceTimer(InputStream inputStream){
        if ( timer == null ){
            timer = new Timer();
            // 开启定时器的时间
            startTime = System.currentTimeMillis();
            // 在1秒后执行此任务,每次间隔2秒    
            timer.schedule(new RunTask(inputStream), 1000, 2000);
            //timer.cancel();
        }
    }
	
	
	static class RunTask extends TimerTask{
		private InputStream inputStream1 = null;
		
		public RunTask(InputStream inputStream){
			inputStream1 = inputStream;
			
			// 终止定时器的时间
			endTime = System.currentTimeMillis();
			// 检测30秒超时
			if((endTime - startTime) > StaticValue.SERVER_CONNECTION_TIMEOUT){
				timeOut = true;
				timerCancel = true;
				timer.cancel();
			}
		}
		
		@Override
		public void run(){
			try {
				if ( inputStream1.available() != 0 ){
					timerCancel = true;
					timer.cancel();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
