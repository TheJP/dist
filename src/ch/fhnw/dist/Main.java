package ch.fhnw.dist;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		ReadData rd = new ReadData();
		try {
			rd.readZip("resources/ham-anlernen.zip");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
