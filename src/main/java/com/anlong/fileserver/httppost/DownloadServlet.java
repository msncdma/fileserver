package com.anlong.fileserver.httppost;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.anlong.fileserver.mongodb.MongoFileIndex;

/**
 * @Title: DownloadServlet.java 
 * @Package com.anlong.fileserver.fileserver
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2013-9-10 下午5:00:14 
 * @version V1.0   
 * @Description: 消息中心文件服务器 文件下载
 */
public class DownloadServlet extends HttpServlet {

	/**TODO*/
	private static final long serialVersionUID = 2926112149598819950L;
	private static Logger logger = Logger.getLogger(DownloadServlet.class);

	public DownloadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		
		
		//0.检查文件是否存在 1.得到下载文件完整HTTP请求路径   ServletResponse.getOutputStream()和PrintWriter不能同时调用
		String md5 = req.getParameter("md5");

		PrintWriter out = resp.getWriter();   //0.索引不存在  1.文件存在  2.索引中存在，但文件系统中不存在
		//mongo索引是否存在 
		if(MongoFileIndex.isExistFile(md5)){
			//校验文件系统是否存在   
			String iopath = DownloadUtils.getFileDownloadIOPath(md5);
			String relativePath = MongoFileIndex.getFilePath(md5);
			if(new File(iopath).exists()){
				//IP+相对路径   全部是/ 
				out.print(relativePath.replaceAll("\\\\", "/"));
			}else{
				MongoFileIndex.deleteFileIndex(md5);
				logger.info("index exists but file not exists:"+md5+"|"+iopath);
				out.print("2");
			}
			
		}else{
			out.print("0");
		}
		
		out.flush();
		out.close();
	
		
		
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
	}

	
}
