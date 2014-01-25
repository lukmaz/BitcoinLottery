package lottery.transaction;

import java.math.BigInteger;
import java.util.List;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.ECKey.ECDSASignature;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.crypto.TransactionSignature;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet3Params;


public abstract class LotteryTx{
	protected Transaction tx;
	protected static final byte[] emptyData = Utils.parseAsHexOrBase58("01"); //TODO: do something with warning
	
	@Override
	public String toString() {
		return tx.toString();
	}
	
	public byte[] toRaw() {
		return tx.bitcoinSerialize();
	}
	
	public static NetworkParameters getNetworkParameters(boolean testnet) { //TODO move to Utils
		return testnet ? TestNet3Params.get() : MainNetParams.get();
	}
	
	protected TransactionSignature sign(int vin, ECKey key) {
		Sha256Hash sighash = tx.hashForSignature(vin, tx.getInput(vin).getConnectedOutput().getScriptBytes(), 
				Transaction.SigHash.ALL, false);
		ECDSASignature sig = key.sign(sighash);
		return new TransactionSignature(sig, Transaction.SigHash.ALL, false);
	}
	
	protected TransactionSignature sign(int vin, byte[] signature) {
		ECDSASignature sig = ECDSASignature.decodeFromDER(signature);
		return new TransactionSignature(sig, Transaction.SigHash.ALL, false);
	}
	

	public BigInteger getValue(int k) {
		return tx.getOutput(k).getValue();
	}

	public TransactionOutput getOutput(int k) {
		return tx.getOutput(k);
	}

	public List<TransactionOutput> getOutputs() {
		return tx.getOutputs();
	}

	
}
