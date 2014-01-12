package parameters;

import java.io.IOException;

import com.google.bitcoin.core.ECKey;

public abstract class MemoryStorage {

	public abstract void saveKey(Parameters parameters, String session, ECKey key) throws IOException;

}
