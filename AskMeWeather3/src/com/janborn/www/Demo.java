/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: Demo.java
* 主调类。
*
* @author Deng Yang
    * @Date    2018/4/17
* @version 2.3
* <ul>
* 	<li>2.0 实装了新的用户界面</li>
* 	<li>2.1 实装了新的分词插件</li>
* 	<li>2.2 实装了新的用户组件（登录等）</li>
* 	<li>2.3 解决了一下序列化问题、实装用户记录部分</li>
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

	/*地点*/
	public static String location=null;
	/*语言*/
	//暂时缺省
	private static String language=null;
	/*气温单位*/
	//暂时缺省
	private static String unit=null;
	/*用于逐日天气查询的时间*/
	private static boolean dailyLife[] = new boolean[4];
	//暂时缺省
	/*用于存储生活指数需求的数组*/
	private static boolean lifeSuggestion[]=new boolean[6];
	/*用于存储天气详细信息需求的数组*/
	private static boolean lifeDetil[]=new boolean[4];
	
	/*用于记录输入信息*/
	public static String userText=null;
	public static int answer = -1;
	
	/*记录是否已经下雨，以纠正降雨率*/
	public static boolean wheatherRain = false;
	
	/*网络检查标记，用于改善回答*/
	public static boolean netanswer = false;
	
	/*机器人是否离开*/
	public static boolean robotGoToWork = false;
	
	/*机器人线程*/
	public static RobotThread robotThread = new RobotThread();
	public static Object robotO = new Object();
	/*主线程阻塞前执行机器人线程：true*/
	public static boolean robotStop = false;
	/*用户线程*/
	public static UserThread userThread = new UserThread();
	public static Object userO = new Object();
	
	public static boolean waitAnswer = false;
	public static Object answerO = new Object();
	public static boolean waitUserText = false;
	public static Object userTextO = new Object();
	public static Object mainO = new Object();
	
	/*数据库*/
	private static UserData userData = new UserData(); 
	
	public static int turn = 1;
	
	/*用户数据*/
	User user=null;
	
	public static UserData getUserData() {
		return userData;
	}
	
	/**
	 * 构造器，用于初始化用户
	 * 
	 * @param none
	 * @return none
	 * @throws IOException 
	 */
	public Demo() {
//		initUser();
		//欢迎界面
 	   WelcomeGUI welcomeWIN = new WelcomeGUI();
 	   welcomeWIN.setVisible(true);
 	   try {
 		   Thread.sleep(3000);
 	   } catch (InterruptedException e) {
				e.printStackTrace();
 	   }
 	   welcomeWIN.setVisible(false);
 	   //开启主界面
 	   Thread t = new Thread(new Runnable() {

		@Override
		public void run() {
			MainGUI mainWIN = new MainGUI();
	 		mainWIN.setVisible(true);
	 		//开启主窗口服务
	 		mainServe();
		}
 		    		   
 	   });
 	   t.start();
 	   
	}
	
	public static void mainServe() {
		//打招呼
		mainGreeting();
		//检查网络
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
		//进行天气请求
		offerMainNowWeather();
	}
	
	public static void offerMainNowWeather() {
		Weather askWeather = null;
		try {
			askWeather = new Weather("ip",language,unit);
		} catch (IOException e) {
			//网络异常
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
			MainGUI.wordLabel2.setText("现在"+text+"，");
			MainGUI.wordLabel3.setText("大概"+temperature+"℃的样子。");
		}
		MainGUI.change2MoreLabel();
	}
	
	public static void mainGreeting() {
		//1.时间问候语
		//获取时间以判断现在是早上、中午还是晚上
		Calendar now = Calendar.getInstance();
		int time=now.get(Calendar.HOUR_OF_DAY);
		//清晨：05：01-06：59 
		//早上：07：01-08：59 
		//上午：09：00-12：00 
		//中午：12：01-13：59 
		//下午：14：00-17：59 
		//傍晚：18：00-18：59 
		//晚上：19：00-23：59 
		//凌晨：24：00-05：00
		int code = 1;
//		if(time>=5 && time<7)
//			code = 0;
//			WriteTimer.println("现在是清晨");
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
//			WriteTimer.println("现在是傍晚时分，来杯咖啡吧！");
//		if(time>=19 && time<=21)
//			code = 6;
//			WriteTimer.println("晚上好！");
		if(time>18 && time<=23 || time==0)
			code = 7;
//			WriteTimer.println("晚上好！");
//		if(time>0 && time<5)
//			code = 8;
//			WriteTimer.println("时间还早，不妨摊开一本书，细品一杯早茶以待日出吧！");
		String greet = null;
		switch(code) {
		case 1:
			greet = "早上好";
			break;
		case 2:
			greet = "上午好";
			break;
		case 3:
			greet = "中午好";
			break;
		case 4:
			greet = "下午好";
			break;
		case 7:
			greet = "晚上好";
			break;
		}
		Demo.playSounds();
		MainGUI.wordLabel1.setText(greet+"，");
		MainGUI.wordLabel2.setText("我是机器人小智");
		MainGUI.wordLabel3.setText("在云端观察天气。");
	}
	
	/**
	 * 用户初始化模块
	 * 
	 * @param none
	 * @return none
	 * @throws IOException 
	 */
	public void initUser() {
		user= new User();
	}
	
	/**
	 * 应用初始化模块</br>
	 * 1.打招呼</br>
	 * 2.询问名字</br>
	 * 3.判断网络连接是否正常：保证网络正常后再返回。
	 * 
	 * @param User user 用户信息
	 * @return boolean
	 * @throws IOException 
	 */
//	public boolean initMain(User user) {
//		//打招呼、询问名字
//		greeting(user);
//		//检查网络状况
//		WriteTimer.println("稍等，我帮你检查一下网络啊……");
//		netanswer = false;
//		if(checkNet())
//			return true;
//		else return false;
//	}
	
	/**
	 * 检查网络
	 * <ul>
	 * <li>如果有网络，返回true</li>
	 * <li>否则，询问用户是否还要继续查询。如果继续，那么机器人在有网络时自动唤醒；否则直接退出程序</li>
	 * 
	 * @param none
	 * @return boolean
	 * @throws IOException 
	 */
	public static boolean checkNet() {
		NetCheckingThread netThread = new NetCheckingThread();
		netThread.start();
		/*是否第一次检查*/
		boolean f = true;
		/*是否是重新连上的*/
		boolean reCon = false;
		
		while(!isConnect("https://www.seniverse.com/") 
				&& !isConnect("https://login.bce.baidu.com/")) {
			if(f==true) {
				f=false;
				WriteTimer.println("我连不上你们人类的网络了！也许你应该给我块饼干……");
				WriteTimer.println("哈哈，逗你的，我该回去工作啦，需要的时候再叫我。");
				
				ChatGUI.clearQuestionBox();
				ChatGUI.addQuestion("1好。");
				ChatGUI.addQuestion("2不用了，不打扰你工作了。");

				
//				waitForIndex();
				
				//扩展：没有网络时的操作
				if(ChatGUI.questionNO == 1) {
					WriteTimer.println("嗯，当我连上网络的时候我会回来叫你的。");
					WriteTimer.println("一会儿见啦。");
					reCon = true;
					continue;
					//这之后如果收到重新查询的呼唤，再检查网络，在此之前等待
				}
				else {
					WriteTimer.println("那再见啦。");
				}
			}
			
		}
		if (isConnect("https://www.seniverse.com/") 
				&& isConnect("https://login.bce.baidu.com/")
				&& !netanswer) {
			WriteTimer.println("很好！网络状联通！");
			if(reCon) WriteTimer.println("嘿！人类！网路重新连上了，你还在吗？");
			netanswer=true;
		}
		return true;
	}
	
	/**
	 * 检查网络连接
	 * <p>使用IO流打开网页连接。如果能打开说明网络连接正常，返回true；如果抛出异常，则网络连接失败，返回false</p>
	 * 
	 * @param String net 要请求访问的网址
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
		//获取时间以判断现在是早上、中午还是晚上
			Calendar now = Calendar.getInstance();
			int time=now.get(Calendar.HOUR_OF_DAY);
			String greeting = null;
			//清晨：05：01-06：59 
			//早上：07：01-08：59 
			//上午：09：00-12：00 
			//中午：12：01-13：59 
			//下午：14：00-17：59 
			//傍晚：18：00-18：59 
			//晚上：19：00-23：59 
			//凌晨：24：00-05：00
			if(time>=5 && time<7)
				greeting = "现在是清晨";
			if(time>=7 && time<9)
				greeting = "早上好";
			if(time>=9 && time<12)
				greeting = "上午好";
			if(time>=12 && time<14)
				greeting = "中午好";
			if(time>=14 && time<18)
				greeting = "下午好";
			if(time>=18 && time<19)
				greeting = "你好";
			if(time>=19 && time<=21)
				greeting = "晚上好";
			if(time>21 && time<=23 || time==0)
				greeting = "晚上好";
			if(time>0 && time<5)
				greeting = "你好";
			return greeting;
	}
	
	/**
	 * 和用户打招呼
	 * <ol>
	 * <li>首先送上一句时间问候</li>
	 * <li>问好、自我介绍</li>
	 * <li>登记用户信息</li>
	 * </ol>
	 * <p>收集用户登录信息的最初目的是记录用户的ip地址，但由于时间关系，这个功能目前已经弃用了。</p>
	 * <p>另外，用户信息将序列化后存在<i>userData</i>用户目录下</p>
	 * 
	 * @param user 用户对象
	 * @return none
	 * @throws IOException 
	 */
//	public void greeting(User user) {
//		//2.打招呼
//		WriteTimer.println("你好呀，我是机器人小智，正在云端观测天象。");
//		WriteTimer.println("哦！让我来找找啊……");
//		WriteTimer.println("那边那个黑乎乎的小点一定就是你吧！");
//		//3.询问名字
//		//判断是否是新客户
//		//如果曾经登录过
//		if(user.getUserFile().length()!=0) {
//			WriteTimer.println("我们或许不是第一次聊天……");
//			WriteTimer.println("我记得你叫"+user.getName()+"对吗？");
//			
//			//收集用户回答
//			//创建按钮
//			MainWindows.createButton("是的", true, 1);
//			MainWindows.createButton("我不叫这个名字", false, 2);
//			//此处可以改为用户输入
//			waitForIndex();
//			
//			if(answer.equals("1"))
//				WriteTimer.println("嘿嘿，你好呀，人类！");
//			else {
//				WriteTimer.println("好奇怪啊……我的芯片出错了吗？");
//				WriteTimer.println("能重新把名字告诉我一下吗？");
//				WriteTimer.println("等等，如果你想更新名字的话，过去的记录数据我就用来填饱肚子了哦。");
//				WriteTimer.println("我可不会吐出来还给你的。");
//				
//				MainWindows.createButton("嗯", true, 1);
//				MainWindows.createButton("再考虑一下", false, 2);
//				waitForIndex();
//				
//				System.out.println(answer);
//				if(answer.equals("1")) {
//					if(user.deleteData()) {
//						WriteTimer.println("日志：过去记录已删除。");
//						initUser();
//						WriteTimer.println("好，现在你可以把新名字告诉我了。");
//						userText=null;
//						while(userText==null || userText.length()==0) {
//							waitForLine();
//							if(userText==null || userText.length()==0)
//								WriteTimer.println("你不能什么都不输入！这样不行！");
//						}
//						user.setName(userText);
//						WriteTimer.println("哦!"+user.getName()+"你好呀！");
//						//序列化
//						if(user.serializeUser(user))
//							WriteTimer.println("你的名字很好记，我已经记住了哦！");
//						else
//							WriteTimer.println("记录：记忆芯片出错。该用户记录丢失。");
//					}
//					else
//						WriteTimer.println("日志：删除失败。");
//				}	
//			}
//		}
//		else {
//			WriteTimer.println("你叫什么？……我知道你一定不叫罗伯特……");
//			WriteTimer.println("你可以像这样输入：“张三”。请直接把名字告诉我，我可不知道你们人类名字的法则。");
//			
//			userText=null;
//			waitForLine();
//			while(userText==null || userText.length()==0) {
//				WriteTimer.println("你的名字不能为空！");
//				waitForLine();
//			}
//			user.setName(userText);
//			
//			
//			//4.给出回应
//			WriteTimer.println("哦!"+user.getName()+"你好呀！");
//			//序列化
//			if(user.serializeUser(user))
//				WriteTimer.println("你的名字很好记，我已经记住了哦！");
//			else
//				WriteTimer.println("记录：记忆芯片出错。该用户记录丢失。");
//		}
//	}
	
	/**
	 * 等待读入用户输入信息
	 * <p>用于读入输入框中用户的输入信息。直到读到用户的输入信息或者触发事件才会退出该方法。</p>
	 * 
	 * @param none
	 * @return none
	 */
	public static void waitForLine() {
		userText=null;
		WriteTimer.paintTextArea();	
		waitUserText = true;
		synchronized(userTextO) {
			System.out.println("等待输入");
			try {
				userTextO.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 等待读入用户按钮信息
	 * <p>用于读取按下的按钮信息。直到读到用户的按钮信息才会退出该方法。</p>
	 * 
	 * @param none
	 * @return none
	 */
	public static void waitForIndex() {
		answer = -1;
		waitAnswer = true;
		synchronized(answerO) {
			System.out.println("等待点击");
			try {
				answerO.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	

	
	/**
	 * 分析提取查询参数（实况天气）
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void analyzeNowWeather() throws InterruptedException, EncryptedDocumentException, InvalidFormatException, IOException {		
		//1.询问地点
		WriteTimer.println("对了，我应该帮你查哪的天气？");
		String loc;
		//是否查询上一次地点
		boolean preF = false;
		if((loc = userData.getUserPreLoc())!=null) {
			WriteTimer.println("还是查"+loc+"的吗？");
			//2.读入用户输入
			waitForLine();
			MyNLP agreeNlp = new MyNLP();
			if(agreeNlp.agreeNlp(userText)) {
				WriteTimer.println("好的！");
				preF = true;
			}
			else 
				WriteTimer.println("那想查哪的呢？");
		}
		if(!preF) {
			while(true) {
				waitForLine();
				
				//3.实例化Nlp对象，分析地点
				Letter l = new Letter(userText,"LOC");
				MyNLP locationNlp = new MyNLP(l);
				location = locationNlp.getLocaction();
				netanswer = false;
				
				//扩展功能增加处。判断一下是否是新增地点。
				//5.通过查询Excel表格，判断是否是支持的地点
				if(location==null) {
					WriteTimer.println("你说的这个地方我好像找不到啊");
					WriteTimer.println("麻烦换一个");
				}
				else {
					try {
						int f = MyNLP.judgeLocation(location);
						if(f==1 || f==2) {
							//如果支持
							if(f==1) {
								//判断是否曾经查过
								//若历史记录存在且曾经查询过
								if(userData.whetherSql(location)) {
									WriteTimer.println(location+"？这个地方我知道！你之前问过我！");
									//刷新历史记录
									userData.refreshUserLocQueue();
								}
								else {
									WriteTimer.println(location+"？我又知道了一个人类世界的地方！");
									//将地点加入历史记录
									userData.addUserLocQueue(location);
								}
								//设置历史记录
								userData.setPreLocation(location);
								break;
							}
							else if(f==2) {
								WriteTimer.println("不够具体啊。要不我直接帮你查省会"+location+"的吧？");
								//画选项按钮
								WriteTimer.paintButton("1查省会的吧");
								WriteTimer.paintButton("2换个地方吧");
		
								waitForIndex();
		
								if(answer==1) {
									if(userData.whetherSql(location)) {
										//刷新历史记录
										userData.refreshUserLocQueue();
									}
									else {
										userData.addUserLocQueue(location);
									}
									//设置历史记录
									userData.setPreLocation(location);
									WriteTimer.println("好的，我这就帮你查！");
									break;
								}
								else {
									WriteTimer.println("换成什么地方？");
									continue;											
								}
							}
							break;
						}
						else {
							WriteTimer.println("日志：地点不支持。");
							WriteTimer.println("噢。出现了非常抱歉的问题。");
							WriteTimer.println("要不要换一个地方？");
							
							//画选项按钮
							WriteTimer.paintButton("1换一个");
							WriteTimer.paintButton("2算了吧");
							
							waitForIndex();
							if(answer==1) {
								WriteTimer.println("要换成什么地方？");
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
	 * 分析提取查询参数（逐日天气）
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void analyzeDailyWeather() throws InterruptedException, EncryptedDocumentException, InvalidFormatException, IOException {	
		//1.询问地点
		WriteTimer.println("对了，你还没告诉我要查询的地点？");
		String loc;
		//是否查询上一次地点
		boolean preF = false;
		if((loc = userData.getUserPreLoc())!=null) {
			WriteTimer.println("还是查"+loc+"的吗？");
			//2.读入用户输入
			waitForLine();
			MyNLP agreeNlp = new MyNLP();
			if(agreeNlp.agreeNlp(userText)) {
				WriteTimer.println("好的！");
				preF = true;
			}
			else 
				WriteTimer.println("那想查哪的呢？");
		}
		//若不是查上一次查过的地方
		if(!preF) {
			while(true) {
				//2.读入用户输入
				waitForLine();
				
				//3.实例化Nlp对象，分析地点
				Letter l = new Letter(userText,"LOC");
				MyNLP locationNlp = new MyNLP(l);
				location = locationNlp.getLocaction();
				netanswer = false;
				
				if(location==null) {
					WriteTimer.println("你说的这个地方我好像找不到啊");
					WriteTimer.println("麻烦换一个");
				}
				else {
					//分析是否是支持的地点
					int f = MyNLP.judgeLocation(location);
					if(f==1 || f==2) {
						//如果支持
						if(f==1) {
							//判断是否曾经查过
							//若历史记录存在且曾经查询过
							if(userData.whetherSql(location)) {
								WriteTimer.println(location+"？这个地方我知道！你之前问过我！");
								//刷新历史记录
								userData.refreshUserLocQueue();
							}
							else {
								WriteTimer.println(location+"？我又知道了一个人类世界的地方！");
								//将地点加入历史记录
								userData.addUserLocQueue(location);
							}
							//设置历史记录
							userData.setPreLocation(location);
							break;
						}
						else if(f==2) {
							WriteTimer.println("不够具体啊。要不我直接帮你查省会"+location+"的吧？");
							//画选项按钮
							WriteTimer.paintButton("1查省会的");
							WriteTimer.paintButton("2换个地方吧");
							
							waitForIndex();
							
							if(answer==1) {
								if(userData.whetherSql(location)) {
									//刷新历史记录
									userData.refreshUserLocQueue();
								}
								else {
									userData.addUserLocQueue(location);
								}
								//设置历史记录
								userData.setPreLocation(location);
								WriteTimer.println("好的，我这就帮你查！");
								break;
							}
							else {
								WriteTimer.println("换成什么地方？");
								continue;
							}
						}
					}
					else {
						WriteTimer.println("日志：地点不支持。");
						WriteTimer.println("噢。出现了非常抱歉的问题。");
						WriteTimer.println("要不要换一个地方？");
						
						//画选项按钮
						WriteTimer.paintButton("1换一个");
						WriteTimer.paintButton("2算了吧");
						
						waitForIndex();
						
						if(answer==1) {
							WriteTimer.println("要换成什么地方？");
							continue;
						}
						else return;
					}
				}
			}
		}
		
		//5.询问要查询的日期
		WriteTimer.println("嘿嘿嘿。");
		WriteTimer.println("忘了告诉你，凭借我的能力，只能帮你最近三天的天气。");
		WriteTimer.println("选一天吧？");
		
		//6.接收日期
		int times = 0;
		while(true) {
			times++;
			waitForLine();
			
			//7.分析
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
				//超出范围
				case -1:
					WriteTimer.println("我查不到这天的，超出了我的能力范围");
					WriteTimer.println("麻烦换一天吧");
					break;
				//请求中包含多个时间
				case -2:
					if(times==1)
						WriteTimer.println("抱歉啦，如果同时查好几天的数据我会精神分裂的，咱们一天一天来");
					if(times>10)
						WriteTimer.println("抱歉，你真的理解我的意思吗？只能从今天、明天、后天中选一天！");
					else if(times>9) {
						WriteTimer.println("选一天……");
						WriteTimer.println("你知道我有多绝望吗= =。");
						WriteTimer.println("你莫不是在调戏我。");
					}
					else if(times>8) {
						WriteTimer.println("我真的不是故意要生气的！我是一个绅士！");
						WriteTimer.println("但我现在甚至想把我的机器脑袋送给你！");
						WriteTimer.println("再说一遍，只能从今天、明天、后天中选一天！");
					}
					else if(times>5){
						WriteTimer.println("我……我都佩服你了");
						WriteTimer.println("我再说一遍，只能从我刚才说的那几天里面选一个！");
					}
					
					else if(times>2) {
						WriteTimer.println("我……");
						WriteTimer.println("最近三天是指今天、明天和后天！");
					}
					break;
				}				
			}
		}	
	}
	
	/**
	 * 分析提取查询参数（生活指数）
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void analyzeLifeSuggestion() throws InterruptedException, EncryptedDocumentException, InvalidFormatException, IOException {
		//1.询问地点
		WriteTimer.println("你可以先选择一个地点");
		String loc;
		//是否查询上一次地点
		boolean preF = false;
		if((loc = userData.getUserPreLoc())!=null) {
			WriteTimer.println("还是查"+loc+"的吗？");
			//2.读入用户输入
			waitForLine();
			MyNLP agreeNlp = new MyNLP();
			if(agreeNlp.agreeNlp(userText)) {
				WriteTimer.println("好的！");
				preF = true;
			}
			else 
				WriteTimer.println("那想查哪的呢？");
		}
		if(!preF) {
			while(true) {
				//2.读入用户输入
				waitForLine();
				
				//3.实例化Nlp对象，分析地点
				Letter l = new Letter(userText,"LOC");
				MyNLP locationNlp = new MyNLP(l);
				location = locationNlp.getLocaction();
				netanswer = false;
				
				//判断是否支持
				if(location==null) {
					WriteTimer.println("你说的这个地方我好像找不到啊");
					WriteTimer.println("麻烦换一个");
				}
				else {
					int f = MyNLP.judgeLocation(location);
					if(f==1 || f==2) {
						//如果支持
						if(f==1) {
							//判断是否曾经查过
							//若历史记录存在且曾经查询过
							if(userData.whetherSql(location)) {
								WriteTimer.println(location+"？这个地方我知道！你之前问过我！");
								//刷新历史记录
								userData.refreshUserLocQueue();
							}
							else {
								WriteTimer.println(location+"？我又知道了一个人类世界的地方！");
								//将地点加入历史记录
								userData.addUserLocQueue(location);
							}
							//设置历史记录
							userData.setPreLocation(location);
							break;
						}
						else if(f==2) {
							WriteTimer.println("不够具体啊。要不我直接帮你查省会"+location+"的吧？");
							//画选项按钮
							WriteTimer.paintButton("1查省会的");
							WriteTimer.paintButton("2换个地方吧");
							
							waitForIndex();
							
							if(answer==1) {
									if(userData.whetherSql(location)) {
										//刷新历史记录
										userData.refreshUserLocQueue();
									}
									else {
										userData.addUserLocQueue(location);
									}
									//设置历史记录
									userData.setPreLocation(location);
									WriteTimer.println("好的，我这就帮你查！");
								break;
							}
							else {
								WriteTimer.println("换成什么地方？");
								continue;
							}
						}
					}
					else {
						WriteTimer.println("日志：地点不支持。");
						WriteTimer.println("噢。出现了非常抱歉的问题。");
						WriteTimer.println("要不要换一个地方？");
						
						//画选项按钮
						WriteTimer.paintButton("1换一个");
						WriteTimer.paintButton("2算了吧");
						
						waitForIndex();
						
						if(ChatGUI.questionNO == 1) {
							WriteTimer.println("要换成什么地方？");
							continue;
						}
						else return;
					}
				}
			}
		}
		
		Arrays.fill(lifeSuggestion, false);

		//提示
		WriteTimer.println("你想知道什么信息呢?");
		WriteTimer.println("我现在能帮你的很有限,只能为你下面几方面的信息和建议哦");
		WriteTimer.println("穿衣、洗车、流感、运动、旅行、紫外线强度");
		
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
					WriteTimer.println("我……");
					WriteTimer.println("你认真点好不好，只能从我刚才说的选项里面选一个！");
				}
				else if(times>5){
					WriteTimer.println("我……我都佩服你了");
					WriteTimer.println("我再说一遍，只能从我刚才说的选项里面选一个！");
				}
				else if(times>8) {
					WriteTimer.println("我真的不是故意要生气的！我是一个绅士！");
					WriteTimer.println("但我现在甚至想把我的机器脑袋送给你！");
					WriteTimer.println("我再说一遍，只能从我刚才说的选项里面选一个！");
				}
				else
					WriteTimer.println("抱歉，你真的理解我的意思吗？只能从我刚才说的选项里面选一个！");				
			}
		}
	}
	
	/**
	 * 分析提取查询参数（用户详情）
	 * 
	 * <p>分析并设置用户想要查找的项目</p>
	 * */
	public static void analyzeLifeDetil() {
		Arrays.fill(lifeDetil, false);
		//提示
		WriteTimer.println("我现在能帮你的很有限,只能只能问我下面几个，不许超纲！");
		WriteTimer.println("温差、晚上的天气、降雨率");
		WriteTimer.println("或者说你想一口气知道全部的信息？");
		
		int times=0;
		while(true) {
			times++;
			//用户输入生活信息
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
					WriteTimer.println("我……");
					WriteTimer.println("你认真点好不好，只能从我刚才说的选项里面选一个！");
				}
				else if(times>5){
					WriteTimer.println("我……我都佩服你了");
					WriteTimer.println("我再说一遍，只能从我刚才说的选项里面选一个！");
				}
				else if(times>8) {
					WriteTimer.println("我真的不是故意要生气的！我是一个绅士！");
					WriteTimer.println("但我现在甚至想把我的机器脑袋送给你！");
					WriteTimer.println("我再说一遍，只能从我刚才说的选项里面选一个！");
				}
				else
					WriteTimer.println("抱歉，你真的理解我的意思吗？只能从我刚才说的选项里面选一个！");				
			}
		}
	}
	
	/**
	 * 提供服务（详细天气信息）
	 * 
	 * <p>设置用户想要查找的项目</p>
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
			if(start.equals("0")) day="今天";
			else if(start.equals("1")) day="明天";
			else if(start.equals("2")) day="后天";
			
			if(lifeDetil[0]) {
				//概况
				WriteTimer.println(day+"晚上"+dailyAsk.getText_night()+","
						+dailyAsk.getLow()+"到"+dailyAsk.getHigh()+"摄氏度。");
				
				//温差
				WriteTimer.println("昼夜温差"+hl+"摄氏度。");
				if(hl>=10) WriteTimer.println("昼夜温差有点大，记得增减衣服啊！");
				//降雨率
				if(dailyAsk.getPrecip()!=null && !dailyAsk.getPrecip().isEmpty()) {
					if(!wheatherRain) {
						WriteTimer.println("另外，"+day+"有"+dailyAsk.getPrecip()+"%的可能会降雨");
						if(dailyAsk.getPrecip().length()==2 && dailyAsk.getPrecip().charAt(0)>='6')
							WriteTimer.println("记得带好雨伞啊，我可不会给你送去的！");
					}
				}
				else {
					WriteTimer.println("降雨率……对不起，我没查到。");
					WriteTimer.println("大概不会下雨吧，嘿嘿。");
				}
			}
			else {
				if(lifeDetil[1]) {
					//概况
					WriteTimer.println(day+dailyAsk.getLow()+"到"+dailyAsk.getHigh()+"摄氏度。");
					//温差
					WriteTimer.println("昼夜温差"+hl+"摄氏度。");
					if(hl>=10) WriteTimer.println("昼夜温差有点大，记得增减衣服啊！");
				}
				else if(lifeDetil[2]) {
					WriteTimer.println(day+"晚上是"+dailyAsk.getText_night()+"天！");
				}
				else if(lifeDetil[3]) {
					if(dailyAsk.getPrecip()!=null && !dailyAsk.getPrecip().isEmpty()) {
						if(!wheatherRain) {
							WriteTimer.println("另外，"+day+"有"+dailyAsk.getPrecip()+"%的可能会降雨");
							if(dailyAsk.getPrecip().length()==2 && dailyAsk.getPrecip().charAt(0)>='6')
								WriteTimer.println("记得带好雨伞啊，我可不会给你送去的！");
						}
					}
					else {
						WriteTimer.println("降雨率……对不起，我没查到。");
						WriteTimer.println("大概不会下雨吧，嘿嘿。");
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
	 * 提供服务（实况天气）
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void offerNowWeather() throws InterruptedException, EncryptedDocumentException, InvalidFormatException {
		try {
			//1.设置相关参数
			netanswer = false;
			try {
				analyzeNowWeather();
			} catch (IOException e1) {
				checkNet();
			}
			//2.实例化天气查询对象
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
			//3.给出反馈
			WriteTimer.println("我回来啦！查询完毕！");
			WriteTimer.println("今天"+location+askWheather.now.getText()+"，现在差不多"
					+askWheather.now.getTemperature()+"度的样子吧");
			if(askWheather.now.getText().indexOf('雨')!=-1)
				wheatherRain = true;
			//4.吐槽补充处（如对气温，还有一些关心）
			//这里！
			//询问是否还需要查询别的信息，如果要，就用逐日天气工具去查“今天”的信息
			WriteTimer.println("怎么样?我还能告诉你别的信息哦！");
			WriteTimer.println("比如说降雨的概率之类的");
			WriteTimer.println("想知道吗？");

			waitForLine();
			MyNLP agreeNlp = new MyNLP();
			answer = -1;
			if(agreeNlp.agreeNlp(userText))
				answer = 1;
			
			if(answer == 1)
				//1.是
				offerDetilLife("0","1");
			else
				//2.否
				WriteTimer.println("那好吧。");
			
		}
		catch(ErrorMessageException e) {
			excReac(Weather.errorManager.getStatus_code());
		}
	}
	
	/**
	 * 回应函数：将逐日天气的查询结果回应给用户
	 * 
	 * @param Weather askWeather 天气信息查询结果
	 * @param int day 要查询的日子
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
			WriteTimer.println("我回来啦！查询完毕！");
			switch(day) {
			case 0:
				WriteTimer.println("今日");
				break;
			case 1:
				WriteTimer.println("明日");
				break;
			case 2:
				WriteTimer.println("后天");
				break;
			}
			WriteTimer.println("白天"+askWeather.daily.getDailyAsk().getText_day()+"，"
								+"晚间"+askWeather.daily.getDailyAsk().getText_night()+"，"
								+askWeather.daily.getDailyAsk().getLow()+"到"+askWeather.daily.getDailyAsk().getHigh()+"度");
		}
		//附加属性添加处	
		//这里！
		WriteTimer.println("怎么样？还想知道点别的天气情况不。");
		
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
		//2.否
			WriteTimer.println("那好吧。");
		//询问还需要查询什么信息并给出回复
	}
	
	/**
	 * 提供服务（逐日天气）
	 * @throws ErrorMessageException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void offerDailyWeather() throws InterruptedException, EncryptedDocumentException, InvalidFormatException {
		try {
			//1.设置相关参数
			netanswer = false;
			try {
				analyzeDailyWeather();
			} catch (IOException e1) {
				checkNet();
			}
			
			//2.实例化天气查询对象
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
					//3.给出反馈
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
	 * 用于处理心知天气错误代码的方法
	 * 
	 * @param String e 错误代码
	 * */
	public static void excReac(String e) {
		switch(e) {
			case "AP010006":
				WriteTimer.println("对不起，我没有查询这个地点的权限。");
				break;
			case "AP010010":
				WriteTimer.println("对不起，这个地点无法查询，说不定不存在呢，嘿嘿。");
				break;
			case "AP010011":
				WriteTimer.println("我好像迷失了方向……我找不到这个城市！");
				break;
			case "AP010014":
				WriteTimer.println("这个小时的查询次数已经用完了，我得歇会儿。");
				break;
			default:
				WriteTimer.println("日志：系统内部发生了不可名状的错误。");
				break;
		}
	}
	
	/**
	 * 回应函数：将生活指数的查询结果回复给用户
	 * @param Weather askLifeSuggestion 天气查询信息
	 * 
	 * */
	public static void lifeSuggestionReaction(Weather askLifeSuggestion) {
		for(int i=0;i<=5;i++) {
			if(lifeSuggestion[i]) {
				switch (i) {
				case 0:
					switch (askLifeSuggestion.lifeSuggestion.getDressing()) {
					case "炎热":
						WriteTimer.println("今天天气"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",建议穿轻棉织物制作的短衣、短裙、薄短裙、短裤");
						break;
					case "较热":
						WriteTimer.println("今天天气"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",建议穿棉麻面料的衬衫、薄长裙、薄T恤");
						break;
					case "寒冷":
						WriteTimer.println("今天天气"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",建议穿棉衣、冬大衣、皮夹克、厚呢外套、呢帽、手套、羽绒服、皮袄");					
						break;
					case "较冷":
						WriteTimer.println("今天天气"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",建议穿毛衣、风衣、毛套装、西服套装");
						break;
					case "舒适":
						WriteTimer.println("今天天气"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",建议穿单层棉麻面料的短套装、T恤衫、薄牛仔衫裤、休闲服、职业套装");
						break;
					case "较舒适":
						WriteTimer.println("今天天气"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",建议穿套装、夹衣、风衣、休闲装、夹克衫、西装、薄毛衣");
						break;
					case "热":
						WriteTimer.println("今天天气"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",建议穿单层棉麻面料的短套装、T恤衫、薄牛仔衫裤、休闲服、职业套装");
						break;
					case "冷":
						WriteTimer.println("今天天气"+askLifeSuggestion.lifeSuggestion.getDressing()
							+",建议穿风衣、大衣、夹大衣、外套、毛衣、毛套装、西装、防寒服");
						break;
					default:
						WriteTimer.println("嗯……我没什么建设性的建议");
						break;
					}
					break;
				case 1:
					WriteTimer.println("今天"+askLifeSuggestion.lifeSuggestion.getCar_washing()+"洗车");
					break;
				case 2:
					WriteTimer.println("今天"+askLifeSuggestion.lifeSuggestion.getFlu()+"感冒");
					break;
				case 3:
					//运动
					WriteTimer.println(askLifeSuggestion.lifeSuggestion.getSport());
					break;
				case 4:
					//旅行
					WriteTimer.println(askLifeSuggestion.lifeSuggestion.getTravel());
					break;
				case 5:
					WriteTimer.println("今天紫外线"+askLifeSuggestion.lifeSuggestion.getUv());
					break;
				default:
					break;
				}
			}
		}
	}

	
	/**
	 * 提供服务（生活指数）
	 * @throws ErrorMessageException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void offerLifeSuggestion() throws InterruptedException, EncryptedDocumentException, InvalidFormatException {
		//1.对分析生活信息进行输出
		netanswer = false;
		try {
			analyzeLifeSuggestion();
		} catch (IOException e1) {
			checkNet();
		}
		//2.获取所有天气信息
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
		//3.给出反馈
		if(askLifeSuggestion!=null)
			//即查询未出错
			lifeSuggestionReaction(askLifeSuggestion);
		
		
	}
	
	/**
	 * 主服务
	 * 进入服务分支的窗口
	 * 
	 * @throws ErrorMessageException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 * */
	public static void server()  {
		//之后改成叫名字
		if(userData.isLogin()) {
			WriteTimer.println("嘿！"+userData.getUserNickName()+"！");			
		}
		else {
			WriteTimer.println("嘿！人类！");
		}
		while(true) {
			//如果是第一次使用
			if(ChatGUI.wheatherFirst) {
				ChatGUI.wheatherFirst=false;
				//1.抛出问题
				WriteTimer.println("你找我有何贵干？");
				//2.显示按钮
				WriteTimer.paintButton("1查一下今天的天气");
				WriteTimer.paintButton("2我想知道最近的天气");
				WriteTimer.paintButton("3我今天……");
			}
			//否则
			else {
				//1.抛出问题
				if(robotGoToWork)
					WriteTimer.println("我刚才去工作了，找我有什么事吗？");
				else WriteTimer.println("还想知道什么吗？");
				//2.显示按钮
				WriteTimer.paintButton("1查一下今天的天气");
				WriteTimer.paintButton("2我想知道最近的天气");
				WriteTimer.paintButton("3我今天……");
				WriteTimer.paintButton("4拜拜！");
			}
			//3.接收选项
			waitForIndex();
			try {
				switch(answer) {
				case 1:
					WriteTimer.println("噢，好的，我明白了！");
					offerNowWeather();
					break;
				case 2:
					WriteTimer.println("噢，好的，我明白了！");
					offerDailyWeather();
					break;
				case 3:
					WriteTimer.println("你今天怎么了？");
					WriteTimer.println("噢，我知道了，你一定是想让我给你点生活方面的建议吧？");
					WriteTimer.println("嘿嘿。");
					offerLifeSuggestion();
					break;
				case 4:
					WriteTimer.println("那么再见啦！");
					Thread.sleep(1500);
					robotGoToWork = true;
					synchronized(mainO) {
						System.out.println("机器人去工作了");
						mainO.wait();
					}
				}
			} catch (EncryptedDocumentException | InvalidFormatException | InterruptedException e) {
				e.printStackTrace();
			}	
		}
		
	}
	
	/**
	 * 主调方法
	 * 
	 * @throws IOException
	 * @throws ErrorMessageException
	 * @throws InterruptedException
	 * @throws EncryptedDocumentException
	 * @throws InvalidFormatException
	 * */
	public static void main(String[] args) throws ErrorMessageException, InterruptedException, EncryptedDocumentException, InvalidFormatException{
		try {
			//解决输入法切换导致的白屏问题
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
	 * 机器人线程：不断把缓存中的信息事件输出</br>
	 * 把所有动作看做事件。</br>
	 * 机器人事件：</br>
	 * <ul>
	 * <li>机器人说的话</li>
	 * <li>弹出按钮</li>
	 * <li>弹出输入框</li>
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
//					System.out.println("轮到机器人");
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
							System.out.println("得到按钮");
						}
					}
					if(waitUserText && userText!=null) {
						waitUserText = false;
						synchronized(userTextO) {
							userTextO.notify();
							System.out.println("得到文字");
						}
					}
					synchronized(userO) {
//						System.out.println("唤醒用户");
						userO.notifyAll();
					}
					turn++;					
					synchronized(robotO) {
						try {
//							System.out.println("机器人等");
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
	 * 用户线程：不断把缓存中的信息事件输出</br>
	 * 把所有动作看做事件。</br>
	 * 用户事件：</br>
	 * <ul>
	 * <li>用户说的话</li>
	 * </ul>
	 * 注意：</br>
	 * <ul>
	 * <li>点击按钮：往用户buffer中增加一个信息事件</li>
	 * <li>文本框有效输入后按回车：往用户buffer中增加一个信息事件</li>
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
//					System.out.println("轮到用户");
					int originalSsize = userMessageBuffer.size();
					boolean f = false;
					while(pos<userMessageBuffer.size() && isRun) {
//						System.out.println("用户");
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
							System.out.println("得到按钮");
						}
					}
					if(waitUserText && userText!=null) {
						waitUserText = false;
						synchronized(userTextO) {
							userTextO.notify();
							System.out.println("得到文字");
						}
					}
					synchronized(robotO) {
//						System.out.println("唤醒机器人");
						robotO.notifyAll();
					}
					turn++;
					synchronized(userO) {
						try {
//							System.out.println("用户等");
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