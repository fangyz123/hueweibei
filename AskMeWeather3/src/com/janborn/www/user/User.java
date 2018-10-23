/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: User.java
* ��¼�û���Ϣ
*
* @author Deng Yang, Liu Hengren, Fang Yuzhen
    * @Date    2018-05-04
* @version 1.00
*/

package com.janborn.www.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class User implements Serializable{
	/**
	 * ���л�ID���Զ����ɣ�
	 */
	private static final long serialVersionUID = 1L;
	/* �û��� */
	private String name = null;
	/* ���� */
	private String password = null;
	/* �û�IP:ͣ�� */
//	private String ip = null;
	/* �û���� */
	private int no = -1;
	/* �ǳ� */
	private String nickName = null;
	/* �ϴβ�ѯ�ĵص� */
	private String preLocation = null;
	/*��ѯ���ĵص�
	 * ���ص㱻�ٴβ�ѯ�����������ӣ���֤��ʼ���ڶ�β
	 * */
	private Queue<String> locQueue = new LinkedBlockingQueue<>();

	// ���캯��
	public User() {
	}
	public User(String name, String nickName,String pw) {
		this.name = name;
		this.password = pw;
		this.nickName = nickName;
	}
	

	public Queue<String> getLocQueue() {
		return locQueue;
	}
	
	@Override
	public String toString() {
		return "[name="+name+"][password="+password+"][nickName="+nickName+"]";
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	public String getIp() {
//		return ip;
//	}

	public String getPreLocation() {
		return preLocation;
	}

	public void setPreLocation(String preLocation) {
		this.preLocation = preLocation;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

//	public void setIp() {
//		InetAddress address;
//		try {
//			address = InetAddress.getLocalHost();// ��ȡ���Ǳ��ص�IP��ַ
//			ip = address.getHostAddress();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
