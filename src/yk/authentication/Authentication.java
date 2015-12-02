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
	
	//认证用户，绑定微信账号
	public static String authenticate(String wxid,String content){	
		try {
			User u = udao.findHostByWechat(wxid);
			if(u!=null)
				return "您的微信号已绑定认证用户"+u.getName()+"(证件尾号"+u.getId().substring(12)+")，如不是本人操作，请联系工作人员，回复【3】可查看相关认证信息";
			int i = content.lastIndexOf("#");
			if(i<=1)
				return "格式错误，请检查。举个例子吧，酱紫回复：#张三#445323197001014226";
			String name = content.substring( 1 , i );
			String id = content.substring(i+1);
			if(name==null||name.length()<1||id==null||id.length()<18)
				return "格式错误，请检查。举个例子吧，酱紫回复：#李四#445323197001014226";
			u = udao.findHostById(id);
			if(u==null) 
				return "认证失败，您申请认证的"+name+"(证件尾号"+id.substring(12)+")不属于本公司员工，感谢您的关注";
			if(!u.getName().equals(name)) 
				return "认证失败，请检查姓名拼写";
			if(u.getWechat()!=null&&u.getWechat().length()>0) 
				return "认证失败，"+name+"(证件尾号"+id.substring(12)+")已通过其他微信号认证，如不是您本人操作，请联系工作人员";
			
			//条件符合，进行绑定微信，并生成密钥
			if(udao.bindHostWechat(id, wxid)==true){
				String code = generateCode(wxid);
				return "认证成功！亲爱的"+name+"，"+code
						+"\n<a href=\"http://yipeizhen.duapp.com/YKWX/appointment.html?code="+getCodeByWechat(wxid)+"\">点击此处预约陪诊</a>"
						+ "\n(温馨提示：预约陪诊前，请先预约挂号)";
			}
			else return "认证失败，请联系工作人员";
		} catch (SQLException e) {
			e.printStackTrace();
			return "系统繁忙001，请稍候再试";
		}
	}	
	
	//查看用户信息
	public static String myStatus(String wxid){
		try {
			User u =udao.findHostByWechat(wxid);
			if(u==null)
				return "尊敬的客户，您是未认证用户。";
			StringBuffer sb = new StringBuffer();
			sb.append("尊敬的").append(u.getName()).append("\n");
			sb.append("状态：已认证用户\ue335\n证件号：").append(u.getId().substring(0, 4));
			sb.append("**********").append(u.getId().substring(14));
			sb.append("\n\ue03f穿测密钥：\n");
			if(u.getCode()==null||u.getCode().length()<6){
				sb.append("尚未生成，回复【生成密钥】生成预约穿测密钥\ue011");
			}else{
				sb.append("\ue23a").append(u.getCode()).append("\ue23b\n");
				sb.append("剩余使用次数：").append(4-u.getUsetimes()).append("\n");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date datetemp = formatter.parse(u.getExpdate());
				formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
				sb.append("有效期至").append(formatter.format(datetemp));		
			}
			return sb.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return "系统繁忙004，请稍候再试";
		} catch (ParseException e) {
			e.printStackTrace();
			return "系统繁忙0042，请稍候再试";
		}
	}
	
	//认证用户以及拥有密钥的客户预约诊疗
	//return
	//		-1 : 参数不正确
	//		0 : 密钥不正确
	//		1 : 密钥使用次数超过4次
	//		2 : 密钥过期
	//		3、4、5 : 异常
	//		6 : 提交成功
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
				//增加密钥使用次数
				udao.setUseTimes(u.getWechat(), u.getUsetimes()+1);
				//发送邮件通知
				StringBuffer sb = new StringBuffer();
				sb.append("姓名：").append(name);
				sb.append("\n 性别：").append(sex);
				sb.append("\n 身份证号：").append(id);
				sb.append("\n 联系电话：").append(tel);
				sb.append("\n 预约陪诊时间：").append(time);
				sb.append("\n 预约医院：").append(hospital);
				sb.append("\n 预约科室：").append(department);
				sb.append("\n 预约医生：").append(doctor);
				sb.append("\n 就诊疾病：").append(disease);
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
	
	//认证用户取消订单
	public static String cancleAppointment(String wxid,String content){
		try {
			User u = udao.findHostByWechat(wxid);
			if(u==null){
				return "抱歉，只有认证用户才能取消其密钥所属订单。";
			}
			content = content.replaceAll("x", "X");
			content = content.substring(content.indexOf("X")+1);
			if(!content.matches("\\d+"))
				return "取消订单格式错误，正确格式应为【QX订单号】英文为大写字母，订单号为正整数，请检查后重试。";
			int seq = Integer.parseInt(content);
			List<User> appointments = udao.findAppointmentsByCode(u.getCode());
			if(appointments.size()==0)
				return "您的密钥没有预约陪诊订单";
			for (User uu : appointments) {
				if(uu.getSeq()==seq){
					
					//就诊前一天20：00前才能取消订单
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd");
					java.util.Date atime = formatter.parse(uu.getTime().substring(0,uu.getTime().indexOf("日")));
					Calendar ctime = Calendar.getInstance();
					ctime.setTime(atime);
					ctime.add(Calendar.DAY_OF_MONTH, -1);//前一天
					ctime.set(Calendar.HOUR_OF_DAY,20);//晚上八点
					Calendar now = Calendar.getInstance();
					if(!now.before(ctime))//现在时间不在预约时间截止前
						return "抱歉，此订单已不能取消，取消订单必须在就诊前一天晚上8点前进行取消。";
					
					boolean res = udao.cancleAppointmentBySeq(seq);
					if(res==true){
						//回滚使用次数
						udao.setUseTimes(u.getWechat(), u.getUsetimes()-1);
						//发送邮件通知
						StringBuffer sb = new StringBuffer();
						sb.append("取消订单\n").append(" 姓名：").append(uu.getName());
						sb.append("\n 身份证号：").append(uu.getId());
						sb.append("\n 联系电话：").append(uu.getTel());
						sb.append("\n 预约陪诊医院：").append(uu.getHospital());
						sb.append("\n 预约陪诊时间：").append(uu.getTime());
						EmailUtil.sendPrepare(udao.getRecipient(), "Cancel appointment", sb.toString());
						EventQueue.invokeLater(new Runnable(){
							public void run(){
								EmailUtil.send();
							}
						});
						//写日志
						java.util.Date time = new java.util.Date();
						SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String timeString = formatter2.format(time);
						udao.makeLog("取消订单【"+uu.getName()+"】【"+uu.getHospital()+"】【"+uu.getTime()+"】", timeString);
						
						return "已成功取消预约陪诊订单【"+content+"】，感谢您的使用。";
					}else return "系统繁忙0082，请稍候再试";
				}
			}
			return "取消订单失败，订单号为【"+content+"】的订单不是您的密钥产生的预约陪诊，无法取消";
		} catch (SQLException e) {
			e.printStackTrace();
			return "系统繁忙008，请稍候再试";
		} catch (ParseException e1) {
			e1.printStackTrace();
			return "系统繁忙0082，请稍候再试";
		}
	}
	
	//认证用户查询预约信息
	public static String myAppointments(String wxid){
		try {
			User u = udao.findHostByWechat(wxid);
			if(u==null)
				return "非认证用户请回复【查询预约@身份证号】查询您的预约陪诊信息";
			List<User> appointments = udao.findAppointmentsByCode(u.getCode());
			if(appointments.size()==0)
				return "查询暂无您的预约陪诊信息";
			StringBuffer sb = new StringBuffer();
			sb.append("尊敬的").append(u.getName()).append("，使用您的穿测密钥预约陪诊的所有订单如下 ：\n");
			for (User uu : appointments) {
				sb.append("\n\ue035订单号【").append(uu.getSeq()).append("】\n");
				sb.append(uu.getName()).append("(证件尾号").append(uu.getId().substring(12));
				sb.append(")\n预约陪诊医院：").append(uu.getHospital()).append("，科室：").append(uu.getDepartment());
				sb.append("\n预约陪诊时间：").append(uu.getTime().substring(0, uu.getTime().indexOf("午")+1));
			}
			sb.append("\n\n(取消预约陪诊订单请回复【QX订单号】)");
			return sb.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return "系统繁忙006，请稍候再试";
		}
	}
	
	//非认证用户查询预约信息
	public static String findAppointment(String content){
		try {
			String id = content.substring(content.indexOf("@")+1);
			if(id.length()!=18)
				return "身份证号码输入错误，请重试";
			List<User> appointments = udao.findAppointmentsById(id);
			if(appointments.size()==0)
				return "查询暂无此用户的预约陪诊信息";
			StringBuffer sb = new StringBuffer();
			sb.append("您查询的预约陪诊信息是：\n");
			for (User uu : appointments) {
				sb.append("\n\ue035");
				sb.append("预约陪诊医院：").append(uu.getHospital());
				sb.append("，科室：").append(uu.getDepartment());
				sb.append("\n预约陪诊时间：").append(uu.getTime().substring(0, uu.getTime().indexOf("午")+1));
			}
			return sb.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return "系统繁忙007，请稍候再试";
		}
	}
	
	//生成预约密钥
	public static String generateCode(String wxid){
		User u;
		try {
			u = udao.findHostByWechat(wxid);
			if(u==null)
				return "请先完成用户认证";
			if(u.getCode()!=null&&u.getCode().length()>=6)
				return "您已经生成过预约分享密钥，回复【3】可查询密钥信息";
			/**
			 * 密钥生成规则 X1 + Y1Y2 + X2 + Y3Y4
			 * Y1Y2为dateString的倒数一二位，Y3Y4为dateString的倒数三四位
			 * X1为用户openid的下标为Y2的字符的大写，X2为用户openid的下标为Y1的字符的大写
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
			 * 密钥有效期，三个月
			 */
			Calendar startDate = Calendar.getInstance();
			startDate.set(Calendar.YEAR, 2015);
			startDate.set(Calendar.MONTH, 11);
			startDate.set(Calendar.DAY_OF_MONTH, 30);
			startDate.set(Calendar.HOUR_OF_DAY, 0);
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.SECOND, 0);
			//密钥开始时间2015/12/1 00:00:00

			startDate.add(Calendar.MONTH,3);//加三个月有效期
			java.util.Date expDate = startDate.getTime();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String expDateString = formatter.format(expDate);
			if(udao.addCode(wxid, code.toString(), expDateString)==true){
				formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
				return "您的穿测密钥为："+code.toString()+"\n剩余使用次数：4 次"+"，\n有效期至"+formatter.format(expDate);
			}
			else return "密钥生成失败001，请稍候回复【生成密钥】再次尝试生成密钥";
		} catch (SQLException e) {
			e.printStackTrace();
			return "密钥生成失败002，请稍候回复【生成密钥】再次尝试生成密钥";
		}
		
	}
	
	//获取密钥
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
	
	//查询指定医疗点
	@SuppressWarnings("rawtypes")
	public static String findHospitals(){
		try {
			HashMap<String,String> hospitals = udao.findHospitals();
			if(hospitals.isEmpty()==true)
				return "暂无指定医疗点，敬请期待";
			StringBuffer sb = new StringBuffer();
			sb.append("【指定医疗点】");
			Iterator<Entry<String, String>> iter = hospitals.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				sb.append("\n\ue049").append((String)entry.getKey()).append("\n地址：").append((String)entry.getValue());
			}
			return sb.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return "系统繁忙007，请稍候再试";
		}
	}
	
	//反馈意见
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
			return "已记录下您的留言，感谢您的宝贵意见\ue417";
		} catch (SQLException e) {
			e.printStackTrace();
			return "系统繁忙009，请稍候再试";
		}
	}
	
}
