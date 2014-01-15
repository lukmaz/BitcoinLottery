package lottery.control;

import java.io.IOException;

import lottery.parameters.MemoryStorage;
import lottery.parameters.Parameters;
import lottery.parameters.IOHandler;

import com.google.bitcoin.core.ECKey;

public class KeyGenerator {
	protected IOHandler parametersUpdater;
	protected MemoryStorage memoryStorage;
	protected String session;
	
	
	public KeyGenerator(IOHandler parametersUpdater, String session, 
			MemoryStorage memoryStorage) {
		super();
		this.parametersUpdater = parametersUpdater;
		this.memoryStorage = memoryStorage;
		this.session = session;
	}

	public void generateKeys() throws IOException {
		Parameters parameters = parametersUpdater.getParameters();
		ECKey key = new ECKey();
		memoryStorage.saveKey(parameters, session, key);
		parametersUpdater.showKey(parameters, session, key);
	}
}
