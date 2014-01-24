package lottery.transaction;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lottery.control.LotteryUtils;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.bitcoin.script.ScriptChunk;

public class OpenTx extends LotteryTx {
	protected List<byte[]> possibleSecrets;
	protected byte[] hash;
	protected final int SECRET_POSITION = 4;

	public OpenTx(byte[] rawTx, byte[] hash, boolean testnet) throws VerificationException {
		this.hash = hash;
		possibleSecrets = new LinkedList<byte[]>();
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
		BigInteger value = BigInteger.valueOf(0);
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

	protected void computeSecret() throws VerificationException {
		for (TransactionInput input : tx.getInputs()) {
			List<ScriptChunk> chunks = input.getScriptSig().getChunks();
			if (chunks.size() == 5) {
				byte[] data = chunks.get(SECRET_POSITION).data;
				if (hash != null) {
					if (Arrays.equals(hash, LotteryUtils.calcHash(data))) {
						possibleSecrets.add(data);
						return;
					}
				}
				else {
					possibleSecrets.add(data);
				}
			}
		}
		
		if (possibleSecrets.size() == 0) {
			throw new VerificationException("Not an Open transaction.");
		}
	}

	protected void validateIsOpen() throws VerificationException {
		computeSecret();
	}
	
	public byte[] getSecret() {
		if (possibleSecrets.size() > 0) {
			return possibleSecrets.get(0);
		}
		else {
			return null;
		}
	}
	
	public List<byte[]> getPossibleSecrets() {
		return possibleSecrets;
	}

}
