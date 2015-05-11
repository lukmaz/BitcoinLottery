package lottery.transaction;

import java.math.BigInteger;
import java.util.Arrays;
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

	public PayDepositTx(CommitTx commitTx, int outNr, ECKey sk, byte[] pk, BigInteger fee,
			long timestamp, boolean testnet) throws VerificationException {
		TransactionOutput out = commitTx.getOutput(outNr);
		NetworkParameters params = getNetworkParameters(testnet);
		tx = new Transaction(params);
		tx.setLockTime(timestamp);
		tx.addOutput(out.getValue().subtract(fee), new Address(params, Utils.sha256hash160(pk)));
		tx.addInput(out);
		tx.getInput(0).setSequenceNumber(0);
		tx.getInput(0).setScriptSig(new ScriptBuilder()
											.data(sign(0, sk).encodeToBitcoin())
											.data(sk.getPubKey())
											.build());
		tx.verify();
	}

	public PayDepositTx(byte[] rawTx, TransactionOutput out, ECKey sk, boolean testnet) throws VerificationException {
		tx = new Transaction(getNetworkParameters(testnet), rawTx);
		validateIsIncopletePayDeposit(sk);
		tx.getInput(0).connect(out);
		computeInScript(sk);
		tx.verify();
		tx.getInput(0).verify();
	}
	
	public long getTimeLock() {
		return tx.getLockTime();
	}
	
	protected void computeInScript(ECKey sk) throws ScriptException {
		List<ScriptChunk> chunks = tx.getInput(0).getScriptSig().getChunks();
	  byte[] sig = sign(0, sk).encodeToBitcoin();
		ScriptBuilder sb = new ScriptBuilder()
									.data(chunks.get(0).data)
									.data(chunks.get(1).data)
									.data(sig)
									.data(sk.getPubKey())
									.data(sig);  // dummy secret
		tx.getInput(0).setScriptSig(sb.build());
	}

	protected void validateIsIncopletePayDeposit(ECKey sk) throws VerificationException {
		if (tx.getInputs().size() != 1) {
			throw new VerificationException("Wrong number of inputs.");
		}
		else if (tx.getOutputs().size() != 1) {
			throw new VerificationException("Wrong number of outputs.");
		}
		else if (tx.getInput(0).getSequenceNumber() != 0) {
			throw new VerificationException("Wrong sequence number.");
		}
		else if (tx.getLockTime() < Transaction.LOCKTIME_THRESHOLD) {
			throw new VerificationException("Wrong lock time.");
		}
		else if (tx.getInput(0).getScriptSig().getChunks().size() != 2) {
			throw new VerificationException("Wrong script sig.");
		}
		else if (!tx.getOutput(0).getScriptPubKey().isSentToAddress()) {
			throw new VerificationException("Wrong out script.");
		}
		else if (!Arrays.equals(tx.getOutput(0).getScriptPubKey().getPubKeyHash(), sk.getPubKeyHash())) {
			throw new VerificationException("Wrong transaction's recipient.");
		}
	}
}
