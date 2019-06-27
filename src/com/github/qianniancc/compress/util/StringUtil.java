package com.github.qianniancc.compress.util;

public class StringUtil {
	
	public static boolean equals(String str,String... ends){
		for(String end:ends){
			if(str.equals(end))return true;
		}
		return false;
	}
	
	public static boolean endsWith(String str,String... ends){
		for(String end:ends){
			if(str.endsWith(end))return true;
		}
		return false;
	}

}
