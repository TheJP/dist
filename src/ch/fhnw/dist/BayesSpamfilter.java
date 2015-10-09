package ch.fhnw.dist;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.mail.MessagingException;

public class BayesSpamfilter {
	private MailParser parser = new MailParser();
//	private static final int SCANCOUNT = 10;
	private static final double ALPHA = 0.001;
	private final HashMap<String, Integer> hamMap = new HashMap<>();
	private int hamMailCount = 0;
	private final HashMap<String, Integer> spamMap = new HashMap<>();
	private int spamMailCount = 0;


	public double probabilitySpam(ZipFile zf, ZipEntry z) {
		try {
			return probabilitySpam(zf.getInputStream(z));
		} catch (IOException | MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public double probabilitySpam(InputStream stream) throws MessagingException, IOException {
		String content = parser.getMessage(stream);
		Set<String> words = getWords(content);
		final ToDoubleFunction<DoublePair> importance = p -> Math.abs(0.5 - p.left / (p.left + p.right));
		DoublePair result = words.stream()
			.map(word -> {
				double spam = spamMap.containsKey(word) ?
					Math.max(spamMap.get(word), ALPHA) / spamMailCount : ALPHA;
				double ham = hamMap.containsKey(word) ?
					Math.max(hamMap.get(word), ALPHA) / hamMailCount : ALPHA;
				return new DoublePair(spam, ham);
			})
			.sorted((a, b) -> Double.compare(importance.applyAsDouble(b), importance.applyAsDouble(a)))
			.limit(10)
			.reduce((a, b) -> new DoublePair(a.getLeft() * b.getLeft(), a.getRight() * b.getRight()))
			.get();
		//left = P(A1 | S) * ... * P(An | S)
		//right = P(A1 | H) * ... * P(An | H)
		return result.getLeft() / (result.getLeft() + result.getRight());
	}
	
	public void addMail(ZipFile zf, ZipEntry z, boolean isSpam) {
		try {
			addMail(zf.getInputStream(z), isSpam);
		} catch (IOException | MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds the mail in the learning phase.
	 * TODO: Add desc
	 * @param stream
	 * @param isSpam
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void addMail(InputStream stream, boolean isSpam) throws MessagingException, IOException {
		String content = parser.getMessage(stream);
		if(content == null || content.equalsIgnoreCase("")){ return; }

		for(String word : getWords(content)){
			if(isSpam) { addWordCount(word, spamMap, hamMap); }
			else { addWordCount(word, hamMap, spamMap); }
		}
		if(isSpam){ ++spamMailCount; }
		else { ++hamMailCount; }
	}

	/**
	 * Adds +1 to the word count of the given word in the given Map "toMap".
	 * Makes sure that the word exists in the Map "existsInMap".
	 * @param word Word to be counted.
	 * @param toMap Map in which to increase the word count.
	 * @param existsInMap Map in which the word has to exist without changing the count.
	 */
	private void addWordCount(String word, Map<String, Integer> toMap, Map<String, Integer> existsInMap){
		if(!toMap.containsKey(word)){ toMap.put(word, 1); }
		else { toMap.put(word, 1 + toMap.get(word)); }
		if(!existsInMap.containsKey(word)){ existsInMap.put(word, 0); }
	}

	/**
	 * Returns a distinct Set of all words in the given text.
	 * @param content
	 * @return
	 */
	private Set<String> getWords(String content){
		return Arrays.stream(content.toLowerCase(Locale.ENGLISH).split("\\W+"))
			.filter(w -> !w.trim().equals(""))
			.collect(Collectors.toSet());
	}

	/**
	 * Immutable double pair, which is used in some stream calculations.
	 */
	private static class DoublePair {
		private double left, right;
		public DoublePair(double left, double right){
			this.left = left;
			this.right = right;
		}
		public double getLeft(){ return left; }
		public double getRight(){ return right; }
	}
}
