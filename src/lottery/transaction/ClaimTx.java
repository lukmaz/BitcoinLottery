package lottery.transaction;

import java.math.BigInteger;
import java.util.List;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.crypto.TransactionSignature;
import com.google.bitcoin.script.ScriptBuilder;


public class ClaimTx extends LotteryTx {
		
	public ClaimTx(ComputeTx computeTx, List<byte[]> secrets, ECKey sk, 
						Address address, BigInteger fee, boolean testnet) throws VerificationException {
		tx = new Transaction(getNetworkParameters(testnet));
		tx.addInput(computeTx.getOutput(0));
		tx.addOutput(computeTx.getValue(0).subtract(fee), address);
		TransactionSignature signature = sign(0, sk);
		completeInScript(computeTx, secrets, signature);
	}
	
	protected void completeInScript(ComputeTx computeTx, List<byte[]> secrets, 
				TransactionSignature signature)  throws VerificationException {
		ScriptBuilder scriptBuilder = new ScriptBuilder();
		int winner = computeTx.getWinner(secrets);
		//TODO !!! ?
		for (int k = 0; k < secrets.size(); ++k) {
			if (k == winner) {
				scriptBuilder.data(signature.encodeToBitcoin());
			}
			else {
				scriptBuilder.data(emptyData);
			}
		}
		for (byte[] secret : secrets) {
			scriptBuilder.data(secret);
		}
		tx.getInput(0).setScriptSig(scriptBuilder.build());
		tx.getInput(0).verify(computeTx.getOutput(0));
	}
}
