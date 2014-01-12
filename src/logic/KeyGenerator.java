package logic;

import com.google.bitcoin.core.ECKey;

public class KeyGenerator {
	public ECKey generate() {
		return new ECKey();
	}
}
