package com.anlong.fileserver.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.apache.mina.core.session.IoSession;

/**
 * @Title: UpdateService.java 
 * @Package com.anlong.msg.socket
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2013-10-23 下午5:31:41 
 * @version V1.0   
 * @Description: 文件上传接口
 */
public interface FileUpdate {
	
	public void parseArgs(DataInputStream dis,DataOutputStream dos,FileSendInfo fileSendInfo);
	
	public void execute(IoSession session,DataInputStream dis,DataOutputStream dos,FileSendInfo fileSendInfo);

}
