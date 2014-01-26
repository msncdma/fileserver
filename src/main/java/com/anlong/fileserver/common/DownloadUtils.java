package com.anlong.fileserver.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.anlong.fileserver.mongodb.MongoFileIndex;

public class DownloadUtils {
	
	 /** 
     * 获得指定文件的byte数组 
     */  
    public static byte[] getBytes(String filePath){  
        byte[] buffer = null;  
        try {  
            File file = new File(filePath);  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buffer;  
    }  

    public static String getTargetDownloadBasePATH(int code){
		String pathItem = "";
		String basePath = "";
		if("Linux".equals(System.getProperties().getProperty("os.name"))){
			pathItem =  "anlong.im.file.base.path.linux";
		}else{
			pathItem = "anlong.im.file.base.path.windows";
		}
		basePath = SystemGlobals.getValue(pathItem + "."+code);
		return basePath;
	}
    
    /**
     * 文件系统中保存的路径
     * @param md5
     * @return 
     */
    public static String getFileIOPath(String md5){
    	String downloadPath = MongoFileIndex.getFilePath(md5);
    	String prefix = SystemGlobals.getValue("anlong.im.file.catalog.prefix");
    	int index1 = downloadPath.lastIndexOf(prefix);
		int length = prefix.length();
		int index2  = downloadPath.indexOf("/", index1);
		int code = Integer.valueOf(downloadPath.substring(index1+length,index2));
		return DownloadUtils.getTargetDownloadBasePATH(code)+UploadUtils.getFileSeparator()+ MongoFileIndex.getFilePath(md5);
	}
}
