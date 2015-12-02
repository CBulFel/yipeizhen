package yk.authentication.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yk.authentication.dao.UserDao;
import yk.authentication.domain.User;
import yk.base.ResultSetHandler;
import yk.util.JdbcTemplete;

public class UserDaoImpl implements UserDao{
	@Override
	public User findHostById(final String id) throws SQLException {
		String sql = "select name,wechat from authenticated_user where id=?";
		return (User)JdbcTemplete.query(new ResultSetHandler() {
			@Override
			public Object doHandler(ResultSet rs) throws SQLException {
				User u = null;
				if(rs.next()){
					u = new User();
					u.setId(id);
					u.setName(rs.getString(1));
					u.setWechat(rs.getString(2));
				}
				return u;
			}
		}, sql, id);
	}

	@Override
	public User findHostByWechat(final String wxid) throws SQLException {
		String sql = "select id,name,code,expdate,usetimes from authenticated_user where wechat=?";
		return (User)JdbcTemplete.query(new ResultSetHandler() {
			@Override
			public Object doHandler(ResultSet rs) throws SQLException {
				User u = null;
				if(rs.next()){
					u = new User();
					u.setId(rs.getString(1));
					u.setName(rs.getString(2));
					u.setCode(rs.getString(3));
					u.setExpdate(rs.getString(4));
					u.setUsetimes(rs.getInt(5));
					u.setWechat(wxid);
				}
				return u;
			}
		}, sql,wxid);
	}
	

	@Override
	public User findHostByCode(final String code) throws SQLException {
		String sql = "select id,name,wechat,expdate,usetimes from authenticated_user where code=?";
		return (User)JdbcTemplete.query(new ResultSetHandler() {
			@Override
			public Object doHandler(ResultSet rs) throws SQLException {
				User u = null;
				if(rs.next()){
					u = new User();
					u.setId(rs.getString(1));
					u.setName(rs.getString(2));
					u.setWechat(rs.getString(3));
					u.setExpdate(rs.getString(4));
					u.setUsetimes(rs.getInt(5));
					u.setCode(code);
				}
				return u;
			}
		}, sql,code);
	}
	
	@Override
	public boolean bindHostWechat(String id,String wxid) throws SQLException {
		String sql = "update authenticated_user set wechat=? where id=?";
		int res = JdbcTemplete.update(sql, wxid,id);
		if(res>0) return true;
		else return false;
	}

	@Override
	public boolean makeAppointment(String code, String name, String sex,String id,String tel,String time,String hospital, String department,String doctor,String disease)
			throws SQLException {
		String sql = "insert into appointments(code,name,sex,id,tel,time,hospital,department,doctor,disease) values(?,?,?,?,?,?,?,?,?,?)";
		int res = JdbcTemplete.update(sql,code,name,sex,id,tel,time,hospital,department,doctor,disease);
		if(res>0) return true;
		else return false;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAppointmentsByCode(final String code) throws SQLException {
		String sql = "select seq,name,sex,id,tel,time,hospital,department,doctor,disease from appointments where code=?";
		return (List<User>)JdbcTemplete.query(new ResultSetHandler() {
			@Override
			public Object doHandler(ResultSet rs) throws SQLException {
				List<User> users = new ArrayList<User>();
				User u = null;
				while(rs.next()){
					u = new User();
					u.setSeq(rs.getInt(1));
					u.setName(rs.getString(2));
					u.setSex(rs.getString(3));
					u.setId(rs.getString(4));
					u.setTel(rs.getString(5));
					u.setTime(rs.getString(6));
					u.setHospital(rs.getString(7));
					u.setDepartment(rs.getString(8));
					u.setDoctor(rs.getString(9));
					u.setDisease(rs.getString(10));
					u.setCode(code);
					users.add(u);
				}
				return users;
			}
		}, sql, code);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAppointmentsById(final String id) throws SQLException {
		String sql = "select code,name,sex,tel,time,hospital,department,doctor,disease from appointments where id=?";
		return (List<User>)JdbcTemplete.query(new ResultSetHandler() {
			@Override
			public Object doHandler(ResultSet rs) throws SQLException {
				List<User> users = new ArrayList<User>();
				User u = null;
				while(rs.next()){
					u = new User();
					u.setCode(rs.getString(1));
					u.setName(rs.getString(2));
					u.setSex(rs.getString(3));
					u.setTel(rs.getString(4));
					u.setTime(rs.getString(5));
					u.setHospital(rs.getString(6));
					u.setDepartment(rs.getString(7));
					u.setDoctor(rs.getString(8));
					u.setDisease(rs.getString(9));
					u.setId(id);
					users.add(u);
				}
				return users;
			}
		}, sql, id);
	}
	
	@Override
	public boolean addCode(String hostwxid, String code,String expdate) throws SQLException {
		String sql = "update authenticated_user set code=?,expdate=?,usetimes=? where wechat=?";
		int res = JdbcTemplete.update(sql, code,expdate,0,hostwxid);
		if(res>0) return true;
		else return false;
	}

	@Override
	public boolean setUseTimes(String hostwxid, int usetimes) throws SQLException {
		String sql = "update authenticated_user set usetimes=? where wechat=?";
		int res = JdbcTemplete.update(sql, usetimes,hostwxid);
		if(res>0) return true;
		else return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, String> findHospitals() throws SQLException {
	String sql = "select hospital,address from hospitals";
		return (HashMap<String, String>)JdbcTemplete.query(new ResultSetHandler() {
			@Override
			public Object doHandler(ResultSet rs) throws SQLException {
				HashMap<String,String> hospitals = new HashMap<String,String>();
				String h = null;
				String a = null;
				while(rs.next()){
					h = rs.getString(1);
					a = rs.getString(2);
					hospitals.put(h, a);
				}
				return hospitals;
			}
		}, sql);
	}

	@Override
	public boolean cancleAppointmentBySeq(int seq) throws SQLException {
		String sql = "delete from appointments where seq=?";
		int res = JdbcTemplete.update(sql, seq);
		if(res>0) return true;
		else return false;
	}

	@Override
	public String getRecipient() throws SQLException {
		String sql = "select address from recipient";
		return (String)JdbcTemplete.query(new ResultSetHandler() {
			@Override
			public Object doHandler(ResultSet rs) throws SQLException {
				if(rs.next()){
					return rs.getString(1);
				}
				else return "wanglj@vhealth.cn";//Ä¬ÈÏÍ¨ÖªÓÊÏä
			}
		}, sql);
	}

	@Override
	public boolean makeLog(String log, String time) throws SQLException {
		String sql = "insert into logs(log,time) values(?,?)";
		int res = JdbcTemplete.update(sql,log,time);
		if(res>0) return true;
		else return false;
	}

}
