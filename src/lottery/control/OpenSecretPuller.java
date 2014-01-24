package lottery.control;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
		OpenTx openTx = ioHandler.askOpen(new InputVerifiers.OpenTxVerifier(null, parameters.isTestnet()));
		memoryStorage.saveTransaction(parameters, openTx);
		List<byte[]> secrets = openTx.getPossibleSecrets();
		File secretFile = memoryStorage.saveSecrets(parameters, secrets);
		ioHandler.showSecrets(secrets, secretFile.getAbsolutePath());
		//TODO: ask for hash if size() > 1
	}
}
