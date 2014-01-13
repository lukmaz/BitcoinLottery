package lottery;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import logic.ClaimTx;
import logic.ComputeTx;
import logic.KeyGenerator;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.VerificationException;

import parameters.MemoryStorage;
import parameters.Parameters;
import parameters.ParametersUpdater;

public class Controller {

	protected ParametersUpdater parametersUpdater;
	protected MemoryStorage memoryStorage;
	protected Notifier notifier;
	protected String session;
		
	public Controller(ParametersUpdater parametersUpdater,
			MemoryStorage memoryStorage, Notifier notifier) {
		Long lDateTime = new Date().getTime();
		this.parametersUpdater = parametersUpdater;
		this.memoryStorage = memoryStorage;
		this.notifier = notifier;
		this.session = lDateTime.toString(); 
	}

	public void run() {
		switch (parametersUpdater.getParameters().getCommand()) {
			case VERSION:
				showVersion();
				break;
			case HELP:
				showHelp();
				break;
			case GENERATE_KEYS:
				generateKeys();
				break;
			case CLAIM_MONEY:
				claimMoney();
				break;
			case LOTTERY:
				lottery();
				break;
			default:
				throw new RuntimeException("BitcoinLottery: Illegal command.");
		}
		
	}

	protected void claimMoney() {
		Parameters parameters = parametersUpdater.getParameters();
		String txString = null;
		try {
			txString = parametersUpdater.askCompute();
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		}
		ComputeTx computeTx = null;
		try {
			computeTx = new ComputeTx(txString, parameters.isTestnet());
		} catch (ProtocolException e) {
			// TODO
			e.printStackTrace();
		} catch (VerificationException e) {
			// TODO
			e.printStackTrace();
		}
		try {
			memoryStorage.saveTransaction(parameters, session, computeTx);
		} catch (IOException e1) {
			// TODO
			e1.printStackTrace();
		}

		List<String> secrets = null;
		try {
			secrets = parametersUpdater.askSecrets(computeTx.getSecretsHashes());
		} catch (ScriptException e) {
			// TODO
			e.printStackTrace();
		}
		try {
			memoryStorage.saveSecrets(parameters, session, secrets);
		} catch (IOException e1) {
			// TODO
			e1.printStackTrace();
		}
		
		boolean secretsCorrect = false;
		secretsCorrect = computeTx.checkSecrets(secrets);
		if (secretsCorrect) {
			int winner = 0;
			try {
				winner = computeTx.getWinner(secrets);
			} catch (VerificationException e1) {// can not happen
				e1.printStackTrace();
			}
			//notify who have won
			//if you are the winner ... otherwies ...
			//TODO: ask for private key and address (will not be saved) and fee
			ECKey sk = null;
			Address address = null;
			BigInteger fee = new BigInteger("0");
			ClaimTx claimMoneyTx = new ClaimTx(computeTx, address, fee);
			try {
				claimMoneyTx.addSecrets(secrets);
				claimMoneyTx.addSignature(winner, sk);
			} catch (VerificationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				memoryStorage.saveTransaction(parameters, session, claimMoneyTx);
			} catch (IOException e) {
				// TODO
				e.printStackTrace();
			}
			//print ClaimMoney and summary
		}
		else {
			//TODO: notify about bad secrets
			//ask again?
		}
	}

	protected void lottery() {
		// TODO Auto-generated method stub
		
	}

	protected void generateKeys() {
		Parameters parameters = parametersUpdater.getParameters();
		ECKey key = new KeyGenerator().generate();
		try {
			memoryStorage.saveKey(parameters, session, key);
			notifier.showKey(parameters, session, key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void showHelp() {
		notifier.showHelp();
	}

	protected void showVersion() {
		notifier.showVersion();
	}

	
}
