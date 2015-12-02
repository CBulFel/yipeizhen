package yk.service;

import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import yk.authentication.Authentication;
import yk.message.resp.TextMessage;
import yk.process.text.TextProcess;
import yk.util.MessageUtil;

/**
 * 核心服务类
 * 
 */
public class CoreService {
	/**
	 * 处理微信发来的请求
	 * 
	 * @param request
	 * @return
	 */
	public static String processRequest(HttpServletRequest request) {
		String respMessage = null;
		try {
			String respContent = "$";
			
			// xml请求解析
			HashMap<String, String> requestMap = MessageUtil.parseXml(request);

			// 发送方帐号（open_id）
			String fromUserName = requestMap.get("FromUserName");
			// 公众帐号
			String toUserName = requestMap.get("ToUserName");
			// 消息类型
			String msgType = requestMap.get("MsgType");

			// 回复文本消息
			TextMessage textMessage = new TextMessage();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new Date().getTime());
			textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
			textMessage.setFuncFlag(0);

			// 文本消息
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
				respContent = TextProcess.doText(requestMap.get("Content"), fromUserName);
			}
			// 图片消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
				
			}
			// 地理位置消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
				
			}
			// 链接消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
				
			}
			// 音频消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
				
			}
			// 事件推送
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				// 事件类型
				String eventType = requestMap.get("Event");
				// 订阅
				if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
					respContent = "小翼陪您，轻松看病\ue056。\n回复？查看功能说明";
				}
				// 取消订阅
				else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
					
				}
				// 自定义菜单点击事件
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
	 * 处理表单提交的数据
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
							+"\n门诊号、就诊号："+request.getParameter("patientno");//直接把选填项加在预约时间后面
		String hospital = request.getParameter("hospital");
		String department = request.getParameter("department");
		String doctor = request.getParameter("doctor");
		String disease = request.getParameter("disease");	
		return Authentication.makeAppointment(code, name, sex, id, tel, time,hospital, department, doctor, disease);
	}
}

