package ch.fhnw.dist;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import javax.mail.MessagingException;

public class Main {

	private final BayesSpamfilter filter = new BayesSpamfilter();
	private final ReadData rd = new ReadData();

	private double[] probabilityOfZip(String filename) throws IOException, MessagingException{
		return rd.readZipDouble(filename, zf -> z -> filter.probabilitySpam(zf, z));
	}

	public void run(){

		try(final Scanner scan = new Scanner(System.in)) {
			System.out.println("Learning phase");
			rd.readZip("resources/spam-anlern.zip", zf -> z -> filter.addMail(zf, z, true));
			rd.readZip("resources/ham-anlern.zip", zf -> z -> filter.addMail(zf, z, false));

			System.out.println("Kalibration phase");
			double[] spamProbabilities = probabilityOfZip("resources/spam-kallibrierung.zip");
			double[] hamProbabilities = probabilityOfZip("resources/ham-kallibrierung.zip");

//			double barrier = 1.0 - (1.0 / Math.pow(10, 10)); //72.25% / 97.31%
			double barrier = 0.99; //93.78% / 91.26%

			long spamDetected = Arrays.stream(spamProbabilities).filter(spam -> spam >= barrier).count();
			System.out.println(String.format("Spam count: %s Detected: %d (%.2f%%)",
				spamProbabilities.length, spamDetected, spamDetected * 100.0 / spamProbabilities.length));
			long hamDetected = Arrays.stream(hamProbabilities).filter(ham -> ham < barrier).count();
			System.out.println(String.format("Ham count: %s Detected: %d (%.2f%%)",
				hamProbabilities.length, hamDetected, hamDetected * 100.0 / hamProbabilities.length));

			System.out.println("Testing phase");
			spamProbabilities = probabilityOfZip("resources/spam-test.zip");
			hamProbabilities = probabilityOfZip("resources/ham-test.zip");

			//TODO: remove code duplication
			spamDetected = Arrays.stream(spamProbabilities).filter(spam -> spam >= barrier).count();
			System.out.println(String.format("Spam count: %s Detected: %d (%.2f%%)",
				spamProbabilities.length, spamDetected, spamDetected * 100.0 / spamProbabilities.length));
			hamDetected = Arrays.stream(hamProbabilities).filter(ham -> ham < barrier).count();
			System.out.println(String.format("Ham count: %s Detected: %d (%.2f%%)",
				hamProbabilities.length, hamDetected, hamDetected * 100.0 / hamProbabilities.length));

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
		new Main().run();
	}
}
