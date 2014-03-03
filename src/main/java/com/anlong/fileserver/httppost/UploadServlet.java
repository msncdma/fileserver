package com.anlong.fileserver.httppost;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.anlong.fileserver.mongodb.MongoFileIndex;

/**
 * @Title: UploadServlet.java 
 * @Package com.anlong.fileserver.fileserver
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2013-9-3 下午4:40:46 
 * @version V1.0   
 * @Description: 文件上传
 */
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = -1297629789361353419L;
	private static Logger logger = Logger.getLogger(UploadServlet.class);
	
	/**
	 * Constructor of the object.
	 */
	public UploadServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
	
	
	/**
	 * 先GET请求判断文件是否存在  
	 * 10－个人聊天图片；11－群组聊天图片；20－个人头像；21－群组头像；30－普通文件；40－个人语音文件；41－群组语音文件；50－PC安装包；51－ANDROID安装包；52－IOS安装包
	 * >(PC,Android,iOS) file/pic>(origin/thumb)/voice > year>date
	 * 0.  文件不存在 1.索引中存在，但文件系统中不存在  2.文件大小超过最大限制 3.请求参数异常
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//GET 请求参数
		String typeStr = request.getParameter("type");
		String md5 = request.getParameter("md5");
		String sizeStr = request.getParameter("size");
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		/** 判定传入参数 */
		if (typeStr !=null && sizeStr != null && md5 != null	&& typeStr.matches("\\d+")&& sizeStr.matches("\\d+") && md5.matches("\\w+")) {
			long b = Long.parseLong(sizeStr);
			/**  判定大小限制 */
			if(b <= UploadUtils.getFileByteSizeLimit()){
			//优先mongoDB中判定索引  加快处理速度
			if(MongoFileIndex.isExistFile(md5)){
				/** 校验文件系统是否存在    */
				String iopath = DownloadUtils.getFileDownloadIOPath(md5);
				String relativePath = MongoFileIndex.getFilePath(md5);
				if(new File(iopath).exists()){
					//下载路径=IP+相对路径   全部是/ 
					out.print(relativePath.replaceAll("\\\\", "/"));
				}else{
					//索引中存在，但文件系统中不存在
					MongoFileIndex.deleteFileIndex(md5);
					logger.info("index exists but file not exists:"+md5+"|"+iopath);
					out.print("1");
				}
				if (logger.isInfoEnabled()) {
					logger.info(request.getQueryString() + "\t文件存在");
				}
			}else{
				if (logger.isInfoEnabled()) {
					logger.info(request.getQueryString() + "\t文件不存在");
				}
				out.print("0");
			
			}
			
			}else{
				if (logger.isInfoEnabled()) {
					logger.info(request.getQueryString() + "\t文件大小超过最大"+UploadUtils.fileSizeLimit+"M限制");
				}
				out.print("2");
			}
			
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(request.getQueryString() + "\t请求参数异常 :type="+typeStr +",md5="+md5+",size="+sizeStr);
			}
			out.print("3");
		}
	
		out.flush();
		out.close();
	}

	
	/**
	 * 这里开始上传文件  
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// 判断是否为multipart格式
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			// 处理文件上传
			if (isMultipart) {
				UploadUtils.doHttpUpload(request, response);
			} else {
				logger.error("upload not Multipart");
				response.sendError(403, "not a file upload request");
			}
		} catch (Exception e) {
			logger.error(this, e);
			e.printStackTrace();
		}

	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
	}

}
