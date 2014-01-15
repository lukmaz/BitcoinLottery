package lottery.control;

import java.io.IOException;

import lottery.parameters.MemoryStorage;
import lottery.parameters.Parameters;
import lottery.parameters.IOHandler;
import lottery.transaction.OpenTx;

public class OpenSecretPuller {
	protected IOHandler ioHandler;
	protected MemoryStorage memoryStorage;
	protected String session;
	
	
	public OpenSecretPuller(IOHandler parametersUpdater, String session, 
			MemoryStorage memoryStorage) {
		super();
		this.ioHandler = parametersUpdater;
		this.memoryStorage = memoryStorage;
		this.session = session;
	}
	
	public void open() throws IOException {
		Parameters parameters = ioHandler.getParameters();
		OpenTx openTx = ioHandler.askOpen(new InputVerifiers.OpenTxVerifier());
		memoryStorage.saveTransaction(parameters, session, openTx);
		byte[] secret = openTx.getSecret();
		memoryStorage.saveSecrets(parameters, session, secret);
		ioHandler.showSecret(parameters, session, secret);
	}
}
