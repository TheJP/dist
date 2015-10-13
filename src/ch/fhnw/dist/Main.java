package ch.fhnw.dist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

public class Main {

	private final BayesSpamfilter filter = new BayesSpamfilter();
	private final ReadData rd = new ReadData();
	private double barrier = 0.5;
	private final static double BARRIER_DIFF = 0.05;
	private final static double STEP = 0.06;

	private double[] probabilityOfZip(String filename) throws IOException, MessagingException{
		return rd.readZipDouble(filename, zf -> z -> filter.probabilitySpam(zf, z));
	}

	private Map<String, Double> probabilityMapOfZip(String filename) throws IOException, MessagingException{
		return rd.readZipStream(filename, zf -> z ->
				new SDPair(z.getName(), filter.probabilitySpam(zf, z)))
			.stream()
			.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}

	private double[] mapToArray(Map<String, Double> map){
		return map.values().stream().mapToDouble(v -> v.doubleValue()).toArray();
	}

	/**
	 * Prints how many spam/ham mails would have been found with given probabilities and the given barrier.
	 * @param spamProbabilities
	 * @param hamProbabilities
	 * @param barrier
	 */
	private double checkFindings(double[] spamProbabilities,  double[] hamProbabilities, double barrier, boolean output){
		long spamDetected = Arrays.stream(spamProbabilities).filter(spam -> spam >= barrier).count();
		double percSpam = spamDetected * 100.0 / spamProbabilities.length;
		long hamDetected = Arrays.stream(hamProbabilities).filter(ham -> ham < barrier).count();
		double percHam = hamDetected * 100.0 / hamProbabilities.length;
		if(output) {
			System.out.println(String.format("Spam count: %s Detected: %d (%.2f%%)",
					spamProbabilities.length, spamDetected, percSpam));
			System.out.println(String.format("Ham count: %s Detected: %d (%.2f%%)",
					hamProbabilities.length, hamDetected, percHam));
		}
//		return (percSpam + percHam) / 2;
		return percHam;
	}

	/**
	 * Reads additional mails from paths entered on the console to the spam filter.
	 * @param scan Standard input
	 * @throws IOException
	 * @throws MessagingException
	 */
	private void readAdditionalMails(final Scanner scan) throws IOException, MessagingException{
		while (true) {
			System.out.println("Insert path to mail or Quit with ':q'");
			String file = scan.nextLine();
			if (":q".equalsIgnoreCase(file)) {
                System.exit(0);
            }
			try(FileInputStream stream = new FileInputStream(file)){
				System.out.println(String.format("Probability: %.2f%%", filter.probabilitySpam(stream)*100));
			} catch (FileNotFoundException e) {
                System.err.println(e.getMessage());
                continue;
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
	}

	/**
	 * Set Barrier to lower to improve the ham detection.
	 * @param spamCalibProbabilities Probabilities that were derived from the spam calibration mails.
	 * @param hamCalibProbabilities Probabilities that were derived from the ham calibration mails.
	 */
	private void calibrateBarrier(double[] spamCalibProbabilities, double[] hamCalibProbabilities){
		double percPlus = checkFindings(spamCalibProbabilities, hamCalibProbabilities, barrier + STEP, false);
		double perc = checkFindings(spamCalibProbabilities, hamCalibProbabilities, barrier, false);
		double percMinus = checkFindings(spamCalibProbabilities, hamCalibProbabilities, barrier - STEP, false);
		while (perc < percPlus && barrier < 0.9) {
			barrier += STEP;
			perc = percPlus;
			percPlus = checkFindings(spamCalibProbabilities, hamCalibProbabilities, barrier + STEP, false);
		}
		while (perc < percMinus && barrier < 0.9) {
			barrier += STEP;
			perc = percMinus;
			percMinus = checkFindings(spamCalibProbabilities, hamCalibProbabilities, barrier + STEP, false);
		}
		System.out.println(String.format("Barrier: %.2f%%",barrier*100));
	}
	
	public void run(){
		try(final Scanner scan = new Scanner(System.in)) {
			System.out.println("Learning phase");
			rd.readZip("resources/spam-anlern.zip", zf -> z -> filter.addMail(zf, z, true));
			rd.readZip("resources/ham-anlern.zip", zf -> z -> filter.addMail(zf, z, false));

//			double barrier = 1.0 - (1.0 / Math.pow(10, 10)); //72.25% / 97.31%
//			double barrier = 0.99; //93.78% / 91.26%

			System.out.println("Kalibration phase");
			final Map<String, Double> spamCalibProbabilities = probabilityMapOfZip("resources/spam-kallibrierung.zip");
			final Map<String, Double> hamCalibProbabilities = probabilityMapOfZip("resources/ham-kallibrierung.zip");

			//Add mails to spam/ham if they are detected wrong or almost wrong (with a barrier of 0.5)
			rd.readZip("resources/spam-kallibrierung.zip", zf -> z -> {
				if(spamCalibProbabilities.get(z.getName()) <= barrier + BARRIER_DIFF){ filter.addMail(zf, z, true); }});
			rd.readZip("resources/ham-kallibrierung.zip", zf -> z -> {
				if(hamCalibProbabilities.get(z.getName()) >= barrier - BARRIER_DIFF){ filter.addMail(zf, z, false); }});

			//Calibrate barrier
			calibrateBarrier(mapToArray(spamCalibProbabilities), mapToArray(hamCalibProbabilities));

			System.out.println("Testing phase");
			double[] spamProbabilities = probabilityOfZip("resources/spam-test.zip");
			double[] hamProbabilities = probabilityOfZip("resources/ham-test.zip");
			checkFindings(spamProbabilities, hamProbabilities, barrier, true);

			//Request form console for new mails.
			readAdditionalMails(scan);

		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Main().run();
	}
}
