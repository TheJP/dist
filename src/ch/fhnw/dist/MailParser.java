package ch.fhnw.dist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailParser {

	public String getMessage(InputStream stream) throws MessagingException, IOException {
		Session s = Session.getDefaultInstance(new Properties());
		MimeMessage message = new MimeMessage(s, stream);
		String content;
		try {
			content = getContent(message.getContent());
		} catch (UnsupportedEncodingException e) {
			content = getContentForDefaultCharset(message);
		}
		Stream<String> result = Arrays.stream(content.split("\n"));
		if (content.trim().startsWith("URL")) { result = result.skip(3); }
		return result.reduce("", (a, b) -> a + b + "\n");
	}

	private String getContent(Object content) throws MessagingException, IOException {
		if (content instanceof String) {
			return (String) content;
		} else if (content instanceof MimeMessage) {
			return getContent(((MimeMessage) content).getContent());
		} else if (content instanceof MimeMultipart) {
			MimeMultipart multipart = (MimeMultipart) content;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < multipart.getCount(); ++i) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if (!bodyPart.getContentType().contains("image/") &&
						!bodyPart.getContentType().contains("application/")) {
					sb.append(getContent(bodyPart.getContent()));
				}
			}
			return sb.toString();
		} else if (content instanceof InputStream) {
			InputStream contentStream = (InputStream) content;
			StringBuilder sb = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(contentStream))) {
				String line = null;
				do {
					line = reader.readLine();
					sb.append(line);
				} while (line != null);
			}
			return sb.toString();
		} else {
			System.err.println(content.getClass());
			throw new RuntimeException("Unknown content type");
		}
	}

	private String getContentForDefaultCharset(MimeMessage message) throws IOException, MessagingException {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(message.getInputStream(), Charset.defaultCharset()))) {
			StringBuilder sb = new StringBuilder();
			String line = reader.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = reader.readLine();
			}
			return sb.toString();
		}
	}
}
