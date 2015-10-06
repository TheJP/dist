package ch.fhnw.dist;

import java.io.IOException;
import java.util.HashMap;

import javax.mail.MessagingException;

public class Main {

	public static void main(String[] args) {
		BayesSpamfilter filter = new BayesSpamfilter();
		
		ReadData rd = new ReadData(filter);
		try {
			rd.readZip("resources/spam-anlern.zip", true);
			rd.readZip("resources/ham-anlern.zip", false);

			//2. Wahrscheinlichkeit bestimmen durch anlernen
			double hamSpam = 1/2;
			//3. Testen mit ham und spam test
//			rd.readZip("resources/ham-test.zip");
//			rd.readZip("resources/spam-test.zip");
			
//			for (String s : spamMap.keySet()) {
//                System.out.println(s + ": " + spamMap.get(s));
//            }
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
	}
}
