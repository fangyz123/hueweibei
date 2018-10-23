/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: UserData.java
* 模拟用户数据库
* 用用户编号（no）或用户名（name）来标识一条记录
* 由于用户不知道自己的用户编号，所以还需要用用户名来标识
* 
* 注：0号为临时用户，即使没有登录，也可以记录历史信息
*
* @author Deng Yang, Liu Hengren, Fang Yuzhen
    * @Date    2018-08-10
* @version 0.00
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
import java.util.ArrayList;
import java.util.List;

import com.janborn.www.Demo;

public class UserData implements Serializable {
	/**
	 * 序列化ID（自动生成）
	 */
	private static final long serialVersionUID = 1L;
	/* 记录用户数据 */
	private SerializationArrayList<User> userData = null;
	/* 记录个数 */
	private int size = 0;
	/* 状态：是否登录 */
	private static boolean whetherLogIn = false;
	/* 若登录，当前的用户序号(>2) */
	private static int loginNo = 0;

	/* 序列化文件保存路径 */
	private final String root = System.getProperty("user.dir") + "\\userData\\user.txt";
	/* 序列化文件 */
	private final File userFile = new File(root);

	public void initUserData() {
		size = 0;
		whetherLogIn = false;
		loginNo = 0;
		userData = new SerializationArrayList<User>();
		User u = new User("临时用户",null,null);
		userData.add(u);
		serializeUser();
	}
	
	public boolean isLogin() {
		if(whetherLogIn) return true;
		else return false;
	}

	public UserData() {
		// 反序列化用户文件
		if (!userFile.exists()) {
			try {
				userFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			initUserData();
		} else {
			UserData u = deserializeUser();
			if (u.userData != null) {
				this.userData = u.userData;
				this.size = u.size;
			} else {
				initUserData();
			}
		}
	}

	public UserData(int i) {
		initUserData();
	}

	public File getUserFile() {
		return userFile;
	}

	public String getRoot() {
		return root;
	}
	
	public String getUserName() {
		User u = Demo.getUserData().userData.get(loginNo);
		return u.getName();
	}
	
	public String getUserNickName() {
		User u = Demo.getUserData().userData.get(loginNo);
		return u.getNickName();
	}
	
	public String getUserPreLoc() {
		User u = Demo.getUserData().userData.get(loginNo);
		return u.getPreLocation();
	}
	
	/**
	 * 判断该地点该用户是否曾经查过
	 * */
	public boolean whetherSql(String loc) {
		boolean returns = false;
		User u = userData.get(loginNo);
		//如果历史记录存在
		if(u.getLocQueue().size()!=0) {
			for(String p :u.getLocQueue()) {
				//且若曾经查询过这个地点
				if(p.equals(loc)) {
					returns = true;
					break;
				}
			}
		}
		return returns;
	}
	
	/**
	 * 更新用户地点查询历史记录队列</br>
	 * 先将preLocation从队列中弹出，再重新入队
	 * */
	public void refreshUserLocQueue() {
		if(userData.get(loginNo).getLocQueue().size()!=0)
			userData.get(loginNo).getLocQueue().add(
				userData.get(loginNo).getLocQueue().remove()
			);
	}
	
	/**
	 * 为用户地点查询历史记录增加表项
	 * */
	public void addUserLocQueue(String loc) {
		userData.get(loginNo).getLocQueue().add(loc);
	}
	
	/**
	 * 根据用户名返回用户编号。未登录也可使用
	 * @return
	 * <ul>
	 * <li>-1 不存在</li>
	 * <li>否则 返回编号</li>
	 * </ul>
	 * */
	public int getNo(String name) {
		int returns = -1;
		for(User u : userData) {
			if(u.getName().equals(name)) {
				returns = userData.indexOf(u);
				break;
			}
		}
		return returns;
	}

	/**
	 * 根据用户名判断该用户是否存在
	 */
	public boolean userIsExists(String name) throws IOException {
		for (int i = 0; i < userData.size(); i++) {
			if (name.equals(userData.get(i).getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 给定用户名和密码，验证是否匹配
	 */
	public boolean checkPassword(String name, String pass) {
		if(userData==null)
			return true;
		else {
			for (int i = 0; i < userData.size(); i++) {
				User u = userData.get(i);
				if (name.equals(u.getName()) && pass.equals(u.getPassword())) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 注册</br>
	 * 注册接口。通过访问该方法尝试注册。
	 * 
	 * @return boolean
	 *         <ul>
	 *         <li>true:注册成功，更新当前类</li>
	 *         <li>false:注册失败</li>
	 *         </ul>
	 */
	public boolean signIn(User u) {
		if(userData!=null) {
			for (int i = 0; i < userData.size(); i++) {
				if (u.getName().equals(userData.get(i).getName())) {
					return false;
				}
			}
		}
		User user = new User();
		user.setName(u.getName());
		user.setPassword(u.getPassword());
		user.setNickName(u.getNickName());
		userData.add(u);
		user.setNo(userData.indexOf(u));
		size++;
		serializeUser();
		return true;
	}

	/**
	 * 登录</br>
	 * 登录接口。通过访问该方法尝试登录。
	 * 
	 * @return boolean
	 *         <ul>
	 *         <li>true:登录成功，更新当前类</li>
	 *         <li>false:登录失败</li>
	 *         </ul>
	 */
	public boolean logIn(String name, String pass) {
		boolean returns= false;
		if(userData!=null && checkPassword(name,pass)){
			whetherLogIn = true;
			loginNo = getNo(name);
			returns = true;
		}
		else returns = false;
		
		return returns;
	}

	/**
	 * 注销当前用户（退出登录）</br>
	 * 修改no、whetherLogIn
	 */
	public void logOff() {
		loginNo = -1;
		whetherLogIn = false;
	}

	/**
	 * 删除一条用户记录</br>
	 * 若已登录，不能删除
	 * 
	 * @param name
	 *            用户名
	 */
	public boolean deleteUser(String name) {
		for (int i = 0; i < userData.size(); i++) {
			if (name.equals(userData.get(i).getName()) && userData.get(i).getNo() == -1) {
				userData.remove(i);
				size--;
				return true;
			}
		}
		return false;
	}

	/**
	 * 若登录，通过用户编号查询用户上一次查询的地点
	 * 
	 * @return String 上一次查询的地点
	 */
	public String sqlPreLocation() {
		for (int i = 0; i < userData.size(); i++) {
			if (loginNo == userData.get(i).getNo()) {
				return userData.get(i).getPreLocation();
			}
		}
		return null;
	}

	/**
	 * 若登录，通过用户编号记录（插入或更新）用户上一次查询的地点
	 * 
	 * @return String 上一次查询的地点
	 */
	public void setPreLocation(String loc) {
		userData.get(loginNo).setPreLocation(loc);
	}

	/**
	 * 若已登录，通过用户编号查询当前用户昵称
	 */
	public String sqlNickName() {
		for (int i = 0; i < userData.size(); i++) {
			if (loginNo == userData.get(i).getNo()) {
				return userData.get(i).getNickName();
			}
		}
		return null;
	}

	/**
	 * 修改对应用户的昵称</br>
	 * 登录状态：修改昵称
	 */
	public void setNickName(String userName, String newNickName) {
		for (int i = 0; i < userData.size(); i++) {
			if (userName.equals(userData.get(i).getName())) {
				userData.get(i).setNickName(newNickName);
			}
		}
	}

	/**
	 * 删除用户文件
	 */
	public boolean deleteData() {
		if (userFile.delete())
			return true;
		else
			return false;
	}

	// 反序列化
	public UserData deserializeUser() {
		UserData u = new UserData(1);
		ObjectInputStream ois = null;
		if (userFile.exists()) {
			try {
				ois = new ObjectInputStream(new FileInputStream(userFile));
			} catch (IOException e) {
				System.out.println("找不到序列化文件");
				e.printStackTrace();
			}
			try {
				u = (UserData) ois.readObject();
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("类未找到");
				e.printStackTrace();
			}
		}

		// u.toString();
		return u;
	}

	// 序列化
	@SuppressWarnings("resource")
	public boolean serializeUser() {

		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(userFile));
			oos.writeObject(this);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// u.toString();
		return true;
	}

}
