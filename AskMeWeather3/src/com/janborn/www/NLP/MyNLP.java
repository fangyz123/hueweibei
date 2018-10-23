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
	/*分词结果集*/
	private List<Letter> set = new ArrayList<>();
	/*时间*/
	private List<NlpSet> sDate = new ArrayList<>();
	/*生活*/
	private List<NlpSet> sLife = new ArrayList<>();
	/*表示同意*/
	private List<NlpSet> sAgree = new ArrayList<>();
	/*详细天气信息*/
	private List<NlpSet> sDetil = new ArrayList<>();
	/*语句-->需求*/
	private Demand demand = null;
	/*行政地区表地址*/
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
	 * 初始化天气详情的词集
	 * */
	public void initDetil() {
		NlpSet s1 = new NlpSet("全部");
		s1.getList().add("都");
		
		NlpSet s2 = new NlpSet("降雨率");
		s2.getList().add("雨");
		
		NlpSet s3 = new NlpSet("温差");
		s3.getList().add("差");
		
		NlpSet s4 = new NlpSet("晚上天气");
		s4.getList().add("晚");
		s4.getList().add("夜");
		
		sDetil.add(s1);
		sDetil.add(s3);
		sDetil.add(s2);
		sDetil.add(s4);
	}
	
	/**
	 * 分词，并提取一个关键词转化为需求
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
	 * 提取句子中所有的生活指数关键词
	 * */
	public void detilNlp(String w) {
		while(true) {
			boolean f = false;
			for(NlpSet s:sDetil) {
				boolean tf = false;
				//若包含关键词
				if(w.indexOf(s.getKey())!=-1) {
					tf = true;
					if(w.indexOf(s.getKey())+s.getKey().length() < w.length())
						w = w.substring(0, w.indexOf(s.getKey())) 
						+ w.substring(w.indexOf(s.getKey())+s.getKey().length());
					else
						w = w.substring(0, w.indexOf(s.getKey()));
				}
				//若包含与关键词同等词意的词
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
	 * 初始化表示同意的词集
	 * */
	public void initAgree() {
		NlpSet agree = new NlpSet("同意");
		agree.getList().add("嗯");
		agree.getList().add("en");
		agree.getList().add("哦");
		agree.getList().add("噢");
		agree.getList().add("o");
		agree.getList().add("O");
		agree.getList().add("想");
		agree.getList().add("告诉");
		agree.getList().add("是");
		agree.getList().add("对");
		agree.getList().add("行");
		agree.getList().add("好");
		agree.getList().add("可以");
		agree.getList().add("ok");
		agree.getList().add("OK");
		agree.getList().add("oK");
		agree.getList().add("Ok");
		agree.getList().add("没问题");
		sAgree.add(agree);
	}
	
	/**
	 * 判断是否含有表示同意的词
	 * */
	public boolean agreeNlp(String w) {
		boolean returns = false;
		for(NlpSet s:sAgree) {
			boolean tf = false;
			//若包含关键词
			if(w.indexOf(s.getKey())!=-1)
				returns = true;
			//若包含与关键词同等词意的词
			else {
				for(String str : s.getList())
					if(w.indexOf(str)!=-1)
						returns = true;
			}
		}
		return returns;
	}

	/**
	 * 包括“今天”、“明天”、“后天”以及所有时间写法
	 * */
	public void initDate() {
		//标准写法
		NlpSet today = new NlpSet("今天");
		NlpSet tomo = new NlpSet("明天");
		NlpSet thedaya = new NlpSet("后天");
		
		//非标准写法
		//设置近三天日期词表
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
		NlpSet s1 = new NlpSet("穿着");
		s1.getList().add("衣");
		s1.getList().add("穿");
		s1.getList().add("着装");
		
		NlpSet s2 = new NlpSet("洗车");
		s2.getList().add("车");
		
		NlpSet s3 = new NlpSet("流感");
		s3.getList().add("感冒");
		
		NlpSet s4 = new NlpSet("运动");
		s4.getList().add("锻炼");
		
		NlpSet s5 = new NlpSet("旅行");
		s5.getList().add("旅游");
		s5.getList().add("玩");
		
		NlpSet s6 = new NlpSet("紫外线");
		s6.getList().add("晒");
		s6.getList().add("皮肤");
		
		sLife.add(s1);
		sLife.add(s2);
		sLife.add(s3);
		sLife.add(s4);
		sLife.add(s5);
		sLife.add(s6);
		
	}
	
	/**
	 * 提取时间关键词
	 * <ul>
	 * 	<li>-1 ：超出范围</li>
	 *  <li>1：成功</li>
	 *  <li>-2：请求中包含多个时间关键词</li>
	 * </ul>
	 * @param String
	 * */
	public int dateNlp(String w) {
		int returns = -1;
		while(w.length()!=0 && !w.equals("") && w!=null) {
			boolean f = true;
			if(		w.indexOf("今天")!= -1
				||	w.indexOf("明天")!= -1
				||	w.indexOf("后天")!= -1) {
				Letter t = new Letter();
				String s = "";
				if(w.indexOf("今天")!= -1)	s = "今天";
				if(w.indexOf("明天")!= -1)	s = "明天";
				if(w.indexOf("后天")!= -1)	s = "后天";
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
				//表达式在原串中的结束位置‘=’标准化串
				String tw = standarizeRex(w);
				if (tw.equals("TooMuch"))
					returns = -2;
				else if(!tw.equals("Can'tFind")) {
					//结束位置
					int endpos = Integer.parseInt(tw.substring(0,tw.indexOf("="))); 
					String neww = tw.substring(tw.indexOf("=")+1);
					w = w.substring(endpos);
					//进行比对
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
		String regex = "([0-9]{0,4}[./\\\\-年]){0,1}([0-9]{1,2}[./\\\\-月]){0,1}[^0-9]*[0-9]{1,2}[号]{0,1}"; 
		String input = w;
		String regex2 = "[0-9]{1,4}";
		String input2;
		Pattern p = Pattern.compile(regex); 
		Matcher m = p.matcher(input); // 获取 matcher 对象 

		/*日期的个数*/
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
			
			//将日期格式化为yyyy.MM.dd
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
	 * 提取句子中所有的生活指数关键词
	 * */
	public void lifeNlp(String w) {
		while(true) {
			boolean f = false;
			for(NlpSet s:sLife) {
				boolean tf = false;
				//若包含关键词
				if(w.indexOf(s.getKey())!=-1) {
					tf = true;
					if(w.indexOf(s.getKey())+s.getKey().length() < w.length())
						w = w.substring(0, w.indexOf(s.getKey())) 
						+ w.substring(w.indexOf(s.getKey())+s.getKey().length());
					else
						w = w.substring(0, w.indexOf(s.getKey()));
				}
				//若包含与关键词同等词意的词
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
	 * 分词，并提取一个关键词转化为需求
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
	 * 分词，并提取一个关键词转化为需求
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
	 * 判断目标位置是否支持查询
	 * <p>用于检查目标位置是否支持查询。目标位置通过参数传入。</p>
	 * <p>通过在官方提供的地点Excel表中检索匹配，来判断目标地点是否支持。</p>
	 * 
	 * @param String 目标地点
	 * @return int (1,2,-1)
	 * <ul>
	 * 	<li>返回值<i>1</i>表示该地点支持，且原始字串以“省”字结尾</li>
	 * 	<li>返回值<i>2</i>表示该地点支持，且已在原始字串后加上了“省”字。</li>
	 * 	<li>返回值<i>-1</i>表示该地点不支持</li>
	 * </ul>
	 */
	public static int judgeLocation(String location)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		InputStream inputStream = new FileInputStream(LOC_FILE_PATH);
		Workbook workbook = null;
		workbook = WorkbookFactory.create(inputStream);
		inputStream.close();
		// 工作表对象
		Sheet sheet = workbook.getSheetAt(0);
		// 总行数
		int rowLength = sheet.getLastRowNum() + 1;
		// 工作表的列
		Row row = sheet.getRow(0);
		// 总列数
		int colLength = row.getLastCellNum();
		// 得到指定的单元格
		Cell cell = row.getCell(0);
		for (int i = 0; i < colLength; i++) {
			//不要第一行：无效的
			for (int j = 1; j < rowLength; j++) {
				row = sheet.getRow(j);
				cell = row.getCell(i);
				// 得到单元格的内容
				String s = cell.getStringCellValue();
				if (s=="") {
					continue;
				}
				if (location.indexOf(s) != -1) {
					if(i==colLength-1) {
						//将地点转换为省+省会
						// 工作表对象
						Sheet sheet1 = workbook.getSheetAt(1);
						// 总行数
						int rowLength1 = sheet1.getLastRowNum() + 1;
						for (int k = 1; k < rowLength1; k++) {
							Row row1 = sheet1.getRow(k);
							Cell cell1 = row1.getCell(0);
							String loc=cell1.getStringCellValue();
							if (loc.equals(s)) {
								if(Demo.location.charAt(Demo.location.length()-1)=='省')
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

		// 工作表对象
		Sheet sheet = workbook.getSheetAt(0);
		// 总行数
		int rowLength = sheet.getLastRowNum() + 1;
		// 工作表的列
		Row row = sheet.getRow(0);
		// 总列数
		int colLength = row.getLastCellNum();
		// 得到指定的单元格
		Cell cell = row.getCell(0);

		int i;
		for (i = 0; i < colLength; i++) {
			// 不要第一行：无效的
			for (int j = 1; j < rowLength; j++) {
				row = sheet.getRow(j);
				cell = row.getCell(i);
				// 得到单元格的内容
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
