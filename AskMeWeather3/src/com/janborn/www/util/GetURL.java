/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: GetURL.java
* 从URL获取JSONP字符串。需要一个String类型的参数URL来实例化对象。
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
	/** 目标URL */
	private String i_url;
	
	/**
	* 构造器
	*
	* @param url
	* @return none
	* @throws 异常类型.错误代码 注明从此类方法中抛出异常的说明
	*/
	public GetURL(String url) {
		// TODO Auto-generated constructor stub
		i_url=url;
	}
	
	/**
	* 从url获取String类型的JSONP字符串
	*
	* @param none
	* @return String
	* @throws 异常类型.错误代码 注明从此类方法中抛出异常的说明
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
	    	throw new IOException("网络异常");
	    	}
	    }	
}
