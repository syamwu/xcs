package com.xcs.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MapperUtil {

	public static boolean isExistColumn(ResultSet rs, String columnName) {
		try {
			if (rs.findColumn(columnName) > 0) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		}
		return false;
	}

	public static Integer getInt(ResultSet rs, String columnName) throws SQLException {
		return isExistColumn(rs, columnName) ? rs.getInt(columnName) : null;
	}

	public static String getString(ResultSet rs, String columnName) throws SQLException {
		return isExistColumn(rs, columnName) ? rs.getString(columnName) : null;
	}

	public static Timestamp getTimestamp(ResultSet rs, String columnName) throws SQLException {
		return isExistColumn(rs, columnName) ? rs.getTimestamp(columnName) : null;
	}

	public static Double getDouble(ResultSet rs, String columnName) throws SQLException {
		return isExistColumn(rs, columnName) ? rs.getDouble(columnName) : null;
	}

	public static String getInSQL(String ins, int type) {
		if (StringUtils.isBlank(ins)) {
			return "";
		}
		String result = "";
		try{
			String[] inarr = ins.split(",");
			for (int i = 0; i < inarr.length; i++) {
				if (type == 1) {
					try {
						if (i == inarr.length - 1) {
							result += Integer.parseInt(inarr[i]);
						} else {
							result += Integer.parseInt(inarr[i]) + ",";
						}
					} catch (Exception e) {
						//LoggerUtil.error(MapperUtil.class, e);
						return "";
					}
				}
				if (type == 2) {
					if (i == inarr.length - 1) {
						result += "'" + MapperUtil.sqlValidate(inarr[i]) + "'";
					} else {
						result += "'" + MapperUtil.sqlValidate(inarr[i]) + "',";
					}
	
				}
			}
		}catch(Exception e){
			//LoggerUtil.error(MapperUtil.class, e);
			return "";
		}
		return result;
	}
	
	public static String selectKey(String key) {
		if (key.indexOf("@") > 0) {
			String[] fields = key.split("@");
			String result = "";
			for (int i = 0; i < fields.length; i++) {
				if (i == 0) {
					result += selectKey(fields[i]);
				} else {
					result += "," + selectKey(fields[i]);
				}
			}
			return result;
		}else if (key.indexOf("tcaid") == 0) {
			return "tca.tcaid";
		} else if (key.indexOf("channelid") == 0) {
			return "tca.channelid";
		} else if (key.indexOf("pid") == 0) {
			return "tca.pid";
		} else if (key.indexOf("uid") == 0) {
			return "tca.uid";
		} else if (key.indexOf("upnum") == 0) {
			return "tca.upnum";
		} else if (key.indexOf("nickname") == 0) {
			return "tca.nickname";
		}else if (key.indexOf("sort") == 0) {
			return "tca.sort";
		} else if (key.indexOf("sort1") == 0) {
			return "tca.sort1";
		} else if (key.indexOf("sort2") == 0) {
			return "tca.sort2";
		}else if (key.indexOf("createtime") == 0) {
			return "tca.createtime";
		}else if (key.indexOf("modifytime") == 0) {
			return "tca.modifytime";
		}
		
		return "";
	}
	
	public static String sqlParams(Map<String, Object> params){
		String sql = "";
		Iterator<String> iterator = params.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			String value = params.get(key).toString();
			String selectkey = MapperUtil.sqlValidate(selectKey(key));
			if(!StringUtils.isBlank(selectkey) && !StringUtils.isBlank(value)){
				if (key.lastIndexOf("_li") == key.length() - 3) {
					//sql += " AND " + selectkey + " LIKE '%" + MapperUtil.sqlValidate(value) + "%' ";
					String[] fields = selectkey.split(",");
					for (int i = 0; i < fields.length; i++) {
						if (i == 0) {
							sql += " AND ( "+fields[i]+" LIKE '%" + MapperUtil.sqlValidate(value) + "%' ";
						}else if(i==fields.length-1){
							sql += " OR "+fields[i]+" LIKE '%" + MapperUtil.sqlValidate(value) + "%' )";
						}else{
							sql += " OR "+fields[i]+" LIKE '%" + MapperUtil.sqlValidate(value) + "%' ";
						}
					}
				} else if (key.length()>3 && key.lastIndexOf("_in") == key.length() - 3) {//in
					sql += " AND " + selectkey + " IN(" + MapperUtil.getInSQL(value, 1) + ") ";
				} else if (key.length()>5 && key.lastIndexOf("_noin") == key.length() - 5) {//not in
					sql += " AND " + selectkey + " NOT IN(" + MapperUtil.getInSQL(value, 1) + ") ";
				} else if (key.length()>5 && key.lastIndexOf("_noeq") == key.length() - 5) {//不等
					sql += " AND " + selectkey + "!='" + MapperUtil.sqlValidate(value) + "' ";
				} else if (key.length()>3 && key.lastIndexOf("_gt") == key.length() - 3) {//大于
					sql += " AND " + selectkey + ">'" + MapperUtil.sqlValidate(value) + "' ";
				} else if (key.length()>5 && key.lastIndexOf("_nogt") == key.length() - 5) {//小于
					sql += " AND " + selectkey + "<'" + MapperUtil.sqlValidate(value) + "' ";
				} else if (key.length()>4 && key.lastIndexOf("_egt") == key.length() - 4) {//大于等于
					sql += " AND " + selectkey + ">='" + MapperUtil.sqlValidate(value) + "' ";
				} else if (key.length()>6 && key.lastIndexOf("_noegt") == key.length() - 6) {//小于等于
					sql += " AND " + selectkey + "<='" + MapperUtil.sqlValidate(value) + "' ";
				} else {
					sql += " AND " + selectkey + "='" + MapperUtil.sqlValidate(value) + "' ";
				}
			}
		}
		return sql;
	}

	public static String sqlValidate(String str) {
		/*String str2 = str.toLowerCase();// 统一转为小写
		String[] SqlStr1 = { "exec", "execute", "insert", "select", "delete", "update", "drop", "chr", "mid", "master",
				"truncate", "char", "declare", "sitename", "net user", "xp_cmdshell", "exec", "execute", "insert",
				"drop", "grant", "group_concat", "column_name", "information_schema.columns", "table_schema", "union",
				"where", "select", "delete", "update", "count", "chr", "mid", "master", "truncate", "char", "declare" };// 词语
		String[] SqlStr2 = { "*", "'", ";", "-", "--", "+", "//", "/", "%", "#" };// 特殊字符

		for (int i = 0; i < SqlStr1.length; i++) {
			if (str2.indexOf(SqlStr1[i]) >= 0) {
				str = str.replaceAll("(?i)" + SqlStr1[i], "");// 正则替换词语，无视大小写
			}
		}
		for (int i = 0; i < SqlStr2.length; i++) {
			if (str2.indexOf(SqlStr2[i]) >= 0) {
				str = str.replaceAll("\\" + SqlStr2[i], "");
			}
		}*/
		return StringEscapeUtils.escapeJava(str);
	}

	public static boolean sqlValidateMatch(String str) {
		String str2 = str.toLowerCase();// 统一转为小写
		String[] SqlStr1 = { "exec", "execute", "insert", "select", "delete", "update", "drop", "chr", "mid", "master",
				"truncate", "char", "declare", "sitename", "net user", "xp_cmdshell", "exec", "execute", "insert",
				"create", "drop", "grant", "group_concat", "column_name", "information_schema.columns", "table_schema",
				"union", "where", "select", "delete", "update", "order", "count", "chr", "mid", "master", "truncate",
				"char", "declare" };// 词语
		String[] SqlStr2 = { "*", "'", ";", "-", "--", "+", "//", "/", "%", "#" };// 特殊字符

		for (int i = 0; i < SqlStr1.length; i++) {
			if (str2.indexOf(SqlStr1[i]) >= 0) {
				if (str.contains(SqlStr1[i])) {
					return true;
				}
			}
		}
		for (int i = 0; i < SqlStr2.length; i++) {
			if (str2.indexOf(SqlStr2[i]) >= 0) {
				if (str.contains(SqlStr2[i])) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isNull(@SuppressWarnings("rawtypes") List list) {
		if (list == null) {
			return true;
		}
		if (list.isEmpty()) {
			return true;
		}
		return false;
	}

	public static int[] getSQLTypes(Object[] obj) {
		int[] types = new int[obj.length];
		for (int i = 0; i < obj.length; i++){
			if(obj[i] instanceof Integer){
				types[i] = java.sql.Types.INTEGER;
				continue;
			}
			if(obj[i] instanceof String){
				types[i] = java.sql.Types.VARCHAR;
				continue;
			}
			if(obj[i] instanceof Timestamp){
				types[i] = java.sql.Types.TIMESTAMP;
				continue;
			}
			if(obj[i] instanceof Double){
				types[i] = java.sql.Types.DOUBLE;
				continue;
			}
		}
		return types;
	}
	
	public static Object getListMapContent(List<Map<String,Object>> rows, int index, String fieldName){
		try{
			if(isNull(rows)){
				return null;
			}else{
				return rows.get(index).get(fieldName);
			}
		}catch(Exception e){
			//LoggerUtil.error(MapperUtil.class, e);
		}
		return null;
	}
	
	public static String getVideoUrl(String head, String videoimgurl){
		return head + videoimgurl.substring(2, videoimgurl.lastIndexOf(".")) + ".mp4";
	}
	
/*	public static String getOrderAndLimit(ListPage listPage){
		String sql="";
		if(!StringUtils.isBlank(listPage.getOrderBy())){
			sql += listPage.getOrderBy();
		}else{
			sql += "";
		}
		sql += " LIMIT " + listPage.getStart() + "," + listPage.getPageSize();
		return sql;
	}*/
	
	@SuppressWarnings("rawtypes")
	public static int getMapInt(Map map,String key){
		if(map.get(key)==null){
			return 0;
		}
		return Integer.parseInt(map.get(key).toString());
	}
	
	@SuppressWarnings("rawtypes")
	public static Integer getMapInteger(Map map,String key){
		if(map.get(key)==null){
			return null;
		}
		return Integer.parseInt(map.get(key).toString());
	}
	
	@SuppressWarnings("rawtypes")
	public static double getMapDouble(Map map,String key){
		if(map.get(key)==null){
			return 0;
		}
		return Double.parseDouble(map.get(key).toString());
	}
	
	@SuppressWarnings("rawtypes")
	public static String getMapString(Map map,String key){
		if(map.get(key)==null){
			return "";
		}
		return MapperUtil.sqlValidate(map.get(key).toString());
	}
	
	@SuppressWarnings("rawtypes")
	public static Timestamp getMapTimeStamp(Map map,String key){
		if(map.get(key)==null){
			return null;
		}
		return Timestamp.valueOf(map.get(key).toString());
	}
	
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	public static Map getXMLMap(String xmlstr){
		try {
			DocumentBuilderFactory dbfactory=DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuild; 
			dbuild = dbfactory.newDocumentBuilder();
			Document doc=dbuild.parse(new ByteArrayInputStream(xmlstr.getBytes()));
			NodeList nodeList = doc.getChildNodes();			
			Node node = nodeList.item(0).getFirstChild();
			Map<String, String> params=new HashMap<String,String>();
			while(node!=null){
				params.put(node.getNodeName(), node.getTextContent());
				node = node.getNextSibling();
			}
			return params;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		String ids = "1,3,2,34,5";
		String sql = "SELECT * FROM t_product WHERE state=1 AND id IN (" + MapperUtil.getInSQL(ids, 1) + ")";
		System.out.println(sql);
	}
}
