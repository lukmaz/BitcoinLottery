package lottery.control;

import java.io.IOException;
import java.util.Date;

import lottery.parameters.MemoryStorage;
import lottery.parameters.ParametersUpdater;

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
		try {
			switch (parametersUpdater.getParameters().getCommand()) {
				case VERSION:
					showVersion();
					break;
				case HELP:
					showHelp();
					break;
				case GENERATE_KEYS:
					new KeyGenerator(parametersUpdater, session, memoryStorage, notifier).generateKeys();
					break;
				case CLAIM_MONEY:
					new MoneyClaimer(parametersUpdater, session, memoryStorage, notifier).claimMoney();
					break;
				case OPEN:
					new OpenSecretPuller(parametersUpdater, session, memoryStorage, notifier).open();
					break;
				case LOTTERY:
					new Lottery(parametersUpdater, session, memoryStorage, notifier).lottery();
					break;
				default:
					throw new RuntimeException("BitcoinLottery: Illegal command.");
			}
		} catch (IOException e) {
			//TODO
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
