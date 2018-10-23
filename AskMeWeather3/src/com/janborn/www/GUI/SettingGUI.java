package com.janborn.www.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.janborn.www.Setting;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class SettingGUI extends JFrame {
	/*��ȡ��Ļ�ֱ���*/
	private final static Dimension SCREENSIZE = Toolkit.getDefaultToolkit().getScreenSize();
	/*��Ļ�ߴ�*/
	private final static int WIDTH = (int)SCREENSIZE.getWidth();
	private final static int HEIGHT = (int)SCREENSIZE.getHeight();
	/*������ߴ�*/
	public final static int MAIN_WIN_HEIGHT = HEIGHT * 2 / 5;
	public final static int MAIN_WIN_WIDTH = MAIN_WIN_HEIGHT * 4 / 3;

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public SettingGUI() {
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
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel = new JLabel("\u58F0\u97F3");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("���� Light", Font.BOLD, 20));
		contentPane.add(lblNewLabel);
		
		JCheckBox checkBox = new JCheckBox("\u5F00\u542F");
		checkBox.setSelected(true);
		checkBox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(!checkBox.isSelected()) {
					Setting.sounds = false;
					checkBox.setSelected(false);
					checkBox.setText("�ر�");
				}
				else {
					Setting.sounds = true;
					checkBox.setSelected(true);
					checkBox.setText("����");
				}
			}
			
		});
		contentPane.add(checkBox);
		
	}

}
