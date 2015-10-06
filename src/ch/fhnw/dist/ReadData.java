package ch.fhnw.dist;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.mail.MessagingException;

import static java.util.stream.Collectors.*;

public class ReadData {
	
	public HashMap<String, Integer> readZip(String fileName) throws IOException, MessagingException {
		final HashMap<String, Integer> hm = new HashMap<>();
		try(ZipFile zf = new ZipFile(fileName)){
			zf.stream().forEach(z -> {
				try {
					findWords(hm, zf.getInputStream(z));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
//			Enumeration<?> enumeration = zf.entries();
//			while(enumeration.hasMoreElements()) {
//				ZipEntry ze = (ZipEntry) enumeration.nextElement();
//				findWords(hm, zf.getInputStream(ze));
//			}
		}
		return hm;
	}
	
	public void findWords(HashMap<String, Integer> hm, InputStream stream) {
		MailParser parser = new MailParser();
		
		String content = null;
		try {
			content = parser.getMessage(stream);
		} catch (IOException | MessagingException e) {
			throw new RuntimeException(e);
		}
		Map<String, Integer> map = Stream.of(content).map(w -> w.split("\\W+")).flatMap(Arrays::stream).collect(groupingBy(Function.identity(), summingInt(e -> 1)));

        Map<String, Integer> nHm = Stream.of(hm, map).parallel().map(Map::entrySet).flatMap(Collection::stream).collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));

        hm.putAll(nHm);
	}

	public void equalsMap(Map<String, Integer> m1, Map<String, Integer> m2) {

        m1.keySet().stream().filter(s -> !m2.containsKey(s)).forEach(s -> {
            m2.put(s, 1);
        });
        m2.keySet().stream().filter(s -> !m1.containsKey(s)).forEach(s -> {
            m1.put(s, 1);
        });
    }
}
