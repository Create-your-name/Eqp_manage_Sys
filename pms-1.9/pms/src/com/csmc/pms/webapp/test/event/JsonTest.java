/*
 * $Id: Test.java,v 1.1 2006/04/15 14:40:06 platform Exp $
 * Created on 2006-4-15
 */
package com.csmc.pms.webapp.test.event;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public class JsonTest {

	public static void main(String[] args) throws Exception{
		JSONObject test11=new JSONObject();
		test11.put("name","保证课巡检");
		test11.put("description","保证课巡检");
		test11.put("balance",new Double(1000.21));
		test11.put("is_vip",new Boolean(true));
		test11.put("nickname",null);
		  System.out.print(test11);
		  
		  JSONObject test22=new JSONObject();
		  test22.put("phone","123456");
		  test22.put("zip","7890");
		  test11.put("contact",test22);
		  System.out.print(test11);

		  JSONObject test1 = new JSONObject();
		  test1.put("text", "testtest");
		  test1.put("date", "October 29th, 2006 9:21pm");
		  test1.put("link", "http://www.jackslocum.com/");
		  test1.put("author", "Jack Slocum");
		  test1.put("id", "10");
		  
		  JSONArray test3 = new JSONArray();
		  test3.put(test1);
		
		JSONObject test2 = new JSONObject();
		test2.put("Comment", test3);
		System.out.println(test2);
		
		JSONArray array1=new JSONArray();
		array1.put("abc\u0010a/");
		array1.put(new Integer(123));
		array1.put(new Double(122.22));
		array1.put(new Boolean(true));
		System.out.println("======array1==========");
		System.out.println(array1);
		System.out.println();
		
		JSONObject obj1=new JSONObject();
		obj1.put("name","fang");
		obj1.put("age",new Integer(27));
		obj1.put("is_developer",new Boolean(true));
		obj1.put("weight",new Double(60.21));
		obj1.put("array1",array1);
		System.out.println();
		
		System.out.println("======obj1 with array1===========");
		System.out.println(obj1);
		System.out.println();
		
		obj1.remove("array1");
		array1.put(obj1);
		System.out.println("======array1 with obj1========");
		System.out.println(array1);
		System.out.println();
		
		System.out.println("======parse to java========");

		/**String s="[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]";
		Object obj=JSONValue.parse(s);
		JSONArray array=(JSONArray)obj;
		System.out.println("======the 2nd element of array======");
		System.out.println(array.get(1));
		System.out.println();
		
		JSONObject obj2=(JSONObject)array.get(1);
		System.out.println("======field \"1\"==========");
		System.out.println(obj2.get("1"));**/	
		
	}
}
