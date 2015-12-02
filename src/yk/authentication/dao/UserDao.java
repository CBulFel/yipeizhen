package yk.authentication.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import yk.authentication.domain.User;

public interface UserDao {
	public User findHostById(String id) throws SQLException;
	public User findHostByWechat(String wxid) throws SQLException;
	public User findHostByCode(String code) throws SQLException;
	public boolean bindHostWechat(String id,String wxid) throws SQLException;
	public boolean makeAppointment(String code, String name, String sex,String id,String tel,String time,String hospital, String department,String doctor,String disease) throws SQLException;
	public List<User> findAppointmentsByCode(String code) throws SQLException; 
	public List<User> findAppointmentsById(String id) throws SQLException;
	public boolean addCode(String hostwxid,String code,String expdate) throws SQLException;
	public boolean setUseTimes(String hostwxid,int usetimes) throws SQLException;
	public HashMap<String,String> findHospitals() throws SQLException;
	public boolean cancleAppointmentBySeq(int seq) throws SQLException;
	public String getRecipient() throws SQLException;
	public boolean makeLog(String log,String time) throws SQLException;
}
