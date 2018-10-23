/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: UserData.java
* ģ���û����ݿ�
* ���û���ţ�no�����û�����name������ʶһ����¼
* �����û���֪���Լ����û���ţ����Ի���Ҫ���û�������ʶ
* 
* ע��0��Ϊ��ʱ�û�����ʹû�е�¼��Ҳ���Լ�¼��ʷ��Ϣ
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
	 * ���л�ID���Զ����ɣ�
	 */
	private static final long serialVersionUID = 1L;
	/* ��¼�û����� */
	private SerializationArrayList<User> userData = null;
	/* ��¼���� */
	private int size = 0;
	/* ״̬���Ƿ��¼ */
	private static boolean whetherLogIn = false;
	/* ����¼����ǰ���û����(>2) */
	private static int loginNo = 0;

	/* ���л��ļ�����·�� */
	private final String root = System.getProperty("user.dir") + "\\userData\\user.txt";
	/* ���л��ļ� */
	private final File userFile = new File(root);

	public void initUserData() {
		size = 0;
		whetherLogIn = false;
		loginNo = 0;
		userData = new SerializationArrayList<User>();
		User u = new User("��ʱ�û�",null,null);
		userData.add(u);
		serializeUser();
	}
	
	public boolean isLogin() {
		if(whetherLogIn) return true;
		else return false;
	}

	public UserData() {
		// �����л��û��ļ�
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
	 * �жϸõص���û��Ƿ��������
	 * */
	public boolean whetherSql(String loc) {
		boolean returns = false;
		User u = userData.get(loginNo);
		//�����ʷ��¼����
		if(u.getLocQueue().size()!=0) {
			for(String p :u.getLocQueue()) {
				//����������ѯ������ص�
				if(p.equals(loc)) {
					returns = true;
					break;
				}
			}
		}
		return returns;
	}
	
	/**
	 * �����û��ص��ѯ��ʷ��¼����</br>
	 * �Ƚ�preLocation�Ӷ����е��������������
	 * */
	public void refreshUserLocQueue() {
		if(userData.get(loginNo).getLocQueue().size()!=0)
			userData.get(loginNo).getLocQueue().add(
				userData.get(loginNo).getLocQueue().remove()
			);
	}
	
	/**
	 * Ϊ�û��ص��ѯ��ʷ��¼���ӱ���
	 * */
	public void addUserLocQueue(String loc) {
		userData.get(loginNo).getLocQueue().add(loc);
	}
	
	/**
	 * �����û��������û���š�δ��¼Ҳ��ʹ��
	 * @return
	 * <ul>
	 * <li>-1 ������</li>
	 * <li>���� ���ر��</li>
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
	 * �����û����жϸ��û��Ƿ����
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
	 * �����û��������룬��֤�Ƿ�ƥ��
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
	 * ע��</br>
	 * ע��ӿڡ�ͨ�����ʸ÷�������ע�ᡣ
	 * 
	 * @return boolean
	 *         <ul>
	 *         <li>true:ע��ɹ������µ�ǰ��</li>
	 *         <li>false:ע��ʧ��</li>
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
	 * ��¼</br>
	 * ��¼�ӿڡ�ͨ�����ʸ÷������Ե�¼��
	 * 
	 * @return boolean
	 *         <ul>
	 *         <li>true:��¼�ɹ������µ�ǰ��</li>
	 *         <li>false:��¼ʧ��</li>
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
	 * ע����ǰ�û����˳���¼��</br>
	 * �޸�no��whetherLogIn
	 */
	public void logOff() {
		loginNo = -1;
		whetherLogIn = false;
	}

	/**
	 * ɾ��һ���û���¼</br>
	 * ���ѵ�¼������ɾ��
	 * 
	 * @param name
	 *            �û���
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
	 * ����¼��ͨ���û���Ų�ѯ�û���һ�β�ѯ�ĵص�
	 * 
	 * @return String ��һ�β�ѯ�ĵص�
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
	 * ����¼��ͨ���û���ż�¼���������£��û���һ�β�ѯ�ĵص�
	 * 
	 * @return String ��һ�β�ѯ�ĵص�
	 */
	public void setPreLocation(String loc) {
		userData.get(loginNo).setPreLocation(loc);
	}

	/**
	 * ���ѵ�¼��ͨ���û���Ų�ѯ��ǰ�û��ǳ�
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
	 * �޸Ķ�Ӧ�û����ǳ�</br>
	 * ��¼״̬���޸��ǳ�
	 */
	public void setNickName(String userName, String newNickName) {
		for (int i = 0; i < userData.size(); i++) {
			if (userName.equals(userData.get(i).getName())) {
				userData.get(i).setNickName(newNickName);
			}
		}
	}

	/**
	 * ɾ���û��ļ�
	 */
	public boolean deleteData() {
		if (userFile.delete())
			return true;
		else
			return false;
	}

	// �����л�
	public UserData deserializeUser() {
		UserData u = new UserData(1);
		ObjectInputStream ois = null;
		if (userFile.exists()) {
			try {
				ois = new ObjectInputStream(new FileInputStream(userFile));
			} catch (IOException e) {
				System.out.println("�Ҳ������л��ļ�");
				e.printStackTrace();
			}
			try {
				u = (UserData) ois.readObject();
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("��δ�ҵ�");
				e.printStackTrace();
			}
		}

		// u.toString();
		return u;
	}

	// ���л�
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
