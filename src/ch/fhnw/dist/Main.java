package ch.fhnw.dist;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.mail.MessagingException;

public class Main {

	public static void main(String[] args) {
		final BayesSpamfilter filter = new BayesSpamfilter();
		ReadData rd = new ReadData(filter);

		try(final Scanner scan = new Scanner(System.in)) {
			rd.zipToSpamfilter("resources/spam-anlern.zip", true);
			rd.zipToSpamfilter("resources/ham-anlern.zip", false);

			rd.zipLern("resources/spam-kallibrierung.zip", true);
			rd.zipLern("resources/ham-kallibrierung.zip", false);
			
			//3. Testen mit ham und spam test
			rd.zipTest("resources/ham-test.zip");
			rd.zipTest("resources/spam-test.zip");

			while (true) {
				System.out.println("Insert path to mail");
				String file = scan.nextLine();
				System.out.println("Probability: " + filter.probabilitySpam(new FileInputStream(file)));
				String yesOrNo = null;
				do{
					if(yesOrNo != null){ System.err.println("Invalid input"); }
					System.out.println("Is Spam? y = yes, n = no");
					yesOrNo = scan.nextLine().trim();
				} while(!yesOrNo.equalsIgnoreCase("n") && !yesOrNo.equalsIgnoreCase("y"));
				filter.addMail(new FileInputStream(file), yesOrNo.equalsIgnoreCase("y"));
			}
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
	}
}
