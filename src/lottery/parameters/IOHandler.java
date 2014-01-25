package lottery.parameters;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import lottery.control.InputVerifiers.GenericVerifier;
import lottery.control.Lottery.LotteryPhases;
import lottery.transaction.PutMoneyTx;
import lottery.transaction.ClaimTx;
import lottery.transaction.CommitTx;
import lottery.transaction.ComputeTx;
import lottery.transaction.LotteryTx;
import lottery.transaction.OpenTx;
import lottery.transaction.PayDepositTx;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.TransactionOutput;

public abstract class IOHandler {
	
	public abstract ComputeTx askCompute(GenericVerifier<ComputeTx> verifier) throws IOException;
	public abstract OpenTx askOpen(GenericVerifier<OpenTx> verifier) throws IOException;
	public abstract TransactionOutput askOutput(BigInteger stake,
						GenericVerifier<TransactionOutput> verifier) throws IOException;
	public abstract List<CommitTx> askOthersCommits(int noPlayers, int position,
			GenericVerifier<CommitTx> verifier) throws IOException;
	public abstract List<PayDepositTx> askOthersPayDeposits(int noPLayers, int position,
			GenericVerifier<PayDepositTx> verifier) throws IOException;

	public abstract ECKey askSK(GenericVerifier<ECKey> verifier) throws IOException;
	public abstract Address askAddress(Address defaultAddress, GenericVerifier<Address> verifier) throws IOException;

	public abstract BigInteger askFee(GenericVerifier<BigInteger> verifier) throws IOException;
	public abstract BigInteger askStake(GenericVerifier<BigInteger> verifier) throws IOException;

	public abstract Long askLockTime(GenericVerifier<Long> verifier) throws IOException;
	public abstract Long askStartTime(long defaultTime, GenericVerifier<Long> verifier) throws IOException;
	
	public abstract Long askMinLength(GenericVerifier<Long> verifier) throws IOException;
	
	public abstract byte[] askSecret(int minLength, int noPlayers, GenericVerifier<byte[]> verifier) throws IOException;
	public abstract List<byte[]> askSecretsOrOpens(int noPlayers, int position, 
			GenericVerifier<byte[]> verifier) throws IOException;
	
	public abstract Long askNoPlayers(GenericVerifier<Long> verifier) throws IOException;
	public abstract List<byte[]> askPks(int noPlayers, GenericVerifier<byte[]> verifier) throws IOException;
	public abstract List<byte[]> askSecrets(List<byte[]> hashes, GenericVerifier<byte[]> verifier) throws IOException;

	public abstract List<PutMoneyTx> askPutMoney(int noPlayers, BigInteger stake,
			GenericVerifier<PutMoneyTx> verifier) throws IOException;
	
	public abstract List<byte[]> askSignatures(int noPlayers, int position,
			GenericVerifier<byte[]> verifier) throws IOException;
	

	public abstract void showHelp();
	
	public abstract void showVersion();

	public abstract void showKey(ECKey key, String dir, boolean testnet) throws IOException;

	public abstract void showWinner(int winner, Address address);

	public abstract void showClaimMoney(ClaimTx claimMoneyTx, String file)  throws IOException;

	public abstract void showOpenedSecret(byte[] secret, String file) throws IOException;
	public abstract void showSecret(byte[] secret, String file) throws IOException;
	public abstract void showSecrets(List<byte[]> secrets, String path) throws IOException;

	public abstract void showHash(byte[] hash) throws IOException;

	public abstract void showCommitmentScheme(LotteryTx commitTx,
			OpenTx openTx, List<PayDepositTx> payTxs, String dir) throws IOException;
	
	public abstract void showEndOfCommitmentPhase(String dir);
	
	public abstract void showCompute(ComputeTx computeTx);
	
	public abstract void showSignature(byte[] sig);

	public abstract void showWin();
	public abstract void showLost(int winner, Address address);
	public abstract void showLotteryPhase(LotteryPhases phase);
}
