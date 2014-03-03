package com.anlong.fileserver.multihttppost;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.anlong.fileserver.httppost.DownloadUtils;
import com.anlong.fileserver.httppost.UploadUtils;
import com.anlong.fileserver.mongodb.MongoFileIndex;

/**
 * @Title: MultiUploadUtils.java
 * @Package com.anlong.fileserver.common
 * @company ShenZhen anlong Technology CO.,LTD.
 * @author lixl
 * @date 2014年2月28日 下午4:52:06
 * @version V1.0
 * @Description: TODO
 */
public class MultiUploadUtils {

	private static Logger logger = Logger.getLogger(MultiUploadUtils.class);
	private static File repository = new File(System.getProperty("java.io.tmpdir"));
	//全局唯一，多线程共享(RandomAccessFile 同一个iopath生成多个对象，windows写入文件是可行的，linux上文件大小也可行，但文件会损坏)
	private static Map<String,RandomAccessFile> rafMap = new HashMap<String,RandomAccessFile>();
	
	public static Map<String,RandomAccessFile> getRafMap(){
		return rafMap;
	}

	/**
	 * 每一次请求，为一个被切割的数据块 分多次请求上传，在最后一个请求里面校验文件的完整性
	 * 具体的执行方法
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public static void doMultiPostUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {

		request.setCharacterEncoding("UTF-8");
		((ServletRequest) response).setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		DiskFileItemFactory factory = new DiskFileItemFactory();// Create a
																// factory for
																// disk-based
																// file items
		factory.setSizeThreshold((int) FileUtils.ONE_MB << 2); // 内存最大2M
		factory.setRepository(repository); // Configure a repository (to ensure
											// a secure temp location is used)
		ServletFileUpload upload = new ServletFileUpload(factory);// Create a
																	// new file
																	// upload
																	// handler
		upload.setSizeMax(FileUtils.ONE_GB); // 最大上传1G文件
		// 请求参数列表
		String typeStr = null;
		String md5Request = null;
		String md5block = null;
		String sizeStr = null;
		String sizeStrBlock = null;
		String startIndexStr = null;

		List<FileItem> items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) { // 解析出错
			out.print("0");
			out.flush();
			out.close();
			logger.error(e);
			return;
		}

		// 处理参数
		for (FileItem item : items) {
			if (item.isFormField()) {
				if ("type".equals(item.getFieldName())) {
					typeStr = item.getString("UTF-8");
				}
				if ("md5".equals(item.getFieldName())) {
					md5Request = item.getString("UTF-8");
				}
				if ("md5block".equals(item.getFieldName())) {
					md5block = item.getString("UTF-8");
				}
				if ("size".equals(item.getFieldName())) {
					sizeStr = item.getString("UTF-8");
				}
				if ("sizeblock".equals(item.getFieldName())) {
					sizeStrBlock = item.getString("UTF-8");
				}
				if ("startindex".equals(item.getFieldName())) {
					startIndexStr = item.getString("UTF-8");
				}

			}
		}

		if (typeStr == null || md5Request == null || md5block == null || sizeStr == null || sizeStrBlock == null || startIndexStr == null) {
			out.print("1");
			out.flush();
			out.close();
			return;
		}
		System.out.println("typeStr = " + typeStr + ",md5Request = " + md5Request + ",md5Request0 = " + md5block + ",sizeStr = " + sizeStr + ",sizeStr0 = " + sizeStrBlock
				+ ",startIndexStr = " + startIndexStr);

		int sizeBlockInt = Integer.parseInt(sizeStrBlock);
		int sizeStrInt = Integer.parseInt(sizeStr);
		int startIndexStrInt = Integer.parseInt(startIndexStr);
		int typeStrInt = Integer.parseInt(typeStr);

		/** 检验当前 被切割的 文件块大小 */
		if (sizeBlockInt > UploadUtils.getFileByteSizeLimit()) {
			if (logger.isInfoEnabled()) {
				logger.error(request.getQueryString() + "\t文件大小超过最大" + UploadUtils.fileSizeLimit + "M限制");
			}
			out.print("4");
			out.flush();
			out.close();
			return;
		}

		if (MongoFileIndex.isExistFile(md5Request)) {
			String downloadIoPath = DownloadUtils.getFileDownloadIOPath(md5Request);

			String relativePath = MongoFileIndex.getFilePath(md5Request);
			/** 已经成功上传过的直接返回 */
			if (new File(downloadIoPath).exists()) {
				// 校验MD5直接返回全路径
				byte[] data = IOUtils.toByteArray(new FileInputStream(new File(downloadIoPath)));
				String md5 = DigestUtils.md5Hex(data);
				/** 对比下传入与检验的MD5是否一致,返回，方法结束 */
				if (md5Request.equals(md5)) {
					// IP+相对path 全部是/
					out.print(relativePath.replaceAll("\\\\", "/"));
					out.flush();
					out.close();
					return;
				}
			}
			MongoFileIndex.deleteFileIndex(md5Request);
		}

		String iopath = UploadUtils.getFileUploadIOPath(typeStrInt, md5Request, sizeStrInt);
		RandomAccessFile raf = getRandomAccessFile(md5Request,iopath);
		/** 全新上传块，插入 mongo索引 */
		for (FileItem item : items) {
			if (!item.isFormField()) {
				byte[] data = IOUtils.toByteArray(item.getInputStream());
				String md5 = DigestUtils.md5Hex(data);
				/** 对比下传入与检验的MD5是否一致 */
				if (!md5block.equals(md5)) {// 解析出错
					out.print("3");
					out.flush();
					out.close();
					logger.error("Request传入MD5	值校验不一致！请检查MD5值并重新提交POST上传请求");
					// 关闭流
					closeRandomAccessFileIo(raf);
					return;
				}
				/** 执行上传文件块 */
				writeRandomAccessFile(data, typeStrInt, md5Request, sizeStrInt, startIndexStrInt, raf,null);
				// 每个子文件块上传 暂时不返回成功状态
				IOUtils.closeQuietly(item.getInputStream());
				break;
			}
		}
		
		if(isEndBlockInMultiUoloadFile(sizeStrInt, sizeBlockInt, startIndexStrInt)){
			String ioopath = UploadUtils.getFileUploadIOPath(typeStrInt, md5Request, sizeStrInt);
			/** 保存文件索引到montodb */
			
			String relativepath = UploadUtils.getFileDownloadPath(ioopath);
			MongoFileIndex.addFileIndex(md5Request, "", relativepath, "");
			//如果超时！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！会抛出异常！！！！！！！！！！！！！！！先返回
			// IP+相对path 全部是/
			out.print(relativepath.replaceAll("\\\\", "/"));
			out.flush();
			out.close();
		}

	}

	/**
	 * 上传完毕 的最后处理业务
	 * @param typeStrInt
	 * @param md5Request
	 * @param sizeStrInt
	 * @param out
	 */
	public static void uploadDoLastAction(int typeStrInt, String md5Request, int sizeStrInt, PrintWriter out) {
		try {
			String ioopath = UploadUtils.getFileUploadIOPath(typeStrInt, md5Request, sizeStrInt);

			byte[] data = IOUtils.toByteArray(new FileInputStream(new File(ioopath)));
			String md5 = DigestUtils.md5Hex(data);
			/** 对比下传入与检验的MD5是否一致,返回，方法结束 */
			if (md5Request.equals(md5)) {
				int type = typeStrInt;
				/** 放入OScache缓存.单个文件大小需要限制， 只缓存群图片，群语音业务类型的 写配置文件 */
				if (type == 11 || type == 41) {
					UploadUtils.putInCache(md5, data);
				}

				/** 10 ,11,20,21 类型同时写入缩略图 图片定长最大200，等比倒缩放，类型 61 为app图标 */
				if (type == 10 || type == 11 || type == 12 || type == 20 || type == 21) {
					String outFile = UploadUtils.getFileUploadIOPath((type * 10), md5, sizeStrInt);
					UploadUtils.compressPic(new File(ioopath), new File(outFile));
				}
				/** 保存文件索引到montodb */
				/*String relativepath = UploadUtils.getFileDownloadPath(ioopath);
				MongoFileIndex.addFileIndex(md5, "", relativepath, "");*/

				// IP+相对path 全部是/
				/*out.print(relativepath.replaceAll("\\\\", "/"));
				out.flush();
				out.close();*/
				///logger.info("iopath:\t" + ioopath+"    relativepath:"+relativepath);
				// return;
			} /*else {
				out.print("5");
				out.flush();
				out.close();
			}*/
			/** 移除对象 */
			rafMap.remove(md5Request);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeRandomAccessFile(byte[] data, int type, String md5, int size, int skip, RandomAccessFile raf, PrintWriter out) {
		String iopath = UploadUtils.getFileUploadIOPath(type, md5, size);
		// 不确定重复写一个文件是否存在 问题？？？？？？？？？？？？？？？？？？？？？？？？
		if (raf == null) {
			logger.error("create RandomAccessFile failed!!!!iopath=" + iopath);
			return;
		}
		new FileWriteThread(size, skip, data, raf,out,type,md5).start();
	}

	public static void httpUpdateFile(byte[] data, int type, String md5, int size) throws IOException {
		/** 放入OScache缓存.单个文件大小需要限制， 只缓存群图片，群语音业务类型的 写配置文件 */
		/*
		 * if(type == 11 || type == 41){ UploadUtils.putInCache(md5, data); }
		 */
		// 按规则生成的 路径
		String iopath = UploadUtils.getFileUploadIOPath(type, md5, size);

		FileUtils.writeByteArrayToFile(new File(iopath), data);
		/** 10 ,11,20,21 类型同时写入缩略图 图片定长最大200，等比倒缩放，类型 61 为app图标 */
		/*
		 * if(type == 10 || type == 11 || type == 12 || type == 20 || type ==
		 * 21){ String outFile = UploadUtils.getFileIOPath((type*10), md5,size);
		 * UploadUtils.compressPic(new File(iopath), new File(outFile)); }
		 */
		/** 保存文件索引到montodb */
		String relativepath = UploadUtils.getFileDownloadPath(iopath);
		MongoFileIndex.addFileIndex(md5, "", relativepath, "");

	}

	/**
	 * 上传是否到达最后一文件块
	 * 
	 * @param size
	 * @param size0
	 * @param startindex
	 * @return
	 */
	public static boolean isEndBlockInMultiUoloadFile(int size, int size0, int startindex) {
		if (startindex + size0 == size) {
			return true;
		}
		return false;
	}

	private static RandomAccessFile getRandomAccessFile(String md5,String iopath){
		if(!rafMap.containsKey(md5)){
			rafMap.put(md5, createRandomAccessFile(iopath));
		}
		return rafMap.get(md5);
		
	}
	
	/**
	 * 创建全新对象
	 * @param path
	 * @return
	 */
	public static RandomAccessFile createRandomAccessFile(String path) {

		try {
			// LINUX???测试
			File iofile = new File(path);
			if (iofile.exists()) {
				iofile.delete();
			}
			String path2 = path.substring(0, path.lastIndexOf(UploadUtils.getFileSeparator()));
			String name = path.substring(path.lastIndexOf(UploadUtils.getFileSeparator()) + 1, path.length());
			File wfile = new File(path2);
			if (!wfile.exists()) {
				wfile.mkdirs();
			}

			RandomAccessFile raf = new RandomAccessFile(path2 + UploadUtils.getFileSeparator() + name, "rw");
			return raf;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void closeRandomAccessFileIo(RandomAccessFile raf) {
		try {
			logger.info("============= begin close RandomAccessFile =============");
			raf.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
