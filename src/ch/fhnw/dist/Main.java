package ch.fhnw.dist;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import javax.mail.MessagingException;

public class Main {

	public void Run(){
		final BayesSpamfilter filter = new BayesSpamfilter();
		final ReadData rd = new ReadData();

		try(final Scanner scan = new Scanner(System.in)) {
			rd.readZip("resources/spam-anlern.zip", zf -> z -> filter.addMail(zf, z, true));
			rd.readZip("resources/ham-anlern.zip", zf -> z -> filter.addMail(zf, z, false));

			double[] spamProbabilities = rd.readZipDouble(
				"resources/spam-kallibrierung.zip", zf -> z -> filter.probabilitySpam(zf, z));
			double[] hamProbabilities = rd.readZipDouble(
				"resources/ham-kallibrierung.zip", zf -> z -> filter.probabilitySpam(zf, z));

			double barrier = 0.5;
			long spamDetected = Arrays.stream(spamProbabilities).filter(spam -> spam >= barrier).count();
			System.out.println(String.format("Spam count: %s Detected: %d (%.2f%%)",
				spamProbabilities.length, spamDetected, spamDetected * 100.0 / spamProbabilities.length));
			long hamDetected = Arrays.stream(hamProbabilities).filter(ham -> ham < barrier).count();
			System.out.println(String.format("Ham count: %s Detected: %d (%.2f%%)",
				hamProbabilities.length, hamDetected, hamDetected * 100.0 / hamProbabilities.length));
			
			//3. Testen mit ham und spam test
//			rd.readZip("resources/ham-test.zip");
//			rd.readZip("resources/spam-test.zip");

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

	public static void main(String[] args) {
		new Main().Run();
	}
}
