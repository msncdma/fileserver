package com.anlong.fileserver.mongodb;



import lombok.Data;

@Data
public class FileIndexObj {
	//MD5 
	private String fileId;
	private String fileName;
	private String filePath;
	private String fileSuffix;
}
