package com.xcs.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest { 
	public static void main(String[] args) {
		String str1 = "http://www.66ba.com.cn/";
		String regex1="^(http://|https://)(\\w{1,}\\.)*(66ba)\\.(com)\\.(cn)/";
		System.out.println(str1.matches(regex1));
		
		
		String str2 = "fasgas_vasgfasfasf.jpggasf";
		String regex2 = "(_v)(.*)(\\.jpg|\\.png)";
		Pattern pattern = Pattern.compile(regex2);
        Matcher matcher = pattern.matcher(str2);//匹配类
        while (matcher.find()) {
        	System.out.println(matcher.group());
        }
        
        String str3 = "video标签之前<video src=\"http://image.66ba.com.cn/portal/c35ff3a9227b41fe95d8d5ceb8854771.jpg\">  video标签里面</video>  img标签前<img src=\"http://image.66ba.com.cn/portal/c35ff3a9227b41fe95d8d5ceb8854771.jpg\">  img标签前<img src=\"http://image.66ba.com.cn/portal/c35ff3a9227b41fe95d8d5ceb8854771.jpg\"/>  img标签前<img src=\"http://image.66ba.com.cn/portal/c35ff3a9227b41fe95d8d5ceb8854771.jpg\">  img标签前</img>  video标签前面<video src=\"http://image.66ba.com.cn/portal/c35ff3a9227b41fe95d8d5ceb8854771.jpg\">  video标签里面</video>结束";
        String regex3 = "<img(.*?>)|<video(.*?/>|.*?</video>)|</img>|</video>";
       // String regex3 = "(?(exp)yes|no)";
        String newstr3 = str3.replaceAll(regex3, "");
        System.out.println(newstr3);
	}
}
