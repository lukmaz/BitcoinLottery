package lottery.control;

import java.io.IOException;

import lottery.parameters.MemoryStorage;
import lottery.parameters.Parameters;
import lottery.parameters.ParametersUpdater;

import com.google.bitcoin.core.ECKey;

public class KeyGenerator {
	protected ParametersUpdater parametersUpdater;
	protected MemoryStorage memoryStorage;
	protected String session;
	protected Notifier notifier;
	
	
	public KeyGenerator(ParametersUpdater parametersUpdater, String session, 
			MemoryStorage memoryStorage, Notifier notifier) {
		super();
		this.parametersUpdater = parametersUpdater;
		this.memoryStorage = memoryStorage;
		this.session = session;
		this.notifier = notifier;
	}

	public void generateKeys() throws IOException {
		Parameters parameters = parametersUpdater.getParameters();
		ECKey key = new ECKey();
		memoryStorage.saveKey(parameters, session, key);
		notifier.showKey(parameters, session, key);
	}
}
