package com.anlong.fileserver.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.anlong.fileserver.common.StaticValue;
import com.anlong.fileserver.httppost.DownloadUtils;
import com.anlong.fileserver.httppost.UploadUtils;
import com.anlong.fileserver.mongodb.MongoFileIndex;

/**
 * @Title: UpdateServiceCommonFile.java 
 * @Package com.anlong.fileserver.socket
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2013-10-23 下午4:19:09 
 * @version V1.0   
 * @Description: 文件上传一般性处理
 */
public class FileUpdateCommonFile implements FileUpdate {
	
	private static Logger logger = Logger.getLogger(FileUpdateCommonFile.class);

	public void parseArgs(DataInputStream dis, DataOutputStream dos,
			FileSendInfo fileSendInfo) {
		try {
			
			fileSendInfo.setType(dis.readInt());
			
			short md5Length = dis.readShort();
			byte[] md5Arr = new byte[md5Length];
			dis.read(md5Arr);
			fileSendInfo.setMd5(new String(md5Arr,StaticValue.CHARSET_NAME));
			
			fileSendInfo.setSize(dis.readInt());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

	public void execute(IoSession session, DataInputStream dis,
			DataOutputStream dos, FileSendInfo fileSendInfo) {
		try {

			/** 校验参数传入的文件大小 */
			if(fileSendInfo.getSize() > UploadUtils.getFileByteSizeLimit()){
				logger.info("socket上传文件大小超过最大"+UploadUtils.getFileByteSizeLimit()+"byte限制");
				return;
			}
				
			/** 校验mongo索引 */
			if(MongoFileIndex.isExistFile(fileSendInfo.getMd5())){
				String ioPath = DownloadUtils.getFileDownloadIOPath(fileSendInfo.getMd5());
				String relativePath = MongoFileIndex.getFilePath(fileSendInfo.getMd5());
				/** 校验物理磁盘中是否存在  */
				if(new File(ioPath).exists()){
					//IP+相对path   全部是/ 
					//out.print(relativePath.replaceAll("\\\\", "/"));
					dos.writeBytes(relativePath.replaceAll("\\\\", "/"));
				}else{
					//索引中存在，但文件系统中不存在    此种情况注意MONGO直接删除索引中有
					MongoFileIndex.deleteFileIndex(fileSendInfo.getMd5());
					logger.info("index exists but file not exists:"+fileSendInfo.getMd5()+"|"+ioPath);
					//out.print("2");
					dos.writeBytes("2");
				}
				
			}


			/** 对比下传入与检验的MD5是否一致 */
			byte[] data = IOUtils.toByteArray(dis);
			String md5 = DigestUtils.md5Hex(data);
			/** 对比下传入与检验的MD5是否一致 */
			if(!fileSendInfo.getMd5().equals(md5)){// 解析出错
				//out.print("3");
				dos.writeBytes("3");
				logger.error("Request传入MD5	值校验不一致！请检查MD5值并重新提交POST上传请求");
				return;
			}

			/** 上传文件 */
			UploadUtils.socketUpdateFile(dis, dos, fileSendInfo.getType(),
					fileSendInfo.getMd5(), fileSendInfo.getSize());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
