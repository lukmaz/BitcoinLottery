package lottery.transaction;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lottery.settings.BitcoinLotterySettings;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.bitcoin.script.ScriptOpCodes;

public class CommitTx extends LotteryTx {
	protected byte[] hash;
	protected byte[] commiterAddress;
	protected List<byte[]> addresses;
	protected int minLength;
	protected BigInteger stake; 
	protected int noPlayers;
	protected int position;

	public CommitTx(TransactionOutput out, ECKey sk, List<byte[]> pks, int position,
			byte[] hash, int minLength, BigInteger fee, boolean testnet) throws VerificationException {
		this.hash = hash;
		this.commiterAddress = sk.getPubKeyHash();
		this.minLength = minLength;
		this.noPlayers = pks.size();
		this.position = position;
		this.stake = out.getValue().subtract(fee).divide(BigInteger.valueOf(noPlayers-1));
		NetworkParameters params = getNetworkParameters(testnet);
		
		tx = new Transaction(params);
		tx.addInput(out);
		if (out.getScriptPubKey().isSentToAddress()) {
			tx.getInput(0).setScriptSig(ScriptBuilder.createInputScript(sign(0, sk), sk));
		}
		else if (out.getScriptPubKey().isSentToRawPubKey()) {
			tx.getInput(0).setScriptSig(ScriptBuilder.createInputScript(sign(0, sk)));
		} 
		else {
			throw new VerificationException("Bad transaction output.");
		}
		for (int k = 0; k < noPlayers; ++k) {
			BigInteger currentStake = stake;
			if (k == position) { //TODO: simplier script?
				currentStake = BigInteger.valueOf(0);
			}
			tx.addOutput(currentStake, getCommitOutScript(Utils.sha256hash160(pks.get(k))));
		}
		this.addresses = new LinkedList<byte[]>();
		for (byte[] pk : pks) {
			this.addresses.add(Utils.sha256hash160(pk));
		}
		tx.verify();
	}
	
	public CommitTx(byte[] rawTx, boolean testnet) throws VerificationException {
		tx = new Transaction(getNetworkParameters(testnet), rawTx);
		validateIsCommit();
	}
	
	public int getNoPlayers() {
		return noPlayers;
	}

	public byte[] getHash() {
		return hash;
	}

	public byte[] getAddress(int k) {
		return addresses.get(k);
	}
	
	public BigInteger getSingleDeposit() {
		return stake;
	}
	
	public int getEmptyOutputNr() {
		return position;
	}
	
	public int getMinLength() {
		return minLength;
	}
	
	public byte[] getCommiterAddress() {
		return commiterAddress;
	}
	
	protected void validateIsCommit() throws VerificationException {
		noPlayers = tx.getOutputs().size();
		if (noPlayers < 2) {
			throw new VerificationException("Wrong number of outputs.");
		}
		addresses = new LinkedList<byte[]>();
		for (int k = 0; k < noPlayers; ++k) {
			if (tx.getOutput(k).getValue().equals(BigInteger.valueOf(0))) {
				position = k;
			}
			else {
				stake = tx.getOutput(k).getValue();
			}
			if (tx.getOutput(k).getScriptPubKey().getChunks().size() < 23) {
				throw new VerificationException("Wrong outputs.");
			}
			addresses.add(tx.getOutput(k).getScriptPubKey().getChunks().get(13).data);
		}
		minLength = Integer.valueOf(Utils.bytesToHexString(tx.getOutput(0).getScriptPubKey().getChunks().get(1).data), 16);
		hash = tx.getOutput(0).getScriptPubKey().getChunks().get(6).data;
		commiterAddress = tx.getOutput(0).getScriptPubKey().getChunks().get(20).data;
		
		for (int k = 0; k < noPlayers; ++k) {
			if (!Arrays.equals(tx.getOutput(k).getScriptPubKey().getProgram(), getCommitOutScript(addresses.get(k)).getProgram())) {
				throw new VerificationException("Wrong outputs.");
			}
			if (k == position && !tx.getOutput(k).getValue().equals(BigInteger.valueOf(0))) {
				throw new VerificationException("Wrong outputs.");
			}
			else if (k != position && !tx.getOutput(k).getValue().equals(stake)) {
				throw new VerificationException("Wrong outputs.");
			} 
		}
		
		tx.verify();
	}

	protected Script getCommitOutScript(byte[] receiverAddress) {
		byte[] min = Utils.parseAsHexOrBase58(Integer.toHexString(minLength));
		byte[] max = Utils.parseAsHexOrBase58(Integer.toHexString(minLength+noPlayers));
		return new ScriptBuilder()
				.op(ScriptOpCodes.OP_SIZE)
				.data(min)
				.data(max)
				.op(ScriptOpCodes.OP_WITHIN)
				.op(ScriptOpCodes.OP_SWAP)
				.op(BitcoinLotterySettings.hashFunctionOpCode)
				.data(hash)
				.op(ScriptOpCodes.OP_EQUAL)
				.op(ScriptOpCodes.OP_BOOLAND)
				.op(ScriptOpCodes.OP_ROT)
				.op(ScriptOpCodes.OP_ROT)
				.op(ScriptOpCodes.OP_DUP)
				.op(ScriptOpCodes.OP_HASH160)
				.data(receiverAddress)
				.op(ScriptOpCodes.OP_EQUALVERIFY)
				.op(ScriptOpCodes.OP_CHECKSIG)
				.op(ScriptOpCodes.OP_BOOLOR)
				.op(ScriptOpCodes.OP_VERIFY)
				.op(ScriptOpCodes.OP_DUP)
				.op(ScriptOpCodes.OP_HASH160)
				.data(commiterAddress)
				.op(ScriptOpCodes.OP_EQUALVERIFY)
				.op(ScriptOpCodes.OP_CHECKSIG)
				.build();
	}
}
