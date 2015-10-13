package ch.fhnw.dist;

/**
 * String, Double pair. (String key, Double value)
 */
public class SDPair {
	private final String key;
	private final double value;
	public SDPair(String key, double value){
		this.key = key;
		this.value = value;
	}
	public String getKey() { return key; }
	public double getValue() { return value; }
}
