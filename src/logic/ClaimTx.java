package logic;

import java.math.BigInteger;
import java.util.List;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.VerificationException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ClaimTx extends LotteryTx {
	protected boolean complete;
	protected ComputeTx computeTx;
		
	public ClaimTx(ComputeTx computeTx, Address address, BigInteger fee) {
		//TODO: create
		complete = false;
		throw new NotImplementedException();
	}
	
	
	public byte[] addSignature(int n, byte[] signature) throws VerificationException {
		return null;
		//TODO
	}

	public byte[] addSignature(int n, ECKey sk) throws VerificationException {
		return null;
		//TODO
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public void addSecrets(List<String> secrets) throws VerificationException {
		if (!computeTx.checkSecrets(secrets)) {
			throw new VerificationException("wrong secrets");
		}
		//TODO
	}
	
}
