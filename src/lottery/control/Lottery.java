package lottery.control;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lottery.control.InputVerifiers.OthersCommitsVerifier;
import lottery.control.InputVerifiers.OthersPaysVerifier;
import lottery.control.InputVerifiers.PkListVerifier;
import lottery.control.InputVerifiers.SignaturesVerifier;
import lottery.control.InputVerifiers.TxOutputVerifier;
import lottery.parameters.MemoryStorage;
import lottery.parameters.Parameters;
import lottery.parameters.IOHandler;
import lottery.transaction.ClaimTx;
import lottery.transaction.CommitTx;
import lottery.transaction.ComputeTx;
import lottery.transaction.LotteryTx;
import lottery.transaction.OpenTx;
import lottery.transaction.PayDepositTx;
import lottery.transaction.PutMoneyTx;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.VerificationException;

public class Lottery {
	protected Parameters parameters;
	protected IOHandler ioHandler;
	protected MemoryStorage memoryStorage;
	protected ECKey sk = null;
	protected List<byte[]> pks = null;
	protected int noPlayers = 0;
	protected int position = 0;
	protected BigInteger stake = null;
	protected BigInteger fee = null;
	protected long lockTime = 0;
	protected int minLength = 0;
	protected byte[] secret = null;
	protected ComputeTx computeTx = null;
	protected List<byte[]> hashes;
	
	
	public Lottery(IOHandler ioHandler, Parameters parameters, MemoryStorage memoryStorage) {
		super();
		this.ioHandler = ioHandler;
		this.parameters = parameters;
		this.memoryStorage = memoryStorage;
	}
	
	public void lottery() throws IOException {
		initializationPhase();
		depositPhase();
		executionPhase();
		claimMoneyPhase();
	}
	
	public enum LotteryPhases {
		INITIALIZATION_PHASE,
		DEPOSIT_PHASE,
		EXECUTION_PHASE,
		CLAIM_MONEY_PHASE,
	}
	
	protected void initializationPhase() throws IOException {
		ioHandler.showLotteryPhase(LotteryPhases.INITIALIZATION_PHASE);
		sk = ioHandler.askSK(new InputVerifiers.SkVerifier(null, parameters.isTestnet()));
		noPlayers = ioHandler.askNoPlayers(new InputVerifiers.NoPlayersVerifier()).intValue();
		PkListVerifier pkListVerifier = new PkListVerifier(sk.getPubKey(), noPlayers);
		pks = ioHandler.askPks(noPlayers, pkListVerifier);
		position = pkListVerifier.getPosition();
		stake = ioHandler.askStake(new InputVerifiers.StakeVerifier(null));
		fee = ioHandler.askFee(new InputVerifiers.FeeVerifier(stake.divide(BigInteger.valueOf(2))));
		lockTime = ioHandler.askLockTime(new InputVerifiers.LockTimeVerifier());
		minLength = ioHandler.askMinLength(new InputVerifiers.MinLengthVerifier()).intValue();
		secret = ioHandler.askSecret(minLength, noPlayers, new InputVerifiers.NewSecretVerifier(minLength, noPlayers));
		
		File secretFile = memoryStorage.saveSecret(parameters, secret);
		ioHandler.showSecret(secret, secretFile.toString());
	}

	protected void depositPhase() throws IOException {
		ioHandler.showLotteryPhase(LotteryPhases.DEPOSIT_PHASE);
		boolean testnet = parameters.isTestnet();
		NetworkParameters params = LotteryTx.getNetworkParameters(testnet);
		byte[] hash = LotteryUtils.calcHash(secret);
		ioHandler.showHash(hash); //TODO: save it?
		BigInteger deposit = stake.multiply(BigInteger.valueOf(noPlayers-1));
		TxOutputVerifier txOutputVerifier = new TxOutputVerifier(sk, deposit, testnet);
		TransactionOutput txOutput = ioHandler.askOutput(deposit, txOutputVerifier);
		//TODO notify output number (txOutputVerifier.getOutNr())
		LotteryTx commitTx = null;
		try {
			commitTx = new CommitTx(txOutput, sk, pks, position, hash, minLength, fee, testnet);
		} catch (VerificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		memoryStorage.saveTransaction(parameters, commitTx);
		OpenTx openTx = null;
		try {
			openTx = new OpenTx(commitTx, sk, pks, sk.toAddress(params), secret, fee, testnet);
		} catch (VerificationException e) {
			// TODO cannot happen
			e.printStackTrace();
		}
		memoryStorage.saveTransaction(parameters, openTx);
		List<PayDepositTx> payTxs = new LinkedList<PayDepositTx>();
		long protocolStart = ioHandler.askStartTime(roundCurrentTime(), new InputVerifiers.StartTimeVerifier());
		long payDepositTimestamp = protocolStart + lockTime * 60;
		//TODO: notify payDepositTimestamp 
		
		for (int k = 0; k < noPlayers; ++k) {
			if (k != position) {
				PayDepositTx payTx = new PayDepositTx(commitTx, k, sk, pks.get(k), fee, payDepositTimestamp, testnet);
				payTxs.add(payTx);
			}
			else {
				payTxs.add(null);
			}
		}
		File payTxsFile = memoryStorage.saveTransactions(parameters, payTxs);
		ioHandler.showCommitmentScheme(commitTx, openTx, payTxs, payTxsFile.getParent());
		OthersCommitsVerifier othersCommitsVerifier = 
				new InputVerifiers.OthersCommitsVerifier(pks, position, minLength, deposit.subtract(fee), testnet);
		List<CommitTx> othersCommitsTxs = ioHandler.askOthersCommits(noPlayers, position, othersCommitsVerifier);
		memoryStorage.saveTransactions(parameters, othersCommitsTxs);
		hashes = othersCommitsVerifier.getHashes();
		hashes.set(position, hash);
		OthersPaysVerifier othersPaysVerifier = 
				new InputVerifiers.OthersPaysVerifier(othersCommitsTxs, sk, pks, position, fee, payDepositTimestamp, testnet);
		List<PayDepositTx> othersPaysTxs = ioHandler.askOthersPayDeposits(noPlayers, position, othersPaysVerifier);
		File othersPaysFile = memoryStorage.saveTransactions(parameters, othersPaysTxs);	//TODO: !! use other filename
		ioHandler.showEndOfCommitmentPhase(othersPaysFile.getParent());
	}

	protected long roundCurrentTime() {
		long seconds = new Date().getTime() / 1000;
		return seconds - (seconds % (60 * 5));
	}

	protected void executionPhase() throws IOException {
		ioHandler.showLotteryPhase(LotteryPhases.EXECUTION_PHASE);
		boolean testnet = parameters.isTestnet();
		List<PutMoneyTx> putMoneyTxs = ioHandler.askPutMoney(noPlayers, stake, 
				new InputVerifiers.PutMoneyVerifier(pks, stake, testnet));
		memoryStorage.saveTransactions(parameters, putMoneyTxs);
		computeTx = new ComputeTx(putMoneyTxs, pks, hashes, minLength, fee, testnet);
		byte[] computeSig = null;
		try {
			computeSig = computeTx.addSignature(position, sk);
		} catch (VerificationException e) {
			// TODO should not happen
			e.printStackTrace();
		}

		if (position == 0) {
			SignaturesVerifier sigVerifier = new InputVerifiers.SignaturesVerifier(computeTx);
			ioHandler.askSignatures(noPlayers, position, sigVerifier);
			ioHandler.showCompute(computeTx);
		}
		else {
			ioHandler.showSignature(computeSig);
			computeTx = ioHandler.askCompute(new InputVerifiers.SignedComputeTxVerifier(computeTx, pks, testnet));
		}
		memoryStorage.saveTransaction(parameters, computeTx);
	}
	
	protected void claimMoneyPhase() throws IOException { //TODO: extract common part with MoneyClaimer ?
		ioHandler.showLotteryPhase(LotteryPhases.CLAIM_MONEY_PHASE);
		boolean testnet = parameters.isTestnet();
		List<byte[]> secrets = ioHandler.askSecretsOrOpens(noPlayers, position, 
				new InputVerifiers.SecretListVerifier(position, hashes, minLength, testnet));
		secrets.set(position, secret);
		memoryStorage.saveSecrets(parameters, secrets);
		
		int winner = 0;
		try {
			winner = computeTx.getWinner(secrets);
		} catch (VerificationException e1) {// can not happen
			e1.printStackTrace();
		}
		if (winner == position) {
			ioHandler.showWin();
			NetworkParameters params = LotteryTx.getNetworkParameters(testnet);
			Address address = ioHandler.askAddress(sk.toAddress(params), new InputVerifiers.AddressVerifier(testnet));
			ClaimTx claimMoneyTx = null;
			try {
				claimMoneyTx = new ClaimTx(computeTx, secrets, sk, address, fee, testnet);
			} catch (VerificationException e) {// can not happen
				e.printStackTrace();
			}
			File claimMoneyFile = memoryStorage.saveTransaction(parameters, claimMoneyTx);
			ioHandler.showClaimMoney(claimMoneyTx, claimMoneyFile.getParent());
		}
		else {
			ioHandler.showLost(winner, computeTx.getAddress(winner));
		}
	}
}
