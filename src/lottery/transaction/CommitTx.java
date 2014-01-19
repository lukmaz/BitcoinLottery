package lottery.transaction;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

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
		NetworkParameters params = getNetworkParameters(testnet);
		stake = out.getValue().subtract(fee).divide(BigInteger.valueOf(noPlayers-1));
		
		tx = new Transaction(params);
		tx.addInput(out); //TODO: validate !!
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
				currentStake = new BigInteger("0");
			}
			tx.addOutput(currentStake, getCommitOutScript(pks.get(k)));
		}
		this.addresses = new LinkedList<byte[]>();
		for (byte[] pk : pks) {
			this.addresses.add(Utils.sha256hash160(pk));
		}
	}
	
	public CommitTx(byte[] rawTx, boolean testnet) throws VerificationException {
		tx = new Transaction(getNetworkParameters(testnet));
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
		//TODO !!!

		noPlayers = tx.getOutputs().size();
		addresses = new LinkedList<byte[]>();
		
		for (int k = 0; k < noPlayers; ++k) {
			if (tx.getOutput(k).getValue().equals(BigInteger.valueOf(0))) {
				position = k;
			}
			else {
				stake = tx.getOutput(k).getValue();
			}
			addresses.add(tx.getOutput(k).getScriptPubKey().getChunks().get(13).data);
		}
		minLength = Integer.valueOf(Utils.bytesToHexString(tx.getOutput(0).getScriptPubKey().getChunks().get(1).data), 16);
		hash = tx.getOutput(0).getScriptPubKey().getChunks().get(6).data;
		commiterAddress = tx.getOutput(0).getScriptPubKey().getChunks().get(20).data;
		//vout >= 2
		//proper scripts
		//same hashes
		//same values, but one output with value 0 (it should have receiverPk == commiterPk (?))
		//same comiterPk
		//same minLength, proper MaxLength+1
	}

	protected Script getCommitOutScript(byte[] receiverPk) {
		byte[] min = Utils.parseAsHexOrBase58(Integer.toHexString(minLength));
		byte[] max = Utils.parseAsHexOrBase58(Integer.toHexString(minLength+noPlayers));
		return new ScriptBuilder()
				.op(ScriptOpCodes.OP_SIZE)
				.data(min)
				.data(max)
				.op(ScriptOpCodes.OP_WITHIN)
				.op(ScriptOpCodes.OP_SWAP)
				.op(ScriptOpCodes.OP_SHA256) //TODO
				.data(hash)
				.op(ScriptOpCodes.OP_EQUAL)
				.op(ScriptOpCodes.OP_BOOLAND)
				.op(ScriptOpCodes.OP_ROT)
				.op(ScriptOpCodes.OP_ROT)
				.op(ScriptOpCodes.OP_DUP)
				.op(ScriptOpCodes.OP_HASH160)
				.data(Utils.sha256hash160(receiverPk))
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
