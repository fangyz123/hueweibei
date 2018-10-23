package com.janborn.www.util;

public class MessageBox {
	/*��ʾ����������*/
	public static final int ROBOT_BUBBLE = 1;
	/*��ʾ�ײ���ť*/
	public static final int BUTTON = 2;
	/*���صײ��ı���*/
	public static final int HIDE_BUTTON = 3;
	/*��ʾ�ײ��ı���*/
	public static final int TEXTAREA = 4;
	/*���صײ��ı���*/
	public static final int HIDE_TEXTAREA = 5;
	/*��ʾ�û�����*/
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

