package yk.process.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Faq {
	//使用simsim_API
	public static String post1(String q,String uid) {
		String strURL;
		StringBuilder contentBuf = new StringBuilder();
		try {
			strURL = "http://www.simsimi.com/requestChat?lc=ch&ft=1.0&req="
					+ URLEncoder.encode(q,"utf-8")
					+ "&uid="+uid;
			URL url = new URL(strURL);  
		    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();  
		    InputStreamReader input = new InputStreamReader(httpConn  
		            .getInputStream(), "utf-8");
		    BufferedReader bufReader = new BufferedReader(input);  
		    String line = "";  	      
		    while ((line = bufReader.readLine()) != null) {  
		        contentBuf.append(line);
		    }
		    bufReader.close();
		    input.close();
		    httpConn.disconnect();
		    //{"res":"..."}
		    int Start = contentBuf.indexOf("msg")+6;
		    int End = contentBuf.lastIndexOf("\"}");
		    return filterLast1(contentBuf.substring(Start, End-1));
		    
		}catch (IOException e) {
			return "[调皮]";
		}//catch	    
	}	
	private static String filterLast1(String m){
		if(m.indexOf("I HAVE NO RESPONSE")>=0)
			return "[微笑]";
		//过滤simsim的黄色信息
		if(m.indexOf("咪咪")>=0||m.indexOf("鸡巴")>=0||m.indexOf("操你")>=0||m.indexOf("插你")>=0||m.indexOf("约炮")>=0) 
			return "[磕头]";
		m = m.replaceAll("simsim","芷韵");
		m = m.replaceAll("小黄鸡","芷韵");
		m = m.replaceAll("主人","你");
		return m;
	}	
}

