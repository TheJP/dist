package ch.fhnw.dist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;

import com.sun.mail.util.DecodingException;

/**
 * Used to extract the content of mails.
 * @author JP
 */
public class MailParser {

	/**
	 * Takes InputStreams to raw mails and extracts the content.
	 * @param stream
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public String getMessage(InputStream stream) throws MessagingException, IOException {
		Session s = Session.getDefaultInstance(new Properties());
		MimeMessage message = new MimeMessage(s, stream);
		String content;
		try {
			content = getContent(message.getContent());
		} catch (UnsupportedEncodingException e) {
			content = getContent(message.getInputStream());
		} catch (DecodingException | MessagingException e) {
			System.err.println("Invalid mail");
			content = "";
		}
		Stream<String> result = Arrays.stream(content.split("\n"));
		if (content.trim().startsWith("URL")) { result = result.skip(3); }
		content = Jsoup.parse(content).text();
		return result.reduce("", (a, b) -> a + b + "\n");
	}

	/**
	 * Gets the mail content in a recursive manner.
	 * If the mail is a multipart mail, all text components are concatenated to the result string.
	 * Every binary content is skipped.
	 * @param content
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	private String getContent(Object content) throws MessagingException, IOException {
		if (content instanceof String) {
			return (String) content;
		} else if (content instanceof MimeMessage) {
			return getContent(((MimeMessage) content).getContent());
		} else if (content instanceof MimeMultipart) {
			//If the message is a multipart message, all text content is concatenated together.
			MimeMultipart multipart = (MimeMultipart) content;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < multipart.getCount(); ++i) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if (bodyPart.getContentType().contains("text/") ||
						bodyPart.getContentType().contains("multipart/")) {
					sb.append(getContent(bodyPart.getContent()));
				}
			}
			return sb.toString();
		} else if (content instanceof InputStream) {
			InputStream contentStream = (InputStream) content;
			StringBuilder sb = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(contentStream))) {
				String line = reader.readLine();
				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = reader.readLine();
				}
			}
			return sb.toString();
		} else {
			throw new RuntimeException("Unknown content type: " + content.getClass());
		}
	}
}
