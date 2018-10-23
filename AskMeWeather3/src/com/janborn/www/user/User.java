/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: User.java
* 记录用户信息
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
	 * 序列化ID（自动生成）
	 */
	private static final long serialVersionUID = 1L;
	/* 用户名 */
	private String name = null;
	/* 密码 */
	private String password = null;
	/* 用户IP:停用 */
//	private String ip = null;
	/* 用户编号 */
	private int no = -1;
	/* 昵称 */
	private String nickName = null;
	/* 上次查询的地点 */
	private String preLocation = null;
	/*查询过的地点
	 * 若地点被再次查询，则出队再入队，保证它始终在队尾
	 * */
	private Queue<String> locQueue = new LinkedBlockingQueue<>();

	// 构造函数
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
//			address = InetAddress.getLocalHost();// 获取的是本地的IP地址
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
