package ch.fhnw.dist;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class BayesSpamfilters {
	final int SCANCOUNT = 10;
	HashMap<String, Integer> hamMap;
    final int hamCount;
	HashMap<String, Integer> spamMap;
    final int spamCount;
	
	public BayesSpamfilters(HashMap<String, Integer> hamMap, HashMap<String, Integer> spamMap) {
		if(hamMap.size() != spamMap.size()) {
			throw new IllegalArgumentException("ham and spam map have not the same size!");
		}
		this.hamMap = hamMap; 
		this.spamMap = spamMap;
        hamCount = hamMap.values().stream().mapToInt(Number::intValue).sum();
        spamCount = spamMap.values().stream().mapToInt(Number::intValue).sum();
	}
	
	public boolean isSpam(HashMap<String, Integer> mail) {
		PriorityQueue<QueueObj> queue = new PriorityQueue<>(mail.size(), Collections.reverseOrder());
		mail.entrySet().forEach(s -> queue.add(new QueueObj(s.getValue(), s.getKey())));
		
		for(int i = 0; i < SCANCOUNT; i++) {
			QueueObj scanObj = queue.poll();
			if(scanObj != null) {
				
			}
		}
		
		return false;
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


