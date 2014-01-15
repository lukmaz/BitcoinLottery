package lottery.control;

import java.io.IOException;

import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.VerificationException;

import lottery.parameters.MemoryStorage;
import lottery.parameters.Parameters;
import lottery.parameters.IOHandler;
import lottery.transaction.OpenTx;

public class OpenSecretPuller {
	protected IOHandler parametersUpdater;
	protected MemoryStorage memoryStorage;
	protected String session;
	
	
	public OpenSecretPuller(IOHandler parametersUpdater, String session, 
			MemoryStorage memoryStorage) {
		super();
		this.parametersUpdater = parametersUpdater;
		this.memoryStorage = memoryStorage;
		this.session = session;
	}
	
	public void open() throws IOException {
		Parameters parameters = parametersUpdater.getParameters();
		String txString = null;
		txString = parametersUpdater.askOpen();
		OpenTx openTx = null;
		try {
			openTx = new OpenTx(Utils.parseAsHexOrBase58(txString), parameters.isTestnet());
		} catch (ProtocolException e) {
			// TODO
			e.printStackTrace();
		} catch (VerificationException e) {
			// TODO
			e.printStackTrace();
		}
		
		memoryStorage.saveTransaction(parameters, session, openTx);
		byte[] secret = openTx.getSecret();
		memoryStorage.saveSecrets(parameters, session, secret);
		parametersUpdater.showSecret(parameters, session, secret);
	}
}
