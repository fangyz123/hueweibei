/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: Letter.java
* ��¼ÿһ����������Ϣ����������������
* �����Դ��롿
* LOC = �ص�
* T	= ʱ��
* L	= ����ָ��
*
* @author Deng Yang
    * @Date    2018-08-12
* @version 1.00
*/
package com.janborn.www.NLP;


public class Letter {
	private String letter;
	private String pos;
	
	public Letter() {
		letter = null;
		pos = null;
	}
	public Letter(String letter, String pos) {
		this.letter = letter;
		this.pos = pos;
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}
	
	

}

