package utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class EmailUtil {
	/*
	 * from=lvpiao1@qq.com fromPassword=gzacjpfkrwfsdjbe to1=2033465789@qq.com
	 * to2=1916429819@qq.com protocol=smtp host=smtp.qq.com port=465
	 */

	private static final String DEFAULT_FORM = "1433209407@qq.com";
	private static final String DEFAULT_FORM_AUTH = "zffxwgbfdqhifhaf";

	// gzacjpfkrwfsdjbe
	public static String sendEmail(String content, String from, String auth, String subject, List<String> tos)
			throws EmailException {
		Email email = new SimpleEmail();
		email.setSSLOnConnect(true);
		email.setHostName("smtp.qq.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator(from, auth));
		email.setFrom(from);
		email.setSubject(subject);
		email.setMsg(content);
		for (String to : tos) {
			email.addTo(to);
		}
		return email.send();
	}

	public static String sendEmail(String content, String subject, String to) throws EmailException {
		Email email = new SimpleEmail();
		email.setSSLOnConnect(true);
		email.setHostName("smtp.qq.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator(DEFAULT_FORM, DEFAULT_FORM_AUTH));
		email.setFrom(DEFAULT_FORM);
		email.setSubject(subject);
		email.setMsg(content);
		email.addTo(to);
		return email.send();
	}

	public static final String EMAIL_REGEX = "\\w+@.+\\.com";

	public static boolean isEmailString(String email) {
		return email.matches(EMAIL_REGEX);
	}

	public static void sendVerifySignupEmail(String randToken, String uid, String varifyUrl) throws EmailException {
		StringBuffer tmp = new StringBuffer("请在5分钟内访问以下链接，确认激活账户！\n\n").append(varifyUrl).append("\n\n")
				.append("如果这不是预料中的邮件，请忽略!");
		String content = tmp.toString();
		ArrayList<String> tos = new ArrayList<String>();
		tos.add(uid);
		sendEmail(content, DEFAULT_FORM, DEFAULT_FORM_AUTH, "注册确认", tos);
	}

	public static void main(String[] args) throws EmailException {
		List<String> tos = new ArrayList<String>();
		tos.add("2033465789@qq.com");
		sendEmail("测试！", DEFAULT_FORM, DEFAULT_FORM_AUTH, "ces", tos);
	}
}
