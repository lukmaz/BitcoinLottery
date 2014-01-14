package lottery.transaction;

import java.math.BigInteger;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.script.ScriptBuilder;

public class PayDepositTx extends LotteryTx {

	public PayDepositTx(LotteryTx commitTx, int outNr, ECKey sk, byte[] pk, BigInteger fee,
			long timestamp, boolean testnet) {
		TransactionOutput out = commitTx.getOutput(outNr);
		NetworkParameters params = getNetworkParameters(testnet);
		tx = new Transaction(params);
		tx.setLockTime(timestamp);
		tx.addInput(out);
		tx.getInput(0).setScriptSig(new ScriptBuilder()
											.data(sign(0, sk).encodeToBitcoin())
											.build());
		tx.getInput(0).setSequenceNumber(0);
		tx.addOutput(out.getValue(), new Address(params, Utils.sha256hash160(pk)));
	}

}
