package com.xcs.utils;

public enum ContentType {
	
	NULL(""),
	JSON(".json"),
	IMAGE(".jpg"),
	TEXT(".txt");
	
	public String suffix;
	
	public String suffix(){
		return this.suffix;
	}
	
	ContentType(String suffix){
		this.suffix = suffix;
	}
}
