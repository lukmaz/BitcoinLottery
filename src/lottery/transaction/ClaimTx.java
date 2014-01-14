package lottery.transaction;

import java.math.BigInteger;
import java.util.List;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.crypto.TransactionSignature;
import com.google.bitcoin.script.ScriptBuilder;


public class ClaimTx extends LotteryTx {
	protected boolean complete;
	protected ComputeTx computeTx;
	protected TransactionSignature signature = null;
	protected List<byte[]> secrets = null;
		
	public ClaimTx(ComputeTx computeTx, Address address, BigInteger fee, boolean testnet) {
		this.computeTx = computeTx;
		tx = new Transaction(getNetworkParameters(testnet));
		tx.addInput(computeTx.getOutput(0));
		tx.addOutput(computeTx.getValue(0).subtract(fee), address);
		complete = false;
	}
	
	
	public byte[] setSignature(byte[] signature) throws VerificationException {
		this.signature = sign(0, signature);
		tryComplete();
		return signature;
	}

	public TransactionSignature setSignature(ECKey sk) throws VerificationException {
		signature = sign(0, sk);
		tryComplete();
		return signature;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public void addSecrets(List<byte[]> secrets) throws VerificationException {
		if (!computeTx.checkSecrets(secrets)) {
			throw new VerificationException("wrong secrets");
		}
		this.secrets = secrets;
		tryComplete();
	}
	
	protected boolean tryComplete()  throws VerificationException {
		if (signature != null && secrets != null) {
			ScriptBuilder scriptBuilder = new ScriptBuilder();
			for (byte[] secret : secrets) {
				scriptBuilder.data(secret);
			}
			int winner = computeTx.getWinner(secrets);
			for (int n = 1; n <= secrets.size(); ++n) {
				if (n == winner) {
					scriptBuilder.data(signature.encodeToBitcoin());
				}
				else {
					scriptBuilder.data(emptySignature);
				}
			}
			tx.getInput(0).setScriptSig(scriptBuilder.build());
			try {
				tx.getInput(0).verify(computeTx.getOutput(0));
			} catch (VerificationException e) {
				tx.getInput(0).setScriptSig(new ScriptBuilder().build());
				throw e;
			}
			complete = true;
			return true;
		}
		return false;
	}
}
