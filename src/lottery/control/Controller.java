package lottery.control;

import java.io.IOException;

import lottery.parameters.MemoryStorage;
import lottery.parameters.IOHandler;
import lottery.parameters.Parameters;

public class Controller {
	protected IOHandler ioHandler;
	protected Parameters parameters;
	protected MemoryStorage memoryStorage;
		
	public Controller(IOHandler ioHandler, Parameters parameters, MemoryStorage memoryStorage) {
		this.ioHandler = ioHandler;
		this.parameters = parameters;
		this.memoryStorage = memoryStorage;
	}

	public void run() {
		try {
			switch (parameters.getCommand()) {
				case VERSION:
					showVersion();
					break;
				case HELP:
					showHelp();
					break;
				case GENERATE_KEYS:
					new KeyGenerator(ioHandler, parameters, memoryStorage).generateKeys();
					break;
				case CLAIM_MONEY:
					new MoneyClaimer(ioHandler, parameters, memoryStorage).claimMoney();
					break;
				case OPEN:
					new OpenSecretPuller(ioHandler, parameters, memoryStorage).open();
					break;
				case LOTTERY:
					new Lottery(ioHandler, parameters, memoryStorage).lottery();
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
		ioHandler.showHelp();
	}

	protected void showVersion() {
		ioHandler.showVersion();
	}
}
