package yk.process.text;

import yk.authentication.Authentication;

public class TextProcess {
	public static String doText(String content,String userOpenId){
		content = content.replaceAll("��", "?");//ͳһ�ʺŸ�ʽ
		content = content.replaceAll("��", "#");//ͳһ���Ÿ�ʽ
		StringBuffer sb = new StringBuffer();
		switch(content.charAt(0)){
		case '?':
			if(content.equals("?")){
			sb.append("\ue443�����˵���\ue443\n\n");
			sb.append("\ue442�ظ� 1��Ա����֤\n");
			sb.append("\ue442�ظ� 2��ԤԼ����\n");
			sb.append("\ue442�ظ� 3����ѯ\n");
			sb.append("\ue442�ظ� 4���������\n");
			sb.append("\nС�����������ɿ���\ue056");
			return sb.toString();
			}
			else return Faq.post1(content, userOpenId);
		case '#':
			return Authentication.authenticate(userOpenId,content);
		case '��':
			if("������Կ".equals(content))
				return Authentication.generateCode(userOpenId);
			else return Faq.post1(content, userOpenId);
		case '1':
			if("1".equals(content))
				return "�װ����ƿ�С���\ue417����л���μӲ�Ʒ���⣬�����ظ���#����#���֤���롿�Ի�ô�����Կ��";
			else return Faq.post1(content, userOpenId);
		case '2':
			if("2".equals(content)){
				String code = Authentication.getCodeByWechat(userOpenId);
				return "<a href=\"http://yipeizhen.duapp.com/YKWX/appointment.html?code="+code+"\">����˴�ԤԼ����</a>\n(��ܰ��ʾ��ԤԼ����ǰ��"
						+ "����ԤԼ�Һţ���ע΢�Ź��ں�<a href=\"http://dwz.cn/24hQaa\">yijiankang114</a>����ʹ������ԤԼ�Һ�)";
			}
			else return Faq.post1(content, userOpenId);
		case '3':
			if("3".equals(content)){
				sb.append("\ue443����ѯ�˵���\ue443\n\n");
				sb.append("\ue442�ظ� a����ѯ����ǰ����֤��Ϣ����Կ��Ϣ�������ƿ�Ա����\n");
				sb.append("\ue442�ظ� b����ѯ����ԤԼ���ﶩ��\n");
				sb.append("\ue442�ظ� c����ѯ��ԤԼ����ҽ�Ƶ�\n");
				sb.append("\n�ظ� ? �鿴���˵�");
				return sb.toString();
			}
			else return Faq.post1(content, userOpenId);
		case '4':
			if("4".equals(content)){
				return "�ظ���@��������Լ����顿��С��ͻ���������Է��������ǵĹ�����Ա��\ue405";
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
		case '��':
			if(content.indexOf("��ѯԤԼ@")>=0)
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
				String id = content.substring(content.length()-18,content.length()-1);//��Щ���֤������X
				if(id.matches("\\d+"))
					return "��������Ҫ����Ա����֤����ظ���#����#���֤���롿��#�Ų���ʡ���ޡ�";
			}
			return Faq.post1(content, userOpenId);
		}//switch
	}
	
}
