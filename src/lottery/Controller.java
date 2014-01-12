package lottery;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import logic.ClaimMoneyCreator;
import logic.KeyGenerator;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.ScriptException;

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
		String txString = null;
		try {
			txString = parametersUpdater.askCompute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ClaimMoneyCreator claimMoneyTxCreator = null;
		try {
			claimMoneyTxCreator = new ClaimMoneyCreator(txString, parametersUpdater.getParameters().isTestnet());
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: save tx
		List<String> secrets = null;
		try {
			secrets = parametersUpdater.askSecrets(claimMoneyTxCreator.getSecretsHashes());
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: save secrets
		boolean secretsCorrect = false;
		try {
			secretsCorrect = claimMoneyTxCreator.checkSecrets(secrets);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (secretsCorrect) {
			//notify who have won
			//TODO: ask for private key and address (will not be saved)
			//fee?
			//compute ClaimMoney
			//save ClaimMoney
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
