package com.janborn.www.util;

import java.util.Date;

public class WaitMessage implements Runnable{
	private long start;
	private long end;
	/*��ʾ���߳��Ƿ�Ӧ����ֹ*/
	public static boolean f;{f = false;}
	
	public WaitMessage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i=0;
		while(true) {
			if(f) return;
			i++;
			start = new Date().getTime();
			end = new Date().getTime();
			while(end-start<3000) {
				if(f) return;
				end = new Date().getTime();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(f) return;
			switch(i) {
			case 1:
				WriteTimer.println("��������״̬��̫��Ŷ�����Ե�һ�����");
				break;
			case 3:
				WriteTimer.println("�����������˰ɣ�����");
				break;
			case 5:
				WriteTimer.println("����̫���˰ɣ��Ҷ���û�����ˡ���");
				WriteTimer.println("�Ҹ���˵����˵�����Ѿ���������:-(");
				break;
			}
		}
	}


}
