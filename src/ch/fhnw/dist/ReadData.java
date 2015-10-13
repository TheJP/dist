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

	/**
	 * Read the Content of a zip and apply action for every ZipFile.
	 * @param fileName Name of the zipfile.
	 * @param action Action with will be performed.
	 * @throws IOException Will be thrown if file could not be opened or detected.
	 */
	public void readZip(String fileName, Function<ZipFile, Consumer<ZipEntry>> action) throws IOException {
		try(ZipFile zf = new ZipFile(fileName)){
			zf.stream().forEach(action.apply(zf));
		}
	}

	/**
	 * Read the content as ZipStream and return content as List.
	 * @param fileName Name of the ZipFile.
	 * @param action Action witch will be performed.
	 * @return List of Objects.
	 * @throws IOException Will be thrown if file could not be opened or detected.
	 */
	public <T> List<T> readZipStream(String fileName, Function<ZipFile, Function<ZipEntry, T>> action) throws IOException {
		try(ZipFile zf = new ZipFile(fileName)){
			return zf.stream().map(action.apply(zf)).collect(Collectors.toList());
		}
	}

	/**
	 * Read the content of a ZipFile and return a double array.
	 * @param fileName Name of the ZipFile.
	 * @param action Action witch will be performed.
	 * @return Return a double array.
	 * @throws IOException Will be thrown if file could not be opened or detected.
	 */
	public double[] readZipDouble(String fileName, Function<ZipFile, ToDoubleFunction<ZipEntry>> action) throws IOException {
		try(ZipFile zf = new ZipFile(fileName)){
			return zf.stream().mapToDouble(action.apply(zf)).toArray();
		}
	}

}
