package lottery.parameters;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import lottery.control.InputVerifiers.GenericVerifier;
import lottery.parameters.CommandParser.CommandArg;
import lottery.transaction.ClaimTx;
import lottery.transaction.ComputeTx;
import lottery.transaction.LotteryTx;
import lottery.transaction.OpenTx;
import lottery.transaction.PayDepositTx;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.TransactionOutput;

public abstract class IOHandler {
	protected Parameters parameters;
	
	public IOHandler(String[] args) {
		parameters = new Parameters();
		CommandArg commandArg = CommandParser.parse(args);
		parameters.setRoot(commandArg.dir);
		parameters.setCommand(commandArg.command);
		parameters.setTestnet(commandArg.testnet);
	}

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public abstract ComputeTx askCompute(GenericVerifier<ComputeTx> verifier) throws IOException;
	public abstract OpenTx askOpen(GenericVerifier<OpenTx> verifier) throws IOException;
	public abstract TransactionOutput askOutput(BigInteger stake,
						GenericVerifier<TransactionOutput> verifier) throws IOException;

	public abstract ECKey askSK(GenericVerifier<ECKey> verifier) throws IOException;
	public abstract Address askAddress(GenericVerifier<Address> verifier) throws IOException;

	public abstract BigInteger askFee(GenericVerifier<BigInteger> verifier) throws IOException;
	public abstract BigInteger askStake(GenericVerifier<BigInteger> verifier) throws IOException;

	public abstract Long askLockTime(GenericVerifier<Long> verifier) throws IOException;
	public abstract Long askStartTime(long defaultTime, GenericVerifier<Long> verifier) throws IOException;
	
	public abstract Long askMinLength(GenericVerifier<Long> verifier) throws IOException;
	
	public abstract byte[] askSecret(int minLength, int noPlayers, GenericVerifier<byte[]> verifier) throws IOException;
	
	public abstract Long askNoPlayers(GenericVerifier<Long> verifier) throws IOException;
	public abstract List<byte[]> askPks(int noPlayers, GenericVerifier<byte[]> verifier) throws IOException;
	public abstract List<byte[]> askSecrets(List<byte[]> hashes, GenericVerifier<byte[]> verifier) throws IOException;


	public abstract void showHelp();
	
	public abstract void showVersion();

	public abstract void showKey(Parameters parameters, String session, ECKey key) throws IOException;

	public abstract void showWinner(int winner);

	public abstract void showClaimMoney(Parameters parameters, String session, ClaimTx claimMoneyTx)  throws IOException;

	public abstract void showWrongSecrets(Collection<Integer> collection);

	public abstract void showSecret(Parameters parameters, String session, byte[] secret) throws IOException;

	public abstract void showHash(byte[] hash) throws IOException;

	public abstract void showCommitmentScheme(Parameters parameters, String session, LotteryTx commitTx,
			OpenTx openTx, List<PayDepositTx> payTxs) throws IOException;

}
