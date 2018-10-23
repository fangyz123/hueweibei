package com.janborn.www.util;

import java.util.Date;

public class WaitMessage implements Runnable{
	private long start;
	private long end;
	/*表示该线程是否应该终止*/
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
				WriteTimer.println("可能网络状态不太好哦，请稍等一会儿！");
				break;
			case 3:
				WriteTimer.println("网络大概塞车了吧？？！");
				break;
			case 5:
				WriteTimer.println("网速太慢了吧，我都快没耐心了……");
				WriteTimer.println("我跟你说，你说不定已经断网了呢:-(");
				break;
			}
		}
	}


}
