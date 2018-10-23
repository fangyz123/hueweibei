/**
* Team Zhinengxianfeng Hebei Normal University
* FileName: GetWeather.java
* 根据需求查询天气数据
*
* @author Liu Hengren
    * @Date    2018-04-24
* @version 2.00
*/

package com.janborn.www.weather;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.janborn.www.util.GetURL;

import java.security.SignatureException;
import java.util.Date;
import java.net.URLEncoder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class GetWeather {
	/** 设置API信息 */
	private final String TIANQI_DAILY_WEATHER_URL = "https://api.seniverse.com/v3/weather/daily.json";
	private final String TIANQI_NOW_WEATHER_URL = "https://api.seniverse.com/v3/weather/now.json";
	private final String TIANQI_LIFE_SUGGESTRION_URL = "https://api.seniverse.com/v3/life/suggestion.json";
	/** 设置KEY和ID */
	private final String TIANQI_API_SECRET_KEY = "vxigji7i0ua4bqzw"; //
	private final String TIANQI_API_USER_ID = "UFB08DAFBA"; //
	/** 保存传输的信息 */
	private String i_location;
	private String i_language;
	private String i_unit;
	private String i_start;
	private String i_end;
	
	private String jsonString;
	private String jsonUrl;

	
	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;//修改
	}

	/**
	 * constructor for now weather
	 * 
	 * @param location
	 * @param language
	 * @param unit
	 * @return none
	 * @throws IOException 
	 */
	public GetWeather(String location, String language, String unit) throws IOException {
		if(location==null) location="";
		if(language==null) language="";
		if(unit==null) unit="";
		this.i_location=location;
		this.i_language=language;
		this.i_unit=unit;
		try {
			generateGetNowWeatherURL();
		} catch (SignatureException | UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	/**
	 * constructor for diary weather
	 * 
	 * @param location
	 * @param language
	 * @param unit
	 * @param start
	 * @param end
	 * @return none
	 * @throws IOException 
	 */
	public GetWeather(String location, String start, String end, String language, String unit) throws IOException {
		if(location==null) location="";
		if(language==null) language="";
		if(unit==null) unit="";
		this.i_location=location;
		this.i_language=language;
		this.i_unit=unit;
		this.i_start=start;
		this.i_end=end;
		try {
			generateGetDiaryWeatherURL();
		} catch (SignatureException | UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	/**
	 * Generate the URL to get life suggestion
	 * 
	 * @param location
	 * @param language
	 * @return none
	 * @throws IOException 
	 */
	public GetWeather(String location, String language) throws IOException {
		if(language==null) language="";
		if(location==null) location="";
		this.i_location=location;
		this.i_language=language;
		try {
			getLifeSuggestionURL();
		} catch (SignatureException | UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	/**
	 * Generate HmacSHA1 signature with given data string and key
	 * 
	 * @param data
	 * @param key
	 * @return String
	 * @throws SignatureException
	 */
	private String generateSignature(String data, String key) throws SignatureException {
		String result;
		try {
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));
			result = new sun.misc.BASE64Encoder().encode(rawHmac);
		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}

	/**
	 * Generate the URL to get now weather
	 * 
	 * @param location
	 * @param language
	 * @param unit
	 * @return none
	 * @throws IOException 
	 */
	
	public void generateGetNowWeatherURL() throws SignatureException, IOException{
		String timestamp = String.valueOf(new Date().getTime());
		String params = "ts=" + timestamp + "&ttl=30&uid=" + TIANQI_API_USER_ID;
        String signature = URLEncoder.encode(generateSignature(params, TIANQI_API_SECRET_KEY), "UTF-8");
        this.jsonUrl=TIANQI_NOW_WEATHER_URL + "?" + params + "&sig=" + signature + "&location=" + URLEncoder.encode( this.i_location,"utf-8" ) + "&language=" + this.i_language + "&unit=" + this.i_unit ;
        String temp = new String();
        GetURL getURL = new GetURL(this.jsonUrl);
		temp=getURL.getUrl();
		this.setJsonString(temp);		
	}

	/**
	 * Generate the URL to get diary weather
	 * 
	 * @param location
	 * @param language
	 * @param unit
	 * @param start
	 * @param end
	 * @return none
	 * @throws SignatureException 
	 * @throws IOException 
	 */
	public void generateGetDiaryWeatherURL()throws  SignatureException, IOException {
		String timestamp = String.valueOf(new Date().getTime());
		String params = "ts=" + timestamp + "&ttl=30&uid=" + TIANQI_API_USER_ID;
        String signature = URLEncoder.encode(generateSignature(params, TIANQI_API_SECRET_KEY), "UTF-8");
        this.jsonUrl=TIANQI_DAILY_WEATHER_URL + "?" + params + "&sig=" + signature + "&location=" + URLEncoder.encode( this.i_location,"utf-8" ) + "&language=" + this.i_language + "&unit=" + this.i_unit + "&start=" + this.i_start + "&days=" + this.i_end;
        String temp = new String();
        GetURL getURL = new GetURL(this.jsonUrl);
		temp=getURL.getUrl();
		this.setJsonString(temp);
	}

	/**
	 * Generate the URL to get life suggestion
	 * 
	 * @param location
	 * @param language
	 * @return none
	 * @throws IOException 
	 */
	public void getLifeSuggestionURL() throws SignatureException, IOException{
		String timestamp = String.valueOf(new Date().getTime());
		String params = "ts=" + timestamp + "&ttl=30&uid=" + TIANQI_API_USER_ID;
        String signature = URLEncoder.encode(generateSignature(params, TIANQI_API_SECRET_KEY), "UTF-8");
        this.jsonUrl=TIANQI_LIFE_SUGGESTRION_URL + "?" + params + "&sig=" + signature + "&location=" + URLEncoder.encode( this.i_location,"utf-8" ) + "&language=" + this.i_language;
        String temp = new String();
        GetURL getURL = new GetURL(this.jsonUrl);
		temp=getURL.getUrl();
		this.setJsonString(temp);
	}

}
