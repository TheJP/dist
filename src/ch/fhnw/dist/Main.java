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

	/**
	 * Prints how many spam/ham mails would have been found with given probabilities and the given barrier.
	 * @param spamProbabilities
	 * @param hamProbabilities
	 * @param barrier
	 */
	private void checkFindings(double[] spamProbabilities,  double[] hamProbabilities, double barrier){
		long spamDetected = Arrays.stream(spamProbabilities).filter(spam -> spam >= barrier).count();
		System.out.println(String.format("Spam count: %s Detected: %d (%.2f%%)",
			spamProbabilities.length, spamDetected, spamDetected * 100.0 / spamProbabilities.length));
		long hamDetected = Arrays.stream(hamProbabilities).filter(ham -> ham < barrier).count();
		System.out.println(String.format("Ham count: %s Detected: %d (%.2f%%)",
			hamProbabilities.length, hamDetected, hamDetected * 100.0 / hamProbabilities.length));
	}

	public void run(){

		try(final Scanner scan = new Scanner(System.in)) {
			System.out.println("Learning phase");
			rd.readZip("resources/spam-anlern.zip", zf -> z -> filter.addMail(zf, z, true));
			rd.readZip("resources/ham-anlern.zip", zf -> z -> filter.addMail(zf, z, false));

//			double barrier = 1.0 - (1.0 / Math.pow(10, 10)); //72.25% / 97.31%
			double barrier = 0.99; //93.78% / 91.26%

			System.out.println("Kalibration phase");
			double[] spamProbabilities = probabilityOfZip("resources/spam-kallibrierung.zip");
			double[] hamProbabilities = probabilityOfZip("resources/ham-kallibrierung.zip");
			checkFindings(spamProbabilities, hamProbabilities, barrier);

			System.out.println("Testing phase");
			spamProbabilities = probabilityOfZip("resources/spam-test.zip");
			hamProbabilities = probabilityOfZip("resources/ham-test.zip");
			checkFindings(spamProbabilities, hamProbabilities, barrier);

			while (true) {
				System.out.println("Insert path to mail");
				String file = scan.nextLine();
				try(FileInputStream stream = new FileInputStream(file)){
					System.out.println("Probability: " + filter.probabilitySpam(stream));
				}
				String yesOrNo = null;
				do{
					if(yesOrNo != null){ System.err.println("Invalid input"); }
					System.out.println("Is Spam? y = yes, n = no");
					yesOrNo = scan.nextLine().trim();
				} while(!yesOrNo.equalsIgnoreCase("n") && !yesOrNo.equalsIgnoreCase("y"));
				try(FileInputStream stream = new FileInputStream(file)){
					filter.addMail(stream, yesOrNo.equalsIgnoreCase("y"));
				}
			}
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Main().run();
	}
}
