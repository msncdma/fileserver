package com.anlong.fileserver.socket;

import lombok.Data;

/**
 * 	10－个人聊天图片；
	11－群组聊天图片；
	12－KK空间图片；
	20－个人头像；
	21－群组头像；
	30－普通文件；
	40－个人语音文件；
	41－群组语音文件；
	50－PC安装包；
	51－ANDROID安装包；
	52－IOS安装包
	60－应用广场app包
	61－应用广场app图标
 *
 */
@Data
public class FileSendInfo {
	private int type ;
	private String md5 ;
	private int size ;
}
