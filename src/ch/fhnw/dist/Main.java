package ch.fhnw.dist;

import java.io.IOException;

import javax.mail.MessagingException;

public class Main {

	public static void main(String[] args) {
		ReadData rd = new ReadData();
		try {
			rd.readZip("resources/ham-anlern.zip");
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
	}
}
