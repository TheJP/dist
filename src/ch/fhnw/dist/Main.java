package ch.fhnw.dist;

import java.io.IOException;
import java.util.HashMap;

import javax.mail.MessagingException;

public class Main {

	public static void main(String[] args) {
		ReadData rd = new ReadData();
		try {
			HashMap<String, Integer> spamMap = rd.readZip("resources/ham-anlern.zip");
			for (String s : spamMap.keySet()) {
                System.out.println(s + ": " + spamMap.get(s));
            }
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
	}
}
