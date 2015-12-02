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
 * ���Ĵ���������
 * 
 */
public class CoreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * ȷ����������΢�ŷ�����
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		// ΢�ż���ǩ��  
        String signature = request.getParameter("signature");
        // ʱ���  
        String timestamp = request.getParameter("timestamp");  
        // �����  
        String nonce = request.getParameter("nonce");  
        // ����ַ���  
        String echostr = request.getParameter("echostr");
 
        PrintWriter out = response.getWriter();  
        // ͨ������signature���������У�飬��У��ɹ���ԭ������echostr����ʾ����ɹ����������ʧ��  
        if (signature!=null && SignUtil.checkSignature(signature, timestamp, nonce)) {  
            out.print(echostr);  
        }  
        out.close();  
        out = null; 
	}

	/**
	 * ����΢�ŷ�������������Ϣ
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ��������Ӧ�ı��������ΪUTF-8����ֹ�������룩  
        request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8"); 
  
        String formsign = request.getParameter("formsign");
        if(formsign!=null&&"true".equals(formsign)){//�Ǳ��ύ����
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
            // ���ú���ҵ���������Ϣ��������Ϣ  
            String respMessage = CoreService.processRequest(request);  
                   
             // ��Ӧ��Ϣ
            if(respMessage!=null){
     	        PrintWriter out = response.getWriter();  
     	        out.print(respMessage); 
     	        out.close();
            }//if
        }//else

	}

}
