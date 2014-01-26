package com.anlong.fileserver.test.basetest;

/**
 * @Title: ExchangeNumber.java 
 * @Package com.anlong.fileserver.test.basetest
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2014年1月24日 上午11:33:20 
 * @version V1.0   
 * @Description: 交换数字
 */
public class ExchangeNumber {
	
	public static void exchangeMethod1(int a,int b){
		System.out.println("交接前       a="+a+",b="+b);
		a = a+b;
		b = a-b;
		a = a-b;
		System.out.println("交接后       a="+a+",b="+b);
	}

	public static void main(String[] args) {
		int a = 3,b=12;
		exchangeMethod1(a, b);
		System.out.println("调用后       a="+a+",b="+b);
	}
}
