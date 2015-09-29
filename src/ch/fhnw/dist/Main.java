package ch.fhnw.dist;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		if(args.length != 1) {
			return;
		}
		ReadData rd = new ReadData();
		try {
			rd.readZip(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
