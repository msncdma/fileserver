package com.anlong.fileserver.test.httpload;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import com.anlong.fileserver.common.MD5;



/**
 * @Title: TestUpload.java 
 * @Package com.anlong.fileserver.test
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2013-9-26 下午5:48:28 
 * @version V1.0   
 * @Description: JAVA测试文件上传
 */
public class TestHttpUpload {

	/**
	 * 10－个人聊天图片；
		11－群组聊天图片；20－个人头像；21－群组头像；30－普通文件；40－个人语音文件；
		41－群组语音文件；
		50－PC安装包；51－ANDROID安装包；
		52－IOS安装包
		60－应用广场app包
		61－应用广场app图标
	 * @param args 
	 */
	public static void main(String[] args) {
		//文件稍大1.12M就报错 413
		//D:\\TEMP\\CentralServer_130605winbat_ok.jar    30
		File f = new File("D:\\tmp\\33.bmp");
		System.out.println("源文件剩余空间："+f.getFreeSpace()/1024/1024/1024);
		System.out.println("源文件总空间："+f.getTotalSpace());
		transferFile("11", f);
		
	}
	
	/**
	 * 文件HTTP上传到文件服务器
	 * 下载文件方法
	 * http://192.168.2.189:8684/fileserver/DownloadServlet/相对路径
	 * http://121.15.130.218:8684/fileserver/DownloadServlet?md5=08a8269da946ba76a49dadee74d4f1db
	 * http://127.0.0.1:8780/fileserver/Common/fileGet.action?path=相对路径&fileName=快快.png
	 * http://121.15.130.218:8684/fileserver/Common/fileGet.action?path=mcdownload1/group-chat-pic/origin/2014/2014-01-13/08a8269da946ba76a49dadee74d4f1db&fileName=20140115112820107676387.jpg
	 * @param type
	 * @param f
	 * @return
	 */
	public static void transferFile(String type, File f) {
		try {
			HttpClient client = new HttpClient();
			//http://121.15.130.218:8684/
			//http://192.168.0.221:8780/
			//http://192.168.0.228:8780/
			PostMethod method = new PostMethod("http://121.15.130.218:8684/fileserver/UploadServlet");

			try {
				// 设置字符串参数
				StringPart sp1 = new StringPart("type", type);
				String fileMD5 = MD5.getFileMD5String(f);
				System.out.println("file md5 value:"+fileMD5);
				StringPart sp2 = new StringPart("md5", fileMD5);
				StringPart sp3 = new StringPart("size", String.valueOf(f
						.length()));
				StringPart sp4 = new StringPart("file_suffix", f.getName()
						.substring(f.getName().lastIndexOf(".")));
				// 设置文件参数
				FilePart fp1 = new FilePart("file_name", f);
				method.setRequestEntity(new MultipartRequestEntity(new Part[] {
						sp1, sp2, sp3, sp4, fp1 }, method.getParams()));
				// method.setRequestHeader("Content-type", "multipart/form-data");
				// 这一步把文件上传了
				int result = client.executeMethod(method);
				System.out.println(" client.executeMethod(method) : "+result);
				
				if (method.getStatusCode() == HttpStatus.SC_OK) {
					String responseStr = method.getResponseBodyAsString();
					System.out.println("执行成功："+responseStr);
					
				} else {
					System.out.println("执行失败：method.getStatusCode()  :"+method.getStatusCode());
				}
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				method.releaseConnection();
			}
		} catch (Throwable e) {
			System.out.println(e);
		}
	}


}
