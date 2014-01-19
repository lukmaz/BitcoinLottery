package lottery.transaction;

import java.math.BigInteger;
import java.util.List;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.bitcoin.script.ScriptChunk;

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
											.data(sk.getPubKey())
											.build());
		tx.getInput(0).setSequenceNumber(0);
		tx.addOutput(out.getValue().subtract(fee), new Address(params, Utils.sha256hash160(pk)));
	}

	public PayDepositTx(byte[] rawTx, TransactionOutput out, ECKey sk, boolean testnet) throws VerificationException {
		tx = new Transaction(getNetworkParameters(testnet), rawTx);
		validateIsIncopletePayDeposit();
		competeInScript(sk);
		tx.getInput(0).verify(out);
	}

	protected void competeInScript(ECKey sk) throws ScriptException {
		List<ScriptChunk> chunks = tx.getInput(0).getScriptSig().getChunks();
		ScriptBuilder sb = new ScriptBuilder()
									.data(chunks.get(0).data)
									.data(chunks.get(1).data)
									.data(sign(0, sk).encodeToBitcoin())
									.data(sk.getPubKey())
									.data(emptyData);
		tx.getInput(0).setScriptSig(sb.build());
	}
	
	public long getTimeLock() {
		return tx.getLockTime();
	}

	protected void validateIsIncopletePayDeposit() throws VerificationException {
		// TODO !!!
		//spends out
		//vin == vout == 1
		//proper outscript (pay-to-pkhash)
		//have timelock, 
		//same values, but one output with value 0 (it should have receiverPk == commiterPk (?))
		//same comiterPk
		//same minLength, proper MaxLength+1
	}

}
