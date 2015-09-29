package ch.fhnw.dist;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ReadData {
	
	
	public HashMap<String, Integer> readZip(String fileName) throws IOException {
		final HashMap<String, Integer> hm = new HashMap<>();
		ZipFile zf = new ZipFile(fileName);
		Enumeration<ZipEntry> enumeration = (Enumeration<ZipEntry>) zf.entries();
		while(enumeration.hasMoreElements()) {
			ZipEntry ze = enumeration.nextElement();
			zf.getInputStream(ze);
		}
		return hm;
	}
	
	public void findWords(HashMap<String, Integer> hm, InputStream istram) {
		
	}
}
