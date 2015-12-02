package yk.util;

import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import yk.email.MailSender;

/**
 * 邮箱操作工具类
 * @author joyxee
 *
 */
public class EmailUtil {
	//服务器邮箱对象
	private static MailSender server;
	//指定邮箱配置文件
	private static ResourceBundle email_rb ;
	//邮件主题
	private static String subject;
	//邮件内容
	private static String content;
	//收件人
	private static String recipient;
	
	//使用加载配置
	static{
		email_rb = ResourceBundle.getBundle("yk.util.email-config");
		server = new MailSender(email_rb.getString("server.address"),email_rb.getString("server.password"),"smtp.exmail.qq.com");
	}
	
	/**
	 * 发送邮件前的准备
	 * @param r recipient 接收邮件地址
	 * @param s subject 邮件主题
	 * @param c content 邮件内容
	 */
	public static void sendPrepare(String r,String s,String c){
		recipient = r;
		subject = s;
		content = c;
	}
	
	/**
	 * 发送邮件
	 */
	public  static void send(){
		try {
			server.send(recipient, subject, content);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
