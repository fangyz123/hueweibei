package com.janborn.www.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.janborn.www.Demo;
import com.janborn.www.Demo.UserThread;
import com.janborn.www.util.MessageBox;

public class ChatGUI extends JFrame {
	public final static int USER_SAY = 1;
	public final static int ROBOT_SAY = 0;
	public final static int TEXTFIELD = 0;
	public final static int QUESTIONBOX = 1;
	public final static int NONE = -1;
	/*��ȡ�ֱ���*/
	private final static Dimension SCREENSIZE = Toolkit.getDefaultToolkit().getScreenSize();
	/*��Ļ�ߴ�*/
	private final static int WIDTH = (int)SCREENSIZE.getWidth();
	private final static int HEIGHT = (int)SCREENSIZE.getHeight();
	/*���ڳߴ�*/
	private final static int CHAT_WIN_HEIGHT = HEIGHT * 12 / 16;
	private final static int CHAT_WIN_WIDTH = CHAT_WIN_HEIGHT * 3 / 4;

	public static JTextField textField = null;
	private static JPanel panel = new JPanel();
	private static JScrollPane scrollPane = null;
	private JLabel headLabel = new JLabel("");
	public static JPanel textareapanel = new JPanel();
	public static JPanel questionBox = new JPanel();
	
	/*�жϴ�ʱ���������������������ӣ�ͣ�ã�
	 * 0�������
	 * 1���������
	 * -1����������
	 * */
//	private static int quesOrtext = -1;

	/*�û������Ҳ����*/
	private static int userLeftX = (int)(CHAT_WIN_WIDTH*552/600);
	/*��һ�����ݵ���ʼ����*/
	private static int nextY = 0;
	private static int robotX = (int)(CHAT_WIN_WIDTH*40/600);
	private int userX = userLeftX;
	
	/*�û�������ɫ*/
	private static Color userColor = new Color(116,180,229);
	/*������������ɫ*/
	private static Color robotColor = new Color(154,212,236);
	/*��������*/
	private static Font font = new Font("����", Font.BOLD, 18);
	/*�Ի�����*/
	private final static int CHAT_BOATD_LENGTH = CHAT_WIN_WIDTH * 513 /600;
	
	/*���������*/
//	private static Font textFildFont = new Font("����", Font.BOLD, 18);
	
	/*���⿨Ƭ����*/
//	private final static Font questionFont = new Font("����", Font.BOLD, 18);
	/*���⿨Ƭ��ɫ*/
	private final static Color questionColor = new Color(19,128,173);
	/*���⿨Ƭ������ɫ*/
	private final static Color questionFontColor = Color.WHITE;
	private static final String JPanel = null;
	/*������*/
	public static int questionNO = -1;
	/*�Ƿ��һ�����⿨Ƭ*/
	public static boolean wheatherFirstCard = true;
	
	/*�Ƿ��һ�δ򿪴���*/
	public static boolean wheatherFirst = true;	

	private static JPanel contentPane;
	
	/**
	 * ����
	 * @author ������
	 * @param w ��Ҫ������ַ���
	 * @param n һ�еĺ�����
	 * */
	public static String newline(String w,int n) {
		StringBuffer s1=new StringBuffer(w);
		int index;
		int fontlength=w.length();
		if(fontlength>n) {
			for(index=n;index<fontlength;index+=27) {
				s1.insert(index,"<br/>");
			}
		}
		return new String("<html>"+s1+"</html>");
	}
	
	/**
	 * �����µ�����
	 * 
	 * @param w ���������ַ�������
	 * @param who ��������һ�����󷢳���</br>
	 * 			0 robot</br>
	 * 			1 user</br>
	 * */
	public static void addBubble(String w,int who) {
		//�Ż�������n��������
		int n = 25;
		//���еĴ���������
		int sum = 1;
		StringBuffer s1=new StringBuffer(w);
		int index;
		int fontlength=w.length();
		if(fontlength>n) {
			for(index=n;index<fontlength;index+=n+1) {
				s1.insert(index,"<br/>");
				sum++;
			}
		}
		String word = new String("<html>"+s1+"</html>");
		
		
		JComponent j = new JLabel();
		//��ȡ�ַ�����ȡ��߶�
		FontMetrics fm = j.getFontMetrics(textField.getFont());
		int fontHeight = fm.getHeight();
		int fontWidth = fm.stringWidth(w);
		//�ܸ�
		int bubbleHeight = fontHeight*sum;
		//�ܿ�
		int bubbleWidth;
		
		if(sum>1) {
			bubbleHeight *= 1.2;
			bubbleWidth = fm.stringWidth("������һ����ʮ���ְ�������һ����ʮ���ְ���ʮ�����");
		}
		else {
			bubbleWidth = fontWidth; 
		}
		
		JPanel tpanel = new JPanel();
		tpanel.setBounds(robotX, nextY, CHAT_BOATD_LENGTH, (int)(fontHeight+bubbleHeight));
		panel.add(tpanel);
		tpanel.setBackground(null);
		tpanel.setOpaque(false);
		tpanel.setLayout(null);
		nextY += fontHeight+bubbleHeight;
		
		JPanel say = new JPanel();
		tpanel.add(say);
		if(who == USER_SAY) {
			userLeftX = (int)(CHAT_BOATD_LENGTH-bubbleWidth-fm.stringWidth("Ү"));
			say.setBounds(userLeftX, 0, (int)(bubbleWidth+fm.stringWidth("Ү")), (int)(fontHeight+bubbleHeight));
			say.setBackground(userColor);
		}
		else {
			say.setBounds(0, 0, (int)(bubbleWidth+fm.stringWidth("Ү")), (int)(fontHeight+bubbleHeight));
			say.setBackground(robotColor);
		}
		say.setLayout(null);
		
		JLabel label = new JLabel(word);
		label.setBounds((int)(fm.stringWidth("Ү")*0.5), (int)(fontHeight*0.5), bubbleWidth,bubbleHeight);
		say.add(label);
		label.setFont(font);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		nextY += (int)(fontHeight);	
		
		panel.setPreferredSize(new Dimension(CHAT_BOATD_LENGTH,nextY));					
		scrollPane.repaint();
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
        vertical.setValue(vertical.getMaximum());
        vertical.setValue(vertical.getMaximum());
		
		
	}
	
	/**
	 * ��������������һ����Ƭ
	 * ע�⣺����ӵ�һ����Ƭ֮ǰ�����������������
	 * @param w:��һλΪ��ţ�����֮����ǰ�ť����
	 * */
	public static void addQuestion(String w) {
		if(wheatherFirstCard) {
			clearQuestionBox();
			wheatherFirstCard = false;
		}
		int no = w.charAt(0) - '0';
		w = w.substring(1);
		
		JComponent j = new JLabel();
		//��ȡ�ַ�����ȡ��߶�
		textField.setText("͵͵��");
		FontMetrics fm = j.getFontMetrics(textField.getFont());
		int fontHeight = fm.getHeight();
		int fontWidth = fm.stringWidth(w);
		
		JPanel card = new JPanel();
		card.setSize((int)(fontWidth+3*fm.stringWidth("Ү")), (int)(fontHeight*5));
		card.setBackground(questionColor);
		
		JLabel label = new JLabel(w);
		label.setSize(card.getWidth(), card.getHeight());
		card.add(label);
		label.setFont(font);
		label.setForeground(questionFontColor);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setText("");
		questionBox.add(card);
		questionBox.repaint();
		questionBox.setVisible(true);
		MainGUI.chatbox.setVisible(true);
		
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				card.setBackground(new Color(0,104,183));
				questionNO = no;
				Demo.answer = no;
				wheatherFirstCard = true;
				
				MessageBox m = new MessageBox(label.getText(),MessageBox.USER_BUBBLE);
				Demo.UserThread.userMessageBuffer.add(m);
//				addBubble(label.getText(),MessageBox.USER_BUBBLE);
				questionBox.setVisible(false);
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				card.setBackground(new Color(0,104,183));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				card.setBackground(questionColor);
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				card.setBackground(new Color(0,104,183));
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				card.setBackground(new Color(0,104,183));
			}
			
		});
	}
	
	/**
	 * ����������
	 * */
	public static void clearQuestionBox() {
		questionBox.setVisible(false);
		questionBox.removeAll();
		
		questionNO = -1;
		Demo.answer = -1;
	}

	/**
	 * Create the frame.
	 */
	public ChatGUI() {		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		//�����Դ������ð�ť��
		UIManager.put("RootPane.setupButtonVisible", false);
		
		//��������
		font = new Font("����", Font.BOLD, (int)(HEIGHT/60));		
		
		setSize((int)(CHAT_WIN_WIDTH*1.08), (int)(CHAT_WIN_HEIGHT*1.1));
		this.setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE );
		addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				wheatherFirst = false;
				MainGUI.moreLabel.setVisible(true);
			}
		});
		setResizable(false);

		//���뱳�����ñ���
		ImageIcon ii = new ImageIcon(MainGUI.class.getResource("/images/chat_bg.png"));
		float widthRate = (float)(1.0*CHAT_WIN_WIDTH/ii.getIconWidth());
		float heightRate = (float)(1.0*CHAT_WIN_HEIGHT/ii.getIconHeight());
		ii.setImage(ii.getImage().getScaledInstance(CHAT_WIN_WIDTH, CHAT_WIN_HEIGHT,Image.SCALE_SMOOTH ));
		
		ImageIcon hi = new ImageIcon(MainGUI.class.getResource("/images/chat_head.png"));
		hi.setImage(hi.getImage().getScaledInstance
				((int)(CHAT_WIN_WIDTH), (int)((CHAT_WIN_WIDTH)*hi.getIconHeight()/hi.getIconWidth()),Image.SCALE_SMOOTH ));
		headLabel.setBounds(0, 0, (int)(CHAT_WIN_WIDTH), (int)((CHAT_WIN_WIDTH)*hi.getIconHeight()/hi.getIconWidth()));
		contentPane.setLayout(null);
		headLabel.setIcon(hi);
		contentPane.add(headLabel);
		
		panel.setBounds(0, (int)(CHAT_WIN_HEIGHT*110/800), 
				CHAT_WIN_WIDTH-10, (int)(CHAT_WIN_HEIGHT*4/5));
		panel.setPreferredSize(new Dimension(CHAT_WIN_WIDTH, (int)(CHAT_WIN_HEIGHT*4/5)));
		panel.setBackground(null);
		panel.setOpaque(false);
		panel.setLayout(null);
		
		scrollPane = new JScrollPane(panel);
		scrollPane.setBounds(0, (int)(CHAT_WIN_HEIGHT*110/800), 
				CHAT_WIN_WIDTH-10, (int)(CHAT_WIN_HEIGHT*5/7));
		scrollPane.setBorder(null);
		scrollPane.setViewportBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		
		questionBox.setBounds(0, (int)(CHAT_WIN_HEIGHT*110/800+CHAT_WIN_HEIGHT*5/7+10), 
				(int)(CHAT_WIN_WIDTH),(int)(CHAT_WIN_HEIGHT*1/18) );
		contentPane.add(questionBox);
		questionBox.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		questionBox.setBackground(null);
		questionBox.setOpaque(false);
		
		ImageIcon tti = new ImageIcon(MainGUI.class.getResource("/images/chat_textarea.png"));
		tti.setImage(tti.getImage().getScaledInstance((int)(tti.getIconWidth()*widthRate), 
				(int)(tti.getIconHeight()*heightRate),Image.SCALE_SMOOTH ));
		JLabel textareaLabel = new JLabel("");
		textareaLabel.setHorizontalAlignment(SwingConstants.CENTER);
		textareaLabel.setBounds((int)(CHAT_WIN_WIDTH*22/600), 0, 
				(int)(CHAT_WIN_WIDTH*552/600),(int)(CHAT_WIN_HEIGHT*1/18) );
		textareaLabel.setIcon(tti);
		
		textareapanel.setBounds(0, (int)(CHAT_WIN_HEIGHT*110/800+CHAT_WIN_HEIGHT*5/7+10), 
				(int)(CHAT_WIN_WIDTH),(int)(CHAT_WIN_HEIGHT*1/18) );
		textareapanel.setBackground(null);
		textareapanel.setOpaque(false);
		contentPane.add(textareapanel);
		textareapanel.setLayout(null);
		textareapanel.setVisible(false);
		
		textField = new JTextField();
		textField.setForeground(Color.WHITE);
		textField.setFont(font);
		textField.setHorizontalAlignment(SwingConstants.LEFT);
		textField.setBounds((int)(CHAT_WIN_WIDTH*65/600), 0, 
				(int)(CHAT_WIN_WIDTH*468/600),(int)(CHAT_WIN_HEIGHT*1/24) );
		textField.setBackground(null);
		textField.setOpaque(false);
		//�Զ���ȡ����
		textField.requestFocus();

		textField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==10 
						&& textField.getText()!=null && !textField.getText().equals("") && textField.getText().length()!=0) {
					String words = textField.getText();
					if(!(words==null || words.length()==0 || words.equals(""))) {
						Demo.userText = words; 
						Demo.playSounds();
						addBubble(words,ChatGUI.USER_SAY);
//						MessageBox m = new MessageBox(words,MessageBox.USER_BUBBLE);
//						Demo.userThread.userMessageBuffer.add(m);
//						System.out.println(Demo.userText);
						
						
						textField.setText("");
						textareapanel.setVisible(false);
					}
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		textareapanel.add(textField);
		textField.setColumns(10);
		
		textareapanel.add(textareaLabel);
		contentPane.add(scrollPane);
		
		JLabel bgLabel = new JLabel(ii);
		bgLabel.setBounds(0, 0, CHAT_WIN_WIDTH, CHAT_WIN_HEIGHT);
		bgLabel.setIcon(ii);
		this.getContentPane().add(bgLabel);

	}

}
