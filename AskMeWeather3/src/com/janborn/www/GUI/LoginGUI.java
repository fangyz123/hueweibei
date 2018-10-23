package com.janborn.www.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.janborn.www.Demo;

import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import java.awt.Color;

public class LoginGUI extends JFrame {
	/*获取屏幕分辨率*/
	private final static Dimension SCREENSIZE = Toolkit.getDefaultToolkit().getScreenSize();
	/*屏幕尺寸*/
	private final static int WIDTH = (int)SCREENSIZE.getWidth();
	private final static int HEIGHT = (int)SCREENSIZE.getHeight();
	/*主界面尺寸*/
	public final static int MAIN_WIN_HEIGHT = HEIGHT * 2 / 5;
	public final static int MAIN_WIN_WIDTH = MAIN_WIN_HEIGHT * 4 / 3;

	private JPanel contentPane;
	private JTextField urNameField;
	private JPasswordField passwordField;
	private JLabel warningLabel = new JLabel("");

	/**
	 * Create the frame.
	 */
	public LoginGUI() {
		setVisible(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setSize(MAIN_WIN_WIDTH, MAIN_WIN_HEIGHT);
		//窗口自动居中
		setLocationRelativeTo(null);
		setResizable(false);
		//隐藏自带“设置按钮”
		UIManager.put("RootPane.setupButtonVisible", false);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				urNameField.setText("");
				passwordField.setText("");
				warningLabel.setText("");
			}
		});
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new GridLayout(3, 1, 0, 0));
		
		JPanel urPanel = new JPanel();
		panel.add(urPanel);
		
		JLabel urNameLabel = new JLabel("\u7528\u6237\u540D");
		urPanel.add(urNameLabel);
		
		urNameField = new JTextField("");
		urPanel.add(urNameField);
		urNameField.setColumns(20);
		
		JPanel pawPanel = new JPanel();
		panel.add(pawPanel);
		
		JLabel pawLabel = new JLabel("\u5BC6\u7801");
		pawPanel.add(pawLabel);
		
		passwordField = new JPasswordField("");
		passwordField.setColumns(20);
		passwordField.setEchoChar('*');
		pawPanel.add(passwordField);
		
		JPanel buttonPanel = new JPanel();
		panel.add(buttonPanel);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		JLabel lblNewLabel = new JLabel("\u203B\u767B\u5F55\u540E\u53EF\u4FDD\u5B58\u7528\u6237\u4FE1\u606F");
		lblNewLabel.setForeground(Color.RED);
		panel_1.add(lblNewLabel);
		
		
		warningLabel.setForeground(Color.RED);
		panel_1.add(warningLabel);
		
		JButton loginButton = new JButton("\u767B\u5F55");
		loginButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(!urNameField.getText().equals("") && passwordField.getPassword().length!=0) {
					if(Demo.getUserData().logIn(urNameField.getText(), String.valueOf(passwordField.getPassword()))) {
						warningLabel.setText("登录成功");
						MainGUI.change2login();
						MainGUI.welcomeLabel.setText("你好，"+Demo.getUserData().getUserNickName());
						MainGUI.welcomeLabel.setVisible(true);
//						setVisible(false);
					}
					else {
						warningLabel.setText("用户名密码不匹配或用户不存在!");
					}
				}
				else {
					warningLabel.setText("信息不可为空！");
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		buttonPanel.add(loginButton);
		
		JButton signinButton = new JButton("\u6CE8\u518C");
		buttonPanel.add(signinButton);
		signinButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				MainGUI.signinGUI.setVisible(true);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
	}

}
