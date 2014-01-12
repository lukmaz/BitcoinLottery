package lottery;

import java.util.Date;

import logic.KeyGenerator;

import com.google.bitcoin.core.ECKey;

import parameters.MemoryStorage;
import parameters.ParametersUpdater;
import settings.BitcoinLotterySettings;

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
		// TODO Auto-generated method stub
		
	}

	protected void lottery() {
		// TODO Auto-generated method stub
		
	}

	protected void generateKeys() {
		String root = parametersUpdater.getParameters().getDir();
		String subdir = BitcoinLotterySettings.keySubdirectory;
		ECKey key = new KeyGenerator().generate();
		boolean testnet = parametersUpdater.getParameters().isTestnet();
		memoryStorage.saveKey(root, subdir, session, key, testnet);
		notifier.showKey(root, subdir, session, key, testnet);
	}

	protected void showHelp() {
		notifier.showHelp();
	}

	protected void showVersion() {
		notifier.showVersion();
	}

	
}
