package ch.fhnw.dist;

import java.io.IOException;

import javax.mail.MessagingException;

public class Main {

	public static void main(String[] args) {
		BayesSpamfilter filter = new BayesSpamfilter();
		
		ReadData rd = new ReadData(filter);
		try {
			rd.zipToSpamfilter("resources/spam-anlern.zip", true);
			rd.zipToSpamfilter("resources/ham-anlern.zip", false);

			rd.zipLern("resources/spam-kallibrierung", true);
			rd.zipLern("resources/ham-kallibrierung", false);
			
			//3. Testen mit ham und spam test
			rd.zipTest("resources/ham-test.zip");
			rd.zipTest("resources/spam-test.zip");
			
//			for (String s : spamMap.keySet()) {
//                System.out.println(s + ": " + spamMap.get(s));
//          }
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
	}
}
