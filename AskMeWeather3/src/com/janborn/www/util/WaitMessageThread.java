package com.janborn.www.util;

public class WaitMessageThread {

	static Thread t =  null;
	
	public WaitMessageThread() {
		// TODO Auto-generated constructor stub
	}
	
	public static void startThread() {
		Thread.yield();
		t = new Thread(new WaitMessage());
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.setPriority(10);
		WaitMessage.f=false;
		t.start();
	}
	public static void stopThread() {
		WaitMessage.f=true;
		System.out.println("stop");
	}

}
