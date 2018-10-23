package com.janborn.www.util;

public class MessageBox {
	/*显示机器人气泡*/
	public static final int ROBOT_BUBBLE = 1;
	/*显示底部按钮*/
	public static final int BUTTON = 2;
	/*隐藏底部文本框*/
	public static final int HIDE_BUTTON = 3;
	/*显示底部文本框*/
	public static final int TEXTAREA = 4;
	/*隐藏底部文本框*/
	public static final int HIDE_TEXTAREA = 5;
	/*显示用户气泡*/
	public static final int USER_BUBBLE = 6;
	
	private String word = "";
	private int type = -1;
	
	public MessageBox() {
	}
	
	public MessageBox(String w,int t) {
		word = w;
		type = t;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	

}

