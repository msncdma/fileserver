package com.anlong.fileserver.test.basetest;
/**
 * @Title: ExA.java 
 * @Package com.anlong.msg.test
 * @company ShenZhen anlong Technology CO.,LTD.   
 * @author lixl   
 * @date 2013-10-23 上午10:14:35 
 * @version V1.0   
 * @Description: private static ExA a = new ExA()是静态变量.
 * java里面静态变量与静态代码块是按代码先后顺序执行
 */
public class ExA {
	private static ExA a = new ExA();
	static {
		System.out.println("父类--静态代码块");
	}

	public ExA() {
		System.out.println("父类--构造函数");
	}

	{
		System.out.println("父类--非静态代码块");
	}

	public static void main(String[] args) {
		new ExB();
	}
}

class ExB extends ExA {
	private static ExB b = new ExB();
	static {
		System.out.println("子类--静态代码块");
	}
	{
		System.out.println("子类--非静态代码块");
	}

	public ExB() {
		System.out.println("子类--构造函数");
	}
}
