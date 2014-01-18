package lottery.transaction;

import java.math.BigInteger;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.VerificationException;
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

	public PayDepositTx(byte[] rawTx, TransactionOutput out, ECKey sk, boolean testnet) throws VerificationException {
		// TODO !!!
		//parse
		//add signature and data("00")
		validateIsPayDeposit();
	}
	
	public long getTimeLock() {
		// TODO !!!
		return 0;
	}

	protected void validateIsPayDeposit() throws VerificationException {
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
