package lottery.transaction;

import java.math.BigInteger;
import java.util.List;

import com.google.bitcoin.core.Address;
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
	protected byte[] secret = null;
	protected final int SECRET_POSITION = 4;

	public OpenTx(byte[] rawTx, boolean testnet) throws VerificationException {
		NetworkParameters params = getNetworkParameters(testnet);
		tx = new Transaction(params, rawTx);
		validateIsOpen();
		computeSecret();
	}

	public OpenTx(LotteryTx commitTx, ECKey sk, List<byte[]> pks, Address address, 
			byte[] secret, BigInteger fee, boolean testnet) throws VerificationException {
		NetworkParameters params = getNetworkParameters(testnet);
		int noPlayers = commitTx.getOutputs().size();
		tx = new Transaction(params);
		BigInteger value = new BigInteger("0");
		for (TransactionOutput out : commitTx.getOutputs()) {
			tx.addInput(out);
			value = value.add(out.getValue());
		}
		tx.addOutput(value.subtract(fee), address);
		for (int k = 0; k < noPlayers; ++k) {
			tx.getInput(k).setScriptSig(new ScriptBuilder()
												.data(sign(k, sk).encodeToBitcoin())
												.data(sk.getPubKey())
												.data(emptyData)
												.data(pks.get(k))
												.data(secret)
												.build());
			tx.getInput(k).verify();
		}
	}

	protected void computeSecret() throws ScriptException {
		Script outScript = tx.getInput(0).getScriptSig();
		List<ScriptChunk> chunks = outScript.getChunks();
		secret = chunks.get(SECRET_POSITION).data;
	}

	protected void validateIsOpen() throws VerificationException {
		verifyCommon();
		verify(!tx.isTimeLocked());
		verify(tx.getOutputs().size() == 1);
		
		//TODO !!!
	}
	
	public byte[] getSecret() {
		return secret;
	}

}
