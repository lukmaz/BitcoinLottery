package lottery.transaction;

import java.math.BigInteger;
import java.util.List;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.bitcoin.script.ScriptChunk;

public class OpenTx extends LotteryTx {
	protected byte[] secret;

	public OpenTx(byte[] rawTx, boolean testnet) throws VerificationException {
		NetworkParameters params = getNetworkParameters(testnet);
		tx = new Transaction(params, rawTx);
		validateIsOpen();
		computeSecret();
	}

	public OpenTx(LotteryTx commitTx, ECKey sk, byte[] secret, BigInteger fee, boolean testnet) {
		BigInteger value = new BigInteger("0");
		NetworkParameters params = getNetworkParameters(testnet);
		int noPlayers = commitTx.getOutputs().size();
		tx = new Transaction(params);
		for (int k = 0; k < noPlayers; ++k) {
			TransactionOutput out = commitTx.getOutput(k);
			value = value.add(out.getValue());
			tx.addInput(out);
			tx.getInput(k).setScriptSig(new ScriptBuilder()
												.data(sign(k, sk).encodeToBitcoin())
												.data(emptySignature)
												.data(secret)
												.build());
		}
		tx.addOutput(value.subtract(fee), sk.toAddress(params));
	}

	protected void computeSecret() throws ScriptException {
		Script outScript = tx.getInput(0).getScriptSig();
		List<ScriptChunk> chunks = outScript.getChunks();
		secret = chunks.get(2).data;
	}

	protected void validateIsOpen() throws VerificationException {
		verifyCommon();
		verify(!tx.isTimeLocked());
		verify(tx.getOutputs().size() == 1);
		
		//TODO 
	}
	
	public byte[] getSecret() {
		return secret.clone();
	}

}
