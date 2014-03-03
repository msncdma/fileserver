package com.anlong.fileserver.multihttppost;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

/**
 * @Title: MultiUploadServlet.java
 * @Package com.anlong.fileserver.multihttppost
 * @company ShenZhen anlong Technology CO.,LTD.
 * @author lixl
 * @date 2014年2月28日 下午4:48:18
 * @version V1.0
 * @Description: 大文件分割，多次上传
 */
public class MultiUploadServlet extends HttpServlet {

	private static final long serialVersionUID = -5687616283845224535L;
	private static Logger logger = Logger.getLogger(MultiUploadServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean isMultiPart = ServletFileUpload.isMultipartContent(req);
		if (isMultiPart) {
			// 调用上传
			try {
				MultiUploadUtils.doMultiPostUpload(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			logger.error("request is not multipart format ,please check your code and data,try again ");
			resp.sendError(403, "request is not multipart format ,please check your code and data,try again ");
		}
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
