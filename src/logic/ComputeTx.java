package logic;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet3Params;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptChunk;
import com.google.bitcoin.script.ScriptOpCodes;

public class ComputeTx extends LotteryTx {
	protected List<byte[]> hashes;
	protected boolean complete = false;
	protected int minLength;
		
	public ComputeTx(List<PutMoneyTx> inputs, List<String> hashes, int minLength, BigInteger fee) {
		//TODO: create !!!
		complete = false;
		throw new NotImplementedException();
	}
	
	public ComputeTx(String txString, boolean testnet) throws ProtocolException, VerificationException {
		NetworkParameters params = testnet ? TestNet3Params.get() : MainNetParams.get();
		tx = new Transaction(params, Utils.parseAsHexOrBase58(txString));
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
	
	public List<String> getSecretsHashes() throws ScriptException {
		List<String> hashStrings = new LinkedList<String>();
		for (byte[] hash : hashes) {
			hashStrings.add(Utils.bytesToHexString(hash));
		}
		return hashStrings;
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

	public boolean checkSecrets(List<String> secrets) {
		if (hashes == null || secrets == null || hashes.size() != secrets.size()) {
			return false;
		}
		
		for (int n = 0; n < secrets.size(); ++n) {
			byte[] sha256 = null;
			try {
				sha256 = MessageDigest.getInstance("SHA-256").digest(Utils.parseAsHexOrBase58(secrets.get(n)));
			} catch (NoSuchAlgorithmException e) {
				// TODO
				e.printStackTrace();
			}
			if (!Utils.bytesToHexString(sha256).equals(Utils.bytesToHexString(hashes.get(n)))) {
				return false;
			}
		}
		
		return true;
	}

	public int getWinner(List<String> secrets) throws VerificationException {
		if (!checkSecrets(secrets)) {
			throw new VerificationException("Wrong secrets");
		}
		int winner = 0;
		for (String secret : secrets) {
			winner += secret.length()/2 - minLength;
		}
		//TODO is it working? !!!
		winner = (winner % getNoPlayers()) + 1;
		return winner;
	}
}
