/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: Write.java
* 定时输出语句，起到一种思考等待的仿真效果
*
* @author Deng Yang
    * @Date    2018-05-04
* @version 0.00
*/

package com.janborn.www.util;

import java.awt.Graphics2D;
import java.util.Date;
import java.util.Random;

import com.janborn.www.Demo;

public final class WriteTimer {
	public WriteTimer() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 定时输出
	 * 
	 * @param String words 需要输出的字符串语句
	 * @return none
	 */
	public static void println(String words) {
//		Date begin = new Date();
//		Date end = new Date();
//		long mintime = 1200;
//		long maxtime = 1800;
//		Random random = new Random();
//		long timer = (long)(random.nextInt()*maxtime%(maxtime-mintime+1) + mintime);
//		while(end.getTime()-begin.getTime()<timer) {
//			end= new Date();
//		}
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		MessageBox m = new MessageBox(words,MessageBox.ROBOT_BUBBLE);
		Demo.robotThread.robotMessageBuffer.add(m);
//		ChatGUI.addBubble(words, ChatGUI.ROBOT_SAY);
	}
	
	public static void paintButton(String w) {
		MessageBox m = new MessageBox(w,MessageBox.BUTTON);
		Demo.robotThread.robotMessageBuffer.add(m);
	}
	public static void paintTextArea() {
		MessageBox m = new MessageBox(null,MessageBox.TEXTAREA);
		Demo.robotThread.robotMessageBuffer.add(m);
	}
	
	public static void hideTextArea() {
		MessageBox m = new MessageBox(null,MessageBox.HIDE_TEXTAREA);
		Demo.robotThread.robotMessageBuffer.add(m);
	}
	public static void hideButton() {
		MessageBox m = new MessageBox(null,MessageBox.HIDE_BUTTON);
		Demo.robotThread.robotMessageBuffer.add(m);
	}

}
