package lottery.transaction;

import java.math.BigInteger;
import java.util.List;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptChunk;

public class OpenTx extends LotteryTx {
	protected byte[] secret;

	public OpenTx(byte[] rawTx, boolean testnet) throws ProtocolException, ScriptException {
		NetworkParameters params = getNetworkParameters(testnet);
		tx = new Transaction(params, rawTx);
		validateIsOpen();
		computeSecret();
	}

	public OpenTx(CommitTx commitTx, ECKey sk, byte[] secret2, BigInteger fee,
			boolean testnet) {
		// TODO create !!!
	}

	protected void computeSecret() throws ScriptException {
		Script outScript = tx.getInput(0).getScriptSig();
		List<ScriptChunk> chunks = outScript.getChunks();
		secret = chunks.get(2).data;
	}

	protected void validateIsOpen() {
		//TODO !!!		
	}
	
	public byte[] getSecret() {
		return secret.clone();
	}

}
