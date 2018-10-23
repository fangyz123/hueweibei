package com.janborn.www.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.border.EmptyBorder;

public class WelcomeGUI extends JFrame {
	/*获取屏幕分辨率*/
	private final static Dimension SCREENSIZE = Toolkit.getDefaultToolkit().getScreenSize();
	/*屏幕尺寸*/
	private final static int WIDTH = (int)SCREENSIZE.getWidth();
	private final static int HEIGHT = (int)SCREENSIZE.getHeight();
	/*主界面尺寸*/
	public final static int MAIN_WIN_HEIGHT = HEIGHT * 2 / 5;
	public final static int MAIN_WIN_WIDTH = MAIN_WIN_HEIGHT * 4 / 3;

	public static JPanel contentPane;
	public static JLabel bgLabel = null;

	/**
	 * Create the frame.
	 */
	public WelcomeGUI() {
		setSize(MAIN_WIN_WIDTH, MAIN_WIN_HEIGHT);
		//窗口自动居中
		setLocationRelativeTo(null);
		//关闭按钮动作
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//去掉窗口的装饰
		setUndecorated(true); 
		//背景透明
		setBackground(new Color(0,0,0,0));
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		getContentPane().setLayout(null);
		
		//设置容器
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		//导入背景图片
		ImageIcon ii = new ImageIcon(MainGUI.class.getResource("/images/logojoy.JPG"));
		ii.setImage(ii.getImage().getScaledInstance(MAIN_WIN_WIDTH, MAIN_WIN_HEIGHT,Image.SCALE_SMOOTH  ));
		bgLabel = new JLabel(ii);
		bgLabel.setBounds(0, 0, MAIN_WIN_WIDTH, MAIN_WIN_HEIGHT);
		bgLabel.setBackground(null);
		bgLabel.setOpaque(false);

		contentPane.add(bgLabel);
	}

}
