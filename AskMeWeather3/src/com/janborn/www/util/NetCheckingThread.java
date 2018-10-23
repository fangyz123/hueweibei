package com.janborn.www.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import com.janborn.www.Demo;
import com.janborn.www.GUI.MainGUI;

public class NetCheckingThread extends Thread {
	public static boolean netanswer = false;

	@Override
	public void run() {
		MainGUI.change2CheckLabel();;
		/*是否第一次检查*/
		boolean f = true;
		/*是否是重新连上的*/
		boolean reCon = false;
		
		while(!isConnect("https://www.seniverse.com/")) {
			if(f==true) {
				f=false;
				Demo.playSounds();
				MainGUI.wordLabel1.setText("人类，");
				MainGUI.wordLabel2.setText("我连不上网络了。");
				MainGUI.wordLabel3.setText("");
				MainGUI.change2CheckErrorLabel();
				reCon = true;
			}
			System.out.println("没网");
			
		}
		if (isConnect("https://www.seniverse.com/")) {
			MainGUI.clearMoreLabel();
			Demo.playSounds();
			if(reCon) {
				MainGUI.wordLabel1.setText("嘿！人类！");
				MainGUI.wordLabel2.setText("网路重新连上了，");
				MainGUI.wordLabel3.setText("你还在吗？");
			}
			else {
				MainGUI.wordLabel1.setText("嘿！人类！");
				MainGUI.wordLabel2.setText("网络状况不错！");
				MainGUI.wordLabel3.setText("");
			}
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			Demo.mainGreeting();
			synchronized(Demo.mainO) {
				Demo.mainO.notifyAll();
			}
		}
	}
	
	/**
	 * 检查网络连接
	 * <p>使用IO流打开网页连接。如果能打开说明网络连接正常，返回true；如果抛出异常，则网络连接失败，返回false</p>
	 * 
	 * @param String net 要请求访问的网址
	 * @return boolean
	 */
	public boolean isConnect(String net){  
		boolean connect=false;
		URL url = null;  
        try {  
            url = new URL(net);  
            try {  
                InputStream in = url.openStream();  
                in.close();  
                connect=true;
            } catch (IOException e) {  
            	connect=false;
            }  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        }  
        return connect;
    }

	public NetCheckingThread() {
		// TODO Auto-generated constructor stub
	}

	public NetCheckingThread(Runnable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public NetCheckingThread(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public NetCheckingThread(ThreadGroup arg0, Runnable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public NetCheckingThread(ThreadGroup arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public NetCheckingThread(Runnable arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public NetCheckingThread(ThreadGroup arg0, Runnable arg1, String arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	public NetCheckingThread(ThreadGroup arg0, Runnable arg1, String arg2, long arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

}
