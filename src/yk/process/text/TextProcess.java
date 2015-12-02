package yk.process.text;

import yk.authentication.Authentication;

public class TextProcess {
	public static String doText(String content,String userOpenId){
		content = content.replaceAll("？", "?");//统一问号格式
		content = content.replaceAll("＃", "#");//统一井号格式
		StringBuffer sb = new StringBuffer();
		switch(content.charAt(0)){
		case '?':
			if(content.equals("?")){
			sb.append("\ue443【主菜单】\ue443\n\n");
			sb.append("\ue442回复 1，员工认证\n");
			sb.append("\ue442回复 2，预约陪诊\n");
			sb.append("\ue442回复 3，查询\n");
			sb.append("\ue442回复 4，反馈意见\n");
			sb.append("\n小翼陪您，轻松看病\ue056");
			return sb.toString();
			}
			else return Faq.post1(content, userOpenId);
		case '#':
			return Authentication.authenticate(userOpenId,content);
		case '生':
			if("生成密钥".equals(content))
				return Authentication.generateCode(userOpenId);
			else return Faq.post1(content, userOpenId);
		case '1':
			if("1".equals(content))
				return "亲爱的云康小伙伴\ue417，感谢您参加产品穿测，请您回复【#姓名#身份证号码】以获得穿测密钥。";
			else return Faq.post1(content, userOpenId);
		case '2':
			if("2".equals(content)){
				String code = Authentication.getCodeByWechat(userOpenId);
				return "<a href=\"http://yipeizhen.duapp.com/YKWX/appointment.html?code="+code+"\">点击此处预约陪诊</a>\n(温馨提示：预约陪诊前，"
						+ "请先预约挂号，关注微信公众号<a href=\"http://dwz.cn/24hQaa\">yijiankang114</a>即可使用翼健康预约挂号)";
			}
			else return Faq.post1(content, userOpenId);
		case '3':
			if("3".equals(content)){
				sb.append("\ue443【查询菜单】\ue443\n\n");
				sb.append("\ue442回复 a，查询您当前的认证信息及密钥信息（仅限云康员工）\n");
				sb.append("\ue442回复 b，查询您的预约陪诊订单\n");
				sb.append("\ue442回复 c，查询可预约陪诊医疗点\n");
				sb.append("\n回复 ? 查看主菜单");
				return sb.toString();
			}
			else return Faq.post1(content, userOpenId);
		case '4':
			if("4".equals(content)){
				return "回复【@您的意见以及建议】，小翼就会把您的留言反馈给我们的工作人员噢\ue405";
			}
			else return Faq.post1(content, userOpenId);
		case 'A':
		case 'a':
			if("a".equals(content))
				return Authentication.myStatus(userOpenId);
			else return Faq.post1(content, userOpenId);
		case 'B':
		case 'b':
			if("b".equals(content))
				return Authentication.myAppointments(userOpenId);
			else return Faq.post1(content, userOpenId);
		case 'C':
		case 'c':
			if("c".equals(content))
				return Authentication.findHospitals();
			else return Faq.post1(content, userOpenId);
		case '查':
			if(content.indexOf("查询预约@")>=0)
				return Authentication.findAppointment(content);
			else return Faq.post1(content, userOpenId);
		case 'q':
		case 'Q':
			if(content.charAt(1)=='X'||content.charAt(1)=='x')
				return Authentication.cancleAppointment(userOpenId, content);
			return Faq.post1(content, userOpenId);
		case '@':
			return Authentication.feedback(content);
		default :
			if(content.length()>18){
				String id = content.substring(content.length()-18,content.length()-1);//有些身份证后面是X
				if(id.matches("\\d+"))
					return "请问您是要进行员工认证吗？请回复【#姓名#身份证号码】，#号不能省略噢。";
			}
			return Faq.post1(content, userOpenId);
		}//switch
	}
	
}
