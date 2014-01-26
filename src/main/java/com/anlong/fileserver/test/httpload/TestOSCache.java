package com.anlong.fileserver.test.httpload;

import java.io.IOException;

import com.anlong.fileserver.common.UploadUtils;
import com.opensymphony.oscache.base.NeedsRefreshException;

/**
 * @Title: TestOSCache.java 
 * @Package com.anlong.msg.test
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2014年1月8日 上午11:19:34 
 * @version V1.0   
 * @Description: TODO
 */
public class TestOSCache {

	/**
	 * 测试缓存
	 * 
	 * @param args
	 * @throws InterruptedException
	 * @throws NeedsRefreshException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException,
			NeedsRefreshException, IOException {
		long a = System.currentTimeMillis();
		for (int i = 200; i < 300; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 290; j < 300; j++) {
				sb.append(((byte[]) UploadUtils.getCache("45135/ce26b45e66cf9cbefad45e845043b0e3_"
						+ j + "_" + i)).length
						+ ",");
			}
			System.out.println(sb);
			for (int j = 0; j < 10; j++) {
				Thread.sleep(1000);
				System.out.println(j);
			}
		}
		long b = System.currentTimeMillis();
		System.out.println(b - a);

	}

}
