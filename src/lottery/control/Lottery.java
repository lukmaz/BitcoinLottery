package lottery.control;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lottery.parameters.MemoryStorage;
import lottery.parameters.Parameters;
import lottery.parameters.IOHandler;
import lottery.transaction.CommitTx;
import lottery.transaction.LotteryTx;
import lottery.transaction.OpenTx;
import lottery.transaction.PayDepositTx;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.TransactionOutput;

public class Lottery {
	protected IOHandler ioHandler;
	protected MemoryStorage memoryStorage;
	protected String session;
	protected ECKey sk = null;
	protected List<byte[]> pks = null;
	protected int noPlayers = 0;
	protected int position = 0;
	protected BigInteger stake = null;
	protected BigInteger fee = null;
	protected long lockTime = 0;
	protected int minLength = 0;
	protected byte[] secret = null;
	
	
	public Lottery(IOHandler parametersUpdater, String session, 
			MemoryStorage memoryStorage) {
		super();
		this.ioHandler = parametersUpdater;
		this.memoryStorage = memoryStorage;
		this.session = session;
	}
	
	public void lottery() throws IOException {
		initializationPhase();
		depositPhase();
		executionPhase();
	}
	
	protected void initializationPhase() throws IOException {
		//TODO: notify phase
		Parameters parameters = ioHandler.getParameters();
		sk = ioHandler.askSK(new InputVerifiers.SkVerifier(parameters.isTestnet()));
		noPlayers = ioHandler.askNoPlayers(new InputVerifiers.NoPlayersVerifier()).intValue();
		pks = ioHandler.askPks(noPlayers, new InputVerifiers.PkListVerifier());
		position = getPlayerPos(sk, pks);
		stake = ioHandler.askStake(new InputVerifiers.StakeVerifier());
		fee = ioHandler.askFee(new InputVerifiers.FeeVerifier());
		lockTime = ioHandler.askLockTime(new InputVerifiers.LockTimeVerifier());
		minLength = ioHandler.askMinLength(new InputVerifiers.MinLengthVerifier()).intValue();
		secret = ioHandler.askSecret(minLength, noPlayers, new InputVerifiers.NewSecretVerifier(minLength, noPlayers));
		memoryStorage.saveSecrets(parameters, session, secret);
		ioHandler.showSecret(parameters, session, secret);
	}

	protected int getPlayerPos(ECKey sk, List<byte[]> pks) { //TODO !!! merge into verifier ?
		byte[] pk = sk.getPubKey();
		for (int k = 0; k < noPlayers; ++k) {
			if (Arrays.equals(pk, pks.get(k))) {
				return k;
			}
		}
		throw new RuntimeException("Provided secret key does not match any of provided public keys"); //TODO change exception
	}

	protected void depositPhase() throws IOException {
		//TODO: notify phase
		MessageDigest SHA256 = null;
		Parameters parameters = ioHandler.getParameters();
		boolean testnet = parameters.isTestnet(); 
		try {
			SHA256 = MessageDigest.getInstance("SHA-256"); //TODO: global settings for hash function
		} catch (NoSuchAlgorithmException e) {
			// TODO
			e.printStackTrace();
		}
		SHA256.update(secret);
		byte[] hash = SHA256.digest();
		ioHandler.showHash(hash); //TODO: save it?
		BigInteger deposit = stake.multiply(BigInteger.valueOf(noPlayers-1));
		TransactionOutput txOutput = ioHandler.askOutput(deposit, new InputVerifiers.TxOutputVerifier(deposit, testnet));
		LotteryTx commitTx = null;
		try {
			commitTx = new CommitTx(txOutput, sk, pks, position, hash, minLength, fee, testnet);
		} catch (ScriptException e) {
			// TODO 
			e.printStackTrace();
		}
		memoryStorage.saveTransaction(parameters, session, commitTx);
		OpenTx openTx = new OpenTx(commitTx, sk, secret, fee, testnet);
		memoryStorage.saveTransaction(parameters, session, openTx);
		List<PayDepositTx> payTxs = new LinkedList<PayDepositTx>();
		long protocolStart = ioHandler.askStartTime(roundCurrentTime(), new InputVerifiers.StartTimeVerifier());
		long payDepositTimestamp = protocolStart + 4 * lockTime * 60; //TODO 4? //TODO: notify
		
		for (int k = 0; k < noPlayers; ++k) {
			if (k != position) {
				PayDepositTx payTx = new PayDepositTx(commitTx, k, sk, pks.get(k), fee, payDepositTimestamp, testnet);
				payTxs.add(payTx);
				memoryStorage.saveTransaction(parameters, session, payTx);
			}
			else {
				payTxs.add(null); //TODO ?
				//TODO: save empty line (?)
			}
		}
		ioHandler.showCommitmentScheme(parameters, session, commitTx, openTx, payTxs);
//		List<CommitTx> othersCommitTxs = parametersUpdater.askOtherCommits();
//		List<PayDepositTx> othersPayTxs = parametersUpdater.askOtherCommits();
		//TODO: ask for other players commits and payDeposits
		//TODO: check them, extract hashes
		//TODO: sign received payDeposits
		//TODO: save everything and notify
		// TODO 
	}

	protected long roundCurrentTime() {
		long seconds = new Date().getTime() / 1000;
		return seconds - (seconds % (60 * 5));
	}

	protected void executionPhase() {
		//TODO: notify phase
		// TODO
		
	}
}
