package com.zyd.xf.publish;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.xcs.utils.HttpUtils;
import com.xcs.utils.StreamUtils;

public class PublishProgram {
	public static void main(String[] args) {

		int abc = 1;

		// HttpUtils.locationCookie=
		// "JSESSIONID=088B129C48B0342B8A786940BF2E7D49";
		// HttpUtils.sendPostXF("http://xf.zhiyd.com.cn/programList/loadDeviceGroupDataList?programListId=42888&deviceGroupName=&deviceGroupType=&currentPage=1&pageSize=10");

		System.out.println(new Timestamp(System.currentTimeMillis()).toString().substring(0, 19));
		if (abc == 1) {
			 //return;
		}

		Map<String, Object> resMap;

		String reJson;
		boolean useFileSession = false;
		File file = new File(PublishProgram.class.getResource("/com/zyd/xf/publish/session").getPath());
		if (file.exists()) {
			String session = StreamUtils.readTxtFile(file.getAbsolutePath());
			System.out.println(session);
			System.out.println();
			System.out.println("检测当前已经存在登录信息，是否使用当前登录session进行操作(y/n):");
			Scanner scanner = new Scanner(System.in);
			String yn = scanner.next();
			if(yn.equalsIgnoreCase("y")){
				useFileSession = true;
				HttpUtils.locationCookie = session;
			}
		}

		if (!useFileSession) {

			/* 获取验证码 */
			HttpUtils.requestContentType = "text/html;charset:utf-8;";
			resMap = HttpUtils.sendURLToInputStream("http://passport.luckshow.cn/getcode");
			System.out.println();
			System.out.println("请输入图片验证码(目录:" + HttpUtils.filedir + "):");
			Scanner scanner = new Scanner(System.in);

			String picCode = scanner.next();
			/*
			 * String username = "zhangxueling"; String password = "a123456";
			 */

			String username = "bazyd66";
			String password = "y@2016";

			HttpUtils.locationCookie = HttpUtils.getHeaderFieldedOne(resMap, "Set-Cookie");

			/* 验证验证码 */
			resMap = HttpUtils.sendGetXF("http://passport.luckshow.cn/getcode?ac=" + picCode);

			/* 登录，获取tk */
			HttpUtils.requestContentType = "application/x-www-form-urlencoded; charset=UTF-8";
			resMap = HttpUtils.sendPostXF("http://passport.luckshow.cn/login?username=" + username + "&password="
					+ password + "&authcode=" + picCode + "&service=http://xf.zhiyd.com.cn/sso", "");

			HttpUtils.requestContentType = "text/html;charset:utf-8;";

			/* 获取session */
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			HttpUtils.putListString("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8", map);
			HttpUtils.putListString("Accept-Encoding", "gzip, deflate, sdch", map);
			HttpUtils.putListString("Accept-Language", "zh-CN,zh;q=0.8", map);
			HttpUtils.putListString("Connection", "keep-alive", map);
			HttpUtils.putListString("Host", "xf.zhiyd.com.cn", map);
			HttpUtils.putListString("Upgrade-Insecure-Requests", "1", map);
			HttpUtils.putListString("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36",
					map);
			String ticket = HttpUtils.getHeaderFielded(resMap, "ticket");
			if (ticket != null && !ticket.trim().equals("")) {
				System.out.println("登录成功,获取到令牌(ticket):" + ticket);
			} else {
				System.out.println("登录失败");
				return;
			}
			resMap = HttpUtils.sendGetXF("http://xf.zhiyd.com.cn/sso?ticket=" + ticket, map, false);

			/* 插入session */
			HttpUtils.locationCookie = HttpUtils.getCookie(resMap, "JSESSIONID");
			if (HttpUtils.locationCookie != null && !HttpUtils.locationCookie.trim().equals("")) {
				System.out.println("验证令牌成功，准备执行操作.....");
			} else {
				System.out.println("验证令牌失败");
				return;
			}
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/* 操作前验证 */
		resMap = HttpUtils.sendGetXF("http://xf.zhiyd.com.cn/main/mainController/goMain");

		Map<String, List<String>> mapthis = new HashMap<String, List<String>>();
		HttpUtils.putListString("Accept", "application/json, text/javascript, */*; q=0.01", mapthis);
		HttpUtils.putListString("Accept-Encoding", "gzip, deflate", mapthis);
		HttpUtils.putListString("Accept-Language", "zh-CN,zh;q=0.8", mapthis);
		HttpUtils.putListString("Connection", "keep-alive", mapthis);
		HttpUtils.putListString("Referer", "http://xf.zhiyd.com.cn/showIndex.html", mapthis);
		HttpUtils.putListString("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36",
				mapthis);
		HttpUtils.putListString("Cookie", HttpUtils.locationCookie, mapthis);

		// resMap
		// =sendPostXF("http://xf.zhiyd.com.cn/show/company/getUserRoleMenu?r=0.22520841482007925",mapthis,"");
		 //resMap = HttpUtils.sendGetXF("http://xf.zhiyd.com.cn/user/showUserController/getSessionUser?r=0.22520841482007925",true);
		
		
		//resMap = HttpUtils.sendPostXF("http://xf.zhiyd.com.cn/user/showUserController/getCompList?r=0.22520841482007925", mapthis, "", true);
		
		
		//reJson = (String) resMap.get("body");
		//System.out.println(JSONObject.parseObject(reJson));
		// resMap
		// =sendPostXF("http://xf.zhiyd.com.cn/user/showUserController/verifyUserLogin?r=0.22520841482007925",mapthis,"");
		// resMap
		// =sendPostXF("http://xf.zhiyd.com.cn/user/showUserController/getUserObj",mapthis,"");
		// resMap
		// =sendPostXF("http://xf.zhiyd.com.cn/main/mainController/getStatInfo",mapthis,"");
		// resMap
		// =sendPostXF("http://xf.zhiyd.com.cn/show/deviceOnline/getDeviceOnlineStat",mapthis,"");
		// Map<String, List<String>> mapthis = new HashMap<String,
		// List<String>>();
		// putListString("Accept", "application/json, text/javascript, */*;
		// q=0.01", mapthis);
		// putListString("Accept-Encoding", "gzip, deflate", mapthis);
		// putListString("Accept-Language", "zh-CN,zh;q=0.8", mapthis);
		// putListString("Content-Type",
		// "application/x-www-form-urlencoded;charset=UTF-8", mapthis);
		// putListString("Cookie", locationCookie, mapthis);
		// programList/publishProgramListToDeviceGroup

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 //resMap =  HttpUtils.sendPostXF("http://xf.zhiyd.com.cn/programList/publishProgramListToDeviceGroup?programListId=75175&groupIds=13464%23B,13461%23B,13405%23B,13394%23B,13384%23B,13319%23B,13370%23B,13368%23B,13354%23B,13352%23B,13351%23B&days=2017-04-11",mapthis,null,true);
		// resMap =  HttpUtils.sendPostXF("http://xf.zhiyd.com.cn/program/programList/publishProgramListToDeviceGroup?programId=75175&deviceGroupIds=13464#B&days=2017-04-11",mapthis,null,true);
		//resMap = HttpUtils.sendPostXF(
		//		"http://xf.zhiyd.com.cn/programList/loadDeviceGroupDataList?programListId=74007&deviceGroupName=&deviceGroupType=&currentPage=1&pageSize=10",
		//		false);

	}
	
	

}
