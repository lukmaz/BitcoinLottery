package lottery.transaction;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.Base58;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptChunk;
import com.google.bitcoin.script.ScriptOpCodes;

public class ComputeTx extends LotteryTx {
	protected List<byte[]> hashes;
	protected boolean complete = false;
	protected int minLength;
		
	public ComputeTx(List<PutMoneyTx> inputs, List<byte[]> hashes, int minLength, BigInteger fee, boolean testnet) {
		//TODO: create !!!
		complete = false;
		throw new NotImplementedException();
	}
	
	public ComputeTx(byte[] rawTx, boolean testnet) throws VerificationException {
		NetworkParameters params = getNetworkParameters(testnet);
		tx = new Transaction(params, rawTx);
		validateIsCompute();
		computeSecretsHashes();
		computeMinLength();
	}

	public byte[] addSignature(int n, byte[] signature) throws VerificationException {
		//TODO pull to superclass?
		return null;
		//TODO !!!
	}

	public byte[] addSignature(int n, ECKey sk) throws VerificationException {
		//TODO pull to superclass?
		return null;
		//TODO !!!
	}
	
	public boolean isComplete() {
		//TODO pull to superclass?
		return complete;
	}
	
	protected void validateIsCompute() throws VerificationException { //TODO: different exception
		if (tx.isCoinBase() ||
				tx.isTimeLocked() ||
				tx.getInputs().size() < 2 ||
				tx.getOutputs().size() != 1) {
			throw new VerificationException("Not a Compute transaction");
		}
		tx.verify();
		complete = true;
		//TODO: make it better (check scripts, signatures, and everything)
	}

	public int getNoPlayers() {
		return tx.getInputs().size();
	}
	
	public List<byte[]> getSecretsHashes() {
		return new LinkedList<byte[]> (hashes);
	}

	protected void computeSecretsHashes() throws ScriptException {
		Script outScript = tx.getOutput(0).getScriptPubKey();
		hashes = new LinkedList<byte[]>();
		List<ScriptChunk> chunks = outScript.getChunks();
		ListIterator<ScriptChunk> it = chunks.listIterator();
		while(it.hasNext()) {
			if (it.next().equalsOpCode(ScriptOpCodes.OP_SHA256)) { //TODO: global settings for ScriptOpCodes.OP_SHA256
				hashes.add(it.next().data);
			}
		}
	}
	
	protected void computeMinLength() throws ScriptException {
		Script outScript = tx.getOutput(0).getScriptPubKey();
		List<ScriptChunk> chunks = outScript.getChunks();
		ListIterator<ScriptChunk> it = chunks.listIterator();
		while(it.hasNext()) {
			if (it.next().equalsOpCode(ScriptOpCodes.OP_NIP)) {
				minLength = Integer.parseInt(Utils.bytesToHexString(it.next().data), 16);
				break;
			}
		}
	}

	public Collection<Integer> findBadSecrets(List<byte[]> secrets) {
		if (hashes == null || secrets == null || hashes.size() != secrets.size()) {
			return null;
		}
		Collection<Integer> errors = new HashSet<Integer>();
		
		for (int n = 0; n < secrets.size(); ++n) {
			byte[] sha256 = null;
			try {
				sha256 = MessageDigest.getInstance("SHA-256").digest(secrets.get(n));
			} catch (NoSuchAlgorithmException e) {
				// TODO
				e.printStackTrace();
			}
			if (!Arrays.equals(sha256, hashes.get(n))) { //TODO!!! or length is not in [min, min+n)
				errors.add(n);
			}
		}
		
		return errors;
	}
	
	public boolean checkSecrets(List<byte[]> secrets) {
		Collection<Integer> errors = findBadSecrets(secrets);
		return errors != null && errors.size() == 0;
	}

	public int getWinner(List<byte[]> secrets) throws VerificationException {
		if (!checkSecrets(secrets)) {
			throw new VerificationException("Wrong secrets");
		}
		int winner = 0;
		for (byte[] secret : secrets) {
			winner += secret.length - minLength;
		}
		//TODO is it working? !!!
		winner = (winner % getNoPlayers()) + 1;
		return winner;
	}

	public int getMinLength() {
		// TODO !!!
		return 32;
	}

	public byte[] getPkHash(int winner) {
		// TODO !!!
		return null;
	}

	public byte[] getAddress(int winner) {
		// TODO !!!
		try {
			return Base58.decode("1M5MRn6hghaWAgCTqoJyR5FVjpcoLqUzBY");
		} catch (AddressFormatException e) {
			return null;
		}
	}
}
