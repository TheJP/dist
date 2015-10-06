package ch.fhnw.dist;

import java.io.IOException;
import java.util.HashMap;

import javax.mail.MessagingException;

public class Main {

	public static void main(String[] args) {
		ReadData rd = new ReadData();
		try {
			HashMap<String, Integer> spamMap = rd.readZip("resources/spam-anlern.zip");
			HashMap<String, Integer> hamMap = rd.readZip("resources/ham-anlern.zip");
			//1. Ausgleich Listen (beni)
			rd.equalsMap(spamMap, hamMap);
			//2. Wahrscheinlichkeit bestimmen durch anlernen
			double hamSpam = 1/2;
			//3. Testen mit ham und spam test
			HashMap<String, Integer> hamTestMap = rd.readZip("resources/ham-test.zip");
			HashMap<String, Integer> spamTestMap = rd.readZip("resources/spam-test.zip");
			
			for (String s : spamMap.keySet()) {
                System.out.println(s + ": " + spamMap.get(s));
            }
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
	}
}
