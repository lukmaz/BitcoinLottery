package lottery.control;

import java.io.IOException;
import java.util.Date;

import lottery.parameters.MemoryStorage;
import lottery.parameters.IOHandler;

public class Controller {

	protected IOHandler parametersUpdater;
	protected MemoryStorage memoryStorage;
	protected String session;
		
	public Controller(IOHandler parametersUpdater,
			MemoryStorage memoryStorage) {
		Long lDateTime = new Date().getTime();
		this.parametersUpdater = parametersUpdater;
		this.memoryStorage = memoryStorage;
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
					new KeyGenerator(parametersUpdater, session, memoryStorage).generateKeys();
					break;
				case CLAIM_MONEY:
					new MoneyClaimer(parametersUpdater, session, memoryStorage).claimMoney();
					break;
				case OPEN:
					new OpenSecretPuller(parametersUpdater, session, memoryStorage).open();
					break;
				case LOTTERY:
					new Lottery(parametersUpdater, session, memoryStorage).lottery();
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
		parametersUpdater.showHelp();
	}

	protected void showVersion() {
		parametersUpdater.showVersion();
	}
}
