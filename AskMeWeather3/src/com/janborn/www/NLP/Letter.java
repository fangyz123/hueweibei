/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: Letter.java
* 记录每一个词条的信息，包括词条、词性
* 【词性代码】
* LOC = 地点
* T	= 时间
* L	= 生活指数
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

