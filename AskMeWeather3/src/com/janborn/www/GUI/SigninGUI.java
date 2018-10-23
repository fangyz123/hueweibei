package com.janborn.www.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.janborn.www.Demo;
import com.janborn.www.user.User;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.Color;

public class SigninGUI extends JFrame {
	/*��ȡ��Ļ�ֱ���*/
	private final static Dimension SCREENSIZE = Toolkit.getDefaultToolkit().getScreenSize();
	/*��Ļ�ߴ�*/
	private final static int WIDTH = (int)SCREENSIZE.getWidth();
	private final static int HEIGHT = (int)SCREENSIZE.getHeight();
	/*������ߴ�*/
	public final static int MAIN_WIN_HEIGHT = HEIGHT * 2 / 5;
	public final static int MAIN_WIN_WIDTH = MAIN_WIN_HEIGHT * 4 / 3;

	private JPanel contentPane;
	private JTextField urNameTextField;
	private JTextField nickNameTextField;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JLabel warningLabel = new JLabel("");


	/**
	 * Create the frame.
	 */
	public SigninGUI() {
		setVisible(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setSize(MAIN_WIN_WIDTH, MAIN_WIN_HEIGHT);
		//�����Զ�����
		setLocationRelativeTo(null);
		setResizable(false);
		//�����Դ������ð�ť��
		UIManager.put("RootPane.setupButtonVisible", false);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		//���ô��ڹرռ�����
		addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				urNameTextField.setText("");
				nickNameTextField.setText("");
				passwordField.setText("");
				passwordField_1.setText("");
				warningLabel.setText("");
			}
		});
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new GridLayout(4, 1, 0, 0));
		
		JPanel urNamepanel = new JPanel();
		panel.add(urNamepanel);
		
		JLabel urNameLabel = new JLabel("*\u7528\u6237\u540D");
		urNamepanel.add(urNameLabel);
		
		urNameTextField = new JTextField("");
		urNamepanel.add(urNameTextField);
		urNameTextField.setColumns(20);
		
		JPanel nickNamePanel = new JPanel();
		panel.add(nickNamePanel);
		
		JLabel nickNameLabel = new JLabel("*\u6635\u79F0");
		nickNamePanel.add(nickNameLabel);
		
		nickNameTextField = new JTextField("");
		nickNamePanel.add(nickNameTextField);
		nickNameTextField.setColumns(20);
		
		JPanel pwPanel = new JPanel();
		panel.add(pwPanel);
		
		JLabel pwLabel = new JLabel("*\u5BC6\u7801");
		pwPanel.add(pwLabel);
		
		passwordField = new JPasswordField("");
		passwordField.setEchoChar('*');
		passwordField.setColumns(20);
		pwPanel.add(passwordField);
		
		JPanel pwPanel2 = new JPanel();
		panel.add(pwPanel2);
		
		JLabel pwLabel2 = new JLabel("*\u786E\u8BA4\u5BC6\u7801");
		pwPanel2.add(pwLabel2);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setEchoChar('*');
		passwordField_1.setColumns(20);
		pwPanel2.add(passwordField_1);
		
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1);
		
		JPanel signinPanel = new JPanel();
		panel_1.add(signinPanel);
		
		JButton signinButton = new JButton("\u6CE8\u518C");
		signinPanel.add(signinButton);
		
		warningLabel.setForeground(Color.RED);
		signinPanel.add(warningLabel);
		signinButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(!urNameTextField.getText().equals("")
						&& !nickNameTextField.getText().equals("")
						&& passwordField.getPassword().length!=0
						&& passwordField_1.getPassword().length!=0) {
					if(String.valueOf(passwordField.getPassword()).equals(String.valueOf(passwordField_1.getPassword()))) {
						User u = new User(urNameTextField.getText(),nickNameTextField.getText(),String.valueOf(passwordField.getPassword()));
						if(Demo.getUserData().signIn(u)) {
							warningLabel.setText("ע��ɹ���");
//							try {
//								Thread.sleep(1000);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//							setVisible(false);
						}else {
							warningLabel.setText("�û����Ѵ��ڣ��������ֱ�ӵ�¼��");
						}
					}
					else {
						warningLabel.setText("�������벻ƥ�䣡");
					}
				}
				else {
					warningLabel.setText("�Ǻ����Ϊ�գ�");
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
	}

}
