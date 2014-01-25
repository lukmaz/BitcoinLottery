package lottery.transaction;


import java.math.BigInteger;
import java.util.Arrays;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.VerificationException;

public class PutMoneyTx extends LotteryTx {
	protected int outNr;
	
	public PutMoneyTx(byte[] rawTx, byte[] pkHash, BigInteger stake, 
			boolean testnet) throws VerificationException, ProtocolException {
		NetworkParameters params = getNetworkParameters(testnet);
		tx = new Transaction(params, rawTx);
		outNr = validateIsPutMoney(pkHash, stake);
	}

	protected int validateIsPutMoney(byte[] pkHash, BigInteger stake) throws VerificationException {
		tx.verify();
		for (int k = 0; k < tx.getOutputs().size(); ++k) {
			TransactionOutput out = tx.getOutput(k);
			if (out.getValue().equals(stake) && out.getScriptPubKey().isSentToAddress()) {
				if (Arrays.equals(out.getScriptPubKey().getPubKeyHash(), pkHash)) {
					return k;
				}
			}
		}
		throw new VerificationException("No output of a proper value corresponding to the expected public key.");
	}

	public byte[] getPkHash() {
		try {
			return tx.getOutput(outNr).getScriptPubKey().getPubKeyHash();
		} catch (ScriptException e) {
			return null;	//do nothing - can not happen
		}
	}
	
	public int getOutNr() {
		return outNr;
	}
	
	public TransactionOutput getOut() {
		return tx.getOutput(outNr);
	}
}
