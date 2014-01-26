package com.anlong.fileserver.socket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;


/**
 * @Title: InitSystemService.java 
 * @Package com.anlong.msg.socket
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2013-10-23 上午9:18:10 
 * @version V1.0   
 * @Description: 后台初始化加载类
 */
public class InitSystemService implements ServletContextListener {
	private static Logger logger = Logger.getLogger(InitSystemService.class);

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		//ServletContext servletContext = servletContextEvent.getServletContext();
		//初始化Spring
		//SpringUtil.setServletContext(servletContext);
		// 初始化socket服务
		this.initSocketServer();
	}

	private void initSocketServer() {
		new Thread(new FileSocketServer()).start();
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		
	}
	

}
