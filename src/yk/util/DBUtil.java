package yk.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * ���ݿ����������
 * @author joyxee
 *
 */
public class DBUtil {
	//���ݿ����ӵ�ַ
	private static String URL ;
	//�û���
	private static String USERNAME ;
	//����
	private static String PASSWORD ;
	//jdbc����
	private static String DRIVER ;
	//ָ�����ݿ������ļ�
	private static ResourceBundle db_rb ;
	
	//ʹ�þ�̬�������������
	static{
		db_rb = ResourceBundle.getBundle("yk.util.bae-db-config");
		URL = db_rb.getString("jdbc.url");
		USERNAME = db_rb.getString("jdbc.username");
		PASSWORD = db_rb.getString("jdbc.password");
		DRIVER = db_rb.getString("jdbc.driver");
//		URL = URL+"?useUnicode=true&characterEncoding=utf8";//����openshift���ݿ���Ҫָ�����ӱ���
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �������ݿ�
	 * @return
	 */
	public static Connection getConnection(){
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("��ȡ����ʧ��");
		}
		return conn;
	}
	
	/**
	 * �ر����ݿ�����
	 * @param rs
	 * @param stat
	 * @param conn
	 */
	public static void close(ResultSet rs,Statement stat,Connection conn){
		try {
			if(rs!=null) rs.close();
			if(stat!=null) stat.close();
			if(conn!=null) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
