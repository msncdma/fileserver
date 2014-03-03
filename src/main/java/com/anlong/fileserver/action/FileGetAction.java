package com.anlong.fileserver.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.anlong.fileserver.common.Utils;
import com.anlong.fileserver.httppost.DownloadUtils;
import com.anlong.fileserver.mongodb.MongoFileIndex;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class FileGetAction extends ActionSupport {
	
	/*日志打印对象*/
	private static Logger logger = Logger.getLogger(FileGetAction.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 3171057728417487424L;
	
	/*文件路径*/
	private String path;
	/*文件真实的路径*/
	private String realPath;
	/*文件名称*/
	private String fileName;
	/*文件大小*/
	private Long fileSize;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取文件流对象
	 * @return
	 * @throws ioException 
	 */
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(this.realPath);
	}
	
	/**
	 * 从服务器获取文件
	 * @return
	 */
	public String fileGet(){
		
		//获取req对象
		HttpServletRequest req = ServletActionContext.getRequest();
		
		try{
			if(Utils.isNull(path)){
				req.setAttribute("errMsg", "文件path为空，请检查参数！");
				logger.error("文件path为空，请检查参数");
				return Action.ERROR;
			}
			//根据相对路径获取本地IO真实路径
			String md5 = path.substring(path.lastIndexOf("/")+1);
			//
			if(!MongoFileIndex.isExistFile(md5)){
				req.setAttribute("errMsg", "文件索引值不存在，请重新上传！");
				logger.error("文件索引值不存在，请重新上传！md5="+md5);
				return Action.ERROR;
			}
			String temp = DownloadUtils.getFileDownloadIOPath(md5);
			//如果为缩略图请求，则作替换处理
			if(path.contains("thumb")){
				temp = temp.replace("origin", "thumb");
			}
			this.realPath = temp;
			
			//根据类型得到文件真实路径
			File file = new File(this.realPath);
			//文件是否存在
			if(file.exists() && file.isFile()){
				this.fileSize = file.length();
			}else{
				req.setAttribute("errMsg", "未找到相应文件");
				logger.error("找不到文件("+this.realPath+") md5="+md5);
				return Action.ERROR;
			}
			return Action.SUCCESS;
		}catch (Exception e) {
			req.setAttribute("errMsg", "获取文件时捕捉到异常");
			//e.printStackTrace();
			logger.error("获取文件时捕捉到异常"+req.getRemoteAddr()+req.getServerPort()+req.getRequestURI()+"(path= "+path+")："+e.getMessage());
			return Action.ERROR;
		}
	}
	
}
