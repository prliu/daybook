package tw.com.sunnybay.daybook;

public class KeyValuePair<K, V> {

	public final K key;
	public final V value;
	
	public KeyValuePair(K k, V v) {
		this.key = k;
		this.value = v;
	}
}
