package yk.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yk.service.CoreService;
import yk.util.SignUtil;

/**
 * 核心处理请求类
 * 
 */
public class CoreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * 确认请求来自微信服务器
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		// 微信加密签名  
        String signature = request.getParameter("signature");
        // 时间戳  
        String timestamp = request.getParameter("timestamp");  
        // 随机数  
        String nonce = request.getParameter("nonce");  
        // 随机字符串  
        String echostr = request.getParameter("echostr");
 
        PrintWriter out = response.getWriter();  
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败  
        if (signature!=null && SignUtil.checkSignature(signature, timestamp, nonce)) {  
            out.print(echostr);  
        }  
        out.close();  
        out = null; 
	}

	/**
	 * 处理微信服务器发来的消息
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）  
        request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8"); 
  
        String formsign = request.getParameter("formsign");
        if(formsign!=null&&"true".equals(formsign)){//是表单提交数据
        	int res = CoreService.processFormSubmit(request);
        	switch(res){
        	case 0:
        	case 1:
        	case 2:
        		response.sendRedirect("codeerror.html");
        		break;
        	case 6:
        		response.sendRedirect("success.html");
        		break;
        	default:
        		response.sendRedirect("msgerror.html");
        		break;
        	}//switch
        }else{
            // 调用核心业务类接收消息、处理消息  
            String respMessage = CoreService.processRequest(request);  
                   
             // 响应消息
            if(respMessage!=null){
     	        PrintWriter out = response.getWriter();  
     	        out.print(respMessage); 
     	        out.close();
            }//if
        }//else

	}

}
