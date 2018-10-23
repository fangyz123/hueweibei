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
		/*�Ƿ��һ�μ��*/
		boolean f = true;
		/*�Ƿ����������ϵ�*/
		boolean reCon = false;
		
		while(!isConnect("https://www.seniverse.com/")) {
			if(f==true) {
				f=false;
				Demo.playSounds();
				MainGUI.wordLabel1.setText("���࣬");
				MainGUI.wordLabel2.setText("�������������ˡ�");
				MainGUI.wordLabel3.setText("");
				MainGUI.change2CheckErrorLabel();
				reCon = true;
			}
			System.out.println("û��");
			
		}
		if (isConnect("https://www.seniverse.com/")) {
			MainGUI.clearMoreLabel();
			Demo.playSounds();
			if(reCon) {
				MainGUI.wordLabel1.setText("�٣����࣡");
				MainGUI.wordLabel2.setText("��·���������ˣ�");
				MainGUI.wordLabel3.setText("�㻹����");
			}
			else {
				MainGUI.wordLabel1.setText("�٣����࣡");
				MainGUI.wordLabel2.setText("����״������");
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
	 * �����������
	 * <p>ʹ��IO������ҳ���ӡ�����ܴ�˵��������������������true������׳��쳣������������ʧ�ܣ�����false</p>
	 * 
	 * @param String net Ҫ������ʵ���ַ
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
