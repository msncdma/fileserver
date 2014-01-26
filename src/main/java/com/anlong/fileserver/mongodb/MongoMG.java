package com.anlong.fileserver.mongodb;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * @Title: MongoMG.java 
 * @Package com.anlong.msgserver.mongodb
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2013-8-6 下午2:38:53 
 * @version V1.0   
 * @Description: MONGODB管理类 创建库、表 命令执行
 */
public class MongoMG {
	
	public static final String MONGODB_ADDRESS = "127.0.0.1";
	public static final int MONGODB_PORT = 27017;
	public static final String MONGODB_DBNAME = "fileserver";
	
	private static MongoClient client = null;
	private static DB db = null;
	
	

	public static MongoClient getMongoClient(){
		if(client == null){
			MongoClient mongoClient = null;
			try {
				mongoClient = new MongoClient(MONGODB_ADDRESS, MONGODB_PORT);
				client = mongoClient;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return client;
	}
	
	
	
	public static DB getDb(){
		if(db == null){
			db = getMongoClient().getDB(MONGODB_DBNAME);
		}
		return db;
	}
	

}
