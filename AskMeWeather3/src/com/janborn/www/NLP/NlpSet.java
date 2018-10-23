package com.janborn.www.NLP;

import java.util.ArrayList;
import java.util.List;

public class NlpSet {
	private String key = null;
	private List<String> list = new ArrayList<>();
	
	public NlpSet() {
	}
	/**
	 * 使用关键词实例化一个对象
	 * */
	public NlpSet(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<String> getList() {
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj==null) return false;
		if(getClass()!=obj.getClass()) return false;
		NlpSet d = (NlpSet)obj;
		return key.equals(d.getKey());
	}
}
