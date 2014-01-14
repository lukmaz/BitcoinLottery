package lottery.parameters;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import lottery.transaction.LotteryTx;

import com.google.bitcoin.core.ECKey;

public abstract class MemoryStorage {

	public abstract void saveKey(Parameters parameters, String session, ECKey key) throws IOException;

	public abstract void saveSecrets(Parameters parameters, String session, List<byte[]> secrets) throws IOException;

	public void saveSecrets(Parameters parameters, String session, byte[] secret) throws IOException {
		List<byte[]> secrets = new LinkedList<byte[]>();
		secrets.add(secret);
		saveSecrets(parameters, session, secrets);
	}

	public abstract void saveTransaction(Parameters parameters, String session,
			LotteryTx tx) throws IOException;

}
