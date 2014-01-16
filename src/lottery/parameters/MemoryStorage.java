package lottery.parameters;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import lottery.transaction.LotteryTx;

import com.google.bitcoin.core.ECKey;

public abstract class MemoryStorage {

	public abstract void saveKey(Parameters parameters, ECKey key) throws IOException;

	public abstract void saveSecrets(Parameters parameters, List<byte[]> secrets) throws IOException;

	public void saveSecret(Parameters parameters, byte[] secret) throws IOException {
		List<byte[]> secrets = new LinkedList<byte[]>();
		secrets.add(secret);
		saveSecrets(parameters, secrets);
	}

	public abstract void saveTransaction(Parameters parameters,
			LotteryTx tx) throws IOException;

}
