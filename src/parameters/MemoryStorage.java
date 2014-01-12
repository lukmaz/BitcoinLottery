package parameters;

import com.google.bitcoin.core.ECKey;

public abstract class MemoryStorage {

	public abstract void saveKey(String root, String subdir, String session, ECKey key);

}
