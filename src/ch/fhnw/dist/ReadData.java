package ch.fhnw.dist;

import java.io.IOException;
import java.util.zip.ZipFile;

import javax.mail.MessagingException;

public class ReadData {
	final BayesSpamfilter spamFilter;
	
	public ReadData(BayesSpamfilter spamFilter) {
		this.spamFilter = spamFilter;
	}
	
	public void zipToSpamfilter(String fileName, boolean isSpam) 
			throws IOException, MessagingException {
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
	
	public void zipLern(String fileName, boolean isSpam) 
			throws IOException, MessagingException {
		try(ZipFile zf = new ZipFile(fileName)){
			zf.stream().forEach(z -> {
				try {
					spamFilter.calcProbability(zf.getInputStream(z), isSpam);	
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}
	
	public void zipTest(String fileName) 
			throws IOException, MessagingException {
		try(ZipFile zf = new ZipFile(fileName)){
			zf.stream().forEach(z -> {
				try {
					System.out.println(spamFilter.isSpam(zf.getInputStream(z)));	
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}
	
}
