package parameters;

import java.io.IOException;
import java.util.List;

import logic.LotteryTx;

import com.google.bitcoin.core.ECKey;

public abstract class MemoryStorage {

	public abstract void saveKey(Parameters parameters, String session, ECKey key) throws IOException;

	public abstract void saveTransaction(Parameters parameters, String session, LotteryTx tx) throws IOException;

	public abstract void saveSecrets(Parameters parameters, String session, List<String> secrets) throws IOException ;
}
