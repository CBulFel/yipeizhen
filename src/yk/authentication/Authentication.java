package yk.authentication;

import java.awt.EventQueue;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import yk.authentication.dao.UserDao;
import yk.authentication.dao.impl.UserDaoImpl;
import yk.authentication.domain.User;
import yk.util.EmailUtil;

public class Authentication {
	private static UserDao udao = new UserDaoImpl();
	
	//��֤�û�����΢���˺�
	public static String authenticate(String wxid,String content){	
		try {
			User u = udao.findHostByWechat(wxid);
			if(u!=null)
				return "����΢�ź��Ѱ���֤�û�"+u.getName()+"(֤��β��"+u.getId().substring(12)+")���粻�Ǳ��˲���������ϵ������Ա���ظ���3���ɲ鿴�����֤��Ϣ";
			int i = content.lastIndexOf("#");
			if(i<=1)
				return "��ʽ�������顣�ٸ����Ӱɣ����ϻظ���#����#445323197001014226";
			String name = content.substring( 1 , i );
			String id = content.substring(i+1);
			if(name==null||name.length()<1||id==null||id.length()<18)
				return "��ʽ�������顣�ٸ����Ӱɣ����ϻظ���#����#445323197001014226";
			u = udao.findHostById(id);
			if(u==null) 
				return "��֤ʧ�ܣ���������֤��"+name+"(֤��β��"+id.substring(12)+")�����ڱ���˾Ա������л���Ĺ�ע";
			if(!u.getName().equals(name)) 
				return "��֤ʧ�ܣ���������ƴд";
			if(u.getWechat()!=null&&u.getWechat().length()>0) 
				return "��֤ʧ�ܣ�"+name+"(֤��β��"+id.substring(12)+")��ͨ������΢�ź���֤���粻�������˲���������ϵ������Ա";
			
			//�������ϣ����а�΢�ţ���������Կ
			if(udao.bindHostWechat(id, wxid)==true){
				String code = generateCode(wxid);
				return "��֤�ɹ����װ���"+name+"��"+code
						+"\n<a href=\"http://yipeizhen.duapp.com/YKWX/appointment.html?code="+getCodeByWechat(wxid)+"\">����˴�ԤԼ����</a>"
						+ "\n(��ܰ��ʾ��ԤԼ����ǰ������ԤԼ�Һ�)";
			}
			else return "��֤ʧ�ܣ�����ϵ������Ա";
		} catch (SQLException e) {
			e.printStackTrace();
			return "ϵͳ��æ001�����Ժ�����";
		}
	}	
	
	//�鿴�û���Ϣ
	public static String myStatus(String wxid){
		try {
			User u =udao.findHostByWechat(wxid);
			if(u==null)
				return "�𾴵Ŀͻ�������δ��֤�û���";
			StringBuffer sb = new StringBuffer();
			sb.append("�𾴵�").append(u.getName()).append("\n");
			sb.append("״̬������֤�û�\ue335\n֤���ţ�").append(u.getId().substring(0, 4));
			sb.append("**********").append(u.getId().substring(14));
			sb.append("\n\ue03f������Կ��\n");
			if(u.getCode()==null||u.getCode().length()<6){
				sb.append("��δ���ɣ��ظ���������Կ������ԤԼ������Կ\ue011");
			}else{
				sb.append("\ue23a").append(u.getCode()).append("\ue23b\n");
				sb.append("ʣ��ʹ�ô�����").append(4-u.getUsetimes()).append("\n");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date datetemp = formatter.parse(u.getExpdate());
				formatter = new SimpleDateFormat("yyyy��MM��dd��HH:mm:ss");
				sb.append("��Ч����").append(formatter.format(datetemp));		
			}
			return sb.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return "ϵͳ��æ004�����Ժ�����";
		} catch (ParseException e) {
			e.printStackTrace();
			return "ϵͳ��æ0042�����Ժ�����";
		}
	}
	
	//��֤�û��Լ�ӵ����Կ�Ŀͻ�ԤԼ����
	//return
	//		-1 : ��������ȷ
	//		0 : ��Կ����ȷ
	//		1 : ��Կʹ�ô�������4��
	//		2 : ��Կ����
	//		3��4��5 : �쳣
	//		6 : �ύ�ɹ�
	public static int makeAppointment(String code, String name, String sex,String id,String tel,String time,String hospital, String department,String doctor,String disease){
		try {	
			if(code==null||code.length()<=0||name==null||name.length()<=0||sex==null||sex.length()!=1
					||id==null||id.length()!=18||tel==null||tel.length()!=11||hospital==null||hospital.length()<=0
					||department==null||department.length()<=0||doctor==null||doctor.length()<=0
					||disease==null||disease.length()<=0||time==null||time.length()<=0)
				return -1;
			User u = udao.findHostByCode(code);
			if(u==null)
				return 0;				
			if(u.getUsetimes()>=4)
				return 1;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date expDate = formatter.parse(u.getExpdate());
			java.util.Date currentDate =  new java.util.Date();
			if(currentDate.after(expDate))
				return 2;
				
			if(udao.makeAppointment(code,name,sex,id,tel,time,hospital,department,doctor,disease)==true){
				//������Կʹ�ô���
				udao.setUseTimes(u.getWechat(), u.getUsetimes()+1);
				//�����ʼ�֪ͨ
				StringBuffer sb = new StringBuffer();
				sb.append("������").append(name);
				sb.append("\n �Ա�").append(sex);
				sb.append("\n ���֤�ţ�").append(id);
				sb.append("\n ��ϵ�绰��").append(tel);
				sb.append("\n ԤԼ����ʱ�䣺").append(time);
				sb.append("\n ԤԼҽԺ��").append(hospital);
				sb.append("\n ԤԼ���ң�").append(department);
				sb.append("\n ԤԼҽ����").append(doctor);
				sb.append("\n ���Ｒ����").append(disease);
				EmailUtil.sendPrepare(udao.getRecipient(), "New appointment", sb.toString());
				EventQueue.invokeLater(new Runnable(){
					public void run(){
						EmailUtil.send();
					}
				});
				return 6;
			}
			else return 3;
		} catch (SQLException e) {
			e.printStackTrace();
			return 4;
		} catch (ParseException e) {
			e.printStackTrace();
			return 5;
		}
	}
	
	//��֤�û�ȡ������
	public static String cancleAppointment(String wxid,String content){
		try {
			User u = udao.findHostByWechat(wxid);
			if(u==null){
				return "��Ǹ��ֻ����֤�û�����ȡ������Կ����������";
			}
			content = content.replaceAll("x", "X");
			content = content.substring(content.indexOf("X")+1);
			if(!content.matches("\\d+"))
				return "ȡ��������ʽ������ȷ��ʽӦΪ��QX�����š�Ӣ��Ϊ��д��ĸ��������Ϊ����������������ԡ�";
			int seq = Integer.parseInt(content);
			List<User> appointments = udao.findAppointmentsByCode(u.getCode());
			if(appointments.size()==0)
				return "������Կû��ԤԼ���ﶩ��";
			for (User uu : appointments) {
				if(uu.getSeq()==seq){
					
					//����ǰһ��20��00ǰ����ȡ������
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy��MM��dd");
					java.util.Date atime = formatter.parse(uu.getTime().substring(0,uu.getTime().indexOf("��")));
					Calendar ctime = Calendar.getInstance();
					ctime.setTime(atime);
					ctime.add(Calendar.DAY_OF_MONTH, -1);//ǰһ��
					ctime.set(Calendar.HOUR_OF_DAY,20);//���ϰ˵�
					Calendar now = Calendar.getInstance();
					if(!now.before(ctime))//����ʱ�䲻��ԤԼʱ���ֹǰ
						return "��Ǹ���˶����Ѳ���ȡ����ȡ�����������ھ���ǰһ������8��ǰ����ȡ����";
					
					boolean res = udao.cancleAppointmentBySeq(seq);
					if(res==true){
						//�ع�ʹ�ô���
						udao.setUseTimes(u.getWechat(), u.getUsetimes()-1);
						//�����ʼ�֪ͨ
						StringBuffer sb = new StringBuffer();
						sb.append("ȡ������\n").append(" ������").append(uu.getName());
						sb.append("\n ���֤�ţ�").append(uu.getId());
						sb.append("\n ��ϵ�绰��").append(uu.getTel());
						sb.append("\n ԤԼ����ҽԺ��").append(uu.getHospital());
						sb.append("\n ԤԼ����ʱ�䣺").append(uu.getTime());
						EmailUtil.sendPrepare(udao.getRecipient(), "Cancel appointment", sb.toString());
						EventQueue.invokeLater(new Runnable(){
							public void run(){
								EmailUtil.send();
							}
						});
						//д��־
						java.util.Date time = new java.util.Date();
						SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String timeString = formatter2.format(time);
						udao.makeLog("ȡ��������"+uu.getName()+"����"+uu.getHospital()+"����"+uu.getTime()+"��", timeString);
						
						return "�ѳɹ�ȡ��ԤԼ���ﶩ����"+content+"������л����ʹ�á�";
					}else return "ϵͳ��æ0082�����Ժ�����";
				}
			}
			return "ȡ������ʧ�ܣ�������Ϊ��"+content+"���Ķ�������������Կ������ԤԼ����޷�ȡ��";
		} catch (SQLException e) {
			e.printStackTrace();
			return "ϵͳ��æ008�����Ժ�����";
		} catch (ParseException e1) {
			e1.printStackTrace();
			return "ϵͳ��æ0082�����Ժ�����";
		}
	}
	
	//��֤�û���ѯԤԼ��Ϣ
	public static String myAppointments(String wxid){
		try {
			User u = udao.findHostByWechat(wxid);
			if(u==null)
				return "����֤�û���ظ�����ѯԤԼ@���֤�š���ѯ����ԤԼ������Ϣ";
			List<User> appointments = udao.findAppointmentsByCode(u.getCode());
			if(appointments.size()==0)
				return "��ѯ��������ԤԼ������Ϣ";
			StringBuffer sb = new StringBuffer();
			sb.append("�𾴵�").append(u.getName()).append("��ʹ�����Ĵ�����ԿԤԼ��������ж������� ��\n");
			for (User uu : appointments) {
				sb.append("\n\ue035�����š�").append(uu.getSeq()).append("��\n");
				sb.append(uu.getName()).append("(֤��β��").append(uu.getId().substring(12));
				sb.append(")\nԤԼ����ҽԺ��").append(uu.getHospital()).append("�����ң�").append(uu.getDepartment());
				sb.append("\nԤԼ����ʱ�䣺").append(uu.getTime().substring(0, uu.getTime().indexOf("��")+1));
			}
			sb.append("\n\n(ȡ��ԤԼ���ﶩ����ظ���QX�����š�)");
			return sb.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return "ϵͳ��æ006�����Ժ�����";
		}
	}
	
	//����֤�û���ѯԤԼ��Ϣ
	public static String findAppointment(String content){
		try {
			String id = content.substring(content.indexOf("@")+1);
			if(id.length()!=18)
				return "���֤�����������������";
			List<User> appointments = udao.findAppointmentsById(id);
			if(appointments.size()==0)
				return "��ѯ���޴��û���ԤԼ������Ϣ";
			StringBuffer sb = new StringBuffer();
			sb.append("����ѯ��ԤԼ������Ϣ�ǣ�\n");
			for (User uu : appointments) {
				sb.append("\n\ue035");
				sb.append("ԤԼ����ҽԺ��").append(uu.getHospital());
				sb.append("�����ң�").append(uu.getDepartment());
				sb.append("\nԤԼ����ʱ�䣺").append(uu.getTime().substring(0, uu.getTime().indexOf("��")+1));
			}
			return sb.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return "ϵͳ��æ007�����Ժ�����";
		}
	}
	
	//����ԤԼ��Կ
	public static String generateCode(String wxid){
		User u;
		try {
			u = udao.findHostByWechat(wxid);
			if(u==null)
				return "��������û���֤";
			if(u.getCode()!=null&&u.getCode().length()>=6)
				return "���Ѿ����ɹ�ԤԼ������Կ���ظ���3���ɲ�ѯ��Կ��Ϣ";
			/**
			 * ��Կ���ɹ��� X1 + Y1Y2 + X2 + Y3Y4
			 * Y1Y2ΪdateString�ĵ���һ��λ��Y3Y4ΪdateString�ĵ�������λ
			 * X1Ϊ�û�openid���±�ΪY2���ַ��Ĵ�д��X2Ϊ�û�openid���±�ΪY1���ַ��Ĵ�д
			 */
			StringBuffer code = new StringBuffer();
			java.util.Date currentTime = new java.util.Date();
			String dateString = Long.toString(currentTime.getTime());
			int index1 = Integer.parseInt(dateString.substring(dateString.length()-1));
			code.append(wxid.substring(index1,index1+1).toUpperCase());		
			code.append(dateString.substring(dateString.length()-2));
			int index2 = Integer.parseInt(dateString.substring(dateString.length()-2,dateString.length()-1));
			code.append(wxid.substring(index2,index2+1).toUpperCase());
			code.append(dateString.substring(dateString.length()-4,dateString.length()-2));
			/**
			 * ��Կ��Ч�ڣ�������
			 */
			Calendar startDate = Calendar.getInstance();
			startDate.set(Calendar.YEAR, 2015);
			startDate.set(Calendar.MONTH, 11);
			startDate.set(Calendar.DAY_OF_MONTH, 30);
			startDate.set(Calendar.HOUR_OF_DAY, 0);
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.SECOND, 0);
			//��Կ��ʼʱ��2015/12/1 00:00:00

			startDate.add(Calendar.MONTH,3);//����������Ч��
			java.util.Date expDate = startDate.getTime();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String expDateString = formatter.format(expDate);
			if(udao.addCode(wxid, code.toString(), expDateString)==true){
				formatter = new SimpleDateFormat("yyyy��MM��dd��HH:mm:ss");
				return "���Ĵ�����ԿΪ��"+code.toString()+"\nʣ��ʹ�ô�����4 ��"+"��\n��Ч����"+formatter.format(expDate);
			}
			else return "��Կ����ʧ��001�����Ժ�ظ���������Կ���ٴγ���������Կ";
		} catch (SQLException e) {
			e.printStackTrace();
			return "��Կ����ʧ��002�����Ժ�ظ���������Կ���ٴγ���������Կ";
		}
		
	}
	
	//��ȡ��Կ
	public static String getCodeByWechat(String wxid){
		try {
			User u = udao.findHostByWechat(wxid);
			if(u==null)
				return "";
			else return u.getCode();
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	//��ѯָ��ҽ�Ƶ�
	@SuppressWarnings("rawtypes")
	public static String findHospitals(){
		try {
			HashMap<String,String> hospitals = udao.findHospitals();
			if(hospitals.isEmpty()==true)
				return "����ָ��ҽ�Ƶ㣬�����ڴ�";
			StringBuffer sb = new StringBuffer();
			sb.append("��ָ��ҽ�Ƶ㡿");
			Iterator<Entry<String, String>> iter = hospitals.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				sb.append("\n\ue049").append((String)entry.getKey()).append("\n��ַ��").append((String)entry.getValue());
			}
			return sb.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return "ϵͳ��æ007�����Ժ�����";
		}
	}
	
	//�������
	public static String feedback(String content){
		try {
			java.util.Date time = new java.util.Date();
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeString = formatter2.format(time);
			udao.makeLog(content, timeString);
			EmailUtil.sendPrepare(udao.getRecipient(), "Receive suggestion", content);
			EventQueue.invokeLater(new Runnable(){
				public void run(){
					EmailUtil.send();
				}
			});
			return "�Ѽ�¼���������ԣ���л���ı������\ue417";
		} catch (SQLException e) {
			e.printStackTrace();
			return "ϵͳ��æ009�����Ժ�����";
		}
	}
	
}
