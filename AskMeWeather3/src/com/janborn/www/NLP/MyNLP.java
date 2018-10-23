package com.janborn.www.NLP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.janborn.www.Demo;

public class MyNLP {
	/*�ִʽ����*/
	private List<Letter> set = new ArrayList<>();
	/*ʱ��*/
	private List<NlpSet> sDate = new ArrayList<>();
	/*����*/
	private List<NlpSet> sLife = new ArrayList<>();
	/*��ʾͬ��*/
	private List<NlpSet> sAgree = new ArrayList<>();
	/*��ϸ������Ϣ*/
	private List<NlpSet> sDetil = new ArrayList<>();
	/*���-->����*/
	private Demand demand = null;
	/*�����������ַ*/
	private final static String LOC_FILE_PATH = System.getProperty("user.dir")+"\\resrc\\ChineseCities.xlsx";
	private final static File LOC_FILE = new File(LOC_FILE_PATH);

	public MyNLP() {
		initDate();
		initLife();
		initAgree();
	}
	
	public MyNLP(Letter letter) {		
		String l = letter.getLetter();
		String pos = letter.getPos();
		
		switch(pos) {
		case "LOC":
			placeNlp(l);
			break;
		case "T":
			initDate();
			getDate(l);
			break;
		case "L":
			initLife();
			getLife(l);
			break;
		case "D":
			initDetil();
			getDetil(l);
			break;
		}
	}
	
	public Demand getDemand() {
		return demand;
	}

	public void setDemand(Demand demand) {
		this.demand = demand;
	}
	
	/**
	 * ��ʼ����������Ĵʼ�
	 * */
	public void initDetil() {
		NlpSet s1 = new NlpSet("ȫ��");
		s1.getList().add("��");
		
		NlpSet s2 = new NlpSet("������");
		s2.getList().add("��");
		
		NlpSet s3 = new NlpSet("�²�");
		s3.getList().add("��");
		
		NlpSet s4 = new NlpSet("��������");
		s4.getList().add("��");
		s4.getList().add("ҹ");
		
		sDetil.add(s1);
		sDetil.add(s3);
		sDetil.add(s2);
		sDetil.add(s4);
	}
	
	/**
	 * �ִʣ�����ȡһ���ؼ���ת��Ϊ����
	 * */
	public void getDetil(String l) {
		detilNlp(l);
		String detil = null;
		for(Letter letter :set) {
			if(letter.getPos()=="D") {
				detil = letter.getLetter();
				break;
			}
		}
		if(detil!=null)
			demand = new Demand(detil,getDetilNum(detil));
	}
	
	public int getDetilNum(String detil) {
		int returns = -1;
		for(NlpSet s :sDetil) {
			if(detil.equals(s.getKey()))
				returns = sDetil.indexOf(s);
		}
		return returns;
	}
	
	/**
	 * ��ȡ���������е�����ָ���ؼ���
	 * */
	public void detilNlp(String w) {
		while(true) {
			boolean f = false;
			for(NlpSet s:sDetil) {
				boolean tf = false;
				//�������ؼ���
				if(w.indexOf(s.getKey())!=-1) {
					tf = true;
					if(w.indexOf(s.getKey())+s.getKey().length() < w.length())
						w = w.substring(0, w.indexOf(s.getKey())) 
						+ w.substring(w.indexOf(s.getKey())+s.getKey().length());
					else
						w = w.substring(0, w.indexOf(s.getKey()));
				}
				//��������ؼ���ͬ�ȴ���Ĵ�
				else {
					for(String str : s.getList())
						if(w.indexOf(str)!=-1) {
							tf = true;
							if(w.indexOf(str)+str.length() < w.length())
								w = w.substring(0, w.indexOf(str)) 
								+ w.substring(w.indexOf(str)+str.length());
							else
								w = w.substring(0, w.indexOf(str));
							break;
						}
				}
				if(tf){
					f = true;
					Letter t = new Letter();
					t.setLetter(s.getKey());
					t.setPos("D");
					set.add(t);
					
					
					break;
				}
					
			}
			if(!f) break;
		}
	}
	
	/**
	 * ��ʼ����ʾͬ��Ĵʼ�
	 * */
	public void initAgree() {
		NlpSet agree = new NlpSet("ͬ��");
		agree.getList().add("��");
		agree.getList().add("en");
		agree.getList().add("Ŷ");
		agree.getList().add("��");
		agree.getList().add("o");
		agree.getList().add("O");
		agree.getList().add("��");
		agree.getList().add("����");
		agree.getList().add("��");
		agree.getList().add("��");
		agree.getList().add("��");
		agree.getList().add("��");
		agree.getList().add("����");
		agree.getList().add("ok");
		agree.getList().add("OK");
		agree.getList().add("oK");
		agree.getList().add("Ok");
		agree.getList().add("û����");
		sAgree.add(agree);
	}
	
	/**
	 * �ж��Ƿ��б�ʾͬ��Ĵ�
	 * */
	public boolean agreeNlp(String w) {
		boolean returns = false;
		for(NlpSet s:sAgree) {
			boolean tf = false;
			//�������ؼ���
			if(w.indexOf(s.getKey())!=-1)
				returns = true;
			//��������ؼ���ͬ�ȴ���Ĵ�
			else {
				for(String str : s.getList())
					if(w.indexOf(str)!=-1)
						returns = true;
			}
		}
		return returns;
	}

	/**
	 * ���������족�������족�������족�Լ�����ʱ��д��
	 * */
	public void initDate() {
		//��׼д��
		NlpSet today = new NlpSet("����");
		NlpSet tomo = new NlpSet("����");
		NlpSet thedaya = new NlpSet("����");
		
		//�Ǳ�׼д��
		//���ý��������ڴʱ�
		SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		
		String todaydate  = day.format(date);
		today.getList().add(todaydate);
		
		date.setTime(date.getTime()+24*60*60*1000);
		String tomodate  = day.format(date);
		tomo.getList().add(tomodate);
		
		date.setTime(date.getTime()+24*60*60*1000);
		String dbtomo = day.format(date);
		thedaya.getList().add(dbtomo);
		
		sDate.add(today);
		sDate.add(tomo);
		sDate.add(thedaya);
	}
	
	public void initLife() {
		NlpSet s1 = new NlpSet("����");
		s1.getList().add("��");
		s1.getList().add("��");
		s1.getList().add("��װ");
		
		NlpSet s2 = new NlpSet("ϴ��");
		s2.getList().add("��");
		
		NlpSet s3 = new NlpSet("����");
		s3.getList().add("��ð");
		
		NlpSet s4 = new NlpSet("�˶�");
		s4.getList().add("����");
		
		NlpSet s5 = new NlpSet("����");
		s5.getList().add("����");
		s5.getList().add("��");
		
		NlpSet s6 = new NlpSet("������");
		s6.getList().add("ɹ");
		s6.getList().add("Ƥ��");
		
		sLife.add(s1);
		sLife.add(s2);
		sLife.add(s3);
		sLife.add(s4);
		sLife.add(s5);
		sLife.add(s6);
		
	}
	
	/**
	 * ��ȡʱ��ؼ���
	 * <ul>
	 * 	<li>-1 ��������Χ</li>
	 *  <li>1���ɹ�</li>
	 *  <li>-2�������а������ʱ��ؼ���</li>
	 * </ul>
	 * @param String
	 * */
	public int dateNlp(String w) {
		int returns = -1;
		while(w.length()!=0 && !w.equals("") && w!=null) {
			boolean f = true;
			if(		w.indexOf("����")!= -1
				||	w.indexOf("����")!= -1
				||	w.indexOf("����")!= -1) {
				Letter t = new Letter();
				String s = "";
				if(w.indexOf("����")!= -1)	s = "����";
				if(w.indexOf("����")!= -1)	s = "����";
				if(w.indexOf("����")!= -1)	s = "����";
				t.setLetter(s);
				t.setPos("T");
				set.add(t);
				
				if(w.indexOf(s)+s.length() < w.length())
					w = w.substring(0, w.indexOf(s)) 
					+ w.substring(w.indexOf(s)+s.length());
				else
					w = w.substring(0, w.indexOf(s));
				
				returns = 1;
				f = false;
			}
			else {
				//���ʽ��ԭ���еĽ���λ�á�=����׼����
				String tw = standarizeRex(w);
				if (tw.equals("TooMuch"))
					returns = -2;
				else if(!tw.equals("Can'tFind")) {
					//����λ��
					int endpos = Integer.parseInt(tw.substring(0,tw.indexOf("="))); 
					String neww = tw.substring(tw.indexOf("=")+1);
					w = w.substring(endpos);
					//���бȶ�
					int len = 0;
					for(NlpSet s : sDate) {
						len++;
						List<String> list = s.getList();
						if(list.indexOf(neww)!=-1) {
							Letter t = new Letter();
							t.setLetter(s.getKey());
							t.setPos("T");
							set.add(t);
							
							returns = 1;
							f = false;
							break;
						}
					}
					if(len>sDate.size())
						returns = -1;
					else {
						Letter t = new Letter();
						t.setLetter(neww);
						t.setPos("T");
						set.add(t);
						
						returns = 1;
						f = false;
					}
				}
			}
			if(f) break;
		}
		return returns;
	}
	
	public String standarizeRex(String w) {
		String regex = "([0-9]{0,4}[./\\\\-��]){0,1}([0-9]{1,2}[./\\\\-��]){0,1}[^0-9]*[0-9]{1,2}[��]{0,1}"; 
		String input = w;
		String regex2 = "[0-9]{1,4}";
		String input2;
		Pattern p = Pattern.compile(regex); 
		Matcher m = p.matcher(input); // ��ȡ matcher ���� 

		/*���ڵĸ���*/
		int sum = 0;
		while(m.find()) {
			sum++;
			if(sum > 1) return "TooMuch";
			String date = "";

			int s = m.start();
			int e = m.end();

			input2 = input.substring(s, e);
			
			Pattern p2 = Pattern.compile(regex2); 
			Matcher m2 = p2.matcher(input2);
			int count = 0;
			while(m2.find()) {
				if(!date.equals("")) { 
					date = date + "-";
					count++;
				}
				int s2 = m2.start();
				int e2 = m2.end();
				date += input2.substring(s2, e2);
			}
			
			//�����ڸ�ʽ��Ϊyyyy.MM.dd
			Date d = new Date();
			SimpleDateFormat year = new SimpleDateFormat("yyyy");
			SimpleDateFormat month = new SimpleDateFormat("MM");
			String y = year.format(d);
			String mon = month.format(d);
			if(count==1) {
				date = y + "-" + date;
			}
			if(count==0) {
				date = y + "-" + mon +"-" + date; 
			}
				
			String standDate = "";
			for (String str: date.split("-")){
				if(str.length()<2)
					str = "0" + str;
				standDate += str;
	        }
			return e + "=" + standDate;
		} 
		return "Can'tFind";
	}

	/**
	 * ��ȡ���������е�����ָ���ؼ���
	 * */
	public void lifeNlp(String w) {
		while(true) {
			boolean f = false;
			for(NlpSet s:sLife) {
				boolean tf = false;
				//�������ؼ���
				if(w.indexOf(s.getKey())!=-1) {
					tf = true;
					if(w.indexOf(s.getKey())+s.getKey().length() < w.length())
						w = w.substring(0, w.indexOf(s.getKey())) 
						+ w.substring(w.indexOf(s.getKey())+s.getKey().length());
					else
						w = w.substring(0, w.indexOf(s.getKey()));
				}
				//��������ؼ���ͬ�ȴ���Ĵ�
				else {
					for(String str : s.getList())
						if(w.indexOf(str)!=-1) {
							tf = true;
							if(w.indexOf(str)+str.length() < w.length())
								w = w.substring(0, w.indexOf(str)) 
								+ w.substring(w.indexOf(str)+str.length());
							else
								w = w.substring(0, w.indexOf(str));
							break;
						}
				}
				if(tf){
					f = true;
					Letter t = new Letter();
					t.setLetter(s.getKey());
					t.setPos("L");
					set.add(t);
					
					
					break;
				}
					
			}
			if(!f) break;
		}
	}
	
	public String getLocaction() {
		String location = null;
		for(Letter letter :set) {
			if(letter.getPos()=="LOC") {
				location = letter.getLetter();
				break;
			}
		}
		return location;
	}
	
	/**
	 * �ִʣ�����ȡһ���ؼ���ת��Ϊ����
	 * */
	public void getDate(String l) {
		int i = dateNlp(l);
		if(i>=0) {
			String date = null;
			for(Letter letter :set) {
				if(letter.getPos()=="T") {
					date = letter.getLetter();
					break;
				}
			}
			demand = new Demand(date,getDateNum(date));
		}
		else if(i==-1){
			demand = new Demand(null,-1);
		}
		else if(i==-2) {
			demand = new Demand(null,-2);
		}
	}
	
	public int getDateNum(String date) {
		int returns = -1;
		for(NlpSet s :sDate) {
			if(date.equals(s.getKey())) {
				returns = sDate.indexOf(s);
				break;
			}
		}
		return returns;
	}
	
	/**
	 * �ִʣ�����ȡһ���ؼ���ת��Ϊ����
	 * */
	public void getLife(String l) {
		lifeNlp(l);
		String life = null;
		for(Letter letter :set) {
			if(letter.getPos()=="L") {
				life = letter.getLetter();
				break;
			}
		}
		if(life!=null)
			demand = new Demand(life,getLifeNum(life));
	}
	
	public int getLifeNum(String life) {
		int returns = -1;
		for(NlpSet s :sLife) {
			if(life.equals(s.getKey()))
				returns = sLife.indexOf(s);
		}
		return returns;
	}
	
	/**
	 * �ж�Ŀ��λ���Ƿ�֧�ֲ�ѯ
	 * <p>���ڼ��Ŀ��λ���Ƿ�֧�ֲ�ѯ��Ŀ��λ��ͨ���������롣</p>
	 * <p>ͨ���ڹٷ��ṩ�ĵص�Excel���м���ƥ�䣬���ж�Ŀ��ص��Ƿ�֧�֡�</p>
	 * 
	 * @param String Ŀ��ص�
	 * @return int (1,2,-1)
	 * <ul>
	 * 	<li>����ֵ<i>1</i>��ʾ�õص�֧�֣���ԭʼ�ִ��ԡ�ʡ���ֽ�β</li>
	 * 	<li>����ֵ<i>2</i>��ʾ�õص�֧�֣�������ԭʼ�ִ�������ˡ�ʡ���֡�</li>
	 * 	<li>����ֵ<i>-1</i>��ʾ�õص㲻֧��</li>
	 * </ul>
	 */
	public static int judgeLocation(String location)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		InputStream inputStream = new FileInputStream(LOC_FILE_PATH);
		Workbook workbook = null;
		workbook = WorkbookFactory.create(inputStream);
		inputStream.close();
		// ���������
		Sheet sheet = workbook.getSheetAt(0);
		// ������
		int rowLength = sheet.getLastRowNum() + 1;
		// ���������
		Row row = sheet.getRow(0);
		// ������
		int colLength = row.getLastCellNum();
		// �õ�ָ���ĵ�Ԫ��
		Cell cell = row.getCell(0);
		for (int i = 0; i < colLength; i++) {
			//��Ҫ��һ�У���Ч��
			for (int j = 1; j < rowLength; j++) {
				row = sheet.getRow(j);
				cell = row.getCell(i);
				// �õ���Ԫ�������
				String s = cell.getStringCellValue();
				if (s=="") {
					continue;
				}
				if (location.indexOf(s) != -1) {
					if(i==colLength-1) {
						//���ص�ת��Ϊʡ+ʡ��
						// ���������
						Sheet sheet1 = workbook.getSheetAt(1);
						// ������
						int rowLength1 = sheet1.getLastRowNum() + 1;
						for (int k = 1; k < rowLength1; k++) {
							Row row1 = sheet1.getRow(k);
							Cell cell1 = row1.getCell(0);
							String loc=cell1.getStringCellValue();
							if (loc.equals(s)) {
								if(Demo.location.charAt(Demo.location.length()-1)=='ʡ')
									Demo.location=Demo.location.substring(0,Demo.location.length()-1)
													+row1.getCell(1).getStringCellValue();
								else
									Demo.location=Demo.location+row1.getCell(1).getStringCellValue();
								return 2;
							}
						}
					}
					return 1;
				}
			}
		}
		return -1;
	}
	
	public void placeNlp(String w) {
		InputStream inputStream;
		Workbook workbook = null;
		try {
			inputStream = new FileInputStream(LOC_FILE);
			workbook = WorkbookFactory.create(inputStream);
			inputStream.close();
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// ���������
		Sheet sheet = workbook.getSheetAt(0);
		// ������
		int rowLength = sheet.getLastRowNum() + 1;
		// ���������
		Row row = sheet.getRow(0);
		// ������
		int colLength = row.getLastCellNum();
		// �õ�ָ���ĵ�Ԫ��
		Cell cell = row.getCell(0);

		int i;
		for (i = 0; i < colLength; i++) {
			// ��Ҫ��һ�У���Ч��
			for (int j = 1; j < rowLength; j++) {
				row = sheet.getRow(j);
				cell = row.getCell(i);
				// �õ���Ԫ�������
				if(cell == null)
					break;
				String is = cell.getStringCellValue();
				if (is == "") {
					break;
				}

				if (w.indexOf(is) != -1) {
					Letter t = new Letter();
					t.setLetter(is);
					t.setPos("LOC");
					set.add(t);
				}
			}
		}
	}

}
