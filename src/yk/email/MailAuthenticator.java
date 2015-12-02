package yk.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * ·şÎñÆ÷ÓÊÏäµÇÂ¼ÑéÖ¤
 */
public class MailAuthenticator extends Authenticator {
  
    /**
     * ÓÃ»§Ãû£¨µÇÂ¼ÓÊÏä£©
     */
    private String username;
    /**
     * ÃÜÂë
     */
    private String password;
  
    /**
     * ³õÊ¼»¯ÓÊÏäºÍÃÜÂë
     * 
     * @param username ÓÊÏä
     * @param password ÃÜÂë
     */
    public MailAuthenticator(String username, String password) {
    this.username = username;
    this.password = password;
    }
  
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(username, password);
    }
  
    String getUsername() {
    return username;
    }
 
    public void setUsername(String username) {
    this.username = username;
    }
    
    String getPassword() {
    return password;
    }
    
    public void setPassword(String password) {
    this.password = password;
    }
  
}