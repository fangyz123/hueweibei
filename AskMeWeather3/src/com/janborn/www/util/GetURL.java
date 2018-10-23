/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: GetURL.java
* ��URL��ȡJSONP�ַ�������Ҫһ��String���͵Ĳ���URL��ʵ��������
*
* @author Liu Hengren
    * @Date    2018-04-24
* @version 2.00
*/

package com.janborn.www.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetURL {
	/** Ŀ��URL */
	private String i_url;
	
	/**
	* ������
	*
	* @param url
	* @return none
	* @throws �쳣����.������� ע���Ӵ��෽�����׳��쳣��˵��
	*/
	public GetURL(String url) {
		// TODO Auto-generated constructor stub
		i_url=url;
	}
	
	/**
	* ��url��ȡString���͵�JSONP�ַ���
	*
	* @param none
	* @return String
	* @throws �쳣����.������� ע���Ӵ��෽�����׳��쳣��˵��
	*/
	public String getUrl() throws IOException {
		int responsecode;
		BufferedReader buffer=null;
        HttpURLConnection urlConnection=null; 
        StringBuffer bs = new StringBuffer();
	    String JsonStr = null;
	    try {
	    URL myurl = new URL(i_url);
	    urlConnection = (HttpURLConnection)myurl.openConnection();  
        responsecode=urlConnection.getResponseCode();
        if(responsecode==200){  
        	buffer=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8")); 
        	while((JsonStr=buffer.readLine())!=null){
    	        bs.append(JsonStr);	
    	        }
        	}
        else{  
        	buffer=new BufferedReader(new InputStreamReader(urlConnection.getErrorStream(),"utf-8"));
        	while((JsonStr=buffer.readLine())!=null){
        		bs.append(JsonStr);	
        		} 
        	}
        return bs.toString();
        }
	    catch(Exception e){
	    	throw new IOException("�����쳣");
	    	}
	    }	
}
