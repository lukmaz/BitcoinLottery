package lottery.transaction;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import lottery.control.LotteryUtils;
import lottery.settings.BitcoinLotterySettings;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.bitcoin.script.ScriptChunk;
import com.google.bitcoin.script.ScriptOpCodes;

public class ComputeTx extends LotteryTx {
	protected List<byte[]> hashes;
	protected List<byte[]> pks;
	protected List<byte[]> signatures;
	protected boolean testnet;
	protected int noPlayers;
	protected int minLength;

	public ComputeTx(List<PutMoneyTx> inputs, List<byte[]> pks, List<byte[]> hashes, 
										int minLength, BigInteger fee, boolean testnet) throws VerificationException {
		this.pks = pks;
		this.hashes = hashes;
		this.testnet = testnet;
		this.minLength = minLength;
		this.noPlayers = inputs.size();
		tx = new Transaction(getNetworkParameters(testnet));
		BigInteger stake = BigInteger.ZERO;
		for (int k = 0; k < noPlayers; ++k) {
			TransactionOutput in = inputs.get(k).getOut();
			tx.addInput(in);
			stake = stake.add(in.getValue());
		}
		
		tx.addOutput(stake.subtract(fee), calculateOutScript());
		signatures = new ArrayList<byte[]>();
		for (int k = 0; k < noPlayers; ++k) {
			signatures.add(null);
		}
		tx.verify();
	}

	public ComputeTx(byte[] rawTx, List<PutMoneyTx> inputs, boolean testnet) throws VerificationException {
		NetworkParameters params = getNetworkParameters(testnet);
		tx = new Transaction(params, rawTx);
		validateIsCompute(inputs);
	}
	
	public boolean checkSecrets(List<byte[]> secrets) {
		if (hashes == null || secrets == null || hashes.size() != secrets.size()) {
			return false;
		}
		
		for (int k = 0; k < secrets.size(); ++k) {
			byte[] secret = secrets.get(k);
			byte[] sha256 = LotteryUtils.calcDoubleHash(secret);
			if (!Arrays.equals(sha256, hashes.get(k))) {
				return false;
			}
			else if (secret.length < minLength || secret.length >= minLength + noPlayers) {
				return false;
			}
		}
		
		return true;
	}

	public int getNoPlayers() {
		return tx.getInputs().size();
	}
	
	public List<byte[]> getSecretsHashes() {
		return new ArrayList<byte[]> (hashes);
	}

	//winner \in [0, noPlayers-1]
	public int getWinner(List<byte[]> secrets) throws VerificationException {
		if (!checkSecrets(secrets)) {
			throw new VerificationException("Wrong secrets");
		}
		int winner = 0;
		for (byte[] secret : secrets) {
			winner += secret.length - minLength;
		}
		
		winner = (winner % getNoPlayers());
		return winner;
	}

	public int getMinLength() {
		return minLength;
	}

	public Address getAddress(int k) {
		return new Address(getNetworkParameters(testnet), Utils.sha256hash160(pks.get(k)));
	}

	public List<byte[]> getSignatures() {
		return signatures;
	}
	
	protected Script calculateOutScript() {
		ScriptBuilder sb = new ScriptBuilder();
		byte[] dataMinLength = {(byte) minLength};
		byte[] dataNoPlayers = {(byte) noPlayers};
		
		sb.smallNum(0);
		for (int k = noPlayers-1; k >= 0; --k) {
			sb.op(ScriptOpCodes.OP_SWAP)
			  .op(ScriptOpCodes.OP_SIZE)
			  .data(dataMinLength)
			  .op(ScriptOpCodes.OP_SUB)
			  .op(ScriptOpCodes.OP_TUCK)
			  .smallNum(0)
			  .data(dataNoPlayers)
			  .op(ScriptOpCodes.OP_WITHIN)
			  .op(ScriptOpCodes.OP_VERIFY)
			  .op(BitcoinLotterySettings.hashFunctionOpCode)
			  .data(hashes.get(k))
			  .op(ScriptOpCodes.OP_EQUALVERIFY)
			  .op(ScriptOpCodes.OP_ADD);
			if (k < noPlayers-1) {
				sb.op(ScriptOpCodes.OP_DUP)
				  .data(dataNoPlayers)
				  .op(ScriptOpCodes.OP_GREATERTHANOREQUAL)
				  .op(ScriptOpCodes.OP_IF)
				  .data(dataNoPlayers)
				  .op(ScriptOpCodes.OP_SUB)
				  .op(ScriptOpCodes.OP_ENDIF);
			}
		}
		for (int k = noPlayers-1; k >= 0; --k) {
			sb.data(Utils.sha256hash160(pks.get(k)));
		}
		sb.data(dataNoPlayers);
		sb.op(ScriptOpCodes.OP_ROLL);
		sb.op(ScriptOpCodes.OP_ROLL);
		sb.data(dataNoPlayers);
		sb.op(ScriptOpCodes.OP_ROLL);
		sb.op(ScriptOpCodes.OP_DUP);
		sb.op(ScriptOpCodes.OP_HASH160);
		sb.op(ScriptOpCodes.OP_ROT);
		sb.op(ScriptOpCodes.OP_EQUALVERIFY);
		sb.data(dataNoPlayers);
		sb.op(ScriptOpCodes.OP_ROLL);
		sb.op(ScriptOpCodes.OP_SWAP);
		sb.op(ScriptOpCodes.OP_CHECKSIG);
		return sb.build();
	}

	public byte[] addSignature(int k, byte[] signature) throws VerificationException {
		tx.getInput(k).setScriptSig(new ScriptBuilder()
												.data(signature)
												.data(pks.get(k))
												.build());
		tx.getInput(k).verify();
		signatures.set(k, signature);
		return signature;
	}

	public byte[] addSignature(int k, ECKey sk) throws VerificationException {
		if (!Arrays.equals(sk.getPubKey(), pks.get(k))) {
			throw new VerificationException("Secret key does not correspond to the provided public key.");
		}	
		byte[] signature = sign(k, sk).encodeToBitcoin();
		tx.getInput(k).setScriptSig(new ScriptBuilder()
												.data(signature)
												.data(sk.getPubKey())
												.build());
		tx.getInput(k).verify();
		signatures.set(k, signature);
		return signature;
	}
	
	protected void validateIsCompute(List<PutMoneyTx> inputs) throws VerificationException {
		if (inputs != null) {
			noPlayers = inputs.size();
		}
		else {
			noPlayers = tx.getInputs().size();
		}
		if (noPlayers < 2 || noPlayers != tx.getInputs().size()) {
			throw new VerificationException("Wrong number of inputs.");
		}
		else if (tx.getOutputs().size() != 1) {
			throw new VerificationException("Wrong number of outputs.");
		}
		else if (tx.isCoinBase() || tx.isTimeLocked()) {
			throw new VerificationException("Not a Compute transaction");
		}
		for (int k = 0; k < tx.getInputs().size(); ++k) {
			TransactionInput in = tx.getInput(k);
			if (in.hasSequence()) {
				throw new VerificationException("Wrong sequence number.");
			}
			else if (in.getScriptSig().getChunks().size() != 2) {
				throw new VerificationException("Wrong sig script.");
			}
			if (inputs != null) {
				in.connect(inputs.get(k).getOut());
				in.verify();
			}
		}
		
		tx.verify();
		
		computeMinLength();
		computeSecretsHashes();
		computePks();
		computeSignatures();
		
		if (minLength < 0 || !Arrays.equals(tx.getOutput(0).getScriptPubKey().getProgram(), calculateOutScript().getProgram())) {
			throw new VerificationException("Wrong out script.");
		}
	}

	protected void computePks() throws ScriptException {
		pks = new ArrayList<byte[]>();
		for (int k = 0; k < noPlayers; ++k) {
			pks.add(tx.getInput(k).getScriptSig().getChunks().get(1).data);
		}
	}

	protected void computeSignatures() throws ScriptException {
		signatures = new ArrayList<byte[]>();
		for (int k = 0; k < noPlayers; ++k) {
			signatures.add(tx.getInput(k).getScriptSig().getChunks().get(0).data);
		}
	}
	
	protected void computeSecretsHashes() throws VerificationException {
		Script outScript = tx.getOutput(0).getScriptPubKey();
		hashes = new ArrayList<byte[]>();
		List<ScriptChunk> chunks = outScript.getChunks();
		ListIterator<ScriptChunk> it = chunks.listIterator();
		while(it.hasNext()) {
			if (it.next().equalsOpCode(BitcoinLotterySettings.hashFunctionOpCode)) {
				hashes.add(it.next().data);
			}
		}
		Collections.reverse(hashes);
		if (hashes.size() != noPlayers) {
			throw new VerificationException("Wrong out script.");
		}
	}
	
	protected void computeMinLength() throws VerificationException {
		Script outScript = tx.getOutput(0).getScriptPubKey();
		List<ScriptChunk> chunks = outScript.getChunks();
		ListIterator<ScriptChunk> it = chunks.listIterator();
		while(it.hasNext()) {
			if (it.next().equalsOpCode(ScriptOpCodes.OP_SIZE)) {
				minLength = Integer.parseInt(Utils.bytesToHexString(it.next().data), 16);
				return;
			}
		}
		throw new VerificationException("Wrong out script.");
	}
}
