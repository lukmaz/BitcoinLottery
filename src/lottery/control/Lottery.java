package lottery.control;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import lottery.parameters.MemoryStorage;
import lottery.parameters.Parameters;
import lottery.parameters.ParametersUpdater;

import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.ECKey;

public class Lottery {
	protected ParametersUpdater parametersUpdater;
	protected MemoryStorage memoryStorage;
	protected String session;
	protected Notifier notifier;
	protected ECKey sk = null;
	protected List<byte[]> pks = null;
	protected int position = 0;
	protected BigInteger stake = null;
	protected BigInteger fee = null;
	protected long lockTime = 0;
	protected int minLength = 0;
	protected byte[] secret = null;
	
	
	public Lottery(ParametersUpdater parametersUpdater, String session, 
			MemoryStorage memoryStorage, Notifier notifier) {
		super();
		this.parametersUpdater = parametersUpdater;
		this.memoryStorage = memoryStorage;
		this.session = session;
		this.notifier = notifier;
	}
	
	public void lottery() throws IOException { //TODO: move to separate class (?)
		initializationPhase();
		depositPhase();
		executionPhase();
		//Deposit phase
		// TODO
	}

	protected void initializationPhase() throws IOException {
		//Initialization phase
		Parameters parameters = parametersUpdater.getParameters();
		boolean testnet = parameters.isTestnet();
		
		try {
			sk = parametersUpdater.askSK(testnet);
			pks = parametersUpdater.askPks();
			position = getPlayerPos(sk, pks);
			stake = parametersUpdater.askStake();
			fee = parametersUpdater.askFee();
			lockTime = parametersUpdater.askLockTime();
			minLength = parametersUpdater.askMinLength();
			secret = parametersUpdater.askSecret(minLength, pks.size());
			//TODO: check values for errors
			if (secret == null) {
				secret = sampleSecret(minLength, pks.size());
			}
			memoryStorage.saveSecrets(parameters, session, secret);
			notifier.showSecret(parameters, session, secret);
		} catch (AddressFormatException e) {
			// TODO
			e.printStackTrace();
		}
	}
	
	protected byte[] sampleSecret(int minLength, int noPlayers) {
	    SecureRandom random = new SecureRandom();
	    int n = random.nextInt(noPlayers); 	//TODO: is it secure?
	    byte[] secret = new byte[minLength + n];
		random.nextBytes(secret);
		return secret;
	}

	protected int getPlayerPos(ECKey sk, List<byte[]> pks) {
		byte[] pk = sk.getPubKey();
		for (int k = 0; k < pks.size(); ++k) {
			if (Arrays.equals(pk, pks.get(k))) {
				return k+1;
			}
		}
		throw new RuntimeException("Provided secret key does not match any of provided public keys"); //TODO change exception
	}

	protected void executionPhase() {
		// TODO Auto-generated method stub
		
	}

	protected void depositPhase() {
		// TODO Auto-generated method stub
		
	}
}
