package ch.fhnw.dist;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.mail.MessagingException;

public class Main {

	public static void main(String[] args) {
		final Scanner scan = new Scanner(System.in);
		final BayesSpamfilter filter = new BayesSpamfilter();
		
		ReadData rd = new ReadData(filter);
		try {
			rd.zipToSpamfilter("resources/spam-anlern.zip", true);
			rd.zipToSpamfilter("resources/ham-anlern.zip", false);

			rd.zipLern("resources/spam-kallibrierung.zip", true);
			rd.zipLern("resources/ham-kallibrierung.zip", false);
			
			//3. Testen mit ham und spam test
			rd.zipTest("resources/ham-test.zip");
			rd.zipTest("resources/spam-test.zip");
			
//			for (String s : spamMap.keySet()) {
//                System.out.println(s + ": " + spamMap.get(s));
//          }
			while (true) {
				System.out.println("Insert path to mail\n");
				String file = scan.nextLine();
				System.out.println("Probability: " 
						+ filter.probabilitySpam(new FileInputStream(file)));
				System.out.println("Is Spam? y = yes, n = no");
				String yesOrNo = scan.nextLine();
				if(yesOrNo.contains("y")) {
					filter.addMail(new FileInputStream(file), true);
				} else if(yesOrNo.contains("n")) {
					filter.addMail(new FileInputStream(file), false);
				}
			}
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
			scan.close();
		}
	}
}
