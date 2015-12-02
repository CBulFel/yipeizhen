package yk.service;

import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import yk.authentication.Authentication;
import yk.message.resp.TextMessage;
import yk.process.text.TextProcess;
import yk.util.MessageUtil;

/**
 * ���ķ�����
 * 
 */
public class CoreService {
	/**
	 * ����΢�ŷ���������
	 * 
	 * @param request
	 * @return
	 */
	public static String processRequest(HttpServletRequest request) {
		String respMessage = null;
		try {
			String respContent = "$";
			
			// xml�������
			HashMap<String, String> requestMap = MessageUtil.parseXml(request);

			// ���ͷ��ʺţ�open_id��
			String fromUserName = requestMap.get("FromUserName");
			// �����ʺ�
			String toUserName = requestMap.get("ToUserName");
			// ��Ϣ����
			String msgType = requestMap.get("MsgType");

			// �ظ��ı���Ϣ
			TextMessage textMessage = new TextMessage();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new Date().getTime());
			textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
			textMessage.setFuncFlag(0);

			// �ı���Ϣ
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
				respContent = TextProcess.doText(requestMap.get("Content"), fromUserName);
			}
			// ͼƬ��Ϣ
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
				
			}
			// ����λ����Ϣ
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
				
			}
			// ������Ϣ
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
				
			}
			// ��Ƶ��Ϣ
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
				
			}
			// �¼�����
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				// �¼�����
				String eventType = requestMap.get("Event");
				// ����
				if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
					respContent = "С�����������ɿ���\ue056��\n�ظ����鿴����˵��";
				}
				// ȡ������
				else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
					
				}
				// �Զ���˵�����¼�
				else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
					
				}
			}
			if("$".equals(respContent))
				return null;
			textMessage.setContent(respContent);
			respMessage = MessageUtil.textMessageToXml(textMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respMessage;
	}
	
	/**
	 * ������ύ������
	 * 
	 */
	public static int processFormSubmit(HttpServletRequest request){
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		String sex = request.getParameter("sex");
		String id = request.getParameter("id");
		String tel = request.getParameter("tel");
		String time = request.getParameter("timeofyear")+request.getParameter("timeofmonth")
							+request.getParameter("timeofday")+request.getParameter("timeofampm")
							+"\n����š�����ţ�"+request.getParameter("patientno");//ֱ�Ӱ�ѡ�������ԤԼʱ�����
		String hospital = request.getParameter("hospital");
		String department = request.getParameter("department");
		String doctor = request.getParameter("doctor");
		String disease = request.getParameter("disease");	
		return Authentication.makeAppointment(code, name, sex, id, tel, time,hospital, department, doctor, disease);
	}
}

