package com.anlong.fileserver.test.socketupload;

import java.util.Timer;
import java.util.TimerTask;

import com.anlong.fileserver.common.StaticValue;
import com.anlong.fileserver.common.SystemGlobals;
import com.anlong.fileserver.socket.FileSendInfo;


/**
 * @Title: InitImageFileServer.java
 * @Package com.anlong.fileserver.test.socketupload
 * @company ShenZhen anlong Technology CO.,LTD.
 * @author lixl
 * @date 2014年1月17日 下午12:00:53
 * @version V1.0
 * @Description: 图片文件传输服务处理线程
 */
public class InitImageFileServer implements Runnable {
	// 请求对象
	private Object request = null;
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

	public InitImageFileServer(Object object) {
		request = object;
	}

	
	public void run() {
			try {
				if (InitFileSocketServer.getInstance() != null) {
					InitFileSocketServer.closeSocketConnection();
				}
				
				// TODO 开启Socket连接
				try {
					InitFileSocketServer.init(SystemGlobals.getValue("anlong.im.file.socket.address"),
							SystemGlobals.getIntValue("anlong.im.file.socket.port", 8781));
				}catch(Exception e){
					// TODO 通知页面更新提示
					//Utils.notifyMessage(7,HandleStaticValue.BCODE1000);
					return;
				}
				
				// TODO 检测Socket连接实例
				getInstanceTimer();
				while(!timerCancel){
					//IMLog.dingjh("等待开启图片上传Socket连接...");
					// 等待0.5秒
					Thread.sleep(500);
				}
				
				// TODO 超时处理
				if (timeOut){
					// TODO 通知页面更新提示
					//Utils.notifyMessage(3,HandleStaticValue.BCODE1000);
					return;
				}
				
				// TODO 连上服务器则通知页面更新提示
				//Utils.notifyMessage(5,HandleStaticValue.BCODE1000);
				
				// TODO 数据流处理
				if (InitFileSocketServer.getInstance() != null){
					// TODO 图片文件上传   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
					new ImageRequestHandle().ImageEncode(new FileSendInfo(), "");
					// TODO 图片文件上传响应
					new ImageResponseHandle().ImageDecode(request);
				} else {
					//IMLog.dingjh("图片上传Socket处于断开状态,图片上传失败.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
	}

	/**
	 * @Title: notifyMessage
	 * @Description: TODO 启动定时器
	 * @author dingjh
	 * @throws
	 */
	private static void getInstanceTimer() {
		if (timer == null) {
			timer = new Timer();
			// 开启定时器的时间
			startTime = System.currentTimeMillis();
			// 在1秒后执行此任务,每次间隔2秒
			timer.schedule(new RunTask(), 1000, 2000);
			// timer.cancel();
		}
	}

	/**
	 * @Title: notifyMessage
	 * @Description: TODO 定时器
	 * @author dingjh
	 * @throws
	 */
	static class RunTask extends TimerTask {
		public RunTask() {
			// 终止定时器的时间
			endTime = System.currentTimeMillis();
			// 检测20秒超时
			if ((endTime - startTime) > StaticValue.SERVER_CONNECTION_TIMEOUT) {
				timeOut = true;
				timerCancel = true;
				timer.cancel();
			}
		}

		@Override
		public void run() {
			if (InitFileSocketServer.getInstance() != null) {
				timerCancel = true;
				timer.cancel();
			}
		}
	}

}
