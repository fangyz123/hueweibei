/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: Demand.java
* 记录转化得到的用户需求
*
* @author Deng Yang
    * @Date    2018-08-12
* @version 1.00
*/
package com.janborn.www.NLP;


public class Demand {
	private String key = null;
	private int num;

	public Demand() {
	}
	
	public Demand(String key, int num) {
		this.key = key;
		this.num = num;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	

}
