package poly.controller;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MailController {
	private Logger log = Logger.getLogger(this.getClass());

	@RequestMapping(value = "mailSend")
	public String mailSend(HttpServletRequest request, Model model) {
		String contactName = request.getParameter("contactName");
		String contactTel = request.getParameter("contactTel");
		String contactEmail = request.getParameter("contactEmail");
		String contactMessage = request.getParameter("contactMessage");
		final String user = "evergreen5m2019@gmail.com";
		final String password = "evergreen2019!";

		// SMTP 서버 정보를 설정한다.
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", 465);
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.ssl.enable", "true");
		prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

		Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});
		try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));

            //수신자메일주소
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("evergreen5m2019@gmail.com")); 

            // Subject
            message.setSubject("5M 고객문의 이메일입니다."); //메일 제목을 입력

            // Text
            message.setText(" 문의자 : "+contactName +"\n 전화번호 : "+contactTel+"\n 이메일 : "+contactEmail+"\n 문의내용 : "+contactMessage);    //메일 내용을 입력
            // send the message
            Transport.send(message); ////전송
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		model.addAttribute("msg", "문의메일을 보냈습니다.");
		model.addAttribute("url", "/main/Contact.do");
		return "/redirect";
	}
	
	@RequestMapping(value = "PwMailSend")
	public String PwMailSend(HttpServletRequest request, Model model) {
		String userEmail = request.getParameter("userEmail");
		String NewPw = request.getParameter("NewPw");
		log.info(userEmail);
		log.info(NewPw);
		final String user = "evergreen5m2019@gmail.com";
		final String password = "evergreen2019!";

		// SMTP 서버 정보를 설정한다.
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", 465);
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.ssl.enable", "true");
		prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

		Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});
		try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));

            //수신자메일주소
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail)); 

            // Subject
            message.setSubject("5M 고객문의 이메일입니다."); //메일 제목을 입력

            // Text
            message.setText(" 새로운 비밀번호는 "+NewPw+" 입니다.");    //메일 내용을 입력
            // send the message
            Transport.send(message); ////전송
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		model.addAttribute("msg", "임시비밀번호를 이메일로 발송했습니다.");
		model.addAttribute("url", "/index.do");
		return "/redirect";
	}

}
