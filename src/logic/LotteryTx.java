package logic;

import com.google.bitcoin.core.Transaction;


public abstract class LotteryTx{
	protected Transaction tx;
	
	@Override
	public String toString() {
		return tx.toString(); //TODO: is it raw? change to raw / create another function
	}
	
	public byte[] toRaw() {
		return tx.bitcoinSerialize();
	}
}
