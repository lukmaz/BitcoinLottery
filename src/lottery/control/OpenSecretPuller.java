package lottery.control;

import java.io.File;
import java.io.IOException;

import lottery.parameters.MemoryStorage;
import lottery.parameters.Parameters;
import lottery.parameters.IOHandler;
import lottery.transaction.OpenTx;

public class OpenSecretPuller {
	protected IOHandler ioHandler;
	protected MemoryStorage memoryStorage;
	protected Parameters parameters;
	
	
	public OpenSecretPuller(IOHandler iOHandler, Parameters parameters, 
			MemoryStorage memoryStorage) {
		super();
		this.ioHandler = iOHandler;
		this.memoryStorage = memoryStorage;
		this.parameters = parameters;
	}
	
	public void open() throws IOException {
		OpenTx openTx = ioHandler.askOpen(new InputVerifiers.OpenTxVerifier(parameters.isTestnet()));
		memoryStorage.saveTransaction(parameters, openTx);
		byte[] secret = openTx.getSecret();
		File secretFile = memoryStorage.saveSecret(parameters, secret);
		ioHandler.showSecret(secret, secretFile.getAbsolutePath());
	}
}
