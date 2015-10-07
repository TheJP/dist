package ch.fhnw.dist;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.mail.MessagingException;

public class BayesSpamfilter {
	MailParser parser = new MailParser();
	final int SCANCOUNT = 10;
	final HashMap<String, Integer> hamMap = new HashMap<>();
	int hamMailCount = 0;
    int hamWordCount;
	final HashMap<String, Integer> spamMap = new HashMap<>();
	int spamMailCount = 0;
    int spamWordCount;
    double spamProbability = 0.5;
	
	public BayesSpamfilter() {
//        hamWordCount = hamMap.values().stream().mapToInt(Number::intValue).sum();
//        spamWordCount = spamMap.values().stream().mapToInt(Number::intValue).sum();
	}
	
	public double probabilitySpam(InputStream mailStream) {
		MailParser parser = new MailParser();
		
		String content = null;
		try {
			content = parser.getMessage(mailStream);
		} catch (IOException | MessagingException e) {
			throw new RuntimeException(e);
		}
		Map<String, Integer> map = Arrays.stream(content.split("\\W+")).collect(groupingBy(Function.identity(), summingInt(e -> 1)));

		PriorityQueue<QueueObj> queue = new PriorityQueue<>(map.size(), Collections.reverseOrder());
		map.entrySet().forEach(s -> queue.add(new QueueObj(s.getValue(), s.getKey())));
		
		double prodPH = 1;
		double pordPS = 1;
		for(int i = 0; i < SCANCOUNT; i++) {
			QueueObj scanObj = queue.poll();
			if(scanObj != null) {
				prodPH *= hamMailCount / hamMap.get(scanObj.val);
				pordPS *= spamMailCount / spamMap.get(scanObj.val);
			}
		}
		double probability = pordPS/(pordPS + prodPH);
		return probability;
	}
	
	public void addMail(ZipFile zf, ZipEntry z, boolean isSpam) {
		try {
			addMail(zf.getInputStream(z), isSpam);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void addMail(InputStream stream, boolean isSpam) {
		String content = null;
		try {
			content = parser.getMessage(stream);
		} catch (IOException | MessagingException e) {
			throw new RuntimeException(e);
		}

		Set<String> words = Arrays.stream(content.toLowerCase(Locale.ENGLISH).split("\\W+"))
			.distinct().filter(w -> !w.trim().equals(""))
			.collect(Collectors.toSet());

		for(String word : words){
			if(isSpam) { addWordCount(word, spamMap, hamMap); }
			else { addWordCount(word, hamMap, spamMap); }
		}
		if(isSpam){ ++spamMailCount; }
		else { ++hamMailCount; }
	}

	private void addWordCount(String word, Map<String, Integer> toMap, Map<String, Integer> existsInMap){
		if(!toMap.containsKey(word)){ toMap.put(word, 1); }
		else { toMap.put(word, 1 + toMap.get(word)); }
		if(!existsInMap.containsKey(word)){ existsInMap.put(word, 0); }
	}
	
	public boolean isSpam(InputStream stream) {
		return probabilitySpam(stream) > spamProbability;
	}
	
	public void calcProbability(InputStream stream, boolean isSpam) {
		if(isSpam(stream)) {
			if(!isSpam) {
				spamProbability -= 1 / (spamMailCount + hamMailCount);
			}
		} else if (isSpam) {
			spamProbability += 1 / (spamMailCount + hamMailCount);
		}
	}
	
	
	class QueueObj implements Comparable<QueueObj>{
		public int key;
		public String val;
		
		public QueueObj(int key, String val) {
			this.key = key;
			this.val = val;
		}

		@Override
		public int compareTo(QueueObj o) {
			return key < o.key ? -1 : key == o.key ? 0 : 1;
		}
	}
}


