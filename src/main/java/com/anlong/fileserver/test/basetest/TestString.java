package com.anlong.fileserver.test.basetest;

public class TestString {

	public static String getFileSeparator(){
		return System.getProperties().getProperty("file.separator");
	}
	
	public static void test1(){
		String path = "D:\\anlong\\upload\\mcdownload24\\2013\\2013-09-17\\ce8adbb6380be4597b2a5409f783dac2";
		path = path.replaceAll("\\\\", "/");   
		System.out.println(path);
		System.out.println(path.substring(path.lastIndexOf("/")+1));
	}
	
	public static void test2(){
		String target = "192.168.2.197:8780/fileserver//Common/fileGet.action?path=mcdownload1/group-chat-pic/thumb/2013/2013-10-11/18aa2cf7a0e8bff0bd93000e7faff6cd&filename=20131011124549128557291_origin.jpg";
		System.out.println(target.contains("thumb"));
		System.out.println(target.replace("thumb", "origin"));
	}
	
	public static void test3(){
		String s = "★◆";
		
		  byte b[] = s.getBytes();
		  System.out.println("length:"+b.length);
		  for (byte c : b) {
			System.out.println(c+"   ,");
		}
		  try {
		   String t = new String(b);
		   System.out.print(t);
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
	}
	
	public static void main(String[] args) {
		
		
		test3();
	}

}
