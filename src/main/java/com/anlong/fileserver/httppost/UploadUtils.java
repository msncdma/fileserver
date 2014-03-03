package com.anlong.fileserver.httppost;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
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

import com.anlong.fileserver.common.SystemGlobals;
import com.anlong.fileserver.mongodb.MongoFileIndex;
import com.anlong.fileserver.multihttppost.MultiUploadUtils;
import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;


/**
 * @Title: UploadUtils.java 
 * @Package com.anlong.fileserver.common
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2014年1月8日 上午11:09:51 
 * @version V1.0   
 * @Description: 上传方法逻辑类
 */
public class UploadUtils {
	
	private static Logger logger = Logger.getLogger(UploadUtils.class);
    
	private static GeneralCacheAdministrator cache = new GeneralCacheAdministrator(Config.loadProperties("/fileoscache.properties","the file Cache configuration"));
	
	private static File repository = new File(System.getProperty("java.io.tmpdir"));
	

	public static final int fileSizeLimit = 100;//100M 
	public static int getFileByteSizeLimit(){
		return fileSizeLimit*1024*1024;
	}
	
	
	/**
	 * 得到操作系统 基础路径
	 * @return 
	 */
	public static String getBasePATH(int sizeNeed){
		int amount = Integer.valueOf(SystemGlobals.getValue("anlong.im.file.base.path.amount"));
		String pathItem = "";
		String basePath = "";
		if("Linux".equals(System.getProperties().getProperty("os.name"))){
			pathItem =  "anlong.im.file.base.path.linux";
		}else{
			pathItem = "anlong.im.file.base.path.windows";
		}
		//遍历路径列表，优先选择靠前的
		for(int i=1;i<=amount;i++){
			basePath = SystemGlobals.getValue(pathItem + "."+i)+getFileSeparator()+SystemGlobals.getValue("anlong.im.file.catalog.prefix")+i;
			File file = new File(basePath);
			if(file.getFreeSpace() > sizeNeed){
				return basePath;
			}
		}
		return SystemGlobals.getValue(pathItem + "."+1)+getFileSeparator()+SystemGlobals.getValue("anlong.im.file.catalog.prefix")+1;
	}
	
	/**
	 * 分隔符
	 * @return
	 */
	public static String getFileSeparator(){
		return System.getProperties().getProperty("file.separator");
	}
	
	
	/**
	 * 获取日期路径  （格式：2013/2013-09-06）
	 * @return 
	 */
	public static String getDatePath(){
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
		return yearSdf.format(new Date())+getFileSeparator()+dateSdf.format(new Date());
	}
	
	
	
	/**
	 * 按规则生成的磁盘写入绝对路径
	  * 10－个人聊天图片；11－群组聊天图片；20－个人头像；21－群组头像；30－普通文件；40－个人语音文件；41－群组语音文件；50－PC安装包；51－ANDROID安装包；52－IOS安装包
	 *  client>(PC,Android,iOS)
	 *  file/pic>(origin/thumb)/voice > year>date
	 * @param type
	 * @param id
	 * @param sizeNeed
	 * @return 
	 */
	public static String getFileUploadIOPath(int type,String id,int sizeNeed){
		return getBasePATH(sizeNeed)+getFileSeparator()+getFileRelativePath(type, id);
	}
	
	/**
	 * 由绝对路径转换生成文件下载路径
	 * @param ioPath
	 * @return
	 */
	public static String getFileDownloadPath(String ioPath){
		String prefix = SystemGlobals.getValue("anlong.im.file.catalog.prefix");
		int index = ioPath.lastIndexOf(prefix);
		return ioPath.substring(index).replaceAll("\\\\", "/");
	}
	
	/**
	 * 生成相对路径  
	 * 10－个人聊天图片；11－群组聊天图片；20－个人头像；21－群组头像；30－普通文件；40－个人语音文件；41－群组语音文件；50－PC安装包；51－ANDROID安装包；52－IOS安装包
	 * data/*****
	 * @param type
	 * @param id
	 * @return 
	 */
	public static String getFileRelativePath(int type,String id){

		switch (type) {
		case 10:
			return SystemGlobals.getValue("anlong.im.file.name.type10")+getFileSeparator()+"origin"+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 100:
			return SystemGlobals.getValue("anlong.im.file.name.type10")+getFileSeparator()+"thumb"+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 11:
			return SystemGlobals.getValue("anlong.im.file.name.type11")+getFileSeparator()+"origin"+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 110:
			return SystemGlobals.getValue("anlong.im.file.name.type11")+getFileSeparator()+"thumb"+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 12:
			return SystemGlobals.getValue("anlong.im.file.name.type12")+getFileSeparator()+"origin"+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 120:
			return SystemGlobals.getValue("anlong.im.file.name.type12")+getFileSeparator()+"thumb"+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 20:
			return SystemGlobals.getValue("anlong.im.file.name.type20")+getFileSeparator()+"origin"+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 200:
			return SystemGlobals.getValue("anlong.im.file.name.type20")+getFileSeparator()+"thumb"+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 21:
			return SystemGlobals.getValue("anlong.im.file.name.type21")+getFileSeparator()+"origin"+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 210:
			return SystemGlobals.getValue("anlong.im.file.name.type21")+getFileSeparator()+"thumb"+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 30:
			return SystemGlobals.getValue("anlong.im.file.name.type30")+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 40:
			return SystemGlobals.getValue("anlong.im.file.name.type40")+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 41:
			return SystemGlobals.getValue("anlong.im.file.name.type41")+getFileSeparator()+getDatePath()+getFileSeparator()+id;
		case 50:
			return "client"+getFileSeparator()+SystemGlobals.getValue("anlong.im.file.name.type50")+getDatePath()+getFileSeparator()+id;
		case 51:
			return "client"+getFileSeparator()+SystemGlobals.getValue("anlong.im.file.name.type51")+getDatePath()+getFileSeparator()+id;
		case 52:
			return "client"+getFileSeparator()+SystemGlobals.getValue("anlong.im.file.name.type52")+getDatePath()+getFileSeparator()+id;
		case 60:
			return SystemGlobals.getValue("anlong.im.file.name.type60")+getDatePath()+getFileSeparator()+id;
		case 61:
			return SystemGlobals.getValue("anlong.im.file.name.type61")+getDatePath()+getFileSeparator()+id;
		default:
			return "";
		}
	
	}
	/**
	 * 放入OScache缓存（仅内存）
	 * @param key
	 * @param o
	 * @return
	 * @throws IOException 
	 */
	public static boolean putInCache(String key, Object o) throws IOException {
		try {
			cache.getFromCache(key, 86400);
			return true;
		} catch (NeedsRefreshException e) {
			try {
				
				cache.putInCache(key, o);
				return true;
			} catch (Exception ex) {
				// cached content is not rebuilt
				logger.error("key:" + key);
				logger.error(UploadUtils.class, ex);
				cache.cancelUpdate(key);
				return false;
			}
		}
	}



	
	/**
	 * 通过OScache缓存取文件
	 * @param key
	 * @return 
	 */
	public static Object getCache(String key) {
		Object o = null;
		try {
			o = cache.getFromCache(key, 86400);
		} catch (NeedsRefreshException e) {
			try {
				//如果内存中不存在  从文件系统中载入到内存
				String ioPath = DownloadUtils.getFileDownloadIOPath(key);
				File file = new File(ioPath);
				o = FileUtils.readFileToByteArray(file);
				cache.putInCache(key, o);
			} catch (Exception ex) {
				logger.error("key:" + key);
				logger.error(UploadUtils.class, ex);
				cache.cancelUpdate(key);
			}
		}
		return o;
	}



	/**
	 * 图片缩略处理   长最大200  等比缩放 
	 * @param file
	 * @param outFile 
	 */
	public static void compressPic(File file,File outFile) { 
		 try { 
			 if(file.exists()){
				 Image img = ImageIO.read(file); 
				 // 判断图片格式是否正确 
				 if (img.getWidth(null) == -1) {
					 return; 
				} else {
					int newWidth = 100;
					int newHeight = 100;
					double rate1 = 1;       // 为等比缩放计算输出的图片宽度及高度
					double rate2 =1;
					double rate3 = 1;
					
					if( img.getWidth(null) > 200){
						rate1 = ((double) img.getWidth(null)) / (double) 200 + 0.1;
					}
					if( img.getHeight(null) > 200){
						rate2 = ((double) img.getHeight(null)) / (double) 200 + 0.1;
					}
					rate3 = rate1 > rate2 ? rate1:rate2;
					
					// 根据缩放比率大的进行缩放控制
					newWidth = (int) (((double) img.getWidth(null)) / rate3);
					newHeight = (int) (((double) img.getHeight(null)) / rate3);
					
					BufferedImage tag = new BufferedImage((int) newWidth, (int) newHeight, BufferedImage.TYPE_INT_RGB);

					/*
					 * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好
					 * 但速度慢
					 */
					tag.getGraphics().drawImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
					
					if(!outFile.getParentFile().exists()){
						outFile.getParentFile().mkdirs();
					}
					if(!outFile.exists()){
						outFile.createNewFile();
					}
					FileOutputStream out = new FileOutputStream(outFile);
					// JPEGImageEncoder可适用于其他图片类型的转换
					JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
					encoder.encode(tag);
					out.close();
				}
				 
			 }
		 } catch (IOException ex) { 
			 if (logger.isInfoEnabled()) {
				 
				 logger.error("ImgUploadService.compressPic error",ex);
			 }
		 } 
	} 


	/**
	 * 按比例缩放图片，指定最大长或宽
	 * 
	 * @param bufferedImage
	 * @param max
	 *            图片的长与宽的最大值
	 * @return 缩放的图片
	 */
	public static BufferedImage resize(BufferedImage bufferedImage,
			int newWidth, int newHeight) {
		int oldWidth = bufferedImage.getWidth();
		int oldHeight = bufferedImage.getHeight();
		if (newWidth == -1 || newHeight == -1) {
			if (newWidth == -1) {
				if (newHeight == -1) {
					return null;
				}
				newWidth = newHeight * oldWidth / oldHeight;
			} else {
				newHeight = newWidth * oldHeight / oldWidth;
			}
		}
		BufferedImage result = new BufferedImage(newWidth, newHeight,
				BufferedImage.TYPE_INT_BGR);
		double widthSkip = new Double(oldWidth - newWidth)
				/ new Double(newWidth);
		double heightSkip = new Double(oldHeight - newHeight)
				/ new Double(newHeight);
		double widthCounter = 0;
		double heightCounter = 0;
		int newY = 0;
		boolean isNewImageWidthSmaller = widthSkip > 0;
		boolean isNewImageHeightSmaller = heightSkip > 0;
		for (int y = 0; y < oldHeight && newY < newHeight; y++) {
			if (isNewImageHeightSmaller && heightCounter > 1) { // new image
				heightCounter -= 1;
			} else if (heightCounter < -1) {
				heightCounter += 1;
				if (y > 1)
					y = y - 2;
				else
					y = y - 1;
			} else {
				heightCounter += heightSkip;
				int newX = 0;
				for (int x = 0; x < oldWidth && newX < newWidth; x++) {
					if (isNewImageWidthSmaller && widthCounter > 1) {
						widthCounter -= 1;
					} else if (widthCounter < -1) {
						widthCounter += 1;
						if (x > 1)
							x = x - 2;
						else
							x = x - 1;
					} else {

						int rgb = bufferedImage.getRGB(x, y);
						result.setRGB(newX, newY, rgb);
						newX++;
						widthCounter += widthSkip;
					}
				}
				newY++;
			}
		}
		return result;
	}

	

	/**
	 * 文件上传处理主程序
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public static void doHttpUpload(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		request.setCharacterEncoding("UTF-8");
		((ServletRequest) response).setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		DiskFileItemFactory factory = new DiskFileItemFactory();// Create a factory for disk-based file items
		factory.setSizeThreshold((int) FileUtils.ONE_MB << 2);       // 内存最大2M
		factory.setRepository(repository);                                                 // Configure a repository (to ensure a secure temp location is used)
		ServletFileUpload upload = new ServletFileUpload(factory);// Create a new file upload handler
		upload.setSizeMax(FileUtils.ONE_GB);                                              // 最大上传1G文件
		// 请求参数列表
		String typeStr = null;
		String md5Request = null;
		String sizeStr = null;
		
		List<FileItem> items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {                                                // 解析出错
			out.print("0");
			logger.error(e);
			return;
		}
		
		// 处理参数
		for (FileItem item : items) {
			if (item.isFormField()) {
				if("type".equals( item.getFieldName())){
					typeStr = item.getString("UTF-8");
				}
				if("md5".equals( item.getFieldName())){
					md5Request = item.getString("UTF-8");
				}
				if("size".equals( item.getFieldName())){
					sizeStr = item.getString("UTF-8");
				}
			} 
		}

		if(typeStr == null || md5Request == null || sizeStr == null){
			out.print("1");
			System.out.println("typeStr = "+typeStr +",md5Request = "+md5Request+",sizeStr = "+sizeStr);
			return;
		}
		
		int size0 = Integer.parseInt(sizeStr);
		/** 检验文件大小 */
		if(size0 <= UploadUtils.getFileByteSizeLimit()){
			/** 校验mongo索引 */
			if(MongoFileIndex.isExistFile(md5Request)){
				String ioPath = DownloadUtils.getFileDownloadIOPath(md5Request);
				String relativePath = MongoFileIndex.getFilePath(md5Request);
				/** 校验物理磁盘中是否存在  */
				if(new File(ioPath).exists()){
					//IP+相对path   全部是/ 
					out.print(relativePath.replaceAll("\\\\", "/"));
				}else{
					//索引中存在，但文件系统中不存在    此种情况注意MONGO直接删除索引中有
					MongoFileIndex.deleteFileIndex(md5Request);
					logger.error("index exists but file not exists:"+md5Request+"|"+ioPath);
					out.print("2");
				}
				
			}else{    
				//文件不存在，开始上传文件
				int type = Integer.parseInt(typeStr);
				//?????????????????????????????????优化循环
				for (FileItem item : items) {
					if(!item.isFormField()){
						
						long size = item.getSize();
						//从inputstream里面全部读取出来，缓存到data数组对象，存放到堆里面
						byte[] data = IOUtils.toByteArray(item.getInputStream());
						String md5 = DigestUtils.md5Hex(data);
						/** 对比下传入与检验的MD5是否一致 */
						if(!md5Request.equals(md5)){// 解析出错
							out.print("3");
							logger.error("Request传入MD5	值校验不一致！请检查MD5值并重新提交POST上传请求");
							return;
						}
						/** 执行上传 */
						httpUpdateFile( data, type, md5, size0);
						//WRITE LOG
						String iopath = UploadUtils.getFileUploadIOPath(type, md5,size0);
						String relativepath = UploadUtils.getFileDownloadPath(iopath);
						
						StringBuilder message = new StringBuilder();
						message.append(size).append("\t");
						message.append(md5).append("\t");
						message.append(DigestUtils.sha1Hex(data)).append("\t");
						message.append(iopath).append("\t");
						message.append(item.getName());
						logger.info(message.toString());
						IOUtils.closeQuietly(item.getInputStream());
						//返回相对文件路径 
						out.print(relativepath);
						logger.info("iopath:\t" + iopath);
						break;
					
					}
				}
			}
		
		}else{
			if (logger.isInfoEnabled()) {
				logger.error(request.getQueryString() + "\t文件大小超过最大"+UploadUtils.fileSizeLimit+"M限制");
			}
			out.print("4");
		}
		
		out.flush();
		out.close();
	}
	

	
	/**
	 * 上传
	 * @param data
	 * @param type
	 * @param md5
	 * @param size
	 * @throws IOException
	 */
	public static void httpUpdateFile(byte[] data,int type,String md5,int size) throws IOException{
		/** 放入OScache缓存.单个文件大小需要限制， 只缓存群图片，群语音业务类型的  写配置文件 */
		if(type == 11 || type == 41){
			UploadUtils.putInCache(md5, data);
		}
		//按规则生成的 路径
		String iopath = UploadUtils.getFileUploadIOPath(type, md5,size);
		
		//FileUtils.writeByteArrayToFile(new File(iopath), data);
		filePersistence(iopath, data);
		
		/** 10 ,11,20,21  类型同时写入缩略图   图片定长最大200，等比倒缩放，类型 61 为app图标 */
		if(type == 10 || type == 11 || type == 12 || type == 20 || type == 21){
			String outFile = UploadUtils.getFileUploadIOPath((type*10), md5,size);
			compressPic(new File(iopath), new File(outFile));
		}
		/** 保存文件索引到montodb */
		String relativepath = UploadUtils.getFileDownloadPath(iopath);
		MongoFileIndex.addFileIndex(md5, "", relativepath,"");
		
	}
	
	/**
	 * 底层写入文件的具体实现
	 * 1.apache writeByteArrayToFile  2.RandomAccessFile
	 * @param iopath
	 * @param data
	 * @throws IOException 
	 */
	public static void filePersistence(String iopath,byte[] data) throws IOException{
		switch (Integer.valueOf(SystemGlobals.getValue("anlong.im.fileupload.persistence.implement.type"))) {
		case 1:
			FileUtils.writeByteArrayToFile(new File(iopath), data);
			break;
			
		case 2:
			logger.info("============*== filePersistence Start RandomAccessFile ==*============");
			RandomAccessFile raf = MultiUploadUtils.createRandomAccessFile(iopath);
			raf.seek(0);
			raf.write(data);
			MultiUploadUtils.closeRandomAccessFileIo(raf);
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * 上传
	 * @param dis
	 * @param dos
	 * @param type
	 * @param md5
	 * @param size
	 * @throws IOException 
	 */
	public static void socketUpdateFile(DataInputStream dis, DataOutputStream dos,int type,String md5,int size) throws IOException{
		byte[] data = IOUtils.toByteArray(dis);
		httpUpdateFile(data, type, md5, size);
		//如果需要进度条，请参照ImgUploadService
	}
	
}
