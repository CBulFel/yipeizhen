package yk.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * 数据库操作工具类
 * @author joyxee
 *
 */
public class DBUtil {
	//数据库连接地址
	private static String URL ;
	//用户名
	private static String USERNAME ;
	//密码
	private static String PASSWORD ;
	//jdbc驱动
	private static String DRIVER ;
	//指定数据库配置文件
	private static ResourceBundle db_rb ;
	
	//使用静态块加载驱动程序
	static{
		db_rb = ResourceBundle.getBundle("yk.util.bae-db-config");
		URL = db_rb.getString("jdbc.url");
		USERNAME = db_rb.getString("jdbc.username");
		PASSWORD = db_rb.getString("jdbc.password");
		DRIVER = db_rb.getString("jdbc.driver");
//		URL = URL+"?useUnicode=true&characterEncoding=utf8";//连接openshift数据库需要指定连接编码
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 连接数据库
	 * @return
	 */
	public static Connection getConnection(){
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("获取连接失败");
		}
		return conn;
	}
	
	/**
	 * 关闭数据库连接
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
