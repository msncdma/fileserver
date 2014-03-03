package com.anlong.fileserver.test.socketupload;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.anlong.fileserver.common.StaticValue;
import com.anlong.fileserver.common.Utils;
import com.anlong.fileserver.socket.FileSendInfo;





/**
 * @Title: ImageRequestHandle.java 
 * @Package com.anlong.fileserver.test.socketupload
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2014年1月20日 上午11:35:21 
 * @version V1.0   
 * @Description: 发送图片文件请求编码类
 */
public class ImageRequestHandle {
	private static Logger logger = Logger.getLogger(ImageRequestHandle.class);
	
	private ByteArrayOutputStream byteArrayOutputStream = null;
	private DataOutputStream  dataOutputStream = null;
	
	
	public void ImageEncode(FileSendInfo fileSendInfo,String srcUrl){
		OutputStream outputStream = null;
		try {
			

			// TODO 获取输出流
			outputStream = InitFileSocketServer.getOutputStream();
			if (outputStream == null)
				return;
			
			//  定义字节流和数据流
			byteArrayOutputStream = new ByteArrayOutputStream();
			dataOutputStream = new DataOutputStream(byteArrayOutputStream);
			
			//  将图片文件流化并计算文件大小
			byte[] imageBuf = HandleImageDataSteam(fileSendInfo,srcUrl);
				
			//  写入参数至数据流
			writeContent(fileSendInfo, dataOutputStream);
			
			// TODO 将图片字节数据写入输出流
			if (imageBuf == null)
				byteArrayOutputStream.write(0);
			else 
				byteArrayOutputStream.write(imageBuf);
						
			//  获取内存缓冲区中的数据
			byte[] buf = byteArrayOutputStream.toByteArray();
			logger.info("图片大小:"+buf.length);
			// TODO 发送图片文件
			outputStream.write(buf);
			outputStream.flush();
			logger.info("图片已发送!");
		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// TODO 关闭流
			if(dataOutputStream != null){
				try {
					dataOutputStream.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if(byteArrayOutputStream != null){
				try {
					byteArrayOutputStream.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	
	private byte[] HandleImageDataSteam(FileSendInfo fileSendInfo,String url){
		try {
			if (Utils.isNull(url))
				return null;
			// TODO 将图片文件转化为字节数组
			byte[] imageSteam = readFromSD(url);
			
			if (imageSteam == null)
				return null;
			// TODO 设置图片字节大小
			logger.info("图片文件字节大小: " + imageSteam.length);
			fileSendInfo.setSize(imageSteam.length);
			
			return imageSteam;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 从SD卡中读取图片文件，并且以字节流返回
	 * @param fileUrl
	 * @return
	 */
	private byte[] readFromSD(String fileUrl){
		InputStream inputStream = null;
		try {
			File file = new File(fileUrl);
			if (!file.exists())
				return null;
			// TODO 读取SD卡中的图片文件
			inputStream = new BufferedInputStream(new FileInputStream(file));
			byte[] data = new byte[inputStream.available()];
		    inputStream.read(data);
		    return data;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if( inputStream != null ){
				     inputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	 }


	
	
	/**
	 * 写入图片文件数据至数据流
	 * @param request100
	 * @param dataOutputStream
	 * private int type ;
	private String md5 ;
	private int size ;
	 */
	private void writeContent(FileSendInfo fileSendInfo, DataOutputStream dataOutputStream){
		try {
			dataOutputStream.writeInt(fileSendInfo.getType());
			dataOutputStream.writeChars(fileSendInfo.getMd5());
			dataOutputStream.writeInt(fileSendInfo.getSize());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * @Title: wirteString 
	 * @Description: TODO 读出字符串数据字节流
	 * @author dingjh 
	 * @param @param str
	 * @param @param dataOutputStream
	 * @param @throws IOException     
	 * @return void     
	 * @throws
	 */
	private void wirteString(String str, DataOutputStream dataOutputStream) throws IOException{
		if(Utils.isNotNull(str)){
			byte[] byteArr = str.getBytes(StaticValue.CHARSET_NAME);
			dataOutputStream.writeShort((short) byteArr.length);
			dataOutputStream.write(byteArr);
		}else{
			dataOutputStream.writeShort((short) 0);
		}
	}
	
}
