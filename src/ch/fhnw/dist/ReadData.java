package ch.fhnw.dist;

import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipFile;

import javax.mail.MessagingException;

public class ReadData {
	final BayesSpamfilter spamFilter;
	
	public ReadData(BayesSpamfilter spamFilter) {
		this.spamFilter = spamFilter;
	}
	
	public void readZip(String fileName, boolean isSpam) throws IOException, MessagingException {
		final HashMap<String, Integer> hm = new HashMap<>();
		try(ZipFile zf = new ZipFile(fileName)){
			zf.stream().forEach(z -> {
				try {
					spamFilter.addMail(zf.getInputStream(z), isSpam);	
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
//			Enumeration<?> enumeration = zf.entries();
//			while(enumeration.hasMoreElements()) {
//				ZipEntry ze = (ZipEntry) enumeration.nextElement();
//				findWords(hm, zf.getInputStream(ze));
//			}
		}
	}
	
	
}
