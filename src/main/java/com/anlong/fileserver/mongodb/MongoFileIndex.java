package com.anlong.fileserver.mongodb;



import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;


/**
 * @Title: MongoFileIndex.java 
 * @Package com.anlong.fileserver.mongodb
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2013-9-10 上午11:02:00 
 * @version V1.0   
 * @Description: FILE_ID  FILE_NAME  FILE_PATH  FILE_SUFFIX
 */
public class MongoFileIndex {
	
	public static final  String FILE_ID = "FILE_ID";
	public static final  String FILE_NAME = "FILE_NAME";
	public static final  String FILE_PATH = "FILE_PATH";
	public static final String FILE_SUFFIX = "FILE_SUFFIX";
	
	public static final String TABLE_NAME = "file_index";
	private static DBCollection collection = null;

	public static DBCollection getDBCollection (){
		if(collection == null){
			collection = MongoMG.getDb().getCollection(TABLE_NAME);
		}
		return collection;
	}
	
	
	
	/**
	 * 单条记录添加
	 * @param fileId
	 * @param fileName
	 * @param filePath 
	 */
	public static void addFileIndex(String fileId,String fileName,String filePath,String fileSuffix){
		List<DBObject> dblist = new ArrayList<DBObject>();
		BasicDBObject msg = new BasicDBObject();
		msg.put(FILE_ID, fileId);
		msg.put(FILE_NAME,fileName);
		msg.put(FILE_PATH, filePath);
		msg.put(FILE_SUFFIX, fileSuffix);
		dblist.add(msg);
		getDBCollection().insert(dblist);
		
	}
	
	/**
	 * 批量添加
	 * @param list 
	 */
	public static void addFileIndex(List<FileIndexObj> list) {
		List<DBObject> dblist = new ArrayList<DBObject>();
		for (FileIndexObj fio : list) {
			BasicDBObject msg = new BasicDBObject();
			msg.put(FILE_ID, fio.getFileId());
			msg.put(FILE_NAME, fio.getFileName());
			msg.put(FILE_PATH, fio.getFilePath());
			msg.put(FILE_SUFFIX, fio.getFileSuffix());
			dblist.add(msg);
		}
		getDBCollection().insert(dblist);
	}

	
	public static  void deleteFileIndex(String id) {
		BasicDBObject conditionObj = new BasicDBObject();
		conditionObj.put(FILE_ID, id);
		WriteResult result =	getDBCollection().remove(conditionObj);
	}
	
	
	/**
	 * 文件索引是否存在
	 * @param md5
	 * @return 
	 */
	public static boolean isExistFile(String md5){
		List<String> query = new ArrayList<String>();
		query.add(md5);
		
		List<FileIndexObj> result = getFileIndex(query);
		
		if(result.size()>0){
			return true;
		}else{
			return false;
		}
	}

	
	/**
	 * 获取MONGO中保存的文件路径
	 * @param md5
	 * @return 
	 */
	public static String getFilePath(String md5){
		List<String> query = new ArrayList<String>();
		query.add(md5);
		
		List<FileIndexObj> result = getFileIndex(query);
		
		if(result.size()>0){
			return result.get(0).getFilePath();
		}else{
			return "";
		}
	}
	
	public static List<FileIndexObj> getFileIndex(List<String> list) {
		List<FileIndexObj> result = new ArrayList<FileIndexObj>();
		for (String str : list) {
			BasicDBObject searchMsg = new BasicDBObject();
			searchMsg.put(FILE_ID, str);
			DBCursor cursor = getDBCollection().find(searchMsg);
			while (cursor.hasNext()) {
				DBObject dj = cursor.next();
				result.add(convertObj(dj));
			}
		}
		return result;
	}
	
	public static FileIndexObj convertObj(DBObject obj){
		FileIndexObj fio = new FileIndexObj();
		fio.setFileId((String)obj.get(FILE_ID));
		fio.setFileName((String)obj.get(FILE_NAME));
		fio.setFilePath((String)obj.get(FILE_PATH));
		return fio;
	}


}
