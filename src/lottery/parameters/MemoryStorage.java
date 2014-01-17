package lottery.parameters;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lottery.control.LotteryUtils;
import lottery.transaction.LotteryTx;

import com.google.bitcoin.core.ECKey;

public abstract class MemoryStorage {

	public abstract File[] saveKey(Parameters parameters, ECKey key) throws IOException;

	public abstract File saveSecrets(Parameters parameters, List<byte[]> secrets) throws IOException;

	public File saveSecret(Parameters parameters, byte[] secret) throws IOException {
		return saveSecrets(parameters, LotteryUtils.singleton(secret));
	}

	public File saveTransaction(Parameters parameters, LotteryTx tx) throws IOException {
		return saveTransactions(parameters, LotteryUtils.singleton(tx));
	}

	public abstract <T extends LotteryTx> File saveTransactions(Parameters parameters, List<T> txs) throws IOException;
}
