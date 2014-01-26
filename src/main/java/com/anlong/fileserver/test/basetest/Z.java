package com.anlong.fileserver.test.basetest;

class X {
	//Y b = new Y();// 7、这里是父类成员变量初始化

	//static Y sb = new Y();// 1行、父类静态变量,输出static Y(静态代码块先初始化)，2行：Y

	static {
		System.out.println("static 静态代码块 X");// 3行、执行静态代码块
		//new Y();// 4行、这里只是输出Y,有static Y(静态代码块只执行一次)
	}

	X() {
		System.out.println("X 执行构造函数");// 8、父类成员变量初始化之后,执行父类构造器输出X
	}
}

class Y {
	static {
		System.out.println("static 静态代码块 Y");
	}

	Y() {// 执行构造函数
		// 这里有个super()==Object()
		System.out.println("Y 执行构造函数");
	}
}

public class Z extends X { // 从这里开始继承会调用父类的static和static成员变量：2：
	final static int mead = 45;
	final byte b = 16;
	//static Y sb = new Y();// 5行、子类的静态变量，输出Y
	static {
		System.out.println("static 静态代码块 Z");// 6行、子类的静态代码块
	}
	//Y y = new Y();// 9、这里是子类成员变量初始化

	Z() {
		// 这里有super()==new X()
		//this.y = null;
		System.out.println("Z 执行构造函数");// 10、子类成员变量初始化之后,执行子类构造器输出Z
	}

	public static void main(String[] args) {
		new Z();
	}
}