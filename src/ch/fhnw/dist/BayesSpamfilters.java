package ch.fhnw.dist;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class BayesSpamfilters {
	final int SCANCOUNT = 10;
	HashMap<String, Double> hamMap;
	HashMap<String, Double> spamMap;
	
	public BayesSpamfilters(HashMap<String, Double> hamMap, HashMap<String, Double> spamMap) {
		this.hamMap = hamMap; 
		this.spamMap = spamMap;
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


