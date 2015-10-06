package ch.fhnw.dist;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.MessagingException;

public class BayesSpamfilter {
	final int SCANCOUNT = 10;
	HashMap<String, Integer> hamMap = new HashMap<>();
	int hamMailCount = 0;
    int hamWordCount;
	HashMap<String, Integer> spamMap = new HashMap<>();
	int spamMailCount = 0;
    int spamWordCount;
    boolean scanned = false;
    
	double hamProbability = 0.5;
	double spamProbability = 0.5;
	
	public BayesSpamfilter() {
//        hamWordCount = hamMap.values().stream().mapToInt(Number::intValue).sum();
//        spamWordCount = spamMap.values().stream().mapToInt(Number::intValue).sum();
	}
	
	public boolean isSpam(InputStream mailStream) {
		MailParser parser = new MailParser();
		
		String content = null;
		try {
			content = parser.getMessage(mailStream);
		} catch (IOException | MessagingException e) {
			throw new RuntimeException(e);
		}
		Map<String, Integer> map = Stream.of(content).map(w -> w.split("\\W+")).flatMap(Arrays::stream).collect(groupingBy(Function.identity(), summingInt(e -> 1)));

		if(!scanned) {
			equalsMap();
			scanned = true;
		}
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
		double robability = (spamProbability * pordPS)/(spamProbability * pordPS + hamProbability * prodPH);
		return robability > 0.5;
	}
	
	public void addMail(InputStream stream, boolean isSpam) {
		scanned = false;
		MailParser parser = new MailParser();
		
		String content = null;
		try {
			content = parser.getMessage(stream);
		} catch (IOException | MessagingException e) {
			throw new RuntimeException(e);
		}
		Map<String, Integer> map = Stream.of(content).map(w -> w.split("\\W+")).flatMap(Arrays::stream).collect(groupingBy(Function.identity(), summingInt(e -> 1)));

        if(isSpam) {
        	Map<String, Integer> nHm = Stream.of(spamMap, map).parallel().map(Map::entrySet).flatMap(Collection::stream).collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
        	spamMap.putAll(nHm);
        	spamMailCount++;
        } else {
        	Map<String, Integer> nHm = Stream.of(hamMap, map).parallel().map(Map::entrySet).flatMap(Collection::stream).collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
        	hamMap.putAll(nHm);
        	hamMailCount++;
        }
	}

	private void equalsMap() {
		hamMap.keySet().stream().filter(s -> !spamMap.containsKey(s)).forEach(s -> {
            spamMap.put(s, 1);
        });
        spamMap.keySet().stream().filter(s -> !hamMap.containsKey(s)).forEach(s -> {
        	hamMap.put(s, 1);
        });
    }
	
	public void calcProbability(InputStream stream, boolean isSpam) {
		//TODO Wahrscheinlichkeit bestimmen durch anlernen
		hamProbability = 0.5;
		spamProbability = 0.5;
	}
	
	
	class QueueObj implements Comparator<QueueObj>{
		public int key;
		public String val;
		
		public QueueObj(int key, String val) {
			this.key = key;
			this.val = val;
		}
		
		@Override
		public int compare(QueueObj o1, QueueObj o2) {
			return o1.key < o2.key ? -1 : o1.key == o2.key ? 0 : 1;
		}
	}
}


