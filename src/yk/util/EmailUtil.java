package yk.util;

import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import yk.email.MailSender;

/**
 * �������������
 * @author joyxee
 *
 */
public class EmailUtil {
	//�������������
	private static MailSender server;
	//ָ�����������ļ�
	private static ResourceBundle email_rb ;
	//�ʼ�����
	private static String subject;
	//�ʼ�����
	private static String content;
	//�ռ���
	private static String recipient;
	
	//ʹ�ü�������
	static{
		email_rb = ResourceBundle.getBundle("yk.util.email-config");
		server = new MailSender(email_rb.getString("server.address"),email_rb.getString("server.password"),"smtp.exmail.qq.com");
	}
	
	/**
	 * �����ʼ�ǰ��׼��
	 * @param r recipient �����ʼ���ַ
	 * @param s subject �ʼ�����
	 * @param c content �ʼ�����
	 */
	public static void sendPrepare(String r,String s,String c){
		recipient = r;
		subject = s;
		content = c;
	}
	
	/**
	 * �����ʼ�
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
