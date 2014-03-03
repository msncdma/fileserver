package com.anlong.fileserver.multihttppost;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

/**
 * @Title: FileWriteThread.java
 * @Package com.anlong.fileserver.multihttppost
 * @company ShenZhen anlong Technology CO.,LTD.
 * @author lixl
 * @date 2014年2月28日 下午6:35:47
 * @version V1.0
 * @Description: 利用线程在文件的指定位置写入指定数据
 */
public class FileWriteThread extends Thread {
	private static Logger logger = Logger.getLogger(FileWriteThread.class);
	private int maxSize;
	private int skip;
	private byte[] content;
	private RandomAccessFile raf;
	private PrintWriter out;
	private int type;
	private String md5;

	public FileWriteThread(int maxSize, int skip, byte[] content, RandomAccessFile raf, PrintWriter out, int type, String md5) {
		// super();
		this.maxSize = maxSize;
		this.skip = skip;
		this.content = content;
		this.raf = raf;
		this.out = out;
		this.type = type;
		this.md5 = md5;
	}

	@Override
	public void run() {
		try {
			
			raf.seek(skip);
			raf.write(content);
			logger.debug("===========^== begin write RandomAccessFile ==^===========" + raf.length());

			if (raf.length() == maxSize) {
				
				//raf.close();
				MultiUploadUtils.closeRandomAccessFileIo(raf);
				logger.debug("===========M== rafMap.containsKey ==M==========="+MultiUploadUtils.getRafMap().containsKey(md5));
				logger.debug("===========M== rafMap.close ==M==========="+MultiUploadUtils.getRafMap().get(md5));
				// 回调
				MultiUploadUtils.uploadDoLastAction(type, md5, maxSize, out);
			}/*
			 * else{ out.flush(); out.close(); }
			 */
		} catch (IOException e) {
			e.printStackTrace();
			MultiUploadUtils.closeRandomAccessFileIo(raf);
		}
	}

}
