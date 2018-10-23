/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: Demo.java
* �����ࡣ
*
* @author Deng Yang
    * @Date    2018/4/17
* @version 2.3
* <ul>
* 	<li>2.0 ʵװ���µ��û�����</li>
* 	<li>2.1 ʵװ���µķִʲ��</li>
* 	<li>2.2 ʵװ���µ��û��������¼�ȣ�</li>
* 	<li>2.3 �����һ�����л����⡢ʵװ�û���¼����</li>
* </ul>
*/

package com.janborn.www;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.janborn.www.user.User;
import com.janborn.www.user.UserData;
import com.janborn.www.util.MessageBox;
import com.janborn.www.util.NetCheckingThread;
import com.janborn.www.util.SoundPlayer;
import com.janborn.www.util.WaitMessageThread;
import com.janborn.www.util.WriteTimer;
import com.janborn.www.weather.Weather;
import com.janborn.www.weather.Weather.DailyContent;
import com.janborn.www.weather.Weather.ErrorMessageException;
import com.janborn.www.GUI.ChatGUI;
import com.janborn.www.GUI.MainGUI;
import com.janborn.www.GUI.WelcomeGUI;
import com.janborn.www.NLP.Letter;
import com.janborn.www.NLP.MyNLP;


public class Demo{

	/*�ص�*/
	public static String location=null;
	/*����*/
	//��ʱȱʡ
	private static String language=null;
	/*���µ�λ*/
	//��ʱȱʡ
	private static String unit=null;
	/*��������������ѯ��ʱ��*/
	private static boolean dailyLife[] = new boolean[4];
	//��ʱȱʡ
	/*���ڴ洢����ָ�����������*/
	private static boolean lifeSuggestion[]=new boolean[6];
	/*���ڴ洢������ϸ��Ϣ���������*/
	private static boolean lifeDetil[]=new boolean[4];
	
	/*���ڼ�¼������Ϣ*/
	public static String userText=null;
	public static int answer = -1;
	
	/*��¼�Ƿ��Ѿ����꣬�Ծ���������*/
	public static boolean wheatherRain = false;
	
	/*�������ǣ����ڸ��ƻش�*/
	public static boolean netanswer = false;
	
	/*�������Ƿ��뿪*/
	public static boolean robotGoToWork = false;
	
	/*�������߳�*/
	public static RobotThread robotThread = new RobotThread();
	public static Object robotO = new Object();
	/*���߳�����ǰִ�л������̣߳�true*/
	public static boolean robotStop = false;
	/*�û��߳�*/
	public static UserThread userThread = new UserThread();
	public static Object userO = new Object();
	
	public static boolean waitAnswer = false;
	public static Object answerO = new Object();
	public static boolean waitUserText = false;
	public static Object userTextO = new Object();
	public static Object mainO = new Object();
	
	/*���ݿ�*/
	private static UserData userData = new UserData(); 
	
	public static int turn = 1;
	
	/*�û�����*/
	User user=null;
	
	public static UserData getUserData() {
		return userData;
	}
	
	/**
	 * �����������ڳ�ʼ���û�
	 * 
	 * @param none
	 * @return none
	 * @throws IOException 
	 */
	public Demo() {
//		initUser();
		//��ӭ����
 	   WelcomeGUI welcomeWIN = new WelcomeGUI();
 	   welcomeWIN.setVisible(true);
 	   try {
 		   Thread.sleep(3000);
 	   } catch (InterruptedException e) {
				e.printStackTrace();
 	   }
 	   welcomeWIN.setVisible(false);
 	   //����������
 	   Thread t = new Thread(new Runnable() {

		@Override
		public void run() {
			MainGUI mainWIN = new MainGUI();
	 		mainWIN.setVisible(true);
	 		//���������ڷ���
	 		mainServe();
		}
 		    		   
 	   });
 	   t.start();
 	   
	}
	
	public static void mainServe() {
		//���к�
		mainGreeting();
		//�������
		NetCheckingThread netThread = new NetCheckingThread();
		netThread.start();
		synchronized(mainO) {
			try {
				mainO.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//������������
		offerMainNowWeather();
	}
	
	public static void offerMainNowWeather() {
		Weather askWeather = null;
		try {
			askWeather = new Weather("ip",language,unit);
		} catch (IOException e) {
			//�����쳣
			NetCheckingThread netThread = new NetCheckingThread();
			netThread.start();
			synchronized(mainO) {
				try {
					mainO.wait();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		} catch (ErrorMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String location = askWeather.location2.getName();
		String text = askWeather.now.getText();
		String temperature = askWeather.now.getTemperature();
		if(location!=null && !location.equals("")
				&& text!=null && !text.equals("")
				&& temperature!=null && !temperature.equals("")) {
			Demo.playSounds();
			MainGUI.wordLabel1.setText(location);
			MainGUI.wordLabel2.setText("����"+text+"��");
			MainGUI.wordLabel3.setText("���"+temperature+"������ӡ�");
		}
		MainGUI.change2MoreLabel();
	}
	
	public static void mainGreeting() {
		//1.ʱ���ʺ���
		//��ȡʱ�����ж����������ϡ����绹������
		Calendar now = Calendar.getInstance();
		int time=now.get(Calendar.HOUR_OF_DAY);
		//�峿��05��01-06��59 
		//���ϣ�07��01-08��59 
		//���磺09��00-12��00 
		//���磺12��01-13��59 
		//���磺14��00-17��59 
		//����18��00-18��59 
		//���ϣ�19��00-23��59 
		//�賿��24��00-05��00
		int code = 1;
//		if(time>=5 && time<7)
//			code = 0;
//			WriteTimer.println("�������峿");
		if(time>=0 && time<9)
			code = 1;
		if(time>=9 && time<12)
			code = 2;
		if(time>=12 && time<14)
			code = 3;
		if(time>=14 && time<18)
			code = 4;
//		if(time>=18 && time<19)
//			code = 5;
//			WriteTimer.println("�����ǰ���ʱ�֣��������Ȱɣ�");
//		if(time>=19 && time<=21)
//			code = 6;
//			WriteTimer.println("���Ϻã�");
		if(time>18 && time<=23 || time==0)
			code = 7;
//			WriteTimer.println("���Ϻã�");
//		if(time>0 && time<5)
//			code = 8;
//			WriteTimer.println("ʱ�仹�磬����̯��һ���飬ϸƷһ������Դ��ճ��ɣ�");
		String greet = null;
		switch(code) {
		case 1:
			greet = "���Ϻ�";
			break;
		case 2:
			greet = "�����";
			break;
		case 3:
			greet = "�����";
			break;
		case 4:
			greet = "�����";
			break;
		case 7:
			greet = "���Ϻ�";
			break;
		}
		Demo.playSounds();
		MainGUI.wordLabel1.setText(greet+"��");
		MainGUI.wordLabel2.setText("���ǻ�����С��");
		MainGUI.wordLabel3.setText("���ƶ˹۲�������");
	}
	
	/**
	 * �û���ʼ��ģ��
	 * 
	 * @param none
	 * @return none
	 * @throws IOException 
	 */
	public void initUser() {
		user= new User();
	}
	
	/**
	 * Ӧ�ó�ʼ��ģ��</br>
	 * 1.���к�</br>
	 * 2.ѯ������</br>
	 * 3.�ж����������Ƿ���������֤�����������ٷ��ء�
	 * 
	 * @param User user �û���Ϣ
	 * @return boolean
	 * @throws IOException 
	 */
//	public boolean initMain(User user) {
//		//���к���ѯ������
//		greeting(user);
//		//�������״��
//		WriteTimer.println("�Եȣ��Ұ�����һ�����簡����");
//		netanswer = false;
//		if(checkNet())
//			return true;
//		else return false;
//	}
	
	/**
	 * �������
	 * <ul>
	 * <li>��������磬����true</li>
	 * <li>����ѯ���û��Ƿ�Ҫ������ѯ�������������ô��������������ʱ�Զ����ѣ�����ֱ���˳�����</li>
	 * 
	 * @param none
	 * @return boolean
	 * @throws IOException 
	 */
	public static boolean checkNet() {
		NetCheckingThread netThread = new NetCheckingThread();
		netThread.start();
		/*�Ƿ��һ�μ��*/
		boolean f = true;
		/*�Ƿ����������ϵ�*/
		boolean reCon = false;
		
		while(!isConnect("https://www.seniverse.com/") 
				&& !isConnect("https://login.bce.baidu.com/")) {
			if(f==true) {
				f=false;
				WriteTimer.println("����������������������ˣ�Ҳ����Ӧ�ø��ҿ���ɡ���");
				WriteTimer.println("����������ģ��Ҹû�ȥ����������Ҫ��ʱ���ٽ��ҡ�");
				
				ChatGUI.clearQuestionBox();
				ChatGUI.addQuestion("1�á�");
				ChatGUI.addQuestion("2�����ˣ��������㹤���ˡ�");

				
//				waitForIndex();
				
				//��չ��û������ʱ�Ĳ���
				if(ChatGUI.questionNO == 1) {
					WriteTimer.println("�ţ��������������ʱ���һ��������ġ�");
					WriteTimer.println("һ���������");
					reCon = true;
					continue;
					//��֮������յ����²�ѯ�ĺ������ټ�����磬�ڴ�֮ǰ�ȴ�
				}
				else {
					WriteTimer.println("���ټ�����");
				}
			}
			
		}
		if (isConnect("https://www.seniverse.com/") 
				&& isConnect("https://login.bce.baidu.com/")
				&& !netanswer) {
			WriteTimer.println("�ܺã�����״��ͨ��");
			if(reCon) WriteTimer.println("�٣����࣡��·���������ˣ��㻹����");
			netanswer=true;
		}
		return true;
	}
	
	/**
	 * �����������
	 * <p>ʹ��IO������ҳ���ӡ�����ܴ�˵��������������������true������׳��쳣������������ʧ�ܣ�����false</p>
	 * 
	 * @param String net Ҫ������ʵ���ַ
	 * @return boolean
	 */
	public static boolean isConnect(String net){  
		boolean connect=false;
		URL url = null;  
        try {  
            url = new URL(net);  
            try {  
                InputStream in = url.openStream();  
                in.close();  
                connect=true;
            } catch (IOException e) {  
            	connect=false;
            }  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        }  
        return connect;
    }
	
	public static String judgeTime() {
		//��ȡʱ�����ж����������ϡ����绹������
			Calendar now = Calendar.getInstance();
			int time=now.get(Calendar.HOUR_OF_DAY);
			String greeting = null;
			//�峿��05��01-06��59 
			//���ϣ�07��01-08��59 
			//���磺09��00-12��00 
			//���磺12��01-13��59 
			//���磺14��00-17��59 
			//����18��00-18��59 
			//���ϣ�19��00-23��59 
			//�賿��24��00-05��00
			if(time>=5 && time<7)
				greeting = "�������峿";
			if(time>=7 && time<9)
				greeting = "���Ϻ�";
			if(time>=9 && time<12)
				greeting = "�����";
			if(time>=12 && time<14)
				greeting = "�����";
			if(time>=14 && time<18)
				greeting = "�����";
			if(time>=18 && time<19)
				greeting = "���";
			if(time>=19 && time<=21)
				greeting = "���Ϻ�";
			if(time>21 && time<=23 || time==0)
				greeting = "���Ϻ�";
			if(time>0 && time<5)
				greeting = "���";
			return greeting;
	}
	
	/**
	 * ���û����к�
	 * <ol>
	 * <li>��������һ��ʱ���ʺ�</li>
	 * <li>�ʺá����ҽ���</li>
	 * <li>�Ǽ��û���Ϣ</li>
	 * </ol>
	 * <p>�ռ��û���¼��Ϣ�����Ŀ���Ǽ�¼�û���ip��ַ��������ʱ���ϵ���������Ŀǰ�Ѿ������ˡ�</p>
	 * <p>���⣬�û���Ϣ�����л������<i>userData</i>�û�Ŀ¼��</p>
	 * 
	 * @param user �û�����
	 * @return none
	 * @throws IOException 
	 */
//	public void greeting(User user) {
//		//2.���к�
//		WriteTimer.println("���ѽ�����ǻ�����С�ǣ������ƶ˹۲�����");
//		WriteTimer.println("Ŷ�����������Ұ�����");
//		WriteTimer.println("�Ǳ��Ǹ��ں�����С��һ��������ɣ�");
//		//3.ѯ������
//		//�ж��Ƿ����¿ͻ�
//		//���������¼��
//		if(user.getUserFile().length()!=0) {
//			WriteTimer.println("���ǻ����ǵ�һ�����졭��");
//			WriteTimer.println("�Ҽǵ����"+user.getName()+"����");
//			
//			//�ռ��û��ش�
//			//������ť
//			MainWindows.createButton("�ǵ�", true, 1);
//			MainWindows.createButton("�Ҳ����������", false, 2);
//			//�˴����Ը�Ϊ�û�����
//			waitForIndex();
//			
//			if(answer.equals("1"))
//				WriteTimer.println("�ٺ٣����ѽ�����࣡");
//			else {
//				WriteTimer.println("����ְ������ҵ�оƬ��������");
//				WriteTimer.println("�����°����ָ�����һ����");
//				WriteTimer.println("�ȵȣ��������������ֵĻ�����ȥ�ļ�¼�����Ҿ������������Ŷ��");
//				WriteTimer.println("�ҿɲ����³���������ġ�");
//				
//				MainWindows.createButton("��", true, 1);
//				MainWindows.createButton("�ٿ���һ��", false, 2);
//				waitForIndex();
//				
//				System.out.println(answer);
//				if(answer.equals("1")) {
//					if(user.deleteData()) {
//						WriteTimer.println("��־����ȥ��¼��ɾ����");
//						initUser();
//						WriteTimer.println("�ã���������԰������ָ������ˡ�");
//						userText=null;
//						while(userText==null || userText.length()==0) {
//							waitForLine();
//							if(userText==null || userText.length()==0)
//								WriteTimer.println("�㲻��ʲô�������룡�������У�");
//						}
//						user.setName(userText);
//						WriteTimer.println("Ŷ!"+user.getName()+"���ѽ��");
//						//���л�
//						if(user.serializeUser(user))
//							WriteTimer.println("������ֺܺüǣ����Ѿ���ס��Ŷ��");
//						else
//							WriteTimer.println("��¼������оƬ�������û���¼��ʧ��");
//					}
//					else
//						WriteTimer.println("��־��ɾ��ʧ�ܡ�");
//				}	
//			}
//		}
//		else {
//			WriteTimer.println("���ʲô��������֪����һ�������޲��ء���");
//			WriteTimer.println("��������������룺������������ֱ�Ӱ����ָ����ң��ҿɲ�֪�������������ֵķ���");
//			
//			userText=null;
//			waitForLine();
//			while(userText==null || userText.length()==0) {
//				WriteTimer.println("������ֲ���Ϊ�գ�");
//				waitForLine();
//			}
//			user.setName(userText);
//			
//			
//			//4.������Ӧ
//			WriteTimer.println("Ŷ!"+user.getName()+"���ѽ��");
//			//���л�
//			if(user.serializeUser(user))
//				WriteTimer.println("������ֺܺüǣ����Ѿ���ס��Ŷ��");
//			else
//				WriteTimer.println("��¼������оƬ�������û���¼��ʧ��");
//		}
//	}
	
	/**
	 * �ȴ������û�������Ϣ
	 * <p>���ڶ�����������û���������Ϣ��ֱ�������û���������Ϣ���ߴ����¼��Ż��˳��÷�����</p>
	 * 
	 * @param none
	 * @return none
	 */
	public static void waitForLine() {
		userText=null;
		WriteTimer.paintTextArea();	
		waitUserText = true;
		synchronized(userTextO) {
			System.out.println("�ȴ�����");
			try {
				userTextO.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * �ȴ������û���ť��Ϣ
	 * <p>���ڶ�ȡ���µİ�ť��Ϣ��ֱ�������û��İ�ť��Ϣ�Ż��˳��÷�����</p>
	 * 
	 * @param none
	 * @return none
	 */
	public static void waitForIndex() {
		answer = -1;
		waitAnswer = true;
		synchronized(answerO) {
			System.out.println("�ȴ����");
			try {
				answerO.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	

	
	/**
	 * ������ȡ��ѯ������ʵ��������
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void analyzeNowWeather() throws InterruptedException, EncryptedDocumentException, InvalidFormatException, IOException {		
		//1.ѯ�ʵص�
		WriteTimer.println("���ˣ���Ӧ�ð�����ĵ�������");
		String loc;
		//�Ƿ��ѯ��һ�εص�
		boolean preF = false;
		if((loc = userData.getUserPreLoc())!=null) {
			WriteTimer.println("���ǲ�"+loc+"����");
			//2.�����û�����
			waitForLine();
			MyNLP agreeNlp = new MyNLP();
			if(agreeNlp.agreeNlp(userText)) {
				WriteTimer.println("�õģ�");
				preF = true;
			}
			else 
				WriteTimer.println("������ĵ��أ�");
		}
		if(!preF) {
			while(true) {
				waitForLine();
				
				//3.ʵ����Nlp���󣬷����ص�
				Letter l = new Letter(userText,"LOC");
				MyNLP locationNlp = new MyNLP(l);
				location = locationNlp.getLocaction();
				netanswer = false;
				
				//��չ�������Ӵ����ж�һ���Ƿ��������ص㡣
				//5.ͨ����ѯExcel����ж��Ƿ���֧�ֵĵص�
				if(location==null) {
					WriteTimer.println("��˵������ط��Һ����Ҳ�����");
					WriteTimer.println("�鷳��һ��");
				}
				else {
					try {
						int f = MyNLP.judgeLocation(location);
						if(f==1 || f==2) {
							//���֧��
							if(f==1) {
								//�ж��Ƿ��������
								//����ʷ��¼������������ѯ��
								if(userData.whetherSql(location)) {
									WriteTimer.println(location+"������ط���֪������֮ǰ�ʹ��ң�");
									//ˢ����ʷ��¼
									userData.refreshUserLocQueue();
								}
								else {
									WriteTimer.println(location+"������֪����һ����������ĵط���");
									//���ص������ʷ��¼
									userData.addUserLocQueue(location);
								}
								//������ʷ��¼
								userData.setPreLocation(location);
								break;
							}
							else if(f==2) {
								WriteTimer.println("�������尡��Ҫ����ֱ�Ӱ����ʡ��"+location+"�İɣ�");
								//��ѡ�ť
								WriteTimer.paintButton("1��ʡ��İ�");
								WriteTimer.paintButton("2�����ط���");
		
								waitForIndex();
		
								if(answer==1) {
									if(userData.whetherSql(location)) {
										//ˢ����ʷ��¼
										userData.refreshUserLocQueue();
									}
									else {
										userData.addUserLocQueue(location);
									}
									//������ʷ��¼
									userData.setPreLocation(location);
									WriteTimer.println("�õģ�����Ͱ���飡");
									break;
								}
								else {
									WriteTimer.println("����ʲô�ط���");
									continue;											
								}
							}
							break;
						}
						else {
							WriteTimer.println("��־���ص㲻֧�֡�");
							WriteTimer.println("�ޡ������˷ǳ���Ǹ�����⡣");
							WriteTimer.println("Ҫ��Ҫ��һ���ط���");
							
							//��ѡ�ť
							WriteTimer.paintButton("1��һ��");
							WriteTimer.paintButton("2���˰�");
							
							waitForIndex();
							if(answer==1) {
								WriteTimer.println("Ҫ����ʲô�ط���");
								continue;
							}
							else break;	
						}
					} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * ������ȡ��ѯ����������������
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void analyzeDailyWeather() throws InterruptedException, EncryptedDocumentException, InvalidFormatException, IOException {	
		//1.ѯ�ʵص�
		WriteTimer.println("���ˣ��㻹û������Ҫ��ѯ�ĵص㣿");
		String loc;
		//�Ƿ��ѯ��һ�εص�
		boolean preF = false;
		if((loc = userData.getUserPreLoc())!=null) {
			WriteTimer.println("���ǲ�"+loc+"����");
			//2.�����û�����
			waitForLine();
			MyNLP agreeNlp = new MyNLP();
			if(agreeNlp.agreeNlp(userText)) {
				WriteTimer.println("�õģ�");
				preF = true;
			}
			else 
				WriteTimer.println("������ĵ��أ�");
		}
		//�����ǲ���һ�β���ĵط�
		if(!preF) {
			while(true) {
				//2.�����û�����
				waitForLine();
				
				//3.ʵ����Nlp���󣬷����ص�
				Letter l = new Letter(userText,"LOC");
				MyNLP locationNlp = new MyNLP(l);
				location = locationNlp.getLocaction();
				netanswer = false;
				
				if(location==null) {
					WriteTimer.println("��˵������ط��Һ����Ҳ�����");
					WriteTimer.println("�鷳��һ��");
				}
				else {
					//�����Ƿ���֧�ֵĵص�
					int f = MyNLP.judgeLocation(location);
					if(f==1 || f==2) {
						//���֧��
						if(f==1) {
							//�ж��Ƿ��������
							//����ʷ��¼������������ѯ��
							if(userData.whetherSql(location)) {
								WriteTimer.println(location+"������ط���֪������֮ǰ�ʹ��ң�");
								//ˢ����ʷ��¼
								userData.refreshUserLocQueue();
							}
							else {
								WriteTimer.println(location+"������֪����һ����������ĵط���");
								//���ص������ʷ��¼
								userData.addUserLocQueue(location);
							}
							//������ʷ��¼
							userData.setPreLocation(location);
							break;
						}
						else if(f==2) {
							WriteTimer.println("�������尡��Ҫ����ֱ�Ӱ����ʡ��"+location+"�İɣ�");
							//��ѡ�ť
							WriteTimer.paintButton("1��ʡ���");
							WriteTimer.paintButton("2�����ط���");
							
							waitForIndex();
							
							if(answer==1) {
								if(userData.whetherSql(location)) {
									//ˢ����ʷ��¼
									userData.refreshUserLocQueue();
								}
								else {
									userData.addUserLocQueue(location);
								}
								//������ʷ��¼
								userData.setPreLocation(location);
								WriteTimer.println("�õģ�����Ͱ���飡");
								break;
							}
							else {
								WriteTimer.println("����ʲô�ط���");
								continue;
							}
						}
					}
					else {
						WriteTimer.println("��־���ص㲻֧�֡�");
						WriteTimer.println("�ޡ������˷ǳ���Ǹ�����⡣");
						WriteTimer.println("Ҫ��Ҫ��һ���ط���");
						
						//��ѡ�ť
						WriteTimer.paintButton("1��һ��");
						WriteTimer.paintButton("2���˰�");
						
						waitForIndex();
						
						if(answer==1) {
							WriteTimer.println("Ҫ����ʲô�ط���");
							continue;
						}
						else return;
					}
				}
			}
		}
		
		//5.ѯ��Ҫ��ѯ������
		WriteTimer.println("�ٺٺ١�");
		WriteTimer.println("���˸����㣬ƾ���ҵ�������ֻ�ܰ�����������������");
		WriteTimer.println("ѡһ��ɣ�");
		
		//6.��������
		int times = 0;
		while(true) {
			times++;
			waitForLine();
			
			//7.����
			Letter l = new Letter(userText,"T");
			MyNLP dayNlp = new MyNLP(l);
			int index = dayNlp.getDemand().getNum();
			
			dailyLife = new boolean[4];
			if(index>=0) {
				dailyLife[index]=true;
				break;
			}
			else {
				switch(index) {
				//������Χ
				case -1:
					WriteTimer.println("�Ҳ鲻������ģ��������ҵ�������Χ");
					WriteTimer.println("�鷳��һ���");
					break;
				//�����а������ʱ��
				case -2:
					if(times==1)
						WriteTimer.println("��Ǹ�������ͬʱ��ü���������һᾫ����ѵģ�����һ��һ����");
					if(times>10)
						WriteTimer.println("��Ǹ�����������ҵ���˼��ֻ�ܴӽ��졢���졢������ѡһ�죡");
					else if(times>9) {
						WriteTimer.println("ѡһ�졭��");
						WriteTimer.println("��֪�����ж������= =��");
						WriteTimer.println("��Ī�����ڵ�Ϸ�ҡ�");
					}
					else if(times>8) {
						WriteTimer.println("����Ĳ��ǹ���Ҫ�����ģ�����һ����ʿ��");
						WriteTimer.println("����������������ҵĻ����Դ��͸��㣡");
						WriteTimer.println("��˵һ�飬ֻ�ܴӽ��졢���졢������ѡһ�죡");
					}
					else if(times>5){
						WriteTimer.println("�ҡ����Ҷ��������");
						WriteTimer.println("����˵һ�飬ֻ�ܴ��Ҹղ�˵���Ǽ�������ѡһ����");
					}
					
					else if(times>2) {
						WriteTimer.println("�ҡ���");
						WriteTimer.println("���������ָ���졢����ͺ��죡");
					}
					break;
				}				
			}
		}	
	}
	
	/**
	 * ������ȡ��ѯ����������ָ����
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void analyzeLifeSuggestion() throws InterruptedException, EncryptedDocumentException, InvalidFormatException, IOException {
		//1.ѯ�ʵص�
		WriteTimer.println("�������ѡ��һ���ص�");
		String loc;
		//�Ƿ��ѯ��һ�εص�
		boolean preF = false;
		if((loc = userData.getUserPreLoc())!=null) {
			WriteTimer.println("���ǲ�"+loc+"����");
			//2.�����û�����
			waitForLine();
			MyNLP agreeNlp = new MyNLP();
			if(agreeNlp.agreeNlp(userText)) {
				WriteTimer.println("�õģ�");
				preF = true;
			}
			else 
				WriteTimer.println("������ĵ��أ�");
		}
		if(!preF) {
			while(true) {
				//2.�����û�����
				waitForLine();
				
				//3.ʵ����Nlp���󣬷����ص�
				Letter l = new Letter(userText,"LOC");
				MyNLP locationNlp = new MyNLP(l);
				location = locationNlp.getLocaction();
				netanswer = false;
				
				//�ж��Ƿ�֧��
				if(location==null) {
					WriteTimer.println("��˵������ط��Һ����Ҳ�����");
					WriteTimer.println("�鷳��һ��");
				}
				else {
					int f = MyNLP.judgeLocation(location);
					if(f==1 || f==2) {
						//���֧��
						if(f==1) {
							//�ж��Ƿ��������
							//����ʷ��¼������������ѯ��
							if(userData.whetherSql(location)) {
								WriteTimer.println(location+"������ط���֪������֮ǰ�ʹ��ң�");
								//ˢ����ʷ��¼
								userData.refreshUserLocQueue();
							}
							else {
								WriteTimer.println(location+"������֪����һ����������ĵط���");
								//���ص������ʷ��¼
								userData.addUserLocQueue(location);
							}
							//������ʷ��¼
							userData.setPreLocation(location);
							break;
						}
						else if(f==2) {
							WriteTimer.println("�������尡��Ҫ����ֱ�Ӱ����ʡ��"+location+"�İɣ�");
							//��ѡ�ť
							WriteTimer.paintButton("1��ʡ���");
							WriteTimer.paintButton("2�����ط���");
							
							waitForIndex();
							
							if(answer==1) {
									if(userData.whetherSql(location)) {
										//ˢ����ʷ��¼
										userData.refreshUserLocQueue();
									}
									else {
										userData.addUserLocQueue(location);
									}
									//������ʷ��¼
									userData.setPreLocation(location);
									WriteTimer.println("�õģ�����Ͱ���飡");
								break;
							}
							else {
								WriteTimer.println("����ʲô�ط���");
								continue;
							}
						}
					}
					else {
						WriteTimer.println("��־���ص㲻֧�֡�");
						WriteTimer.println("�ޡ������˷ǳ���Ǹ�����⡣");
						WriteTimer.println("Ҫ��Ҫ��һ���ط���");
						
						//��ѡ�ť
						WriteTimer.paintButton("1��һ��");
						WriteTimer.paintButton("2���˰�");
						
						waitForIndex();
						
						if(ChatGUI.questionNO == 1) {
							WriteTimer.println("Ҫ����ʲô�ط���");
							continue;
						}
						else return;
					}
				}
			}
		}
		
		Arrays.fill(lifeSuggestion, false);

		//��ʾ
		WriteTimer.println("����֪��ʲô��Ϣ��?");
		WriteTimer.println("�������ܰ���ĺ�����,ֻ��Ϊ�����漸�������Ϣ�ͽ���Ŷ");
		WriteTimer.println("���¡�ϴ�������С��˶������С�������ǿ��");
		
		int times=0;
		while(true) {
			times++;
			waitForLine();
			
			Letter l =new Letter(userText,"L");
			MyNLP lifeNlp = new MyNLP(l);
			
			lifeSuggestion=new boolean[6];
			int index;
			if(lifeNlp.getDemand()!=null &&(index = lifeNlp.getDemand().getNum())!=-1) {
				lifeSuggestion[index] = true;
				break;
			}
			else {
				if(times>2) {
					WriteTimer.println("�ҡ���");
					WriteTimer.println("�������ò��ã�ֻ�ܴ��Ҹղ�˵��ѡ������ѡһ����");
				}
				else if(times>5){
					WriteTimer.println("�ҡ����Ҷ��������");
					WriteTimer.println("����˵һ�飬ֻ�ܴ��Ҹղ�˵��ѡ������ѡһ����");
				}
				else if(times>8) {
					WriteTimer.println("����Ĳ��ǹ���Ҫ�����ģ�����һ����ʿ��");
					WriteTimer.println("����������������ҵĻ����Դ��͸��㣡");
					WriteTimer.println("����˵һ�飬ֻ�ܴ��Ҹղ�˵��ѡ������ѡһ����");
				}
				else
					WriteTimer.println("��Ǹ�����������ҵ���˼��ֻ�ܴ��Ҹղ�˵��ѡ������ѡһ����");				
			}
		}
	}
	
	/**
	 * ������ȡ��ѯ�������û����飩
	 * 
	 * <p>�����������û���Ҫ���ҵ���Ŀ</p>
	 * */
	public static void analyzeLifeDetil() {
		Arrays.fill(lifeDetil, false);
		//��ʾ
		WriteTimer.println("�������ܰ���ĺ�����,ֻ��ֻ���������漸���������٣�");
		WriteTimer.println("�²���ϵ�������������");
		WriteTimer.println("����˵����һ����֪��ȫ������Ϣ��");
		
		int times=0;
		while(true) {
			times++;
			//�û�����������Ϣ
			waitForLine();
			
			Letter l =new Letter(userText,"D");
			MyNLP detilNlp = new MyNLP(l);
			int index;
			lifeDetil=new boolean[4];
			if(detilNlp.getDemand()!=null && (index = detilNlp.getDemand().getNum())!=-1) {
				lifeDetil[index] = true;
				break;
			}
			else {
				if(times>2) {
					WriteTimer.println("�ҡ���");
					WriteTimer.println("�������ò��ã�ֻ�ܴ��Ҹղ�˵��ѡ������ѡһ����");
				}
				else if(times>5){
					WriteTimer.println("�ҡ����Ҷ��������");
					WriteTimer.println("����˵һ�飬ֻ�ܴ��Ҹղ�˵��ѡ������ѡһ����");
				}
				else if(times>8) {
					WriteTimer.println("����Ĳ��ǹ���Ҫ�����ģ�����һ����ʿ��");
					WriteTimer.println("����������������ҵĻ����Դ��͸��㣡");
					WriteTimer.println("����˵һ�飬ֻ�ܴ��Ҹղ�˵��ѡ������ѡһ����");
				}
				else
					WriteTimer.println("��Ǹ�����������ҵ���˼��ֻ�ܴ��Ҹղ�˵��ѡ������ѡһ����");				
			}
		}
	}
	
	/**
	 * �ṩ������ϸ������Ϣ��
	 * 
	 * <p>�����û���Ҫ���ҵ���Ŀ</p>
	 * */
	public static void offerDetilLife(String start,String end) throws  ErrorMessageException
	{
		analyzeLifeDetil();
		try {
			Weather askDetil=null;
			netanswer = false;
			while(askDetil==null) {
				try {
					WaitMessageThread.startThread();
					askDetil = new Weather(location, start, end, language, unit);
					WaitMessageThread.stopThread();
				} catch (IOException e) {
					WaitMessageThread.stopThread();
					checkNet();
				}
			}
			
			askDetil.daily.getToday();
			DailyContent dailyAsk = askDetil.daily.getDailyAsk();
			
			int high=Integer.parseInt(dailyAsk.getHigh());
			int low=Integer.parseInt(dailyAsk.getLow());
			int hl=high-low;
			String day = null;
			if(start.equals("0")) day="����";
			else if(start.equals("1")) day="����";
			else if(start.equals("2")) day="����";
			
			if(lifeDetil[0]) {
				//�ſ�
				WriteTimer.println(day+"����"+dailyAsk.getText_night()+","
						+dailyAsk.getLow()+"��"+dailyAsk.getHigh()+"���϶ȡ�");
				
				//�²�
				WriteTimer.println("��ҹ�²�"+hl+"���϶ȡ�");
				if(hl>=10) WriteTimer.println("��ҹ�²��е�󣬼ǵ������·�����");
				//������
				if(dailyAsk.getPrecip()!=null && !dailyAsk.getPrecip().isEmpty()) {
					if(!wheatherRain) {
						WriteTimer.println("���⣬"+day+"��"+dailyAsk.getPrecip()+"%�Ŀ��ܻή��");
						if(dailyAsk.getPrecip().length()==2 && dailyAsk.getPrecip().charAt(0)>='6')
							WriteTimer.println("�ǵô�����ɡ�����ҿɲ��������ȥ�ģ�");
					}
				}
				else {
					WriteTimer.println("�����ʡ����Բ�����û�鵽��");
					WriteTimer.println("��Ų�������ɣ��ٺ١�");
				}
			}
			else {
				if(lifeDetil[1]) {
					//�ſ�
					WriteTimer.println(day+dailyAsk.getLow()+"��"+dailyAsk.getHigh()+"���϶ȡ�");
					//�²�
					WriteTimer.println("��ҹ�²�"+hl+"���϶ȡ�");
					if(hl>=10) WriteTimer.println("��ҹ�²��е�󣬼ǵ������·�����");
				}
				else if(lifeDetil[2]) {
					WriteTimer.println(day+"������"+dailyAsk.getText_night()+"�죡");
				}
				else if(lifeDetil[3]) {
					if(dailyAsk.getPrecip()!=null && !dailyAsk.getPrecip().isEmpty()) {
						if(!wheatherRain) {
							WriteTimer.println("���⣬"+day+"��"+dailyAsk.getPrecip()+"%�Ŀ��ܻή��");
							if(dailyAsk.getPrecip().length()==2 && dailyAsk.getPrecip().charAt(0)>='6')
								WriteTimer.println("�ǵô�����ɡ�����ҿɲ��������ȥ�ģ�");
						}
					}
					else {
						WriteTimer.println("�����ʡ����Բ�����û�鵽��");
						WriteTimer.println("��Ų�������ɣ��ٺ١�");
					}
				}
				
			}
			
			wheatherRain=false;
			
		}
		catch(ErrorMessageException e) {
			excReac(Weather.errorManager.getStatus_code());
		}
	}
	
	/**
	 * �ṩ����ʵ��������
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void offerNowWeather() throws InterruptedException, EncryptedDocumentException, InvalidFormatException {
		try {
			//1.������ز���
			netanswer = false;
			try {
				analyzeNowWeather();
			} catch (IOException e1) {
				checkNet();
			}
			//2.ʵ����������ѯ����
			Weather askWheather=null;
			netanswer = false;
			while(askWheather==null) {
				try {
					WaitMessageThread.startThread();
					askWheather = new Weather(location,language,unit);
					WaitMessageThread.stopThread();
					
				} catch (IOException e) {
					WaitMessageThread.stopThread();
					checkNet();
				}
			}
			//3.��������
			WriteTimer.println("�һ���������ѯ��ϣ�");
			WriteTimer.println("����"+location+askWheather.now.getText()+"�����ڲ��"
					+askWheather.now.getTemperature()+"�ȵ����Ӱ�");
			if(askWheather.now.getText().indexOf('��')!=-1)
				wheatherRain = true;
			//4.�²۲��䴦��������£�����һЩ���ģ�
			//���
			//ѯ���Ƿ���Ҫ��ѯ�����Ϣ�����Ҫ������������������ȥ�顰���족����Ϣ
			WriteTimer.println("��ô��?�һ��ܸ���������ϢŶ��");
			WriteTimer.println("����˵����ĸ���֮���");
			WriteTimer.println("��֪����");

			waitForLine();
			MyNLP agreeNlp = new MyNLP();
			answer = -1;
			if(agreeNlp.agreeNlp(userText))
				answer = 1;
			
			if(answer == 1)
				//1.��
				offerDetilLife("0","1");
			else
				//2.��
				WriteTimer.println("�Ǻðɡ�");
			
		}
		catch(ErrorMessageException e) {
			excReac(Weather.errorManager.getStatus_code());
		}
	}
	
	/**
	 * ��Ӧ�����������������Ĳ�ѯ�����Ӧ���û�
	 * 
	 * @param Weather askWeather ������Ϣ��ѯ���
	 * @param int day Ҫ��ѯ������
	 * @throws ErrorMessageException
	 * 
	 * */
	public static void lifeDailyReaction(Weather askWeather,int day) throws ErrorMessageException {
		switch(day) {
		case 0:
			askWeather.daily.getToday();
			break;
		case 1:
			askWeather.daily.getTomorrow();
			break;
		case 2:
			askWeather.daily.getTheDayAfterTomo();
			break;
		}
		if(askWeather.daily.getDailyAsk()!=null) {
			WriteTimer.println("�һ���������ѯ��ϣ�");
			switch(day) {
			case 0:
				WriteTimer.println("����");
				break;
			case 1:
				WriteTimer.println("����");
				break;
			case 2:
				WriteTimer.println("����");
				break;
			}
			WriteTimer.println("����"+askWeather.daily.getDailyAsk().getText_day()+"��"
								+"���"+askWeather.daily.getDailyAsk().getText_night()+"��"
								+askWeather.daily.getDailyAsk().getLow()+"��"+askWeather.daily.getDailyAsk().getHigh()+"��");
		}
		//����������Ӵ�	
		//���
		WriteTimer.println("��ô��������֪�����������������");
		
		waitForLine();
		MyNLP agreeNlp = new MyNLP();
		answer = -1;
		if(agreeNlp.agreeNlp(userText))
			answer = 1;
		
		if(answer==1)
			switch(day) {
			case 0:
				offerDetilLife("0","1");
				break;
			case 1:
				offerDetilLife("1","1");
				break;
			case 2:
				offerDetilLife("2","1");
				break;
			}
		else
		//2.��
			WriteTimer.println("�Ǻðɡ�");
		//ѯ�ʻ���Ҫ��ѯʲô��Ϣ�������ظ�
	}
	
	/**
	 * �ṩ��������������
	 * @throws ErrorMessageException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void offerDailyWeather() throws InterruptedException, EncryptedDocumentException, InvalidFormatException {
		try {
			//1.������ز���
			netanswer = false;
			try {
				analyzeDailyWeather();
			} catch (IOException e1) {
				checkNet();
			}
			
			//2.ʵ����������ѯ����
			String day="";
			int i;
			for(i=0;i<4;i++) {
				if(dailyLife[i]) {
					day=day+(i+1);
					break;
				}
			}
			Weather askWeather=null;
			netanswer = false;
			while(askWeather==null) {
				try {
					WaitMessageThread.startThread();
					askWeather = new Weather(location,"0",day,language,unit);
					WaitMessageThread.stopThread();
					//3.��������
				} catch (IOException e) {
					WaitMessageThread.stopThread();
					checkNet();
				}
			}
			lifeDailyReaction(askWeather,i);
			
		}
		catch(ErrorMessageException e) {
			excReac(Weather.errorManager.getStatus_code());
		}
	}
	
	/**
	 * ���ڴ�����֪�����������ķ���
	 * 
	 * @param String e �������
	 * */
	public static void excReac(String e) {
		switch(e) {
			case "AP010006":
				WriteTimer.println("�Բ�����û�в�ѯ����ص��Ȩ�ޡ�");
				break;
			case "AP010010":
				WriteTimer.println("�Բ�������ص��޷���ѯ��˵�����������أ��ٺ١�");
				break;
			case "AP010011":
				WriteTimer.println("�Һ�����ʧ�˷��򡭡����Ҳ���������У�");
				break;
			case "AP010014":
				WriteTimer.println("���Сʱ�Ĳ�ѯ�����Ѿ������ˣ��ҵ�Ъ�����");
				break;
			default:
				WriteTimer.println("��־��ϵͳ�ڲ������˲�����״�Ĵ���");
				break;
		}
	}
	
	/**
	 * ��Ӧ������������ָ���Ĳ�ѯ����ظ����û�
	 * @param Weather askLifeSuggestion ������ѯ��Ϣ
	 * 
	 * */
	public static void lifeSuggestionReaction(Weather askLifeSuggestion) {
		for(int i=0;i<=5;i++) {
			if(lifeSuggestion[i]) {
				switch (i) {
				case 0:
					switch (askLifeSuggestion.lifeSuggestion.getDressing()) {
					case "����":
						WriteTimer.println("��������"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",���鴩����֯�������Ķ��¡���ȹ������ȹ���̿�");
						break;
					case "����":
						WriteTimer.println("��������"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",���鴩�������ϵĳ���������ȹ����T��");
						break;
					case "����":
						WriteTimer.println("��������"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",���鴩���¡������¡�Ƥ�пˡ��������ס���ñ�����ס����޷���Ƥ��");					
						break;
					case "����":
						WriteTimer.println("��������"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",���鴩ë�¡����¡�ë��װ��������װ");
						break;
					case "����":
						WriteTimer.println("��������"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",���鴩�����������ϵĶ���װ��T��������ţ�����㡢���з���ְҵ��װ");
						break;
					case "������":
						WriteTimer.println("��������"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",���鴩��װ�����¡����¡�����װ���п�������װ����ë��");
						break;
					case "��":
						WriteTimer.println("��������"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",���鴩�����������ϵĶ���װ��T��������ţ�����㡢���з���ְҵ��װ");
						break;
					case "��":
						WriteTimer.println("��������"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",���鴩���¡����¡��д��¡����ס�ë�¡�ë��װ����װ��������");
						break;
					default:
						WriteTimer.println("�š�����ûʲô�����ԵĽ���");
						break;
					}
					break;
				case 1:
					WriteTimer.println("����"+askLifeSuggestion.lifeSuggestion.getCar_washing()+"ϴ��");
					break;
				case 2:
					WriteTimer.println("����"+askLifeSuggestion.lifeSuggestion.getFlu()+"��ð");
					break;
				case 3:
					//�˶�
					WriteTimer.println(askLifeSuggestion.lifeSuggestion.getSport());
					break;
				case 4:
					//����
					WriteTimer.println(askLifeSuggestion.lifeSuggestion.getTravel());
					break;
				case 5:
					WriteTimer.println("����������"+askLifeSuggestion.lifeSuggestion.getUv());
					break;
				default:
					break;
				}
			}
		}
	}

	
	/**
	 * �ṩ��������ָ����
	 * @throws ErrorMessageException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void offerLifeSuggestion() throws InterruptedException, EncryptedDocumentException, InvalidFormatException {
		//1.�Է���������Ϣ�������
		netanswer = false;
		try {
			analyzeLifeSuggestion();
		} catch (IOException e1) {
			checkNet();
		}
		//2.��ȡ����������Ϣ
		Weather askLifeSuggestion=null;
		netanswer = false;
		while(askLifeSuggestion==null) {
			try {
				WaitMessageThread.startThread();
				askLifeSuggestion = new Weather(location, language);
				WaitMessageThread.stopThread();
			} catch (IOException e) {
				WaitMessageThread.stopThread();
				checkNet();
			} catch (ErrorMessageException e) {
				WaitMessageThread.stopThread();
				excReac(Weather.errorManager.getStatus_code());
				break;
			}
		}
		//3.��������
		if(askLifeSuggestion!=null)
			//����ѯδ����
			lifeSuggestionReaction(askLifeSuggestion);
		
		
	}
	
	/**
	 * ������
	 * ��������֧�Ĵ���
	 * 
	 * @throws ErrorMessageException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void server()  {
		//֮��ĳɽ�����
		if(userData.isLogin()) {
			WriteTimer.println("�٣�"+userData.getUserNickName()+"��");			
		}
		else {
			WriteTimer.println("�٣����࣡");
		}
		while(true) {
			//����ǵ�һ��ʹ��
			if(ChatGUI.wheatherFirst) {
				ChatGUI.wheatherFirst=false;
				//1.�׳�����
				WriteTimer.println("�������кι�ɣ�");
				//2.��ʾ��ť
				WriteTimer.paintButton("1��һ�½��������");
				WriteTimer.paintButton("2����֪�����������");
				WriteTimer.paintButton("3�ҽ��졭��");
			}
			//����
			else {
				//1.�׳�����
				if(robotGoToWork)
					WriteTimer.println("�Ҹղ�ȥ�����ˣ�������ʲô����");
				else WriteTimer.println("����֪��ʲô��");
				//2.��ʾ��ť
				WriteTimer.paintButton("1��һ�½��������");
				WriteTimer.paintButton("2����֪�����������");
				WriteTimer.paintButton("3�ҽ��졭��");
				WriteTimer.paintButton("4�ݰݣ�");
			}
			//3.����ѡ��
			waitForIndex();
			try {
				switch(answer) {
				case 1:
					WriteTimer.println("�ޣ��õģ��������ˣ�");
					offerNowWeather();
					break;
				case 2:
					WriteTimer.println("�ޣ��õģ��������ˣ�");
					offerDailyWeather();
					break;
				case 3:
					WriteTimer.println("�������ô�ˣ�");
					WriteTimer.println("�ޣ���֪���ˣ���һ���������Ҹ���������Ľ���ɣ�");
					WriteTimer.println("�ٺ١�");
					offerLifeSuggestion();
					break;
				case 4:
					WriteTimer.println("��ô�ټ�����");
					Thread.sleep(1500);
					robotGoToWork = true;
					synchronized(mainO) {
						System.out.println("������ȥ������");
						mainO.wait();
					}
				}
			} catch (EncryptedDocumentException | InvalidFormatException | InterruptedException e) {
				e.printStackTrace();
			}	
		}
		
	}
	
	/**
	 * ��������
	 * 
	 * @throws IOException
	 * @throws ErrorMessageException
	 * @throws InterruptedException
	 * @throws EncryptedDocumentException
	 * @throws InvalidFormatException
	 * */
	public static void main(String[] args) throws ErrorMessageException, InterruptedException, EncryptedDocumentException, InvalidFormatException{
		try {
			//������뷨�л����µİ�������
			System.setProperty("sun.java2d.noddraw", "true");
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
	           public void run() {
	        	   Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						Demo start = new Demo();
					}
	        		   
	        	   });
	        	   t.start();
	           }
	       });
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * �������̣߳����ϰѻ����е���Ϣ�¼����</br>
	 * �����ж��������¼���</br>
	 * �������¼���</br>
	 * <ul>
	 * <li>������˵�Ļ�</li>
	 * <li>������ť</li>
	 * <li>���������</li>
	 * </ul>
	 * */
	public static class RobotThread extends Thread{
		static public boolean isRun = true;
		static public ArrayList<MessageBox> robotMessageBuffer = new ArrayList<>();
		static int pos = 0;

		@Override
		public void run() {
			while(isRun) {
				try {
					sleep(2);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(turn%2!=0) {
//					System.out.println("�ֵ�������");
					int originalSsize = robotMessageBuffer.size();
					boolean f = false;
					while(pos < robotMessageBuffer.size() && isRun) {
						if(f && originalSsize!=robotMessageBuffer.size())
							break;
						else if(pos<robotMessageBuffer.size() && robotMessageBuffer.size()>0) {
							f = true;
							MessageBox m = robotMessageBuffer.get(pos);
	
							if(m.getType()==MessageBox.BUTTON) {
//								ChatGUI.setQuesOrText(ChatGUI.QUESTIONBOX);
								ChatGUI.addQuestion(m.getWord());
								pos++;
								continue;
							}
							if(m.getType()==MessageBox.TEXTAREA) {
								ChatGUI.textareapanel.setVisible(true);
//								ChatGUI.setQuesOrText(ChatGUI.TEXTFIELD);
								ChatGUI.textField.requestFocus();
							}
							if(m.getType()==MessageBox.HIDE_TEXTAREA) {
								ChatGUI.textareapanel.setVisible(false);
//								ChatGUI.setQuesOrText(ChatGUI.NONE);
							}
							if(m.getType()==MessageBox.HIDE_BUTTON) {
								ChatGUI.questionBox.setVisible(false);
//								ChatGUI.setQuesOrText(ChatGUI.NONE);
							}
							if(m.getType()==MessageBox.ROBOT_BUBBLE) {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								ChatGUI.addBubble(m.getWord(), ChatGUI.ROBOT_SAY);
							}
							playSounds();
						}
						pos++;
					}
					if(waitAnswer && answer!=-1) {
						waitAnswer = false;
						synchronized(answerO) {
							answerO.notify();
							System.out.println("�õ���ť");
						}
					}
					if(waitUserText && userText!=null) {
						waitUserText = false;
						synchronized(userTextO) {
							userTextO.notify();
							System.out.println("�õ�����");
						}
					}
					synchronized(userO) {
//						System.out.println("�����û�");
						userO.notifyAll();
					}
					turn++;					
					synchronized(robotO) {
						try {
//							System.out.println("�����˵�");
							robotO.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				else continue;
			}
		}
		
	}
	
	/**
	 * �û��̣߳����ϰѻ����е���Ϣ�¼����</br>
	 * �����ж��������¼���</br>
	 * �û��¼���</br>
	 * <ul>
	 * <li>�û�˵�Ļ�</li>
	 * </ul>
	 * ע�⣺</br>
	 * <ul>
	 * <li>�����ť�����û�buffer������һ����Ϣ�¼�</li>
	 * <li>�ı�����Ч����󰴻س������û�buffer������һ����Ϣ�¼�</li>
	 * </ul>
	 * */
	public static class UserThread extends Thread{
		static public boolean isRun = true;
		static public ArrayList<MessageBox> userMessageBuffer = new ArrayList<>();
		static int pos = 0;
		@Override
		public void run() {
			while(isRun) {
				try {
					sleep(2);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(turn%2==0) {
//					System.out.println("�ֵ��û�");
					int originalSsize = userMessageBuffer.size();
					boolean f = false;
					while(pos<userMessageBuffer.size() && isRun) {
//						System.out.println("�û�");
//						if(f && originalSsize!=userMessageBuffer.size())
//							break;
//						else
							if(pos<userMessageBuffer.size() && userMessageBuffer.size()>0) {
							MessageBox m = userMessageBuffer.get(pos);
							if(m.getType() == MessageBox.USER_BUBBLE)
								ChatGUI.addBubble(m.getWord(), ChatGUI.USER_SAY);	
						}
						playSounds();
						pos++;
					}
					if(waitAnswer && answer!=-1) {
						waitAnswer = false;
						synchronized(answerO) {
							answerO.notify();
							System.out.println("�õ���ť");
						}
					}
					if(waitUserText && userText!=null) {
						waitUserText = false;
						synchronized(userTextO) {
							userTextO.notify();
							System.out.println("�õ�����");
						}
					}
					synchronized(robotO) {
//						System.out.println("���ѻ�����");
						robotO.notifyAll();
					}
					turn++;
					synchronized(userO) {
						try {
//							System.out.println("�û���");
							userO.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else continue;
			}
		}
	}
	public static void playSounds() {
//		System.out.println(Demo.class.getResource("/").getPath());
		if(Setting.sounds) {
			new SoundPlayer().play();
		}
	}
}