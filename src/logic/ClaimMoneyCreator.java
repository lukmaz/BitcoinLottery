package logic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet3Params;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptChunk;
import com.google.bitcoin.script.ScriptOpCodes;

public class ClaimMoneyCreator {
	protected Transaction tx;
	protected boolean testnet;
	protected List<byte[]> hashes;

	public ClaimMoneyCreator(String txString, boolean testnet) throws ProtocolException {
		this.testnet = testnet;
		NetworkParameters params = testnet ? TestNet3Params.get() : MainNetParams.get();
		tx = new Transaction(params, Utils.parseAsHexOrBase58(txString));
		//TODO: check if it is compute (using class Compute in logic)
	}
	
	public int getNoPlayers() {
		return tx.getInputs().size();
	}
	
	public List<String> getSecretsHashes() throws ScriptException {
		//TODO: move to class Compute in logic
		Script outScript = tx.getOutput(0).getScriptPubKey();
		hashes = new LinkedList<byte[]>();
		List<ScriptChunk> chunks = outScript.getChunks();
		ListIterator<ScriptChunk> it = chunks.listIterator();
		while(it.hasNext()) {
			if (it.next().equalsOpCode(ScriptOpCodes.OP_SHA256)) { //TODO: global settings for ScriptOpCodes.OP_SHA256
				hashes.add(it.next().data);
			}
		}
		
		List<String> hashStrings = new LinkedList<String>();
		for (byte[] hash : hashes) {
			hashStrings.add(Utils.bytesToHexString(hash));
		}
		return hashStrings;
	}

	public boolean checkSecrets(List<String> secrets) throws NoSuchAlgorithmException {
		//TODO: move to class Compute in logic
		if (hashes == null || secrets == null || hashes.size() != secrets.size()) {
			return false;
		}
		
		for (int n = 0; n < secrets.size(); ++n) {
			byte[] sha256 = MessageDigest.getInstance("SHA-256").digest(Utils.parseAsHexOrBase58(secrets.get(n)));
			if (!Utils.bytesToHexString(sha256).equals(Utils.bytesToHexString(hashes.get(n)))) {
				return false;
			}
		}
		
		return true;
	}

}
