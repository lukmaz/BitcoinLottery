package lottery.transaction;

import java.math.BigInteger;
import java.util.List;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.TransactionOutput;

public class CommitTx extends LotteryTx {

	public CommitTx(TransactionOutput txOutput, ECKey sk, List<byte[]> pks,
			byte[] hash, BigInteger fee, boolean testnet) {
		// TODO create !!!
	}

}
