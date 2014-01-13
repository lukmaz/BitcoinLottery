package logic;

import java.math.BigInteger;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.ECKey.ECDSASignature;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.crypto.TransactionSignature;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet3Params;


public abstract class LotteryTx{
	protected Transaction tx;
	
	@Override
	public String toString() {
		return tx.toString(); //TODO: is it raw? change to raw / create another function
	}
	
	public byte[] toRaw() {
		return tx.bitcoinSerialize();
	}
	
	public static NetworkParameters getNetworkParameters(boolean testnet) {
		return testnet ? TestNet3Params.get() : MainNetParams.get();
	}
	
	public TransactionSignature sign(int vin, ECKey key) {
		Sha256Hash sighash = tx.hashForSignature(vin, tx.getInput(vin).getConnectedOutput().getScriptBytes(), 
				Transaction.SigHash.ALL, false);
		ECDSASignature sig = key.sign(sighash);
		return new TransactionSignature(sig, Transaction.SigHash.ALL, false);
	}
	
	public TransactionSignature sign(int vin, byte[] signature) {
		ECDSASignature sig = ECDSASignature.decodeFromDER(signature);
		return new TransactionSignature(sig, Transaction.SigHash.ALL, false);
	}
	

	public BigInteger getValue(int n) {
		return tx.getOutput(n).getValue();
	}

	public TransactionOutput getOutput(int n) {
		return tx.getOutput(n);
	}
}
