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
		completeInScript(computeTx, secrets, sk, address);
	}
	
	protected void completeInScript(ComputeTx computeTx, List<byte[]> secrets, 
										ECKey sk, Address address)  throws VerificationException {
		TransactionSignature signature = sign(0, sk);
		ScriptBuilder scriptBuilder = new ScriptBuilder();
		scriptBuilder.data(signature.encodeToBitcoin())
					 .data(sk.getPubKey());
		for (byte[] secret : secrets) {
			scriptBuilder.data(secret);
		}
		tx.getInput(0).setScriptSig(scriptBuilder.build());
		tx.getInput(0).verify(computeTx.getOutput(0));
	}
}
