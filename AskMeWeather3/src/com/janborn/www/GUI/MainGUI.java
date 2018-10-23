package com.janborn.www.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.janborn.www.Demo;
import com.janborn.www.user.UserData;
import com.janborn.www.util.NetCheckingThread;

public class MainGUI extends JFrame {
	/*获取屏幕分辨率*/
	private final static Dimension SCREENSIZE = Toolkit.getDefaultToolkit().getScreenSize();
	private final static int WIDTH = (int)SCREENSIZE.getWidth();
	private final static int HEIGHT = (int)SCREENSIZE.getHeight();
	/*主界面尺寸*/
	public final static int MAIN_WIN_WIDTH = WIDTH * 4 / 5;
	public final static int MAIN_WIN_HEIGHT = MAIN_WIN_WIDTH * 9 / 16;
	/*话语条尺寸*/
	public final static int WORD_WIDTH = MAIN_WIN_WIDTH * 539 /1600;
	public final static int WORD_HEIGHT = MAIN_WIN_HEIGHT * 61 /900;
	/*话语条文字颜色*/
	public final static Color wordColor = new Color(34, 35, 35);
	/*话语条文字*/
	public static Font wordFont = new Font("等线", Font.PLAIN, 55);
	
	private JLabel exitLabel = new JLabel("");
	private JLabel settingLabel = new JLabel("");
	public final static JLabel wordLabel1 = new JLabel("");
	public final static JLabel wordLabel2 = new JLabel("");
	public final static JLabel wordLabel3 = new JLabel("");
	public static JLabel moreLabel = new JLabel("");
	private final JLabel minisizeLabel = new JLabel("");
	public static JLabel checkLabel = new JLabel("");
	public static JLabel checkErrorLabel = new JLabel("");
	public static JLabel loginLabel = new JLabel();

	private JPanel contentPane;
	public static ChatGUI chatbox = new ChatGUI();
	public static SettingGUI settingWIN = new SettingGUI();
	public static SigninGUI signinGUI = new SigninGUI(); 
	public static LoginGUI loginGUI = new LoginGUI();
	public static JLabel welcomeLabel = new JLabel("\u4F60\u597D");

	/**
	 * Create the frame.
	 */
	public MainGUI() {
		
		chatbox.setVisible(false);
		//设置窗口
		setTitle("知天气\r\n");
		setSize((int)(MAIN_WIN_WIDTH*1.01), (int)(MAIN_WIN_HEIGHT*1.01));
		setResizable(false);
		//窗口自动居中
		setLocationRelativeTo(null);
		//关闭按钮动作
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//隐藏自带“设置按钮”
		UIManager.put("RootPane.setupButtonVisible", false);
		
		//设置字体
		wordFont = new Font("等线", Font.PLAIN, (int)(WIDTH/35));
		
		
		//设置容器
		contentPane = new JPanel();
//		Insets set=contentPane.getInsets();
//		int titlebarH=set.top;
//		System.out.println(titlebarH);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//设置窗口关闭监听器
		addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				UserData u = new UserData(1);          
				//如果序列化文件存在但为空，删除
				if(u.getUserFile().exists() && u.getUserFile().length()==0) {
					u.getUserFile().delete();
				}
				setVisible(false);
			}
		});
		
		//设置比率
		//导入背景图片
		ImageIcon ii = new ImageIcon(MainGUI.class.getResource("/images/main_bg_noshadow.png"));
		float widthRate = (float) (1.0*MAIN_WIN_WIDTH / ii.getIconWidth());
		float heightRate = (float) (1.0*MAIN_WIN_HEIGHT / ii.getIconHeight());
		
		
		ImageIcon cli = new ImageIcon(MainGUI.class.getResource("/images/checkingNet.png"));
		final int checkLabelWidth = (int)(cli.getIconWidth() * widthRate);
		final int checkLabelHeight = (int)(cli.getIconHeight() * heightRate);
		cli.setImage(cli.getImage().getScaledInstance(checkLabelWidth, checkLabelHeight,Image.SCALE_SMOOTH ));
		checkLabel.setIcon(cli);
		checkLabel.setHorizontalAlignment(SwingConstants.CENTER);
		checkLabel.setBounds((int)(MAIN_WIN_WIDTH*112/1600), (int)(MAIN_WIN_HEIGHT*559/900), checkLabelWidth, checkLabelHeight);
		getContentPane().add(checkLabel);
		checkLabel.setVisible(false);
		
		ImageIcon celi = new ImageIcon(MainGUI.class.getResource("/images/checkingNet_error.png"));
		final int checkErrorLabelWidth = (int)(celi.getIconWidth() * widthRate);
		final int checkErrorLabelHeight = (int)(celi.getIconHeight() * heightRate);
		celi.setImage(celi.getImage().getScaledInstance(checkErrorLabelWidth, checkErrorLabelHeight,Image.SCALE_SMOOTH ));
		checkErrorLabel.setIcon(celi);
		checkErrorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		checkErrorLabel.setBounds((int)(MAIN_WIN_WIDTH*112/1600), (int)(MAIN_WIN_HEIGHT*559/900),
				checkErrorLabelWidth, checkErrorLabelHeight);
		getContentPane().add(checkErrorLabel);
		checkErrorLabel.setVisible(false);
		
		//more按钮
		ImageIcon mli = new ImageIcon(MainGUI.class.getResource("/images/more_normal.png"));
		ImageIcon mhli = new ImageIcon(MainGUI.class.getResource("/images/more_hover.png"));
		ImageIcon mali = new ImageIcon(MainGUI.class.getResource("/images/more_active.png"));
		final int moreLabelWidth = (int)(MAIN_WIN_WIDTH*355/1600);
		final int moreLabelHeight = (int)(MAIN_WIN_HEIGHT*49/900);
		mli.setImage(mli.getImage().getScaledInstance(moreLabelWidth, moreLabelHeight,Image.SCALE_SMOOTH ));
		mhli.setImage(mhli.getImage().getScaledInstance(moreLabelWidth, moreLabelHeight,Image.SCALE_SMOOTH ));
		mali.setImage(mali.getImage().getScaledInstance(moreLabelWidth, moreLabelHeight,Image.SCALE_SMOOTH ));
		moreLabel.setIcon(mli);
		moreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		moreLabel.setBounds((int)(MAIN_WIN_WIDTH*112/1600), (int)(MAIN_WIN_HEIGHT*559/900), moreLabelWidth, moreLabelHeight);
		moreLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				moreLabel.setVisible(false);;
//				ChatGUI.wheatherFirst = false;
				if(!chatbox.isVisible()) {
					chatbox.setVisible(true);
					synchronized(Demo.mainO) {
						Demo.mainO.notifyAll();
						System.out.println("机器人回来了");
					}
					if(!Demo.userThread.isAlive() && !Demo.robotThread.isAlive()) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
									Demo.userThread.start();
									Demo.robotThread.start();								
								}
							
						}).start() ;
					}
					if(ChatGUI.wheatherFirst) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								Demo.server();						
							}
							
						}).start();					
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				moreLabel.setIcon(mhli);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				moreLabel.setIcon(mli);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				moreLabel.setIcon(mali);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				moreLabel.setIcon(mhli);
			}
			
		});
		getContentPane().add(moreLabel);
		moreLabel.setVisible(false);

		//按钮
		ImageIcon eali = new ImageIcon(MainGUI.class.getResource("/images/head_exit_active.png"));
		final int buttonLabelWidth = (int)(widthRate * eali.getIconWidth());
		final int buttonLabelHeight = (int)(heightRate * eali.getIconHeight());
		//关闭按钮
//		ImageIcon eli = new ImageIcon(MainGUI.class.getResource("/images/head_exit_normal.png"));
//		eli.setImage(eli.getImage().getScaledInstance((int)(eli.getIconWidth()*widthRate), (int)(eli.getIconHeight()*heightRate),Image.SCALE_SMOOTH ));
//		eali.setImage(eali.getImage().getScaledInstance(buttonLabelWidth ,buttonLabelHeight,Image.SCALE_SMOOTH ));
//		exitLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		exitLabel.setIcon(eli);
//		exitLabel.setBounds((int)(MAIN_WIN_WIDTH*1380/1600), (int)(MAIN_WIN_HEIGHT*51/900), buttonLabelWidth, buttonLabelHeight);
//		exitLabel.addMouseListener(new MouseListener() {
//
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
//				exitLabel.setIcon(eali);
//				System.exit(0);
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent arg0) {
//				exitLabel.setIcon(eali);
//			}
//
//			@Override
//			public void mouseExited(MouseEvent arg0) {
//				exitLabel.setIcon(eli);
//			}
//
//			@Override
//			public void mousePressed(MouseEvent arg0) {
//				exitLabel.setIcon(eali);
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent arg0) {
//				exitLabel.setIcon(eli);
//			}
//			
//		});
//		getContentPane().add(exitLabel);
		
		ImageIcon sli = new ImageIcon(MainGUI.class.getResource("/images/head_setting_normal.png"));
		ImageIcon sali = new ImageIcon(MainGUI.class.getResource("/images/head_setting_active.png"));
		sli.setImage(sli.getImage().getScaledInstance((int)(sli.getIconWidth()*widthRate), (int)(sli.getIconHeight()*heightRate),Image.SCALE_SMOOTH ));
		sali.setImage(sali.getImage().getScaledInstance(buttonLabelWidth, buttonLabelHeight,Image.SCALE_SMOOTH ));
		settingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		settingLabel.setIcon(sli);
		settingLabel.setBounds((int)(MAIN_WIN_WIDTH * 1367/1600), (int)(MAIN_WIN_HEIGHT*55/900), buttonLabelWidth, buttonLabelHeight);
		settingLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				settingLabel.setIcon(sali);
				settingWIN.setVisible(true);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				settingLabel.setIcon(sali);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				settingLabel.setIcon(sli);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				settingLabel.setIcon(sali);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				settingLabel.setIcon(sli);
			}
			
		});
		getContentPane().add(settingLabel);
		
		ImageIcon loginli = new ImageIcon(MainGUI.class.getResource("/images/head_login_normal.png"));
		ImageIcon loginali = new ImageIcon(MainGUI.class.getResource("/images/head_login_active.png"));
		ImageIcon logoutli = new ImageIcon(MainGUI.class.getResource("/images/head_logout_normal.png"));
		ImageIcon logoutali = new ImageIcon(MainGUI.class.getResource("/images/head_logout_active.png"));
		loginli.setImage(loginli.getImage().getScaledInstance
				((int)(loginli.getIconWidth()*widthRate), (int)(loginli.getIconHeight()*heightRate),Image.SCALE_SMOOTH ));
		loginali.setImage(loginali.getImage().getScaledInstance(buttonLabelWidth, buttonLabelHeight,Image.SCALE_SMOOTH ));
		logoutli.setImage(logoutli.getImage().getScaledInstance
				((int)(logoutli.getIconWidth()*widthRate), (int)(logoutli.getIconHeight()*heightRate),Image.SCALE_SMOOTH ));
		logoutali.setImage(logoutali.getImage().getScaledInstance(buttonLabelWidth, buttonLabelHeight,Image.SCALE_SMOOTH ));
		loginLabel.setIcon(loginli);
		loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
		loginLabel.setBounds((int)(MAIN_WIN_WIDTH*1175/1600), (int)(MAIN_WIN_HEIGHT*55/900), 
				buttonLabelWidth, buttonLabelHeight);
		loginLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(!Demo.getUserData().isLogin()) {
					loginLabel.setIcon(loginali);
					MainGUI.loginGUI.setVisible(true);
				}
				else {
					loginLabel.setIcon(logoutali);
					if(JOptionPane.showConfirmDialog(new JFrame().getContentPane(), 
							"确认注销？", "系统信息",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
						Demo.getUserData().logOff();
						change2logout();
						welcomeLabel.setVisible(false);
						welcomeLabel.setText("");
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if(!Demo.getUserData().isLogin()) {
					loginLabel.setIcon(loginali);
				}
				else {
					loginLabel.setIcon(logoutali);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if(!Demo.getUserData().isLogin()) {
					loginLabel.setIcon(loginli);
				}
				else {
					loginLabel.setIcon(logoutli);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if(!Demo.getUserData().isLogin()) {
					loginLabel.setIcon(loginali);
				}
				else {
					loginLabel.setIcon(logoutali);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(!Demo.getUserData().isLogin()) {
					loginLabel.setIcon(loginli);
				}
				else {
					loginLabel.setIcon(logoutli);
				}
			}
			
		});
		
		
		welcomeLabel.setVisible(false);
		welcomeLabel.setFont(new Font("等线", Font.BOLD, 20));
		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		welcomeLabel.setForeground(Color.WHITE);
		welcomeLabel.setBounds((int)(MAIN_WIN_WIDTH*975/1600), (int)(MAIN_WIN_HEIGHT*55/900), buttonLabelWidth, buttonLabelHeight);
		contentPane.add(welcomeLabel);
		getContentPane().add(loginLabel);
		
//		ImageIcon minili = new ImageIcon(MainGUI.class.getResource("/images/head_minisize_normal.png"));
//		ImageIcon miniali = new ImageIcon(MainGUI.class.getResource("/images/head_minisize_active.png"));
//		minili.setImage(minili.getImage().getScaledInstance
//				((int)(minili.getIconWidth()*widthRate), (int)(minili.getIconHeight()*heightRate),Image.SCALE_SMOOTH ));
//		miniali.setImage(miniali.getImage().getScaledInstance(buttonLabelWidth, buttonLabelHeight,Image.SCALE_SMOOTH ));
//		minisizeLabel.setIcon(minili);
//		minisizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		minisizeLabel.setBounds((int)(MAIN_WIN_WIDTH*1200/1600), (int)(MAIN_WIN_HEIGHT*51/900), 
//				buttonLabelWidth, buttonLabelHeight);
//		minisizeLabel.addMouseListener(new MouseListener() {
//
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				minisizeLabel.setIcon(miniali);
//				setExtendedState(JFrame.ICONIFIED);
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent e) {
//				minisizeLabel.setIcon(miniali);
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//				minisizeLabel.setIcon(minili);
//			}
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//				minisizeLabel.setIcon(miniali);
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				minisizeLabel.setIcon(minili);
//			}
//			
//		});
//		getContentPane().add(minisizeLabel);
		
		wordLabel1.setText("");
		wordLabel1.setForeground(wordColor);
		wordLabel1.setFont(wordFont);
		wordLabel1.setBounds((int)(MAIN_WIN_WIDTH*120/1600), (int)(MAIN_WIN_HEIGHT*264/900), WORD_WIDTH, WORD_HEIGHT+20);
		getContentPane().add(wordLabel1);
		
		wordLabel2.setText("");
		wordLabel2.setForeground(wordColor);
		wordLabel2.setFont(wordFont);
		wordLabel2.setBounds((int)(MAIN_WIN_WIDTH*120/1600), (int)(MAIN_WIN_HEIGHT*357/900), WORD_WIDTH, WORD_HEIGHT+20);
		getContentPane().add(wordLabel2);
		
		wordLabel3.setText("");
		wordLabel3.setForeground(wordColor);
		wordLabel3.setFont(wordFont);
		wordLabel3.setBounds((int)(MAIN_WIN_WIDTH*120/1600), (int)(MAIN_WIN_HEIGHT*444/900), WORD_WIDTH, WORD_HEIGHT+20);
		
		getContentPane().add(wordLabel3);
		
		
		//背景		
		ii.setImage(ii.getImage().getScaledInstance(MAIN_WIN_WIDTH - 20, MAIN_WIN_HEIGHT - 70,Image.SCALE_SMOOTH ));
		JLabel bgLabel = new JLabel(ii);
		bgLabel.setBounds(0, 0, MAIN_WIN_WIDTH-20, MAIN_WIN_HEIGHT-70);
		bgLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(!NetCheckingThread.netanswer) {
					wordLabel1.setText("嘿！别动！");
					wordLabel2.setText("我在帮你检查网络呢！");
					wordLabel3.setText("");
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		getContentPane().add(bgLabel);
	}
	public static void change2CheckLabel() {
		checkErrorLabel.setVisible(false);
		moreLabel.setVisible(false);
		checkLabel.setVisible(true);
	}
	public static void change2CheckErrorLabel() {
		moreLabel.setVisible(false);
		checkLabel.setVisible(false);
		checkErrorLabel.setVisible(true);
	}
	public static void change2MoreLabel() {
		checkErrorLabel.setVisible(false);
		checkLabel.setVisible(false);
		moreLabel.setVisible(true);
	}
	public static void clearMoreLabel() {
		checkErrorLabel.setVisible(false);
		checkLabel.setVisible(false);
		moreLabel.setVisible(false);
	}
	public static void change2login() {
		ImageIcon logoutli = new ImageIcon(MainGUI.class.getResource("/images/head_logout_normal.png"));
		logoutli.setImage(logoutli.getImage().getScaledInstance
				((int)(MAIN_WIN_WIDTH*162/1600), (int)(MAIN_WIN_HEIGHT*42/900),Image.SCALE_SMOOTH ));
		loginLabel.setIcon(logoutli);
	}
	public static void change2logout() {
		ImageIcon loginli = new ImageIcon(MainGUI.class.getResource("/images/head_login_normal.png"));
		loginli.setImage(loginli.getImage().getScaledInstance
				((int)(MAIN_WIN_WIDTH*162/1600), (int)(MAIN_WIN_HEIGHT*42/900),Image.SCALE_SMOOTH ));
		loginLabel.setIcon(loginli);
	}
}
