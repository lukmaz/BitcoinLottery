package lottery.control;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lottery.control.InputVerifiers.PkListVerifier;
import lottery.control.InputVerifiers.TxOutputVerifier;
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
	}
	
	protected void initializationPhase() throws IOException {
		//TODO: notify phase
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
		
		memoryStorage.saveSecret(parameters, secret);
		ioHandler.showSecret(parameters, secret);
	}

	protected void depositPhase() throws IOException {
		//TODO: notify phase
		boolean testnet = parameters.isTestnet();
		byte[] hash = LotteryUtils.calcHash(secret);
		ioHandler.showHash(hash); //TODO: save it?
		BigInteger deposit = stake.multiply(BigInteger.valueOf(noPlayers-1));
		TxOutputVerifier txOutputVerifier = new TxOutputVerifier(sk, deposit, testnet);
		TransactionOutput txOutput = ioHandler.askOutput(deposit, txOutputVerifier);
		//TODO notify output number (txOutputVerifier.getOutNr())
		LotteryTx commitTx = null;
		try {
			commitTx = new CommitTx(txOutput, sk, pks, position, hash, minLength, fee, testnet);
		} catch (ScriptException e) {
			// TODO 
			e.printStackTrace();
		}
		memoryStorage.saveTransaction(parameters, commitTx);
		OpenTx openTx = new OpenTx(commitTx, sk, secret, fee, testnet);
		memoryStorage.saveTransaction(parameters, openTx);
		List<PayDepositTx> payTxs = new LinkedList<PayDepositTx>();
		long protocolStart = ioHandler.askStartTime(roundCurrentTime(), new InputVerifiers.StartTimeVerifier());
		long payDepositTimestamp = protocolStart + 4 * lockTime * 60; //TODO 4? //TODO: notify
		
		for (int k = 0; k < noPlayers; ++k) {
			if (k != position) {
				PayDepositTx payTx = new PayDepositTx(commitTx, k, sk, pks.get(k), fee, payDepositTimestamp, testnet);
				payTxs.add(payTx);
				memoryStorage.saveTransaction(parameters, payTx);
			}
			else {
				payTxs.add(null); //TODO ?
				//TODO: save empty line (?)
			}
		}
		ioHandler.showCommitmentScheme(parameters, commitTx, openTx, payTxs);
//		List<CommitTx> othersCommitTxs = ioHandler.askOtherCommits();
//		List<PayDepositTx> othersPayTxs = ioHandler.askOtherCommits();
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
