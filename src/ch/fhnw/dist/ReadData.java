package ch.fhnw.dist;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Reads zip files by name and applies a given function to each element.
 */
public class ReadData {

	public void readZip(String fileName, Function<ZipFile, Consumer<ZipEntry>> action) throws IOException {
		try(ZipFile zf = new ZipFile(fileName)){
			zf.stream().forEach(action.apply(zf));
		}
	}

	public <T> List<T> readZipStream(String fileName, Function<ZipFile, Function<ZipEntry, T>> action) throws IOException {
		try(ZipFile zf = new ZipFile(fileName)){
			return zf.stream().map(action.apply(zf)).collect(Collectors.toList());
		}
	}

	public double[] readZipDouble(String fileName, Function<ZipFile, ToDoubleFunction<ZipEntry>> action) throws IOException {
		try(ZipFile zf = new ZipFile(fileName)){
			return zf.stream().mapToDouble(action.apply(zf)).toArray();
		}
	}

}
